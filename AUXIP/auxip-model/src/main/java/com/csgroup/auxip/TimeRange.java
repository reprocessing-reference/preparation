package com.csgroup.auxip;

import java.time.ZonedDateTime;

import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmComplex(namespace = "OData.CSC")
public class TimeRange {
	 
	@EdmProperty(name = "Start",nullable = false, precision = 6)
	private ZonedDateTime Start;
	 
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
