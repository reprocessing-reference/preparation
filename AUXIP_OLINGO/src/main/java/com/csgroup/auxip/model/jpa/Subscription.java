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

import java.sql.Timestamp;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "Subscriptions")
public class Subscription {

	@Id
	@GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID Id;
	@Enumerated(EnumType.STRING)
	private SubscriptionStatus Status;

	private String NotificationEndpoint;
	private String NotificationEpUsername;
	private String NotificationEpPassword;

	// @Enumerated(EnumType.STRING)
	// private SubscriptionEvent event;
	private String FilterParam;
	private Timestamp SubmissionDate;
    private Timestamp LastNotificationDate;
	

	public static final String ET_NAME = "Subscription";
	public static final FullQualifiedName FQN = new FullQualifiedName(Globals.NAMESPACE, ET_NAME);
	public static final String ES_NAME = "Subscriptions";

    public UUID getId() {
		return Id;
	}

	public void setId(UUID id) {
		Id = id;
	}

	public SubscriptionStatus getStatus() {
		return Status;
	}

	public void setStatus(SubscriptionStatus status) {
		this.Status = status;
	}

	public String getNotificationEndpoint() {
		return NotificationEndpoint;
	}
	public void setNotificationEndpoint(String notificationEndpoint) {
		NotificationEndpoint = notificationEndpoint;
	}

	public String getNotificationEpPassword() {
		return NotificationEpPassword;
	}
	public void setNotificationEpPassword(String notificationEpPassword) {
		NotificationEpPassword = notificationEpPassword;
	}
	public void setNotificationEpUsername(String notificationEpUsername) {
		NotificationEpUsername = notificationEpUsername;
	}
	public String getNotificationEpUsername() {
		return NotificationEpUsername;
	}

	public String getFilterParam() {
		return FilterParam;
	}

	public void setFilterParam(String filterParam) {
		this.FilterParam = filterParam;
	}

	public Timestamp getSubmissionDate() {
		return SubmissionDate;
	}

	public void setSubmissionDate(Timestamp submissionDate) {
		this.SubmissionDate = submissionDate;
	}

	public Timestamp getLastNotificationDate() {
		return LastNotificationDate;
	}

	public void setLastNotificationDate(Timestamp lastNotificationDate) {
		this.LastNotificationDate = lastNotificationDate;
	}
	
	public static CsdlEntityType getEntityType()
	{
		CsdlEntityType entityType = null;

		// create EntityType properties
		CsdlProperty id = new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.Guid.getFullQualifiedName());
		// CsdlProperty status = new CsdlProperty().setName("Status").setType(SubscriptionStatus.getFullQualifiedName());
		CsdlProperty status = new CsdlProperty().setName("Status").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		CsdlProperty notificationEndpoint 	= new CsdlProperty().setName("NotificationEndpoint").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName() );
		CsdlProperty notificationEpUsername = new CsdlProperty().setName("NotificationEpUsername").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName() );
		CsdlProperty notificationEpPassword = new CsdlProperty().setName("NotificationEpPassword").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName() );
		CsdlProperty filterParam = new CsdlProperty().setName("FilterParam").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		CsdlProperty submissionDate = new CsdlProperty().setName("SubmissionDate").setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(3);
		// CsdlProperty lastNotificationDate = new CsdlProperty().setName("LastNotificationDate").setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(3);

		// create PropertyRef for Key element
		CsdlPropertyRef propertyRef = new CsdlPropertyRef();
		propertyRef.setName("Id");

		// configure EntityType
		entityType = new CsdlEntityType();
		entityType.setName(ET_NAME);
		entityType.setProperties(Arrays.asList(id,  status,notificationEndpoint,notificationEpUsername,notificationEpPassword,filterParam ,submissionDate/*,lastNotificationDate*/));
		entityType.setKey(Arrays.asList(propertyRef));


		return entityType;
	}

	public static CsdlEntitySet getEntitySet() 
	{

        CsdlEntitySet entitySet = new CsdlEntitySet();
        entitySet.setName(ES_NAME);
        entitySet.setType(FQN);
		
		return entitySet;
	} 
	

	public org.apache.olingo.commons.api.data.Entity getOdataEntity()
	{
		org.apache.olingo.commons.api.data.Entity entity = new org.apache.olingo.commons.api.data.Entity();
		final String stringType = "String";
		// create EntityType properties
		Property id = new Property("Guid", "Id",ValueType.PRIMITIVE,this.Id) ;
		
		Property status = new Property(null, "Status",ValueType.ENUM,this.Status) ;

		Property notificationEndpoint = new Property(stringType, "NotificationEndpoint",ValueType.PRIMITIVE,this.NotificationEndpoint) ;
		Property notificationEpUsername = new Property(stringType, "NotificationEpUsername",ValueType.PRIMITIVE,this.NotificationEpUsername) ;
		Property notificationEpPassword = new Property(stringType, "NotificationEpPassword",ValueType.PRIMITIVE,"**********") ;
		Property filterParam = new Property(stringType, "FilterParam",ValueType.PRIMITIVE,this.FilterParam) ;
		Property submissionDate = new Property("DateTimeOffset", "SubmissionDate",ValueType.PRIMITIVE,this.SubmissionDate) ;
		// Property lastNotificationDate = new Property("DateTimeOffset", "LastNotificationDate",ValueType.PRIMITIVE,this.LastNotificationDate) ;
		
		entity.addProperty( id );
		entity.addProperty( status );
		entity.addProperty( notificationEndpoint );
		entity.addProperty( notificationEpUsername );
		entity.addProperty( notificationEpPassword );
		entity.addProperty( filterParam );
		entity.addProperty( submissionDate );

		entity.setType(FQN.getFullQualifiedNameAsString());
		return entity;
	}
	

}
