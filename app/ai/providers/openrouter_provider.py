import asyncio
from typing import AsyncGenerator
from app.config import settings


class OpenRouterError(Exception):
    """Raised when OpenRouter API fails."""
    pass


class OpenRouterRateLimitError(OpenRouterError):
    """Raised when OpenRouter hits rate limit."""
    pass


async def call_openrouter(
    api_key: str,
    prompt: str,
    system: str,
    stream: bool = False,
    timeout: int = None,
) -> AsyncGenerator[str, None]:
    """
    Call OpenRouter API — aggregates many models.
    Uses OpenAI-compatible format.
    Final fallback — most reliable.
    """
    try:
        from openai import AsyncOpenAI
    except ImportError:
        raise OpenRouterError("openai package not installed")

    if timeout is None:
        timeout = settings.OPENROUTER_TIMEOUT

    messages = [
        {"role": "system", "content": system},
        {"role": "user",   "content": prompt},
    ]

    try:
        client = AsyncOpenAI(
            api_key=api_key,
            base_url=settings.OPENROUTER_BASE_URL,
        )

        if stream:
            async def _stream_gen():
                try:
                    response = await asyncio.wait_for(
                        client.chat.completions.create(
                            model=settings.OPENROUTER_MODEL,
                            messages=messages,
                            stream=True,
                        ),
                        timeout=timeout,
                    )
                    async for chunk in response:
                        delta = chunk.choices[0].delta.content
                        if delta:
                            yield delta
                except asyncio.TimeoutError:
                    raise OpenRouterError(
                        f"OpenRouter timed out after {timeout}s"
                    )
            async for token in _stream_gen():
                yield token
        else:
            response = await asyncio.wait_for(
                client.chat.completions.create(
                    model=settings.OPENROUTER_MODEL,
                    messages=messages,
                    stream=False,
                ),
                timeout=timeout,
            )
            yield response.choices[0].message.content

    except Exception as e:
        error_str = str(e).lower()
        if "429" in error_str or "rate" in error_str:
            raise OpenRouterRateLimitError(f"OpenRouter rate limit: {e}")
        if "timeout" in error_str:
            raise OpenRouterError(f"OpenRouter timeout: {e}")
        raise OpenRouterError(f"OpenRouter error: {e}")
