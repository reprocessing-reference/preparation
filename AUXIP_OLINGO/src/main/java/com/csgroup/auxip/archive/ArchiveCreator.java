package com.csgroup.auxip.archive;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

import org.apache.olingo.commons.api.data.EntityCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.csgroup.auxip.config.ArchiveConfiguration;
import com.csgroup.auxip.model.jpa.Product;
import com.csgroup.auxip.serializer.ProductSerializer;

@Component
public class ArchiveCreator {
	private static final Logger LOG = LoggerFactory.getLogger(ArchiveCreator.class);

	private static List<String> SATS = new ArrayList<>(List.of("S1_","S1A","S1B","S2_","S2A","S2B","S3_","S3A","S3B"));
	private static List<String> CALVAL = new ArrayList<>(List.of(""));
	private static List<String> POD = new ArrayList<>(List.of(""));
	private static List<String> ADG = new ArrayList<>(List.of(""));
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private ArchiveConfiguration config;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	//@Scheduled(fixedRate = 300000, initialDelay = 5000)
	public void reportCurrentTime() {
		LOG.info("The time is now {}", dateFormat.format(new Date()));		
		LOG.info("Working in "+config.getTempFolder());
		Path working_path = Paths.get(config.getTempFolder());

		if (Files.notExists(working_path) || !Files.isDirectory(working_path))
		{
			LOG.error("Folder doesn't exist or is not a directory");
			return;
		}
		long total_exported = 0;
		//get current date
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		String queryString = "SELECT entity FROM com.csgroup.auxip.model.jpa.Product entity WHERE " ;
		queryString = queryString.concat("entity.ContentDate.Start < :publicationStop "
				+ "AND entity.ContentDate.Start > :publicationStart");

		//Loop on years
		for (int y = 2010; y <= now.getYear();y++)
		{
			//Loop on months
			for (int m = 1; m <= 12;m++)
			{
				//Create entityManager
				EntityManager entityManager = entityManagerFactory.createEntityManager();

				LOG.debug("Treating : "+String.valueOf(y)+"/"+String.valueOf(m));
				LocalDateTime tmp_dt_m_start = LocalDateTime.of(y, m,1, 0, 0);
				LocalDateTime tmp_dt_m_stop = tmp_dt_m_start.with(TemporalAdjusters.lastDayOfMonth());
				Map<String, Object> queryParams_m = new HashMap<String,Object>();						
				queryParams_m.put("publicationStart", Date.from(tmp_dt_m_start.atZone(ZoneId.of("Z")).toInstant()));    	
				queryParams_m.put("publicationStop", Date.from(tmp_dt_m_stop.atZone(ZoneId.of("Z")).toInstant()));
				LOG.debug("StartDate: "+tmp_dt_m_start.atZone(ZoneId.of("Z")).format(DateTimeFormatter.ISO_DATE_TIME));
				LOG.debug("StopDate: "+tmp_dt_m_stop.atZone(ZoneId.of("Z")).format(DateTimeFormatter.ISO_DATE_TIME));

				Query query_m = entityManager.createQuery(queryString);
				for (Map.Entry<String, Object> entry_m : queryParams_m.entrySet()) {
					query_m.setParameter(entry_m.getKey(), entry_m.getValue());
				}
				List<Product> products_m;
				try {	
					products_m = query_m.getResultList();
				} finally {
					entityManager.close();
				}
				if (products_m.size() ==0)
				{
					continue;
				}
				//Loop on days
				for (int d = tmp_dt_m_start.getDayOfMonth();d <= tmp_dt_m_stop.getDayOfMonth();d++)
				{
					LocalDateTime tmp_dt_start = LocalDateTime.of(y, m,d, 0, 0);
					LocalDateTime tmp_dt_stop = LocalDateTime.of(y, m,d, 23, 59,59,999999999);						
					
					//Loop on SAT
					for (String str_sat : SATS)
					{
						List<Product> products;
						products = filterProducts(products_m, str_sat, tmp_dt_start, tmp_dt_stop);

						LOG.info("Number of products found: "+products.size());

						if (products.size() > 0)
						{
							//Create year folder
							Path dir_path = Paths.get(config.getTempFolder(),str_sat, String.valueOf(y),String.valueOf(m));
							try {
								Files.createDirectories(dir_path);
							} catch (Exception e) {
								LOG.error("Couldnt create folder " +dir_path.toString());	  
							}
							String filename = str_sat + "_" + String.format("%04d", y) + String.format("%02d", m) + String.format("%02d", d) + "_" + "AUX_ADG" + "_catalogue_"+now.format(formatter)+".json";
							LOG.debug("Exporting to : "+filename);
							Path file_path = Paths.get(config.getTempFolder(),str_sat,String.valueOf(y),String.valueOf(m),filename);
							try (FileOutputStream fos = new FileOutputStream(file_path.toString())) {
								ProductSerializer serializer = new ProductSerializer();
								serializer.SerializeProductList(products, fos);
							} catch (Exception e) {
								LOG.error("Couldnt write products to file " +e.getLocalizedMessage());	    	
							}
							total_exported = total_exported + products.size();
						}
					}
				}
			}
		}

		LOG.info("Number of exported products : "+String.valueOf(total_exported));
	}

	public List<Product> filterProducts(final List<Product> inProducts, final String sat, final LocalDateTime startDate, final LocalDateTime stopDate){
		List<Product> products = new ArrayList<>();
		for (Product pr : inProducts)
		{
			if (pr.getName().startsWith(sat) && pr.getContentDate().getStart().toInstant().atZone(ZoneId.of("Z")).toLocalDateTime().isBefore(stopDate)
					&& pr.getContentDate().getStart().toInstant().atZone(ZoneId.of("Z")).toLocalDateTime().isAfter(startDate))
			{
				products.add(pr);
			}
		}

		return products;

	}
}
