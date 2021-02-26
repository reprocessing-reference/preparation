package com.csgroup.auxip.model.jpa;


import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;


import java.time.ZonedDateTime;
import java.util.Arrays;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity(name = "Metrics")
public class Metric {

	 @Id
	 private String Name;
	 private ZonedDateTime Timestamp;
	 @Enumerated(EnumType.STRING)
	 private EnumType MetricType;
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

	public ZonedDateTime getTimestamp() {
		return Timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		Timestamp = timestamp;
	}

	public EnumType getMetricType() {
		return MetricType;
	}

	public void setMetricType(EnumType metricType) {
		MetricType = metricType;
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
		CsdlProperty value_type = new CsdlProperty().setName("ContentType").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		
		// create PropertyRef for Key element
		CsdlPropertyRef propertyRef = new CsdlPropertyRef();
		propertyRef.setName("Name");

		// configure EntityType
		entityType = new CsdlEntityType();
		entityType.setName(ET_NAME);
		entityType.setProperties(Arrays.asList(name,value_type));
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
	 
	 
}
