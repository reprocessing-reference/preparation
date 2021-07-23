#!/bin/python
# -*- coding: utf-8 -*-

import argparse
import PRIP_S2

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="This script poll the PRIP all the files",  # main description for help
            epilog='Usage samples : \n\tpython PRIP_Ingestion.py -u username -pw password \n\n', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-u", "--user",
                        help="Prip user",
                        required=True)
    parser.add_argument("-pw", "--password",
                        help="Prip password ",
                        required=True)
    parser.add_argument("-w", "--working",
                        help="Working folder",
                        required=True)
    args = parser.parse_args()

    prip_list_GIPP = PRIP_S2.prip_list(args.user, args.password, "https://prip.s2pdgs.com/odata/v1/", "_GIP_")
    for f in prip_list_GIPP:
        PRIP_S2.prip_download(f[0],f[1],args.user, args.password, "https://prip.s2pdgs.com/odata/v1/", args.working)
    prip_list_UTC = PRIP_S2.prip_list(args.user, args.password, "https://prip.s2pdgs.com/odata/v1/", "_UT1UTC_")
    for f in prip_list_UTC:
        PRIP_S2.prip_download(f[0],f[1],args.user, args.password, "https://prip.s2pdgs.com/odata/v1/", args.working)


