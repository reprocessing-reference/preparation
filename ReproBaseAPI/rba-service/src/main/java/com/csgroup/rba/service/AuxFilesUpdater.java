package com.csgroup.rba.service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.csgroup.jpadatasource.StorageStatus;
import com.csgroup.rba.config.AuxFilesUpdateConfiguration;
import com.csgroup.rba.model.jpa.AuxFileJPA;
import com.csgroup.rba.model.jpa.serializer.AuxFileJPASerializer;
import com.csgroup.rba.model.jpa.AuxTypeJPA;
import com.csgroup.rba.model.jpa.TimeValidityJPA;

/**
 * @author beon
 *
 */
@Component
public class AuxFilesUpdater {
	private static final Logger LOG = LoggerFactory.getLogger(AuxFilesUpdater.class);

	private static List<String> ERRMAT_TYPES = 
			new ArrayList<>(List.of(
					"AMH_ERRMAT_MPC",
					"AMV_ERRMAT_MPC",
					"MW_1_DNB_AX",
					"MW_1_MON_AX",
					"MW_1_NIR_AX",
					"SR_1_CA1LAX",
					"SR_1_CA1SAX",
					"SR_1_CA2CAX",
					"SR_1_CA2KAX",
					"SR_1_CA2KAX",
					"SR_2_POL_AX",
					"SR_1_USO_AX"));

	@Autowired
	private EntityManagerFactory entityManagerFactory;
	@Autowired
	private AuxFilesUpdateConfiguration config;
	@Autowired
	private StorageStatus status;

	private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

	private final static ZonedDateTime PAST_DATE = LocalDateTime.parse("1983-01-01T00:00:00",formatter).atZone( ZoneId.systemDefault());
	private final static ZonedDateTime FUTUR_DATE = LocalDateTime.parse("2100-01-01T00:00:00",formatter).atZone( ZoneId.systemDefault());

