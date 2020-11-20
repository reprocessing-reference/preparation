package com.csgroup.auxip;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.CSC", key = "Id", containerName = "Container")
@EdmEntitySet("Subscriptions")
public class Subscription {

	@EdmProperty(name = "Id")
    private UUID Id;
    
    @EdmProperty(name = "Status")
    private SubscriptionStatus status;
    
    @EdmProperty(name = "SubscriptionEvent")
    private SubscriptionEvent event;
    
    @EdmProperty(name = "FilterParam")
    private String filterParam;
    
    @EdmProperty(name = "ContentLength")
    private long ContentLength;
    
    @EdmProperty(name = "SubmissionDate", precision = 3)
    private ZonedDateTime submissionDate;
    
    @EdmProperty(name = "LastNotificationDate", precision = 3)
    private ZonedDateTime lastNotificationDate;
    
    
    public UUID getId() {
		return Id;
	}

	public void setId(UUID id) {
		Id = id;
	}

	public SubscriptionStatus getStatus() {
		return status;
	}

	public void setStatus(SubscriptionStatus status) {
		this.status = status;
	}

	public SubscriptionEvent getEvent() {
		return event;
	}

	public void setEvent(SubscriptionEvent event) {
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
