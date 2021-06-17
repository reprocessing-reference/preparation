package com.csgroup.reprodatabaseline.http;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.qos.logback.core.joran.conditional.ElseAction;

import com.csgroup.reprodatabaseline.config.UrlsConfiguration;
import com.csgroup.reprodatabaseline.datamodels.AuxFile;
import com.csgroup.reprodatabaseline.datamodels.AuxType;
import com.csgroup.reprodatabaseline.datamodels.AuxTypeDeltas;
import com.csgroup.reprodatabaseline.datamodels.AuxTypes;
import com.csgroup.reprodatabaseline.datamodels.L0Product;
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
	private final EntityManagerFactory entityManagerFactory;

	// for internal use
	private Map<String,AuxTypeDeltas> auxTypesDeltas = null;
	private String accessToken;
	// ReproBaselineAccess entity can be used several times for the same productType
	// so for the performences optimization and to avoid requesting for the same data , AuxTypes and AuxFiles should be keeped in memory
	private final Map<String,AuxTypes> cachedAuxTypes;
	private final Map<String,List<AuxFile>> cachedAuxFiles;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public ReproBaselineAccess(HttpHandler handler, UrlsConfiguration conf,AuxipAccess auip,EntityManagerFactory entityManager) {
		this.httpHandler = handler;
		this.config = conf;
		this.auxip = auip;
		this.entityManagerFactory = entityManager;

		this.cachedAuxFiles = new HashMap<>();
		this.cachedAuxTypes = new HashMap<>();

	}

	public AuxTypes getListOfAuxTypes(final String mission){
		String res = httpHandler.getPost(config.getReprocessing_baseline_url()+
				"/AuxTypes?$expand=ProductTypes&$filter=Mission eq \'"+
				mission+"\'",this.accessToken);
		AuxTypes res_aux = AuxTypes.loadValues(res);

		if(mission.contains("S3"))
		{
			// add S3ALL types
			String s3All = httpHandler.getPost(config.getReprocessing_baseline_url()+
			"/AuxTypes?$expand=ProductTypes&$filter=Mission eq \'S3ALL\'",this.accessToken);
			res_aux.add(AuxTypes.loadValues(s3All).getValues());
		}

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

	public void setAuxTypesDeltas(String mission)
	{
		LOG.info(">> Starting ReproBaselineAccess.setAuxTypesDeltas");
		// do this once , only if auxTypesDeltas is not already set
		if( this.auxTypesDeltas == null )
		{
			String queryString = "SELECT DISTINCT entity FROM com.csgroup.reprodatabaseline.datamodels.AuxTypeDeltas entity "
			+ "WHERE entity.isCurrent = true AND entity.mission = \'MISSION\' ORDER BY entity.creationDateTime ASC";
			queryString = queryString.replace("MISSION", mission);
	
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			List<AuxTypeDeltas> deltasList = null;
			try {	
				Query query = entityManager.createQuery(queryString);
				deltasList = query.getResultList();

				this.auxTypesDeltas = new HashMap<>();
				for(AuxTypeDeltas deltas : deltasList)
				{
					this.auxTypesDeltas.put(deltas.getAuxType(), deltas);
				}

			} finally {
				entityManager.close();
			}

		}

		LOG.info("<< Ending ReproBaselineAccess.setAuxTypesDeltas");

		
	}

	public List<L0Product> getLevel0Products(String start,String stop, String mission,String unit,String productType)
	{
		final String satellite = mission.substring(0, 2);
		final String instrument = mission.substring(2, 4);

		String queryString = "";
		if(mission.contains("S3"))
		{
			String startsWith = satellite + unit + "_" + instrument + "_0_";
			if( instrument.equals("OL")) startsWith += "EFR";
			else if( instrument.equals("SL")) startsWith += "SLT";
			else if( instrument.equals("MW")) startsWith += "MWR";
			else { //SR
				if( productType.contains("CAL")) startsWith += "CAL";
				else startsWith += "SRA";
			}

			queryString = "SELECT DISTINCT entity FROM com.csgroup.reprodatabaseline.datamodels.L0Product entity "
			+ "WHERE entity.validityStart >= \'start\' AND  entity.validityStop <= \'stop\'"
			+ "AND entity.name LIKE \'literal%\'";
			queryString = queryString.replace("start", start).replace("stop", stop).replace("literal", startsWith);
		}else
		{
			String startsWith = satellite + unit;
			// for S1 and S2 we dont care about the product type 
			queryString = "SELECT DISTINCT entity FROM com.csgroup.reprodatabaseline.datamodels.L0Product entity "
			+ "WHERE entity.validityStart >= \'start\' AND  entity.validityStop <= \'stop\'"
			+ "AND entity.name LIKE \'literal%\'";
			queryString = queryString.replace("start", start).replace("stop", stop).replace("literal", startsWith);
		}

		// LOG.debug(">> queryString " + queryString);
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List<L0Product> l0_products;
		try {	
			Query query_m1 = entityManager.createQuery(queryString);
			l0_products = query_m1.getResultList();
			LOG.debug("Number of level0 products found : "+String.valueOf(l0_products.size()));
		} finally {
			entityManager.close();
		}
		return l0_products;
	}


	public List<AuxFile> getReprocessingDataBaseline(String level0,String mission,String unit,String productType) {
		// 1 -> get mission and sat_unit
		// 2 -> get AuxType for this mission
		// 3 -> get AuxFiles
		// 4 -> apply rules
		// 5 -> return the selected Auxfiles

		LOG.info(">> Starting ReproBaselineAccess.getReprocessingDataBaseline");
		
		// the output AuxFile listing 
		List<AuxFile> results = new ArrayList<>();
		
		String platformShortName = level0.substring(0, 2); // "S1", "S2" or "S3"
        String platformSerialIdentifier = level0.substring(2, 3); //"A" or "B" or "_"

		// Check the matching between the level0 and mission/unit 
		if( !platformShortName.equals(mission.substring(0, 2)) || !platformSerialIdentifier.equals(unit) )
		{
			LOG.info(">> ReproBaselineAccess.getReprocessingDataBaseline : mismatching between level0 product " + level0 + " and mission/unit " + mission + "/" + unit);
			// return an empty collection
			return results;
		}
		// set/get deltas to be applied with selection rules for a given productType
		this.setAuxTypesDeltas(mission);

		// String mission = getMission(platformShortName, productType);
		AuxTypes types ;
		// 
		if( this.cachedAuxTypes.containsKey(mission) )
		{
			types = this.cachedAuxTypes.get(mission);
		}else
		{
			types = getListOfAuxTypes(mission);
			this.cachedAuxTypes.put(mission, types);
		}
			
		ZonedDateTime t0;
		ZonedDateTime t1;
		if( platformShortName.equals("S3"))
		{
			// S3B_OL_0_EFR____20210418T201042_20210418T201242_20210418T215110_0119_051_242______LN1_O_NR_002.SEN3
			t0 = ZonedDateTime.parse(level0.subSequence(16, 16+15),DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC")));
			t1 = ZonedDateTime.parse(level0.subSequence(32, 32+15),DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC")));
		}else if( platformShortName.equals("S2"))
		{
			// S2A_OPER_MSI_L0__LT_MTI__20150725T193419_S20150725T181440_N01.01
			t0 = ZonedDateTime.parse(level0.subSequence(42, 42+15),DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC")));
			t1 = t0;
		}else
		{
			// S1A_IW_RAW__0SDV_20201102T203348_20201102T203421_035074_0417B3_02B4.SAFE.zip
			t0 = ZonedDateTime.parse(level0.subSequence(17, 17+15),DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC")));
			t1 = ZonedDateTime.parse(level0.subSequence(33, 33+15),DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC")));
		}

		for (AuxType t: types.getValues()) 
		{
			// take into account only auxiliary data files with requested product type 
			// but take care about auxtype from mission S3ALL 
			final String level = productType.substring(0,2);
			if( t.ProductTypes.contains(productType) || ( t.Mission.equals("S3ALL") && t.ProductTypes.contains(level) ) )
			{
				
				Duration delta0 = Duration.ofSeconds(this.auxTypesDeltas.get(t.LongName).getDelta0()); 
				Duration delta1 = Duration.ofSeconds(this.auxTypesDeltas.get(t.LongName).getDelta1()); 
				
				// check if auxtype is not already treated
				List<AuxFile> files_repro;
				String key = t.LongName+platformShortName+platformSerialIdentifier ;
				if( this.cachedAuxFiles.containsKey(key))
				{
					files_repro = this.cachedAuxFiles.get(key);
				}else{
					files_repro = getListOfAuxFiles(t,platformShortName,platformSerialIdentifier,t.Rule);
					this.cachedAuxFiles.put(key, files_repro);
				}
			
				RuleApplierInterface rule_applier = RuleApplierFactory.getRuleApplier(t.Rule);
				List<AuxFile> files_repro_filtered = rule_applier.apply(files_repro,t0,t1,delta0,delta1);
	
				try {
					auxip.setAuxFileUrls(files_repro_filtered, this.accessToken);
					results.addAll(files_repro_filtered);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		LOG.info("<< Ending ReproBaselineAccess.getReprocessingDataBaseline");

		return results;

	}

}
