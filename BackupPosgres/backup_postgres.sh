#!/bin/bash

CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"
CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

if [ $# -lt 1 ]; then
    echo "backup_postgres.sh REP_BACKUP"
    exit 1
fi

echo "RCLONE_CONFIG_WASABI_SECRET_ACCESS_KEY: "${RCLONE_CONFIG_WASABI_SECRET_ACCESS_KEY}
echo "RCLONE_CONFIG_WASABI_ACCESS_KEY_ID: "${RCLONE_CONFIG_WASABI_ACCESS_KEY_ID}

BACKUP_FOLDER=$1

#verify stuffs
if [[ ! -d ${BACKUP_FOLDER} ]];then
    echo ${BACKUP_FOLDER}" folder doesn't exists"
    exit 1
fi

AUXIP_BACKUP=${BACKUP_FOLDER}/AUXIP
REPROBASE_BACKUP=${BACKUP_FOLDER}/REPROBASE

mkdir -p ${AUXIP_BACKUP}
mkdir -p ${REPROBASE_BACKUP}

cd ${AUXIP_BACKUP}
export PGPASSWORD='**auxip**';pg_dumpall -c -U auxip -d 'postgresql://database_auxip_olingo/auxip' | gzip > dump_auxip_`date +%d-%m-%Y"_"%H_%M_%S`.gz

if [ ! -z "${RCLONE_CONFIG_WASABI_SECRET_ACCESS_KEY}" ]; then
    #tar the whole stuff
    rclone copy dump_auxip*gz wasabi:backupposgres/
fi


cd ${REPROBASE_BACKUP}
export PGPASSWORD='**reprobaseline**';pg_dumpall -c -U reprobaseline -d 'postgresql://database/reprobaseline' | gzip > dump_reprobaseline_`date +%d-%m-%Y"_"%H_%M_%S`.gz | gzip > dump_reprobaseline_`date +%d-%m-%Y"_"%H_%M_%S`.gz

if [ ! -z "${RCLONE_CONFIG_WASABI_SECRET_ACCESS_KEY}" ]; then
    rclone copy dump_repobaseline*gz wasabi:backupposgres/
fi


cd ${CUR_DIR}
