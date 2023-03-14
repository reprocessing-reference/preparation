package com.csgroup.reprodatabaseline.http;

import java.text.MessageFormat;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.commons.io.FilenameUtils;
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
	private Map<String,Map<String,AuxTypeDeltas>> cachedAuxTypesDeltas;
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
		this.cachedAuxTypesDeltas = new HashMap<>();

	}

	public AuxTypes getListOfAuxTypes(final String mission){
		LOG.info(">> Starting ReproBaselineAccess.getListOfAuxTypes");

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
		LOG.info("<< Ending ReproBaselineAccess.getListOfAuxTypes");

		return res_aux;
	}

	public List<AuxFile> getListOfAuxFiles(final AuxType type, final String sat, final String unit, RuleEnum rl){
		LOG.info(">> Starting ReproBaselineAccess.getListOfAuxFiles");

		// remove _S1 and _S2 from AUX_RESORB_S1,AUX_PREORB_S2,AUX_PREORB_S1,AUX_RESORB_S2,AUX_POEORB_S1 )
		String longName = type.LongName;
		if(longName.contains("AUX_RESORB") || longName.contains("AUX_PREORB") || longName.contains("AUX_POEORB") )
		{
			longName = longName.split("_S")[0];
		}
		//Maybe it-s shortName on type ?
		String res = httpHandler.getPost(config.getReprocessing_baseline_url()+
				"/AuxFiles?$expand=AuxType&$filter=startswith(FullName,\'"+sat+
				"_\') and contains(FullName,\'"+longName+"\')",this.accessToken);
		List<AuxFile> res_aux = AuxFile.loadValues(type,res);
		res = httpHandler.getPost(config.getReprocessing_baseline_url()+
				"/AuxFiles?$expand=AuxType&$filter=startswith(FullName,\'"+sat+unit+
				"\') and contains(FullName,\'"+longName+"\')",this.accessToken);
		List<AuxFile> res_aux_unit = AuxFile.loadValues(type,res);
		res_aux.addAll(res_aux_unit);
		LOG.info(String.valueOf(res_aux.size()));
		LOG.info("<< Ending ReproBaselineAccess.getListOfAuxFiles");

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

	public Map<String,AuxTypeDeltas> getAuxTypesDeltas(String mission)
	{
		// do this once for each mission , only if auxTypesDeltas is not already set
		LOG.info(">> Starting ReproBaselineAccess.setAuxTypesDeltas");
		if ( this.cachedAuxTypesDeltas.containsKey(mission) )
		{
			LOG.info("<< Ending ReproBaselineAccess.setAuxTypesDeltas");
			return this.cachedAuxTypesDeltas.get(mission);
		}else
		{
			String queryString = "SELECT DISTINCT entity FROM com.csgroup.reprodatabaseline.datamodels.AuxTypeDeltas entity "
			+ "WHERE entity.isCurrent = true AND entity.mission = \'MISSION\' ORDER BY entity.creationDateTime ASC";
			queryString = queryString.replace("MISSION", mission);
	
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			Map<String,AuxTypeDeltas> auxTypesDeltas = new HashMap<>();
			List<AuxTypeDeltas> deltasList = null;
			try {	
				Query query = entityManager.createQuery(queryString);
				deltasList = query.getResultList();
				for(AuxTypeDeltas deltas : deltasList)
				{
					auxTypesDeltas.put(deltas.getAuxType(), deltas);
				}
			} finally {
				entityManager.close();
			}
			LOG.info("<< Ending ReproBaselineAccess.setAuxTypesDeltas");
			this.cachedAuxTypesDeltas.put(mission, auxTypesDeltas);
			return auxTypesDeltas;
		}		
	}

	public List<L0Product> getLevel0Products(String start,String stop, String mission,String unit,String productType)
	{
		final String satellite = mission.substring(0, 2);
		final String instrument = mission.substring(2, 4);

		String queryString = "";
		if(mission.contains("S3"))
		{
			String startsWith = satellite + unit + "_" + instrument + "_0_";
			if( instrument.equals("OL") || instrument.equals("SY") ) startsWith += "EFR";
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
	
	public List<L0Product> getLevel0ProductsByName(String level0Name) {
		
		String reformatedLevel0Name = level0Name.replace("\\\"", "");
		reformatedLevel0Name = FilenameUtils.removeExtension(reformatedLevel0Name);
		
		String queryString = "SELECT DISTINCT entity FROM com.csgroup.reprodatabaseline.datamodels.L0Product entity "
				+ "WHERE entity.name LIKE \'%level0Name%\'";
		
		queryString = queryString.replace("level0Name", reformatedLevel0Name);
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List<L0Product> l0_products;
		try {	
			Query query = entityManager.createQuery(queryString);
			l0_products = query.getResultList();
			LOG.debug(MessageFormat.format("{0} L0 products match \"{1}\" in the database.", String.valueOf(l0_products.size()), reformatedLevel0Name));
		} finally {
			entityManager.close();
		}
		
		return l0_products;
	}
	
	private class T0T1DateTime {
		public ZonedDateTime _t0;
		public ZonedDateTime _t1;
	}

	public List<AuxFile> getReprocessingDataBaseline(L0Product level0,String mission,String unit,String productType) {
		// 1 -> get mission and sat_unit
		// 2 -> get AuxType for this mission
		// 3 -> get AuxFiles
		// 4 -> apply rules
		// 5 -> return the selected Auxfiles

		LOG.info(">> Starting ReproBaselineAccess.getReprocessingDataBaseline");
		
		// the output AuxFile listing 
		List<AuxFile> results = new ArrayList<>();
		
		String platformShortName = level0.getName().substring(0, 2); // "S1", "S2" or "S3"
        String platformSerialIdentifier = level0.getName().substring(2, 3); //"A" or "B" or "_"

		// Check the matching between the level0 and mission/unit 
		if( !platformShortName.equals(mission.substring(0, 2)) || !platformSerialIdentifier.equals(unit) )
		{
			LOG.info(">> ReproBaselineAccess.getReprocessingDataBaseline : mismatching between level0 product " + level0.getName() + " and mission/unit " + mission + "/" + unit);
			// return an empty collection
			return results;
		}
		// get deltas to be applied with selection rules for a given mission
		Map<String, AuxTypeDeltas> auxTypesDeltas = this.getAuxTypesDeltas(mission);
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
		
		T0T1DateTime t0t1;
		
		if( platformShortName.equals("S3"))
		{
			t0t1 = getT0T1ForS3(level0);
			
		}else if( platformShortName.equals("S2"))
		{
			t0t1 = getT0T1ForS2(level0);
			
		}else
		{
			t0t1 = getT0T1ForS1(level0);
			
		}

		try {

			for (AuxType t: types.getValues()) 
			{
				// take into account only auxiliary data files with requested product type 
				// but take care about auxtype from mission S3ALL 
				final String level = productType.substring(0,2);
				if( t.ProductTypes.contains(productType) || ( t.Mission.equals("S3ALL") && t.ProductTypes.contains(level) ) )
				{
					Duration delta0 = Duration.ofSeconds(auxTypesDeltas.get(t.LongName).getDelta0()); 
					Duration delta1 = Duration.ofSeconds(auxTypesDeltas.get(t.LongName).getDelta1()); 
					
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
					List<AuxFile> files_repro_filtered;

					if (!files_repro.isEmpty()) {
						if (files_repro.get(0).FullName.matches(".*B..\\..*")) {
							// The type has band files

							// We need to group the files by band and apply the rule on each group
							Map<String, List<AuxFile>> sortedFilesByBand = sortFilesByBand(files_repro);

							files_repro_filtered = new ArrayList<AuxFile>();

							for (String band : sortedFilesByBand.keySet()) {
								files_repro_filtered.addAll(rule_applier.apply(sortedFilesByBand.get(band),t0t1._t0,t0t1._t1,delta0,delta1));
							}


						} else {
							// The type does not have band files

							// We need to apply the rule on every file at once
							files_repro_filtered = rule_applier.apply(files_repro,t0t1._t0,t0t1._t1,delta0,delta1);

						}
					
						try {
							auxip.setAuxFileUrls(files_repro_filtered, this.accessToken);
							results.addAll(files_repro_filtered);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
	
			}	
		} catch (Exception e) {
			//TODO: handle exception
			e.printStackTrace();
			LOG.debug("Exception : "+e.getLocalizedMessage());
		}


		LOG.info("<< Ending ReproBaselineAccess.getReprocessingDataBaseline");

		return results;

	}

	private T0T1DateTime getT0T1ForS3(L0Product level0) {
		
		T0T1DateTime t0t1 = new T0T1DateTime();
		
		// We should read t0 and t1 from the validityStart and validityStop of the L0Product, but having the L0Product object is new and not
		// necessary for the following operation. To keep the current service stable, we left it the way it was since the launching of the service.
		
		// S3B_OL_0_EFR____20210418T201042_20210418T201242_20210418T215110_0119_051_242______LN1_O_NR_002.SEN3
		t0t1._t0 = ZonedDateTime.parse(level0.getName().subSequence(16, 16+15),DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC")));
		t0t1._t1 = ZonedDateTime.parse(level0.getName().subSequence(32, 32+15),DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC")));
		
		return t0t1;
	}

	private T0T1DateTime getT0T1ForS2(L0Product level0) {
		
		T0T1DateTime t0t1 = new T0T1DateTime();
		
		if (level0 != null && level0.getValidityStart() != null) {
			// L0Product was found on data base
			
			// Retrieve the t0t1 from the data base content
			t0t1._t0 = level0.getValidityStart().atZone(ZoneId.of("UTC"));
			t0t1._t1 = level0.getValidityStop().atZone(ZoneId.of("UTC"));
			
		} else {
			// L0Product not found 

			// We read the t0t1 from the validity date contained in the file name
			
			// S2A_OPER_MSI_L0__LT_MTI__20150725T193419_S20150725T181440_N01.01
			t0t1._t0 = ZonedDateTime.parse(level0.getName().subSequence(42, 42+15),DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC")));
			t0t1._t1 = t0t1._t0;
		}
		
		return t0t1;
	}

	private T0T1DateTime getT0T1ForS1(L0Product level0) {
		
		T0T1DateTime t0t1 = new T0T1DateTime();
		
		// We should read t0 and t1 from the validityStart and validityStop of the L0Product, but having the L0Product object is new and not
		// necessary for the following operation. To keep the current service stable, we left it the way it was since the launching of the service.
		
		// S1A_IW_RAW__0SDV_20201102T203348_20201102T203421_035074_0417B3_02B4.SAFE.zip
		t0t1._t0 = ZonedDateTime.parse(level0.getName().subSequence(17, 17+15),DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC")));
		t0t1._t1 = ZonedDateTime.parse(level0.getName().subSequence(33, 33+15),DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC")));
		
		return t0t1;
	}

	private Map<String, List<AuxFile>> sortFilesByBand(List<AuxFile> files) {
		Map<String, List<AuxFile>> sortedFiles = new HashMap<>();
		Pattern pattern = Pattern.compile("B..\\.");
		for (AuxFile file : files) {
			Matcher matcher = pattern.matcher(file.FullName);
			if (matcher.find()) {
				String bandName = matcher.group();
				if (!sortedFiles.containsKey(bandName)) {
					sortedFiles.put(bandName, new ArrayList<AuxFile>());
				}
				sortedFiles.get(bandName).add(file);
			}
		}
		return sortedFiles;
	}

}
