import base64
from datetime import datetime, timedelta, timezone
from email.utils import parsedate_to_datetime
from typing import Any, Dict, List

from bs4 import BeautifulSoup
from google.oauth2.credentials import Credentials
from googleapiclient.discovery import build


def _build_gmail_service(access_token: str, refresh_token: str = None):
    """Build Google Gmail API service from stored tokens."""
    creds = Credentials(
        token=access_token,
        refresh_token=refresh_token,
        token_uri="https://oauth2.googleapis.com/token",
    )
    return build("gmail", "v1", credentials=creds)


def _decode_email_body(payload: dict) -> str:
    """
    Recursively extract plain text body from Gmail message payload.
    Handles multipart emails.
    """
    body = ""

    if "parts" in payload:
        for part in payload["parts"]:
            body += _decode_email_body(part)
    else:
        mime_type = payload.get("mimeType", "")
        data = payload.get("body", {}).get("data", "")
        if data:
            decoded = base64.urlsafe_b64decode(data + "==").decode("utf-8", errors="ignore")
            if mime_type == "text/plain":
                body += decoded
            elif mime_type == "text/html":
                soup = BeautifulSoup(decoded, "lxml")
                body += soup.get_text(separator=" ", strip=True)

    return body


def fetch_gmail_messages(
    access_token: str,
    refresh_token: str = None,
    days_back: int = 30,
    max_results: int = 100,
) -> List[Dict[str, Any]]:
    """
    Fetch emails from Gmail for the last N days.
    Returns list of parsed email dicts.

    Each dict:
    {
        "document_id": "gmail_<message_id>",
        "source_type": "gmail",
        "subject": "...",
        "sender": "...",
        "timestamp": "ISO string",
        "content": "full plain text of email body",
    }
    """
    service = _build_gmail_service(access_token, refresh_token)

    # Build date filter
    after_date = datetime.now(timezone.utc) - timedelta(days=days_back)
    after_str = after_date.strftime("%Y/%m/%d")
    query = f"after:{after_str}"

    # List messages
    results = (
        service.users()
        .messages()
        .list(
            userId="me",
            q=query,
            maxResults=max_results,
        )
        .execute()
    )

    messages_list = results.get("messages", [])
    parsed_messages = []

    for msg_ref in messages_list:
        try:
            msg = (
                service.users()
                .messages()
                .get(
                    userId="me",
                    id=msg_ref["id"],
                    format="full",
                )
                .execute()
            )

            headers = msg.get("payload", {}).get("headers", [])
            header_map = {h["name"].lower(): h["value"] for h in headers}

            subject = header_map.get("subject", "(no subject)")
            sender = header_map.get("from", "unknown")
            date_str = header_map.get("date", "")

            body = _decode_email_body(msg.get("payload", {})).strip()

            if not body:
                continue

            # Parse timestamp
            try:
                timestamp = parsedate_to_datetime(date_str).isoformat()
            except Exception:
                timestamp = datetime.now(timezone.utc).isoformat()

            parsed_messages.append({
                "document_id": f"gmail_{msg_ref['id']}",
                "source_type": "gmail",
                "subject": subject,
                "sender": sender,
                "timestamp": timestamp,
                "content": f"Subject: {subject}\nFrom: {sender}\n\n{body}",
            })

        except Exception as e:
            print(f"⚠️ Failed to fetch Gmail message {msg_ref.get('id')}: {e}")
            continue

    print(f"✅ Gmail: fetched {len(parsed_messages)} emails")
    return parsed_messages
