url="http://reprocessing-preparation.ml:8080/reprocessing.svc"
pass="VC&&v*9rS4cFVSMW"

rm -r json/file_types
rm -r json/*files
rm -r json/*files_updated
mkdir json/file_types
mkdir json/s1_files
mkdir json/s2_files
mkdir json/s3_files
mkdir json/s1_files_updated
mkdir json/s2_files_updated
mkdir json/s3_files_updated

python pythonscripts/ingest_filetypes.py -i RRPP_all_AUX.csv -t ../rba-service/src/test/resources/auxfiletype.json -o json/file_types/

python pythonscripts/ingest_s2files.py -i ~/sentinel2/ -f json/file_types/ -t ../rba-service/src/test/resources/auxfile.json -o json/s2_files/

python pythonscripts/update_s2file_validity.py -i json/s2_files/ -o json/s2_files_updated

python pythonscripts/ingest_s3files.py -i listS3.txt -t ../rba-service/src/test/resources/auxfile.json -o json/s3_files/ -f json/file_types/

python pythonscripts/update_s2file_validity.py -i json/s3_files/ -o json/s3_files_updated

python pythonscripts/ingest_s1files.py -i listS1.txt -t ../rba-service/src/test/resources/auxfile.json -o json/s1_files/ -f json/file_types/

python pythonscripts/update_s2file_validity.py -i json/s1_files/ -o json/s1_files_updated

exit 0

#ProductLevels
for f in json/product_levels/L*.json; do curl -X POST -u admin:$pass -H "Content-Type: application/json" -d @$f $url/ProductLevels; done
#ProductTypes
for f in json/product_type/L*.json; do curl -X POST -u admin:$pass -H "Content-Type: application/json" -d @$f $url/ProductTypes;done

#FileType
for f in json/file_types/*.json; do curl -X POST -u admin:$pass -H "Content-Type: application/json" -d @'$f' $url/AuxTypes; done
#AuxFiles
for f in json/aux_files/*.json; do curl -X POST -u admin:$pass -H "Content-Type: application/json" -d @$f $url/AuxFiles; done

