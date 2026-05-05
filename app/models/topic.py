from sqlalchemy import Column, String, DateTime, ForeignKey, Integer, Text
from sqlalchemy.dialects.postgresql import UUID, JSONB
from sqlalchemy.sql import func
import uuid
from app.database import Base

class Topic(Base):
    __tablename__ = "topics"

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(
        UUID(as_uuid=True),
        ForeignKey("users.id", ondelete="CASCADE"),
        nullable=False,
        index=True,
    )

    name = Column(String, nullable=False)
    # LLM-generated name: "Project Alpha", "Q2 Budget", etc.

    summary = Column(Text, nullable=True)
    # 1-2 sentence summary of this topic

    chunk_ids = Column(JSONB, nullable=True)
    # List of DocumentChunk UUIDs belonging to this topic
    # ["uuid1", "uuid2", ...]

    source_types = Column(JSONB, nullable=True)
    # List of source types: ["gmail", "slack"]

    item_count = Column(Integer, default=0)
    # How many chunks belong to this topic

    last_refreshed_at = Column(
        DateTime(timezone=True),
        server_default=func.now(),
    )
    created_at = Column(
        DateTime(timezone=True),
        server_default=func.now(),
    )
