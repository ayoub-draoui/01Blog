#!/bin/bash

docker compose up -d

# 2️⃣ Wait for Postgres
sleep 5

pkill gnome-terminal

gnome-terminal -- bash -c "
 
cd  backend || exit 1
./mvnw spring-boot:run
exec bash
"

gnome-terminal -- bash -c "
cd frontend || exit
npm install
npx ng serve
exec bash
"
