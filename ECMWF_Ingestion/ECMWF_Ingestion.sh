#!/bin/bash

CUR_DIR="$(
  cd "$(dirname "$0")"
  pwd -P
)"

if [ $# -lt 9 ]; then
  echo "ECMWF_Ingestion.sh timetosleep tmp ecmwf_user ecmwf_pass ecmwf_url auxip_user auxip_pass mode[dev/prod] mcpath"
  exit 1
fi

TIME_TO_SLEEP=$1
WORK_FOLDER=$2
ECMWF_USER=$3
ECMWF_PASS=$4
ECMWF_URL=$5
AUXIP_USER=$6
AUXIP_PASS=$7
MODE=$8
MCPATH=$9
echo "Mode : "$MODE
echo "S3_ACCESS_KEY: "${S3_ACCESS_KEY}
echo "S3_SECRET_KEY: "${S3_SECRET_KEY}
echo "S3_ENDPOINT: "${S3_ENDPOINT}
S3_BUCKET="auxip"
echo "S3_BUCKET: "${S3_BUCKET}

${MCPATH} alias set "wasabi-auxip-archives" ${S3_ENDPOINT} ${S3_ACCESS_KEY} ${S3_SECRET_KEY} --api S3v4

if [[ ! -d $WORK_FOLDER ]]; then
  mkdir -p $WORK_FOLDER
  if [[ ! -d $WORK_FOLDER ]]; then
    echo $WORK_FOLDER" folder can't be created and doesnt exists"
    exit 1
  fi
fi

while true; do
  STOP_DATE=$(date '+%Y-%m-%d' -d "5 day ago")
  START_DATE=$(date '+%Y-%m-%d' -d "$((TIME_TO_SLEEP+5)) day ago")
  echo "START_DATE: "$START_DATE
  echo "STOP_DATE: "$STOP_DATE
  TEMP_FOLDER=$(mktemp -p $WORK_FOLDER -d)
  TEMP_FOLDER_AUX=$(mktemp -p $WORK_FOLDER -d)
  TEMP_FOLDER_LISTING=$(mktemp -p $WORK_FOLDER -d)
  TEMP_FOLDER_JSONS=$(mktemp -p $WORK_FOLDER -d)
  echo "Temporary folder : "$TEMP_FOLDER
  echo "Starting ECMWF download"
  python3 ECMWF_Ingestion.py -k ${ECMWF_PASS} -w ${TEMP_FOLDER} -s $START_DATE -e $STOP_DATE -u ${ECMWF_URL} -m ${ECMWF_USER} -o ${TEMP_FOLDER_AUX}
  code=$?
  if [ $code -ne 0 ]; then
    echo "ECMWF Retrieve failed"
    rm -r ${TEMP_FOLDER_LISTING}
    rm -r ${TEMP_FOLDER_JSONS}
  else
    echo "ECMWF download done"
    echo "Starting AUXIP ingestion"
    python3 ingestion/ingestion.py -i ${TEMP_FOLDER_AUX} -u ${AUXIP_USER} -pw ${AUXIP_PASS} -mc ${MCPATH} -b "wasabi-auxip-archives/"${S3_BUCKET} -o ${TEMP_FOLDER_LISTING}/file_list_S2.txt -m ${MODE}
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
        for f in $(find ${TEMP_FOLDER_JSONS} -name '*.json'); do
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
