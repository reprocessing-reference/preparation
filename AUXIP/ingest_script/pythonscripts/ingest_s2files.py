import argparse
import csv
import datetime
import hashlib
import json
import os
import re
import copy
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
    parser.add_argument("-t", "--template",
                        help="template",
                        required=True)
    parser.add_argument(
            "-o",
            "--output",
            help="Output data directory (product directory). Default value: '.'",
            required=True)

    args = parser.parse_args()
    template_base = None
    with open(args.template) as f:
        template_base = json.load(f)




    odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"

    list_of_files = {}
    idx = 1
    for (dirpath, dirnames, filenames) in os.walk(args.input):
        for filename in filenames:
            template = copy.deepcopy(template_base)
            print("Treating "+filename+ " : " +str(idx)+ " / " + str(len(filenames)))
            s2dict =parse_all_as_dict(filename)
            template["@odata.context"] = "$metadata#Products"
            template["Id"] = str(uuid.uuid4())
            template["Name"] = filename
            template["ContentType"] = "application/octet-stream"
            template["ContentLength"] = os.path.getsize(os.path.join(dirpath, filename))
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
            crea_dt = datetime.datetime.strptime(s2dict['Creation_Date'], "%Y%m%dT%H%M%S")
            template["OriginDate"] = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            template["PublicationDate"] = datetime.datetime.strftime(datetime.datetime.now(), odata_datetime_format)
            template["EvictionDate"] = datetime.datetime.strftime(datetime.datetime.now()+datetime.timedelta(days=7), odata_datetime_format)
            template["ContentDate"]["Start"] = datetime.datetime.strftime(start_dt, odata_datetime_format)
            template["ContentDate"]["End"] = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            #Checksum
            template["Checksum"][0]["Value"] = md5(os.path.join(dirpath,filename))
            template["Checksum"][0]["ChecksumDate"] = datetime.datetime.strftime(datetime.datetime.now(), odata_datetime_format)
            #Attributes
            # sensor
            if s2dict['Mission_ID'] == "S2A":
                template["StringAttributes"][0]["Value"] = "S2A"
            elif s2dict['Mission_ID'] == "S2B":
                template["StringAttributes"][0]["Value"] = "S2B"
            elif s2dict['Mission_ID'] == "S2_":
                template["StringAttributes"][0]["Value"] = "S2_"
            else:
                raise Exception("Unknown mission ID " + s2dict['Mission_ID'])
            template["StringAttributes"][0]["Name"] = "platformShortName"
            template["StringAttributes"].append(copy.copy(template["StringAttributes"][0]))
            template["StringAttributes"][1]["Name"] = "productType"
            # band
            if "band_index" in s2dict.keys():
                band_id = "B" + s2dict["band_index"]
                template["StringAttributes"][1]["Value"] = s2dict['File_Semantic'] + "_" + band_id
            else:
                template["StringAttributes"][1]["Value"] = s2dict['File_Semantic']
            #DateTimeOffsetAttributes
            template["DateTimeOffsetAttributes"][0]["Name"] = "beginningDateTime"
            template["DateTimeOffsetAttributes"][0]["Value"] = datetime.datetime.strftime(start_dt, odata_datetime_format)
            template["DateTimeOffsetAttributes"].append(copy.copy(template["DateTimeOffsetAttributes"][0]))
            template["DateTimeOffsetAttributes"][1]["Name"] = "endingDateTime"
            template["DateTimeOffsetAttributes"][1]["Value"] = datetime.datetime.strftime(stop_dt,
                                                                                          odata_datetime_format)

            # Write down
            with open(os.path.join(args.output, os.path.splitext(filename)[0] + ".json"), 'w') as json_file:
                json.dump(template, json_file)
            idx = idx + 1


if __name__ == "__main__":
    main()
