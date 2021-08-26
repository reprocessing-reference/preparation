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

DEBUG = True

time_dependency_dict = {
    "AUX_ECMWFD": "ValidityPeriod",
    "R2ABCA": "ValidityPeriod",
    "R2MACO": "AnyTime",
    "ATMIMA": "AnyTime",
    "ATMSAD": "AnyTime",
    "BLINDP": "AnyTime",
    "CLOINV": "AnyTime",
    "CONVER": "AnyTime",
    "DATATI": "AnyTime",
    "DECOMP": "AnyTime",
    "EARMOD": "AnyTime",
    "ECMWFP": "AnyTime",
    "G2PARA": "AnyTime",
    "G2PARE": "AnyTime",
    "GEOPAR": "AnyTime",
    "INTDET": "AnyTime",
    "INVLOC": "AnyTime",
    "JP2KPA": "AnyTime",
    "LREXTR": "AnyTime",
    "MASPAR": "AnyTime",
    "OLQCPA": "AnyTime",
    "PRDLOC": "AnyTime",
    "PROBAS": "AnyTime",
    "R2BINN": "AnyTime",
    "R2CRCO": "AnyTime",
    "R2DEBA": "AnyTime",
    "R2DECT": "AnyTime",
    "R2DEFI": "AnyTime",
    "R2DENT": "AnyTime",
    "R2DEPI": "ValidityPeriod",
    "R2EOB2": "ValidityPeriod",
    "R2EQOG": "ValidityPeriod",
    "R2L2NC": "AnyTime",
    "R2NOMO": "AnyTime",
    "R2PARA": "AnyTime",
    "R2SWIR": "ValidityPeriod",
    "R2WAFI": "AnyTime",
    "RESPAR": "AnyTime",
    "SPAMOD": "ValidityPeriod",
    "TILPAR": "AnyTime",
    "VIEDIR": "AnyTime",
    "AUX_PP1": "ValidityPeriod",
    "AUX_PP2": "ValidityPeriod",
    "AUX_CAL": "ValidityPeriod",
    "AUX_INS": "ValidityPeriod",
    "AUX_UT1UTC": "ValidityPeriod",
    "AUX_POE": "ValidityPeriod",
    "AUX_ATT": "ValidityPeriod",
    "AUX_SCS": "ValidityPeriod",
    "AUX_WND": "ValidityPeriod",
    "AUX_WAV": "ValidityPeriod",
    "AUX_ICE": "ValidityPeriod",
    "AMH_ERRMAT": "ValidityPeriod",
    "L2ACAC": "AnyTime",
    "L2ACSC": "AnyTime",
    "PROBA2": "AnyTime",
    "L2ACFG": "AnyTime",
    "MPL_ORBRES": "ValidityPeriod",
    "MPL_ORBPRE": "ValidityPeriod",
    "AUX_TIM": "ValidityPeriod",
    "AUX_ECE": "ValidityPeriod",
    "AX___MF1_AX": "ValidityPeriod",
    "AX___MA1_AX": "ValidityPeriod",
    "AX___MA2_AX": "ValidityPeriod",
    "AX___MF2_AX": "ValidityPeriod",
    "AX___MFA_AX": "ValidityPeriod",
    "AX___CST_AX": "AnyTime",
    "AX___DEM_AX": "AnyTime",
    "AX___LWM_AX": "AnyTime",
    "AX___OOM_AX": "AnyTime",
    "AX___CLM_AX": "AnyTime",
    "AX___TRM_AX": "AnyTime",
    "AX___BB2_AX": "ValidityPeriod",
    "AX___BA__AX": "ValidityPeriod",
    "OL_2_PCP_AX": "AnyTime",
    "OL_2_PPP_AX": "AnyTime",
    "OL_2_WVP_AX": "AnyTime",
    "OL_2_ACP_AX": "AnyTime",
    "OL_2_OCP_AX": "AnyTime",
    "OL_2_VGP_AX": "AnyTime",
    "OL_2_CLP_AX": "AnyTime",
    "SR_1_USO_AX": "ValidityPeriod",
    "SR_2_CON_AX": "AnyTime",
    "SR___LSM_AX": "AnyTime",
    "SR_2_PMO_AX": "ValidityPeriod",
    "SR_2_RMO_AX": "ValidityPeriod",
    "SR_2_POL_AX": "ValidityPeriod",
    "SR_2_PGI_AX": "ValidityPeriod",
    "SR_2_RGI_AX": "ValidityPeriod",
    "SR_2_SIC_AX": "ValidityPeriod",
    "SR_2_LRC_AX": "AnyTime",
    "SR_2_SST_AX": "AnyTime",
    "SR_2_SFL_AX": "AnyTime",
    "SR_2_FLT_AX": "AnyTime",
    "SR_2_CCT_AX": "AnyTime",
    "SR_2_RRC_AX": "AnyTime",
    "SR_2_EOT1AX": "AnyTime",
    "SR_2_EOT2AX": "AnyTime",
    "SR_2_LT1_AX": "AnyTime",
    "SR_2_LT2_AX": "AnyTime",
    "SR_2_LNEQAX": "AnyTime",
    "SR_2_MSS1AX": "AnyTime",
    "SR_2_MSS2AX": "AnyTime",
    "SR_2_GEO_AX": "AnyTime",
    "SR_2_ODLEAX": "AnyTime",
    "SR_2_WNDLAX": "AnyTime",
    "SR_2_WNDSAX": "AnyTime",
    "SR_2_SIGLAX": "AnyTime",
    "SR_2_SIGSAX": "AnyTime",
    "SR_2_SET_AX": "AnyTime",
    "SR_2_SSM_AX": "AnyTime",
    "SR_2_MSMGAX": "AnyTime",
    "SR_2_CP00AX": "AnyTime",
    "SR_2_CP06AX": "AnyTime",
    "SR_2_CP12AX": "AnyTime",
    "SR_2_CP18AX": "AnyTime",
    "SR_2_S1AMAX": "AnyTime",
    "SR_2_S2AMAX": "AnyTime",
    "SR_2_S1PHAX": "AnyTime",
    "SR_2_S2PHAX": "AnyTime",
    "SR_2_MDT_AX": "AnyTime",
    "SR_2_SHD_AX": "AnyTime",
    "SR_2_SSBLAX": "AnyTime",
    "SR_2_SSBSAX": "AnyTime",
    "SR_2_SD01AX": "AnyTime",
    "SR_2_SD02AX": "AnyTime",
    "SR_2_SD03AX": "AnyTime",
    "SR_2_SD04AX": "AnyTime",
    "SR_2_SD05AX": "AnyTime",
    "SR_2_SD06AX": "AnyTime",
    "SR_2_SD07AX": "AnyTime",
    "SR_2_SD08AX": "AnyTime",
    "SR_2_SD09AX": "AnyTime",
    "SR_2_SD10AX": "AnyTime",
    "SR_2_SD11AX": "AnyTime",
    "SR_2_SD12AX": "AnyTime",
    "SR_2_SI01AX": "AnyTime",
    "SR_2_SI02AX": "AnyTime",
    "SR_2_SI03AX": "AnyTime",
    "SR_2_SI04AX": "AnyTime",
    "SR_2_SI05AX": "AnyTime",
    "SR_2_SI06AX": "AnyTime",
    "SR_2_SI07AX": "AnyTime",
    "SR_2_SI08AX": "AnyTime",
    "SR_2_SI09AX": "AnyTime",
    "SR_2_SI10AX": "AnyTime",
    "SR_2_SI11AX": "AnyTime",
    "SR_2_SI12AX": "AnyTime",
    "SR_2_SURFAX": "AnyTime",
    "SY_1_PCP_AX": "AnyTime",
    "MPL_ORBSCT": "AnyTime",
    "AUX_RES": "ValidityPeriod",
    "AUX_PRE": "ValidityPeriod",
    "1_AX_TPDB": "AnyTime",
    "1_AX_OLIC": "AnyTime",
    "1_AX_SLIC": "AnyTime",
    "SY_1_CDIBAX": "AnyTime",
    "SY_2_SYCPAX": "AnyTime",
    "SY_2_SYRTAX": "AnyTime",
    "SY_2_VPRTAX": "AnyTime",
    "SY_2_VPSRAX": "AnyTime",
    "SY_2_VSRTAX": "AnyTime",
    "GIP_L2A_SC": "AnyTime",
    "GIP_L2A_AC": "AnyTime",
    "GIP_L2A": "AnyTime",
    "GIP_L2A_USR": "AnyTime",
    "GIP_L2A_PB": "AnyTime",
    "AUX_DEM": "ValidityPeriod",
    "AUX_DEM": "ValidityPeriod",
    "AUX_DEM": "ValidityPeriod",
    "AX___FRO_AX": "ValidityPeriod",
    "AX___FPO_AX": "ValidityPeriod",
    "AX___OSF_AX": "AnyTime",
    "SY_1_GCPBAX": "AnyTime",
    "OL_1_MCHDAX": "ValidityPeriod",
    "SL_1_MCHDAX": "ValidityPeriod",
    "SY_2_PCP_AX": "AnyTime",
    "SY_2_RAD_AX": "AnyTime",
    "SY_2_RADPAX": "AnyTime",
    "SY_2_SPCPAX": "AnyTime",
    "SY_2_RADSAX": "AnyTime",
    "L2A_GIPP": "ValidityPeriod",
    "DEM_GLOBEF": "ValidityPeriod",
    "DEM_SRTMFO": "ValidityPeriod",
    "DEM_GEOIDF": "ValidityPeriod",
    "AUX_GRIXXX": "ValidityPeriod",
    "PRD_HKTM__": "ValidityPeriod",
    "AUX_ATMCOR": "ValidityPeriod",
    "SL_1_PCP_AX": "AnyTime",
    "SL_1_ANC_AX": "AnyTime",
    "SL_1_N_S7AX": "AnyTime",
    "SL_1_N_S8AX": "AnyTime",
    "SL_1_N_S9AX": "AnyTime",
    "SL_1_N_F1AX": "AnyTime",
    "SL_1_N_F2AX": "AnyTime",
    "SL_1_O_S7AX": "AnyTime",
    "SL_1_O_S8AX": "AnyTime",
    "SL_1_O_S9AX": "AnyTime",
    "SL_1_O_F1AX": "AnyTime",
    "SL_1_O_F2AX": "AnyTime",
    "SL_1_N_S1AX": "AnyTime",
    "SL_1_N_S2AX": "AnyTime",
    "SL_1_N_S3AX": "AnyTime",
    "SL_1_O_S1AX": "AnyTime",
    "SL_1_O_S2AX": "AnyTime",
    "SL_1_O_S3AX": "AnyTime",
    "SL_1_NAS4AX": "AnyTime",
    "SL_1_NAS5AX": "AnyTime",
    "SL_1_NAS6AX": "AnyTime",
    "SL_1_NBS4AX": "AnyTime",
    "SL_1_NBS5AX": "AnyTime",
    "SL_1_NBS6AX": "AnyTime",
    "SL_1_OAS4AX": "AnyTime",
    "SL_1_OAS5AX": "AnyTime",
    "SL_1_OAS6AX": "AnyTime",
    "SL_1_OBS4AX": "AnyTime",
    "SL_1_OBS5AX": "AnyTime",
    "SL_1_OBS6AX": "AnyTime",
    "SL_1_VSC_AX": "ValidityPeriod",
    "SL_1_VIC_AX": "AnyTime",
    "SL_1_GEO_AX": "AnyTime",
    "SL_1_GEC_AX": "ValidityPeriod",
    "SL_1_ESSTAX": "AnyTime",
    "SL_1_CLO_AX": "AnyTime",
    "SL_2_PCP_AX": "AnyTime",
    "SL_2_S6N_AX": "AnyTime",
    "OL_1_EO__AX": "ValidityPeriod",
    "OL_1_RAC_AX": "ValidityPeriod",
    "OL_1_SPC_AX": "ValidityPeriod",
    "OL_1_CLUTAX": "ValidityPeriod",
    "OL_1_INS_AX": "ValidityPeriod",
    "OL_1_CAL_AX": "ValidityPeriod",
    "OL_1_PRG_AX": "ValidityPeriod",
    "SL_2_S7N_AX": "AnyTime",
    "SL_2_S8N_AX": "AnyTime",
    "SL_2_S9N_AX": "AnyTime",
    "SL_2_F1N_AX": "AnyTime",
    "SL_2_F2N_AX": "ValidityPeriod",
    "SL_2_S7O_AX": "AnyTime",
    "SL_2_S8O_AX": "AnyTime",
    "SL_2_S9O_AX": "AnyTime",
    "SL_2_N2_CAX": "AnyTime",
    "SL_2_N3RCAX": "AnyTime",
    "SL_2_N3_CAX": "AnyTime",
    "SL_2_D2_CAX": "AnyTime",
    "SL_2_D3_CAX": "AnyTime",
    "SL_2_SST_AX": "AnyTime",
    "SL_2_SDI3AX": "AnyTime",
    "SL_2_SDI2AX": "AnyTime",
    "SL_2_SSESAX": "AnyTime",
    "SL_2_SSTAAX": "ValidityPeriod",
    "SL_2_LSTCAX": "ValidityPeriod",
    "SL_2_LSTBAX": "ValidityPeriod",
    "SL_2_LSTVAX": "ValidityPeriod",
    "SL_2_LSTWAX": "ValidityPeriod",
    "SL_2_LSTEAX": "ValidityPeriod",
    "SL_2_FRPTAX": "ValidityPeriod",
    "SY_2_PCPSAX": "AnyTime",
    "SR___ROE_AX": "ValidityPeriod",
    "SR___MGNPAX": "ValidityPeriod",
    "SR___MGNSAX": "ValidityPeriod",
    "SR___MDO_AX": "ValidityPeriod",
    "SR___POEPAX": "ValidityPeriod",
    "SR___POESAX": "ValidityPeriod",
    "SR___CHDNAX": "ValidityPeriod",
    "SR___CHDRAX": "ValidityPeriod",
    "SR_1_CA1LAX": "ValidityPeriod",
    "SR_1_CA1SAX": "ValidityPeriod",
    "SR_1_CA2KAX": "ValidityPeriod",
    "SR_1_CA2CAX": "ValidityPeriod",
    "SR_1_CONCAX": "AnyTime",
    "SR_1_CONMAX": "AnyTime",
    "SR_2_NRPPAX": "ValidityPeriod",
    "SR_2_PMPPAX": "ValidityPeriod",
    "SR_2_PCPPAX": "ValidityPeriod",
    "SR_2_PMPSAX": "ValidityPeriod",
    "SR_2_PCPSAX": "ValidityPeriod",
    "SR_2_MAG_AX": "AnyTime",
    "SR_2_IC01AX": "AnyTime",
    "SR_2_IC02AX": "AnyTime",
    "SR_2_IC03AX": "AnyTime",
    "SR_2_IC04AX": "AnyTime",
    "SR_2_IC05AX": "AnyTime",
    "SR_2_IC06AX": "AnyTime",
    "SR_2_IC07AX": "AnyTime",
    "SR_2_IC08AX": "AnyTime",
    "SR_2_IC09AX": "AnyTime",
    "SR_2_IC10AX": "AnyTime",
    "SR_2_RET_AX": "AnyTime",
    "SR_2_LUTFAX": "AnyTime",
    "SR_2_LUTEAX": "AnyTime",
    "SR_2_LUTSAX": "AnyTime",
    "SR_2_MLM_AX": "AnyTime",
    "MW_1_SLC_AX": "AnyTime",
    "MW___CHDNAX": "AnyTime",
    "MW___CHDRAX": "AnyTime",
    "MW___STD_AX": "AnyTime",
    "MW_1_NIR_AX": "ValidityPeriod",
    "MW_1_DNB_AX": "ValidityPeriod",
    "MW_1_MON_AX": "ValidityPeriod",
    "OL_1_PCPBAX": "ValidityPeriod",
    "OL_1_PLTBAX": "ValidityPeriod",
    "SL_2_ACLMAX": "ValidityPeriod",
    "SL_2_ART_AX": "ValidityPeriod",
    "SL_2_OSR_AX": "ValidityPeriod",
    "SL_2_PCPAAX": "ValidityPeriod",
    "SL_2_CFM_AX": "AnyTime",
    "SL_2_FXPAAX": "AnyTime",
    "SL_2_PCPFAX": "AnyTime",
    "SL_2_PLFMAX": "AnyTime",
    "SL_2_SXPAAX": "AnyTime",
    "SL_1_IRE_AX": "AnyTime",
    "SL_1_LCC_AX": "AnyTime",
    "SL_1_CDP_AX": "AnyTime",
    "SL_1_CLP_AX": "AnyTime",
    "SL_1_ADJ_AX": "AnyTime",
    "SL_1_RTT_AX": "AnyTime",
    "SL_1_PCPBAX": "AnyTime",
    "SL_1_PLTBAX": "AnyTime",
    "OL_2_PCPBAX": "AnyTime",
    "OL_2_PLTBAX": "AnyTime",
    "SL_2_PCPBAX": "AnyTime",
    "SL_2_PLTBAX": "AnyTime",
    "SY_2_ACLMAX": "AnyTime",
    "SY_2_ART_AX": "AnyTime",
    "SY_2_LSR_AX": "AnyTime",
    "SY_2_OSR_AX": "AnyTime",
    "SY_2_AODCAX": "AnyTime",
    "SY_2_PCPAAX": "AnyTime",
    "SY_2_PCPBAX": "AnyTime",
    "SY_2_PLTBAX": "AnyTime",
    "SY_2_CVPBAX": "AnyTime",
    "SY_2_PVPBAX": "AnyTime",
    "SY_2_CVSBAX": "AnyTime",
    "SY_2_PVSBAX": "AnyTime",
    "AUX_RDB____MPC": "ValidityPeriod",
    "AMV_ERRMAT": "ValidityPeriod",
    "ASA_XCH": "ValidityPeriod",
    "ASA_XCA": "ValidityPeriod",
    "ASA_INS": "ValidityPeriod",
    "AUX_OBMEMC": "ValidityPeriod",
    "AUX_ECMWFT": "ValidityPeriod",
    "AUX_CAMSAN": "ValidityPeriod",
    "AUX_CAMSRE": "ValidityPeriod",
    "AUX_SADATA": "ValidityPeriod",
    "AUX_SXXYYY": "ValidityPeriod",
    "AUX_GPS___": "ValidityPeriod",
    "AUX_ROE_AX": "ValidityPeriod",
    "AUX_MOEORB": "ValidityPeriod"
}

odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"
service_datetime_format = "%Y-%m-%dT%H:%M:%SZ"
service_datetime_format2 = "%Y-%m-%dT%H:%MZ"
def strtime(strin):
    try:
        return datetime.datetime.strptime(strin,odata_datetime_format)
    except ValueError:
        try:
            return datetime.datetime.strptime(strin, service_datetime_format)
        except ValueError:
            return datetime.datetime.strptime(strin, service_datetime_format2)



def treatOneDict(dict_file, input, output, offset=0):
    dict_band = {}
    for s in dict_file:
        if s[1]["Band"] in dict_band.keys():
            dict_band[s[1]["Band"]].append(s)
        else:
            dict_band[s[1]["Band"]] = []
            dict_band[s[1]["Band"]].append(s)
    for band, files in dict_band.items():
        if len(files) == 1:
            if DEBUG:
                print("only one file for band : " + band)
                print("not updating : " + files[0][0])
            with open(os.path.join(output, files[0][0]), 'w') as json_file:
                json.dump(files[0][1], json_file)
            #shutil.copyfile(os.path.join(input, files[0][0]), os.path.join(output, files[0][0]))
            continue
        if DEBUG:
            print("Band " + band)
        found = False
        for k, v in time_dependency_dict.items():
            if 'ShortName' in files[0][1].keys():
                if k in files[0][1]['ShortName']:
                    time_validity = v
                    found = True
                    break
            elif 'AuxType' in files[0][1].keys():
                if k in files[0][1]['AuxType']['ShortName']:
                    time_validity = v
                    found = True
                    break
        if not found:
            raise Exception("One filetype is not associated with a TimeValididty :" + files[0][1]['ShortName'])
        if time_validity == "AnyTime":
            l_sorted = sorted(files, key=lambda x: strtime(x[1]["CreationDate"]))
            if DEBUG:
                print("Only writing : " + l_sorted[-1][0])
            l_sorted[-1][1]["SensingTimeApplicationStart"] = "1983-01-01T00:00:00.000000Z"
            l_sorted[-1][1]["SensingTimeApplicationStop"] = "2100-01-01T00:00:00.000000Z"
            # Write down
            with open(os.path.join(output, l_sorted[-1][0]), 'w') as json_file:
                json.dump(l_sorted[-1][1], json_file)
            #shutil.copyfile(os.path.join(input, l_sorted[-1][0]), os.path.join(output, l_sorted[-1][0]))
        else:
            l_sorted = sorted(files,
                              key=lambda x: strtime(x[1]["ValidityStart"]))
            if DEBUG:
                print(len(l_sorted))
            for idx in range(len(l_sorted) - 1):
                fifi = l_sorted[idx]
                dt_file_stop = strtime(fifi[1]["ValidityStop"])
                dt_file_creation = strtime(fifi[1]["CreationDate"])
                dt_file_start = strtime(fifi[1]["ValidityStart"])
                fofo = l_sorted[idx + 1]
                nt_file_stop = strtime(fofo[1]["ValidityStop"])
                nt_file_creation = strtime(fofo[1]["CreationDate"])
                nt_file_start = strtime(fofo[1]["ValidityStart"])
                if isTheLatest(fifi, idx, l_sorted):
                    n = 2
                    while ((idx + n) < len(l_sorted) and dt_file_start == nt_file_start):
                        fofo = l_sorted[idx + n]
                        n = n + 1
                        nt_file_stop = strtime(fofo[1]["ValidityStop"])
                        nt_file_creation = strtime(fofo[1]["CreationDate"])
                        nt_file_start = strtime(fofo[1]["ValidityStart"])
                    dt_sensing_start = fifi[1]["ValidityStart"]
                    if dt_file_stop >= nt_file_start != dt_file_start:
                        dt_sensing_stop = fofo[1]["ValidityStart"]
                    else:
                        dt_sensing_stop = fifi[1]["ValidityStop"]
                    if DEBUG:
                        print("Sensing validity for file : " + fifi[
                            0] + " : " + dt_sensing_start + " : " + dt_sensing_stop)
                    fifi[1]["SensingTimeApplicationStart"] = dt_sensing_start
                    fifi[1]["SensingTimeApplicationStop"] = dt_sensing_stop
                    # Write down
                    with open(os.path.join(output, fifi[0]), 'w') as json_file:
                        json.dump(fifi[1], json_file)
            if isTheLatest(l_sorted[-1], len(l_sorted) - 1, l_sorted):
                dt_sensing_start_last = l_sorted[-1][1]["ValidityStart"]
                dt_sensing_stop_last = l_sorted[-1][1]["ValidityStop"]
                if DEBUG:
                    print("Sensing validity for last file : " + l_sorted[-1][
                        0] + " : " + dt_sensing_start_last + " : " + dt_sensing_stop_last)
                l_sorted[-1][1]["SensingTimeApplicationStart"] = dt_sensing_start_last
                l_sorted[-1][1]["SensingTimeApplicationStop"] = dt_sensing_stop_last
                # Write down
                with open(os.path.join(output, l_sorted[-1][0]), 'w') as json_file:
                    json.dump(l_sorted[-1][1], json_file)


