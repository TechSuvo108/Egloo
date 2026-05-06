from fastapi import APIRouter, Depends
from app.dependencies import get_current_user
from app.models.user import User
from app.ai.provider_health import get_all_health, get_usage_stats
from app.ai.llm_router import call_llm_simple
from app.config import settings

router = APIRouter(prefix="/llm", tags=["LLM"])


@router.get("/health")
async def llm_health(
    current_user: User = Depends(get_current_user),
):
    """
    Returns health status of all 3 LLM providers.
    Shows which are healthy, which are rate-limited or erroring.
    """
    health = await get_all_health()

    configured = {
        "gemini": bool(settings.GEMINI_API_KEYS),
        "groq": bool(settings.GROQ_API_KEYS),
        "openrouter": bool(settings.OPENROUTER_API_KEYS),
    }

    return {
        "providers": {
            name: {
                **status,
                "configured": configured.get(name, False),
            }
            for name, status in health.items()
        },
        "ready": any(configured.values()),
        "message": (
            "Pingo is ready to answer questions!"
            if any(configured.values())
            else "No LLM providers configured. Add an API key to .env"
        ),
    }


@router.get("/usage")
async def llm_usage(
    current_user: User = Depends(get_current_user),
):
    """
    Returns usage statistics for all providers.
    Shows success/failure counts and estimated tokens used.
    """
    stats = await get_usage_stats()
    return {
        "usage": stats,
        "note": "Token counts are estimates based on prompt length / 4",
    }


@router.post("/test")
async def test_llm(
    current_user: User = Depends(get_current_user),
):
    """
    Send a test prompt through the full LLM router.
    Confirms which provider responds and that fallback works.
    Good for verifying API keys are working.
    """
    try:
        answer, model = await call_llm_simple(
            prompt=(
                "Say exactly this and nothing else: "
                "'Pingo is online and ready to help!'"
            ),
            system="You are Pingo. Follow instructions exactly.",
        )
        return {
            "status": "success",
            "model_used": model,
            "response": answer,
            "message": "LLM router is working correctly",
        }
    except RuntimeError as e:
        return {
            "status": "failed",
            "model_used": None,
            "response": None,
            "message": str(e),
        }
