import argparse
import csv
import datetime
import hashlib
import json
import os
import re
import uuid
import shutil
from FileUtils import parse_all_as_dict

time_dependency_dict = {
"AUX_ECMWFD" : "ValidityPeriod",
"R2MACO" : "AnyTime",
"R2ABCA" : "ValidityPeriod",
"ATMIMA" : "AnyTime",
"ATMSAD" :"AnyTime",
"BLINDP" : "AnyTime",
"CLOINV" : "AnyTime",
"CONVER" : "AnyTime",
"DATATI" : "AnyTime",
"DECOMP" : "AnyTime",
"EARMOD" : "AnyTime",
"ECMWFP" : "AnyTime",
"G2PARA" : "AnyTime",
"G2PARE" : "AnyTime",
"GEOPAR" : "AnyTime",
"INTDET" : "AnyTime",
"INVLOC" : "AnyTime",
"JP2KPA" : "AnyTime",
"LREXTR" : "AnyTime",
"MASPAR" : "AnyTime",
"OLQCPA" : "AnyTime",
"PRDLOC" : "AnyTime",
"PROBAS" : "AnyTime",
"R2BINN" : "AnyTime",
"R2CRCO" : "AnyTime",
"R2DEBA" : "AnyTime",
"R2DECT" : "AnyTime",
"R2DEFI" : "AnyTime",
"R2DENT" : "AnyTime",
"R2DEPI" : "ValidityPeriod",
"R2EOB2" : "ValidityPeriod",
"R2EQOG" : "ValidityPeriod",
"R2L2NC" : "AnyTime",
"R2NOMO" : "AnyTime",
"R2PARA" : "AnyTime",
"R2SWIR" : "ValidityPeriod",
"R2WAFI" : "AnyTime",
"RESPAR" : "AnyTime",
"SPAMOD" : "ValidityPeriod",
"TILPAR" : "AnyTime",
"VIEDIR" : "AnyTime",
"AUX_PP1" : "ValidityPeriod",
"AUX_PP2" : "ValidityPeriod",
"AUX_CAL" : "ValidityPeriod",
"AUX_INS" : "ValidityPeriod",
"AUX_UT1UTC" : "ValidityPeriod",
"AUX_POEORB" : "ValidityPeriod",
"AUX_RESORB" : "ValidityPeriod",
"AUX_PREORB" : "ValidityPeriod",
"AUX_RESATT" : "ValidityPeriod",
"AUX_SCS" : "ValidityPeriod",
"AUX_WND" : "ValidityPeriod",
"AUX_WAV" : "ValidityPeriod",
"AUX_ICE" : "ValidityPeriod",
"AMH_ERRMAT" : "ValidityPeriod",
"AMV_ERRMAT" : "ValidityPeriod",
"L2ACAC" : "AnyTime",
"L2ACSC" : "AnyTime",
"PROBA2" : "AnyTime",
"L2ACFG" : "AnyTime"
}


odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"

def treatOneDict(dict_file, input, output):
    dict_band = {}
    for s in dict_file:
        if s[1]["Band"] in dict_band.keys():
            dict_band[s[1]["Band"]].append(s)
        else:
            dict_band[s[1]["Band"]] = []
            dict_band[s[1]["Band"]].append(s)
    for band, files in dict_band.items():
        if len(files) == 1:
            print("only one file for band : " + band)
            print("not updating : " + files[0][0])
            shutil.copyfile(os.path.join(input, files[0][0]), os.path.join(output, files[0][0]))
            continue
        print("Band " + band)
        found = False
        for k, v in time_dependency_dict.items():
            if k in files[0][1]['ShortName']:
                time_validity = v
                found = True
                break
        if not found:
            raise Exception("One filetype is not associated with a TimeValididty :" + files[0][1]['ShortName'])
        if time_validity == "AnyTime":
            l_sorted = sorted(files, key=lambda x: datetime.datetime.strptime(x[1]["CreationDate"],
                                                                              odata_datetime_format))
            print("Only writing : " + l_sorted[-1][0])
            shutil.copyfile(os.path.join(input, l_sorted[-1][0]), os.path.join(output, l_sorted[-1][0]))
        else:
            l_sorted = sorted(files,
                              key=lambda x: datetime.datetime.strptime(x[1]["ValidityStart"], odata_datetime_format))
            print(len(l_sorted))
            for idx in range(len(l_sorted) - 1):
                fifi = l_sorted[idx]
                dt_file_stop = datetime.datetime.strptime(fifi[1]["ValidityStop"], odata_datetime_format)
                dt_file_creation = datetime.datetime.strptime(fifi[1]["CreationDate"], odata_datetime_format)
                dt_file_start = datetime.datetime.strptime(fifi[1]["ValidityStart"], odata_datetime_format)
                fofo = l_sorted[idx + 1]
                nt_file_stop = datetime.datetime.strptime(fofo[1]["ValidityStop"], odata_datetime_format)
                nt_file_creation = datetime.datetime.strptime(fofo[1]["CreationDate"], odata_datetime_format)
                nt_file_start = datetime.datetime.strptime(fofo[1]["ValidityStart"], odata_datetime_format)
                if isTheLatest(fifi, idx, l_sorted):
                    dt_sensing_start = fifi[1]["ValidityStart"]
                    dt_sensing_stop = fofo[1]["ValidityStart"]
                    print("Sensing validity for file : " + fifi[0] + " : " + dt_sensing_start + " : " + dt_sensing_stop)
                    fifi[1]["SensingTimeApplicationStart"] = dt_sensing_start
                    fifi[1]["SensingTimeApplicationStop"] = dt_sensing_stop
                    # Write down
                    with open(os.path.join(output, fifi[0]), 'w') as json_file:
                        json.dump(fifi[1], json_file)
            if isTheLatest(l_sorted[-1], len(l_sorted) - 1, l_sorted):
                dt_sensing_start_last = l_sorted[-1][1]["ValidityStart"]
                dt_sensing_stop_last = l_sorted[-1][1]["ValidityStop"]
                print("Sensing validity for last file : " + l_sorted[-1][
                    0] + " : " + dt_sensing_start_last + " : " + dt_sensing_stop_last)
                l_sorted[-1][1]["SensingTimeApplicationStart"] = dt_sensing_start_last
                l_sorted[-1][1]["SensingTimeApplicationStop"] = dt_sensing_stop_last
                # Write down
                with open(os.path.join(output, l_sorted[-1][0]), 'w') as json_file:
                    json.dump(l_sorted[-1][1], json_file)

