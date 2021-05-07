package com.csgroup.reprodatabaseline.http;

import java.util.ArrayList;
import java.util.List;

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

	public ReproBaselineAccess(HttpHandler handler, UrlsConfiguration conf,AuxipAccess auip) {
		this.httpHandler = handler;
		this.config = conf;
		this.auxip = auip;
	}

	public AuxTypes getListOfAuxTypes(final String mission){
		String res = httpHandler.getPost(config.getReprocessing_baseline_url()+
				"/AuxTypes?$expand=ProductLevels&$filter=Mission eq \'"+
				mission+"\'",null);
		AuxTypes res_aux = AuxTypes.loadValues(res);
		LOG.info(String.valueOf(res_aux.getValues().size()));
		return res_aux;
	}

	public List<AuxFile> getListOfAuxFiles(final AuxType type, final String sat, final String unit, RuleEnum rl){
		//Maybe it-s shortName on type ?
		String res = httpHandler.getPost(config.getReprocessing_baseline_url()+
				"/AuxFiles?$expand=AuxType&$filter=startswith(FullName,\'"+sat+
				"_\') and contains(FullName,\'"+type.LongName+"\')",null);
		List<AuxFile> res_aux = AuxFile.loadValues(type,res);
		res = httpHandler.getPost(config.getReprocessing_baseline_url()+
				"/AuxFiles?$expand=AuxType&$filter=startswith(FullName,\'"+sat+unit+
				"\') and contains(FullName,\'"+type.LongName+"\')",null);
		List<AuxFile> res_aux_unit = AuxFile.loadValues(type,res);
		res_aux.addAll(res_aux_unit);
		LOG.info(String.valueOf(res_aux.size()));
		return res_aux;
	}

	@Scheduled(fixedRate = 3600000, initialDelay = 5000)
	public void doMetrics() {
		LOG.info("Starting retireve");
		AuxTypes types = getListOfAuxTypes("S2MSI");
		List<String> results = new ArrayList<String>();
		for (AuxType t: types.getValues()) {
			List<AuxFile> files_repro = getListOfAuxFiles(t,"S2","B",t.Rule);
			RuleApplierInterface rule_applier = RuleApplierFactory.getRuleApplier(t.Rule);
			List<AuxFile> files_repro_filtered = rule_applier.apply(files_repro);
			//String bearerToken = request.getHeader("Authorization").replace("Bearer ", "") ;
			String bearerToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFaG5SUnZJd0M4aEhNdnl3a1RKQjFNRWlqVzRwc2o2ZmZ0MUQ2bVVpWHg4In0.eyJleHAiOjE2MjAzOTEyMTIsImlhdCI6MTYyMDM5MDMxMiwianRpIjoiZjBiM2M0ODgtOTA0MS00MWZlLWJjZGQtMjJlMDdiMmYyNjBjIiwiaXNzIjoiaHR0cHM6Ly9yZXByb2Nlc3NpbmctcHJlcGFyYXRpb24ubWwvYXV0aC9yZWFsbXMvYXV4aXAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiMGRjYjI2MjAtN2FiZS00YTY2LTg5Y2MtNmE4N2I5MDEzMDcyIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYXV4aXAiLCJzZXNzaW9uX3N0YXRlIjoiZjI1NzU5NTgtMzY1OC00NWIzLTliNjYtZWY0OTljYTFlOWY1IiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkb3dubG9hZCIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJFc3F1aXMgQmVuamFtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJiZXNxdWlzIiwiZ2l2ZW5fbmFtZSI6IkVzcXVpcyIsImZhbWlseV9uYW1lIjoiQmVuamFtaW4iLCJlbWFpbCI6ImJlbmphbWluLmVzcXVpc0Bjc2dyb3VwLmV1In0.s4CBr3vD1_MhhJly9BLMrsvEH6tDK0_a49NdjVGicGPHMu1EMlE9o0BhsbHwryPcGtalL5AssneK5meWFFfweh84N03M3rszkTJbGDV1CVnlg2qU2G32tU1AHEyfd3j1LYZ7P7xI6zwnTLukDCYNR3mCYldFTBq9j10kFPUQpUHI2fxALoQLw57H1bcno8wMmpYyu9EzLi6T7fDjBX3NBJ0bG54Kf71ZU19CAk0lr-M9TYvULvBdQ8QpuD3mTg4An6NiFR7SLT-EpmrdbLPyTy7rqsRDCTsY6MHfEna6fH5SHGoXP9T5F4FcjwrN26kkFdOdGoVAUS1R9OyNWwAFyw0";
			try {
				List<String> wasa_files = auxip.getListOfAuxFileURLs(files_repro_filtered, bearerToken);
				results.addAll(wasa_files);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LOG.info("Retrieve done, "+String.valueOf(results.size()));			
		for (String str: results) {
			LOG.info("File: "+str);
		}
	}

}
