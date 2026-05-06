import asyncio
from typing import AsyncGenerator
from app.config import settings


class GroqError(Exception):
    """Raised when Groq API fails."""
    pass


class GroqRateLimitError(GroqError):
    """Raised when Groq hits rate limit."""
    pass


async def call_groq(
    api_key: str,
    prompt: str,
    system: str,
    stream: bool = False,
    timeout: int = None,
) -> AsyncGenerator[str, None]:
    """
    Call Groq API with timeout.
    Groq is the fastest provider — usually responds in < 1s.
    Raises GroqRateLimitError on 429.
    Raises GroqError on any other failure.
    """
    try:
        from groq import AsyncGroq, RateLimitError
    except ImportError:
        raise GroqError("groq not installed")

    if timeout is None:
        timeout = settings.GROQ_TIMEOUT

    messages = [
        {"role": "system", "content": system},
        {"role": "user",   "content": prompt},
    ]

    try:
        client = AsyncGroq(api_key=api_key)

        if stream:
            async def _stream_gen():
                try:
                    response = await asyncio.wait_for(
                        client.chat.completions.create(
                            model=settings.GROQ_MODEL,
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
                    raise GroqError(f"Groq timed out after {timeout}s")
            async for token in _stream_gen():
                yield token
        else:
            response = await asyncio.wait_for(
                client.chat.completions.create(
                    model=settings.GROQ_MODEL,
                    messages=messages,
                    stream=False,
                ),
                timeout=timeout,
            )
            yield response.choices[0].message.content

    except Exception as e:
        error_str = str(e).lower()
        if "429" in error_str or "rate" in error_str or "limit" in error_str:
            raise GroqRateLimitError(f"Groq rate limit: {e}")
        if "timeout" in error_str:
            raise GroqError(f"Groq timeout: {e}")
        raise GroqError(f"Groq error: {e}")
