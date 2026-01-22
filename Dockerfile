FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt /app/
RUN pip install --no-cache-dir -r requirements.txt

COPY recposts.proto /app/
RUN python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. recposts.proto

COPY . /app/

COPY supervisord.conf /etc/supervisord.conf

ENV PYTHONUNBUFFERED=1

EXPOSE 8000
CMD ["supervisord", "-c", "/etc/supervisord.conf"]
