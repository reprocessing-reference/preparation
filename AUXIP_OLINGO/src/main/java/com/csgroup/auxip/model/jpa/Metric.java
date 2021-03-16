package com.csgroup.auxip.model.jpa;


import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;


import java.time.ZonedDateTime;
import java.util.Arrays;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity(name = "Metrics")
public class Metric {

	 @Id
	 private String Name;
	 private Timestamp Timestamp;
	 @Enumerated(EnumType.STRING)
	 private MetricType metricType;
	 private String Gauge;
	 private long Counter;

	 public static final String ET_NAME = "Metric";
	 public static final FullQualifiedName FQN = new FullQualifiedName(Globals.NAMESPACE, ET_NAME);
	 public static final String ES_NAME = "Metrics";

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public Timestamp getTimestamp() {
		return Timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		Timestamp = timestamp;
	}

	public MetricType getMetricType() {
		return metricType;
	}

	public void setMetricType(MetricType ametricType) {
		metricType = ametricType;
	}

	public String getGauge() {
		return Gauge;
	}

	public void setGauge(String gauge) {
		Gauge = gauge;
	}

	public long getCounter() {
		return Counter;
	}

	public void setCounter(long counter) {
		Counter = counter;
	}

	public static CsdlEntityType getEntityType()
	{
		CsdlEntityType entityType = null;

		// create EntityType properties
		CsdlProperty name = new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		CsdlProperty timestamp = new CsdlProperty().setName("TimeStamp").setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName());
		CsdlProperty metric_type = new CsdlProperty().setName("MetricType").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		CsdlProperty gauge = new CsdlProperty().setName("Gauge").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		CsdlProperty counter = new CsdlProperty().setName("Counter").setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
		// create PropertyRef for Key element
		CsdlPropertyRef propertyRef = new CsdlPropertyRef();
		propertyRef.setName("Name");

		// configure EntityType
		entityType = new CsdlEntityType();
		entityType.setName(ET_NAME);
		entityType.setProperties(Arrays.asList(name,timestamp,metric_type,gauge,counter));
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
		Property id = new Property(stringType, "Name",ValueType.PRIMITIVE,this.Name) ;
		
		Property metric_type = new Property("MetricType", "MetricType",ValueType.ENUM,this.metricType) ;

		Property gauge = new Property(stringType, "Gauge",ValueType.PRIMITIVE,this.Gauge) ;
		Property counter = new Property("Int64", "Counter",ValueType.PRIMITIVE,this.Counter) ;
		Property timestamp = new Property("DateTimeOffset", "TimeStamp",ValueType.PRIMITIVE,this.Timestamp) ;
				
		entity.addProperty( id );
		entity.addProperty( timestamp );
		entity.addProperty( metric_type );
		entity.addProperty( gauge );
		entity.addProperty( counter );
		
		entity.setType(FQN.getFullQualifiedNameAsString());
		return entity;
	}
	 
	 
}
