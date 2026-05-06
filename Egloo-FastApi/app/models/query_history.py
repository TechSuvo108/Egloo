from sqlalchemy import Column, String, DateTime, Text, ForeignKey
from sqlalchemy.dialects.postgresql import UUID, JSONB
from sqlalchemy.sql import func
import uuid
from app.database import Base


class QueryHistory(Base):
    __tablename__ = "query_history"

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(
        UUID(as_uuid=True),
        ForeignKey("users.id", ondelete="CASCADE"),
        nullable=False,
        index=True,
    )

    question = Column(Text, nullable=False)
    answer = Column(Text, nullable=True)

    sources_used = Column(JSONB, nullable=True)
    # list of source objects returned alongside the answer

    model_used = Column(String, nullable=True)
    # which LLM answered: "gemini" | "groq" | "openrouter"

    created_at = Column(DateTime(timezone=True), server_default=func.now())
