package com.csgroup.rba.model;

import java.time.ZonedDateTime;

import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.csgroup.rba.model.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;


@ODataJPAEntity(value = "com.csgroup.rba.model.jpa.TimeRangeJPA")
@EdmComplex(namespace = "OData.RBA")
public class TimeRange {
	 
	public TimeRange(ZonedDateTime start, ZonedDateTime end) {
		super();
		Start = start;
		End = end;
	}
	
	public TimeRange() {		
	}

	@ODataJPAProperty
	@EdmProperty(name = "Start",nullable = false, precision = 6)
	private ZonedDateTime Start;
	 
	@ODataJPAProperty
	@EdmProperty(name = "End",nullable = false, precision = 6)
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
