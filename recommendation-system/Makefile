PROJECT_NAME := recommendation-system
DOCKER_COMPOSE := docker-compose
PYTHON := python3
INFRA_SERVICES := recsys-postgres recsys-redis recsys-grpc-server recsys-consumer-1 recsys-consumer-2 recsys-consumer-3

RED := \033[0;31m
GREEN := \033[0;32m
BLUE := \033[0;34m
NC := \033[0m

.PHONY: help dev infra infra-up infra-down run logs \
        status ps db-connect alembic-upgrade alembic-downgrade venv requirements docker-space

help:
	@echo "$(GREEN)Available commands:$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
	   awk 'BEGIN {FS = ":.*?## "}; {printf "$(BLUE)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""

debug:
	python -m main
dev: run ## build and run app + infrastructure

infra: infra-up
infra-up: ## run infrastructure
	@$(DOCKER_COMPOSE) up --detach $(INFRA_SERVICES)

infra-down: ## stop infrastructure
	@$(DOCKER_COMPOSE) stop $(INFRA_SERVICES)

infra-logs: ## show infrastructure logs
	@$(DOCKER_COMPOSE) logs --follow $(INFRA_SERVICES)

run: infra-up ## run app + infrastructure
	@$(DOCKER_COMPOSE) up --build app

run-detached: infra-up ## run app without logs
	@$(DOCKER_COMPOSE) up --build --detach app

app-logs: ## show app logs
	@$(DOCKER_COMPOSE) logs --follow app

app-stop: ## stop app
	@$(DOCKER_COMPOSE) stop app

app-restart: app-stop run ## restart app


redis-connect:
	@docker exec -it recsys-redis redis-cli -a dev_redis
db-connect: ## connect to PostgreSQL
	@$(DOCKER_COMPOSE) exec postgres psql --username $$(grep POSTGRES_USER .env | cut --delimiter='=' --fields=2) --dbname $$(grep POSTGRES_DB .env | cut --delimiter='=' --fields=2)

alembic-upgrade: ## run migrations
	@$(DOCKER_COMPOSE) exec recsys-app alembic upgrade head

alembic-downgrade: ## rollback migrations
	@$(DOCKER_COMPOSE) exec recsys-app alembic downgrade -1

alembic-history: ## migration history
	@$(DOCKER_COMPOSE) exec recsys-app alembic history

status: ## services status
	@docker ps

ps: status

storage:
	@docker system df -v

logs: ## show logs
	@$(DOCKER_COMPOSE) logs --follow

## generates classes-serialization + its call, from .proto
## NB: after auto-generation it is necessary to change import to app.grpc.proto.recposts_pb2
proto:
	python -m grpc_tools.protoc \
		-Iapp/grpc/proto \
		--python_out=app/grpc/proto \
		--grpc_python_out=app/grpc/proto \
		app/grpc/proto/recposts.proto

venv:
	python -m venv venv

requirements:
	pip install -r requirements.txt

docker-space:
	docker system df