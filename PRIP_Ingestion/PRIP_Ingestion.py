#!/bin/python
# -*- coding: utf-8 -*-

import argparse
import PRIP_S2
import time
import json
import os

from ingestion.lib.auxip import get_token_info

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="This script poll the PRIP all the files",  # main description for help
            epilog='Usage samples : \n\tpython PRIP_Ingestion.py -u username -pw password \n\n', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-u", "--user",
                        help="Prip user",
                        required=True)
    parser.add_argument("-pw", "--password",
                        help="Prip password ",
                        required=True)
    parser.add_argument("-au", "--auxipuser",
                        help="Auxip user",
                        required=True)
    parser.add_argument("-apw", "--auxippassword",
                        help="Auxip password ",
                        required=True)
    parser.add_argument("-w", "--working",
                        help="Working folder",
                        required=True)
    args = parser.parse_args()

    working_S2 = os.path.join(args.working,"S2")
    if not os.path.exists(working_S2):
        os.makedirs(working_S2)

    try:
        token_info = get_token_info(args.auxipuser, args.auxippassword, mode="prod")
        prip_list_GIPP = PRIP_S2.prip_list(args.user, args.password,
                                           args.auxipuser, args.auxippassword,"https://lta.cloudferro.copernicus.eu/odata/v1/", ["_UT1UTC_"],sat="S2",mode="prod")
                                           #args.auxipuser, args.auxippassword,"https://lta.cloudferro.copernicus.eu/odata/v1/", ["_GIP_","_UT1UTC_"],sat="S2",mode="prod")
    except Exception as e:
        print(e)
        time.sleep(5)
        token_info = get_token_info(args.auxipuser, args.auxippassword, mode="prod")
        prip_list_GIPP = PRIP_S2.prip_list(args.user, args.password,
                                           args.auxipuser, args.auxippassword,
                                           "https://lta.cloudferro.copernicus.eu/odata/v1/", ["_UT1UTC_"],sat="S2",mode="prod")
                                           #"https://lta.cloudferro.copernicus.eu/odata/v1/", ["_GIP_","_UT1UTC_"],sat="S2",mode="prod")
    print("Number of PRIP File : "+str(len(prip_list_GIPP)))
    for f in prip_list_GIPP:
        PRIP_S2.prip_download(f[0],f[1],args.user, args.password, "https://lta.cloudferro.copernicus.eu/odata/v1/", working_S2)


