from sqlalchemy import Column, String, DateTime, Date, Text, ForeignKey
from sqlalchemy.dialects.postgresql import UUID, JSONB
from sqlalchemy.sql import func
import uuid
from app.database import Base


class Digest(Base):
    __tablename__ = "digests"

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(
        UUID(as_uuid=True),
        ForeignKey("users.id", ondelete="CASCADE"),
        nullable=False,
        index=True,
    )

    date = Column(Date, nullable=False)
    # the calendar date this digest covers

    summary_text = Column(Text, nullable=True)
    # the full LLM-generated summary paragraph

    action_items = Column(JSONB, nullable=True)
    # list of strings: ["Reply to Rohit about deadline", "Review Figma mockup"]

    topics = Column(JSONB, nullable=True)
    # list of topic objects: [{"name": "Project Alpha", "summary": "...", "item_count": 4}]

    created_at = Column(DateTime(timezone=True), server_default=func.now())
