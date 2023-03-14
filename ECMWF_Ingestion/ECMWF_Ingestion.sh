#!/bin/bash

CUR_DIR="$(
  cd "$(dirname "$0")"
  pwd -P
)"

if [ $# -lt 1 ]; then
  echo "ECMWF_Ingestion.sh tmp"
  exit 1
fi

if [ -z ${TIME_PERIOD+x} ]; then
  echo "TIME_PERIOD not set"
  exit 1
fi
echo "TIME_PERIOD : "$TIME_PERIOD
WORK_FOLDER=$1
echo "WORK_FOLDER : "$WORK_FOLDER
if [ -z ${ECMWF_USER+x} ]; then
  echo "ECMWF_USER not set"
  exit 1
fi
echo "ECMWF_USER : "$ECMWF_USER
if [ -z ${ECMWF_PASS+x} ]; then
  echo "ECMWF_PASS not set"
  exit 1
fi
echo "ECMWF_PASS : "$ECMWF_PASS
if [ -z ${ECMWF_URL+x} ]; then
  echo "ECMWF_URL not set"
  exit 1
fi
echo "ECMWF_URL : "$ECMWF_URL
if [ -z ${AUXIP_USER+x} ]; then
  echo "AUXIP_USER not set"
  exit 1
fi
echo "AUXIP_USER : "$AUXIP_USER
if [ -z ${AUXIP_PASS+x} ]; then
  echo "AUXIP_PASS not set"
  exit 1
fi
echo "AUXIP_PASS : "$AUXIP_PASS
if [ -z ${MODE+x} ]; then
  echo "MODE not set"
  exit 1
fi
echo "MODE : "$MODE
if [ -z ${S3_ACCESS_KEY+x} ]; then
  echo "S3_ACCESS_KEY not set, no S3"
  MCPATH="mc"
else
  if [ -z ${MCPATH+x} ]; then
    echo "MCPATH not set"
    exit 1
  fi
  echo "MCPATH : "$MCPATH
  echo "S3_ACCESS_KEY: "${S3_ACCESS_KEY}
  if [ -z ${S3_SECRET_KEY+x} ]; then
    echo "S3_SECRET_KEY not set"
    exit 1
  fi
  echo "S3_SECRET_KEY: "${S3_SECRET_KEY}
  if [ -z ${S3_ENDPOINT+x} ]; then
    echo "S3_ENDPOINT not set"
    exit 1
  fi
  echo "S3_ENDPOINT: "${S3_ENDPOINT}
  if [ -z ${S3_BUCKET+x} ]; then
    echo "S3_BUCKET not set"
    exit 1
  fi
  echo "S3_BUCKET: "${S3_BUCKET}
  ${MCPATH} alias set "wasabi-auxip-archives" ${S3_ENDPOINT} ${S3_ACCESS_KEY} ${S3_SECRET_KEY} --api S3v4
fi

if [[ ! -d $WORK_FOLDER ]]; then
  mkdir -p $WORK_FOLDER
  if [[ ! -d $WORK_FOLDER ]]; then
    echo $WORK_FOLDER" folder can't be created and doesnt exists"
    exit 1
  fi
fi

STOP_DATE=$(date '+%Y-%m-%d' -d "5 day ago")
START_DATE=$(date '+%Y-%m-%d' -d "$((TIME_PERIOD + 5)) day ago")
echo "START_DATE: "$START_DATE
echo "STOP_DATE: "$STOP_DATE
TEMP_FOLDER=$(mktemp -p $WORK_FOLDER -d)
TEMP_FOLDER_AUX=$(mktemp -p $WORK_FOLDER -d)
TEMP_FOLDER_LISTING=$(mktemp -p $WORK_FOLDER -d)
TEMP_FOLDER_JSONS=$(mktemp -p $WORK_FOLDER -d)
echo "Temporary folder : "$TEMP_FOLDER
echo "Starting ECMWF download"
python3 -u ${CUR_DIR}/ECMWF_Ingestion.py -k ${ECMWF_PASS} -w ${TEMP_FOLDER} -s $START_DATE -e $STOP_DATE -u ${ECMWF_URL} -m ${ECMWF_USER} -o ${TEMP_FOLDER_AUX}
code=$?
if [ $code -ne 0 ]; then
  echo "ECMWF Retrieve failed"
else
  echo "ECMWF download done"
  echo "Starting AUXIP ingestion"
  python3 -u ${CUR_DIR}/ingestion/ingestion.py -i ${TEMP_FOLDER_AUX} -u ${AUXIP_USER} -pw ${AUXIP_PASS} -mc ${MCPATH} -b "wasabi-auxip-archives/"${S3_BUCKET} -o ${TEMP_FOLDER_LISTING}/file_list_S2.txt -m ${MODE}
  code=$?
  if [ $code -ne 0 ]; then
    echo "AUXIP ingestion failed"
  else
    echo "AUXIP ingestion done"
    echo "Starting Reprobase jsons generation"
    python3 -u ${CUR_DIR}/ingest_s2files.py -i ${TEMP_FOLDER_LISTING}/file_list_S2.txt -f ${CUR_DIR}/file_types -t ${CUR_DIR}/template.json -o ${TEMP_FOLDER_JSONS}/
    code=$?
    if [ $code -ne 0 ]; then
      echo "Reprobase jsons generation failed"
    else
      echo "Reprobase json generation done"
      master_code=0
      for f in $(find ${TEMP_FOLDER_JSONS} -name '*.json'); do
        echo "Pushing "$f" to reprobase"
        python3 ${CUR_DIR}/update_base.py -i $f -u ${AUXIP_USER} -pw ${AUXIP_PASS} -m ${MODE}
        code=$?
        if [ $code -ne 0 ]; then
          echo "Reprobase ingestion failed for file "$f
          master_code=$code
        fi
      done
      if [ $master_code -ne 0 ]; then
            echo "Reprobase ingestion failed"
      else
        echo "Removing temporary folders"
        rm -r ${TEMP_FOLDER}
        rm -r ${TEMP_FOLDER_AUX}
      echo "Done"
      fi
    fi
  fi
fi
