#!/usr/bin/env bash

#Run images for db (mysql) + adminer
docker-compose -f ./docker-compose.yml up -d
#Start local server
./gradlew bootRun