import argparse
import csv
import json
import os


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

    template = None
    with open(args.template) as f:
        template = json.load(f)

    with open(args.input) as csvfile:
        spamreader = csv.DictReader(csvfile, delimiter=',', quotechar='"')
        for lines in spamreader:
            print(lines)
            template['LongName'] = lines['long name']
            if lines['short name'] != '':
                template['ShortName'] = lines['short name']
            else:
                template['ShortName'] = lines['long name']
            if lines['Format'] != '':
                template['Format'] = lines['Format']
            else:
                template['Format'] = 'NotSpecified'
            if lines['Origin'] != '':
                template['Origin'] = lines['Origin']
            else:
                template['Origin'] = 'NotSpecified'
            if lines['Usage'] != '':
                listoflevel = []
                if "L0" in lines['Usage']:
                    listoflevel.append("ProductLevels('L0')")
                if "L1" in lines['Usage']:
                    listoflevel.append("ProductLevels('L1')")
                if "L2" in lines['Usage']:
                    listoflevel.append("ProductLevels('L2')")
                template['ProductLevelApplicability@odata.bind'] = listoflevel
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
            if lines['Comments'] != '':
                template['Description'] = lines['Comments']
            else:
                template['Description'] = 'NotSpecified'
            #Write down
            with open(os.path.join(args.output, lines['long name']+".json"), 'w') as json_file:
                json.dump(template, json_file)




if __name__ == "__main__":
    main()
