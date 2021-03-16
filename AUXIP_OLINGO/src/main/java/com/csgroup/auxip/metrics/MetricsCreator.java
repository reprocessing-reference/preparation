package com.csgroup.auxip.metrics;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.csgroup.auxip.config.MetricsConfiguration;
import com.csgroup.auxip.model.jpa.Metric;
import com.csgroup.auxip.model.jpa.MetricType;
import com.csgroup.auxip.model.jpa.Product;
import com.csgroup.auxip.model.repository.StorageStatus;

@Component
public class MetricsCreator {
	private static final Logger LOG = LoggerFactory.getLogger(MetricsCreator.class);
	
	@Autowired
	private StorageStatus storageStatus;
	@Autowired
	private MetricsConfiguration config;

	private static Map<String, String> SATS = new HashMap<String, String>() {{
		put("S1_", "Sentinel1.All");
		put("S1A", "Sentinel1.A");
		put("S1B", "Sentinel1.B");
		put("S2_", "Sentinel2.All");
		put("S2B", "Sentinel2.B");
		put("S2A", "Sentinel2.A");
		put("S3_", "Sentinel3.All");
		put("S3A", "Sentinel3.A");
		put("S3B", "Sentinel3.B");
	}};

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(fixedRate = 3600000, initialDelay = 5000)
	public void doMetrics() {
		if (!config.getActive())
		{
			LOG.info("Metrics are not activated in config");
			return;
		}
		LOG.info("The time is now {}", dateFormat.format(new Date()));	
		if (config.getOnTrigger() && !storageStatus.hasChanges())
		{
			LOG.info("No change in the repository, nothing to do");
			return;
		}

		//get current date
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		//Query
		String queryString = "SELECT entity FROM com.csgroup.auxip.model.jpa.Product entity WHERE " ;
		queryString = queryString.concat("entity.Name LIKE :sat");

		for (Map.Entry<String, String> entry : SATS.entrySet()) {
			LOG.debug("Treating: "+entry.getKey());

			EntityManager entityManager = this.entityManagerFactory.createEntityManager();
			try {			
				EntityTransaction transaction = entityManager.getTransaction();
				Map<String, Object> queryParams_m = new HashMap<String,Object>();						
				queryParams_m.put("sat", entry.getKey()+"%");    
				List<Product> products_m;
				Query query_m = entityManager.createQuery(queryString);
				for (Map.Entry<String, Object> entry_m : queryParams_m.entrySet()) {
					query_m.setParameter(entry_m.getKey(), entry_m.getValue());
				}
				products_m = query_m.getResultList();
				LOG.debug("Number of products : "+String.valueOf(products_m.size()));
				List<Metric> list = doMetricProducts(products_m, entry.getValue());

				for (Metric m : list)
				{
					transaction.begin();				
					Metric attached = entityManager.merge(m);                
					if (transaction.isActive()) {
						transaction.commit();
					} else {
						transaction.rollback();
					}
				}
			} finally {                    
				entityManager.close();
			}
		}


		storageStatus.metricsDone();


	}

	private List<Product> filterProducts(final List<Product> inProducts, final String sat, final LocalDateTime startDate, final LocalDateTime stopDate){
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

	private List<Metric> doMetricProducts(final List<Product> inProducts,final String name) {
		List<Metric> list = new ArrayList<Metric>();
		//Count metric
		Metric metric = new Metric();
		metric.setName(name+".count");
		metric.setMetricType(MetricType.Counter);
		metric.setCounter(inProducts.size());
		metric.setGauge("");
		metric.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
		list.add(metric);
		//Size metric
		long total_size =0;
		for (final Product p : inProducts)
		{
			total_size = total_size + p.getContentLength();
		}
		Metric metric_size = new Metric();
		metric_size.setName(name+".size");
		metric_size.setMetricType(MetricType.Counter);
		metric_size.setCounter(total_size);
		metric_size.setGauge("");
		metric_size.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
		list.add(metric_size);
		return list;

	}


}
