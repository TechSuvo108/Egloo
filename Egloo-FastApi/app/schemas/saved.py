from pydantic import BaseModel
from uuid import UUID
from datetime import datetime
from typing import Optional, Any, Dict

class SaveItemRequest(BaseModel):
    item_type: str
    # "digest" | "query_result" | "summary" | "topic"

    title: str
    # Short display title shown in saved list

    content: Optional[str] = None
    # Full text content of what is saved

    item_metadata: Optional[Dict[str, Any]] = None
    # Any extra data:
    # For digest:       {"digest_id": "uuid", "date": "2024-04-29"}
    # For query_result: {"question": "...", "model_used": "gemini"}
    # For topic:        {"topic_id": "uuid", "item_count": 5}

class SavedItemResponse(BaseModel):
    id: UUID
    item_type: str
    title: str
    content: Optional[str] = None
    item_metadata: Optional[Dict[str, Any]] = None
    created_at: datetime

    model_config = {"from_attributes": True}

class SavedListResponse(BaseModel):
    items: list[SavedItemResponse]
    total: int

class MessageResponse(BaseModel):
    message: str
