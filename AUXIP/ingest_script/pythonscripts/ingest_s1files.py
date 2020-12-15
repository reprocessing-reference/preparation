import argparse
import datetime
import hashlib
import json
import os
import copy
import re
import uuid

def parse_filename_wnd(the_file_name):
    print(the_file_name)
    p = re.compile('(S1A|S1B|S1_)_([A-Z|0-9|_]{7})_V([A-Z|0-9|_]{15})_G([A-Z|0-9|_]{15}).*')
    ama = p.match(the_file_name)
    if not ama:
        return ama
    items = ama.groups()
    result = {}
    result["Mission_ID"]= items[0]
    result["File_Class"]= items[1]
    result["Validity_Start"]= items[2]
    result["Generation_Date"]= items[3]
    result["ShortName"] = items[1]
    return result

def parse_filename_orb(the_file_name):
    print(the_file_name)
    p = re.compile('(S1A|S1B|S1_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([A-Z|0-9|_]{15})_V([A-Z|0-9|_]{15})_([A-Z|0-9|_]{15}).*')
    ama = p.match(the_file_name)
    if not ama:
        return ama
    items = ama.groups()
    result = {}
    result["Mission_ID"]= items[0]
    result["File_Class"]= items[1]
    result["File_Category"] = items[2]
    result["File_Type"] = items[3]
    result["File_Content"] = items[4]
    result["Generation_Date"] = items[5]
    result["Validity_Start"]= items[6]
    result["Validity_Stop"] = items[7]
    result["ShortName"] = result["File_Category"]+result["File_Type"]
    return result



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
        template = copy.deepcopy(template_base)
        print("Treating "+filename+ " : " +str(idx)+ " / " + str(len(lines)))

        if "AUX_WND" in filename:
            print("AUX_WND file detected")
            filetype_str = "S1__AUX_WND"
            dic = parse_filename_wnd(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = start_dt + datetime.timedelta(days=1)
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AUX_WAV" in filename:
            print("AUX_WAVE file detected")
            filetype_str = "S1__AUX_WAV"
            dic = parse_filename_wnd(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = start_dt + datetime.timedelta(days=1)
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AUX_ICE" in filename:
            print("AUX_ICE file detected")
            filetype_str = "S1__AUX_ICE"
            dic = parse_filename_wnd(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = start_dt + datetime.timedelta(days=1)
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AUX_PP1" in filename:
            print("AUX_PP1 file detected")
            filetype_str = "AUX_PP1"
            dic = parse_filename_wnd(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = datetime.datetime.strptime("21000101T000000", "%Y%m%dT%H%M%S")
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AUX_PP2" in filename:
            print("AUX_PP2 file detected")
            filetype_str = "AUX_PP2"
            dic = parse_filename_wnd(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = datetime.datetime.strptime("21000101T000000", "%Y%m%dT%H%M%S")
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AUX_CAL" in filename:
            print("AUX_CAL file detected")
            filetype_str = "AUX_CAL"
            dic = parse_filename_wnd(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = datetime.datetime.strptime("21000101T000000", "%Y%m%dT%H%M%S")
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AUX_INS" in filename:
            print("AUX_INS file detected")
            filetype_str = "AUX_INS"
            dic = parse_filename_wnd(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = datetime.datetime.strptime("21000101T000000", "%Y%m%dT%H%M%S")
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AUX_SCS" in filename:
            print("AUX_SCS file detected")
            filetype_str = "AUX_SCS"
            dic = parse_filename_wnd(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = datetime.datetime.strptime("21000101T000000", "%Y%m%dT%H%M%S")
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AUX_PREORB" in filename:
            print("AUX_PREORB file detected")
            filetype_str = "AUX_PREORB"
            dic = parse_filename_orb(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = datetime.datetime.strptime(dic["Validity_Stop"], "%Y%m%dT%H%M%S")
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AUX_RESORB" in filename:
            print("AUX_RESORB file detected")
            filetype_str = "AUX_RESORB"
            dic = parse_filename_orb(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = datetime.datetime.strptime(dic["Validity_Stop"], "%Y%m%dT%H%M%S")
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AUX_POEORB" in filename:
            print("AUX_PREORB file detected")
            filetype_str = "AUX_POEORB"
            dic = parse_filename_orb(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = datetime.datetime.strptime(dic["Validity_Stop"], "%Y%m%dT%H%M%S")
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AMH_ERRMAT" in filename:
            print("AMH_ERRMAT file detected")
            filetype_str = "AMH_ERRMAT_MPC"
            dic = parse_filename_orb(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = datetime.datetime.strptime(dic["Validity_Stop"], "%Y%m%dT%H%M%S")
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AMV_ERRMAT" in filename:
            print("AMV_ERRMAT file detected")
            filetype_str = "AMH_ERRMAT_MPC"
            dic = parse_filename_orb(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = datetime.datetime.strptime(dic["Validity_Stop"], "%Y%m%dT%H%M%S")
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        elif "AUX_RESATT" in filename:
            print("AUX_RESATT file detected")
            filetype_str = "AUX_RESATT"
            dic = parse_filename_orb(os.path.splitext(os.path.splitext(filename)[0])[0])
            start_dt = datetime.datetime.strptime(dic["Validity_Start"], "%Y%m%dT%H%M%S")
            stop_dt = datetime.datetime.strptime(dic["Validity_Stop"], "%Y%m%dT%H%M%S")
            start_good = datetime.datetime.strftime(start_dt, odata_datetime_format)
            stop_good = datetime.datetime.strftime(stop_dt, odata_datetime_format)
            crea_dt = datetime.datetime.strptime(dic['Generation_Date'], "%Y%m%dT%H%M%S")
            crea_good = datetime.datetime.strftime(crea_dt, odata_datetime_format)
            shortname = dic["ShortName"]
        else:
            raise Exception("Filetype not detected : "+filename)

        template["@odata.context"] = "$metadata#Products"
        template["Id"] = str(uuid.uuid4())
        template["Name"] = filename
        template["ContentType"] = "application/octet-stream"
        #template["ContentLength"] = os.path.getsize(os.path.join(dirpath, filename))
        template["OriginDate"] = datetime.datetime.strftime(crea_dt, odata_datetime_format)
        template["PublicationDate"] = datetime.datetime.strftime(datetime.datetime.now(), odata_datetime_format)
        template["EvictionDate"] = datetime.datetime.strftime(datetime.datetime.now() + datetime.timedelta(days=7),
                                                              odata_datetime_format)
        template["ContentDate"]["Start"] = datetime.datetime.strftime(start_dt, odata_datetime_format)
        template["ContentDate"]["End"] = datetime.datetime.strftime(stop_dt, odata_datetime_format)
        # Checksum
        #template["Checksum"][0]["Value"] = md5(os.path.join(dirpath, filename))
        template["Checksum"][0]["ChecksumDate"] = datetime.datetime.strftime(datetime.datetime.now(),
                                                                             odata_datetime_format)
        # Attributes
        # sensor
        if "S1A" in filename:
            template["StringAttributes"][0]["Value"] = "S1A"
        elif "S1B" in filename:
            template["StringAttributes"][0]["Value"] = "S1B"
        else:
            template["StringAttributes"][0]["Value"] = "S1_"
        template["StringAttributes"][0]["Name"] = "platformShortName"
        template["StringAttributes"].append(copy.copy(template["StringAttributes"][0]))
        template["StringAttributes"][1]["Name"] = "productType"
        template["StringAttributes"][1]["Value"] = filetype_str
        # DateTimeOffsetAttributes
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
