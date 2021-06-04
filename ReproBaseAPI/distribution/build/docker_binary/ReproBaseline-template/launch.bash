#!/usr/bin/env bash


# Launch script for the service

CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

java -jar $CUR_DIR/lib/rba-service-<version>.jar --spring.config.location=file:$CUR_DIR/conf/ -Ddatasource.password=${DATASOURCE_PASSWORD} $*
