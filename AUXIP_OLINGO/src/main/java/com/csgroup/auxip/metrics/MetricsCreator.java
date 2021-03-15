package com.csgroup.auxip.metrics;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.csgroup.auxip.model.jpa.Metric;
import com.csgroup.auxip.model.jpa.MetricType;
import com.csgroup.auxip.model.jpa.Product;

@Component
public class MetricsCreator {
	private static final Logger LOG = LoggerFactory.getLogger(MetricsCreator.class);

	private static List<String> SATS = new ArrayList<>(List.of("S1_","S1A","S1B","S2_","S2A","S2B","S3_","S3A","S3B"));

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(fixedRate = 300000, initialDelay = 5000)
	public void doMetrics() {
		LOG.info("The time is now {}", dateFormat.format(new Date()));	
		

		//get current date
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		//String queryString = "SELECT entity FROM com.csgroup.auxip.model.jpa.Product entity";
		
		EntityManager entityManager = this.entityManagerFactory.createEntityManager();
		
		//Query query_m = entityManager.createQuery(queryString);		
		
		
		EntityTransaction transaction = entityManager.getTransaction();
		try {
			//List<Product> products_m;
			//products_m = query_m.getResultList();

			transaction.begin();
			Metric metric = new Metric();
			metric.setName("Total.product.count");
			metric.setMetricType(MetricType.Counter);
			metric.setCounter(12);
			metric.setGauge("");
			metric.setTimestamp(ZonedDateTime.now());

			Metric attached = entityManager.merge(metric);                
			if (transaction.isActive()) {
				transaction.commit();
			} else {
				transaction.rollback();
			}			
		} finally {                    
			entityManager.close();
		}





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
