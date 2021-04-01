#!/usr/bin/env bash
VERSION="0.0.2"


CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

cd build_package

rm -rf source

mkdir source
cp -r ${CUR_DIR}/../../AUXIP_OLINGO/* source/

docker build -t auxip_olingo_build:01.00.00 .

docker run --rm -v ${CUR_DIR}/build_package/source:/source auxip_olingo_build:01.00.00


cd ${CUR_DIR}

cd build_docker

rm -r tmp_dir

mkdir tmp_dir



cp -r Auxip-template tmp_dir/Auxip-${VERSION}

mkdir  tmp_dir/Auxip-${VERSION}/lib

cp ../build_package/source/target/auxip-*.jar tmp_dir/Auxip-${VERSION}/lib/

sed -i 's+<version>+'${VERSION}'+'  tmp_dir/Auxip-${VERSION}/launch.bash

cp Dockerfile tmp_dir/
sed -i 's+<version>+'${VERSION}'+g'  tmp_dir/Dockerfile

cd tmp_dir 
tar cvzf  Auxip-${VERSION}.tar.gz  Auxip-${VERSION}

docker build -t auxip_olingo:${VERSION} .

cd ${CUR_DIR}
