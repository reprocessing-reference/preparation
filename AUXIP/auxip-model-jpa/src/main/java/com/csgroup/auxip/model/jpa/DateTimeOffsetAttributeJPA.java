package com.csgroup.auxip.model.jpa;

import java.time.ZonedDateTime;

import javax.persistence.Embeddable;

@Embeddable
public class DateTimeOffsetAttributeJPA extends AttributeJPA {
		
	private ZonedDateTime value;
	
	public DateTimeOffsetAttributeJPA() {
		
	}

	public DateTimeOffsetAttributeJPA(String name, String valueType, ZonedDateTime value) {
		super(name, valueType);
		this.value = value;
	}

	public ZonedDateTime getValue() {
		return value;
	}

	public void setValue(ZonedDateTime value) {
		this.value = value;
	}
}
