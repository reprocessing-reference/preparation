package com.csgroup.auxip.model.jpa;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;

import jdk.jfr.Name;

import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class TimeRange {
	private Timestamp Start;
	@Column(name = "stop")
	private Timestamp End;

	// needed by Olingo OData API
	public static final String CT_NAME = "TimeRange";
	public static final FullQualifiedName FQN = new FullQualifiedName(Globals.NAMESPACE, CT_NAME);
	public static final String ES_NAME = "TimeRanges";


	public Timestamp getStart() {
		return Start;
	}

	public void setStart(Timestamp start) {
		Start = start;
	}

	public Timestamp getEnd() {
		return End;
	}

	public void setEnd(Timestamp end) {
		End = end;
	}
	
	public static CsdlComplexType getComplexType() {

		CsdlComplexType complexType = null;

		complexType = new CsdlComplexType().setName(CT_NAME).setProperties(Arrays.asList(
			new CsdlProperty().setName("Start").setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(6),
			new CsdlProperty().setName("End").setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(6)  ));

		return complexType;
	}
	 
}
