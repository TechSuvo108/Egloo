from sqlalchemy import Column, String, DateTime, ForeignKey, Text
from sqlalchemy.dialects.postgresql import UUID, JSONB
from sqlalchemy.sql import func
import uuid
from app.database import Base


class DocumentChunk(Base):
    __tablename__ = "document_chunks"

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(
        UUID(as_uuid=True),
        ForeignKey("users.id", ondelete="CASCADE"),
        nullable=False,
        index=True,
    )
    source_id = Column(
        UUID(as_uuid=True),
        ForeignKey("data_sources.id", ondelete="CASCADE"),
        nullable=False,
        index=True,
    )

    content = Column(Text, nullable=False)
    # raw text of this chunk

    chunk_metadata = Column(JSONB, nullable=True)
    # stores: {subject, sender, timestamp, url, source_type, document_id}

    chroma_id = Column(String, nullable=True)
    # the ID of this chunk in ChromaDB so we can cross-reference

    created_at = Column(DateTime(timezone=True), server_default=func.now())
