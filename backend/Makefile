# VARIABLES
PROJECT_NAME := backend-app
DOCKER_COMPOSE := docker compose
MAVEN := mvn
INFRA_SERVICES := backend-postgres zookeeper kafka-1 kafka-2 kafka-3 backend-redis

# Phony targets
.PHONY: help dev infra infra-up infra-down build run logs \
        test restart db-connect kafka-topics netstat tasklist

help:
	@echo "$(GREEN)Available commands:$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "$(BLUE)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""

# DEVELOPMENT
dev: infra build  ## build JAR (without clean) + run app and infra (app is not running)
run: build full-build ## clean development (full rebuild)

# INFRASTRUCTURE
infra: ## run all infrastructure services (except app) without logs (--detached, -d for short flag)
	@$(DOCKER_COMPOSE) up --detach $(INFRA_SERVICES)

infra-down: ## stop all infrastructure services
	@$(DOCKER_COMPOSE) stop $(INFRA_SERVICES)

infra-logs: ## show infrastructure logs
	@$(DOCKER_COMPOSE) logs --follow $(INFRA_SERVICES)

# APPLICATION

build: ## rebuild JAR (full clean build, skip tests)
	@$(MAVEN) clean package -DskipTests

full-build: infra ## run full-app in docker
	@$(DOCKER_COMPOSE) up --build backend-app

fast: ## incremental build JAR (no clean)
	@$(MAVEN) package -DskipTests

compile: ## compile (use for grpc-files generation from .proto)
	@$(MAVEN) clean compile -DskipTests

run-detached: infra ## run app without logs
	@$(DOCKER_COMPOSE) up --build --detach app

app-logs: ## show app logs
	@$(DOCKER_COMPOSE) logs --follow app

app-stop: ## stop app
	@$(DOCKER_COMPOSE) stop app

app-restart: app-stop run ## restart the app

down: infra-down infra-down

# DATABASE
db-connect: ## connect to PostgreSQL
	@$(DOCKER_COMPOSE) exec db psql --username $$(grep DB_USERNAME .env | cut --delimiter='=' --fields=2) --dbname backend

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

test-build: ## build with all tests
	@$(MAVEN) clean verify

# MONITORING & STATUS
logs: ## all-services logs
	@$(DOCKER_COMPOSE) logs --follow

logs-app: app-logs ## alias for app-logs

logs-infra: infra-logs ## alias for infra-logs

# DOCKER
docker-prune: ## clean un-used resources
	@docker system prune --force

ps: ps
	@$(DOCKER_COMPOSE) ps

storage:
	@docker system df -v

# check port usage
netstat:
	@netstat -ano | findstr ":$(filter-out $@,$(MAKECMDGOALS))"

tasklist:
	@tasklist | findstr "$(filter-out $@,$(MAKECMDGOALS))"

kill:
	@taskkill /PID "$(filter-out $@,$(MAKECMDGOALS))" /F

kafka-describe:
	docker compose exec kafka-1 kafka-topics --describe --topic pastach.posts,pastach.reactions,pastach.recommendations --bootstrap-server kafka-1:9092,kafka-2:9093,kafka-3:9094

delete-topics:
	docker compose exec kafka-1 kafka-topics --delete \
      --topic pastach.posts,pastach.reactions,pastach.recommendations \
      --bootstrap-server kafka-1:9092

# before bash scripts for analytics (+refresh increment)
#TRUNCATE TABLE posts RESTART IDENTITY CASCADE;
#TRUNCATE TABLE comments RESTART IDENTITY CASCADE;
#TRUNCATE TABLE reactions RESTART IDENTITY CASCADE;

#TRUNCATE TABLE posts_ml RESTART IDENTITY CASCADE;
#TRUNCATE TABLE reactions_ml RESTART IDENTITY CASCADE;
#TRUNCATE TABLE user_preferences RESTART IDENTITY CASCADE;