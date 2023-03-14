====== UPDATING L0 PRODUCTS ======

===== DOWNLOADING THE NEW L0 PRODUCTS =====
In this directory, you will find 3 directories "S1", "S2" and "S3".
In each of them, there is a list of text files but also a python script "lta_SX.py" which allows us to download the L0 on LTA for a given month or year.

WARNING :  the scripts are not the same from one directory to the other.

One after the other, go in each of the "SX" directories to launch the script. You will have to create a virtual environment or
directly install the different required packages to make the script work on your machine.

For "S1" and "S2", you can launch the script with the month (optionnal) and the year with the following command (example for March 2022) :
python -u lta_SX.py -y 2022 -m 03 -u '<LTA user>' -pwd '<LTA password>'

If the month is not specified, the whole year will be downloaded.

For "S3", there is also an optionnal parameter that can be specified : the product type of the L0.
A list of all the product types is available inside the script.
The following example command can be launched (March 2022, only for "MW_0_MWR___" product types) :
python -u lta_S3.py -y 2022 -m 03 -pt MW_0_MWR___ -u '<LTA user>' -pwd '<LTA password>'

If the product type is not specified, it will loop on every know types in the script.

To know the last month that was uploaded on the service, look for the last text file of each "SX" directory and read its month.
All the files listed here contain the list of the L0 of the specified month_year in the filename. They are all supposed to already be ingested, so the
new ingestion has to begin where the last file stopped.

RECOMMENDATION : Do not download the L0 of the current month as it will be incomplete.


===== GATHER ALL THE L0 LISTED =====
In this directory, you will find the script "printL0List.py". It allows us to list all the L0 names downloaded from LTA from a given month up to the last downloaded and a given input directory.

You have to launch it from the month you started to download the new L0 files, with the following commande for example :
python -u printL0List.py -y 2021 -m 10 -i S1 > S1_L0_202110_202205.listing

This command will print all the S1 L0 from October 2021 up to the last month that has been downloaded in the same directory. The output is redirected to a listing file which will be used
to upload every new L0 to the task 3 database.


===== UPLOAD NEW L0 TO TASK 3 =====
For each listing generated ("S1", "S2" and "S3"), launch the script "upload_S1_to_base.py" or "upload_S2_to_base.py" or "upload_S3_to_base.py" like the following example command :
python -u upload_S1_L0_to_base.py -i S1_L0_202110_202205.listing -dbh <IP of the host of the database> -p <Port on which the database listens> -dbn <Name of the database> -u <User to access the database> -pwd <Password to access the database>