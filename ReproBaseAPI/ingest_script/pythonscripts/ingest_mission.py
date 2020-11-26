import argparse
import csv
import datetime
import hashlib
import json
import os
import re
import uuid

from FileUtils import parse_all_as_dict


def main():
    parser = argparse.ArgumentParser(description="",  # main description for help
            epilog='Beta', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-a", "--auxfiles",
                        help="auxfiles jsons dir",
                        required=True)
    parser.add_argument("-t", "--template",
                        help="template",
                        required=True)
    parser.add_argument(
            "-o",
            "--output",
            help="Output data directory (product directory). Default value: '.'",
            required=True)

    args = parser.parse_args()
    template = None
    with open(args.template) as f:
        template = json.load(f)


    template["Name"] = "SENTINEL2"
    odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"

    list_of_aux = []
    idx = 1
    for (dirpath, dirnames, filenames) in os.walk(args.auxfiles):
        for filename in filenames:
            print("Treating "+filename+ " : " +str(idx)+ " / " + str(len(filenames)))
            json_aux = None
            with open(os.path.join(args.auxfiles,filename)) as f:
                json_aux = json.load(f)
            s2dict =parse_all_as_dict(json_aux["FullName"])
            #sensor
            list_of_aux.append("AuxFiles("+json_aux["Id"]+")")
            idx = idx + 1

    # Write down
    template["AuxFiles@odata.bind"] = list_of_aux
    with open(os.path.join(args.output, "SENTINEL2.json"), 'w') as json_file:
        json.dump(template, json_file)



if __name__ == "__main__":
    main()
