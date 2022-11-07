#!/bin/bash

CUR_DIR="$(
  cd "$(dirname "$0")"
  pwd -P
)"

if [ $# -lt 1 ]; then
  echo "PRIP_Ingestion.sh tmp"
  exit 1
fi

WORK_FOLDER=$1
echo "WORK_FOLDER : "$WORK_FOLDER
if [ -z ${PRIP_USER+x} ]; then
  echo "PRIP_USER not set"
  exit 1
fi
echo "PRIP_USER: "${PRIP_USER}
if [ -z ${PRIP_PASS+x} ]; then
  echo "PRIP_PASS not set"
  exit 1
fi
echo "PRIP_PASS: "${PRIP_PASS}
if [ -z ${AUXIP_USER+x} ]; then
  echo "AUXIP_USER not set"
  exit 1
fi
echo "AUXIP_USER: "${AUXIP_USER}
if [ -z ${AUXIP_PASS+x} ]; then
  echo "AUXIP_PASS not set"
  exit 1
fi
echo "AUXIP_PASS: "${AUXIP_PASS}
if [ -z ${MODE+x} ]; then
  echo "MODE not set"
  exit 1
fi
echo "MODE: "${MODE}

if [ $MODE != "prod" ]; then
  echo "Due to IP restriction the PRIP ingestion can't be launched in dev mode"
  exit 1
fi
#S3 stuff
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

START_DATE=$(date '+%Y-%m-%d')
ERROR_FILE_LOG="${WORK_FOLDER}/${START_DATE}_PRIP_error.log"
TEMP_FOLDER=$(mktemp -p $WORK_FOLDER -d)
echo "TEMP_FOLDER : ${TEMP_FOLDER}" >> $ERROR_FILE_LOG
TEMP_FOLDER_LISTING=$(mktemp -p $WORK_FOLDER -d)
echo "TEMP_FOLDER_LISTING : ${TEMP_FOLDER_LISTING}" >> $ERROR_FILE_LOG
TEMP_FOLDER_JSONS=$(mktemp -p $WORK_FOLDER -d)
echo "TEMP_FOLDER_JSONS : ${TEMP_FOLDER_JSONS}" >> $ERROR_FILE_LOG

echo "Temporary folder : "$TEMP_FOLDER
echo "Starting PRIP download"
python3 ${CUR_DIR}/PRIP_Ingestion.py -u ${PRIP_USER} -pw ${PRIP_PASS} -w ${TEMP_FOLDER} -au ${AUXIP_USER} -apw ${AUXIP_PASS}
code=$?
if [ $code -ne 0 ]; then
  echo "PRIP Retrieve failed"
  echo "PRIP Retrieve failed" >> ${ERROR_FILE_LOG}
else
  echo "PRIP download done"
  master_code_auxip=0
  
  echo "Starting AUXIP ingestion S2"
  python3 -u ${CUR_DIR}/ingestion/ingestion.py -i ${TEMP_FOLDER}/S2 -u ${AUXIP_USER} -pw ${AUXIP_PASS} -mc ${MCPATH} -b "wasabi-auxip-archives/"${S3_BUCKET} -o ${TEMP_FOLDER_LISTING}/file_list_S2.txt -m ${MODE}
  code=$?
  if [ $code -ne 0 ]; then
     echo "Auxip ingestion failed for S2"
     master_code_auxip=$code
  fi

  if [ $master_code_auxip -ne 0 ]; then
    echo "AUXIP ingestion failed"
    echo "AUXIP ingestion failed" >> ${ERROR_FILE_LOG}

  else
    echo "AUXIP ingestion done"
    master_code_reprobase=0
    echo "Starting Reprobase jsons generation for S2"
    python3 -u ${CUR_DIR}/ingest_s2files.py -i ${TEMP_FOLDER_LISTING}/file_list_S2.txt -f ${CUR_DIR}/file_types -t ${CUR_DIR}/template.json -o ${TEMP_FOLDER_JSONS}/
    code=$?
    if [ $code -ne 0 ]; then
     echo "Reprobase jsons generation failed for S2"
     master_code_reprobase=$code
    fi

    if [ $master_code_reprobase -ne 0 ]; then
      echo "Reprobase jsons generation failed"
      echo "Reprobase jsons generation failed" >> ${ERROR_FILE_LOG}
    else
      echo "Reprobase json generation done"
      master_code=0
      for f in $(find ${TEMP_FOLDER_JSONS} -name '*.json'); do
        echo "Pushing "$f" to reprobase"
        python3 ${CUR_DIR}/update_base.py -i $f -u ${AUXIP_USER} -pw ${AUXIP_PASS} -m ${MODE}
        code=$?
        if [ $code -ne 0 ]; then
          echo "Reprobase ingestion failed for file "$f
          echo "Reprobase ingestion failed for file "$f >> ${ERROR_FILE_LOG}
          master_code=$code
        fi
      done
      if [ $master_code -ne 0 ]; then
        echo "Reprobase ingestion failed"
        echo "Reprobase ingestion failed" >> ${ERROR_FILE_LOG}
      else
        echo "Removing temporary folders"
        rm -r ${TEMP_FOLDER}
        rm -r ${TEMP_FOLDER_LISTING}
        rm -r ${TEMP_FOLDER_JSONS}
        echo "Done"
      fi
    fi
  fi
fi

# Removing the error log if the number of lines equals 3 : one line per tmp directories created at the beginning
if [ "$( wc -l < ${ERROR_FILE_LOG} )" -eq 3 ]; then
  echo "No errors during ingestion : deleting error file"
  rm $ERROR_FILE_LOG
fi