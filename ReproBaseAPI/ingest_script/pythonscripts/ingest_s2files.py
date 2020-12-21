import argparse
import csv
import datetime
import hashlib
import json
import os
import re
import uuid

from FileUtils import parse_all_as_dict

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
    template = None
    with open(args.template) as f:
        template = json.load(f)

    # band_dict = {}
    # for (dirpath, dirnames, filenames) in os.walk(args.bands):
    #     for filename in filenames:
    #         with open(os.path.join(args.bands,filename)) as f:
    #             band = json.load(f)
    #             band_dict[band["Name"]] = band["Id"]
    filetype_dict = []
    for (dirpath, dirnames, filenames) in os.walk(args.filetypes):
        for filename in filenames:
            with open(os.path.join(args.filetypes, filename)) as f:
                filetype = json.load(f)
                filetype_dict.append(filetype["LongName"])


    odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"

    list_of_files = {}
    idx = 1
    for (dirpath, dirnames, filenames) in os.walk(args.input):
        for filename in filenames:
            print("Treating "+filename+ " : " +str(idx)+ " / " + str(len(filenames)))
            s2dict =parse_all_as_dict(filename)
            template["@odata.context"] = "$metadata#AuxFiles"
            template["Id"] = str(uuid.uuid4())
            if 'File_Semantic' in s2dict.keys():
                filetype = None
                if s2dict['File_Semantic'] == "AUX_RESORB":
                    filetype = "AUX_RESORB_S2"
                elif s2dict['File_Semantic'] == "AUX_PREORB":
                    filetype = "AUX_PREORB_S2"
                else:
                    for type in filetype_dict:
                        if s2dict['File_Semantic'] in type:
                            filetype = type
                            break
                    if filetype is None:
                        raise Exception("unknown file type")
                template["AuxType@odata.bind"] = "AuxTypes('"+filetype+"')"
            else:
                raise Exception("unknown file type")
            template["FullName"] = filename
            template["ShortName"] = s2dict['File_Category']+s2dict['File_Semantic']
            if 'processing_baseline' in s2dict.keys():
                template["Baseline"] = s2dict['processing_baseline']
            else:
                template["Baseline"] = "02.09"
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
                template["Unit"] = ["B')"]
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
