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

    template_s2a = template.copy()
    template_s2b = template.copy()
    template_s2a["FullName"] = "SENTINEL2A_S2MSI"
    template_s2a["ShortName"] = "S2MSI"
    template_s2a["Unit"] = "A"
    template_s2a["Mission"] = "SENTINEL2"
    template_s2b["FullName"] = "SENTINEL2B_S2MSI"
    template_s2b["ShortName"] = "S2MSI"
    template_s2b["Unit"] = "B"
    template_s2b["Mission"] = "SENTINEL2"
    odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"

    list_of_aux_s2a = []
    list_of_aux_s2b = []
    idx = 1
    for (dirpath, dirnames, filenames) in os.walk(args.auxfiles):
        for filename in filenames:
            print("Treating "+filename+ " : " +str(idx)+ " / " + str(len(filenames)))
            json_aux = None
            with open(os.path.join(args.auxfiles,filename)) as f:
                json_aux = json.load(f)
            s2dict =parse_all_as_dict(json_aux["FullName"])
            #sensor
            if s2dict['Mission_ID'] == "S2A":
                list_of_aux_s2a.append("AuxFiles("+json_aux["Id"]+")")
            elif s2dict['Mission_ID'] == "S2B":
                list_of_aux_s2b.append("AuxFiles(" + json_aux["Id"] + ")")
            elif s2dict['Mission_ID'] == "S2_":
                list_of_aux_s2a.append("AuxFiles(" + json_aux["Id"] + ")")
                list_of_aux_s2b.append("AuxFiles(" + json_aux["Id"] + ")")
            idx = idx + 1

    # Write down
    template_s2a["AuxFiles@odata.bind"] = list_of_aux_s2a
    template_s2b["AuxFiles@odata.bind"] = list_of_aux_s2b
    with open(os.path.join(args.output, "SENTINEL2A.json"), 'w') as json_file:
        json.dump(template_s2a, json_file)
    with open(os.path.join(args.output, "SENTINEL2B.json"), 'w') as json_file:
        json.dump(template_s2b, json_file)



if __name__ == "__main__":
    main()
