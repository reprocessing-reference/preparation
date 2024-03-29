import requests
from openpyxl import load_workbook
from datetime import datetime 
from requests import Request, Session

s = Session()
s.auth = ('user', '*K7KzTrZWhC2zkc')

mission = "S2MSI"

wb = load_workbook("./data/S2_AUX_for_reprocessing_baseline_ESRIN.xlsx")
s2a_sheet = wb['S2A']
s2b_sheet = wb['S2B']

static_aux = [
"S2A_OPER_GIP_R2MACO_MPC__20150605T094742_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_R2MACO_MPC__20170206T103039_V20170101T000000_21000101T000000_BXX",
"S2A_OPER_GIP_ATMIMA_MPC__20150605T094744_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_ATMIMA_MPC__20170206T103051_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_ATMSAD_MPC__20160729T000005_V20150703T000000_21000101T000000_B00",
"S2B_OPER_GIP_ATMSAD_MPC__20170324T155501_V20170306T000000_21000101T000000_B00",
"S2A_OPER_GIP_BLINDP_MPC__20150605T094736_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_BLINDP_MPC__20170221T000000_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_CLOINV_MPC__20151021T225159_V20150701T225159_21000101T000000_B00",
"S2B_OPER_GIP_CLOINV_MPC__20170206T103032_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_CONVER_MPC__20150710T131444_V20150627T000000_21000101T000000_B00",
"S2B_OPER_GIP_CONVER_MPC__20150710T131444_V20150627T000000_21000101T000000_B00",
"S2A_OPER_GIP_DATATI_MPC__20160208T132500_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_DATATI_MPC__20170428T123038_V20170322T000000_21000101T000000_B00",
"S2A_OPER_GIP_DECOMP_MPC__20121031T075922_V19830101T000000_21000101T000000_B00",
"S2B_OPER_GIP_DECOMP_MPC__20121031T075922_V19830101T000000_21000101T000000_B00",
"S2__OPER_GIP_EARMOD_MPC__20150605T094736_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_ECMWFP_MPC__20121031T075922_V19830101T000000_21000101T000000_B00",
"S2B_OPER_GIP_ECMWFP_MPC__20121031T075922_V19830101T000000_21000101T000000_B00",
"S2A_OPER_GIP_G2PARA_MPC__20150605T094736_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_G2PARA_MPC__20170206T103032_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_G2PARE_MPC__20150605T094736_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_G2PARE_MPC__20170206T103032_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_GEOPAR_MPC__20150605T094741_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_GEOPAR_MPC__20170206T103032_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_INVLOC_MPC__20171206T000000_V20150703T000000_21000101T000000_B00",
"S2B_OPER_GIP_INTDET_MPC__20170523T080300_V20170322T000000_21000101T000000_B00",
"S2A_OPER_GIP_INVLOC_MPC__20171206T000000_V20150703T000000_21000101T000000_B00",
"S2B_OPER_GIP_INVLOC_MPC__20170523T080300_V20170322T000000_21000101T000000_B00",
"S2A_OPER_GIP_JP2KPA_MPC__20160222T110000_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_JP2KPA_MPC__20160222T110000_V20150622T000000_21000101T000000_B00",
"S2A_OPER_GIP_LREXTR_MPC__20150605T094736_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_LREXTR_MPC__20170206T103047_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_MASPAR_MPC__20161104T000000_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_MASPAR_MPC__20170206T103032_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_OLQCPA_MPC__20191004T000021_V20191020T233000_21000101T000000_B00",
"S2B_OPER_GIP_OLQCPA_MPC__20191004T000021_V20191021T003000_21000101T000000_B00",
"S2A_OPER_GIP_PRDLOC_MPC__20180301T130000_V20180305T005000_21000101T000000_B00",
"S2B_OPER_GIP_PRDLOC_MPC__20180301T130000_V20180305T014000_21000101T000000_B00",
"S2A_OPER_GIP_PROBAS_MPC__20200221T000209_V20200225T013000_21000101T000000_B00",
"S2B_OPER_GIP_PROBAS_MPC__20200221T000209_V20200225T013000_21000101T000000_B00",
"S2A_OPER_GIP_R2BINN_MPC__20150605T094803_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_R2BINN_MPC__20170206T103032_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_R2CRCO_MPC__20151023T224715_V20150622T224715_21000101T000000_B00",
"S2B_OPER_GIP_R2CRCO_MPC__20170206T103032_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_R2DEBA_MPC__20150605T094741_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_R2DEBA_MPC__20170206T103038_V20170101T000000_21000101T000000_BXX",
"S2A_OPER_GIP_R2DECT_MPC__20150605T094742_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_R2DECT_MPC__20170206T103038_V20170101T000000_21000101T000000_BXX",
"S2A_OPER_GIP_R2DEFI_MPC__20150605T094741_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_R2DEFI_MPC__20170206T103040_V20170101T000000_21000101T000000_BXX",
"S2A_OPER_GIP_R2DENT_MPC__20150605T094742_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_R2DENT_MPC__20170206T103040_V20170101T000000_21000101T000000_BXX",
"S2A_OPER_GIP_R2L2NC_MPC__20150605T094742_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_R2L2NC_MPC__20170206T103040_V20170101T000000_21000101T000000_BXX",
"S2A_OPER_GIP_R2NOMO_MPC__20150605T094803_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_R2NOMO_MPC__20170206T103047_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_R2PARA_MPC__20160303T160000_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_R2PARA_MPC__20170403T000002_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_R2WAFI_MPC__20150605T094742_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_R2WAFI_MPC__20170206T103047_V20170101T000000_21000101T000000_BXX",
"S2A_OPER_GIP_RESPAR_MPC__20150605T094736_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_RESPAR_MPC__20170206T103032_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_TILPAR_MPC__20151209T095117_V20150622T000000_21000101T000000_B00",
"S2B_OPER_GIP_TILPAR_MPC__20170206T103032_V20170101T000000_21000101T000000_B00",
"S2A_OPER_GIP_VIEDIR_MPC__20160208T132500_V20150622T000000_21000101T000000_BXX",
"S2B_OPER_GIP_VIEDIR_MPC__20170512T114736_V20170322T000000_21000101T000000_BXX",
"S2__OPER_GIP_L2ACAC_MPC__20191018T000003_V20150622T000000_21000101T000000_B00",
"S2__OPER_GIP_L2ACSC_MPC__20181003T000002_V20181007T234500_21000101T000000_B00",
"S2__OPER_GIP_PROBA2_MPC__20200221T000214_V20200225T013000_21000101T000000_B00",
"S2__OPER_GIP_L2ACFG_MPC__20200723T120000_V20190506T004000_21000101T000000_B00"]


