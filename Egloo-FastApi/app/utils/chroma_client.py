import chromadb
from app.config import settings

_client = None


def get_chroma_client() -> chromadb.HttpClient:
    """
    Returns a singleton ChromaDB HTTP client.
    ChromaDB runs as a separate Docker service on port 8001.
    """
    global _client
    if _client is None:
        _client = chromadb.HttpClient(
            host=settings.CHROMA_HOST,
            port=settings.CHROMA_PORT,
        )
    return _client


def get_or_create_collection(user_id: str) -> chromadb.Collection:
    """
    Each user gets their own ChromaDB collection.
    Collection name: egloo_user_{user_id}
    This isolates one user's vectors from another's completely.
    """
    client = get_chroma_client()
    collection_name = f"egloo_user_{user_id.replace('-', '_')}"
    collection = client.get_or_create_collection(
        name=collection_name,
        metadata={"hnsw:space": "cosine"},
    )
    return collection
