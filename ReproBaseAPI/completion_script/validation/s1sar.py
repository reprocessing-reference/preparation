import requests
from openpyxl import load_workbook
from datetime import datetime 
from requests import Request, Session
import argparse

parser = argparse.ArgumentParser(description="", epilog='Beta', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help

parser.add_argument("-t", "--token",
                        help="Token of auxip services",
                        required=True)

args = parser.parse_args()
s = Session()

mission = "S1SAR"

wb = load_workbook("./data/S1IPF03.40ESRIN.xlsx")
sheet = wb['S1IPF03.40']
nb_periods = 11

periods_dict = {}

for i in range(nb_periods):
    period = ""
    period2 = ""
    unit = sheet['A'][i*10+1].value.strip()
    for pd in sheet['B'][i*10+1:i*10+6]:
        if pd.value is not None:
            period = pd.value
            # print( period )
            if "and" in period:
                periods = period.strip().split("and")
                period = periods[0].strip() + "-%s" % unit
                period2 = periods[1].strip() + "-%s" % unit
    
    for aux in sheet['H'][i*10+1:i*10+6] :
        if period not in periods_dict:
            periods_dict[period] = [[],unit]

        periods_dict[period][0].append(aux.value.strip())

        if period2 :
            if period2 not in periods_dict:
                periods_dict[period2] = [[],unit]
            periods_dict[period2][0].append(aux.value.strip())
        
        
    
for period in periods_dict :

    checks = {}

    print(period.strip())
    start = period.strip().split('-')[0]
    stop = period.strip().split('-')[1]

    start = datetime.strptime(start, '%Y%m%dT%H%M%S').strftime('%Y-%m-%dT%H:%M:%SZ')
    stop = datetime.strptime(stop, '%Y%m%dT%H%M%S').strftime('%Y-%m-%dT%H:%M:%SZ')

    unit = periods_dict[period][1]
    # GET request
    headers = {'Content-Type': 'application/json','Authorization' : 'Bearer %s' % args.token}
    request="https://reprocessing-auxiliary.copernicus.eu/reprocessing.svc/GetReproBaselineNamesForPeriod(Mission='%s',Unit='%s',SensingTimeStart='%s',SensingTimeStop='%s',Variability='Static')" % (mission,unit,start,stop)

    print(request)

    r = s.get(request, headers=headers)
    response_list = r.json()['value']

    for expected_aux in periods_dict[period][0]:
        aux_found = False
        for element in response_list:
            if expected_aux in element :
                # print( expected_aux , "OK")
                checks[expected_aux] = "OK"
                aux_found = True
                break
        if aux_found == False:
            # print( expected_aux , "KO")
            checks[expected_aux] = "KO"

    # print(period.strip())
    start = period.strip().split('-')[0]
    stop = period.strip().split('-')[1]

    # write report
    file = open(r"test-%s-%s-%s-%s.txt" % (mission,unit,start,stop),"w")

    for check in checks:
        file.write( "%s\t%s" %  (check,checks[check]) )
        file.write( "\n")
        print( "%s\t\t%s" %  (check,checks[check]) )
        # print(" ")

    
    file.write( "\n")
    file.write( "\n")

    file.write( "\tGET %s " % request)
    file.write( "\n")
    file.write( "\n")
    for element in response_list:
        file.write("\t fullname : %s " % element )
        file.write( "\n")

    file.close()