	//@Scheduled(cron = "0 0 0 * * *", zone = "Europe/Paris")
	@Scheduled(fixedRate = 300000, initialDelay = 5000)
	public void updateAuxFiles() {
		if (!config.getActive())
		{
			LOG.info("Updating is not activated in config");
			return;
		}
		if (!status.hasChanges())
		{
			LOG.info("Updating skip : no change in base");
			return;
		}
		LocalDateTime now = LocalDateTime.now();		
		LOG.info("The time is now {}", now.toString());		

		int total_updated = 0;
		int total_removed = 0;

		String queryString_AuxTypes= "SELECT DISTINCT entity FROM com.csgroup.rba.model.jpa.AuxTypeJPA entity";	//Create entityManager
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Query query_m1 = entityManager.createQuery(queryString_AuxTypes);

		List<AuxTypeJPA> products_m;
		try {	
			products_m = query_m1.getResultList();				
			LOG.info("Number of AuxTypes found: "+products_m.size());			
			//Treat per type
			for (AuxTypeJPA type : products_m) {
				LOG.info("Treating : "+type.getLongName());
				LOG.info("Time validity: "+type.getValidity().toString());
				String queryString_AuxFiles ="SELECT DISTINCT e1 FROM com.csgroup.rba.model.jpa.AuxFileJPA e1 "
						+ "JOIN e1.AuxType e2 WHERE e2.LongName = :e2Type";
				Query query_files = entityManager.createQuery(queryString_AuxFiles);
				query_files.setParameter("e2Type", type.getLongName());
				List<AuxFileJPA> aux_files = query_files.getResultList();
				LOG.info("Number of AuxFiles found: "+aux_files.size());
				//Sort per unit
				Map<String,List<AuxFileJPA> > aux_files_per_unit = new HashMap<String,List<AuxFileJPA> >();
				for (AuxFileJPA aux : aux_files) {
					if (!aux_files_per_unit.containsKey(aux.getUnit())) {
						aux_files_per_unit.put(aux.getUnit(), new ArrayList<AuxFileJPA>());
					}
					aux_files_per_unit.get(aux.getUnit()).add(aux);
				}
				for (Entry<String, List<AuxFileJPA>> files : aux_files_per_unit.entrySet())
				{
					//Sort per band
					Map<String,List<AuxFileJPA> > aux_files_per_unit_per_band = new HashMap<String,List<AuxFileJPA> >();
					for (AuxFileJPA aux : files.getValue()) {
						if (!aux_files_per_unit_per_band.containsKey(aux.getBand())) {
							aux_files_per_unit_per_band.put(aux.getBand(), new ArrayList<AuxFileJPA>());
						}
						aux_files_per_unit_per_band.get(aux.getBand()).add(aux);
					}
					//Treat lists					
					for (Entry<String, List<AuxFileJPA>> files_band : aux_files_per_unit_per_band.entrySet())
					{
						List<AuxFileJPA> result_to_remove = new ArrayList<AuxFileJPA>();
						List<AuxFileJPA> result_to_update = null;
						if (ERRMAT_TYPES.contains(type.getLongName())) {							
							result_to_update = treatOneAuxTypeOneBandERRMAT(files_band.getValue(), type, 
									files_band.getKey(), result_to_remove);	
						} else {							
							result_to_update = treatOneAuxTypeOneBand(files_band.getValue(), type, 
									files_band.getKey(), result_to_remove);
						}
						LOG.info("Type "+type.getLongName()+" | "+files_band.getKey()+" | "+ files.getKey() +" has "+String.valueOf(result_to_update.size())+" to be updated");
						LOG.info("Type "+type.getLongName()+" | "+files_band.getKey()+" | "+ files.getKey() +" has "+String.valueOf(result_to_remove.size())+" to be removed");
						//Create year folder
						if ( result_to_remove.size()>0) {
							Path dir_path = Paths.get(config.getTempFolder(),type.getLongName(), files.getKey() , files_band.getKey());
							try {
								Files.createDirectories(dir_path);
							} catch (Exception e) {
								LOG.error("Couldnt create folder " +dir_path.toString());	  
							}

							for (AuxFileJPA remo : result_to_remove) {
								LOG.info("Removing: "+remo.getFullName());
								String filename = remo.getFullName()+"_"+remo.getIdentifier().toString()+".json";
								LOG.debug("Exporting ADG to : "+filename);
								Path file_path = Paths.get(config.getTempFolder(),type.getLongName(), files.getKey() , files_band.getKey(),filename);
								try (FileOutputStream fos = new FileOutputStream(file_path.toString())) {
									AuxFileJPASerializer serializer = new AuxFileJPASerializer();
									serializer.SerializeAuxFile(remo, fos);
								} catch (Exception e) {
									LOG.error("Couldnt write products to file " +e.getLocalizedMessage());	    	
								}
								entityManager.getTransaction().begin();
								entityManager.remove(remo);
								entityManager.getTransaction().commit();
							}							
						}
						entityManager.getTransaction().begin();
						for (AuxFileJPA remo : result_to_update) {
							LOG.info("Updating: "+remo.getFullName());
							entityManager.merge(remo);
						}
						entityManager.getTransaction().commit();
						total_updated = total_updated + result_to_update.size();
						total_removed = total_removed + result_to_remove.size();						
					}					
				}
			}
		} finally {
			entityManager.close();
		}
		LOG.info("Number of product removed: "+String.valueOf(total_removed));
		LOG.info("Number of product updated: "+String.valueOf(total_updated));
		LOG.info("Done in {} min {} seconds", Duration.between(now, LocalDateTime.now()).toMinutes(), Duration.between(now, LocalDateTime.now()).toSecondsPart());
		status.jobDone();
	}


