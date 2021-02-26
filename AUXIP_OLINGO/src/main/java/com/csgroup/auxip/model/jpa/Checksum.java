package com.csgroup.auxip.model.jpa;



import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Embeddable;
import javax.persistence.Id;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class Checksum {

	private String Algorithm;
	private String Value;
	private Timestamp ChecksumDate;

	// needed by Olingo OData API
	public static final String CT_NAME = "Checksum";
	public static final FullQualifiedName FQN = new FullQualifiedName(Globals.NAMESPACE, CT_NAME);

	public String getAlgorithm() {
		return Algorithm;
	}

	public void setAlgorithm(String algorithm) {
		Algorithm = algorithm;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public Timestamp getChecksumDate() {
		return ChecksumDate;
	}

	public void setChecksumDate(Timestamp checksumDate) {
		ChecksumDate = checksumDate;
	}

	public static CsdlComplexType getComplexType() {

		CsdlComplexType complexType = null;

		complexType = new CsdlComplexType().setName(CT_NAME).setProperties(Arrays.asList(
			new CsdlProperty().setName("ChecksumDate").setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()),
			new CsdlProperty().setName("Algorithm").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false),
			new CsdlProperty().setName("Value").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false)  ));

		return complexType;
	}

}
