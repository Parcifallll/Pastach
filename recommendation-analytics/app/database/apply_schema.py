import sys
import clickhouse_connect
from pathlib import Path
import logging

logger = logging.getLogger(__name__)

sys.path.insert(0, str(Path(__file__).parent))

from app.config import settings


def apply_schema():
    logger.info(f"Host: clickhouse-rec-analytics")
    logger.info(f"Port: {settings.REC_ANALYTICS_CLICKHOUSE_PORT}")
    logger.info(f"User: {settings.REC_ANALYTICS_CLICKHOUSE_USER}")

    try:
        client = clickhouse_connect.get_client(
            host="clickhouse-rec-analytics",
            port=settings.REC_ANALYTICS_CLICKHOUSE_PORT,
            username=settings.REC_ANALYTICS_CLICKHOUSE_USER,
            password=settings.REC_ANALYTICS_CLICKHOUSE_PASSWORD,
        )

        schema_file = Path(__file__).parent / 'init.sql'

        if not schema_file.exists():
            logger.info(f"Schema file not found: {schema_file}")
            sys.exit(1)

        with open(schema_file, 'r') as f:
            sql_statements = f.read()

        for statement in sql_statements.split(';'):
            statement = statement.strip()
            if statement and not statement.startswith('--'):
                client.command(statement)
        result = client.query("SHOW TABLES FROM analytics")

        if result.result_rows:
            logger.info("Tables created:")
            for row in result.result_rows:
                logger.info(f"  - {row[0]}")
        else:
            logger.info("No tables found")

        client.close()

    except Exception as e:
        logger.info(f"Error applying schema: {e}")
        sys.exit(1)


if __name__ == '__main__':
    apply_schema()