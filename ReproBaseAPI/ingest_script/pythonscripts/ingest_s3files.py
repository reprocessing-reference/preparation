import argparse
import datetime
import hashlib
import json
import os
import re
import uuid

def parse_filename_s3(the_file_name):
    print(the_file_name)
    p = re.compile('(S3_|S3B|S3A)_([A-Z|0-9|_]{11})_([A-Z|0-9|_]{15})_([A-Z|0-9|_]{15})_([A-Z|0-9|_]{15}).*')
    ama = p.match(the_file_name)
    if not ama:
        return ama
    items = ama.groups()
    result = {}
    result["Mission_ID"] = items[0]
    result["File_Class"] = items[1]
    result["Validity_Start"] = items[2]
    result["Validity_Stop"] = items[3]
    result["Generation_Date"] = items[4]
    return result

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
                filetype_dict.append((filetype["LongName"], filetype["Mission"]))


    odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"

    # Using readlines()
    file1 = open(args.input, 'r')
    lines = file1.readlines()
    filetype_str = ""
    start_good = ""
    stop_good = ""
    crea_good = ""
    shortname = ""
    # Strips the newline character
    list_of_files = {}
    idx = 1
    for filenames in lines:
        filename = os.path.basename(filenames)
        print("Treating "+filename+ " : " +str(idx)+ " / " + str(len(lines)))
        if "S3A" in filename:
            template["Unit"] = "A"
        elif "S3B" in filename:
            template["Unit"] = "B"
        else:
            template["Unit"] = "X"
        dic = parse_filename_s3(os.path.splitext(os.path.splitext(filename)[0])[0])
        start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
        stop_dt = datetime.datetime.strptime(dic["Validity_Stop"], "%Y%m%dT%H%M%S")
        start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
        stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
        crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
        crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
        shortname = dic["File_Class"]
        filetype_str = dic["File_Class"]

        filetype = None
        mission = None
        for type in filetype_dict:
            if filetype_str in type[0]:
                filetype = type[0]
                mission = type[1]
                break
        if filetype is None:
            raise Exception("unknown file type")
        template["AuxType@odata.bind"] = "AuxTypes('" + filetype + "')"
        template["@odata.context"] = "$metadata#AuxFiles"
        template["Id"] = str(uuid.uuid4())
        template["FullName"] = filename.strip()
        template["ShortName"] = shortname
        template["Baseline"] = "06.11"
        if "SRAL" in mission:
            template["Baseline"] = "06.19"
            if "S3A" in filename:
                template["IpfVersion"] = "S3A-2.69"
            elif "S3B" in filename:
                template["IpfVersion"] = "S3B-1.45"
            else:
                template["IpfVersion"] = "S3A-2.69 & S3B-1.45"
        if "MWR" in mission:
            template["Baseline"] = "06.11"
            if "S3A" in filename:
                template["IpfVersion"] = "S3A-2.69"
            elif "S3B" in filename:
                template["IpfVersion"] = "S3B-1.45"
            else:
                template["IpfVersion"] = "S3A-2.69 & S3B-1.45"
        #Date part
        template["ValidityStart"] = start_good
        template["ValidityStop"] = stop_good
        template["SensingTimeApplicationStart"] = start_good
        template["SensingTimeApplicationStop"] = stop_good
        template["CreationDate"] = crea_good
        #band
        template["Band"] = "BXX"
        # Write down
        with open(os.path.join(args.output, os.path.splitext(filename)[0] + ".json"), 'w') as json_file:
            json.dump(template, json_file)
        idx = idx + 1


if __name__ == "__main__":
    main()