def treatOneDictERRMAT(dict_file, input, output):
    dict_band = {}
    for s in dict_file:
        if s[1]["Band"] in dict_band.keys():
            dict_band[s[1]["Band"]].append(s)
        else:
            dict_band[s[1]["Band"]] = []
            dict_band[s[1]["Band"]].append(s)
    for band, files in dict_band.items():
        if len(files) == 1:
            if DEBUG:
                print("only one file for band : " + band)
                print("not updating : " + files[0][0])
            with open(os.path.join(output, files[0][0]), 'w') as json_file:
                json.dump(files[0][1], json_file)
            #shutil.copyfile(os.path.join(input, files[0][0]), os.path.join(output, files[0][0]))
            continue
        if DEBUG:
            print("Band " + band)
        time_validity = "ValidityPeriod"
        l_sorted = sorted(files,
                          key=lambda x: strtime(x[1]["ValidityStop"]))
        for idx in range(len(l_sorted) - 1):
            fifi = l_sorted[idx]
            dt_file_stop = strtime(fifi[1]["ValidityStop"])
            dt_file_creation = strtime(fifi[1]["CreationDate"])
            dt_file_start = strtime(fifi[1]["ValidityStart"])
            fofo = l_sorted[idx + 1]
            nt_file_stop = strtime(fofo[1]["ValidityStop"])
            nt_file_creation = strtime(fofo[1]["CreationDate"])
            nt_file_start = strtime(fofo[1]["ValidityStart"])
            fifi[1]["SensingTimeApplicationStart"] = fifi[1]["ValidityStop"]
            fifi[1]["SensingTimeApplicationStop"] = fofo[1]["ValidityStop"]
            if isTheLatest(fifi, idx, l_sorted):
                n = 2
                while ((idx + n) < len(l_sorted) and dt_file_stop == nt_file_stop):
                    fofo = l_sorted[idx + n]
                    n = n + 1
                    nt_file_stop = strtime(fofo[1]["ValidityStop"])
                    nt_file_creation = strtime(fofo[1]["CreationDate"])
                    nt_file_start = strtime(fofo[1]["ValidityStart"])
                fifi[1]["SensingTimeApplicationStart"] = fifi[1]["ValidityStop"]
                fifi[1]["SensingTimeApplicationStop"] = fofo[1]["ValidityStop"]
                if DEBUG:
                    print("Sensing validity for file : " + fifi[
                        0] + " : " + fifi[1]["SensingTimeApplicationStart"] + " : " + fifi[1]["SensingTimeApplicationStop"])
                # Write down
                with open(os.path.join(output, fifi[0]), 'w') as json_file:
                    json.dump(fifi[1], json_file)
        dt_sensing_start_last = l_sorted[-1][1]["ValidityStart"]
        dt_sensing_stop_last = l_sorted[-1][1]["ValidityStop"]
        if DEBUG:
            print("Sensing validity for last file : " + l_sorted[-1][
                0] + " : " + dt_sensing_stop_last + " : " + "2100-01-01T00:00:00.000000Z")
        l_sorted[-1][1]["SensingTimeApplicationStart"] = dt_sensing_stop_last
        l_sorted[-1][1]["SensingTimeApplicationStop"] = "2100-01-01T00:00:00.000000Z"
        if isTheLatest(l_sorted[-1], len(l_sorted) - 1, l_sorted):
            # Write down
            with open(os.path.join(output, l_sorted[-1][0]), 'w') as json_file:
                json.dump(l_sorted[-1][1], json_file)


