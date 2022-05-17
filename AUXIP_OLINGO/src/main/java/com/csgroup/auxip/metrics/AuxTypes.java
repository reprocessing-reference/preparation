package com.csgroup.auxip.metrics;

import java.util.ArrayList;
import java.util.List;

public class AuxTypes {
	
	public static List<String> S1_AUX_TYPES = new ArrayList<>(List.of( 
			"AUX_PP1",
			"AUX_PP2",
			"AUX_CAL",
			"AUX_INS",
			"AUX_POEORB",
			"AUX_TIM",
			"S1__AUX_SCS",
			"S1__AUX_WND",
			"S1__AUX_WAV",			
			"S1__AUX_ICE",
			"AMH_ERRMAT_MPC",
			"AMV_ERRMAT_MPC"
			));
	
	public static List<String> S2_AUX_TYPES = new ArrayList<>(List.of( 
			"GIP_R2MACO",
			"GIP_L2ACSC",
			"GIP_L2ACAC",
			"GIP_L2ACFG",
			"GIP_PROBA2",
			"AUX_UT1UTC",
			"GIP_ATMIMA",
			"GIP_ATMSAD",
			"GIP_BLINDP",
			"GIP_EARMOD",
			"GIP_CLOINV",
			"GIP_CONVER",
			"GIP_DATATI",
			"GIP_DECOMP",
			"GIP_G2PARA",
			"GIP_G2PARE",
			"GIP_GEOPAR",
			"GIP_INTDET",
			"GIP_INVLOC",
			"GIP_JP2KPA",
			"GIP_LREXTR",
			"GIP_MASPAR",
			"GIP_OLQCPA",
			"GIP_PRDLOC",
			"GIP_PROBAS",
			"GIP_R2ABCA",
			"GIP_R2BINN",
			"GIP_R2CRCO",
			"GIP_R2DEBA",
			"GIP_R2DECT",
			"GIP_R2DEFI",
			"GIP_R2DENT",
			"GIP_R2DEPI",
			"GIP_R2EOB2",
			"GIP_R2EQOG",
			"GIP_R2L2NC",
			"GIP_R2NOMO",
			"GIP_R2PARA",
			"GIP_R2SWIR",
			"GIP_R2WAFI",
			"GIP_RESPAR",
			"GIP_SPAMOD",
			"GIP_TILPAR",
			"GIP_VIEDIR",
			"GIP_ECMWFP",
			"AUX_ECMWFD",
			"AUX_ECMWFT",
			"AUX_RESORB",
			"GIP_CLOPAR",
			"GIP_HRTPAR",
			"AUX_CAMSAN",
			"AUX_CAMSRE",
			"AUX_PREORB"
			));
	
	public static List<String> S3_SRAL_AUX_TYPES = new ArrayList<>(List.of(
			"SR_1_USO_AX",
			"SR_2_CON_AX",
			"SR___LSM_AX",
			"SR_2_RMO_AX",
			"SR_2_RGI_AX",
			"SR_2_MSS2AX",
			"SR_2_GEO_AX",
			"SR_2_ODLEAX",
			"SR_2_WNDLAX",
			"SR_2_WNDSAX",
			"SR_2_SIGLAX",
			"SR_2_SIGSAX",
			"SR_2_SET_AX",
			"SR_2_SSM_AX",
			"SR_2_MSMGAX",
			"SR_2_CP00AX",
			"SR_2_CP06AX",
			"SR_2_CP12AX",
			"SR_2_CP18AX",
			"SR_2_S1AMAX",
			"SR_2_S2AMAX",
			"SR_2_S1PHAX",
			"SR_2_S2PHAX",
			"SR_2_MDT_AX",
			"SR_2_SHD_AX",
			"SR_2_SSBLAX",
			"SR_2_SSBSAX",
			"SR_2_SDMMAX",
			"SR_2_SST_AX",
			"SR_2_LRC_AX",
			"SR_2_RRC_AX",
			"SR_2_CCT_AX",
			"SR___POEPAX",
			"SR___POESAX",
			"SR___CHDNAX",
			"SR___CHDRAX",
			"SR_1_CA1LAX",
			"SR_1_CA1SAX",
			"SR_1_CA2KAX",
			"SR_1_CA2CAX",
			"SR_1_CONCAX",
			"SR_1_CONMAX",
			"SR_2_PCPPAX",
			"SR_2_MAG_AX",
			"SR_2_IC01AX",
			"SR_2_IC02AX",
			"SR_2_IC03AX",
			"SR_2_IC04AX",
			"SR_2_IC05AX",
			"SR_2_IC06AX",
			"SR_2_IC07AX",
			"SR_2_IC08AX",
			"SR_2_IC09AX",
			"SR_2_IC10AX",
			"SR_2_RET_AX",
			"SR_2_LUTFAX",
			"SR_2_LUTEAX",
			"SR_2_LUTSAX",
			"SR_2_MLM_AX"
			));
	
