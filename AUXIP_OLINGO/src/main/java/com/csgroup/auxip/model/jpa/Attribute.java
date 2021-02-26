package com.csgroup.auxip.model.jpa;


import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.csgroup.auxip.model.jpa.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@MappedSuperclass
public abstract class Attribute {	
	
	private String Name;
	private String ValueType;
	
	// needed by Olingo OData API
	public static final String ET_NAME = "Attribute";
	public static final FullQualifiedName FQN = new FullQualifiedName(Globals.NAMESPACE, ET_NAME);
	public static final String ES_NAME = "Attributes";
	
	protected Attribute() {		
	}
	
	protected Attribute(String name, String valueType) {
		Name = name;
		ValueType = valueType;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getValueType() {
		return ValueType;
	}

	public void setValueType(String valueType) {
		ValueType = valueType;
	}	

	public static CsdlEntityType getEntityType()
	{
		CsdlEntityType entityType = null;

		// create EntityType properties
		CsdlProperty name = new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		CsdlProperty valueType = new CsdlProperty().setName("ValueType").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	
		// configure EntityType
		entityType = new CsdlEntityType();
		entityType.setName(ET_NAME);
		entityType.setProperties(Arrays.asList(name,valueType));
		entityType.setAbstract(true);

		return entityType;
	}


	public static CsdlEntitySet getEntitySet() 
	{

        CsdlEntitySet entitySet = new CsdlEntitySet();
        entitySet.setName(ES_NAME);
        entitySet.setType(FQN);

		entitySet.setIncludeInServiceDocument(false);
	
		return entitySet;
	} 



	public org.apache.olingo.commons.api.data.Entity getOdataEntity()
	{
		org.apache.olingo.commons.api.data.Entity entity = new org.apache.olingo.commons.api.data.Entity();

		// create EntityType properties
		Property name = new Property("String", "Name",org.apache.olingo.commons.api.data.ValueType.PRIMITIVE,this.Name) ;
		Property valueType = new Property("String", "ValueType",org.apache.olingo.commons.api.data.ValueType.PRIMITIVE,this.ValueType) ;
		
		entity.addProperty( name );
		entity.addProperty( valueType );

		entity.setType(FQN.getFullQualifiedNameAsString());

		return entity;
	}
}
