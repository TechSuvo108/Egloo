from typing import List, Tuple
from app.config import settings


def validate_environment() -> Tuple[bool, List[str], List[str]]:
    """
    Validate all required and optional environment variables.

    Returns:
        (is_valid, errors, warnings)

        errors   — missing critical config — app will NOT work
        warnings — missing optional config — app works but features disabled
    """
    errors = []
    warnings = []

    # ── Critical: must have these or app breaks ───────────────────────────────

    if not settings.DATABASE_URL:
        errors.append("DATABASE_URL is not set")

    if not settings.REDIS_URL:
        errors.append("REDIS_URL is not set")

    if settings.SECRET_KEY in (
        "change-this-secret-key-in-production",
        "your-secret-key-here",
        "",
        None,
    ):
        errors.append(
            "SECRET_KEY is using the default insecure value. "
            "Generate one with: python -c \"import secrets; print(secrets.token_hex(32))\""
        )

    if settings.ENCRYPTION_KEY in (
        "change-this-encryption-key-in-production",
        "your-fernet-key-here",
        "",
        None,
    ):
        errors.append(
            "ENCRYPTION_KEY is using the default insecure value. "
            "Generate one with: python -c \"import secrets; print(secrets.token_hex(32))\""
        )

    # ── LLM: need at least one provider ──────────────────────────────────────

    llm_keys = [
        settings.GEMINI_API_KEYS,
        settings.GROQ_API_KEYS,
        settings.OPENROUTER_API_KEYS,
    ]
    if not any(llm_keys):
        errors.append(
            "No LLM API key configured. "
            "Set at least one of: GEMINI_API_KEYS, GROQ_API_KEYS, OPENROUTER_API_KEYS. "
            "Get free keys at: "
            "Gemini→https://aistudio.google.com/app/apikey "
            "Groq→https://console.groq.com/keys"
        )

    # ── Optional: OAuth sources ───────────────────────────────────────────────

    if not settings.GOOGLE_CLIENT_ID or not settings.GOOGLE_CLIENT_SECRET:
        warnings.append(
            "GOOGLE_CLIENT_ID / GOOGLE_CLIENT_SECRET not set. "
            "Gmail and Google Drive OAuth will not work."
        )

    if not settings.SLACK_CLIENT_ID or not settings.SLACK_CLIENT_SECRET:
        warnings.append(
            "SLACK_CLIENT_ID / SLACK_CLIENT_SECRET not set. "
            "Slack OAuth will not work."
        )

    # ── Optional: FCM ─────────────────────────────────────────────────────────

    if not settings.FCM_CREDENTIALS_PATH:
        warnings.append(
            "FCM_CREDENTIALS_PATH not set. "
            "Push notifications to Android will be disabled."
        )

    is_valid = len(errors) == 0
    return is_valid, errors, warnings


def print_env_report():
    """Print a formatted environment validation report to console."""
    is_valid, errors, warnings = validate_environment()

    print("\n" + "=" * 55)
    print("EGLOO - Environment Validation Report")
    print("=" * 55)

    if errors:
        print(f"\n[ERRORS] ({len(errors)}) - must fix before running:\n")
        for i, err in enumerate(errors, 1):
            print(f"  {i}. {err}")

    if warnings:
        print(f"\n[WARNINGS] ({len(warnings)}) - optional features disabled:\n")
        for i, warn in enumerate(warnings, 1):
            print(f"  {i}. {warn}")

    if is_valid:
        print(
            "\n[OK] Environment is valid - "
            "PenGo is ready to hatch!\n"
        )
    else:
        print(
            "\n[FAIL] Environment has errors - "
            "fix them before starting.\n"
        )

    print("=" * 55 + "\n")
    return is_valid
