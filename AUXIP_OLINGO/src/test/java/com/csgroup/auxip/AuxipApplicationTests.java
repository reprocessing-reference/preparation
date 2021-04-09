package com.csgroup.auxip;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.csgroup.auxip.model.jpa.*;

@SpringBootTest
class AuxipApplicationTests {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Test
	void contextLoads() {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query query = entityManager.createQuery("select Name from com.csgroup.auxip.model.jpa.Product ");

		try {
			List<Product> products = query.getResultList();
			for (Product product : products) {
				System.out.println("product.Name = " + product );
			}
		} finally {
			entityManager.close();
			// emf.close();

		}



	}

}
