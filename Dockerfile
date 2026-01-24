FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt /app/
RUN pip install --no-cache-dir -r requirements.txt

COPY app/grpc/proto /app/app/grpc/proto/
RUN python -m grpc_tools.protoc \
    -I/app/app/grpc/proto \
    --python_out=/app/app/grpc/proto \
    --grpc_python_out=/app/app/grpc/proto \
    /app/app/grpc/proto/recposts.proto

COPY . /app/

ENV PYTHONUNBUFFERED=1
ENV PYTHONPATH=/app

EXPOSE 8000 50051

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]