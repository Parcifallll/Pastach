pip-install:
	pip install -r requirements.txt

superset-psycopb:
	docker compose exec -u root superset bash -c "/app/.venv/bin/python -m ensurepip && /app/.venv/bin/python -m pip install psycopg2-binary"

superset-clickhouse:
	docker compose exec -u root superset bash -c "/app/.venv/bin/python -m pip install clickhouse-connect"