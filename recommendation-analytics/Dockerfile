FROM python:3.11-slim as base

WORKDIR /app

RUN apt-get update && apt-get install -y \
    gcc \
    curl \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

FROM base as migrations
ENV PYTHONPATH=/app
CMD ["python", "-m", "app.database.apply_schema"]

FROM base as consumer
ENV PYTHONPATH=/app
CMD ["python", "-m", "app.main"]