
#bands
for f in json/bands/S2_B*; do  curl -X POST -H "Content-Type: application/json" -d @$f http://localhost:8080/reprocessing.svc/Bands; done

#ProductLevels
for f in json/product_levels/L*; do curl -X POST -H "Content-Type: application/json" -d @$f http://localhost:8080/reprocessing.svc/ProductLevels; done

#Sensors
for f in json/sensors/SENTINEL*; do curl -X POST -H "Content-Type: application/json" -d @$f http://localhost:8080/reprocessing.svc/Sensors; done

#Baselines
for f in json/baselines/*; do curl -X POST -H "Content-Type: application/json" -d @$f http://localhost:8080/reprocessing.svc/Baselines; done

#FileType
for f in json/file_types/*; do curl -X POST -H "Content-Type: application/json" -d @$f http://localhost:8080/reprocessing.svc/AuxFileTypes; done
