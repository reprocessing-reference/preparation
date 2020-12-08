#!/usr/bin/env bash
VERSION="1.0-SNAPSHOT"


CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

cd docker_build

rm -rf source
rm -rf source_jpadatasource

mkdir source
cp -r ${CUR_DIR}/../../../AUXIP/* source/
mkdir source_jpadatasource
cp -r ${CUR_DIR}/../../../JPADatasource/* source_jpadatasource/


docker build -t auxip_build:01.00.00 .

docker run --rm -v ${CUR_DIR}/docker_build/source:/source -v ${CUR_DIR}/docker_build/source_jpadatasource:/source_datasource auxip_build:01.00.00


cd ${CUR_DIR}

cd docker_binary

rm -r tmp_dir

mkdir tmp_dir



cp -r Auxip-template tmp_dir/Auxip-${VERSION}

cp ../docker_build/source/auxip-service/target/auxip-service-*.jar tmp_dir/Auxip-${VERSION}/lib/

sed -i 's+<version>+'${VERSION}'+'  tmp_dir/Auxip-${VERSION}/launch.bash

cp Dockerfile tmp_dir/
sed -i 's+<version>+'${VERSION}'+g'  tmp_dir/Dockerfile

cd tmp_dir 
tar cvzf  Auxip-${VERSION}.tar.gz  Auxip-${VERSION}

docker build -t auxip:${VERSION} .

cd ${CUR_DIR}
