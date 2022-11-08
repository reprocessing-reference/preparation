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

mission = "S2MSI"

wb = load_workbook("./data/S2_AUX_for_reprocessing_baseline_ESRIN.xlsx")
s2a_sheet = wb['S2A']
s2b_sheet = wb['S2B']

static_aux = [
"S2A_OPER_GIP_ATMIMA_MPC__20210608T000002_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_ATMIMA_MPC__20210608T000001_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_ATMSAD_MPC__20210608T000005_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_ATMSAD_MPC__20210608T000003_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_BLINDP_MPC__20210608T000003_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_BLINDP_MPC__20210608T000002_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_CLOINV_MPC__20210609T000005_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_CLOINV_MPC__20210609T000002_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_CLOPAR_MPC__20220120T000001_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_CLOPAR_MPC__20220120T000001_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_CONVER_MPC__20210608T000000_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_CONVER_MPC__20210608T000000_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_DATATI_MPC__20210608T000007_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_DATATI_MPC__20210608T000003_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_DECOMP_MPC__20210608T000000_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_DECOMP_MPC__20210727T000001_V20150622T000000_21000101T000000_B00",
"S2__OPER_GIP_EARMOD_MPC__20210608T000001_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_ECMWFP_MPC__20210608T000002_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_ECMWFP_MPC__20210727T000002_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_G2PARA_MPC__20220328T000025_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_G2PARA_MPC__20220328T000025_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_G2PARE_MPC__20150605T094736_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_G2PARE_MPC__20210610T000001_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_GEOPAR_MPC__20210608T000003_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_GEOPAR_MPC__20210608T000001_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_HRTPAR_MPC__20220328T000000_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_HRTPAR_MPC__20220328T000000_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_INVLOC_MPC__20210608T000006_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_INVLOC_MPC__20210608T000003_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_INTDET_MPC__20210906T000010_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_INTDET_MPC__20210906T000010_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_JP2KPA_MPC__20210713T123700_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_JP2KPA_MPC__20210713T123700_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_LREXTR_MPC__20210608T000001_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_LREXTR_MPC__20210608T000001_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_MASPAR_MPC__20220120T000009_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_MASPAR_MPC__20220120T000002_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_OLQCPA_MPC__20220217T000041_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_OLQCPA_MPC__20220217T000041_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_PRDLOC_MPC__20210608T000012_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_PRDLOC_MPC__20210608T000009_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_PROBAS_MPC__20220121T000400_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_PROBAS_MPC__20220121T000400_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_R2BINN_MPC__20210608T000003_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_R2BINN_MPC__20210608T000001_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_R2CRCO_MPC__20210608T000003_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_R2CRCO_MPC__20220120T000002_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_R2DECT_MPC__20210608T000003_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_R2DECT_MPC__20210608T000001_V20150622T000000_21000101T000000_BXX",
"S2A_OPER_GIP_R2DEFI_MPC__20210608T000003_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_R2DEFI_MPC__20210608T000001_V20150622T000000_21000101T000000_BXX",
"S2A_OPER_GIP_R2DENT_MPC__20210608T000003_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_R2DENT_MPC__20210608T000001_V20150622T000000_21000101T000000_BXX",
"S2A_OPER_GIP_R2L2NC_MPC__20210608T000003_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_R2L2NC_MPC__20210608T000001_V20150622T000000_21000101T000000_BXX",
"S2A_OPER_GIP_R2NOMO_MPC__20210608T000004_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_R2NOMO_MPC__20210608T000001_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_R2PARA_MPC__20220307T000009_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_R2PARA_MPC__20220307T000009_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_R2WAFI_MPC__20210608T000003_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_R2WAFI_MPC__20210608T000001_V20150622T000000_21000101T000000_BXX",
"S2A_OPER_GIP_RESPAR_MPC__20210608T000001_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_RESPAR_MPC__20210608T000001_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_TILPAR_MPC__20210608T000007_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_TILPAR_MPC__20210608T000001_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_VIEDIR_MPC__20210608T000005_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_VIEDIR_MPC__20210608T000006_V20150622T000000_21000101T000000_BXX",
"S2__OPER_GIP_L2ACAC_MPC__20220121T000004_V20150622T000000_21000101T000000_B00",
"S2__OPER_GIP_L2ACFG_MPC__20220124T000000_V20150622T000000_21000101T000000_B00",
"S2__OPER_GIP_L2ACSC_MPC__20220121T000003_V20150622T000000_21000101T000000_B00",
"S2__OPER_GIP_PROBA2_MPC__20220121T000400_V20150622T000000_21000101T000000_B00"]


static_aux_A = []
static_aux_B = []

for aux in static_aux:
    if "S2B" not in aux:
        if "BXX" in aux:
            for m in range(1,13):
                static_aux_A.append( aux.replace("XX","%02d" % m ) )

            static_aux_A.append( aux.replace("XX","8A" ) )
        else:
            static_aux_A.append(aux)



for aux in static_aux:
    if "S2A" not in aux:
        if "BXX" in aux:
            for m in range(1,13):
                static_aux_B.append( aux.replace("XX","%02d" % m ) )
                
            static_aux_B.append( aux.replace("XX","8A" ) )
        else:
            static_aux_B.append(aux)

periods_dict = {}

