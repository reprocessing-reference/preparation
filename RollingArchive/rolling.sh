#!/bin/bash


#


CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

if [ $# -lt 3 ]; then
    echo "rolling.sh REP_ARCHIVE REP_WORK DAYS"
    exit 1
fi

echo "RCLONE_CONFIG_WASABI_SECRET_ACCESS_KEY: "${RCLONE_CONFIG_WASABI_SECRET_ACCESS_KEY}
echo "RCLONE_CONFIG_WASABI_ACCESS_KEY_ID: "${RCLONE_CONFIG_WASABI_ACCESS_KEY_ID}
echo "RCLONE_CONFIG_WASABI_BUCKET: "${RCLONE_CONFIG_WASABI_BUCKET}

ARCHIVE=$1
REP_WORK=$2
DAYS=$3
BUCKET=${RCLONE_CONFIG_WASABI_BUCKET}

#verify stuffs
if [[ ! -d $ARCHIVE ]];then
    echo $ARCHIVE" folder doesn't exists"
    exit 1
fi
if [[ ! -d $REP_WORK ]];then
    mkdir -p $REP_WORK
    if [[ ! -d $REP_WORK ]];then
	echo $REP_WORK" folder can't be created and doesnt exists"
	exit 1
    fi
fi

ERROR_FOLDER=$REP_WORK/errors
mkdir -p ${ERROR_FOLDER}
ERROR_BUCKET_FOLDER=$REP_WORK/errors_bucket
mkdir -p ${ERROR_BUCKET_FOLDER}


SNAP_FOLDER=$REP_WORK/snap_$(date '+%Y%m%d')
mkdir $SNAP_FOLDER

#find files older than days
FILES=$(find $ARCHIVE -type f -mtime +$DAYS -name "*json")

for f in $FILES;
do
    #echo 'File '$f' is older than '$DAYS' days'
    NEW_FOLDER=$SNAP_FOLDER'/'$(dirname $f | sed 's+'$ARCHIVE'+''+')
    echo $NEW_FOLDER
    mkdir -p $NEW_FOLDER
    mv $f $NEW_FOLDER/    
done

if [ -z "$(ls -A $SNAP_FOLDER)" ]; then
   echo "No file in archive, exit"
else
    #tar the whole stuff
    cd $SNAP_FOLDER
    tar cvzf archive_$(date '+%Y%m%d%H%M%S').tgz *
    if [ ! -z "${RCLONE_CONFIG_WASABI_SECRET_ACCESS_KEY}" ]; then
	if [ -z "${BUCKET}" ]; then
	    echo "No RCLONE_CONFIG_WASABI_BUCKET found in env"
	    mv archive*tgz ${ERROR_BUCKET_FOLDER}
	    exit 1
	fi
	rclone copy archive*tgz wasabi:${BUCKET}/
	code=$?
	if [ $code -ne 0]; then
	    echo "RCLONE failed to transfer"
	    mv archive*tgz ${ERROR_BUCKET_FOLDER}
	    exit 1
	else
	    rm archive*tgz
	fi
    else
	rm ${ERROR_FOLDER}/archive*tgz
	mv archive*tgz ${ERROR_FOLDER}
    fi
fi
#remove snap folder
cd $CUR_DIR 
rm -r $SNAP_FOLDER

echo "Done"


