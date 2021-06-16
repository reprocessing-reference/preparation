#!/bin/bash

CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"


BACKUP_FOLDER=/home/ubuntu/backup_postgres/
AUXIP_BACKUP=${BACKUP_FOLDER}/AUXIP
REPROBASE_BACKUP=${BACKUP_FOLDER}/REPROBASE

mkdir -p ${AUXIP_BACKUP}
mkdir -p ${REPROBASE_BACKUP}

cd ${AUXIP_BACKUP}
docker exec  -t kongkeycloak_database_auxip_olingo_1 pg_dumpall -c -U auxip | gzip > dump_auxip_`date +%d-%m-%Y"_"%H_%M_%S`.gz

cd ${REPROBASE_BACKUP}
docker exec  -t kongkeycloak_database_1 pg_dumpall -c -U reprobaseline | gzip > dump_reprobaseline_`date +%d-%m-%Y"_"%H_%M_%S`.gz

cd ${CUR_DIR}
