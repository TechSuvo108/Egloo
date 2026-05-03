from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware


@asynccontextmanager
async def lifespan(app: FastAPI):
    print("🐧 Egloo API starting... PenGo is waking up!")
    yield
    print("🐧 Egloo API shutting down... PenGo is sleeping!")


app = FastAPI(
    title="Egloo API",
    version="1.0.0",
    description="PenGo — Your second brain assistant",
    lifespan=lifespan,
)

# ── CORS ──────────────────────────────────────────────────────────────────────
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ── Root ──────────────────────────────────────────────────────────────────────
@app.get("/")
async def root():
    return {
        "message": "Welcome to Egloo",
        "status": "running",
        "assistant": "PenGo",
        "version": "1.0.0",
    }


# ── Health ────────────────────────────────────────────────────────────────────
@app.get("/health")
async def health():
    # Placeholder — real DB/Redis checks will be added later.
    return {
        "status": "healthy",
        "database": "connected",
        "redis": "connected",
    }
