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
"AUX_POE" : "ValidityPeriod",
"AUX_ATT" : "ValidityPeriod",
"AUX_SCS" : "ValidityPeriod",
"AUX_WND" : "ValidityPeriod",
"AUX_WAV" : "ValidityPeriod",
"AUX_ICE" : "ValidityPeriod",
"AMH_ERRMAT" : "ValidityPeriod",
"L2ACAC" : "AnyTime",
"L2ACSC" : "AnyTime",
"PROBA2" : "AnyTime",
"L2ACFG" : "AnyTime",
"MPL_ORBRES" : "ValidityPeriod",
"MPL_ORBPRE" : "ValidityPeriod",
"AUX_TIM" : "ValidityPeriod",
"AUX_ECE" : "ValidityPeriod",
"AX___MF1_AX" : "ValidityPeriod",
"AX___MA1_AX" : "ValidityPeriod",
"AX___MA2_AX" : "ValidityPeriod",
"AX___MF2_AX" : "ValidityPeriod",
"AX___MFA_AX" : "ValidityPeriod",
"AX___CST_AX" : "AnyTime",
"AX___DEM_AX" : "AnyTime",
"AX___LWM_AX" : "AnyTime",
"AX___OOM_AX" : "AnyTime",
"AX___CLM_AX" : "AnyTime",
"AX___TRM_AX" : "AnyTime",
"AX___BB2_AX" : "ValidityPeriod",
"AX___BA__AX" : "ValidityPeriod",
"OL_2_PCP_AX" : "AnyTime",
"OL_2_PPP_AX" : "AnyTime",
"OL_2_WVP_AX" : "AnyTime",
"OL_2_ACP_AX" : "AnyTime",
"OL_2_OCP_AX" : "AnyTime",
"OL_2_VGP_AX" : "AnyTime",
"OL_2_CLP_AX" : "AnyTime",
"SR_1_USO_AX" : "ValidityPeriod",
"SR_2_CON_AX" : "AnyTime",
"SR___LSM_AX" : "AnyTime",
"SR_2_PMO_AX" : "ValidityPeriod",
"SR_2_RMO_AX" : "ValidityPeriod",
"SR_2_POL_AX" : "ValidityPeriod",
"SR_2_PGI_AX" : "ValidityPeriod",
"SR_2_RGI_AX" : "ValidityPeriod",
"SR_2_SIC_AX" : "ValidityPeriod",
"SR_2_LRC_AX" : "AnyTime",
"SR_2_SST_AX" : "AnyTime",
"SR_2_SFL_AX" : "AnyTime",
"SR_2_FLT_AX" : "AnyTime",
"SR_2_CCT_AX" : "AnyTime",
"SR_2_RRC_AX" : "AnyTime",
"SR_2_EOT1AX" : "AnyTime",
"SR_2_EOT2AX" : "AnyTime",
"SR_2_LT1_AX" : "AnyTime",
"SR_2_LT2_AX" : "AnyTime",
"SR_2_LNEQAX" : "AnyTime",
"SR_2_MSS1AX" : "AnyTime",
"SR_2_MSS2AX" : "AnyTime",
"SR_2_GEO_AX" : "AnyTime",
"SR_2_ODLEAX" : "AnyTime",
"SR_2_WNDLAX" : "AnyTime",
"SR_2_WNDSAX" : "AnyTime",
"SR_2_SIGLAX" : "AnyTime",
"SR_2_SIGSAX" : "AnyTime",
"SR_2_SET_AX" : "AnyTime",
"SR_2_SSM_AX" : "AnyTime",
"SR_2_MSMGAX" : "AnyTime",
"SR_2_CP00AX" : "AnyTime",
"SR_2_CP06AX" : "AnyTime",
"SR_2_CP12AX" : "AnyTime",
"SR_2_CP18AX" : "AnyTime",
"SR_2_S1AMAX" : "AnyTime",
"SR_2_S2AMAX" : "AnyTime",
"SR_2_S1PHAX" : "AnyTime",
"SR_2_S2PHAX" : "AnyTime",
"SR_2_MDT_AX" : "AnyTime",
"SR_2_SHD_AX" : "AnyTime",
"SR_2_SSBLAX" : "AnyTime",
"SR_2_SSBSAX" : "AnyTime",
"SR_2_SD01AX" : "AnyTime",
"SR_2_SD02AX" : "AnyTime",
"SR_2_SD03AX" : "AnyTime",
"SR_2_SD04AX" : "AnyTime",
"SR_2_SD05AX" : "AnyTime",
"SR_2_SD06AX" : "AnyTime",
"SR_2_SD07AX" : "AnyTime",
"SR_2_SD08AX" : "AnyTime",
"SR_2_SD09AX" : "AnyTime",
"SR_2_SD10AX" : "AnyTime",
"SR_2_SD11AX" : "AnyTime",
"SR_2_SD12AX" : "AnyTime",
"SR_2_SI01AX" : "AnyTime",
"SR_2_SI02AX" : "AnyTime",
"SR_2_SI03AX" : "AnyTime",
"SR_2_SI04AX" : "AnyTime",
"SR_2_SI05AX" : "AnyTime",
"SR_2_SI06AX" : "AnyTime",
"SR_2_SI07AX" : "AnyTime",
"SR_2_SI08AX" : "AnyTime",
"SR_2_SI09AX" : "AnyTime",
"SR_2_SI10AX" : "AnyTime",
"SR_2_SI11AX" : "AnyTime",
"SR_2_SI12AX" : "AnyTime",
"SR_2_SURFAX" : "AnyTime",
"SY_1_PCP_AX" : "AnyTime",
"MPL_ORBSCT" : "AnyTime",
"AUX_RES" : "ValidityPeriod",
"AUX_PRE" : "ValidityPeriod",
"1_AX_TPDB" : "AnyTime",
"1_AX_OLIC" : "AnyTime",
"1_AX_SLIC" : "AnyTime",
"SY_1_CDIBAX" : "AnyTime",
"SY_2_SYCPAX" : "AnyTime",
"SY_2_SYRTAX" : "AnyTime",
"SY_2_VPRTAX" : "AnyTime",
"SY_2_VPSRAX" : "AnyTime",
"SY_2_VSRTAX" : "AnyTime",
"GIP_L2A_SC" : "AnyTime",
"GIP_L2A_AC" : "AnyTime",
"GIP_L2A" : "AnyTime",
"GIP_L2A_USR" : "AnyTime",
"GIP_L2A_PB" : "AnyTime",
"AUX_DEM" : "ValidityPeriod",
"AUX_DEM" : "ValidityPeriod",
"AUX_DEM" : "ValidityPeriod",
"AX___FRO_AX" : "ValidityPeriod",
"AX___FPO_AX" : "ValidityPeriod",
"AX___OSF_AX" : "AnyTime",
"SY_1_GCPBAX" : "AnyTime",
"OL_1_MCHDAX" : "AnyTime",
"SL_1_MCHDAX" : "AnyTime",
"SY_2_PCP_AX" : "AnyTime",
"SY_2_RAD_AX" : "AnyTime",
"SY_2_RADPAX" : "AnyTime",
"SY_2_SPCPAX" : "AnyTime",
"SY_2_RADSAX" : "AnyTime",
"L2A_GIPP" : "ValidityPeriod",
"DEM_GLOBEF" : "ValidityPeriod",
"DEM_SRTMFO" : "ValidityPeriod",
"DEM_GEOIDF" : "ValidityPeriod",
"AUX_GRIXXX" : "ValidityPeriod",
"PRD_HKTM__" : "ValidityPeriod",
"AUX_ATMCOR" : "ValidityPeriod",
"SL_1_PCP_AX" : "AnyTime",
"SL_1_ANC_AX" : "AnyTime",
"SL_1_N_S7AX" : "AnyTime",
"SL_1_N_S8AX" : "AnyTime",
"SL_1_N_S9AX" : "AnyTime",
"SL_1_N_F1AX" : "AnyTime",
"SL_1_N_F2AX" : "AnyTime",
"SL_1_O_S7AX" : "AnyTime",
"SL_1_O_S8AX" : "AnyTime",
"SL_1_O_S9AX" : "AnyTime",
"SL_1_O_F1AX" : "AnyTime",
"SL_1_O_F2AX" : "AnyTime",
"SL_1_N_S1AX" : "AnyTime",
"SL_1_N_S2AX" : "AnyTime",
"SL_1_N_S3AX" : "AnyTime",
"SL_1_O_S1AX" : "AnyTime",
"SL_1_O_S2AX" : "AnyTime",
"SL_1_O_S3AX" : "AnyTime",
"SL_1_NAS4AX" : "AnyTime",
"SL_1_NAS5AX" : "AnyTime",
"SL_1_NAS6AX" : "AnyTime",
"SL_1_NBS4AX" : "AnyTime",
"SL_1_NBS5AX" : "AnyTime",
"SL_1_NBS6AX" : "AnyTime",
"SL_1_OAS4AX" : "AnyTime",
"SL_1_OAS5AX" : "AnyTime",
"SL_1_OAS6AX" : "AnyTime",
"SL_1_OBS4AX" : "AnyTime",
"SL_1_OBS5AX" : "AnyTime",
"SL_1_OBS6AX" : "AnyTime",
"SL_1_VSC_AX" : "ValidityPeriod",
"SL_1_VIC_AX" : "AnyTime",
"SL_1_GEO_AX" : "AnyTime",
"SL_1_GEC_AX" : "AnyTime",
"SL_1_ESSTAX" : "AnyTime",
"SL_1_CLO_AX" : "AnyTime",
"SL_2_PCP_AX" : "AnyTime",
"SL_2_S6N_AX" : "AnyTime",
"OL_1_EO__AX" : "ValidityPeriod",
"OL_1_RAC_AX" : "ValidityPeriod",
"OL_1_SPC_AX" : "ValidityPeriod",
"OL_1_CLUTAX" : "AnyTime",
"OL_1_INS_AX" : "AnyTime",
"OL_1_CAL_AX" : "ValidityPeriod",
"OL_1_PRG_AX" : "ValidityPeriod",
"SL_2_S7N_AX" : "AnyTime",
"SL_2_S8N_AX" : "AnyTime",
"SL_2_S9N_AX" : "AnyTime",
"SL_2_F1N_AX" : "AnyTime",
"SL_2_F2N_AX" : "ValidityPeriod",
"SL_2_S7O_AX" : "AnyTime",
"SL_2_S8O_AX" : "AnyTime",
"SL_2_S9O_AX" : "AnyTime",
"SL_2_N2_CAX" : "AnyTime",
"SL_2_N3RCAX" : "AnyTime",
"SL_2_N3_CAX" : "AnyTime",
"SL_2_D2_CAX" : "AnyTime",
"SL_2_D3_CAX" : "AnyTime",
"SL_2_SST_AX" : "AnyTime",
"SL_2_SDI3AX" : "AnyTime",
"SL_2_SDI2AX" : "AnyTime",
"SL_2_SSESAX" : "AnyTime",
"SL_2_SSTAAX" : "ValidityPeriod",
"SL_2_LSTCAX" : "ValidityPeriod",
"SL_2_LSTBAX" : "ValidityPeriod",
"SL_2_LSTVAX" : "ValidityPeriod",
"SL_2_LSTWAX" : "ValidityPeriod",
"SL_2_LSTEAX" : "ValidityPeriod",
"SL_2_FRPTAX" : "ValidityPeriod",
"SY_2_PCPSAX" : "AnyTime",
"SR___ROE_AX" : "ValidityPeriod",
"SR___MGNPAX" : "ValidityPeriod",
"SR___MGNSAX" : "ValidityPeriod",
"SR___MDO_AX" : "ValidityPeriod",
"SR___POEPAX" : "ValidityPeriod",
"SR___POESAX" : "ValidityPeriod",
"SR___CHDNAX" : "ValidityPeriod",
"SR___CHDRAX" : "ValidityPeriod",
"SR_1_CA1LAX" : "ValidityPeriod",
"SR_1_CA1SAX" : "ValidityPeriod",
"SR_1_CA2KAX" : "ValidityPeriod",
"SR_1_CA2CAX" : "ValidityPeriod",
"SR_1_CONCAX" : "AnyTime",
"SR_1_CONMAX" : "AnyTime",
"SR_2_NRPPAX" : "ValidityPeriod",
"SR_2_PMPPAX" : "ValidityPeriod",
"SR_2_PCPPAX" : "ValidityPeriod",
"SR_2_PMPSAX" : "ValidityPeriod",
"SR_2_PCPSAX" : "ValidityPeriod",
"SR_2_MAG_AX" : "AnyTime",
"SR_2_IC01AX" : "AnyTime",
"SR_2_IC02AX" : "AnyTime",
"SR_2_IC03AX" : "AnyTime",
"SR_2_IC04AX" : "AnyTime",
"SR_2_IC05AX" : "AnyTime",
"SR_2_IC06AX" : "AnyTime",
"SR_2_IC07AX" : "AnyTime",
"SR_2_IC08AX" : "AnyTime",
"SR_2_IC09AX" : "AnyTime",
"SR_2_IC10AX" : "AnyTime",
"SR_2_RET_AX" : "AnyTime",
"SR_2_LUTFAX" : "AnyTime",
"SR_2_LUTEAX" : "AnyTime",
"SR_2_LUTSAX" : "AnyTime",
"SR_2_MLM_AX" : "AnyTime",
"MW_1_SLC_AX" : "AnyTime",
"MW___CHDNAX" : "AnyTime",
"MW___CHDRAX" : "AnyTime",
"MW___STD_AX" : "AnyTime",
"MW_1_NIR_AX" : "ValidityPeriod",
"MW_1_DNB_AX" : "ValidityPeriod",
"MW_1_MON_AX" : "ValidityPeriod",
"OL_1_PCPBAX" : "ValidityPeriod",
"OL_1_PLTBAX" : "ValidityPeriod",
"SL_2_ACLMAX" : "ValidityPeriod",
"SL_2_ART_AX" : "ValidityPeriod",
"SL_2_OSR_AX" : "ValidityPeriod",
"SL_2_PCPAAX" : "ValidityPeriod",
"SL_2_CFM_AX" : "AnyTime",
"SL_2_FXPAAX" : "AnyTime",
"SL_2_PCPFAX" : "AnyTime",
"SL_2_PLFMAX" : "AnyTime",
"SL_2_SXPAAX" : "AnyTime",
"SL_1_IRE_AX" : "AnyTime",
"SL_1_LCC_AX" : "AnyTime",
"SL_1_CDP_AX" : "AnyTime",
"SL_1_CLP_AX" : "AnyTime",
"SL_1_ADJ_AX" : "AnyTime",
"SL_1_RTT_AX" : "AnyTime",
"SL_1_PCPBAX" : "AnyTime",
"SL_1_PLTBAX" : "AnyTime",
"OL_2_PCPBAX" : "AnyTime",
"OL_2_PLTBAX" : "AnyTime",
"SL_2_PCPBAX" : "AnyTime",
"SL_2_PLTBAX" : "AnyTime",
"SY_2_ACLMAX" : "AnyTime",
"SY_2_ART_AX" : "AnyTime",
"SY_2_LSR_AX" : "AnyTime",
"SY_2_OSR_AX" : "AnyTime",
"SY_2_AODCAX" : "AnyTime",
"SY_2_PCPAAX" : "AnyTime",
"SY_2_PCPBAX" : "AnyTime",
"SY_2_PLTBAX" : "AnyTime",
"SY_2_CVPBAX" : "AnyTime",
"SY_2_PVPBAX" : "AnyTime",
"SY_2_CVSBAX" : "AnyTime",
"SY_2_PVSBAX" : "AnyTime",
"AUX_RDB____MPC" : "ValidityPeriod",
"AMV_ERRMAT_MPC" : "ValidityPeriod",
"ASA_XCH" : "ValidityPeriod",
"ASA_XCA" : "ValidityPeriod",
"ASA_INS" : "ValidityPeriod",
"AUX_OBMEMC" : "ValidityPeriod",
"AUX_ECMWFT" : "ValidityPeriod",
"AUX_SADATA" : "ValidityPeriod",
"AUX_SXXYYY" : "ValidityPeriod",
"AUX_GPS___" : "ValidityPeriod",
"AUX_ROE_AX" : "ValidityPeriod",
"AUX_MOEORB" : "ValidityPeriod"
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
    list_missing = []
    with open(args.input) as csvfile:
        spamreader = csv.DictReader(csvfile, delimiter=',', quotechar='"')
        for lines in spamreader:
            #print("Treating: "+lines['long name'])
            template['LongName'] = lines['long name']
            print("curl -X PUT -u admin:'%HEl$1698OgDa%L' -H 'Content-Type: application/json' -d @"
                  +os.path.join(args.output, lines['long name']+".json")
                  + " \"https://reprocessing-preparation.ml/reprocessing.svc/AuxTypes('"+lines['long name']+"')\"")
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
                if k in template['LongName']:
                    template['Validity'] = v
                    found = True
                    break
            if not found:
                print("\""+template['LongName'] + "\" : \"ValidityPeriod\",")
                template['Validity'] = "ValidityPeriod"
                list_missing.append(template['LongName'])
            if lines['Comments'] != '':
                if len(lines['Comments']) > 255:
                    template['Comments'] = lines['Comments'][:255]
                else:
                    template['Comments'] = lines['Comments']
            else:
                template['Comments'] = 'Not available'
            if lines['Mission'] != '':
               template['Mission'] = lines['Mission']
            else:
                template['Mission'] = 'Not available'
            #Write down
            with open(os.path.join(args.output, lines['long name']+".json"), 'w') as json_file:
                json.dump(template, json_file)

        print(list_missing)


if __name__ == "__main__":
    main()
