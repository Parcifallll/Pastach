from logging.config import fileConfig
from sqlalchemy import engine_from_config
from sqlalchemy import pool
from alembic import context
from app.config import settings
from clickhouse_sqlalchemy.alembic.dialect import patch_alembic_version, include_object

config = context.config
fileConfig(config.config_file_name)

target_metadata = None

def run_migrations_offline():
    url = settings.REC_ANALYTICS_CLICKHOUSE_URL
    context.configure(
        url=url,
        target_metadata=target_metadata,
        literal_binds=True,
        dialect_opts={"paramstyle": "named"},
    )
    with context.begin_transaction():
        patch_alembic_version(context)
        context.run_migrations()

def run_migrations_online():
    config.set_main_option("sqlalchemy.url", settings.REC_ANALYTICS_CLICKHOUSE_URL)

    connectable = engine_from_config(
        config.get_section(config.config_ini_section),
        prefix="sqlalchemy.",
        poolclass=pool.NullPool,
    )
    with connectable.connect() as connection:
        context.configure(
            connection=connection,
            target_metadata=target_metadata,
            include_object=include_object
        )
        with context.begin_transaction():
            patch_alembic_version(context)
            context.run_migrations()

if context.is_offline_mode():
    run_migrations_offline()
else:
    run_migrations_online()