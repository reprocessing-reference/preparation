#!/usr/bin/env bash


# Launch script for the service

CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

java -jar $CUR_DIR/lib/rba-service-1.0-SNAPSHOT.jar $*
