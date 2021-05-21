package com.csgroup.reprodatabaseline.http;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.csgroup.reprodatabaseline.config.UrlsConfiguration;
import com.csgroup.reprodatabaseline.datamodels.AuxFile;
import com.csgroup.reprodatabaseline.datamodels.AuxType;
import com.csgroup.reprodatabaseline.datamodels.AuxTypes;
import com.csgroup.reprodatabaseline.rules.RuleApplierFactory;
import com.csgroup.reprodatabaseline.rules.RuleApplierInterface;
import com.csgroup.reprodatabaseline.rules.RuleEnum;

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



	public List<AuxFile> getReprocessingDataBaseline(String level0,String productType,int deltaT0,int deltaT1) throws ODataApplicationException {
		// 1 -> get mission and sat_unit
		// 2 -> get AuxType for this mission
		// 3 -> get AuxFiles
		// 4 -> apply rules
		// 5 -> return the selected Auxfiles

		LOG.info(">> Starting ReproBaselineAccess.getReprocessingDataBaseline");
		
		String platformShortName = level0.substring(0, 2); // "S1", "S2" or "S3"
        String platformSerialIdentifier = level0.substring(2, 3); //"A" or "B" or "_"

		String mission = getMission(platformShortName, productType);
		LOG.debug(">> Starting retrieving AuxTypes list for mission : "+mission);
		AuxTypes types = getListOfAuxTypes(mission);
		LOG.debug(">> Retrieving Done, "+String.valueOf(types.getValues().size())+" elements found");
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
	        int statusCode = HttpStatusCode.NOT_IMPLEMENTED.getStatusCode();
	        throw new ODataApplicationException("Not supported.",statusCode, Locale.ROOT,String.valueOf(statusCode));
			//t0 = ZonedDateTime.parse(level0.subSequence(16, 16+15));
			//t1 = ZonedDateTime.parse(level0.subSequence(16+16, 2*16+15));
		}else
		{
			// TODO TO be updated following l0 of S1 
	        int statusCode = HttpStatusCode.NOT_IMPLEMENTED.getStatusCode();
	        throw new ODataApplicationException("Not supported.",statusCode, Locale.ROOT,String.valueOf(statusCode));
			//t0 = ZonedDateTime.parse(level0.subSequence(16, 16+15));
			//t1 = ZonedDateTime.parse(level0.subSequence(16+16, 2*16+15));
		}
		//Log found time
		LOG.debug("T0: "+t0.toString());
		LOG.debug("T1: "+t1.toString());
		
		List<AuxFile> results = new ArrayList<>();

		for (AuxType t: types.getValues()) 
		{
			LOG.debug(">> Starting retrieving AuxFiles list for type : "+t.LongName);
			List<AuxFile> files_repro = getListOfAuxFiles(t,platformShortName,platformSerialIdentifier,t.Rule);
			LOG.debug(">> Done, elements: "+String.valueOf(files_repro.size()));
			RuleApplierInterface rule_applier = RuleApplierFactory.getRuleApplier(t.Rule);
			LOG.debug(">> Starting applying rule");
			List<AuxFile> files_repro_filtered = rule_applier.apply(files_repro,t0,t1,delta0,delta1);
			LOG.debug(">> Done");
			try {
				LOG.debug(">> Starting retrieving auxip links");
				auxip.setAuxFileUrls(files_repro_filtered, this.accessToken);
				LOG.debug(">> Done");
				results.addAll(files_repro_filtered);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		LOG.info("<< Ending ReproBaselineAccess.getReprocessingDataBaseline");

		return results;

	}

}
