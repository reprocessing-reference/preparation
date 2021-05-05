package com.csgroup.auxip.archive;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.QueryHint;

import org.apache.olingo.commons.api.data.EntityCollection;
import org.hibernate.jpa.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.csgroup.auxip.config.ArchiveConfiguration;
import com.csgroup.auxip.model.jpa.Product;
import com.csgroup.auxip.model.repository.StorageStatus;
import com.csgroup.auxip.serializer.ProductSerializer;

@Component
public class ArchiveCreator {
	private static final Logger LOG = LoggerFactory.getLogger(ArchiveCreator.class);

	private static List<String> SATS = new ArrayList<>(List.of("S1_","S1A","S1B","S2_","S2A","S2B","S3_","S3A","S3B"));
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	@Autowired
	private StorageStatus storageStatus;
	@Autowired
	private ArchiveConfiguration config;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(cron = "0 0 0 * * *", zone = "Europe/Paris")
	//@Scheduled(fixedRate = 3000000, initialDelay = 5000)
	public void reportCurrentTime() {
		if (!config.getActive())
		{
			LOG.info("Archiving is not activated in config");
			return;
		}
		LocalDateTime now = LocalDateTime.now();		
		LOG.info("The time is now {}", now.toString());		
		if (config.getOnTrigger() && !storageStatus.hasChanges())
		{
			LOG.info("No change in the repository, nothing to do");
			return;
		}

		LOG.info("Working in "+config.getTempFolder());
		Path working_path = Paths.get(config.getTempFolder());

		if (Files.notExists(working_path) || !Files.isDirectory(working_path))
		{
			LOG.error("Folder doesn't exist or is not a directory");
			return;
		}
		long total_exported = 0;
		long total_found = 0;
		//get current date
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter formatter_file = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		String queryString1= "SELECT DISTINCT entity FROM com.csgroup.auxip.model.jpa.Product entity "
				+ "JOIN entity.StringAttributes e1 WHERE ";				
		String queryString2 = "SELECT DISTINCT entity FROM com.csgroup.auxip.model.jpa.Product entity "
				+ "JOIN entity.IntegerAttributes e2 WHERE " ;
		String queryString3 = "SELECT DISTINCT entity FROM com.csgroup.auxip.model.jpa.Product entity "
				+ "JOIN entity.DoubleAttributes p1 WHERE " ;
		String queryString4= "SELECT DISTINCT entity FROM com.csgroup.auxip.model.jpa.Product entity "
				+ "JOIN entity.DateTimeOffsetAttributes e4 WHERE " ;
		//queryString = queryString.concat("entity.ContentDate.Start <= :publicationStop AND entity.ContentDate.Start >= :publicationStart");
		//List of years to do
		List<Integer> years = new ArrayList<>(List.of(1983,1987,2000));
		for (int y = 2010; y <= now.getYear();y++)
		{
			years.add(y);
		}
		//Loop on years
		for (int y : years)
		{
			//Loop on months
			for (int m = 1; m <= 12;m++)
			{
				//Create entityManager
				EntityManager entityManager = entityManagerFactory.createEntityManager();

				LOG.debug("Treating : "+String.valueOf(y)+"/"+String.valueOf(m));				
				LocalDateTime tmp_dt_m_start = LocalDateTime.of(y, m,1, 0,0);
				LocalDateTime tmp_dt_m_stop = tmp_dt_m_start.with(TemporalAdjusters.lastDayOfMonth()).withHour(23)
						.withMinute(59).withSecond(59);				
				Map<String, Object> queryParams_m = new HashMap<String,Object>();						
				String tmp_query1 = queryString1.concat("entity.ContentDate.Start <= '"+tmp_dt_m_stop.format(formatter)+"' ");
				tmp_query1= tmp_query1.concat("AND entity.ContentDate.Start >= '"+tmp_dt_m_start.format(formatter)+"'");
				String tmp_query2 = queryString2.concat("entity.ContentDate.Start <= '"+tmp_dt_m_stop.format(formatter)+"' ");
				tmp_query2= tmp_query2.concat("AND entity.ContentDate.Start >= '"+tmp_dt_m_start.format(formatter)+"'");
				String tmp_query3 = queryString3.concat("entity.ContentDate.Start <= '"+tmp_dt_m_stop.format(formatter)+"' ");
				tmp_query3= tmp_query3.concat("AND entity.ContentDate.Start >= '"+tmp_dt_m_start.format(formatter)+"'");
				String tmp_query4 = queryString4.concat("entity.ContentDate.Start <= '"+tmp_dt_m_stop.format(formatter)+"' ");
				tmp_query4= tmp_query4.concat("AND entity.ContentDate.Start >= '"+tmp_dt_m_start.format(formatter)+"'");
				LOG.debug(tmp_query1);
				LOG.debug(tmp_query2);
				LOG.debug(tmp_query3);
				LOG.debug(tmp_query4);
				LOG.debug("StartDate: "+tmp_dt_m_start.toString());
				LOG.debug("StopDate: "+tmp_dt_m_stop.format(formatter));				
				Query query_m1 = entityManager.createQuery(tmp_query1);
				for (Map.Entry<String, Object> entry_m : queryParams_m.entrySet()) {
					query_m1.setParameter(entry_m.getKey(), entry_m.getValue());
				}
				Query query_m2 = entityManager.createQuery(tmp_query2);
				for (Map.Entry<String, Object> entry_m : queryParams_m.entrySet()) {
					query_m2.setParameter(entry_m.getKey(), entry_m.getValue());
				}
				Query query_m3 = entityManager.createQuery(tmp_query3);
				for (Map.Entry<String, Object> entry_m : queryParams_m.entrySet()) {
					query_m3.setParameter(entry_m.getKey(), entry_m.getValue());
				}
				Query query_m4 = entityManager.createQuery(tmp_query4);
				for (Map.Entry<String, Object> entry_m : queryParams_m.entrySet()) {
					query_m4.setParameter(entry_m.getKey(), entry_m.getValue());
				}
				List<Product> products_m;
				try {	
					products_m = query_m1.getResultList();
					if (products_m.size() ==0)
					{
						continue;
					}
					products_m = query_m2.getResultList();
					products_m = query_m3.getResultList();
					products_m = query_m4.getResultList();					
					
					LOG.info("Number of products found for date ["+tmp_dt_m_start.toString()+"/"+tmp_dt_m_stop.toString()+"] : "+products_m.size());
					List<Product> products_nofilter = new ArrayList<>();
					products_nofilter.addAll(products_m);
					total_found = total_found + products_m.size();
					//Loop on days
					for (int d = tmp_dt_m_start.getDayOfMonth();d <= tmp_dt_m_stop.getDayOfMonth();d++)
					{
						LocalDateTime tmp_dt_start = LocalDateTime.of(y, m,d, 0, 0);
						LocalDateTime tmp_dt_stop = LocalDateTime.of(y, m,d, 23, 59,59,999999999);						
						LOG.debug("StartDate: "+tmp_dt_start.toString());
						LOG.debug("StopDate: "+tmp_dt_stop.format(formatter));
						//Loop on SAT
						for (String str_sat : SATS)
						{
							List<Product> products;

							products = filterProducts(products_m, str_sat, Timestamp.valueOf(tmp_dt_start.format(formatter)),
									Timestamp.valueOf(tmp_dt_stop.format(formatter)),products_nofilter);

							LOG.info("Number of products found for sat "+str_sat+" for date ["+tmp_dt_start.toString()+"/"+tmp_dt_stop.toString()+"] : "+products.size());

							if (products.size() > 0)
							{
								List<Product> mpc_products = new ArrayList<>();
								List<Product> pod_products = new ArrayList<>();
								List<Product> adg_products = new ArrayList<>();
								filterProvider(products, mpc_products, pod_products, adg_products);
								LOG.debug("MPC: "+String.valueOf(mpc_products.size())+" ,POD: "+String.valueOf(pod_products.size())+" ,ADG: "+String.valueOf(adg_products.size()));

								//Create year folder
								Path dir_path = Paths.get(config.getTempFolder(),str_sat, String.valueOf(y),String.valueOf(m));
								try {
									Files.createDirectories(dir_path);
								} catch (Exception e) {
									LOG.error("Couldnt create folder " +dir_path.toString());	  
								}
								//MPC products export
								if (mpc_products.size() > 0)
								{
									String filename = str_sat + "_" + String.format("%04d", y) + String.format("%02d", m) + 
											String.format("%02d", d) + "_" + "AUX_C-V" + "_catalogue_"+now.format(formatter_file)+".json";
									LOG.debug("Exporting MPC to : "+filename);
									Path file_path = Paths.get(config.getTempFolder(),str_sat,String.valueOf(y),String.valueOf(m),filename);
									try (FileOutputStream fos = new FileOutputStream(file_path.toString())) {
										ProductSerializer serializer = new ProductSerializer();
										serializer.SerializeProductList(mpc_products, fos);
									} catch (Exception e) {
										LOG.error("Couldnt write products to file " +e.getLocalizedMessage());	    	
									}
									total_exported = total_exported + mpc_products.size();
								}
								//POD products export
								if (pod_products.size() > 0)
								{
									String filename = str_sat + "_" + String.format("%04d", y) + String.format("%02d", m) + 
											String.format("%02d", d) + "_" + "AUX_POD" + "_catalogue_"+now.format(formatter_file)+".json";
									LOG.debug("Exporting POD to : "+filename);
									Path file_path = Paths.get(config.getTempFolder(),str_sat,String.valueOf(y),String.valueOf(m),filename);
									try (FileOutputStream fos = new FileOutputStream(file_path.toString())) {
										ProductSerializer serializer = new ProductSerializer();
										serializer.SerializeProductList(pod_products, fos);
									} catch (Exception e) {
										LOG.error("Couldnt write products to file " +e.getLocalizedMessage());	    	
									}
									total_exported = total_exported + pod_products.size();
								}
								//ADG products export
								if (adg_products.size() > 0)
								{
									String filename = str_sat + "_" + String.format("%04d", y) + String.format("%02d", m) + 
											String.format("%02d", d) + "_" + "AUX_ADG" + "_catalogue_"+now.format(formatter_file)+".json";
									LOG.debug("Exporting ADG to : "+filename);
									Path file_path = Paths.get(config.getTempFolder(),str_sat,String.valueOf(y),String.valueOf(m),filename);
									try (FileOutputStream fos = new FileOutputStream(file_path.toString())) {
										ProductSerializer serializer = new ProductSerializer();
										serializer.SerializeProductList(adg_products, fos);
									} catch (Exception e) {
										LOG.error("Couldnt write products to file " +e.getLocalizedMessage());	    	
									}
									total_exported = total_exported + adg_products.size();
								}
							}
						}
					}

					for (Product p : products_nofilter)
					{
						LOG.debug("Product : "+p.getName()+" : "+p.getContentDate().getStart().toLocalDateTime().toString()+" / "+p.getContentDate().getEnd().toLocalDateTime().toString() +" has not been filtered");
					}
				} finally {
					entityManager.close();
				}
			}
		}


		LOG.info("Number of exported products : "+String.valueOf(total_exported));
		LOG.info("Number of products found : "+String.valueOf(total_found));
		LOG.info("Done in {} min {} seconds", Duration.between(now, LocalDateTime.now()).toMinutes(), Duration.between(now, LocalDateTime.now()).toSecondsPart());
		storageStatus.archiveDone();
	}