def isTheLatest(file, idx, sorted_list):
    dt_file_stop = strtime(file[1]["ValidityStop"])
    dt_file_creation = strtime(file[1]["CreationDate"])
    dt_file_start = strtime(file[1]["ValidityStart"])
    tmp_idx = idx - 1
    while tmp_idx >= 0:
        nt_file_stop = strtime(sorted_list[tmp_idx][1]["ValidityStop"])
        nt_file_creation = strtime(sorted_list[tmp_idx][1]["CreationDate"])
        nt_file_start = strtime(sorted_list[tmp_idx][1]["ValidityStart"])
        if nt_file_start == dt_file_start and nt_file_stop == dt_file_stop:
            if DEBUG:
                print("Two files have same properties : " + file[0] + " : " + sorted_list[tmp_idx][0])
            if dt_file_creation < nt_file_creation:
                if DEBUG:
                    print("Not the latest : " + file[0])
                return False
        else:
            break
        tmp_idx = tmp_idx - 1
    tmp_idx = idx + 1
    while tmp_idx < len(sorted_list):
        nt_file_stop = strtime(sorted_list[tmp_idx][1]["ValidityStop"])
        nt_file_creation = strtime(sorted_list[tmp_idx][1]["CreationDate"])
        nt_file_start = strtime(sorted_list[tmp_idx][1]["ValidityStart"])
        if nt_file_start == dt_file_start and nt_file_stop == dt_file_stop:
            if DEBUG:
                print("Two files have same properties : " + file[0] + " : " + sorted_list[tmp_idx][0])
            if dt_file_creation < nt_file_creation:
                if DEBUG:
                    print("Not the latest : " + file[0])
                return False
        else:
            break
        tmp_idx = tmp_idx + 1
    # No one to prove the contrary
    if DEBUG:
        print("Latest : " + file[0])
    return True


