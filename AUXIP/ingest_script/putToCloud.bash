#!/bin/bash

for f in $(find $1 -type f); do echo $f; uuid=$(uuidgen); echo " : "$uuid;mkdir /DATA2/auxip_s3/$uuid;cp $f /DATA2/auxip_s3/$uuid/; echo $(basename $f)" : "$uuid >> s2_uuid_listing.txt;done
