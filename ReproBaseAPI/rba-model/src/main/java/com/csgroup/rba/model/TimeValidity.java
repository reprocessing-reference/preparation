package com.csgroup.rba.model;
import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.sdl.odata.api.edm.annotations.EdmEnum;
import com.sdl.odata.api.edm.model.PrimitiveType;


@ODataJPAEntity(value = "com.csgroup.rba.model.jpa.TimeDependencyJPA")
@EdmEnum(namespace = "OData.RBA", name = "TimeDependency", underlyingType = PrimitiveType.INT32, flags = false)
public enum TimeDependency {
	ValidityPeriod,
	AnyTime
}
