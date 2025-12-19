# VARIABLES
PROJECT_NAME := pastach
DOCKER_COMPOSE := docker-compose
MAVEN := mvn
INFRA_SERVICES := db zookeeper kafka

# colors for printing
RED := \033[0;31m
GREEN := \033[0;32m
YELLOW := \033[1;33m
BLUE := \033[0;34m
NC := \033[0m # No Color

# Phony targets
.PHONY: help dev infra infra-up infra-down build run logs clean clean-all \
        test restart status ps db-connect kafka-topics

help:
	@echo "$(GREEN)Available commands:$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "$(BLUE)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""

# DEVELOPMENT
dev: fast run ## build JAR (without clean) + run app and infr (if it's not running)
all: build run ## clean development (full rebuild)

# INFRASTRUCTURE
infra: infra-up
infra-up: ## run all infrastructure services (except app) without logs (--detached, -d for short flag)
	@$(DOCKER_COMPOSE) up --detach $(INFRA_SERVICES)

infra-down: ## stop all infrastructure services
	@$(DOCKER_COMPOSE) stop $(INFRA_SERVICES)

infra-logs: ## show infrastructure logs
	@$(DOCKER_COMPOSE) logs --follow $(INFRA_SERVICES)

# APPLICATION
build: ## rebuild JAR (full clean build, skip tests)
	@$(MAVEN) clean package -DskipTests

fast: ## incremental build JAR (no clean)
	@$(MAVEN) package -DskipTests

compile: ## compile only
	@$(MAVEN) compile -DskipTests

run: infra-up ## run app with infrastructure
	@$(DOCKER_COMPOSE) up --build app

run-detached: infra-up ## run app without logs
	@$(DOCKER_COMPOSE) up --build --detach app

app-logs: ## show app logs
	@$(DOCKER_COMPOSE) logs --follow app

app-stop: ## stop app
	@$(DOCKER_COMPOSE) stop app

app-restart: app-stop run ## restart the app

# DATABASE
db-connect: ## connect to PostgreSQL
	@$(DOCKER_COMPOSE) exec db psql --username $$(grep DB_USERNAME .env | cut --delimiter='=' --fields=2) --dbname PastachDB

db-backup: ## create db-backup
	@mkdir --parents backups
	@$(DOCKER_COMPOSE) exec --interactive=false db pg_dump --username $$(grep DB_USERNAME .env | cut --delimiter='=' --fields=2) PastachDB > backups/backup_$$(date +%Y%m%d_%H%M%S).sql

db-restore: ## restore db from backup (make db-restore FILE=backup.sql)
	@if [ -z "$(FILE)" ]; then \
		echo "$(RED)Enter filename: make db-restore FILE=backup.sql$(NC)"; \
		exit 1; \
	fi
	@$(DOCKER_COMPOSE) exec --interactive=false db psql --username $$(grep DB_USERNAME .env | cut --delimiter='=' --fields=2) --dbname PastachDB < $(FILE)

# KAFKA
kafka-topics: ## show kafka topics
	@$(DOCKER_COMPOSE) exec kafka kafka-topics --list --bootstrap-server localhost:9092

kafka-console-producer: ## run console kafka-producer
	@$(DOCKER_COMPOSE) exec kafka kafka-console-producer --broker-list localhost:9092 --topic test

kafka-console-consumer: ## run console consumer-kafka (make kafka-console-consumer TOPIC=test)
	@if [ -z "$(TOPIC)" ]; then \
		echo "$(RED)enter topic: make kafka-console-consumer TOPIC=test$(NC)"; \
		exit 1; \
	fi
	@$(DOCKER_COMPOSE) exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic $(TOPIC) --from-beginning

# TESTING
test: ## run all tests
	@$(MAVEN) test

test-unit: ## run unit tests
	@$(MAVEN) test -Dtest="*Test"

test-integration: ## run integration tests
	@$(MAVEN) test -Dtest="*IT"

test-build: ## build with all tests
	@$(MAVEN) clean verify

# MONITORING & STATUS
status: ## all services-statuses
	@$(DOCKER_COMPOSE) ps

ps: status ## alias for status

logs: ## all-services logs
	@$(DOCKER_COMPOSE) logs --follow

logs-app: app-logs ## alias for app-logs

logs-infra: infra-logs ## alias for infra-logs

# CLEANUP
clean: ## stop all containers
	@$(DOCKER_COMPOSE) down

clean-all: ## full clean-up (images, containers, volumes)
	@$(DOCKER_COMPOSE) down --volumes --remove-orphans
	@docker image prune --force
	@$(MAVEN) clean

# DOCKER
docker-images: ## show docker images
	@docker images

docker-prune: ## clean un-used resources
	@docker system prune --force

# DEPLOYMENT
deploy: test-build ## deploy (build with tests and run in PROD)
	@$(DOCKER_COMPOSE) --file docker-compose.prod.yml up --detach --build