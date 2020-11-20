package com.csgroup.rba.model.jpa;

import java.time.ZonedDateTime;

import javax.persistence.Embeddable;

@Embeddable
public class TimeRangeJPA {
	private ZonedDateTime Start;
	private ZonedDateTime Stop;

	public ZonedDateTime getStart() {
		return Start;
	}

	public void setStart(ZonedDateTime start) {
		Start = start;
	}

	public ZonedDateTime getStop() {
		return Stop;
	}

	public void setStop(ZonedDateTime end) {
		Stop = end;
	}
	
	
	 
}
