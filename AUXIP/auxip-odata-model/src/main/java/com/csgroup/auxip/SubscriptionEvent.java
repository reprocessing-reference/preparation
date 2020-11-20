package com.csgroup.auxip;

import com.sdl.odata.api.edm.annotations.EdmEnum;
import com.sdl.odata.api.edm.model.PrimitiveType;

@EdmEnum(namespace = "OData.CSC", name = "SubscriptionEvent", underlyingType = PrimitiveType.INT32, flags = false)
public enum SubscriptionEvent {	
	created,
	deleted
}
