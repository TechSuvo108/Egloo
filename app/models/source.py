from sqlalchemy import Column, String, DateTime, ForeignKey
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.sql import func
import uuid
from app.database import Base


class DataSource(Base):
    __tablename__ = "data_sources"

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(
        UUID(as_uuid=True),
        ForeignKey("users.id", ondelete="CASCADE"),
        nullable=False,
        index=True,
    )
    source_type = Column(String, nullable=False)
    # source_type values: "gmail" | "slack" | "google_drive"

    access_token = Column(String, nullable=True)
    # stored encrypted using Fernet — we encrypt before saving, decrypt before using

    refresh_token = Column(String, nullable=True)
    # stored encrypted using Fernet

    token_expiry = Column(DateTime(timezone=True), nullable=True)
    last_synced_at = Column(DateTime(timezone=True), nullable=True)
    sync_status = Column(String, default="idle")
    # sync_status values: "idle" | "syncing" | "error" | "success"

    created_at = Column(DateTime(timezone=True), server_default=func.now())
