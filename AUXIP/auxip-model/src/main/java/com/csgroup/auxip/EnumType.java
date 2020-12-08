package com.csgroup.auxip;
import com.csgroup.jpadatasource.annotations.ODataJPAEntity;
import com.sdl.odata.api.edm.annotations.EdmEnum;
import com.sdl.odata.api.edm.model.PrimitiveType;

@EdmEnum(namespace = "OData.CSC", name = "EnumType", underlyingType = PrimitiveType.INT32, flags = false)
@ODataJPAEntity(value = "com.csgroup.auxip.model.jpa.EnumTypeJPA")
public enum EnumType {
	Gauge,
	counter
}
