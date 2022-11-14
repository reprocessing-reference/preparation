#!/bin/python
# -*- coding: utf-8 -*-

import psycopg2
import argparse

host=""
port=""
database=""
user=""
password=""

if __name__ == "__main__": 

    parser = argparse.ArgumentParser(description="This script allows you to upload to the Task 3 a listing of L0 files for S1",  # main description for help
            epilog='\n\n', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-i", "--inputFile",
                        help="Input listing file",
                        required=True)

    args = parser.parse_args()

    try:
        conn = psycopg2.connect(host=host,port=port,database=database,user=user,password=password)

        s1_l0=args.inputFile

        with open(s1_l0,"r") as fid:
            lines = fid.readlines()
            for line in lines:
            
                l0_name = line.replace('\n','').strip()
                start = l0_name[17:17+15]
                stop = l0_name[17+16:17+16+15]
                print(l0_name)

                # S1B_IW_RAW__0SDV_20190822T145111_20190822T145143_017700_0214CF_EBA5.SAFE.zip

                cursor = conn.cursor()
                sql = """INSERT INTO l0_products(name,validitystart,validitystop) VALUES(%s,%s,%s);"""
                try:
                    cursor.execute(sql, (l0_name,start,stop))
                except Exception as e:
                    print(e)
                    cursor.execute("ROLLBACK")

            conn.commit()
    except Exception as e:
        print(e)


