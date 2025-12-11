#!/bin/bash

# ===============================
# 01Blog Backend Starter Script
# ===============================

# 1️⃣ Stop on any error
set -e

# 2️⃣ Make sure Docker container is running
echo "Checking if PostgreSQL container is running..."
if ! docker ps --format '{{.Names}}' | grep -q '^postgres$'; then
    echo "Postgres container not running. Starting container..."
    docker start postgres
else
    echo "Postgres container is already running."
fi

# 3️⃣ Wait a few seconds for Postgres to be ready
echo "Waiting for Postgres to initialize..."
sleep 5

# 4️⃣ Build the project
echo "Building Spring Boot project..."
./mvnw clean package -DskipTests

# 5️⃣ Run the Spring Boot application
echo "Starting Spring Boot application..."
./mvnw spring-boot:run
