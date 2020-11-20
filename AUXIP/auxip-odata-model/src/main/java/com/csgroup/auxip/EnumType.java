package com.csgroup.auxip;
import com.sdl.odata.api.edm.annotations.EdmEnum;
import com.sdl.odata.api.edm.model.PrimitiveType;

@EdmEnum(namespace = "OData.CSC", name = "EnumType", underlyingType = PrimitiveType.INT32, flags = false)
public enum EnumType {	
	Gauge,
	counter
}
