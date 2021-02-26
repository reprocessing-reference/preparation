package com.csgroup.auxip.model.jpa;

import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.data.Property;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.print.Doc;

public class Notification {

    private Product product;
    private Subscription subscription;
    private Timestamp notificationDate;
	
	public Notification() {
		this.notificationDate = new Timestamp(System.currentTimeMillis());
	}

	public Notification( Product product, Subscription subscription) {
		this.product = product;
		this.subscription = subscription;
		this.notificationDate = new Timestamp(System.currentTimeMillis());
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public Timestamp getNotificationDate() {
		return notificationDate;
	}

	public Product getProduct() {
		return product;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	/**
	 * Send this notifaction the endpoint of subscription 
	 * @return HttpStatus
	 */
	public HttpStatus send() {
		
		String url = subscription.getNotificationEndpoint();

		// create headers
		HttpHeaders headers = new HttpHeaders();
		// set `content-type` header
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setBasicAuth(subscription.getNotificationEpUsername(), subscription.getNotificationEpPassword());
		
		// create a map for post parameters
		Map<String, String> notificationJson = new HashMap<>();

		notificationJson.put("ProductId", product.getId().toString());
		notificationJson.put("ProductName", product.getName());
		notificationJson.put("SubscriptionId", subscription.getId().toString());
		notificationJson.put("NotificationDate", notificationDate.toString());

		// build the request
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(notificationJson, headers);

		// send POST request
		ResponseEntity<String> response = new RestTemplate().postForEntity(url, entity, String.class);

		HttpStatus status = response.getStatusCode();
		if( status == HttpStatus.OK )
		{
			//update a subscription lastNotificationDate
			subscription.setLastNotificationDate(notificationDate);
		}
		System.out.println( response.getStatusCode().toString() );

		return status;
	}
	

}
