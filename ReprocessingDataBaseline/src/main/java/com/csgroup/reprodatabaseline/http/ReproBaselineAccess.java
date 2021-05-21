package com.csgroup.reprodatabaseline.http;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.csgroup.reprodatabaseline.config.UrlsConfiguration;
import com.csgroup.reprodatabaseline.datamodels.AuxFile;
import com.csgroup.reprodatabaseline.datamodels.AuxType;
import com.csgroup.reprodatabaseline.datamodels.AuxTypes;
import com.csgroup.reprodatabaseline.rules.RuleApplierFactory;
import com.csgroup.reprodatabaseline.rules.RuleApplierInterface;
import com.csgroup.reprodatabaseline.rules.RuleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ReproBaselineAccess {
	private static final Logger LOG = LoggerFactory.getLogger(ReproBaselineAccess.class);

	private final HttpHandler httpHandler;

	private final UrlsConfiguration config;

	private final AuxipAccess auxip;
	private String accessToken;
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public ReproBaselineAccess(HttpHandler handler, UrlsConfiguration conf,AuxipAccess auip) {
		this.httpHandler = handler;
		this.config = conf;
		this.auxip = auip;
	}

	public AuxTypes getListOfAuxTypes(final String mission){
		String res = httpHandler.getPost(config.getReprocessing_baseline_url()+
				"/AuxTypes?$expand=ProductLevels&$filter=Mission eq \'"+
				mission+"\'",this.accessToken);
		AuxTypes res_aux = AuxTypes.loadValues(res);
		LOG.info(String.valueOf(res_aux.getValues().size()));
		return res_aux;
	}

	public List<AuxFile> getListOfAuxFiles(final AuxType type, final String sat, final String unit, RuleEnum rl){
		//Maybe it-s shortName on type ?
		String res = httpHandler.getPost(config.getReprocessing_baseline_url()+
				"/AuxFiles?$expand=AuxType&$filter=startswith(FullName,\'"+sat+
				"_\') and contains(FullName,\'"+type.LongName+"\')",this.accessToken);
		List<AuxFile> res_aux = AuxFile.loadValues(type,res);
		res = httpHandler.getPost(config.getReprocessing_baseline_url()+
				"/AuxFiles?$expand=AuxType&$filter=startswith(FullName,\'"+sat+unit+
				"\') and contains(FullName,\'"+type.LongName+"\')",this.accessToken);
		List<AuxFile> res_aux_unit = AuxFile.loadValues(type,res);
		res_aux.addAll(res_aux_unit);
		LOG.info(String.valueOf(res_aux.size()));
		return res_aux;
	}

	public String getMission(String platformShortName,String productType) 
	{
		if( platformShortName.equals("S3"))
		{
			if( productType.contains("OL"))
			{
				return "S3OLCI";
			}
			if( productType.contains("SL"))
			{
				return "S3SLSTR";
			}
			if( productType.contains("SY"))
			{
				return "S3SYN";
			}
			if( productType.contains("SR"))
			{
				return "S3SRAL";
			}
			if( productType.contains("MW"))
			{
				return "S3MWR";
			}
		}else if(platformShortName.equals("S2"))
		{
			return "S2MSI";
		}else{ // S1 
			return "S1SAR";
		}

		return null;
	}



	public List<AuxFile> getReprocessingDataBaseline(String level0,String productType,int deltaT0,int deltaT1) {
		// 1 -> get mission and sat_unit
		// 2 -> get AuxType for this mission
		// 3 -> get AuxFiles
		// 4 -> apply rules
		// 5 -> return the selected Auxfiles

		LOG.info(">> Starting ReproBaselineAccess.getReprocessingDataBaseline");
		
		String platformShortName = level0.substring(0, 2); // "S1", "S2" or "S3"
        String platformSerialIdentifier = level0.substring(2, 3); //"A" or "B" or "_"

		String mission = getMission(platformShortName, productType);
		AuxTypes types = getListOfAuxTypes(mission);
		ZonedDateTime t0;
		ZonedDateTime t1;
		Duration delta0 = Duration.ofSeconds(deltaT0); 
		Duration delta1 = Duration.ofSeconds(deltaT1);

		if( platformShortName.equals("S3"))
		{
			// S3B_OL_0_EFR____20210418T201042_20210418T201242_20210418T215110_0119_051_242______LN1_O_NR_002.SEN3
			t0 = ZonedDateTime.parse(level0.subSequence(16, 16+15),DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC")));
			t1 = ZonedDateTime.parse(level0.subSequence(16+16, 2*16+15),DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC")));

		}else if( platformShortName.equals("S2"))
		{
			// TODO TO be updated
			t0 = ZonedDateTime.parse(level0.subSequence(16, 16+15));
			t1 = ZonedDateTime.parse(level0.subSequence(16+16, 2*16+15));
		}else
		{
			// TODO TO be updated following l0 of S1 
			t0 = ZonedDateTime.parse(level0.subSequence(16, 16+15));
			t1 = ZonedDateTime.parse(level0.subSequence(16+16, 2*16+15));
		}

		List<AuxFile> results = new ArrayList<>();

		for (AuxType t: types.getValues()) 
		{
			List<AuxFile> files_repro = getListOfAuxFiles(t,platformShortName,platformSerialIdentifier,t.Rule);
			RuleApplierInterface rule_applier = RuleApplierFactory.getRuleApplier(t.Rule);
			List<AuxFile> files_repro_filtered = rule_applier.apply(files_repro,t0,t1,delta0,delta1);

			try {
				auxip.setAuxFileUrls(files_repro_filtered, this.accessToken);
				results.addAll(files_repro_filtered);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		LOG.info("<< Ending ReproBaselineAccess.getReprocessingDataBaseline");

		return results;

	}

	// @Scheduled(fixedRate = 3600000, initialDelay = 5000)
	// public void doMetrics() {
	// 	LOG.info("Starting retireve");
	// 	AuxTypes types = getListOfAuxTypes("S2MSI");
	// 	List<String> results = new ArrayList<String>();
	// 	for (AuxType t: types.getValues()) {
	// 		List<AuxFile> files_repro = getListOfAuxFiles(t,"S2","B",t.Rule);
	// 		RuleApplierInterface rule_applier = RuleApplierFactory.getRuleApplier(t.Rule);
	// 		List<AuxFile> files_repro_filtered = rule_applier.apply(files_repro);
	// 		//String bearerToken = request.getHeader("Authorization").replace("Bearer ", "") ;
	// 		String bearerToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFaG5SUnZJd0M4aEhNdnl3a1RKQjFNRWlqVzRwc2o2ZmZ0MUQ2bVVpWHg4In0.eyJleHAiOjE2MjAzOTEyMTIsImlhdCI6MTYyMDM5MDMxMiwianRpIjoiZjBiM2M0ODgtOTA0MS00MWZlLWJjZGQtMjJlMDdiMmYyNjBjIiwiaXNzIjoiaHR0cHM6Ly9yZXByb2Nlc3NpbmctcHJlcGFyYXRpb24ubWwvYXV0aC9yZWFsbXMvYXV4aXAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiMGRjYjI2MjAtN2FiZS00YTY2LTg5Y2MtNmE4N2I5MDEzMDcyIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYXV4aXAiLCJzZXNzaW9uX3N0YXRlIjoiZjI1NzU5NTgtMzY1OC00NWIzLTliNjYtZWY0OTljYTFlOWY1IiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkb3dubG9hZCIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJFc3F1aXMgQmVuamFtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJiZXNxdWlzIiwiZ2l2ZW5fbmFtZSI6IkVzcXVpcyIsImZhbWlseV9uYW1lIjoiQmVuamFtaW4iLCJlbWFpbCI6ImJlbmphbWluLmVzcXVpc0Bjc2dyb3VwLmV1In0.s4CBr3vD1_MhhJly9BLMrsvEH6tDK0_a49NdjVGicGPHMu1EMlE9o0BhsbHwryPcGtalL5AssneK5meWFFfweh84N03M3rszkTJbGDV1CVnlg2qU2G32tU1AHEyfd3j1LYZ7P7xI6zwnTLukDCYNR3mCYldFTBq9j10kFPUQpUHI2fxALoQLw57H1bcno8wMmpYyu9EzLi6T7fDjBX3NBJ0bG54Kf71ZU19CAk0lr-M9TYvULvBdQ8QpuD3mTg4An6NiFR7SLT-EpmrdbLPyTy7rqsRDCTsY6MHfEna6fH5SHGoXP9T5F4FcjwrN26kkFdOdGoVAUS1R9OyNWwAFyw0";
	// 		try {
	// 			List<String> wasa_files = auxip.getListOfAuxFileURLs(files_repro_filtered, bearerToken);
	// 			results.addAll(wasa_files);
	// 		} catch (Exception e) {
	// 			// TODO Auto-generated catch block
	// 			e.printStackTrace();
	// 		}
	// 	}
	// 	LOG.info("Retrieve done, "+String.valueOf(results.size()));			
	// 	for (String str: results) {
	// 		LOG.info("File: "+str);
	// 	}
	// }

}