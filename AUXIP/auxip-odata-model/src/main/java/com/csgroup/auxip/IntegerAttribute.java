package com.csgroup.auxip;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.CSC", key = { "Name" },containerName = "Container")
@EdmEntitySet("IntegerAttributes")
public class IntegerAttribute extends Attribute {
	
	@EdmProperty(name = "Value", nullable = false)
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
