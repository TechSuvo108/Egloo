from typing import Optional
from uuid import UUID

from pydantic import BaseModel


class IngestResponse(BaseModel):
    job_id: str
    source_id: UUID
    source_type: str
    message: str


class IngestResultResponse(BaseModel):
    source_type: str
    documents_fetched: int
    chunks_created: int
    vectors_stored: Optional[int] = 0
    message: str
