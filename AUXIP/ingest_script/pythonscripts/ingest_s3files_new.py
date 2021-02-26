# coding=utf-8


import argparse
import csv
from datetime import datetime 
import hashlib
import json
import os
import re
import copy
import uuid
import glob

import psycopg2




def md5(fname):
    hash_md5 = hashlib.md5()
    with open(fname, "rb") as f:
        for chunk in iter(lambda: f.read(524288), b""):
            hash_md5.update(chunk)
    return hash_md5.hexdigest()

def parse_filename(file_name):
    # OLCI
    odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"
    # start_dt = datetime.strftime( datetime.strptime(file_name[16:16+15], "%Y%m%dT%H%M%S"),odata_datetime_format)
    # stop_dt  = datetime.strftime( datetime.strptime(file_name[32:32+15], "%Y%m%dT%H%M%S"), odata_datetime_format)
    # creation_dt  = datetime.strftime( datetime.strptime(file_name[48:48+15], "%Y%m%dT%H%M%S"), odata_datetime_format)

    start_dt = datetime.strptime(file_name[16:16+15], "%Y%m%dT%H%M%S") 
    stop_dt  = datetime.strptime(file_name[32:32+15], "%Y%m%dT%H%M%S")
    creation_dt  = datetime.strptime(file_name[48:48+15], "%Y%m%dT%H%M%S")
    
    return (start_dt,stop_dt,creation_dt)

    # "S3A_OL_1_EFR____20160614T101206_20160614T101506_20160614T120248_0180_005_179_2339_SVL_O_NR_001.SEN3"



def main(attributes_json_folder,product_names,cursor):
    
    json_attributes = glob.glob(attributes_json_folder + "/*.json")
    
    aux_types =[]
    for att in json_attributes:
        
        file_name = os.path.basename(att)

        n = len("S3__AX___LWM_AX_")
        if file_name[:n] not in aux_types:
            aux_types.append( file_name[:n] )
            
            # start_dt,stop_dt,creation_dt = parse_filename(file_name)

            fid = open(att)
            
            attributes = json.load( fid )
            file_name = file_name.split(".json")[0]

            # product = Product(name=file_name +".zip",content_type="",content_length=124535,origin_date=creation_dt,publication_date=datetime.utcnow(),eviction_date=datetime.utcnow())
            # product.start = start_dt
            # product.stop = stop_dt

            # session.add(product)
            product_id = product_names[file_name]
            for at in attributes:
                if "Date" in at:
                    cursor.execute('''INSERT INTO product_datetimeoffsetattributes(product_id, name, valuetype, value) VALUES ('%s','%s', 'DateTimeOffset', '%s')''' % (product_id,at,attributes[at]))
                else:
                    cursor.execute('''INSERT INTO product_stringattributes(product_id, name, valuetype, value) VALUES ('%s','%s', 'String', '%s')''' % (product_id,at,attributes[at]))

            


        

if __name__ == "__main__":

    attributes_json_folder="/home/naceur/workspace/extract_attributes/attributes/acri"

    #establishing the connection
    conn = psycopg2.connect(
    database="auxipdb", user='auxip', password='**auxip**', host='172.20.0.2', port= '5432'
    )
    conn.autocommit = True

    cursor = conn.cursor()

    #Retrieving single row
    sql = '''SELECT * from product'''

    #Executing the query
    cursor.execute(sql)

    #Fetching 1st row from the table
    # result = cursor.fetchone();
    # print(result)

    #Fetching 1st row from the table
    products = cursor.fetchall();
    product_names={}
    for product in products:
        # print(product)
        product_id=product[0]
        name = product[6].split('.zip')[0]
        if name not in product_names:
            product_names[name] = product_id 
            print name , " : " , product_id

        # cursor.execute('''INSERT INTO product_checksum(product_id, algorithm, checksumdate, value) VALUES ('%s', 'md5', '%s', 'slkhsdlhkslksdlsdmljsdjsd')''' % (product_id,datetime.utcnow()))
        # cursor.execute('''INSERT INTO product_checksum(product_id, algorithm, checksumdate, value) VALUES ('%s', 'md5', '%s', 'dfdfdfdfdfdfd')''' % (product_id,datetime.utcnow()) ) 
        # cursor.execute('''INSERT INTO product_checksum(product_id, algorithm, checksumdate, value) VALUES ('%s', 'md5', '%s', 'slkhsdlhkslktyhghdsdlsdmljsdjsd')''' % (product_id,datetime.utcnow()) )


    main(attributes_json_folder,product_names,cursor)

    # Commit your changes in the database
    conn.commit()
    # print("Records inserted........")

    # Closing the connection
    conn.close()