def isTheLatest(file, idx, sorted_list):
    dt_file_stop = datetime.datetime.strptime(file[1]["ValidityStop"], odata_datetime_format)
    dt_file_creation = datetime.datetime.strptime(file[1]["CreationDate"], odata_datetime_format)
    dt_file_start = datetime.datetime.strptime(file[1]["ValidityStart"], odata_datetime_format)
    tmp_idx = idx - 1
    while tmp_idx >= 0:
        nt_file_stop = datetime.datetime.strptime(sorted_list[tmp_idx][1]["ValidityStop"], odata_datetime_format)
        nt_file_creation = datetime.datetime.strptime(sorted_list[tmp_idx][1]["CreationDate"], odata_datetime_format)
        nt_file_start = datetime.datetime.strptime(sorted_list[tmp_idx][1]["ValidityStart"], odata_datetime_format)
        if nt_file_start == dt_file_start :
            print("Two files have same properties : " + file[0] + " : " + sorted_list[tmp_idx][0])
            if dt_file_creation < nt_file_creation:
                print("Not the latest : " + file[0])
                return False
        else:
            break
        tmp_idx = tmp_idx - 1
    tmp_idx = idx + 1
    while tmp_idx < len(sorted_list):
        nt_file_stop = datetime.datetime.strptime(sorted_list[tmp_idx][1]["ValidityStop"], odata_datetime_format)
        nt_file_creation = datetime.datetime.strptime(sorted_list[tmp_idx][1]["CreationDate"], odata_datetime_format)
        nt_file_start = datetime.datetime.strptime(sorted_list[tmp_idx][1]["ValidityStart"], odata_datetime_format)
        if nt_file_start == dt_file_start:
            print("Two files have same properties : " + file[0] + " : " + sorted_list[tmp_idx][0])
            if dt_file_creation < nt_file_creation:
                print("Not the latest : "+file[0])
                return False
        else:
            break
        tmp_idx = tmp_idx + 1
    #No one to prove the contrary
    print("Latest : " + file[0])
    return True



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
            if auxfile["AuxType@odata.bind"] in dict_type_file.keys():
                dict_type_file[auxfile["AuxType@odata.bind"]].append((filename,auxfile.copy()))
            else:
                dict_type_file[auxfile["AuxType@odata.bind"]] = []
                dict_type_file[auxfile["AuxType@odata.bind"]].append((filename, auxfile.copy()))
            idx = idx + 1

    for k, v in dict_type_file.items():
        print("Treating file type : " + k)
        if len(v) == 1:
            print("only one file for type : "+k)
            print("not updating : " + v[0][0])
            shutil.copyfile(os.path.join(args.input, v[0][0]), os.path.join(args.output, v[0][0]))
        else:
            dict_sensor = { "A" : [],
                            "B" : [],
                            "X" : []
                          }
            for f in v:
                    if "A" in f[1]["Unit"][0]:
                        dict_sensor["A"].append(f)
                    elif "B" in f[1]["Unit"][0]:
                        dict_sensor["B"].append(f)
                    elif "X" in f[1]["Unit"][0]:
                        dict_sensor["X"].append(f)
                    else:
                        raise Exception("Bad Sensor")
                #list_of_sibling = search_similar(f, v)
                #print("Number of sibling for file : "+f[0] + " : "+str(len(list_of_sibling)))
            treatOneDict(dict_sensor["X"],args.input, args.output)
            treatOneDict(dict_sensor["A"], args.input, args.output)
            treatOneDict(dict_sensor["B"], args.input, args.output)











if __name__ == "__main__":
    main()
