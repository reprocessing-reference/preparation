#!/bin/bash

CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

if [ $# -lt 7 ]; then
    echo "PRIP_Ingestion.sh timetosleep tmp prip_user prip_pass auxip_user auxip_pass mode[dev/prod]"
    exit 1
fi

TIME_TO_SLEEP=$1
WORK_FOLDER=$2
PRIP_USER=$3
PRIP_PASS=$4
AUXIP_USER=$5
AUXIP_PASS=$6
MODE=$7




echo "S3_ACCESS_KEY: "${S3_ACCESS_KEY}
echo "S3_SECRET_KEY: "${S3_SECRET_KEY}
S3_BUCKET="wasabi-auxip-archives/auxip"
echo "S3_BUCKET: "${S3_BUCKET}


if [[ ! -d $WORK_FOLDER ]];then
    mkdir -p $WORK_FOLDER
    if [[ ! -d $WORK_FOLDER ]];then
	echo $WORK_FOLDER" folder can't be created and doesnt exists"
	exit 1
    fi
fi


while true
do
    TEMP_FOLDER=$(mktemp -p $WORK_FOLDER -d)
    TEMP_FOLDER_LISTING=$(mktemp -p $WORK_FOLDER -d)
    TEMP_FOLDER_JSONS=$(mktemp -p $WORK_FOLDER -d)
    echo "Temporary folder : "$TEMP_FOLDER
    echo "Starting PRIP download" 
    python3 PRIP_Ingestion.py -u ${PRIP_USER} -pw ${PRIP_PASS} -w ${TEMP_FOLDER}
    code=$?
    if [ $code -ne 0 ]; then
	echo "PRIP Retrieve failed"
	rm -r ${TEMP_FOLDER_LISTING}
	rm -r ${TEMP_FOLDER_JSONS}
    else
	echo "PRIP download done"
	echo "Starting AUXIP ingestion"
	python3 ingestion/ingestion.py -i ${TEMP_FOLDER} -u ${AUXIP_USER} -pw ${AUXIP_PASS} -mc /home/ubuntu/mc -b ${S3_BUCKET} -o ${TEMP_FOLDER_LISTING}/file_list_S2.txt -m ${MODE}
	code=$?
	if [ $code -ne 0 ]; then
	    echo "AUXIP ingestion failed"
	    rm -r ${TEMP_FOLDER_LISTING}
	    rm -r ${TEMP_FOLDER_JSONS}
	else
	    echo "AUXIP ingestion done"
	    echo "Starting Reprobase jsons generation"
	    python3 ingest_s2files.py -i ${TEMP_FOLDER_LISTING}/file_list_S2.txt -f ${CUR_DIR}/file_types -t ${CUR_DIR}/template.json -o ${TEMP_FOLDER_JSONS}/
	    code=$?
	    if [ $code -ne 0 ]; then
		echo "Reprobase jsons generation failes"
	    else
		echo "Reprobase json generation done"
		master_code=0
		for f in $(find ${TEMP_FOLDER_JSONS} -name '*.json');
		do
		    echo "Pushing "$f" to reprobase"
		    python3 update_base.py -i $f -u ${AUXIP_USER} -pw ${AUXIP_PASS} -m ${MODE}
		    code=$?
		    if [ $code -ne 0 ]; then
			echo "Reprobase ingestion failed for file "$f
			master_code=$code
		    fi
		done
		echo "Removing temporary folders"
		if [ $master_code -ne 0 ]; then
		    echo "Reprobase ingestion failed"
		else
		    rm -r ${TEMP_FOLDER}
		    rm -r ${TEMP_FOLDER_LISTING}
		    rm -r ${TEMP_FOLDER_JSONS}
		    echo "Done"
		fi
	    fi
	fi
    fi
    sleep ${TIME_TO_SLEEP}d
done

