#!/bin/python
# -*- coding: utf-8 -*-

import sys
import os
import argparse
import requests
from requests.auth import HTTPBasicAuth
from calendar import monthrange

S3_L0_Types = ["MW_0_MWR___", "OL_0_EFR___", "SL_0_SLT___", "SR_0_SRA___"]
coreURL = "https://lta.cloudferro.copernicus.eu/odata/v1/Products?$filter=ContentDate/Start gt %04d-%02d-%02dT00:00:00.000000Z and ContentDate/Start lt %04d-%02d-%02dT23:59:59.999999Z and contains(Name,'%s')&$top=200"

def getL0(year, month, product_type, ltaUsr, ltaPwd):

    headers = {'Content-type': 'application/json'}
    days_in_month = monthrange(year,month)[1]
    names = set()
    authentification = HTTPBasicAuth(ltaUsr, ltaPwd)
    with open("S3_L0_names_%02d_%04d_%s.txt" % (month,year,product_type) ,"w") as l0_names:
    
        previousNbDays = 0
        for nb_days in [10,20,days_in_month]:
            # On découpe le mois en plusieurs sections pour éviter un traitement trop long pour LTA
            start_day = nb_days - (nb_days - previousNbDays) + 1
            previousNbDays = nb_days

            #Construction de la requête
            request = (coreURL + "&$count=true") % (year,month,start_day,year,month,nb_days,product_type)
            print(request)
            resp = requests.get(request, auth=authentification,headers=headers)
            if resp.status_code == 200:
                count = int(resp.json()["@odata.count"])
                nb_steps = int(count/200)
                
                for aux in resp.json()["value"]:
                    name = aux['Name']
                    names.add(name)

                for step in range( nb_steps ):
                    # On boucle sur toutes les pages de 200 fichiers L0

                    # Mise à jour de la requete
                    request = (coreURL + "&$skip=%d") % (year,month,start_day,year,month,nb_days,product_type,(step+1)*200)
                    print(request)
                    resp = requests.get(request, auth=authentification,headers=headers)            
                    if resp.status_code == 200:
                        for aux in resp.json()["value"]:
                            name = aux['Name']
                            names.add(name)
                    else:
                        raise Exception("Bad return code for request: "+request)
            else:
                raise Exception("Bad return code for request: "+request)

        names = sorted(names)
        for aux in names:
            print(aux)
            l0_names.write(str(aux) + '\n')

def launchGetL0ForType(year, month, type, ltaUsr, ltaPwd):
    print(type)

    if args.month == "all":
        # On doit boucler sur tous les mois
        for m in range(12):
            try:
                getL0(year, m + 1, type, ltaUsr, ltaPwd)
            except Exception as e:
                print(e)
                exc_type, exc_tb = sys.exc_info()
                fname = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
                print(exc_type, fname, exc_tb.tb_lineno)
    else :
        # On ne traite qu'un seul mois
        month_cast = int(month)

        try:
            getL0(year, month_cast, type, ltaUsr, ltaPwd)
        except Exception as e:
            print(e)
            exc_type, exc_tb = sys.exc_info()
            fname = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
            print(exc_type, fname, exc_tb.tb_lineno)

if __name__ == "__main__": 

    parser = argparse.ArgumentParser(description="This script allows you to get the S3 level 0 product names from LTA CloudFerro for a given month and year",  # main description for help
            epilog='\n\n', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-y", "--year",
                        help="Year",
                        required=True)
    parser.add_argument("-pt", "--product_type",
                        help="product type code or part of it",
                        default="all",
                        required=False)
    parser.add_argument("-m", "--month",
                        help="Month",
                        default="all",
                        required=False)
    parser.add_argument("-u", "--user",
                        help="LTA user",
                        required=True)
    parser.add_argument("-pwd", "--password",
                        help="LTA password",
                        required=True)

    args = parser.parse_args()
    year = int(args.year)
    product_type = args.product_type

    if product_type == "all":
        # On doit boucler sur tous les types S3
        for type in S3_L0_Types:
            launchGetL0ForType(year, args.month, type, args.user, args.password)
    else:
        # On ne traite qu'un seul type
        launchGetL0ForType(year, args.month, product_type, args.user, args.password)
