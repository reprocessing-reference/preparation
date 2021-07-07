#!/bin/python
# -*- coding: utf-8 -*-

import sys
import os
import argparse
import time
import PRIP_S2

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="This script poll the PRIP to retrieve the files that are not in AUXIP",  # main description for help
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

    args = parser.parse_args()

    prip_list = PRIP_S2.prip_list(args.user, args.password, "_GIP_")
