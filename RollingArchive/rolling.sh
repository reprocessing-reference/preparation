#!/bin/bash


#


CUR_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

if [ $# -lt 3 ]; then
    echo "rolling.sh REP_ARCHIVE REP_WORK DAYS"
    exit 1
fi

ARCHIVE=$1
REP_WORK=$2
DAYS=$3

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

SNAP_FOLDER=$REP_WORK/snap_$(date '+%Y%m%d')
mkdir $SNAP_FOLDER

#find files older than days
FILES=$(find $ARCHIVE -type f -mtime +$DAYS)

for f in $FILES;
do
    echo 'File '$f' is older than '$DAYS' days'
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
    rclone copy archive*tgz wasabi:auxiparchives/
    mv archive*tgz $REP_WORK/
fi
#remove snap folder
cd $CUR_DIR 
rm -r $SNAP_FOLDER

echo "Done"

