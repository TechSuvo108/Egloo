from uuid import UUID
from fastapi import APIRouter, Depends, HTTPException, Query as QueryParam
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.dependencies import get_current_user
from app.models.user import User
from app.schemas.saved import (
    SaveItemRequest,
    SavedItemResponse,
    SavedListResponse,
    MessageResponse,
)
from app.services.saved_service import (
    save_item,
    get_saved_items,
    get_saved_item_by_id,
    delete_saved_item,
    delete_all_saved_items,
    count_saved_items,
)

router = APIRouter(prefix="/saved", tags=["Saved"])

VALID_ITEM_TYPES = {"digest", "query_result", "summary", "topic"}


@router.post("", response_model=SavedItemResponse, status_code=201)
async def create_saved_item(
    body: SaveItemRequest,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """
    Bookmark an item.
    item_type must be one of:
      digest | query_result | summary | topic

    Examples:

    Save a digest:
    {
      "item_type": "digest",
      "title": "Q1 Retrospective — April 29",
      "content": "Summary text here...",
      "item_metadata": {"digest_id": "uuid", "date": "2024-04-29"}
    }

    Save a query result:
    {
      "item_type": "query_result",
      "title": "What is the status of Project Alpha?",
      "content": "Pingo's answer here...",
      "item_metadata": {
        "question": "What is the status of Project Alpha?",
        "model_used": "gemini",
        "sources_count": 4
      }
    }

    Save a topic:
    {
      "item_type": "topic",
      "title": "Project Alpha",
      "content": "Topic summary here...",
      "item_metadata": {"topic_id": "uuid", "item_count": 12}
    }
    """
    if body.item_type not in VALID_ITEM_TYPES:
        raise HTTPException(
            status_code=400,
            detail=(
                f"Invalid item_type '{body.item_type}'. "
                f"Must be one of: {', '.join(sorted(VALID_ITEM_TYPES))}"
            ),
        )

    if not body.title.strip():
        raise HTTPException(
            status_code=400,
            detail="title cannot be empty",
        )

    if len(body.title) > 300:
        raise HTTPException(
            status_code=400,
            detail="title too long — max 300 characters",
        )

    item = await save_item(
        db=db,
        user_id=current_user.id,
        item_type=body.item_type,
        title=body.title.strip(),
        content=body.content,
        item_metadata=body.item_metadata,
    )

    return item


@router.get("", response_model=SavedListResponse)
async def list_saved_items(
    item_type: str = QueryParam(
        default=None,
        description="Filter by type: digest | query_result | summary | topic",
    ),
    limit: int = QueryParam(default=50, ge=1, le=100),
    offset: int = QueryParam(default=0, ge=0),
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """
    List all bookmarked items for the current user.
    Optionally filter by item_type.
    Ordered by newest first.
    """
    if item_type and item_type not in VALID_ITEM_TYPES:
        raise HTTPException(
            status_code=400,
            detail=(
                f"Invalid item_type filter '{item_type}'. "
                f"Must be one of: {', '.join(sorted(VALID_ITEM_TYPES))}"
            ),
        )

    items = await get_saved_items(
        db=db,
        user_id=current_user.id,
        item_type=item_type,
        limit=limit,
        offset=offset,
    )

    return SavedListResponse(
        items=[SavedItemResponse.model_validate(i) for i in items],
        total=len(items),
    )


@router.get("/counts", response_model=dict)
async def get_saved_counts(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """
    Get count of saved items broken down by type.
    Used by the profile/stats screen in the frontend.

    Returns:
    {
      "total": 15,
      "digest": 5,
      "query_result": 7,
      "summary": 2,
      "topic": 1
    }
    """
    counts = await count_saved_items(db, current_user.id)
    return counts


@router.get("/{item_id}", response_model=SavedItemResponse)
async def get_saved_item(
    item_id: UUID,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """Get a specific saved item by ID."""
    item = await get_saved_item_by_id(db, item_id, current_user.id)
    if not item:
        raise HTTPException(status_code=404, detail="Saved item not found")
    return item


@router.delete("/all", response_model=MessageResponse)
async def clear_all_saved(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """
    Delete ALL saved items for the current user.
    This is a danger zone action — irreversible.
    """
    count = await delete_all_saved_items(db, current_user.id)
    return MessageResponse(
        message=(
            f"Deleted {count} saved items. "
            f"Pingo cleared your bookmarks."
        )
    )


@router.delete("/{item_id}", response_model=MessageResponse)
async def delete_saved_item_endpoint(
    item_id: UUID,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """Delete a specific saved item by ID."""
    deleted = await delete_saved_item(db, item_id, current_user.id)
    if not deleted:
        raise HTTPException(status_code=404, detail="Saved item not found")
    return MessageResponse(
        message="Bookmark removed."
    )
