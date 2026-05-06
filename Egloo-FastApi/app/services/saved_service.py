from uuid import UUID
from typing import Optional, List
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, desc
from app.models.saved_item import SavedItem


async def save_item(
    db: AsyncSession,
    user_id: UUID,
    item_type: str,
    title: str,
    content: Optional[str],
    item_metadata: Optional[dict],
) -> SavedItem:
    """
    Save a new bookmarked item for the user.
    No deduplication — user can save the same thing multiple times.
    """
    item = SavedItem(
        user_id=user_id,
        item_type=item_type,
        title=title,
        content=content,
        item_metadata=item_metadata,
    )
    db.add(item)
    await db.commit()
    await db.refresh(item)
    return item


async def get_saved_items(
    db: AsyncSession,
    user_id: UUID,
    item_type: Optional[str] = None,
    limit: int = 50,
    offset: int = 0,
) -> List[SavedItem]:
    """
    Get all saved items for a user.
    Optionally filter by item_type.
    Ordered by newest first.
    """
    query = (
        select(SavedItem)
        .where(SavedItem.user_id == user_id)
        .order_by(desc(SavedItem.created_at))
        .limit(limit)
        .offset(offset)
    )

    if item_type:
        query = query.where(SavedItem.item_type == item_type)

    result = await db.execute(query)
    return result.scalars().all()


async def get_saved_item_by_id(
    db: AsyncSession,
    item_id: UUID,
    user_id: UUID,
) -> Optional[SavedItem]:
    result = await db.execute(
        select(SavedItem).where(
            SavedItem.id == item_id,
            SavedItem.user_id == user_id,
        )
    )
    return result.scalar_one_or_none()


async def delete_saved_item(
    db: AsyncSession,
    item_id: UUID,
    user_id: UUID,
) -> bool:
    """
    Delete a saved item.
    Returns True if deleted, False if not found.
    """
    item = await get_saved_item_by_id(db, item_id, user_id)
    if not item:
        return False
    await db.delete(item)
    await db.commit()
    return True


async def delete_all_saved_items(
    db: AsyncSession,
    user_id: UUID,
) -> int:
    """
    Delete all saved items for a user.
    Returns count of deleted items.
    """
    from sqlalchemy import delete, func, select as sel

    count_result = await db.execute(
        sel(func.count())
        .select_from(SavedItem)
        .where(SavedItem.user_id == user_id)
    )
    count = count_result.scalar()

    await db.execute(
        delete(SavedItem).where(SavedItem.user_id == user_id)
    )
    await db.commit()
    return count


async def count_saved_items(
    db: AsyncSession,
    user_id: UUID,
) -> dict:
    """
    Count saved items per type.
    Returns a breakdown for the profile/stats screen.
    """
    from sqlalchemy import func

    result = await db.execute(
        select(
            SavedItem.item_type,
            func.count(SavedItem.id).label("count"),
        )
        .where(SavedItem.user_id == user_id)
        .group_by(SavedItem.item_type)
    )
    rows = result.all()

    counts = {
        "total": 0,
        "digest": 0,
        "query_result": 0,
        "summary": 0,
        "topic": 0,
    }
    for row in rows:
        counts[row.item_type] = row.count
        counts["total"] += row.count

    return counts
