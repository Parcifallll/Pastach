#!/bin/bash

# run infrastructure db, kafka, zookeeper (without app)
# docker-compose up

mvn package -DskipTests # rebuild JAR
docker-compose up --build app # rebuild && run only app-cont