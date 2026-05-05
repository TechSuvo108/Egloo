from typing import List

from sentence_transformers import SentenceTransformer

_model = None


def get_embedding_model() -> SentenceTransformer:
    """
    Loads sentence-transformers model once and reuses it.
    Model: all-MiniLM-L6-v2
    - Fast, lightweight, good quality
    - 384-dimensional vectors
    - Downloads automatically on first use (~80MB)
    """
    global _model
    if _model is None:
        print("[Pingo] Loading embedding model for the first time...")
        _model = SentenceTransformer("all-MiniLM-L6-v2")
        print("[OK] Embedding model loaded")
    return _model


def embed_texts(texts: List[str]) -> List[List[float]]:
    """
    Convert a list of text strings into vectors.
    Returns list of float lists — one vector per text.
    """
    model = get_embedding_model()
    embeddings = model.encode(texts, show_progress_bar=False)
    return embeddings.tolist()


def embed_single(text: str) -> List[float]:
    """
    Embed a single text string.
    Used for embedding user queries in the RAG pipeline.
    """
    return embed_texts([text])[0]
