import requests
from openpyxl import load_workbook
from datetime import datetime 
from requests import Request, Session

s = Session()
s.auth = ('user', '*K7KzTrZWhC2zkc')

mission = "S3OLCI"

wb = load_workbook("S3OLCI_AUX_for_reprocessing_baseline_ESRIN.xlsx")
sheet = wb['Feuil1']

static_aux = [
"S3A_OL_1_RAC_AX_20160425T103700_20991231T235959_20190320T120000___________________MPC_O_AL_005.SEN3",
"S3B_OL_1_RAC_AX_20180425T000000_20991231T235959_20190320T120000___________________MPC_O_AL_002.SEN3",
"S3A_OL_1_SPC_AX_20160425T103700_20991231T235959_20190320T120000___________________MPC_O_AL_007.SEN3",
"S3B_OL_1_SPC_AX_20180425T000000_20991231T235959_20190320T120000___________________MPC_O_AL_002.SEN3",
"S3A_OL_1_CLUTAX_20160425T095210_20991231T235959_20160525T120000___________________MPC_O_AL_003.SEN3",
"S3B_OL_1_CLUTAX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3",
"S3__AX___CLM_AX_20000101T000000_20991231T235959_20151214T120000___________________MPC_O_AL_001.SEN3",
"S3__AX___DEM_AX_20000101T000000_20991231T235959_20151214T120000___________________MPC_O_AL_001.SEN3",
"S3__AX___LWM_AX_20000101T000000_20991231T235959_20151214T120000___________________MPC_O_AL_001.SEN3",
"S3__AX___OOM_AX_20000101T000000_20991231T235959_20151214T120000___________________MPC_O_AL_001.SEN3",
"S3__AX___TRM_AX_20000101T000000_20991231T235959_20151214T120000___________________MPC_O_AL_001.SEN3",
"S3A_OL_2_PCP_AX_20160216T000000_20991231T235959_20170609T120000___________________MPC_O_AL_002.SEN3",
"S3B_OL_2_PCP_AX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3",
"S3A_OL_2_PPP_AX_20160216T000000_20991231T235959_20170609T120000___________________MPC_O_AL_005.SEN3",
"S3B_OL_2_PPP_AX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3",
"S3A_OL_2_WVP_AX_20160216T000000_20991231T235959_20170113T120000___________________MPC_O_AL_003.SEN3",
"S3B_OL_2_WVP_AX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3",
"S3A_OL_2_ACP_AX_20160216T000000_20991231T235959_20170609T120000___________________MPC_O_AL_004.SEN3",
"S3B_OL_2_ACP_AX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3",
"S3A_OL_2_OCP_AX_20160216T000000_20991231T235959_20170609T120000___________________MPC_O_AL_003.SEN3",
"S3B_OL_2_OCP_AX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3",
"S3A_OL_2_VGP_AX_20160216T000000_20991231T235959_20170113T120000___________________MPC_O_AL_004.SEN3",
"S3B_OL_2_VGP_AX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3",
"S3A_OL_2_CLP_AX_20160216T000000_20991231T235959_20170210T120000___________________MPC_O_AL_003.SEN3",
"S3B_OL_2_CLP_AX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3"]


static_aux_A = []
static_aux_B = []

for aux in static_aux:
    if "S3B" not in aux:
        static_aux_A.append(aux)

for aux in static_aux:
    if "S3A" not in aux:
        static_aux_B.append(aux)


periods_dict = {}

for i in range(10):
    period = ""
    period2 = ""
    unit = sheet['A'][i*7+1].value.strip()
    for pd in sheet['B'][i*7+1:i*7+7]:
        if pd.value is not None:
            period = pd.value
            # print( period )
            if "and" in period:
                periods = period.strip().split("and")
                period = periods[0].strip() + "-%s" % unit
                period2 = periods[1].strip() + "-%s" % unit
            else:
                period = period.strip() + "-%s" % unit
    
    for aux in sheet['H'][i*7+1:i*7+7] :
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
    request="https://reprocessing-preparation.ml/reprocessing.svc/GetReproBaselineNamesForPeriod(Mission='%s',Unit='%s',SensingTimeStart='%s',SensingTimeStop='%s')" % (mission,unit,start,stop)

    # print(request)

    r = s.get(request)
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




