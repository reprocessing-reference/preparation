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
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.Constants;

import java.io.ObjectInputStream.GetField;
import java.lang.module.FindException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.csgroup.auxip.controller.AuxipBeanUtil;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ManyToAny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.annotation.Transient;

import java.util.UUID;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



import java.net.HttpURLConnection;
import java.time.Duration;

import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.utils.IoUtils;


/**
 * @author nmeskini
 */
@Entity(name = "Product")
public class Product {
	@Id    
	private UUID Id;
    private String Name;
    private String ContentType;
    private long ContentLength;
    private Timestamp OriginDate;
    private Timestamp PublicationDate;
	private Timestamp EvictionDate;    
	
	
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
	private List<Checksum> Checksum;
	
    @Embedded
	private TimeRange ContentDate;
	
    @ElementCollection(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<StringAttribute> StringAttributes;
	
    @ElementCollection(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<IntegerAttribute> IntegerAttributes;
	
    @ElementCollection(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<DoubleAttribute> DoubleAttributes;
	
    @ElementCollection(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
    private List<DateTimeOffsetAttribute> DateTimeOffsetAttributes;
		

	// Product
	public static final String ET_NAME = "Product";
	public static final FullQualifiedName FQN = new FullQualifiedName(Globals.NAMESPACE, ET_NAME);
    public static final String ES_NAME = "Products";
	/** Special property to store the media content **/
	private static final String MEDIA_PROPERTY_NAME = "$value";

	public UUID getId() {
		return Id;
	}

	public void setId(UUID id) {
		Id = id;
	}
	@Column
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
	@Column
	public String getContentType() {
		return ContentType;
	}

	public void setContentType(String contentType) {
		ContentType = contentType;
	}
	@Column
	public long getContentLength() {
		return ContentLength;
	}

	public void setContentLength(long contentLength) {
		ContentLength = contentLength;
	}
	@Column
	public Timestamp getOriginDate() {
		return OriginDate;
	}

	public void setOriginDate(Timestamp originDate) {
		OriginDate = originDate;
	}
	@Column
	public Timestamp getPublicationDate() {
		return PublicationDate;
	}

	public void setPublicationDate(Timestamp publicationDate) {
		PublicationDate = publicationDate;
	}
	@Column
	public Timestamp getEvictionDate() {
		return EvictionDate;
	}

	public void setEvictionDate(Timestamp evictionDate) {
		EvictionDate = evictionDate;
	}
	
	@Column
	public TimeRange getContentDate() {
		return ContentDate;
	}

	public void setContentDate(TimeRange contentDate) {
		ContentDate = contentDate;
	}
    

	public static CsdlEntityType getEntityType()
	{
		CsdlEntityType entityType = null;

		// create EntityType properties
		CsdlProperty id = new CsdlProperty().setName("ID").setType(EdmPrimitiveTypeKind.Guid.getFullQualifiedName());
		CsdlProperty name = new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		CsdlProperty contenttype = new CsdlProperty().setName("ContentType").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		CsdlProperty contentlength = new CsdlProperty().setName("ContentLength").setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
		CsdlProperty origindate = new CsdlProperty().setName("OriginDate").setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(3);
		CsdlProperty publicationdate = new CsdlProperty().setName("PublicationDate").setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(3);
		CsdlProperty evictiondate = new CsdlProperty().setName("EvictionDate").setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(3);
		CsdlProperty checksum = new CsdlProperty().setName("Checksum").setType(com.csgroup.auxip.model.jpa.Checksum.FQN).setCollection(true); //TODO : set a collection of checksums
		CsdlProperty contentDate = new CsdlProperty().setName("ContentDate").setType(TimeRange.FQN);

		// create PropertyRef for Key element
		CsdlPropertyRef propertyRef = new CsdlPropertyRef();
		propertyRef.setName("ID");

		// navigation property: many-to-one, 
		CsdlNavigationProperty attributesNavProp = new CsdlNavigationProperty().setName("Attributes").setType(Attribute.FQN).setCollection(true);
		// CsdlNavigationProperty attributesNavProp = new CsdlNavigationProperty().setName("Attributes").setType(Attribute.FQN).setContainsTarget(true).setCollection(true);
		// CsdlNavigationProperty stringAttributesNavProp = new CsdlNavigationProperty().setName(StringAttribute.FQN.getFullQualifiedNameAsString()).setType(StringAttribute.FQN);
		// CsdlNavigationProperty stringAttributesNavProp = new CsdlNavigationProperty().setName("StringAttributes").setType(StringAttribute.FQN).setContainsTarget(true).setCollection(true);
		// CsdlNavigationProperty integerAttributesNavProp = new CsdlNavigationProperty().setName("IntegerAttributes").setType(IntegerAttribute.FQN).setContainsTarget(true).setCollection(true);
		// CsdlNavigationProperty doubleAttributesNavProp = new CsdlNavigationProperty().setName("DoubleAttributes").setType(DoubleAttribute.FQN).setContainsTarget(true).setCollection(true);
		// CsdlNavigationProperty dateTimeOffesetAttributesNavProp = new CsdlNavigationProperty().setName("DateTimeOffsetAttributes").setType(DateTimeOffsetAttribute.FQN).setContainsTarget(true).setCollection(true);
		List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
		navPropList.add(attributesNavProp);
		// navPropList.add(stringAttributesNavProp);
		// navPropList.add(integerAttributesNavProp);
		// navPropList.add(doubleAttributesNavProp);
		// navPropList.add(dateTimeOffesetAttributesNavProp);

		// configure EntityType
		entityType = new CsdlEntityType();
		entityType.setName(ET_NAME);
		entityType.setProperties(Arrays.asList(id, name,contenttype,contentlength ,origindate,publicationdate,evictiondate,checksum,contentDate));
		entityType.setKey(Arrays.asList(propertyRef));
		entityType.setNavigationProperties(navPropList);
		entityType.setHasStream(true);
		
		return entityType;
	
	}

	public static CsdlEntitySet getEntitySet() 
	{

        CsdlEntitySet entitySet = new CsdlEntitySet();
        entitySet.setName(ES_NAME);
        entitySet.setType(FQN);

        // Navigation
        CsdlNavigationPropertyBinding attributesNavPropBinding = new CsdlNavigationPropertyBinding();
        attributesNavPropBinding.setTarget(Attribute.ES_NAME); // the target entity set, where the navigation property points to
		attributesNavPropBinding.setPath(Attribute.ES_NAME); // the path from entity type to navigation property
		
		// CsdlNavigationPropertyBinding stringAttributesNavPropBinding = new CsdlNavigationPropertyBinding();
        // stringAttributesNavPropBinding.setTarget(ES_NAME); // the target entity set, where the navigation property points to
		// stringAttributesNavPropBinding.setPath(StringAttribute.FQN.getFullQualifiedNameAsString()); // the path from entity type to navigation property
	
		// CsdlNavigationPropertyBinding integerAttributesNavPropBinding = new CsdlNavigationPropertyBinding();
        // integerAttributesNavPropBinding.setTarget(IntegerAttribute.ES_NAME); // the target entity set, where the navigation property points to
		// integerAttributesNavPropBinding.setPath(IntegerAttribute.ES_NAME); // the path from entity type to navigation property

		// CsdlNavigationPropertyBinding doubleAttributesNavPropBinding = new CsdlNavigationPropertyBinding();
        // doubleAttributesNavPropBinding.setTarget(DoubleAttribute.ES_NAME); // the target entity set, where the navigation property points to
		// doubleAttributesNavPropBinding.setPath(DoubleAttribute.ES_NAME); // the path from entity type to navigation property

		// CsdlNavigationPropertyBinding dateTimeOffsetAttributesNavPropBinding = new CsdlNavigationPropertyBinding();
		// dateTimeOffsetAttributesNavPropBinding.setTarget(DateTimeOffsetAttribute.ES_NAME); // the target entity set, where the navigation property points to
		// dateTimeOffsetAttributesNavPropBinding.setPath(DateTimeOffsetAttribute.ES_NAME); // the path from entity type to navigation property

		entitySet.setNavigationPropertyBindings(Arrays.asList(attributesNavPropBinding));
																// stringAttributesNavPropBinding,
																// integerAttributesNavPropBinding,
																// doubleAttributesNavPropBinding ,
																// dateTimeOffsetAttributesNavPropBinding
																// ));

		return entitySet;
	} 

	public org.apache.olingo.commons.api.data.Entity getOdataEntity(Boolean expandAttributes)
	{
		org.apache.olingo.commons.api.data.Entity entity = new org.apache.olingo.commons.api.data.Entity();

		// create EntityType properties
		Property id = new Property("Guid", "ID",ValueType.PRIMITIVE,this.Id) ;
		Property name = new Property("String", "Name",ValueType.PRIMITIVE,this.Name) ;
		Property contenttype = new Property("String", "ContentType",ValueType.PRIMITIVE,this.ContentType) ;
		Property contentlength = new Property("Int64", "ContentLength",ValueType.PRIMITIVE,this.ContentLength) ;
		Property origindate = new Property("DateTimeOffset", "OriginDate",ValueType.PRIMITIVE,this.OriginDate) ;
		Property evictiondate = new Property("DateTimeOffset", "EvictionDate",ValueType.PRIMITIVE,this.EvictionDate) ;
		Property publicationdate = new Property("DateTimeOffset", "PublicationDate",ValueType.PRIMITIVE,this.PublicationDate) ;

		Property mediaValue = new Property(null, MEDIA_PROPERTY_NAME,ValueType.PRIMITIVE, this.getPresignedUrl().getBytes()) ;

		ComplexValue timeRange = new ComplexValue() ;
		timeRange.getValue().add( new Property(null, "Start", ValueType.PRIMITIVE, this.ContentDate.getStart())) ;
		timeRange.getValue().add( new Property(null, "End", ValueType.PRIMITIVE, this.ContentDate.getEnd()) );
			 
		Property contentDate = new Property(null, "ContentDate",ValueType.COMPLEX, timeRange ) ;

		List<ComplexValue> checksums = new ArrayList<>();
		for( Checksum cs : this.Checksum )
		{
			ComplexValue checksum = new ComplexValue() ;
			checksum.getValue().add( new Property(null, "ChecksumDate", ValueType.PRIMITIVE, cs.getChecksumDate() ) );
			checksum.getValue().add( new Property(null, "Algorithm", ValueType.PRIMITIVE, cs.getAlgorithm() ) );
			checksum.getValue().add( new Property(null, "Value", ValueType.PRIMITIVE, cs.getValue() ) );

			checksums.add(checksum);
		}
		Property checksum = new Property(null, "Checksum",ValueType.COLLECTION_COMPLEX,checksums) ;

		entity.addProperty( id );
		entity.addProperty( name );
		entity.addProperty( contenttype );
		entity.addProperty( contentlength );
		entity.addProperty( origindate );
		entity.addProperty( evictiondate );
		entity.addProperty( publicationdate );
		entity.addProperty( contentDate );
		entity.addProperty( mediaValue );
		entity.addProperty( checksum );

		entity.setMediaContentType(org.apache.olingo.commons.api.format.ContentType.parse("text/plain").toContentTypeString());

		entity.setType(FQN.getFullQualifiedNameAsString());

		try {
			StringBuilder sb = new StringBuilder(ES_NAME).append("(");
			sb.append(id.asPrimitive()).append(")");

			entity.setId( new URI(sb.toString()) );
			
		  } catch (URISyntaxException e) {
			throw new ODataRuntimeException("Unable to create (Atom) id for entity: " + entity, e);
		  }

		if( expandAttributes )
		{
			// Add attibutes set here

			Link link = new Link();
			link.setTitle("Attributes");
			link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
			link.setRel(Constants.NS_ASSOCIATION_LINK_REL + "Attributes");
			
			EntityCollection attributes = new EntityCollection();
			List<Attribute> attributesList = new ArrayList<>();
			attributesList.addAll(this.StringAttributes);
			attributesList.addAll(this.IntegerAttributes);
			attributesList.addAll(this.DoubleAttributes);
			attributesList.addAll(this.DateTimeOffsetAttributes);

			// add all attributes to the entity collection
			for( Attribute att : attributesList)
			{
				attributes.getEntities().add( att.getOdataEntity() );
			}
			link.setInlineEntitySet(attributes);
		    link.setHref("Products(uuid)/Attributes".replace( "uuid", this.Id.toString() ));
			entity.getNavigationLinks().add(link);
		}

		return entity;
	}

	public String getPresignedUrl() {
		// inject S3Presigner
		S3Presigner presigner = AuxipBeanUtil.getBean(S3Presigner.class);

		// Create a GetObjectRequest to be pre-signed
		GetObjectRequest getObjectRequest =
				GetObjectRequest.builder()
								.bucket("auxip")
								.key("uuid/fullname".replace("uuid", this.Id.toString()).replace("fullname", this.Name) )
								.build();
   
		// Create a GetObjectPresignRequest to specify the signature duration
		GetObjectPresignRequest getObjectPresignRequest =
			GetObjectPresignRequest.builder()
								   .signatureDuration(Duration.ofMinutes(10))
								   .getObjectRequest(getObjectRequest)
								   .build();
   
		// Generate the presigned request
		PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
   
		// Log the presigned URL, for example.
		// System.out.println("Presigned URL: " + presignedGetObjectRequest.url());
   
		// It is recommended to close the S3Presigner when it is done being used, because some credential
		// providers (e.g. if your AWS profile is configured to assume an STS role) require system resources
		// that need to be freed. If you are using one S3Presigner per application (as recommended), this
		// usually is not needed.
		presigner.close();

		return presignedGetObjectRequest.url().toString();
	}





}
