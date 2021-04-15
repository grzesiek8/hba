#!/usr/bin/env bash

#Run images for db (mysql) + adminer
docker-compose -f ./docker-compose.yml up -d
#Waiting for db - workaround for server starting too fast (before sql scripts are loaded to db)
DUMP_DATA_DIR="./database/dump-data"
if [ -z "$(ls -A $DUMP_DATA_DIR)" ]; then
   	echo "Need to load initial data, wait for it..."
	  sleep 30
fi
#Start local server
./gradlew bootRun