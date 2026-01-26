# VARIABLES
PROJECT_NAME := pastach
DOCKER_COMPOSE := docker-compose
MAVEN := mvn
INFRA_SERVICES := db zookeeper kafka redis

# Phony targets
.PHONY: help dev infra infra-up infra-down build run logs \
        test restart db-connect kafka-topics netstat

help:
	@echo "$(GREEN)Available commands:$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "$(BLUE)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""

# DEVELOPMENT
debug:
	@$(MAVEN) spring-boot:run
dev: fast run ## build JAR (without clean) + run app and infr (if it's not running)
dev-all: build run ## clean development (full rebuild)

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

compile: ## compile (use for grpc-files generation from .proto)
	@$(MAVEN) clean compile -DskipTests

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

# check port usage
netstat:
	@netstat -ano | findstr ":$(filter-out $@,$(MAKECMDGOALS))"
