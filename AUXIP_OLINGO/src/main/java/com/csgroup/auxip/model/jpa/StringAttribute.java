package com.csgroup.auxip.model.jpa;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import javax.persistence.Embeddable;
import java.util.Arrays;

@Embeddable
public class StringAttribute extends Attribute {
	
	private String Value;

	// needed by Olingo OData API
	public static final String ET_NAME = "StringAttribute";
	public static final FullQualifiedName FQN = new FullQualifiedName(Globals.NAMESPACE, ET_NAME);
	public static final String ES_NAME = "StringAttributes";

	public StringAttribute() {				
	}
	
	public StringAttribute(String name, String valueType, String value) {
		super(name, valueType);
		this.Value = value;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		this.Value = value;
	}
	
	
	public static CsdlEntityType getEntityType()
	{
		CsdlEntityType entityType = null;
		CsdlProperty value = new CsdlProperty().setName("Value").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		// configure EntityType
		entityType = new CsdlEntityType();
		entityType.setName(ET_NAME);
		entityType.setBaseType(Attribute.FQN);
		entityType.setProperties(Arrays.asList(value));
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

	@Override
	public Entity getOdataEntity()
	{
		Entity entity = super.getOdataEntity();

		// Add value property
		Property value = new Property("String", "Value",ValueType.PRIMITIVE,this.Value) ;
		entity.addProperty( value );
		
		// set a right type
		entity.setType(FQN.getFullQualifiedNameAsString());

		return entity;
	}

}
