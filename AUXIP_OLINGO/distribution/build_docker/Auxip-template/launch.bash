#!/usr/bin/env bash


# Launch script for the service

CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

java -jar $CUR_DIR/lib/auxip-<version>.jar --spring.config.location=file:$CUR_DIR/conf/ -Ddatasource.password=${DATASOURCE_PASSWORD} -Ds3.access_key=${S3_ACCESS_KEY} -Ds3.secret_access_key=${S3_SECRET_KEY} $*
