package com.csgroup.auxip;

import com.sdl.odata.api.edm.annotations.EdmEnum;
import com.sdl.odata.api.edm.model.PrimitiveType;

@EdmEnum(namespace = "OData.CSC",name = "SubscriptionStatus", underlyingType = PrimitiveType.INT32, flags = false)
public enum SubscriptionStatus {	
	running,
	paused,
	cancelled
}
