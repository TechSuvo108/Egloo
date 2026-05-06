# 🐧 Egloo Backend — Pingo Second Brain

Egloo is an AI-powered **second brain** that connects your **Gmail, Slack, and Google Drive** into a single intelligent assistant named **Pingo**.

Pingo helps users:

* Search across connected sources using natural language
* Generate daily digests and summaries
* Cluster information into smart topics
* Save important answers and notes
* Build a personalized knowledge base over time

---

## 🚀 Tech Stack

| Layer                 | Technology                        |
| --------------------- | --------------------------------- |
| **Backend Framework** | FastAPI (Python 3.12)             |
| **Database**          | PostgreSQL 15 + SQLAlchemy Async  |
| **Vector Database**   | ChromaDB                          |
| **Cache / Queue**     | Redis 7                           |
| **Workers**           | Celery + Celery Beat              |
| **AI Pipeline**       | LangChain + sentence-transformers |
| **Primary LLM**       | Gemini 1.5 Flash                  |
| **Secondary LLM**     | Groq (Llama 3)                    |
| **Fallback LLM**      | OpenRouter                        |
| **Containerization**  | Docker + Docker Compose           |

---

## 📦 Features

* JWT Authentication
* Gmail OAuth Integration
* Google Drive OAuth Integration
* Slack OAuth Integration
* Document Ingestion + Chunking
* Embedding + Vector Search
* Retrieval-Augmented Generation (RAG)
* Multi-Provider LLM Routing
* Daily Digest Generation
* Topic Clustering
* Saved / Bookmark System
* Background Jobs with Celery
* Dockerized Development Environment
* Environment Validation System
* End-to-End Test Suite

---

## ⚡ Quick Start

### 1) Clone repository

```bash
git clone <your-repo-url>
cd egloo/backend
```

### 2) Create environment file

```bash
cp .env.example .env
```

### 3) Generate secure keys

```bash
python -c "import secrets; print('SECRET_KEY=' + secrets.token_hex(32))"
python -c "import secrets; print('ENCRYPTION_KEY=' + secrets.token_hex(32))"
```

Paste both into `.env`.

---

### 4) Add at least one LLM API key

Free providers:

* Gemini → https://aistudio.google.com/app/apikey
* Groq → https://console.groq.com/keys

Example:

```env
GEMINI_API_KEYS=your_key_here
GROQ_API_KEYS=your_key_here
OPENROUTER_API_KEYS=
```

---

### 5) Start backend

```bash
docker compose build
docker compose up -d
```

Run migrations:

```bash
docker compose exec api alembic upgrade head
```

Seed demo user:

```bash
docker compose exec api python -m app.seed
```

---

### 6) Verify

Swagger Docs:

```text
http://localhost:8000/docs
```

Health Check:

```text
http://localhost:8000/health
```

Expected:

```json
{
  "status": "healthy",
  "services": {
    "postgres": "connected",
    "redis": "connected",
    "chroma": "connected",
    "llm": "configured"
  },
  "assistant": "Pingo 🐧"
}
```

---

## 🧠 API Modules

| Module  | Endpoint Prefix   | Purpose                          |
| ------- | ----------------- | -------------------------------- |
| Auth    | `/api/v1/auth`    | Register, login, refresh, logout |
| Sources | `/api/v1/sources` | Connect Gmail / Slack / Drive    |
| Ingest  | `/api/v1/ingest`  | Fetch, chunk, embed, store       |
| Query   | `/api/v1/query`   | Ask Pingo questions              |
| Digest  | `/api/v1/digest`  | Daily summaries                  |
| Topics  | `/api/v1/topics`  | Auto-clustered topic groups      |
| Saved   | `/api/v1/saved`   | Bookmarks                        |
| LLM     | `/api/v1/llm`     | Provider health + usage          |

---

## 🔐 Environment Variables

| Variable             | Required | Description                     |
| -------------------- | -------- | ------------------------------- |
| DATABASE_URL         | Yes      | PostgreSQL connection string    |
| REDIS_URL            | Yes      | Redis connection                |
| CHROMA_HOST          | Yes      | Chroma host                     |
| CHROMA_PORT          | Yes      | Chroma port                     |
| SECRET_KEY           | Yes      | JWT signing secret              |
| ENCRYPTION_KEY       | Yes      | Encryption secret               |
| GEMINI_API_KEYS      | One LLM  | Comma-separated Gemini keys     |
| GROQ_API_KEYS        | One LLM  | Comma-separated Groq keys       |
| OPENROUTER_API_KEYS  | One LLM  | Comma-separated OpenRouter keys |
| GOOGLE_CLIENT_ID     | Optional | Gmail / Drive OAuth             |
| GOOGLE_CLIENT_SECRET | Optional | Gmail / Drive OAuth             |
| SLACK_CLIENT_ID      | Optional | Slack OAuth                     |
| SLACK_CLIENT_SECRET  | Optional | Slack OAuth                     |
| FCM_CREDENTIALS_PATH | Optional | Firebase push notifications     |

---

## 🏗 Architecture

```text
Android App (Kotlin)
      |
      v
FastAPI Gateway
(CORS + JWT + Routers)
      |
      v
+----------------------------------+
| auth | sources | ingest | query  |
| digest | topics | saved | llm    |
+----------------------------------+
      |
      v
+----------------------------------+
| AI Pipeline                      |
| embed -> vector search -> LLM    |
| Gemini -> Groq -> OpenRouter     |
+----------------------------------+
      |
      v
+------------+-----------+---------+
| PostgreSQL | ChromaDB  | Redis   |
| Metadata   | Vectors   | Cache   |
+------------+-----------+---------+
      |
      v
Celery Worker + Beat
```

---

## 🛠 Common Commands

```bash
docker compose up -d        # Start services
docker compose down         # Stop services
docker compose logs -f      # Watch logs
docker compose ps           # Service status
docker compose build        # Rebuild images
docker compose restart api  # Restart API
```

---

## ✅ Status

**Backend Complete — Production Ready**

Assistant Name: **Pingo 🐧**
