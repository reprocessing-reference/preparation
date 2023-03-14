====== INGEST A NEW AUXILIARY TYPE ======

===== INGEST TO THE TASK 1 =====
The goal is to send a PUT request on reprocessing.svc (Task1). This service is the one that holds the reference to every auxiliary types.

Create a new Postman request (or curl). Select the PUT request type.
Paste the following request : https://reprocessing-auxiliary.copernicus.eu/reprocessing.svc/AuxTypes('REPLACE_NEW_TYPE_NAME')

In the body section of the request, paste the details of the new type in JSON format.
An example of the details in JSON is available in the same directory as this README : AUX_ECMWFD.json.

Fill in the authentication of the postman request as detailed in the official AUXIP documentation.

Send the request.

Copy the JSON to the different file_types.tgz archives in the ECMWF_Ingestions and PRIP_Ingestions.


===== INGEST TO THE TASK 3 =====
We also have to update the rdb.svc (Task 3) and add the new type to the auxtype_deltas database table.

It should be possible to to proceed the same way as previously with a postman request, but we'll explore another possibility :
modify the database directly from PGAdmin.

Open the PGAdmin web page : https://reprocessing-auxiliary.copernicus.eu/pgadmin4/browser/#
Connect using the credentials contained in the environment file used to launch the services.

Prior to this step you have to have linked the task 3 database in the PGAdmin webpage.

On the left panel, unfold the "Servers" node and the "Reprocessing". Authenticate with the credentials related to the task 3 database.
Click on the "reprocessingdatabaseline" database, and open a new Query Tool (one of the icons in the Browser panel).

Paste the following content :

INSERT INTO public.auxtype_deltas(id, auxtype, creationdatetime, delta0, delta1, iscurrent, mission)
VALUES ('ID', 'NEW_TYPE_NAME', 'CURRENT_DATE', 'DELTA_0', 'DELTA_1', 'true', 'MISSION')

Replace the uppercase values above :
- Concerning the ID, you have to take the ID of the last type entered in the service, and add 1 to it.
To check the last ID used, unfold "reprocessingdatabaseline" > "Schemas" > "public" > "Tables".
Right click on "auxtype_deltas" > "View/Edit Data" > "All Rows" and scroll down to the last row.
Take the ID and add 1 and repalce it in the previous values.
- Concerning the CURRENT_DATE, use the format used in the table opened right above.

Execute the query.


===== CODE MODIFICATION AND RELAUNCH SERVICE =====
Add the new file type to the list in AUXIP_OLINGO\src\main\java\com\csgroup\auxip\metrics\AuxTypes.java.

Rebuild the whole project thanks to the script build_all.sh in the root git directory.

Stop all the services and relaunch them as usual so the file types cached in the different part of the project may be cleared.