	/**
	 * Get the aux to keep in list
	 * @param input
	 * @param type
	 * @param band
	 * @return
	 */
	private List<AuxFileJPA> treatOneAuxTypeOneBand(List<AuxFileJPA> input, final AuxTypeJPA type, final String band,
			List<AuxFileJPA> files_to_remove) {
		List<AuxFileJPA> output = new ArrayList<AuxFileJPA>();
		if (input.size() == 0) {
			LOG.debug("no file");
			return output;
		}
		if (input.size() == 1) {
			LOG.debug("only one file");
			LOG.debug("not updating : " + input.get(0).getFullName());
			return output;
		}
		TimeValidityJPA time_validity = type.getValidity();
		Comparator<AuxFileJPA> compa_validstart = Comparator.comparing(AuxFileJPA::getValidityStart);
		input.sort(compa_validstart);
		//Aux is good anytime, get the latest
		if (time_validity == TimeValidityJPA.AnyTime) {
			if (!input.get(input.size() -1).getSensingTimeApplicationStart().equals(PAST_DATE) 
					|| !input.get(input.size() -1).getSensingTimeApplicationStop().equals(FUTUR_DATE)) {
				LOG.debug("Only updating : " + input.get(input.size() -1).getFullName());
				input.get(input.size() -1).setSensingTimeApplicationStart(PAST_DATE);
				input.get(input.size() -1).setSensingTimeApplicationStop(FUTUR_DATE);
				//Write down
				output.add(input.get(input.size() -1));
			}
		}
		else {
			//Loop on aux to find the one to keep
			for (int idx = 0; idx < input.size()-1;idx = idx+1) {
				AuxFileJPA first = input.get(idx);
				AuxFileJPA next = input.get(idx+1);
				LOG.debug("Testing : "+first.getFullName()+" : "+first.getIdentifier().toString());
				LOG.debug("Dates: "+first.getSensingTimeApplicationStart().toString()
						+" : "+first.getSensingTimeApplicationStop().toString()
						+" : "+first.getValidityStart().toString()
						+" : "+first.getValidityStop().toString());
				if (isTheLatest(first, idx, input)) {
					int n = 2;
					while ((idx + n) < input.size() && first.getValidityStart().equals(next.getValidityStart())) {
						next = input.get(idx + n);
						n = n + 1;
					}
					ZonedDateTime stop = first.getValidityStop();
					if ( first.getValidityStop().compareTo(next.getValidityStart()) >= 0 
							&&  !first.getValidityStart().isEqual(next.getValidityStart())) {
						stop = next.getValidityStart();
					}
					else {
						stop = first.getValidityStop();
					}
					if (!first.getSensingTimeApplicationStart().equals(first.getValidityStart()) 
							|| !first.getSensingTimeApplicationStop().equals(stop)) {
						first.setSensingTimeApplicationStart(first.getValidityStart());
						first.setSensingTimeApplicationStop(stop);
						//LOG it					
						LOG.debug("Sensing validity for file : " + first.getFullName() + " : " + 
								first.getSensingTimeApplicationStart() + " : " + first.getSensingTimeApplicationStop());
						// Register
						output.add(first);
					}
				} else {
					files_to_remove.add(first);
					LOG.debug("Removing : "+first.getFullName());
				}
			}
			//Last of the list
			AuxFileJPA first = input.get(input.size()-1);
			LOG.debug("Testing : "+first.getFullName()+" : "+first.getIdentifier().toString());
			LOG.debug("Dates: "+first.getSensingTimeApplicationStart().toString()
					+" : "+first.getSensingTimeApplicationStop().toString()
					+" : "+first.getValidityStart().toString()
					+" : "+first.getValidityStop().toString());
			if (isTheLatest(first, input.size()-1, input)) {

				if (!first.getSensingTimeApplicationStart().equals(first.getValidityStart()) 
						|| !first.getSensingTimeApplicationStop().equals(first.getValidityStop())) {
					first.setSensingTimeApplicationStart(first.getValidityStart());
					first.setSensingTimeApplicationStop(first.getValidityStop());
					//LOG it
					LOG.debug("Sensing validity for file : " + first.getFullName() + " : " + 
							first.getSensingTimeApplicationStart() + " : " + first.getSensingTimeApplicationStop());
					// Register
					output.add(first);
				}
			} else {
				files_to_remove.add(first);
				LOG.debug("Removing : "+first.getFullName());
			}

		}

		return output;
	}


