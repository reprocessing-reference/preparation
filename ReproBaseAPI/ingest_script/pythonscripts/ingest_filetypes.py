import argparse
import csv
import json
import os

time_dependency_dict = {
"AUX_ECMWFD" : "ValidityPeriod",
"R2ABCA" : "ValidityPeriod",
"R2MACO" : "AnyTime",
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
"AUX_RESATT" : "ValidityPeriod",
"AUX_SCS" : "ValidityPeriod",
"AUX_WND" : "ValidityPeriod",
"AUX_WAV" : "ValidityPeriod",
"AUX_ICE" : "ValidityPeriod",
"AMH_ERRMAT" : "ValidityPeriod",
"L2ACAC" : "AnyTime",
"L2ACSC" : "AnyTime",
"PROBA2" : "AnyTime",
"L2ACFG" : "AnyTime"
}

def main():
    parser = argparse.ArgumentParser(description="",  # main description for help
            epilog='Beta', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-i", "--input",
                        help="input csv file from the xls of filetypes",
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

    with open(args.input) as csvfile:
        spamreader = csv.DictReader(csvfile, delimiter=',', quotechar='"')
        for lines in spamreader:
            template['LongName'] = lines['long name']
            if lines['short name'] != '':
                template['ShortName'] = lines['short name']
            else:
                template['ShortName'] = lines['long name']
            if lines['Format'] != '':
                template['Format'] = lines['Format']
            else:
                template['Format'] = 'NotSpecified'
            if lines['Usage'] != '':
                listoflevel = []
                if "L0" in lines['Usage']:
                    listoflevel.append("ProductLevels('L0')")
                if "L1" in lines['Usage']:
                    listoflevel.append("ProductLevels('L1')")
                if "L2" in lines['Usage']:
                    listoflevel.append("ProductLevels('L2')")
                template['ProductLevels@odata.bind'] = listoflevel
                listoftype = []
                if "/" in lines['Usage'] or "+" in lines['Usage']:
                    if lines['Usage']=="L1+L2":
                        listoftype.append("ProductTypes('L1')")
                        listoftype.append("ProductTypes('L2')")
                    elif lines['Usage']=="L1 SLC/GRD":
                        listoftype.append("ProductTypes('L1SLC')")
                        listoftype.append("ProductTypes('L1GRD')")
                    elif lines['Usage'] == "L2 LFR/LRR":
                        listoftype.append("ProductTypes('L2LFR')")
                        listoftype.append("ProductTypes('L2LRR')")
                    elif lines['Usage'] == "L1 CAL/SRA":
                        listoftype.append("ProductTypes('L1CAL')")
                        listoftype.append("ProductTypes('L1SRA')")
                    elif lines['Usage'] == "L1 CAL/SRA+L2 LAN":
                        listoftype.append("ProductTypes('L1CAL')")
                        listoftype.append("ProductTypes('L1SRA')")
                        listoftype.append("ProductTypes('L2LAN')")
                    elif lines['Usage'] == "L1A/B/C":
                        listoftype.append("ProductTypes('L1A')")
                        listoftype.append("ProductTypes('L1B')")
                        listoftype.append("ProductTypes('L1C')")
                    elif lines['Usage'] == "L2 LST/FRP":
                        listoftype.append("ProductTypes('L2LST')")
                        listoftype.append("ProductTypes('L2FRP')")
                    elif lines['Usage'] == "L1 EFR/ERR":
                        listoftype.append("ProductTypes('L1EFR')")
                        listoftype.append("ProductTypes('L1ERR')")
                    elif lines['Usage'] == "L1 CAL/MWR":
                        listoftype.append("ProductTypes('L1CAL')")
                        listoftype.append("ProductTypes('L1MWR')")
                    elif lines['Usage'] == "L1A/B/C+L2A":
                        listoftype.append("ProductTypes('L1A')")
                        listoftype.append("ProductTypes('L1B')")
                        listoftype.append("ProductTypes('L1C')")
                        listoftype.append("ProductTypes('L2A')")
                    else:
                        raise Exception("No type associated with : "+lines['Usage'])
                else:
                    listoftype.append(str("ProductTypes('"+lines['Usage']+"')").replace(" ",""))
                template['ProductTypes@odata.bind'] = listoftype
            else:
                raise Exception("No usage specified for "+lines['long name'])
            if lines['Static/Dynamic'] != '':
                if lines['Static/Dynamic'] == 'dynamic':
                   template['Variability'] = 'Dynamic'
                else:
                   template['Variability'] = 'Static'
            else:
                template['Variability'] = 'Static'
            if lines['Rule'] != '':
                rule_int = int(lines['Rule'])
                if rule_int == 1:
                    template['Rule'] = 'ValIntersectWithoutDuplicate'
                elif rule_int == 2:
                    template['Rule'] = 'LatestValIntersect'
                elif rule_int == 3:
                    template['Rule'] = 'ValCover'
                elif rule_int == 4:
                    template['Rule'] = 'LatestValCover'
                elif rule_int == 5:
                    template['Rule'] = 'LatestValidity'
                elif rule_int == 6:
                    template['Rule'] = 'LatestValCoverLatestValidity '
                elif rule_int == 7:
                    template['Rule'] = 'LatestValidityClosest'
                elif rule_int == 8:
                    template['Rule'] = 'BestCentredCover'
                elif rule_int == 9:
                    template['Rule'] = 'LatestValCoverClosest '
                elif rule_int == 10:
                    template['Rule'] = 'LargestOverlap'
                elif rule_int == 11:
                    template['Rule'] = 'LatestGeneration'
                elif rule_int == 12:
                    template['Rule'] = 'ClosestStartValidity'
                elif rule_int == 13:
                    template['Rule'] = 'ClosestStopValidity'
                elif rule_int == 14:
                    template['Rule'] = 'LatestStopValidity'
                else:
                    template['Rule'] = ''
            else:
                template['Rule'] = 'ValIntersectWithoutDuplicate'
            found = False
            for k,v in time_dependency_dict.items():
                if k in template['ShortName']:
                    template['Validity'] = v
                    found = True
                    break
            if not found:
                print("One filetype is not associated with a TimeValididty :"+template['ShortName'])
                template['Validity'] = "ValidityPeriod"
            if lines['Comments'] != '':
                if len(lines['Comments']) > 255:
                    template['Comments'] = lines['Comments'][:255]
                else:
                    template['Comments'] = lines['Comments']
            else:
                template['Comments'] = 'Not available'
            #Write down
            with open(os.path.join(args.output, lines['long name']+".json"), 'w') as json_file:
                json.dump(template, json_file)




if __name__ == "__main__":
    main()
