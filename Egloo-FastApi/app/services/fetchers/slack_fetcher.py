from datetime import datetime, timedelta, timezone
from typing import Any, Dict, List

from slack_sdk.web.async_client import AsyncWebClient


async def fetch_slack_messages(
    access_token: str,
    days_back: int = 30,
    max_messages_per_channel: int = 200,
) -> List[Dict[str, Any]]:
    """
    Fetch messages from all Slack channels the user has access to.
    Returns list of parsed message dicts.

    Each dict:
    {
        "document_id": "slack_<channel_id>_<ts>",
        "source_type": "slack",
        "channel": "#general",
        "sender": "username",
        "timestamp": "ISO string",
        "content": "channel and message text",
    }
    """
    client = AsyncWebClient(token=access_token)

    oldest_ts = str(
        (datetime.now(timezone.utc) - timedelta(days=days_back)).timestamp()
    )

    # Fetch all channels
    try:
        channels_resp = await client.conversations_list(
            types="public_channel,private_channel",
            limit=100,
        )
        channels = channels_resp.get("channels", [])
    except Exception as e:
        print(f"⚠️ Failed to list Slack channels: {e}")
        return []

    all_messages = []

    for channel in channels:
        channel_id = channel["id"]
        channel_name = channel.get("name", channel_id)

        # Skip archived channels
        if channel.get("is_archived"):
            continue

        try:
            history_resp = await client.conversations_history(
                channel=channel_id,
                oldest=oldest_ts,
                limit=max_messages_per_channel,
            )
            messages = history_resp.get("messages", [])

            for msg in messages:
                text = msg.get("text", "").strip()
                if not text:
                    continue

                # Skip bot messages and system messages
                if msg.get("subtype") in ("bot_message", "channel_join", "channel_leave"):
                    continue

                sender = msg.get("user", "unknown")
                ts = msg.get("ts", "")

                # Convert Slack ts to ISO
                try:
                    timestamp = datetime.fromtimestamp(
                        float(ts), tz=timezone.utc
                    ).isoformat()
                except Exception:
                    timestamp = datetime.now(timezone.utc).isoformat()

                all_messages.append({
                    "document_id": f"slack_{channel_id}_{ts}",
                    "source_type": "slack",
                    "channel": f"#{channel_name}",
                    "sender": sender,
                    "timestamp": timestamp,
                    "content": f"Channel: #{channel_name}\n\n{text}",
                })

        except Exception as e:
            print(f"⚠️ Failed to fetch Slack channel {channel_name}: {e}")
            continue

    print(f"✅ Slack: fetched {len(all_messages)} messages")
    return all_messages