	/**
	 * Special cases for aux not respecting the normal naming convention
	 * 
	 * @param input
	 * @param type
	 * @param band
	 * @return List<AuxFileJPA>
	 */
	private List<AuxFileJPA> treatOneAuxTypeOneBandERRMAT(List<AuxFileJPA> input, final AuxTypeJPA type, 
			final String band, List<AuxFileJPA> files_to_remove) {
		List<AuxFileJPA> output = new ArrayList<AuxFileJPA>();
		if (input.size() == 0) {
			LOG.debug("no file");
			return output;
		}
		if (input.size() == 1) {
			LOG.debug("only one file");
			LOG.debug("not updating : " + input.get(0).getFullName());
			return output;
		}
		Comparator<AuxFileJPA> compa_validstart = Comparator.comparing(AuxFileJPA::getValidityStop);
		input.sort(compa_validstart);
		for (int idx = 0; idx < input.size()-1;idx = idx+1) {
			AuxFileJPA first = input.get(idx);
			AuxFileJPA next = input.get(idx+1);
			LOG.debug("Testing : "+first.getFullName());
			if (isTheLatest(first, idx, input)) { 
				int n = 2;
				while ((idx + n) < input.size() 
						&& first.getValidityStop().equals(next.getValidityStop())) {
					next = input.get(idx+n);
					n = n + 1;
				}				
				if (!first.getSensingTimeApplicationStart().equals(first.getValidityStop()) 
						|| !first.getSensingTimeApplicationStop().equals(next.getValidityStop())) {
					LOG.debug("ERRMAT Sensing validity for file : " + first.getFullName() + " : " + 
							first.getSensingTimeApplicationStart() + " : " + first.getSensingTimeApplicationStop());
					first.setSensingTimeApplicationStart(first.getValidityStop());
					first.setSensingTimeApplicationStop(next.getValidityStop());
					LOG.debug("ERRMAT Sensing validity for file : " + first.getFullName() + " : " + 
							first.getSensingTimeApplicationStart() + " : " + first.getSensingTimeApplicationStop());
					// Register down
					output.add(first);
				}
			} else {
				LOG.debug("Removing : "+first.getFullName());
				files_to_remove.add(first);
			}
		}
		AuxFileJPA first = input.get(input.size()-1);
		LOG.debug("Testing : "+first.getFullName());
		if (isTheLatest(first, input.size() - 1, input)) {
			if (!first.getSensingTimeApplicationStart().equals(first.getValidityStop()) 
					|| !first.getSensingTimeApplicationStop().equals(FUTUR_DATE)) {
				first.setSensingTimeApplicationStart(first.getValidityStop());
				first.setSensingTimeApplicationStop(FUTUR_DATE);
				LOG.debug("ERRMAT Sensing validity for file : " + first.getFullName() + " : " + 
						first.getSensingTimeApplicationStart() + " : " + first.getSensingTimeApplicationStop());
				// Register down
				output.add(first);
			}
		} else {
			LOG.debug("Removing : "+first.getFullName());
			files_to_remove.add(first);
		}

		return output;
	}

	/**
	 * Test if file is the latest created for this validity period
	 * @param file
	 * @param idx
	 * @param sorted_list
	 * @return latest
	 */
	/**
	 * @param file
	 * @param idx
	 * @param sorted_list
	 * @return
	 */
	private boolean isTheLatest(AuxFileJPA file, final int idx, final List<AuxFileJPA> sorted_list) {
		int tmp_idx = idx - 1;
		while (tmp_idx >= 0) {
			if (file.getValidityStart().equals(sorted_list.get(tmp_idx).getValidityStart())
					&&  file.getValidityStop().equals(sorted_list.get(tmp_idx).getValidityStop())) {
				LOG.debug("Two files have same properties : " + file.getFullName() + " : " + sorted_list.get(tmp_idx).getFullName());
				if (file.getCreationDate().compareTo(sorted_list.get(tmp_idx).getCreationDate()) < 0) {
					LOG.debug("Not the latest : " + file.getFullName());
					return false;
				}
			}
			else {
				break;
			}
			tmp_idx = tmp_idx - 1;
		}
		tmp_idx = idx + 1;
		while (tmp_idx < sorted_list.size()) {
			if (file.getValidityStart().equals(sorted_list.get(tmp_idx).getValidityStart())
					&&  file.getValidityStop().equals(sorted_list.get(tmp_idx).getValidityStop())) {
				LOG.debug("Two files have same properties : " + file.getFullName() + " : " + sorted_list.get(tmp_idx).getFullName());
				if (file.getCreationDate().compareTo(sorted_list.get(tmp_idx).getCreationDate()) < 0) {
					LOG.debug("Not the latest : " + file.getFullName());
					return false;
				}
			}
			else {
				break;
			}
			tmp_idx = tmp_idx + 1;
		}
		// No one to prove the contrary
		LOG.debug("Latest : " + file.getFullName());
		return true;
	}


}
