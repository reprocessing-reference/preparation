package com.csgroup.auxip.model.jpa;

import java.util.Arrays;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumMember;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;

public enum SubscriptionEvent {
	created,
	deleted;

	public static CsdlEnumType getEnumType() {

		CsdlEnumType enumType = null;

		enumType = new CsdlEnumType().setName("SubscriptionEvent") ;
		enumType.setMembers(Arrays.asList(
			new CsdlEnumMember().setName("created").setValue("0"),
			new CsdlEnumMember().setName("deleted").setValue("1") )) ;

		enumType.setFlags(false);
		enumType.setUnderlyingType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
		
		return enumType;
	}
}
