import uuid

from fastapi import APIRouter, BackgroundTasks, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.dependencies import get_current_user
from app.models.user import User
from app.schemas.ingest import IngestResponse, IngestResultResponse
from app.services.ingestion_service import ingest_source
from app.services.source_service import get_all_sources, get_source_by_id

router = APIRouter(prefix="/ingest", tags=["ingest"])


# ---------------------------------------------------------------------------
# Background task helper
# ---------------------------------------------------------------------------

async def _run_ingestion(
    source_id: str,
    user_id: str,
    source_type: str,
    db: AsyncSession,
):
    """
    Background task that runs the full ingestion pipeline.
    Called by BackgroundTasks — runs after the HTTP response is sent.
    """
    source = await get_source_by_id(db, uuid.UUID(source_id), uuid.UUID(user_id))
    if not source:
        print(f"❌ Source {source_id} not found during background ingestion")
        return

    try:
        result = await ingest_source(db, source, user_id)
        print(f"✅ Background ingestion complete: {result}")
    except Exception as e:
        print(f"❌ Background ingestion error: {e}")


# ---------------------------------------------------------------------------
# Endpoints
# ---------------------------------------------------------------------------

@router.post("/trigger/{source_id}", response_model=IngestResponse)
async def trigger_ingest(
    source_id: uuid.UUID,
    background_tasks: BackgroundTasks,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """
    Trigger ingestion for a specific source.
    Returns immediately with a job_id.
    Ingestion runs in the background.
    """
    source = await get_source_by_id(db, source_id, current_user.id)
    if not source:
        raise HTTPException(status_code=404, detail="Source not found")

    if source.sync_status == "syncing":
        raise HTTPException(status_code=400, detail="Sync already in progress")

    job_id = str(uuid.uuid4())

    background_tasks.add_task(
        _run_ingestion,
        source_id=str(source_id),
        user_id=str(current_user.id),
        source_type=source.source_type,
        db=db,
    )

    return IngestResponse(
        job_id=job_id,
        source_id=source_id,
        source_type=source.source_type,
        message=f"PenGo is fetching your {source.source_type} data in the background! 🐧",
    )


@router.post("/trigger-all", response_model=list[IngestResponse])
async def trigger_all_ingest(
    background_tasks: BackgroundTasks,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """
    Trigger ingestion for ALL connected sources at once.
    Useful for initial setup.
    """
    sources = await get_all_sources(db, current_user.id)
    if not sources:
        raise HTTPException(
            status_code=404,
            detail="No sources connected. Connect Gmail, Slack, or Drive first.",
        )

    responses = []
    for source in sources:
        if source.sync_status == "syncing":
            continue

        job_id = str(uuid.uuid4())
        background_tasks.add_task(
            _run_ingestion,
            source_id=str(source.id),
            user_id=str(current_user.id),
            source_type=source.source_type,
            db=db,
        )

        responses.append(IngestResponse(
            job_id=job_id,
            source_id=source.id,
            source_type=source.source_type,
            message=f"PenGo is fetching your {source.source_type} data! 🐧",
        ))

    return responses


@router.post("/trigger-direct/{source_id}", response_model=IngestResultResponse)
async def trigger_ingest_direct(
    source_id: uuid.UUID,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """
    Trigger ingestion synchronously — waits for result.
    Slower but good for testing.
    Use /trigger/{source_id} for production (background).
    """
    source = await get_source_by_id(db, source_id, current_user.id)
    if not source:
        raise HTTPException(status_code=404, detail="Source not found")

    if source.sync_status == "syncing":
        raise HTTPException(status_code=400, detail="Sync already in progress")

    try:
        result = await ingest_source(db, source, str(current_user.id))
        return IngestResultResponse(**result)
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Ingestion failed: {str(e)}",
        )
