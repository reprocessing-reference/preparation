package com.csgroup.auxip;

import java.time.ZonedDateTime;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.CSC", key = { "Name" }, containerName = "Container")
@EdmEntitySet("DateTimeOffsetAttributes")
public class DateTimeOffsetAttribute extends Attribute {
	
	@EdmProperty(name = "Value", nullable = false)
	private ZonedDateTime value;

	public DateTimeOffsetAttribute(String name, String valueType, ZonedDateTime value) {
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
