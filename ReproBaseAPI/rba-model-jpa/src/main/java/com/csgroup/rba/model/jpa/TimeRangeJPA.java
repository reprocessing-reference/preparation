package com.csgroup.rba.model.jpa;

import java.time.ZonedDateTime;

import javax.persistence.Embeddable;

@Embeddable
public class TimeRangeJPA {
	private ZonedDateTime Start;
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
