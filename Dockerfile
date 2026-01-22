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

COPY supervisord.conf /etc/supervisord.conf

ENV PYTHONUNBUFFERED=1

EXPOSE 8000 50051

CMD ["supervisord", "-c", "/etc/supervisord.conf"]