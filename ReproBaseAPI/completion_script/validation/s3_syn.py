import requests
from openpyxl import load_workbook
from datetime import datetime 
from requests import Request, Session

s = Session()
s.auth = ('user', '*K7KzTrZWhC2zkc')

mission="S3SYN"

periods_dict = {}


static_aux =[
"S3A_SY_1_PCP_AX_20160216T000000_20991231T235959_20170120T120000___________________MPC_O_AL_005.SEN3",
"S3B_SY_1_PCP_AX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3",
"S3A_SY_1_GCPBAX_20160216T000000_20991231T235959_20170120T120000___________________MPC_O_AL_003.SEN3",
"S3B_SY_1_GCPBAX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3",
"S3A_OL_1_MCHDAX_20160216T000000_20991231T235959_20170120T120000___________________MPC_O_AL_003.SEN3",
"S3B_OL_1_MCHDAX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3",
"S3A_SL_1_MCHDAX_20160216T000000_20991231T235959_20170120T120000___________________MPC_O_AL_003.SEN3",
"S3B_SL_1_MCHDAX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3",
"S3__SY_1_CDIBAX_20000101T000000_20991231T235959_20151214T120000___________________MPC_O_AL_001.SEN3",
"S3A_SY_2_PCP_AX_20160216T000000_20991231T235959_20181207T120000___________________MPC_O_AL_005.SEN3",
"S3B_SY_2_PCP_AX_20180425T000000_20991231T235959_20181207T120000___________________MPC_O_AL_003.SEN3",
"S3A_SY_2_RAD_AX_20160216T000000_20991231T235959_20190912T120000___________________MPC_O_AL_003.SEN3",
"S3B_SY_2_RAD_AX_20180425T000000_20991231T235959_20190912T120000___________________MPC_O_AL_002.SEN3",
"S3A_SY_2_RADPAX_20160216T000000_20991231T235959_20190912T120000___________________MPC_O_AL_002.SEN3",
"S3B_SY_2_RADPAX_20180425T000000_20991231T235959_20190912T120000___________________MPC_O_AL_002.SEN3",
"S3A_SY_2_SPCPAX_20000101T000000_20991231T235959_20151214T120000___________________MPC_O_AL_001.SEN3",
"S3B_SY_2_SPCPAX_20180425T000000_20991231T235959_20180409T120000___________________MPC_O_AL_001.SEN3",
"S3A_SY_2_RADSAX_20160216T000000_20991231T235959_20190912T120000___________________MPC_O_AL_002.SEN3",
"S3B_SY_2_RADSAX_20180425T000000_20991231T235959_20190912T120000___________________MPC_O_AL_002.SEN3",
"S3A_SY_2_PCPSAX_20160216T000000_20991231T235959_20181207T120000___________________MPC_O_AL_002.SEN3",
"S3B_SY_2_PCPSAX_20180425T000000_20991231T235959_20181207T120000___________________MPC_O_AL_002.SEN3",
"S3__SY_2_ACLMAX_20160216T000000_20991231T235959_20190930T120000___________________MPC_O_AL_002.SEN3",
"S3__SY_2_ART_AX_20160216T000000_20991231T235959_20190930T120000___________________MPC_O_AL_002.SEN3",
"S3__SY_2_LSR_AX_20160216T000000_20991231T235959_20190930T120000___________________MPC_O_AL_002.SEN3",
"S3__SY_2_OSR_AX_20160216T000000_20991231T235959_20190930T120000___________________MPC_O_AL_002.SEN3",
"S3__SY_2_AODCAX_20000101T000000_20991231T235959_20180704T120000___________________MPC_O_AL_001.SEN3",
"S3A_SY_2_PCPAAX_20160216T000000_20991231T235959_20190930T120000___________________MPC_O_AL_002.SEN3",
"S3B_SY_2_PCPAAX_20180425T000000_20991231T235959_20190930T120000___________________MPC_O_AL_001.SEN3",
]

static_aux_A = []
static_aux_B = []

for aux in static_aux:
    if "S3B" not in aux:
        static_aux_A.append(aux)

for aux in static_aux:
    if "S3A" not in aux:
        static_aux_B.append(aux)


periods_dict["20160216T000000-20990101T000000-A"] = [ static_aux_A ,'A']
periods_dict["20180425T000000-20990101T000000-B"] = [ static_aux_B ,'B']


    
for period in periods_dict :

    checks = {}

    print(period.strip())
    start = period.strip().split('-')[0]
    stop = period.strip().split('-')[1]

    start = datetime.strptime(start, '%Y%m%dT%H%M%S').strftime('%Y-%m-%dT%H:%M:%SZ')
    stop = datetime.strptime(stop, '%Y%m%dT%H%M%S').strftime('%Y-%m-%dT%H:%M:%SZ')

    unit = periods_dict[period][1]
    request="https://reprocessing-auxiliary.copernicus.eu/reprocessing.svc/GetReproBaselineNamesForPeriod(Mission='%s',Unit='%s',SensingTimeStart='%s',SensingTimeStop='%s')" % (mission,unit,start,stop)

    # print(request)

    r = s.get(request)
    out_list = r.json()['value']

    for expected_aux in periods_dict[period][0]:
        aux_found = False
        for element in out_list:
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
    for element in out_list:
        file.write("\t fullname : %s " % element )
        file.write( "\n")

    file.close()




