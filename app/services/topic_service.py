from uuid import UUID
from typing import List, Dict, Any, Optional
from datetime import datetime, timezone

from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, delete, desc

from app.models.topic import Topic
from app.models.document_chunk import DocumentChunk
from app.ai.topic_ai import cluster_chunks
from app.config import settings


async def get_all_topics(
    db: AsyncSession,
    user_id: UUID,
) -> List[Topic]:
    """Get all topics for a user ordered by item count descending."""
    result = await db.execute(
        select(Topic)
        .where(Topic.user_id == user_id)
        .order_by(desc(Topic.item_count))
    )
    return result.scalars().all()


async def get_topic_by_id(
    db: AsyncSession,
    topic_id: UUID,
    user_id: UUID,
) -> Optional[Topic]:
    result = await db.execute(
        select(Topic).where(
            Topic.id == topic_id,
            Topic.user_id == user_id,
        )
    )
    return result.scalar_one_or_none()


async def get_chunks_for_topic(
    db: AsyncSession,
    topic: Topic,
) -> List[Dict[str, Any]]:
    """
    Fetch the actual DocumentChunk records for a topic.
    Uses chunk_ids stored in the Topic row.
    Returns list of plain dicts.
    """
    if not topic.chunk_ids:
        return []

    chunk_uuids = [UUID(cid) for cid in topic.chunk_ids]

    result = await db.execute(
        select(DocumentChunk).where(
            DocumentChunk.id.in_(chunk_uuids)
        )
    )
    chunks = result.scalars().all()

    return [
        {
            "id": str(c.id),
            "content": c.content,
            "chunk_metadata": c.chunk_metadata or {},
            "source_id": str(c.source_id),
            "created_at": c.created_at.isoformat() if c.created_at else "",
        }
        for c in chunks
    ]


async def fetch_all_chunks_for_user(
    db: AsyncSession,
    user_id: UUID,
) -> List[Dict[str, Any]]:
    """
    Fetch ALL chunks for a user (not just last 24h).
    Used for topic clustering across all ingested data.
    """
    result = await db.execute(
        select(DocumentChunk)
        .where(DocumentChunk.user_id == user_id)
        .order_by(desc(DocumentChunk.created_at))
        .limit(500)
        # Cap at 500 — enough for good clustering
        # without blowing the embedding budget
    )
    chunks = result.scalars().all()

    return [
        {
            "id": str(c.id),
            "content": c.content,
            "chunk_metadata": c.chunk_metadata or {},
            "source_id": str(c.source_id),
            "created_at": c.created_at.isoformat() if c.created_at else "",
        }
        for c in chunks
    ]


async def refresh_topics(
    db: AsyncSession,
    user_id: str,
    strategy: str = "auto",
    max_topics: int = 10,
) -> Dict[str, Any]:
    """
    Full topic refresh pipeline:
    1. Fetch all chunks for user
    2. Run clustering (LLM or KMeans)
    3. Delete old topics
    4. Save new topics to PostgreSQL

    Returns summary of what was created.
    """
    user_uuid = UUID(user_id)

    print(f"[INFO] Refreshing topics for user {user_id} using strategy={strategy}")

    # ── Step 1: Fetch all chunks ──────────────────────────────────────────────
    chunks = await fetch_all_chunks_for_user(db, user_uuid)

    if len(chunks) < 3:
        return {
            "message": (
                "Not enough data to cluster topics. "
                "Ingest more sources first."
            ),
            "topics_created": 0,
            "chunks_processed": len(chunks),
        }

    # ── Step 2: Cluster ───────────────────────────────────────────────────────
    topic_clusters = await cluster_chunks(
        chunks=chunks,
        strategy=strategy,
        max_topics=max_topics,
    )

    if not topic_clusters:
        return {
            "message": "Clustering returned no topics.",
            "topics_created": 0,
            "chunks_processed": len(chunks),
        }

    # ── Step 3: Delete old topics for this user ───────────────────────────────
    await db.execute(
        delete(Topic).where(Topic.user_id == user_uuid)
    )
    await db.flush()

    # ── Step 4: Save new topics ───────────────────────────────────────────────
    now = datetime.now(timezone.utc)
    new_topics = []

    for cluster in topic_clusters:
        indices = cluster.get("chunk_indices", [])
        if not indices:
            continue

        # Map indices to actual chunk IDs
        chunk_id_list = [
            chunks[i]["id"]
            for i in indices
            if i < len(chunks)
        ]

        source_types = cluster.get("source_types", [])
        if not source_types:
            source_types = list({
                (chunks[i].get("chunk_metadata", {}) or {})
                .get("source_type", "unknown")
                for i in indices
                if i < len(chunks)
            })

        topic = Topic(
            user_id=user_uuid,
            name=cluster.get("name", "Unnamed Topic"),
            summary=cluster.get("summary", ""),
            chunk_ids=chunk_id_list,
            source_types=list(source_types),
            item_count=len(chunk_id_list),
            last_refreshed_at=now,
        )
        new_topics.append(topic)

    db.add_all(new_topics)
    await db.commit()

    print(f"[OK] Topics refreshed: {len(new_topics)} topics saved")

    return {
        "message": (
            f"PenGo clustered your data into "
            f"{len(new_topics)} topics!"
        ),
        "topics_created": len(new_topics),
        "chunks_processed": len(chunks),
        "strategy_used": strategy,
    }
