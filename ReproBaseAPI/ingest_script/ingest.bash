

python pythonscripts/ingest_s2files.py -i ~/sentinel2/ -b json/bands/ -f json/file_types/ -t ../rba-service/src/test/resources/auxfile.json -o json/aux_files/


#bands
for f in json/bands/S2_B*; do  curl -X POST -H "Content-Type: application/json" -d @$f http://localhost:8080/reprocessing.svc/Bands; done

#ProductLevels
for f in json/product_levels/L*; do curl -X POST -H "Content-Type: application/json" -d @$f http://localhost:8080/reprocessing.svc/ProductLevels; done



#Baselines
for f in json/baselines/*; do curl -X POST -H "Content-Type: application/json" -d @$f http://localhost:8080/reprocessing.svc/Baselines; done

#FileType
for f in json/file_types/*; do curl -X POST -H "Content-Type: application/json" -d @$f http://localhost:8080/reprocessing.svc/AuxFileTypes; done

#AuxFiles
for f in json/aux_files/*; do curl -X POST -H "Content-Type: application/json" -d @$f http://localhost:8080/reprocessing.svc/AuxFiles; done

#Sensors
for f in json/sensors/SENTINEL*; do curl -X POST -H "Content-Type: application/json" -d @$f http://localhost:8080/reprocessing.svc/Sensors; done
