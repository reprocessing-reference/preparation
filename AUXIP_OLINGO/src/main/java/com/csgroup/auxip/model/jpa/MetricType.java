package com.csgroup.auxip.model.jpa;

import java.util.Arrays;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumMember;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;

public enum MetricType {	
	Gauge,
	Counter;

	public static CsdlEnumType getEnumType() {

		CsdlEnumType enumType = null;

		enumType = new CsdlEnumType().setName("MetricType") ;
		enumType.setMembers(Arrays.asList(
			new CsdlEnumMember().setName("Gauge").setValue("0"),
			new CsdlEnumMember().setName("Counter").setValue("1") )) ;

		enumType.setFlags(false);
		enumType.setUnderlyingType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
		
		return enumType;
	}

	public static FullQualifiedName getFullQualifiedName() {
		return new FullQualifiedName(Globals.NAMESPACE, "MetricType");
	}

}
