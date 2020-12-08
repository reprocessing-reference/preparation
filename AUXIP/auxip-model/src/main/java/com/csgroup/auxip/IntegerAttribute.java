package com.csgroup.auxip;
import com.csgroup.jpadatasource.annotations.ODataJPAEntity;
import com.csgroup.jpadatasource.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.CSC", key = { "Name" },containerName = "Container")
@EdmEntitySet("IntegerAttributes")
@ODataJPAEntity(value = "com.csgroup.auxip.model.jpa.IntegerAttributeJPA")
public class IntegerAttribute extends Attribute {
	
	@EdmProperty(name = "Value", nullable = false)
	@ODataJPAProperty
	private long value;

	public IntegerAttribute(String name, String valueType, long value) {
		super(name, valueType);
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
	
}
