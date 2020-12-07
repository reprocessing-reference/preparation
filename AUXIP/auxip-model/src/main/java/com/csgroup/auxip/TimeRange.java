package com.csgroup.auxip;

import java.time.ZonedDateTime;

import com.csgroup.jpadatasource.annotations.ODataJPAEntity;
import com.csgroup.jpadatasource.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmComplex(namespace = "OData.CSC")
@ODataJPAEntity(value = "com.csgroup.auxip.model.jpa.TimeRangeJPA")
public class TimeRange {
	 
	public TimeRange(ZonedDateTime start, ZonedDateTime end) {
		super();
		Start = start;
		End = end;
	}
	
	public TimeRange() {		
	}

	@EdmProperty(name = "Start",nullable = false, precision = 6)
	@ODataJPAProperty
	private ZonedDateTime Start;
	 
	@EdmProperty(name = "End",nullable = false, precision = 6)
	@ODataJPAProperty(value = "Stop")
	private ZonedDateTime End;

	public ZonedDateTime getStart() {
		return Start;
	}

	public void setStart(ZonedDateTime start) {
		Start = start;
	}

	public ZonedDateTime getEnd() {
		return End;
	}

	public void setEnd(ZonedDateTime end) {
		End = end;
	}
	
	
	 
}