static_aux_A = []
static_aux_B = []

for aux in static_aux:
    if "S2B" not in aux:
        if "BXX" in aux:
            for m in range(1,13):
                static_aux_A.append( aux.replace("XX","%02d" % m ) )
        else:
            static_aux_A.append(aux)



for aux in static_aux:
    if "S2A" not in aux:
        if "BXX" in aux:
            for m in range(1,13):
                static_aux_B.append( aux.replace("XX","%02d" % m ) )
        else:
            static_aux_B.append(aux)

periods_dict = {}

def read_sheet(sheet,periods_dict):

    nb_periods = len( sheet["B"] ) / 10
    
    for i in range(nb_periods):
        period = ""
        period2 = ""
        unit = sheet['A'][i*10+1].value.strip()
        for pd in sheet['B'][i*10+1:i*10+9]:
            if pd.value is not None:
                period = pd.value
                # print( period )
                if "and" in period:
                    periods = period.strip().split("and")
                    period = periods[0].strip() + "-%s" % unit
                    period2 = periods[1].strip() + "-%s" % unit
                else:
                    period = period.strip() + "-%s" % unit
        
        for aux in sheet['E'][i*10+1:i*10+5] :
            if period not in periods_dict:
                periods_dict[period] = [[],unit]

            aux_name = aux.value.strip()

            if "BXX" in aux_name:
                for m in range(1,13):
                    periods_dict[period][0].append(  aux_name.replace("XX","%02d" % m ) )
            else:
                periods_dict[period][0].append( aux_name )


            if period2 :
                if period2 not in periods_dict:
                    periods_dict[period2] = [[],unit]
                
                if "BXX" in aux_name:
                    for m in range(1,13):
                        periods_dict[period2][0].append(  aux_name.replace("XX","%02d" % m ) )
                else:
                    periods_dict[period2][0].append( aux_name )

            
        
    
read_sheet(s2a_sheet,periods_dict)
read_sheet(s2b_sheet,periods_dict)


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

    for check in sorted(checks.keys()):
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




