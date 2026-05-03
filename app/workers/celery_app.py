from celery import Celery
from app.config import settings

celery_app = Celery(
    "egloo",
    broker=settings.REDIS_URL,
    backend=settings.REDIS_URL,
)

celery_app.conf.update(
    task_serializer="json",
    result_serializer="json",
    accept_content=["json"],
    timezone="UTC",
    enable_utc=True,
)

# This is a placeholder. Tasks will be added in later steps.
