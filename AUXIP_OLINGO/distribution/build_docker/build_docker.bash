#!/usr/bin/env bash
VERSION="1.0.1"

CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

# clear all packages
rm auxip*.jar

# generate a new package
echo "generate a new auxip package "
./../build_package/build_package.bash 

echo "copy the package "
cp build_dir/target/auxip*.jar .

echo "build the docker image"
docker build -t auxip_olingo:${VERSION} .

echo " clean all"

rm -rf build_dir
rm auxip-*.jar 

