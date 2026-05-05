# Egloo Backend - PenGo Second Brain

Egloo is a second brain application that connects your Gmail,
Slack, and Google Drive into a single intelligent assistant named PenGo.

## Tech Stack

| Layer          | Technology                        |
|----------------|-----------------------------------|
| Backend        | FastAPI (Python 3.12)             |
| Database       | PostgreSQL 15 + SQLAlchemy async  |
| Vector DB      | ChromaDB                          |
| Cache / Queue  | Redis 7                           |
| AI Pipeline    | LangChain + sentence-transformers |
| LLM Primary    | Gemini 1.5 Flash                  |
| LLM Secondary  | Groq (llama3-8b)                  |
| LLM Fallback   | OpenRouter                        |
| Workers        | Celery + Celery Beat              |
| Containers     | Docker + Docker Compose           |

## Quick Start

### 1. Clone and setup

cd egloo/backend
cp .env.example .env

### 2. Generate secure keys

python -c "import secrets; print('SECRET_KEY=' + secrets.token_hex(32))"
python -c "import secrets; print('ENCRYPTION_KEY=' + secrets.token_hex(32))"

Paste into .env

### 3. Add at least one LLM API key to .env

Free options:
* Gemini: https://aistudio.google.com/app/apikey
* Groq:   https://console.groq.com/keys

### 4. Start everything

make build
make up
make migrate
make seed

### 5. Open the API

Swagger UI: http://localhost:8000/docs
Health:     http://localhost:8000/health

## API Modules

| Module   | Prefix              | Description                    |
|----------|---------------------|--------------------------------|
| Auth     | /api/v1/auth        | Register, login, JWT tokens    |
| Sources  | /api/v1/sources     | OAuth connect Gmail/Slack/Drive|
| Ingest   | /api/v1/ingest      | Fetch, chunk, embed, store     |
| Query    | /api/v1/query       | Ask PenGo questions (RAG)      |
| Digest   | /api/v1/digest      | Daily auto-generated summaries |
| Topics   | /api/v1/topics      | Auto-clustered topic groups    |
| Saved    | /api/v1/saved       | Bookmark digests and answers   |
| LLM      | /api/v1/llm         | Provider health and usage      |

## Environment Variables

| Variable              | Required | Description                     |
|-----------------------|----------|---------------------------------|
| DATABASE_URL          | Yes      | PostgreSQL connection string    |
| REDIS_URL             | Yes      | Redis connection string         |
| SECRET_KEY            | Yes      | JWT signing key                 |
| ENCRYPTION_KEY        | Yes      | Token encryption key            |
| GEMINI_API_KEYS        | One LLM  | Comma-separated Gemini keys      |
| GROQ_API_KEYS          | One LLM  | Comma-separated Groq keys        |
| OPENROUTER_API_KEYS    | One LLM  | Comma-separated OpenRouter keys  |
| GOOGLE_CLIENT_ID      | Optional | Gmail + Drive OAuth             |
| GOOGLE_CLIENT_SECRET  | Optional | Gmail + Drive OAuth             |
| SLACK_CLIENT_ID       | Optional | Slack OAuth                     |
| SLACK_CLIENT_SECRET   | Optional | Slack OAuth                     |
| FCM_CREDENTIALS_PATH  | Optional | Firebase push notifications     |

## Architecture

Android (Kotlin + Ktor)
        |
        v
FastAPI Gateway (CORS + JWT middleware)
        |
        v
+-----------------------------------+
|  Routers: auth sources ingest      |
|           query digest topics      |
|           saved llm                |
+-----------------------------------+
        |
        v
+-----------------------------------+
|  Services + AI Pipeline            |
|  RAG: embed -> ChromaDB -> LLM    |
|  LLM router: Gemini -> Groq -> OR |
+-----------------------------------+
        |
        v
+-------------+----------+---------+
| PostgreSQL  | ChromaDB |  Redis  |
| (metadata)  |(vectors) |(cache)  |
+-------------+----------+---------+
        |
        v
Celery Worker + Beat (background jobs)

## Common Commands

make up            # Start all services
make down          # Stop all services
make logs          # Follow all logs
make migrate       # Run DB migrations
make seed          # Create demo user
make health        # Check service health
make shell         # Shell into API container