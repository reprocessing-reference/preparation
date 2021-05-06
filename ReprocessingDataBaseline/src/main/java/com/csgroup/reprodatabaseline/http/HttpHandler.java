package com.csgroup.reprodatabaseline.http;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpHandler {
	private static final Logger LOG = LoggerFactory.getLogger(HttpHandler.class);

	private final RestTemplate restTemplate;

	public HttpHandler(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder
				.setConnectTimeout(Duration.ofSeconds(30))
				.setReadTimeout(Duration.ofSeconds(30))
				.build();
	}

	public String getPost(final String url) {
		LOG.debug("Url requested : "+url);
		try {
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity entity = new HttpEntity(headers);
			ResponseEntity<String> response = restTemplate.exchange(
					url, HttpMethod.GET, entity, String.class);
			return response.getBody();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LOG.error(e.getLocalizedMessage());
		}
		return null;
	}
}