def main():
    parser = argparse.ArgumentParser(description="",  # main description for help
                                     epilog='Beta',
                                     formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
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
            if DEBUG:
                print("Treating " + filename + " : " + str(idx) + " / " + str(len(filenames)))
            auxfile = None
            with open(os.path.join(args.input, filename)) as f:
                try:
                    auxfile = json.load(f)
                except Exception as e:
                    raise Exception("Could not open : " + os.path.join(args.input, filename))
            if "AuxType@odata.bind" in auxfile.keys():
                if auxfile["AuxType@odata.bind"] in dict_type_file.keys():
                    dict_type_file[auxfile["AuxType@odata.bind"]].append((filename, auxfile.copy()))
                else:
                    dict_type_file[auxfile["AuxType@odata.bind"]] = []
                    dict_type_file[auxfile["AuxType@odata.bind"]].append((filename, auxfile.copy()))
            elif "AuxType" in auxfile.keys():
                auxfile["AuxType@odata.bind"]="AuxTypes('" + auxfile["AuxType"]["LongName"] + "')"
                if "AuxType("+auxfile["AuxType"]["LongName"] in dict_type_file.keys():
                    dict_type_file["AuxType("+auxfile["AuxType"]["LongName"]].append((filename, auxfile.copy()))
                else:
                    dict_type_file["AuxType("+auxfile["AuxType"]["LongName"]] = []
                    dict_type_file["AuxType("+auxfile["AuxType"]["LongName"]].append((filename, auxfile.copy()))
            else:
                print(auxfile)
                raise Exception("No auxType found in json")
            idx = idx + 1

    for k, v in dict_type_file.items():
        if DEBUG:
            print("Treating file type : " + k)
        if len(v) == 1:
            if DEBUG:
                print("only one file for type : " + k)
            if DEBUG:
                print("not updating : " + v[0][0])
            # Write down
            with open(os.path.join(args.output, v[0][0]), 'w') as json_file:
                json.dump(v[0][1], json_file)
            #shutil.copyfile(os.path.join(args.input, v[0][0]), os.path.join(args.output, v[0][0]))
        else:
            dict_sensor = {"A": [],
                           "B": [],
                           "X": []
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
            # list_of_sibling = search_similar(f, v)
            # print("Number of sibling for file : "+f[0] + " : "+str(len(list_of_sibling)))
            if k == "AuxTypes('AMH_ERRMAT_MPC')" or k == "AuxTypes('AMV_ERRMAT_MPC')" \
                    or k == "AuxTypes('MW_1_DNB_AX')" \
                    or k == "AuxTypes('MW_1_MON_AX')" \
                    or k == "AuxTypes('MW_1_NIR_AX')" \
                    or k == "AuxTypes('SR_1_CA1LAX')" \
                    or k == "AuxTypes('SR_1_CA1SAX')" \
                    or k == "AuxTypes('SR_1_CA2CAX')" \
                    or k == "AuxTypes('SR_1_CA2KAX')" \
                    or k == "AuxTypes('SR_1_CA2KAX')" \
                    or k == "AuxTypes('SR_2_POL_AX')" \
                    or k == "AuxTypes('SR_1_USO_AX')":
                treatOneDictERRMAT(dict_sensor["X"], args.input, args.output)
                treatOneDictERRMAT(dict_sensor["A"], args.input, args.output)
                treatOneDictERRMAT(dict_sensor["B"], args.input, args.output)
            else:
                treatOneDict(dict_sensor["X"], args.input, args.output)
                treatOneDict(dict_sensor["A"], args.input, args.output)
                treatOneDict(dict_sensor["B"], args.input, args.output)


if __name__ == "__main__":
    main()
