package com.csgroup.auxip.model.jpa;

import java.util.Arrays;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumMember;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;

public enum SubscriptionStatus {	
	running,
	paused,
	cancelled;

	public static CsdlEnumType getEnumType() {

		CsdlEnumType enumType = null;

		enumType = new CsdlEnumType().setName("SubscriptionStatus") ;
		enumType.setMembers(Arrays.asList(
			new CsdlEnumMember().setName("running").setValue("0"),
			new CsdlEnumMember().setName("paused").setValue("1"),
			new CsdlEnumMember().setName("cancelled").setValue("2") )) ;

		enumType.setFlags(false);
		enumType.setUnderlyingType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
		
		return enumType;
	}

	public static FullQualifiedName getFullQualifiedName() {
		return new FullQualifiedName(Globals.NAMESPACE, "SubscriptionStatus");
	}

}
