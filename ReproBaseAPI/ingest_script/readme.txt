Initial setup with empty repository:

You need the list of files in a text file.
Warning : this ingestion will not take into account all the modification done in Reprobase to be compliant
see file initial_ingestion/ingest.bash to create the various elements needed

Update/Add a file:

Same as above, based on the list of file run the ingest_<Sat>.py using this file
Push the json to the service either manually or by using the update_base.py script


