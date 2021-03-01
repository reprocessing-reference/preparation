#!/usr/bin/env bash


# Launch script for the service

CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"
export JAVA_HOME=$( dirname $(dirname $(readlink -f $(which javac))))


echo $CUR_DIR

# remove the old package
rm  auxip-*.jar

# clean all
rm -rf build_dir
mkdir build_dir

# copy code source to be compiled
cp -rf ../../src build_dir
cp -rf ../../pom.xml build_dir

# add the the target configuration 
rm build_dir/src/main/resources/application.properties
cp ../build_package/application.properties build_dir/src/main/resources

# remove test folder
rm -rf build_dir/src/test

# Build a package
cd build_dir
mvn clean; mvn compile; mvn package 




