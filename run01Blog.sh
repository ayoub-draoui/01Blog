#!/bin/bash

 
 

# 1️⃣ Check PostgreSQL container
echo "Checking if PostgreSQL container is running..."
if ! docker ps --format '{{.Names}}' | grep -q '^postgres$'; then
    echo "Postgres container not running. Starting container..."
    docker start postgres
else
    echo "Postgres container is already running."
fi

# 2️⃣ Wait for Postgres
sleep 5

gnome-terminal -- bash -c "
 
cd  backend || exit 1
./mvnw spring-boot:run
exec bash
"

# 5️⃣ Start FRONTEND in NEW terminal
gnome-terminal -- bash -c "
cd frontend || exit
npm install
npx ng serve
exec bash
"
