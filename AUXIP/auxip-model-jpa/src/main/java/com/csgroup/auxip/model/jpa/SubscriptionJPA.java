package com.csgroup.auxip.model.jpa;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity(name = "Subscriptions")
public class SubscriptionJPA {

	@Id
    private UUID Id;
	@Enumerated(EnumType.STRING)
	private SubscriptionStatusJPA status;
	@Enumerated(EnumType.STRING)
	private SubscriptionEventJPA event;
	private String filterParam;
	private long ContentLength;
	private ZonedDateTime submissionDate;
    private ZonedDateTime lastNotificationDate;
        
    public UUID getId() {
		return Id;
	}

	public void setId(UUID id) {
		Id = id;
	}

	public SubscriptionStatusJPA getStatus() {
		return status;
	}

	public void setStatus(SubscriptionStatusJPA status) {
		this.status = status;
	}

	public SubscriptionEventJPA getEvent() {
		return event;
	}

	public void setEvent(SubscriptionEventJPA event) {
		this.event = event;
	}

	public String getFilterParam() {
		return filterParam;
	}

	public void setFilterParam(String filterParam) {
		this.filterParam = filterParam;
	}

	public long getContentLength() {
		return ContentLength;
	}

	public void setContentLength(long contentLength) {
		ContentLength = contentLength;
	}

	public ZonedDateTime getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(ZonedDateTime submissionDate) {
		this.submissionDate = submissionDate;
	}

	public ZonedDateTime getLastNotificationDate() {
		return lastNotificationDate;
	}

	public void setLastNotificationDate(ZonedDateTime lastNotificationDate) {
		this.lastNotificationDate = lastNotificationDate;
	}
    
	
	
	
}
