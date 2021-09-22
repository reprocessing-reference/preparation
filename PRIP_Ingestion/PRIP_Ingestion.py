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
    parser.add_argument("-lu", "--ltauser",
                        help="LTA user",
                        required=True)
    parser.add_argument("-lpw", "--ltapassword",
                        help="LTA password ",
                        required=True)
    parser.add_argument("-f", "--filetypes",
                        help="filetypes jsons",
                        required=True)
    parser.add_argument("-w", "--working",
                        help="Working folder",
                        required=True)
    args = parser.parse_args()

    working_S2 = os.path.join(args.working,"S2")
    if not os.path.exists(working_S2):
        os.makedirs(working_S2)
    working_S1 = os.path.join(args.working, "S1")
    if not os.path.exists(working_S1):
        os.makedirs(working_S1)
    working_S3 = os.path.join(args.working, "S3")
    if not os.path.exists(working_S3):
        os.makedirs(working_S3)



    filetype_dict_S1 = []
    filetype_dict_S3 = []
    for (dirpath, dirnames, filenames) in os.walk(args.filetypes):
        for filename in filenames:
            with open(os.path.join(args.filetypes, filename)) as f:
                filetype = json.load(f)
                if "S1" in filetype["Mission"]:
                    filetype_dict_S1.append(filetype["LongName"])
                elif "S3" in filetype["Mission"]:
                    filetype_dict_S3.append(filetype["LongName"])

    for t in filetype_dict_S1:
        token_info = get_token_info(args.auxipuser, args.auxippassword, mode="prod")
        print(t)
        if t == "AUX_POEORB_S1":
            t = "AUX_POEORB"
        if t == "AUX_PREORB_S1":
            t = "AUX_PREORB"
        prip_list_S1 = []
        try:
            prip_list_S1 = PRIP_S2.prip_list(args.ltauser, args.ltapassword,
                                             args.auxipuser, args.auxippassword, "https://lta.cloudferro.copernicus.eu/odata/v1/",
                                             [t], sat="S1",mode="prod")
        except Exception as e:
            print(e)
            time.sleep(5)
            token_info = get_token_info(args.auxipuser, args.auxippassword, mode="prod")
            prip_list_S1 = PRIP_S2.prip_list(args.ltauser, args.ltapassword,
                                             args.auxipuser, args.auxippassword, "https://lta.cloudferro.copernicus.eu/odata/v1/",
                                             [t],  sat="S1",mode="prod")
        for f in prip_list_S1:
            PRIP_S2.prip_download(f[0], f[1], args.ltauser, args.ltapassword, "https://lta.cloudferro.copernicus.eu/odata/v1/",
                                  working_S1)
    for t in filetype_dict_S3:
        token_info = get_token_info(args.auxipuser, args.auxippassword, mode="prod")
        print(t)
        prip_list_S3 = []
        try:
            prip_list_S3 = PRIP_S2.prip_list(args.ltauser, args.ltapassword,
                                             args.auxipuser, args.auxippassword, "https://lta.cloudferro.copernicus.eu/odata/v1/",
                                             [t],  sat="S3",mode="prod")
        except Exception as e:
            print(e)
            time.sleep(5)
            token_info = get_token_info(args.auxipuser, args.auxippassword, mode="prod")
            prip_list_S3 = PRIP_S2.prip_list(args.ltauser, args.ltapassword,
                                             args.auxipuser, args.auxippassword, "https://lta.cloudferro.copernicus.eu/odata/v1/",
                                             [t],  sat="S3",mode="prod")
        for f in prip_list_S3:
            PRIP_S2.prip_download(f[0], f[1], args.ltauser, args.ltapassword,"https://lta.cloudferro.copernicus.eu/odata/v1/",
                                  working_S3)

    exit(0)

    try:
        token_info = get_token_info(args.auxipuser, args.auxippassword, mode="prod")
        prip_list_GIPP = PRIP_S2.prip_list(args.user, args.password,
                                           args.auxipuser, args.auxippassword,"https://prip.s2pdgs.com/odata/v1/", ["_GIP_","_UT1UTC_"],mode="prod")
    except Exception as e:
        print(e)
        time.sleep(5)
        token_info = get_token_info(args.auxipuser, args.auxippassword, mode="prod")
        prip_list_GIPP = PRIP_S2.prip_list(args.user, args.password,
                                           args.auxipuser, args.auxippassword,
                                           "https://prip.s2pdgs.com/odata/v1/", ["_GIP_","_UT1UTC_"],mode="prod")
    print("Number of PRIP File : "+str(len(prip_list_GIPP)))
    for f in prip_list_GIPP:
        PRIP_S2.prip_download(f[0],f[1],args.user, args.password, "https://prip.s2pdgs.com/odata/v1/", working_S2)


