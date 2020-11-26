import argparse
import csv
import datetime
import hashlib
import json
import os
import re
import uuid

from FileUtils import parse_all_as_dict

odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"
def search_similar(json_file, json_files):
    result = []
    print("FileIn: " + json_file[0])
    for f in json_files:
        if f[0] == json_file[0]:
            continue
        print("File: "+f[0])
        dt_file_stop = datetime.datetime.strptime(json_file[1]["ValidityStop"],odata_datetime_format)
        dt_file_creation = datetime.datetime.strptime(json_file[1]["CreationDate"], odata_datetime_format)
        dt_file_start = datetime.datetime.strptime(json_file[1]["ValidityStart"], odata_datetime_format)
        if f[1]["Baseline@odata.bind"] == json_file[1]["Baseline@odata.bind"] and\
                f[1]["Band@odata.bind"] == json_file[1]["Band@odata.bind"]:
            isSameSens = True
            if len(f[1]["Sensors@odata.bind"]) == len(json_file[1]["Sensors@odata.bind"]):
                print(len(f[1]["Sensors@odata.bind"]))
                for s in f[1]["Sensors@odata.bind"]:
                    print("truc "+s)
                    if s not in json_file[1]["Sensors@odata.bind"]:
                        isSameSens = False
            else:
                isSameSens = False
            if isSameSens:
                dt_temp_creation = datetime.datetime.strptime(f[1]["CreationDate"], odata_datetime_format)
                dt_temp = datetime.datetime.strptime(f[1]["ValidityStart"],odata_datetime_format)
                if dt_temp == dt_file_start:
                    raise Exception("Two files have same properties : "+f[0]+" : "+json_file[0])
                result.append(f[1])
    return result

def main():
    parser = argparse.ArgumentParser(description="",  # main description for help
            epilog='Beta', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-i", "--input",
                        help="input",
                       required=True)
    parser.add_argument(
            "-o",
            "--output",
            help="Output data directory (product directory). Default value: '.'",
            required=True)

    args = parser.parse_args()

    odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"

    dict_type_file = {}

    idx = 1
    for (dirpath, dirnames, filenames) in os.walk(args.input):
        for filename in filenames:
            print("Treating "+filename+ " : " +str(idx)+ " / " + str(len(filenames)))
            auxfile = None
            with open(os.path.join(args.input, filename)) as f:
                auxfile = json.load(f)
            if auxfile["FileType@odata.bind"] in dict_type_file.keys():
                dict_type_file[auxfile["FileType@odata.bind"]].append((filename,auxfile.copy()))
            else:
                dict_type_file[auxfile["FileType@odata.bind"]] = []
                dict_type_file[auxfile["FileType@odata.bind"]].append((filename, auxfile.copy()))
            idx = idx + 1

    for k, v in dict_type_file.items():
        print("Treating file type : " + k)
        if len(v) == 1:
            print("only one file for type : "+k)
            print("not updating : " + v[0][0])
        else:
            idx = 1
            for f in v:
                list_of_sibling = search_similar(f, v)
                print("Number of sibling for file : "+f[0] + " : "+str(len(list_of_sibling)))



if __name__ == "__main__":
    main()
