package com.csgroup.auxip;

import java.time.ZonedDateTime;

import com.csgroup.jpadatasource.annotations.ODataJPAEntity;
import com.csgroup.jpadatasource.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.CSC", key = "Name", containerName = "Container")
@EdmEntitySet("Metrics")
@ODataJPAEntity(value = "com.csgroup.auxip.model.jpa.MetricJPA")
public class Metric {

	 @EdmProperty(name = "Name")
	 @ODataJPAProperty
	 private String Name;
	 @EdmProperty(name = "Timestamp", precision = 3)
	 private ZonedDateTime Timestamp;
	 
	 @EdmProperty(name = "MetricType")
	 @ODataJPAProperty
	 private EnumType MetricType;
	 
	 @EdmProperty(name = "Gauge")
	 @ODataJPAProperty
	 private String Gauge;
	 
	 @EdmProperty(name = "Counter")
	 @ODataJPAProperty
	 private long Counter;

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
	 
	 
	 
	 
}
