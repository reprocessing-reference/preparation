/**
 * This class will be used to :
 * 1 - retrieves data from the databse
 * 2 - create new entities 
 * 3 - applying subscription actions
 */
package com.csgroup.auxip.model.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceLambdaAny;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Binary;
import org.apache.olingo.server.api.uri.queryoption.expression.Method;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;

import com.csgroup.auxip.model.jpa.Attribute;
import com.csgroup.auxip.model.jpa.Globals;
import com.csgroup.auxip.model.jpa.Metric;
import com.csgroup.auxip.model.jpa.Product;
import com.csgroup.auxip.model.jpa.StringAttribute;
import com.csgroup.auxip.model.jpa.Subscription;
import com.csgroup.auxip.model.jpa.SubscriptionStatus;
import com.csgroup.auxip.model.jpa.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class AttributeFilter {

	public String attributeName;
	public String attributeValue;
	public String valueOperator;
	public String attributeType;

	public void print() {
		System.out.println(
				attributeType + " : { " + attributeName + " : " + attributeValue + " valueOperator :" + valueOperator + " }");
	}
}

public class Storage {

	private static final Logger LOG = LoggerFactory.getLogger(Storage.class);

	private EntityManagerFactory entityManagerFactory;
	private AccessToken accessToken;
	private User user;

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
		this.user = new User(accessToken);

	}
	// utility member
	private Map<String, String> operatorsMapping = new HashMap<>();

	public Storage() {

		operatorsMapping.put("eq", "=");
		operatorsMapping.put("ne", "!=");
		operatorsMapping.put("lt", "<");
		operatorsMapping.put("le", "<=");
		operatorsMapping.put("gt", ">");
		operatorsMapping.put("ge", ">=");

	}

	public Entity createSubscription(Entity requestEntity ) {

		EntityManager entityManager = this.entityManagerFactory.createEntityManager();
		EntityTransaction transac = entityManager.getTransaction();

		transac.begin();
		Subscription subscription = new Subscription();

		subscription.setFilterParam(requestEntity.getProperty("FilterParam").getValue().toString());
		subscription.setStatus( SubscriptionStatus.running );
		subscription.setNotificationEndpoint(requestEntity.getProperty("NotificationEndpoint").getValue().toString());
		subscription.setNotificationEpUsername(requestEntity.getProperty("NotificationEpUsername").getValue().toString());
		subscription.setNotificationEpPassword(requestEntity.getProperty("NotificationEpPassword").getValue().toString());
		subscription.setSubmissionDate( new Timestamp(System.currentTimeMillis()) );
		try {
			entityManager.persist(subscription);
			if (transac.isActive()) {
				transac.commit();
			} else {
				transac.rollback();
			}				
		} finally {                    
			entityManager.close();
		}

		return subscription.getOdataEntity();

	}


	public Integer subscriptionAction(final String actionName,final String uuid ) {

		EntityManager entityManager = this.entityManagerFactory.createEntityManager();
		EntityTransaction transac = entityManager.getTransaction();


		String queryString = "SELECT subscription FROM " + Subscription.class.getName() + " subscription WHERE subscription.Id =  'uuid'".replace("uuid", uuid) ;

		Query query = entityManager.createQuery(queryString);
		Subscription subscription = (Subscription)query.getSingleResult();

		try {

			transac.begin();
			// Only not cancelled Subscription are concerned 
			if( !subscription.getStatus().equals(SubscriptionStatus.cancelled) )
			{
				if( actionName.equals("Pause") )
				{
					subscription.setStatus( SubscriptionStatus.paused );
				}else if ( actionName.equals("Resume") )
				{
					subscription.setStatus( SubscriptionStatus.running );
				}else if ( actionName.equals("Cancel") )
				{
					subscription.setStatus( SubscriptionStatus.cancelled );
				}
			}else{
				return Globals.NOT_OK;
			}

			transac.commit();
			entityManager.close();  

			return Globals.OK;

		} catch (Exception e) {
			return Globals.NOT_OK;
		}

	}



	public String getAnyQuery(Expression anyExpression, String typeAttributes) {

		String query = "";

		if (anyExpression instanceof Binary) {
			Binary binaryExpression = (Binary) anyExpression;

			// Exprerssion for attribute Name
			Binary nameExpression = (Binary) binaryExpression.getLeftOperand();
			// String nameOperator = nameExpression.getOperator().toString().toUpperCase();
			String attributeName = ((Member) nameExpression.getLeftOperand()).getResourcePath().getUriResourceParts().get(1)
					.getSegmentValue();
			String attribiteNameValue = ((Literal) nameExpression.getRightOperand()).getText();

			// Expression for attribute Value
			Binary valueExpression = (Binary) binaryExpression.getRightOperand();
			String valueOperator = valueExpression.getOperator().toString().toLowerCase();
			String attributeValue = ((Member) valueExpression.getLeftOperand()).getResourcePath().getUriResourceParts().get(1)
					.getSegmentValue();
			// Attributes with type string value comes with '' but DateTimeOffset attributes
			// shouldn't , remove '' and them after.
			String attributeValueValue = ((Literal) valueExpression.getRightOperand()).getText().replace("'", "");

			// we only deal with a logical request which asks for a value of a specified and
			// named attibute.
			if (attributeName.equals("Name") && attributeValue.equals("Value")) {
				query = " entity.id IN ( SELECT p.id FROM Product.class p JOIN p.typeAttributes attribute WHERE attribute.Name =  attributeName AND attribute.Value valueOperator  'attributeValue' ) "
						.replace("Product.class", Product.class.getName()).replace("typeAttributes", typeAttributes)
						.replace("attributeName", attribiteNameValue).replace("attributeValue", attributeValueValue)
						.replace("valueOperator", operatorsMapping.get(valueOperator));
			}
		} else {
			// do nothing , no logical request => no response
			query = "";
		}
		return query;
	}


	public String getQuery(Expression filterExpression,String placeHolder, String outQuery)
	{
		//1 => get the requested entitySet / possible entitySets are : Products / Subscription / Metrics / Attributes
		Binary binaryExpression = null;
		String placeHolderName = placeHolder ;

		int operandId = 0;

		if( filterExpression instanceof Binary )
		{
			List<Pair<String,Expression>> filters = new ArrayList<>();

			do {
				binaryExpression = (Binary)filterExpression;
				Expression leftExpression = binaryExpression.getLeftOperand();
				Expression rightExpression =  binaryExpression.getRightOperand();
				BinaryOperatorKind operator =  binaryExpression.getOperator();
				String rightOperand = "rightOperand_" + operandId ;
				String leftOperand = "leftOperand_" + operandId ;

				// if the binaryExpression is simple : a filter with property and value  
				if( leftExpression instanceof Member && rightExpression instanceof Literal )
				{
					List<UriResource> uriResources = ((Member)leftExpression).getResourcePath().getUriResourceParts();

					String member = uriResources.get(0).toString();
					if( uriResources.size() > 1 ) //as for ContentDate
					{
						member += "." + uriResources.get(1).toString();
					}
					String literal = ((Literal)rightExpression).getText().replace("'", "");

					String whereFilter = " ( entity.member operator 'literal' ) "
							.replace("member", member)
							.replace("literal", literal )
							.replace("operator", this.operatorsMapping.get(operator.toString().toLowerCase()) ) ;

					if( outQuery.contains(placeHolderName) )
					{
						outQuery = outQuery.replace(placeHolderName, whereFilter);
					}else{
						outQuery +=  whereFilter ;
					}
				}else
				{
					// else , loop over expression components 
					if( outQuery.contains(placeHolderName) )
					{
						outQuery = outQuery.replace(placeHolderName, String.format( " ( ( %s ) %s ( %s ) ) " , leftOperand,operator.toString().toUpperCase(),rightOperand) );
					}else{
						outQuery +=  String.format( " ( ( %s ) %s ( %s ) ) " , leftOperand,operator.toString().toUpperCase(),rightOperand) ;
					}

					operandId += 1;
					if( leftExpression instanceof Method || leftExpression instanceof Member )
					{
						outQuery = getQuery(leftExpression, leftOperand ,outQuery);
					}else{
						filters.add( Pair.of(leftOperand , leftExpression) );
					}
					if( rightExpression instanceof Method || rightExpression instanceof Member )
					{
						outQuery = getQuery(rightExpression,rightOperand, outQuery);
					}else{
						filters.add( Pair.of(rightOperand , rightExpression) );
					}        
				}
				if( filters.size() >  0)
				{
					filterExpression = filters.get(0).getSecond();
					placeHolderName = filters.get(0).getFirst();
					filters.remove(0);
				}else{
					filterExpression = null;
				}

			} while ( filterExpression instanceof Binary );


		}else if ( filterExpression instanceof Method )
		{
			Method methodFilter = (Method)filterExpression;
			//method implementation => where Member method parameter 

			String method = methodFilter.getMethod().toString();
			Object[] parameters = methodFilter.getParameters().toArray();
			//only two parameters are expected : class Member and Literal
			String member = ((Member)parameters[0]).getResourcePath().getUriResourceParts().get(0).toString();
			String literal =  ((Literal)parameters[1]).getText().replace("'", "");

			String whereFilter = "" ;
			//supported functions are : contains, startswith, endswith
			if( method.equals("contains")) // CONTAINS
			{
				whereFilter = " ( entity.member LIKE '%literal%' ) ".replace("member", member).replace("literal", literal ) ;

			}else if( method.equals("startswith") ) // STARTSWITH
			{
				whereFilter = " ( entity.member LIKE 'literal%' ) ".replace("member", member).replace("literal", literal ) ;

			}else if( method.equals("endswith") ) //ENDSWITH
			{
				whereFilter = " ( entity.member LIKE '%literal' ) ".replace("member", member).replace("literal", literal ) ;
			}

			if( outQuery.contains(placeHolderName) )
			{
				outQuery = outQuery.replace(placeHolderName,whereFilter);
			}else{
				outQuery += whereFilter ; 
			}

		}else 
		{ 
			// Member  => where Member operator value
			Member memberFilter = (Member)filterExpression;

			List<UriResource> uriResourceParts = memberFilter.getResourcePath().getUriResourceParts();
			// the first part is always a member
			String member = uriResourceParts.get(0).toString();

			if( member.equals("Attributes") )
			{      
				// the second part should be  : 'any' lambda function 
				UriResource secondPart = uriResourceParts.get(1);

				String attributeType = ((UriResourceNavigation)uriResourceParts.get(0)).getTypeFilterOnEntry().getName();
				// this could be StringAttibutes / IntegerAttributes / DoubleAttributes or DateTimeOfsetAttributes
				// adding 's' in order to be able to join with the right TypeAttribute set 
				String typeAttributes = attributeType + "s" ;
				//Any lambda function 
				if( secondPart instanceof UriResourceLambdaAny)
				{
					Expression anyExpression = ((UriResourceLambdaAny)secondPart).getExpression();

					String anyQuery = getAnyQuery(anyExpression,typeAttributes) ;
					if( outQuery.contains(placeHolderName) )
					{
						outQuery = outQuery.replace(placeHolderName,anyQuery);
					}else{
						outQuery += anyQuery ; 
					}
				}
			}
		}

		return outQuery;
	}

	public String getQueryOld(Expression filterExpression,String placeHolder, String outQuery)
	{
		//1 => get the requested entitySet / possible entitySets are : Products / Subscription / Metrics / Attributes
		Binary binaryExpression = null;
		String placeHolderName = placeHolder ;

		int operandId = 0;

		if( filterExpression instanceof Binary )
		{
			List<Pair<String,Expression>> filters = new ArrayList<>();

			do {
				binaryExpression = (Binary)filterExpression;
				Expression leftExpression = binaryExpression.getLeftOperand();
				Expression rightExpression =  binaryExpression.getRightOperand();
				BinaryOperatorKind operator =  binaryExpression.getOperator();
				String rightOperand = "rightOperand_" + operandId ;
				String leftOperand = "leftOperand_" + operandId ;

				// if the binaryExpression is simple : a filter with property and value  
				if( leftExpression instanceof Member && rightExpression instanceof Literal )
				{
					List<UriResource> uriResources = ((Member)leftExpression).getResourcePath().getUriResourceParts();

					String member = uriResources.get(0).toString();
					if( uriResources.size() > 1 ) //as for ContentDate
					{
						member += "." + uriResources.get(1).toString();
					}
					String literal = ((Literal)rightExpression).getText().replace("'", "");

					String whereFilter = " ( entity.member operator 'literal' ) "
							.replace("member", member)
							.replace("literal", literal )
							.replace("operator", this.operatorsMapping.get(operator.toString().toLowerCase()) ) ;

					if( outQuery.contains(placeHolderName) )
					{
						outQuery = outQuery.replace(placeHolderName, whereFilter);
					}else{
						outQuery +=  whereFilter ;
					}
				}else
				{
					// else , loop over expression components 
					if( outQuery.contains(placeHolderName) )
					{
						outQuery = outQuery.replace(placeHolderName, String.format( " ( ( %s ) %s ( %s ) ) " , leftOperand,operator.toString().toUpperCase(),rightOperand) );
					}else{
						outQuery +=  String.format( " ( ( %s ) %s ( %s ) ) " , leftOperand,operator.toString().toUpperCase(),rightOperand) ;
					}

					operandId += 1;
					if( leftExpression instanceof Method || leftExpression instanceof Member )
					{
						outQuery = getQuery(leftExpression, leftOperand ,outQuery);
					}else{
						filters.add( Pair.of(leftOperand , leftExpression) );
					}
					if( rightExpression instanceof Method || rightExpression instanceof Member )
					{
						outQuery = getQuery(rightExpression,rightOperand, outQuery);
					}else{
						filters.add( Pair.of(rightOperand , rightExpression) );
					}        
				}
				if( filters.size() >  0)
				{
					filterExpression = filters.get(0).getSecond();
					placeHolderName = filters.get(0).getFirst();
					filters.remove(0);
				}else{
					filterExpression = null;
				}

			} while ( filterExpression instanceof Binary );


		}else if ( filterExpression instanceof Method )
		{
			Method methodFilter = (Method)filterExpression;
			//method implementation => where Member method parameter 

			String method = methodFilter.getMethod().toString();
			Object[] parameters = methodFilter.getParameters().toArray();
			//only two parameters are expected : class Member and Literal
			String member = ((Member)parameters[0]).getResourcePath().getUriResourceParts().get(0).toString();
			String literal =  ((Literal)parameters[1]).getText().replace("'", "");

			String whereFilter = "" ;
			//supported functions are : contains, startswith, endswith
			if( method.equals("contains")) // CONTAINS
			{
				whereFilter = " ( entity.member LIKE '%literal%' ) ".replace("member", member).replace("literal", literal ) ;

			}else if( method.equals("startswith") ) // STARTSWITH
			{
				whereFilter = " ( entity.member LIKE 'literal%' ) ".replace("member", member).replace("literal", literal ) ;

			}else if( method.equals("endswith") ) //ENDSWITH
			{
				whereFilter = " ( entity.member LIKE '%literal' ) ".replace("member", member).replace("literal", literal ) ;
			}

			if( outQuery.contains(placeHolderName) )
			{
				outQuery = outQuery.replace(placeHolderName,whereFilter);
			}else{
				outQuery += whereFilter ; 
			}

		}else 
		{ 
			// Member  => where Member operator value
			Member memberFilter = (Member)filterExpression;

			List<UriResource> uriResourceParts = memberFilter.getResourcePath().getUriResourceParts();
			// the first part is always a member
			String member = uriResourceParts.get(0).toString();

			if( member.equals("Attributes") )
			{      
				// the second part should be  : 'any' lambda function 
				UriResource secondPart = uriResourceParts.get(1);

				String attributeType = ((UriResourceNavigation)uriResourceParts.get(0)).getTypeFilterOnEntry().getName();
				// this could be StringAttibutes / IntegerAttributes / DoubleAttributes or DateTimeOfsetAttributes
				// adding 's' in order to be able to join with the right TypeAttribute set 
				String typeAttributes = attributeType + "s" ;
				//Any lambda function 
				if( secondPart instanceof UriResourceLambdaAny)
				{
					Expression anyExpression = ((UriResourceLambdaAny)secondPart).getExpression();

					String anyQuery = getAnyQuery(anyExpression,typeAttributes) ;
					if( outQuery.contains(placeHolderName) )
					{
						outQuery = outQuery.replace(placeHolderName,anyQuery);
					}else{
						outQuery += anyQuery ; 
					}
				}
			}
		}

		return outQuery;
	}

	public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet, FilterOption filterOption,
			ExpandOption expandOption,OrderByOption orderByOption, SkipOption skipOption, TopOption topOption) throws ODataApplicationException {
		EntityCollection entitySet = new EntityCollection();

		String queryString;
		String entitySetName = edmEntitySet.getName();
		String className;
		switch (entitySetName) {
		case Product.ES_NAME:
			className = Product.class.getName();
			break;
		case Subscription.ES_NAME:
			className = Subscription.class.getName();
			break;
		case Metric.ES_NAME:
			className = Metric.class.getName();
			break;
		default:
			throw new ODataApplicationException("No Class found for "+entitySetName, HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);		
		}
		if( filterOption != null )
		{
			Expression filterExpression = filterOption.getExpression();
			queryString = "SELECT entity FROM " + className + " entity WHERE PLACE_HOLDER";
			queryString = getQuery(filterExpression, "PLACE_HOLDER", queryString);
		}else
		{
			queryString = "SELECT entity FROM " + className + " entity" ;
		}

		// add OrderBy Options
		//queryString = addOrderByOption(entitySetName,queryString, orderByOption);		
		LOG.debug("Query: "+queryString);
		EntityManager entityManager = this.entityManagerFactory.createEntityManager();

		Query query = entityManager.createQuery(queryString);

		//Set the top option
		if (topOption != null)
		{
			int topNumber = topOption.getValue();
			if (topNumber >= 0) {
				LOG.debug("In max");
				query.setMaxResults(topOption.getValue());
				query.setFirstResult(0);
			} else {
				throw new ODataApplicationException("Invalid value for $top", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
			}
		}
		//Set the skipOption
		if (skipOption != null)
		{
			int skipNumber = skipOption.getValue();
			if (skipNumber >= 0) {
				query.setFirstResult(skipNumber);
			} else {
				throw new ODataApplicationException("Invalid value for $skip", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
			}
		}

		//Cast to destination class
		switch (entitySetName) {
		case Product.ES_NAME:
			LOG.debug("Top Be");
			List<Product> products;
			try {
				products = query.getResultList();
			} finally {
				entityManager.close();
			}
			LOG.debug("Top Ok :"+String.valueOf(products.size()));			
			Boolean expandAttributes = false;
			for (Product product : products) {
				entitySet.getEntities().add(product.getOdataEntity(expandAttributes));
			}
			break;
		case Subscription.ES_NAME:
			List<Subscription> subscriptions = query.getResultList();
			for (Subscription subscription : subscriptions) {
				entitySet.getEntities().add(subscription.getOdataEntity());
			}
			break;
		case Metric.ES_NAME:
			List<Metric> metrics = query.getResultList();
			for (Metric metric : metrics) {
				entitySet.getEntities().add(metric.getOdataEntity());
			}
			break;
		default:		
			break;
		}    

		return entitySet;
	}

	public String addOrderByOption(String entitySetName,String query,OrderByOption orderByOption)
	{
		String orderBy = " ORDER BY ";
		// 3rd apply $orderby
		if (orderByOption != null ) 
		{
			List<OrderByItem> orderItemList = orderByOption.getOrders();
			for( OrderByItem orderByItem : orderItemList )
			{
				String orderDirection =  orderByItem.isDescending() ? " DESC" : " ASC";
				Expression expression = orderByItem.getExpression();
				if(expression instanceof Member){
					UriInfoResource resourcePath = ((Member)expression).getResourcePath();
					//Get property name
					String propertyName = resourcePath.getUriResourceParts().get(0).getSegmentValue();
					if( propertyName.equals("ContentDate"))
					{
						//get Member of a complexProperty => Start or End of ContentDate
						propertyName += "." + resourcePath.getUriResourceParts().get(1).getSegmentValue();
					}
					orderBy += " entity." + propertyName + orderDirection + " ,";
				}
			}
			// remove the last " ,"
			orderBy = orderBy.replaceAll(" ,$", "");

			query += orderBy ;

		}else{
			switch (entitySetName) {
			case Product.ES_NAME:
				// By default, the Products query are to be ordered by Publication Date, in an ascending order 
				query += " ORDER BY entity.PublicationDate ASC";
				break;
			case Subscription.ES_NAME:
				query += " ORDER BY entity.SubmissionDate ASC";
				break;		
			default:
				break;
			}
		}

		return query;
	}

	public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams) {
		Entity entity = null;

		EdmEntityType edmEntityType = edmEntitySet.getEntityType();

		String uuid = keyParams.get(0).getText();

		String queryString = "SELECT entity FROM " + Product.class.getName() + " entity WHERE entity.Id =  'uuid'".replace("uuid", uuid) ;

		EntityManager entityManager = this.entityManagerFactory.createEntityManager();

		Query query = entityManager.createQuery(queryString);
		Product product = (Product)query.getSingleResult();

		return product.getOdataEntity(false);
	}

	// Navigation

	public Entity getRelatedEntity(Entity entity, EdmEntityType relatedEntityType) throws ODataApplicationException {
		EntityCollection collection = getRelatedEntityCollection(entity, relatedEntityType);
		if (collection.getEntities().isEmpty()) {
			return null;
		}
		return collection.getEntities().get(0);
	}

	public Entity getRelatedEntity(Entity entity, EdmEntityType relatedEntityType, List<UriParameter> keyPredicates) throws ODataApplicationException {
		// Entity entity = null;

		EntityCollection relatedEntities = getRelatedEntityCollection(entity, relatedEntityType);
		// Util.findEntity(relatedEntityType, relatedEntities, keyPredicates);
		return entity;
	}

	public EntityCollection getRelatedEntityCollection(Entity sourceEntity, EdmEntityType targetEntityType) throws ODataApplicationException {
		EntityCollection navigationTargetEntityCollection = new EntityCollection();

		FullQualifiedName relatedEntityFqn = targetEntityType.getFullQualifiedName();
		String sourceEntityFqn = sourceEntity.getType();
		LOG.debug("SourceEntity : "+sourceEntityFqn);
		switch (sourceEntityFqn) {
		case Product.ET_NAME:
			String className = StringAttribute.class.getName();
			navigationTargetEntityCollection.setId(createId(sourceEntity, "ID", "Attributes"));
			String queryString = "SELECT entity FROM " + className + " entity WHERE entity.product_id = :productid";
			Map<String, Object> queryParams_m = new HashMap<String,Object>();						
			queryParams_m.put("productid", UUID.fromString(sourceEntity.getProperty("ID").toString()));
			EntityManager entityManager = this.entityManagerFactory.createEntityManager();
			try {
				List<StringAttribute> strAttribs;
				Query query_m = entityManager.createQuery(queryString);
				for (Map.Entry<String, Object> entry_m : queryParams_m.entrySet()) {
					query_m.setParameter(entry_m.getKey(), entry_m.getValue());
				}
				strAttribs = query_m.getResultList();
				for (StringAttribute s:strAttribs)
				{
					navigationTargetEntityCollection.getEntities().add(s.getOdataEntity());	
				}				
			} finally {                    
				entityManager.close();
			}
			break;
		}

		if (navigationTargetEntityCollection.getEntities().isEmpty()) {
			throw new ODataApplicationException("No related entity found ", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
			//return null;
		}

		return navigationTargetEntityCollection;
	}

	public Entity createMediaEntity(EdmEntityType entityType, String contentTypeString, byte[] mediaContent) throws ODataApplicationException {
		Entity entity = null;
		LOG.debug(contentTypeString);
		LOG.debug(entityType.getName());
		if(entityType.getName().equals(Product.ET_NAME)) {
			LOG.debug("In product");
			Product prod = new Product();
			if (contentTypeString.equals("application/json"))
			{
				LOG.debug("In product");
				ObjectMapper mapper = new ObjectMapper();
				try {
					JsonNode actualObj = mapper.readTree(new String(mediaContent));
					prod.setId(UUID.fromString(actualObj.get("uuid").asText()));
					//prod.setContentLength(actualObj.get("").);
					LOG.debug("Id: "+actualObj.get("uuid").asText());
				} catch (JsonProcessingException e) {
					throw new ODataApplicationException("Can't parse body as JSON for product POST", HttpStatusCode.BAD_REQUEST.getStatusCode(), 
							Locale.ENGLISH);
				}
			} else {
				throw new ODataApplicationException("Bad contenttype for Product POST request", HttpStatusCode.BAD_REQUEST.getStatusCode(), 
						Locale.ENGLISH);
			}
			LOG.debug("In product");
			entity = prod.getOdataEntity(false);
			LOG.debug("Entity : "+entity.getType());
			EntityManager entityManager = this.entityManagerFactory.createEntityManager();
			EntityTransaction transac = entityManager.getTransaction();			
			transac.begin();
			try {
				entityManager.persist(prod);
				if (transac.isActive()) {
					transac.commit();
				} else {
					transac.rollback();
				}				
			} finally {                    
				entityManager.close();
			}

		}

		return entity;
	}

	private URI createId(Entity entity, String idPropertyName) {
		return createId(entity, idPropertyName, null);
	}

	private URI createId(Entity entity, String idPropertyName, String navigationName) {
		try {
			StringBuilder sb = new StringBuilder(getEntitySetName(entity)).append("(");
			final Property property = entity.getProperty(idPropertyName);
			sb.append(property.asPrimitive()).append(")");
			if(navigationName != null) {
				sb.append("/").append(navigationName);
			}
			return new URI(sb.toString());
		} catch (URISyntaxException e) {
			throw new ODataRuntimeException("Unable to create (Atom) id for entity: " + entity, e);
		}
	}

	private String getEntitySetName(Entity entity) {
		if(entity.getType().equals(Product.ET_NAME)) {
			return Product.ES_NAME;
		} else if(entity.getType().equals(Subscription.ET_NAME)) {
			return Subscription.ES_NAME;
		}
		return entity.getType();
	}

}
