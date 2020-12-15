url="http://reprocessing-preparation.ml:8080/reprocessing.svc"
pass="VC&&v*9rS4cFVSMW"

python pythonscripts/ingest_filetypes.py -i RRPP_all_AUX.csv -t ../rba-service/src/test/resources/auxfiletype.json -o json/file_types/

python pythonscripts/ingest_s2files.py -i ~/sentinel2/ -f json/file_types/ -t ../rba-service/src/test/resources/auxfile.json -o json/aux_files/

python pythonscripts/update_s2file_validity.py -i json/aux_files/ -o json/aux_files_updated

#ProductLevels
for f in json/product_levels/L*.json; do curl -X POST -u admin:$pass -H "Content-Type: application/json" -d @$f $url/ProductLevels; done
#ProductTypes
for f in json/product_type/L*.json; do curl -X POST -u admin:$pass -H "Content-Type: application/json" -d @$f $url/ProductTypes;done

#FileType
for f in json/file_types/*.json; do curl -X POST -u admin:$pass -H "Content-Type: application/json" -d @'$f' $url/AuxTypes; done
#AuxFiles
for f in json/aux_files/*.json; do curl -X POST -u admin:$pass -H "Content-Type: application/json" -d @$f $url/AuxFiles; done

