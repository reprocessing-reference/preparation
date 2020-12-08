#!/usr/bin/env bash


# Launch script for the service

CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"
export JAVA_HOME=$( dirname $(dirname $(readlink -f $(which javac))))

cd /source_datasource
mvn clean; mvn compile; mvn install

cd /source
ls
mvn clean; mvn compile; mvn package

