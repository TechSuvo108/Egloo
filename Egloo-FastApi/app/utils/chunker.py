from typing import Any, Dict, List

from langchain_text_splitters import RecursiveCharacterTextSplitter


def chunk_text(
    text: str,
    metadata: Dict[str, Any],
    chunk_size: int = 500,
    chunk_overlap: int = 50,
) -> List[Dict[str, Any]]:
    """
    Split a long text into overlapping chunks.
    Each chunk keeps the original metadata attached.

    Returns list of dicts:
    [
        {
            "content": "chunk text here...",
            "metadata": {
                "source_type": "gmail",
                "sender": "rohit@company.com",
                "subject": "Project Alpha update",
                "timestamp": "2024-04-29T10:00:00",
                "document_id": "email_id_xyz",
                "chunk_index": 0,
            }
        },
        ...
    ]
    """
    if not text or not text.strip():
        return []

    splitter = RecursiveCharacterTextSplitter(
        chunk_size=chunk_size,
        chunk_overlap=chunk_overlap,
        separators=["\n\n", "\n", ". ", " ", ""],
    )

    raw_chunks = splitter.split_text(text)

    chunks = []
    for i, chunk_content in enumerate(raw_chunks):
        chunk_content = chunk_content.strip()
        if not chunk_content:
            continue
        chunk_metadata = {**metadata, "chunk_index": i}
        chunks.append({
            "content": chunk_content,
            "metadata": chunk_metadata,
        })

    return chunks