	public List<Product> filterProducts(final List<Product> inProducts, final String sat, final Timestamp startDate, final Timestamp stopDate, List<Product> products_nofilter){
		List<Product> products = new ArrayList<>();
		for (Product pr : inProducts)
		{
			if (pr.getName().startsWith(sat) && pr.getContentDate().getStart().compareTo(stopDate) <= 0
					&& pr.getContentDate().getStart().compareTo(startDate) >= 0)
			{
				products.add(pr);
				products_nofilter.remove(pr);
			}
		}

		return products;

	}

	public void filterProvider(final List<Product> inProducts, List<Product> mpcProducts,List<Product> podProducts,List<Product> adgProducts){
		for (Product pr : inProducts)
		{
			Boolean mpc_found = false;
			//Test mpc provider types
			for (final String mpc : AuxProviders.MPC_CAL)
			{
				if ( pr.getName().contains(mpc))
				{
					mpcProducts.add(pr);
					mpc_found = true;
					break;
				}

			}
			Boolean pod_found = false;
			//Test pod provider types
			for (final String pod : AuxProviders.POD)
			{
				if ( pr.getName().contains(pod))
				{
					podProducts.add(pr);
					pod_found = true;
					break;
				}

			}
			if (!pod_found && !mpc_found)
			{
				adgProducts.add(pr);
			}
		}
	}

}
