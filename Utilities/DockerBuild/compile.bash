#!/usr/bin/env bash


# Launch script for the service

CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"
export JAVA_HOME=$( dirname $(dirname $(readlink -f $(which javac))))

depends=$(ls / | grep 'source_')
echo "Dependencies: "$depends

for f in $depends;
do
    cd /$f
    mvn clean; mvn compile -DskipTests; mvn install -DskipTests
done

cd /source
mvn clean; mvn compile -DskipTests; mvn install -DskipTests
