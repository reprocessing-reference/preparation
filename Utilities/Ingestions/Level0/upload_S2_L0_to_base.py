#!/bin/python
# -*- coding: utf-8 -*-

import psycopg2
import argparse

if __name__ == "__main__": 

    parser = argparse.ArgumentParser(description="This script allows you to upload to the Task 3 a listing of L0 files for S2",  # main description for help
            epilog='\n\n', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-i", "--inputFile",
                        help="Input listing file",
                        required=True)
    parser.add_argument("-h", "--host",
                        help="IP of the host of the DataBase",
                        required=True)
    parser.add_argument("-p", "--port",
                        help="Port on which the host of the DataBase is listening for DB requests",
                        required=True)
    parser.add_argument("-dbn", "--dbName",
                        help="Name of the DataBase",
                        required=True)
    parser.add_argument("-u", "--user",
                        help="User for DataBase authentication",
                        required=True)
    parser.add_argument("-pwd", "--password",
                        help="Password for DataBase authentication",
                        required=True)

    args = parser.parse_args()

    host=args.host
    port=args.port
    database=args.dbName
    user=args.user
    password=args.password

    try:
        conn = psycopg2.connect(host=host,port=port,database=database,user=user,password=password)

        s2_l0=args.inputFile

        with open(s2_l0,"r") as fid:
            lines = fid.readlines()
            for line in lines:
                if "http" not in line:
                    splittedLine = line.split(';')
                    l0_name = splittedLine[0].strip()
                    start = stop = splittedLine[1].replace('\n', '').strip()
                    print(l0_name)
                    
                    # S2A_OPER_MSI_L0__DS_MPS__20160504T004104_S20160503T213533_N02.02.tar

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

