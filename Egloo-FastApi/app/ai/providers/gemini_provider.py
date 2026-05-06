import asyncio
from typing import AsyncGenerator
from app.config import settings


class GeminiError(Exception):
    """Raised when Gemini API fails."""
    pass


class GeminiRateLimitError(GeminiError):
    """Raised when Gemini hits rate limit (429)."""
    pass


async def call_gemini(
    api_key: str,
    prompt: str,
    system: str,
    stream: bool = False,
    timeout: int = None,
) -> AsyncGenerator[str, None]:
    """
    Call Gemini API with timeout.
    Raises GeminiRateLimitError on 429.
    Raises GeminiError on any other failure.
    """
    try:
        import google.generativeai as genai
    except ImportError:
        raise GeminiError("google-generativeai not installed")

    if timeout is None:
        timeout = settings.GEMINI_TIMEOUT

    try:
        genai.configure(api_key=api_key)
        model = genai.GenerativeModel(
            model_name=settings.GEMINI_MODEL,
            system_instruction=system,
        )

        if stream:
            async def _stream_gen():
                try:
                    response = await asyncio.wait_for(
                        model.generate_content_async(prompt, stream=True),
                        timeout=timeout,
                    )
                    async for chunk in response:
                        if chunk.text:
                            yield chunk.text
                except asyncio.TimeoutError:
                    raise GeminiError(
                        f"Gemini timed out after {timeout}s"
                    )
            async for token in _stream_gen():
                yield token
        else:
            response = await asyncio.wait_for(
                model.generate_content_async(prompt),
                timeout=timeout,
            )
            yield response.text

    except Exception as e:
        error_str = str(e).lower()
        if "429" in error_str or "quota" in error_str or "rate" in error_str:
            raise GeminiRateLimitError(f"Gemini rate limit: {e}")
        if "timeout" in error_str:
            raise GeminiError(f"Gemini timeout: {e}")
        raise GeminiError(f"Gemini error: {e}")
