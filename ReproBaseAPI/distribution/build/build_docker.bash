#!/usr/bin/env bash
VERSION="1.0"


CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

cd docker_build

rm -rf source
rm -rf source_jpadatasource

mkdir source
cp -r ${CUR_DIR}/../../../ReproBaseAPI/* source/
mkdir source_jpadatasource
cp -r ${CUR_DIR}/../../../JPADatasource/* source_jpadatasource/


docker build -t reprobaseline_build:01.00.00 .

docker run --rm -v ${CUR_DIR}/docker_build/source:/source -v ${CUR_DIR}/docker_build/source_jpadatasource:/source_datasource reprobaseline_build:01.00.00


cd ${CUR_DIR}

cd docker_binary

rm -r tmp_dir

mkdir tmp_dir



cp -r ReproBaseline-template tmp_dir/ReproBaseline-${VERSION}

mkdir tmp_dir/ReproBaseline-${VERSION}/lib

cp ../docker_build/source/rba-service/target/rba-service-*.jar tmp_dir/ReproBaseline-${VERSION}/lib/

sed -i 's+<version>+'${VERSION}'+'  tmp_dir/ReproBaseline-${VERSION}/launch.bash

cp Dockerfile tmp_dir/
sed -i 's+<version>+'${VERSION}'+g'  tmp_dir/Dockerfile

cd tmp_dir 
tar cvzf  ReproBaseline-${VERSION}.tar.gz  ReproBaseline-${VERSION}

docker build -t reprobaseline:${VERSION} .

cd ${CUR_DIR}
