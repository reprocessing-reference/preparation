package com.csgroup.auxip;

import java.time.ZonedDateTime;

import com.csgroup.jpadatasource.annotations.ODataJPAEntity;
import com.csgroup.jpadatasource.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmComplex(namespace = "OData.CSC")
@ODataJPAEntity(value = "com.csgroup.auxip.model.jpa.DateTimeOffsetAttributeJPA")
public class DateTimeOffsetAttribute extends Attribute {
	
	@EdmProperty(name = "Value", nullable = false)
	@ODataJPAProperty
	private ZonedDateTime value;

	public DateTimeOffsetAttribute() {
		
	}
	
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