	public static List<String> S3_MWR_AUX_TYPES = new ArrayList<>(List.of(
			"MW_1_SLC_AX",
			"MW___CHDNAX",
			"MW___CHDRAX",
			"MW___STD_AX",
			"MW_1_NIR_AX",
			"MW_1_DNB_AX",
			"MW_1_MON_AX"			
			));
	
	public static List<String> S3_OLCI_AUX_TYPES = new ArrayList<>(List.of(
			"OL_2_PCP_AX",
			"OL_2_PPP_AX",
			"OL_2_WVP_AX",
			"OL_2_ACP_AX",
			"OL_2_OCP_AX",
			"OL_2_VGP_AX",
			"OL_2_CLP_AX",
			"OL_1_EO__AX",
			"OL_1_RAC_AX",
			"OL_1_SPC_AX",
			"OL_1_CLUTAX",
			"OL_1_INS_AX",
			"OL_1_CAL_AX",
			"OL_1_PRG_AX"
			));
	
	public static List<String> S3_SLSTR_AUX_TYPES = new ArrayList<>(List.of(
			"SL_1_PCP_AX",
			"SL_1_ANC_AX",
			"SL_1_N_S7AX",
			"SL_1_N_S8AX",
			"SL_1_N_S9AX",
			"SL_1_N_F1AX",
			"SL_1_N_F2AX",
			"SL_1_O_S7AX",
			"SL_1_O_S8AX",
			"SL_1_O_S9AX",
			"SL_1_O_F1AX",
			"SL_1_O_F2AX",
			"SL_1_N_S1AX",
			"SL_1_N_S2AX",
			"SL_1_N_S3AX",
			"SL_1_O_S1AX",
			"SL_1_O_S2AX",
			"SL_1_O_S3AX",
			"SL_1_NAS4AX",
			"SL_1_NAS5AX",
			"SL_1_NAS6AX",
			"SL_1_NBS4AX",
			"SL_1_NBS5AX",
			"SL_1_NBS6AX",
			"SL_1_OAS4AX",
			"SL_1_OAS5AX",
			"SL_1_OAS6AX",
			"SL_1_OBS4AX",
			"SL_1_OBS5AX",
			"SL_1_OBS6AX",
			"SL_1_VSC_AX",
			"SL_1_VIC_AX",
			"SL_1_GEO_AX",
			"SL_1_GEC_AX",
			"SL_1_ESSTAX",
			"SL_1_CLO_AX",
			"SL_2_PCP_AX",
			"SL_2_S6N_AX",
			"SL_2_S7N_AX",
			"SL_2_S8N_AX",
			"SL_2_S9N_AX",
			"SL_2_F1N_AX",
			"SL_2_F2N_AX",
			"SL_2_S7O_AX",
			"SL_2_S8O_AX",
			"SL_2_S9O_AX",
			"SL_2_N2_CAX",
			"SL_2_N3RCAX",
			"SL_2_N3_CAX",
			"SL_2_D2_CAX",
			"SL_2_D3_CAX",
			"SL_2_SST_AX",
			"SL_2_SDI3AX",
			"SL_2_SDI2AX",
			"SL_2_SSESAX",
			"SL_2_SSTAAX",
			"SL_2_LSTCAX",
			"SL_2_LSTBAX",
			"SL_2_LSTVAX",
			"SL_2_LSTWAX",
			"SL_2_LSTEAX",
			"SL_2_FRPTAX",
			"SL_2_CFM_AX",
			"SL_2_FXPAAX",
			"SL_2_PCPFAX",
			"SL_2_PLFMAX",
			"SL_2_SXPAAX",
			"SL_1_IRE_AX",
			"SL_1_LCC_AX",
			"SL_1_CDP_AX",
			"SL_1_CLP_AX",
			"SL_1_ADJ_AX",
			"SL_1_RTT_AX"			
			));
	
	public static List<String> S3_SYN_AUX_TYPES = new ArrayList<>(List.of(
			"SY_1_PCP_AX",
			"SY_1_GCPBAX",
			"OL_1_MCHDAX",
			"SL_1_MCHDAX",
			"SY_1_CDIBAX",
			"SY_2_PCP_AX",
			"SY_2_RAD_AX",
			"SY_2_RADPAX",
			"SY_2_SPCPAX",
			"SY_2_RADSAX",
			"SY_2_PCPSAX",
			"SY_2_ACLMAX",
			"SY_2_ART_AX",
			"SY_2_LSR_AX",
			"SY_2_OSR_AX",
			"SY_2_AODCAX",
			"SY_2_PCPAAX"			
			));
}
