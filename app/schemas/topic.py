from pydantic import BaseModel, Field
from uuid import UUID
from datetime import datetime
from typing import Optional, List, Any

class TopicResponse(BaseModel):
    id: UUID
    name: str
    summary: Optional[str] = ""
    source_types: List[str] = Field(default_factory=list)
    item_count: int = 0
    last_refreshed_at: Optional[datetime] = None
    created_at: Optional[datetime] = None

    model_config = {"from_attributes": True}

class TopicListResponse(BaseModel):
    topics: List[TopicResponse]
    total: int

class TopicDetailResponse(BaseModel):
    id: UUID
    name: str
    summary: Optional[str] = ""
    source_types: List[str] = Field(default_factory=list)
    item_count: int = 0
    last_refreshed_at: Optional[datetime] = None
    chunks: List[Any] = Field(default_factory=list)

    model_config = {"from_attributes": True}

class RefreshTopicsRequest(BaseModel):
    strategy: str = "auto"
    # "auto" | "llm" | "kmeans"
    max_topics: int = 10

class RefreshTopicsResponse(BaseModel):
    message: str
    topics_created: int
    chunks_processed: int
    strategy_used: Optional[str] = "auto"

class MessageResponse(BaseModel):
    message: str
