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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Property {
	private String Name;
	private String Value;
		
	// needed by Olingo OData API
	public static final String CT_NAME = "Property";
	public static final FullQualifiedName FQN = new FullQualifiedName(Globals.NAMESPACE, CT_NAME);

	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getValue() {
		return Value;
	}
	public void setValue(String value) {
		Value = value;
	}	


	public static CsdlComplexType getComplexType() {

		CsdlComplexType complexType = null;

		complexType = new CsdlComplexType().setName(CT_NAME).setProperties(Arrays.asList(
			new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false),
			new CsdlProperty().setName("Value").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false)  ));

		return complexType;
	}
}
