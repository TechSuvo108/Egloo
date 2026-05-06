.PHONY: help build up down logs shell migrate seed test

help:
	@echo ""
	@echo "🐧 Egloo Backend — Available commands"
	@echo ""
	@echo "  make build     Build all Docker images"
	@echo "  make up        Start all services"
	@echo "  make down      Stop all services"
	@echo "  make logs      Follow all service logs"
	@echo "  make api-logs  Follow API logs only"
	@echo "  make worker-logs  Follow worker logs"
	@echo "  make shell     Open shell in API container"
	@echo "  make migrate   Run Alembic migrations"
	@echo "  make seed      Seed demo user"
	@echo "  make health    Check all service health"
	@echo ""

build:
	docker compose build

up:
	docker compose up -d
	@echo "🐧 Egloo is starting..."
	@echo "   API:     http://localhost:8000"
	@echo "   Docs:    http://localhost:8000/docs"
	@echo "   Health:  http://localhost:8000/health"

down:
	docker compose down

logs:
	docker compose logs -f

api-logs:
	docker compose logs -f api

worker-logs:
	docker compose logs -f worker

beat-logs:
	docker compose logs -f beat

shell:
	docker compose exec api sh

migrate:
	docker compose exec api alembic upgrade head

seed:
	docker compose exec api python -m app.seed

health:
	curl -s http://localhost:8000/health | python -m json.tool

restart-api:
	docker compose restart api

restart-worker:
	docker compose restart worker

clean:
	docker compose down -v
	@echo "⚠️  All volumes deleted including database data"
