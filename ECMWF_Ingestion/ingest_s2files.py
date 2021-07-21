import argparse
import copy
import csv
import datetime
import hashlib
import json
import os
import re
import uuid

from FileUtils import parse_all_as_dict,is_a_valid_filename

DEBUG=True

def md5(fname):
    hash_md5 = hashlib.md5()
    with open(fname, "rb") as f:
        for chunk in iter(lambda: f.read(524288), b""):
            hash_md5.update(chunk)
    return hash_md5.hexdigest()


def main():
    parser = argparse.ArgumentParser(description="",  # main description for help
            epilog='Beta', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-i", "--input",
                        help="input",
                       required=True)
    parser.add_argument("-f", "--filetypes",
                        help="filetypes jsons",
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

    #Create output dir
    if not os.path.exists(args.output):
        os.makedirs(args.output)
        if not os.path.exists(args.output):
            raise Exception("Impossible to create output dir "+args.output)


    template_base = None
    with open(args.template) as f:
        template_base = json.load(f)

    # band_dict = {}
    # for (dirpath, dirnames, filenames) in os.walk(args.bands):
    #     for filename in filenames:
    #         with open(os.path.join(args.bands,filename)) as f:
    #             band = json.load(f)
    #             band_dict[band["Name"]] = band["Id"]
    filetype_dict = []
    for (dirpath, dirnames, filenames) in os.walk(args.filetypes):
        for filename in filenames:
            if os.path.splitext(filename)[1] == ".json":
                with open(os.path.join(args.filetypes, filename)) as f:
                    print(filename)
                    filetype = json.load(f)
                    levels = []
                    if "ProductLevels@odata.bind" in filetype:
                        levels = filetype["ProductLevels@odata.bind"]
                    elif "ProductLevels" in filetype:
                        levels = ["ProductLevels('"+f["Level"]+"')" for f in filetype["ProductLevels"]]
                    filetype_dict.append((filetype["LongName"], filetype["Mission"], levels))
    print("FileType dict:")
    print(filetype_dict)
    if len(filetype_dict) == 0:
        raise Exception("No filetypes found in folder")

    odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"

    list_of_files = {}
    idx = 1
    file1 = open(args.input, 'r')
    lines = file1.readlines()
    for filenames in lines:
        filename = os.path.basename(filenames)
        if DEBUG:
            print("Treating "+filename+ " : " +str(idx)+ " / " + str(len(lines)))
        template = None
        update = False
        if os.path.exists(os.path.join(args.output, os.path.splitext(filename)[0] + ".json")):
            with open(os.path.join(args.output, os.path.splitext(filename)[0] + ".json")) as f:
                try:
                    template = json.load(f)
                except Exception as e:
                    raise Exception(
                        "Could not open : " + os.path.join(args.output, os.path.splitext(filename)[0] + ".json"))
            update = True
        else:
            template = copy.copy(template_base)
        if not is_a_valid_filename(filename):
            raise Exception("Filename is not valid : "+filename)
        s2dict =parse_all_as_dict(filename)
        template["@odata.context"] = "$metadata#AuxFiles"
        if not update:
            template["Id"] = str(uuid.uuid4())
        filetype = None
        mission = None
        product_levels = None
        if 'File_Semantic' in s2dict.keys():
            if s2dict['File_Semantic'] == "AUX_RESORB":
                filetype = "AUX_RESORB_S2"
                mission = "S2MSI"
                product_levels = "ProductLevels('L1')"
            elif s2dict['File_Semantic'] == "AUX_PREORB":
                filetype = "AUX_PREORB_S2"
                mission = "S2MSI"
                product_levels = "ProductLevels('L1')"
            else:
                print(s2dict['File_Semantic'])
                for type in filetype_dict:
                    if s2dict['File_Semantic'] in type[0]:
                        print(type[0])
                        filetype = type[0]
                        mission = type[1]
                        product_levels = type[2]
                        break
                if filetype is None:
                    raise Exception("unknown file type")
            template["AuxType@odata.bind"] = "AuxTypes('"+filetype+"')"
        else:
            raise Exception("unknown file type")
        template["FullName"] = filename.strip()
        template["ShortName"] = s2dict['File_Category']+s2dict['File_Semantic']
        if 'processing_baseline' in s2dict.keys():
            template["Baseline"] = s2dict['processing_baseline']
        else:
            if "ProductLevels('L2')" in product_levels:
                template["Baseline"] = "02.14"
                template["IpfVersion"] = "V02.08.00"
            elif "ProductLevels('L1')" in product_levels:
                template["Baseline"] = "02.09"
                template["IpfVersion"] = "V2B-4.2.8"
            else:
                raise Exception("unknown product level")
        #Date part
        start_str = s2dict['applicability_time_period'].split("_")[0]
        stop_str = s2dict['applicability_time_period'].split("_")[1]
        start_dt = datetime.datetime.strptime(start_str, "%Y%m%dT%H%M%S")
        try:
            stop_dt = datetime.datetime.strptime(stop_str, "%Y%m%dT%H%M%S")
        except:
            if stop_str == "99999999T999999":
                stop_dt = datetime.datetime.strptime("99991231T235959", "%Y%m%dT%H%M%S")
        start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
        stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
        template["ValidityStart"] = start_good
        template["ValidityStop"] = stop_good
        template["SensingTimeApplicationStart"] = start_good
        template["SensingTimeApplicationStop"] = stop_good
        crea_dt = datetime.datetime.strptime(s2dict['Creation_Date'], "%Y%m%dT%H%M%S")
        template["CreationDate"] = datetime.datetime.strftime(crea_dt, odata_datetime_format)
        #band
        if "band_index" in s2dict.keys():
            band_id = "B"+s2dict["band_index"]
            template["Band"] = band_id
        else:
            template["Band"] = "BXX"
        #sensor
        if s2dict['Mission_ID'] == "S2A":
            template["Unit"] = "A"
        elif s2dict['Mission_ID'] == "S2B":
            template["Unit"] = "B"
        elif s2dict['Mission_ID'] == "S2_":
            template["Unit"] = "X"
        else:
            raise Exception("Unknown mission ID "+s2dict['Mission_ID'])
        # Write down
        with open(os.path.join(args.output, os.path.splitext(filename)[0] + ".json"), 'w') as json_file:
            json.dump(template, json_file)
        idx = idx + 1


if __name__ == "__main__":
    main()
