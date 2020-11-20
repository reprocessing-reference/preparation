package com.csgroup.auxip.model.jpa;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity(name = "Metrics")
public class MetricJPA {

	 @Id
	 private String Name;
	 private ZonedDateTime Timestamp;
	 @Enumerated(EnumType.STRING)
	 private EnumTypeJPA MetricType;
	 private String Gauge;
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

	public EnumTypeJPA getMetricType() {
		return MetricType;
	}

	public void setMetricType(EnumTypeJPA metricType) {
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
