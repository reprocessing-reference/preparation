package com.csgroup.auxip.model.jpa;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;

import jdk.jfr.Name;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;


@Embeddable
public class DurationVolume {
	// starting point of the period where to count a downloaded volumes
	private Timestamp periodStart;
	// duration of the 
	@Transient
	private Duration duration = Globals.DOWNLOAD_DURATION;
	// cumulative volume in the current duration ( period )
	private long volume;


	public Timestamp getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(Timestamp periodStart) {
		this.periodStart = periodStart;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}




}
