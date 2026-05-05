import json
from typing import List, Dict, Any, Tuple
from app.ai.llm_router import call_llm_simple

TOPIC_SYSTEM_PROMPT = """You are PenGo, an intelligent assistant that
helps knowledge workers organize their information.
Your job is to identify meaningful topics from a person's
emails, messages, and documents.
Always respond with valid JSON only. No markdown. No code fences."""


# ─── Strategy 1: LLM-based clustering ────────────────────────────────────────

async def cluster_with_llm(
    chunks: List[Dict[str, Any]],
    max_topics: int = 10,
) -> List[Dict[str, Any]]:
    """
    Send chunk summaries to LLM and ask it to group them into topics.
    Best quality. Use for up to ~150 chunks.

    Returns list of topic dicts:
    [
        {
            "name": "Project Alpha",
            "summary": "Discussions about the product launch...",
            "chunk_indices": [0, 3, 7, 12],
            "source_types": ["gmail", "slack"],
        },
        ...
    ]
    """
    if not chunks:
        return []

    # Build compact chunk summaries for the prompt
    lines = []
    for i, chunk in enumerate(chunks[:150]):
        meta = chunk.get("chunk_metadata", {}) or {}
        source = meta.get("source_type", "unknown").upper()
        subject = meta.get("subject", "")
        sender = meta.get("sender", "")
        preview = chunk["content"][:120].replace("\n", " ").strip()

        line = f"[{i}] {source}"
        if sender:
            line += f" | {sender}"
        if subject:
            line += f" | {subject}"
        line += f" — {preview}"
        lines.append(line)

    chunks_text = "\n".join(lines)

    prompt = f"""Here are {len(lines)} items from a person's emails,
Slack messages, and documents.

{chunks_text}

Group these into {min(max_topics, 10)} meaningful topics.
Each topic should represent a distinct theme, project, or concern.

Respond with ONLY valid JSON in exactly this format:
{{
  "topics": [
    {{
      "name": "Clear topic name (3-5 words max)",
      "summary": "One sentence describing what this topic is about.",
      "chunk_indices": [0, 3, 7],
      "source_types": ["gmail", "slack"]
    }}
  ]
}}"""

    try:
        response, model = await call_llm_simple(
            prompt=prompt,
            system=TOPIC_SYSTEM_PROMPT,
        )

        cleaned = response.strip()
        if cleaned.startswith("```"):
            cleaned = "\n".join(
                l for l in cleaned.split("\n")
                if not l.startswith("```")
            )

        data = json.loads(cleaned)
        topics = data.get("topics", [])
        print(f"[INFO] LLM clustered {len(chunks)} chunks into {len(topics)} topics using {model}")
        return topics

    except Exception as e:
        print(f"[WARNING] LLM clustering failed: {e}")
        return []


# ─── Strategy 2: KMeans + LLM naming ─────────────────────────────────────────

async def cluster_with_kmeans(
    chunks: List[Dict[str, Any]],
    n_clusters: int = 8,
) -> List[Dict[str, Any]]:
    """
    Embed all chunks → run KMeans → name each cluster with LLM.
    Better for large chunk sets (150+ chunks).
    Falls back to LLM clustering if sklearn not available.

    Returns same format as cluster_with_llm.
    """
    if not chunks:
        return []

    if len(chunks) < n_clusters:
        n_clusters = max(2, len(chunks) // 2)

    try:
        from sklearn.cluster import KMeans
        import numpy as np
        from app.utils.embedder import embed_texts
    except ImportError:
        print("[WARNING] scikit-learn not available — falling back to LLM clustering")
        return await cluster_with_llm(chunks, max_topics=n_clusters)

    print(f"[INFO] KMeans: embedding {len(chunks)} chunks...")

    # Embed all chunks in batches
    texts = [c["content"] for c in chunks]
    batch_size = 32
    all_embeddings = []

    for i in range(0, len(texts), batch_size):
        batch = texts[i:i + batch_size]
        embeddings = embed_texts(batch)
        all_embeddings.extend(embeddings)

    X = np.array(all_embeddings)

    # Run KMeans
    print(f"[INFO] KMeans: clustering into {n_clusters} groups...")
    kmeans = KMeans(
        n_clusters=n_clusters,
        random_state=42,
        n_init=10,
        max_iter=300,
    )
    labels = kmeans.fit_predict(X)

    # Group chunk indices by cluster label
    cluster_map: Dict[int, List[int]] = {}
    for idx, label in enumerate(labels):
        cluster_map.setdefault(int(label), []).append(idx)

    # Name each cluster using LLM
    topics = []
    for cluster_id, indices in cluster_map.items():
        if not indices:
            continue

        # Take up to 5 representative chunks per cluster for naming
        sample_indices = indices[:5]
        sample_chunks = [chunks[i] for i in sample_indices]

        sample_lines = []
        for chunk in sample_chunks:
            meta = chunk.get("chunk_metadata", {}) or {}
            preview = chunk["content"][:100].replace("\n", " ")
            source = meta.get("source_type", "unknown")
            sample_lines.append(f"[{source.upper()}] {preview}")

        sample_text = "\n".join(sample_lines)

        # Ask LLM to name this cluster
        name_prompt = f"""These messages and documents belong to the same topic:

{sample_text}

Give this topic a clear, specific name (3-5 words max)
and a one-sentence summary.

Respond with ONLY valid JSON:
{{
  "name": "Topic Name Here",
  "summary": "One sentence about this topic."
}}"""

        try:
            resp, _ = await call_llm_simple(
                prompt=name_prompt,
                system=TOPIC_SYSTEM_PROMPT,
            )
            cleaned = resp.strip()
            if cleaned.startswith("```"):
                cleaned = "\n".join(
                    l for l in cleaned.split("\n")
                    if not l.startswith("```")
                )
            meta_data = json.loads(cleaned)
            topic_name = meta_data.get("name", f"Topic {cluster_id + 1}")
            topic_summary = meta_data.get("summary", "")
        except Exception:
            topic_name = f"Topic {cluster_id + 1}"
            topic_summary = ""

        # Collect source types for this cluster
        source_types = list({
            (chunks[i].get("chunk_metadata", {}) or {}).get("source_type", "unknown")
            for i in indices
        })

        topics.append({
            "name": topic_name,
            "summary": topic_summary,
            "chunk_indices": indices,
            "source_types": source_types,
        })

    print(f"[OK] KMeans: created {len(topics)} named topic clusters")
    return topics


# ─── Auto-select strategy based on chunk count ───────────────────────────────

async def cluster_chunks(
    chunks: List[Dict[str, Any]],
    strategy: str = "auto",
    max_topics: int = 10,
) -> List[Dict[str, Any]]:
    """
    Auto-selects clustering strategy:
    - "auto":   LLM for ≤150 chunks, KMeans for >150
    - "llm":    Always use LLM
    - "kmeans": Always use KMeans

    Returns list of topic dicts with chunk_indices.
    """
    if strategy == "llm" or (strategy == "auto" and len(chunks) <= 150):
        return await cluster_with_llm(chunks, max_topics=max_topics)
    else:
        n = min(max_topics, max(3, len(chunks) // 15))
        return await cluster_with_kmeans(chunks, n_clusters=n)