def read_sheet(sheet,periods_dict):

    # In the following lines, the offset of 1 in the column coordinates is due to avoid the title of the column

    # Number of lines for each period in the xlsx
    nbLinesPerPeriod = 9
    # Number of lines where a file name is defined for each period
    nbLinesOfFilesInPeriod = 3
    # Number of periods (is computed automatically later)
    nb_periods = 0
    
    # Finding the right number of periods without trusting too much len(sheet["B"])
    # Instead, we loop on the number of line in the B column, and check if the column A has a unit
    # If it has no unit, then we stop the loop and the number of periods is confirmed to be true
    for i in range(len(sheet["B"])):
        if sheet['A'][i].value is None:
            nb_periods = int((i - 1) / nbLinesPerPeriod)
            break

    # Loop over each period
    for i in range(nb_periods):
        period = ""
        period2 = ""
        currentPeriodIndex = i*nbLinesPerPeriod+1
        unit = sheet['A'][currentPeriodIndex].value.strip()
        # Loop over the merged cells of the period to find the value
        for pd in sheet['B'][currentPeriodIndex:currentPeriodIndex+nbLinesPerPeriod-1]:
            if pd.value is not None:
                period = pd.value
                # print( period )
                if "and" in period:
                    periods = period.strip().split("and")
                    period = periods[0].strip() + "-%s" % unit
                    period2 = periods[1].strip() + "-%s" % unit
                else:
                    period = period.strip() + "-%s" % unit
                break
        
        # Loop over each file name
        for aux in sheet['E'][currentPeriodIndex:currentPeriodIndex+nbLinesOfFilesInPeriod] :
            if period not in periods_dict:
                periods_dict[period] = [[],unit]

            aux_name = aux.value.strip()

            if "BXX" in aux_name:
                for m in range(1,13):
                    periods_dict[period][0].append(  aux_name.replace("XX","%02d" % m ) )

                periods_dict[period][0].append(  aux_name.replace("XX","8A" ) )

            else:
                periods_dict[period][0].append( aux_name )


            if period2 :
                if period2 not in periods_dict:
                    periods_dict[period2] = [[],unit]
                
                if "BXX" in aux_name:
                    for m in range(1,13):
                        periods_dict[period2][0].append(  aux_name.replace("XX","%02d" % m ) )

                    periods_dict[period][0].append(  aux_name.replace("XX","8A" ) )

                else:
                    periods_dict[period2][0].append( aux_name )

            
        
    
read_sheet(s2a_sheet,periods_dict)
read_sheet(s2b_sheet,periods_dict)


nbOK = 0
nbKO = 0

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
    request="https://reprocessing-preparation.ml/reprocessing.svc/GetReproBaselineNamesForPeriod(Mission='%s',Unit='%s',SensingTimeStart='%s',SensingTimeStop='%s',Variability='Static')" % (mission,unit,start,stop)

    # print(request)

    r = s.get(request, headers=headers)
    response_list = r.json()['value']

    expected_aux_list = periods_dict[period][0] + static_aux_A
    if unit == "B" :
        expected_aux_list = periods_dict[period][0] + static_aux_B

    for expected_aux in expected_aux_list:
        aux_found = False
        for element in response_list:
            if expected_aux in element :
                # print( expected_aux , "OK")
                checks[expected_aux] = "OK"
                nbOK+=1
                aux_found = True
                break
        if aux_found == False:
            # print( expected_aux , "KO")
            checks[expected_aux] = "KO"
            nbKO+=1

    # print(period.strip())
    start = period.strip().split('-')[0]
    stop = period.strip().split('-')[1]

    # write report
    file = open(r"outputs/test-%s-%s-%s-%s.txt" % (mission,unit,start,stop),"w")

    #############################################################
    # Demande personnelle de Catherine B. :                     #
    # Afficher en premier les type "R2SWIR", "R2DEPI", "R2EOB2" #
    #############################################################
    file.write("OK : \n")
    for check in sorted(checks.keys()):
        if checks[check] == "OK" and ("R2SWIR" in check or "R2DEPI" in check or "R2EOB2" in check):
            file.write( "%s\t%s" %  (check,checks[check]) )
            file.write( "\n")
            #print( "%s\t\t%s" %  (check,checks[check]) )
    for check in sorted(checks.keys()):
        if checks[check] == "OK" and not ("R2SWIR" in check or "R2DEPI" in check or "R2EOB2" in check):
            file.write( "%s\t%s" %  (check,checks[check]) )
            file.write( "\n")
            #print( "%s\t\t%s" %  (check,checks[check]) )

    file.write("\n\nKO : \n")
    for check in sorted(checks.keys()):
        if checks[check] == "KO":
            file.write( "%s\t%s" %  (check,checks[check]) )
            file.write( "\n")
            #print( "%s\t\t%s" %  (check,checks[check]) )
    
    file.write( "\n")
    file.write( "\n")

    file.write( "Request : \n")
    file.write( "\tGET %s " % request)
    file.write( "\n")
    file.write( "\n")
    for element in response_list:
        file.write("\t fullname : %s " % element )
        file.write( "\n")

    file.write( "\n")
    file.write("Number of expected files : %s\n" % len(checks.keys()))
    file.write("Number of files in response : %s\n" % len(response_list))

    file.write( "\n")
    file.write( "\n")
    
    file.write( "List of files in response which are not expected :\n\n")
    for element in response_list:
        if element.split('.')[0] not in checks.keys():
            file.write("\t%s " % element )
            file.write( "\n")

    file.write("\n\nFor the test to be successful, There must be no KO, there must be the same number of files in the expectations and response and there must be no files in the response that are not expected.")

    file.close()

print("OK : %s" % nbOK)
print("KO : %s" % nbKO)




