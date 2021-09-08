/**
 * This class will be used to :
 * 1 - retrieves data from the databse
 * 2 - create new entities 
 * 3 - applying subscription actions
 */
package com.csgroup.auxip.model.repository;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
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
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceLambdaAny;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
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
import com.csgroup.auxip.controller.AuxipBeanUtil;
import com.csgroup.auxip.model.jpa.Attribute;
import com.csgroup.auxip.model.jpa.Checksum;
import com.csgroup.auxip.model.jpa.DateTimeOffsetAttribute;
import com.csgroup.auxip.model.jpa.DoubleAttribute;
import com.csgroup.auxip.model.jpa.Globals;
import com.csgroup.auxip.model.jpa.IntegerAttribute;
import com.csgroup.auxip.model.jpa.Metric;
import com.csgroup.auxip.model.jpa.Product;
import com.csgroup.auxip.model.jpa.StringAttribute;
import com.csgroup.auxip.model.jpa.Subscription;
import com.csgroup.auxip.model.jpa.SubscriptionStatus;
import com.csgroup.auxip.model.jpa.TimeRange;
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
	
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
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

	public Entity createSubscription(Entity requestEntity ) throws ODataApplicationException {

		LOG.debug("createSubscription start ...");

		Subscription subscription = new Subscription();
		
		try {
			subscription.setFilterParam(requestEntity.getProperty("FilterParam").getValue().toString());
			subscription.setStatus( SubscriptionStatus.running );
			subscription.setNotificationEndpoint(requestEntity.getProperty("NotificationEndpoint").getValue().toString());
			subscription.setNotificationEpUsername(requestEntity.getProperty("NotificationEpUsername").getValue().toString());
			subscription.setNotificationEpPassword(requestEntity.getProperty("NotificationEpPassword").getValue().toString());
			subscription.setSubmissionDate( new Timestamp(System.currentTimeMillis()) );
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage());
			int statusCode = HttpStatusCode.BAD_REQUEST.getStatusCode();
			throw new ODataApplicationException("Can't create subscription ...", statusCode , Locale.ROOT,String.valueOf(statusCode));
		}
		EntityManager entityManager = this.entityManagerFactory.createEntityManager();
		EntityTransaction transac = entityManager.getTransaction();
		try {
			transac.begin();
			entityManager.persist(subscription);
			if (transac.isActive()) {
				transac.commit();
			} else {
				transac.rollback();
			}				
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage());
			int statusCode = HttpStatusCode.BAD_REQUEST.getStatusCode();
			throw new ODataApplicationException("Can't persist subscription ...",statusCode , Locale.ROOT,String.valueOf(statusCode));
		}finally {                    
			entityManager.close();
		}

		LOG.debug("createSubscription done ...");
		return subscription.getOdataEntity();

	}

	public List<Subscription> getAllValidSubscriptions(){
		String queryString1= "SELECT DISTINCT entity FROM com.csgroup.auxip.model.jpa.Subscription entity "
				+ "WHERE entity.Status = \'running\'";
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List<Subscription> subscriptions;
		try {	
			Query query_m1 = entityManager.createQuery(queryString1);
			subscriptions = query_m1.getResultList();
			LOG.debug("Number of subscriptions found : "+String.valueOf(subscriptions.size()));
		} finally {
			entityManager.close();
		}
		return subscriptions;
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


			return Globals.OK;

		} catch (Exception e) {
			return Globals.NOT_OK;
		} finally {
			entityManager.close();  
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
			// shouldn't , remove '' and add them after.
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
			String literal =  ((Literal)parameters[1]).getText().replace("'", "").replace("_","\\_");

			String whereFilter = "" ;
			//supported functions are : contains, startswith, endswith
			if( method.equals("contains")) // CONTAINS
			{
				whereFilter = " ( entity.member LIKE '%literal%' ESCAPE '\\' ) ".replace("member", member).replace("literal", literal ) ;

			}else if( method.equals("startswith") ) // STARTSWITH
			{
				whereFilter = " ( entity.member LIKE 'literal%' ESCAPE '\\' ) ".replace("member", member).replace("literal", literal ) ;

			}else if( method.equals("endswith") ) //ENDSWITH
			{
				whereFilter = " ( entity.member LIKE '%literal' ESCAPE '\\' ) ".replace("member", member).replace("literal", literal ) ;
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

			// if( member.equals("Attributes") )
			if( member.contains("Attributes") )
			{      
				// the second part should be  : 'any' lambda function 
				UriResource secondPart = uriResourceParts.get(1);
				String typeAttributes = member ;
				if( member.equals("Attributes") )
				{
					// Get the real type if the filter is polymorphic via Attributes
					String attributeType = ((UriResourceNavigation)uriResourceParts.get(0)).getTypeFilterOnEntry().getName();
					// this could be StringAttibutes / IntegerAttributes / DoubleAttributes or DateTimeOfsetAttributes
					// adding 's' in order to be able to join with the right TypeAttribute set 
					typeAttributes = attributeType + "s" ;
				}
				
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
			String literal =  ((Literal)parameters[1]).getText().replace("'", "").replace("_","\\_");

			String whereFilter = "" ;
			//supported functions are : contains, startswith, endswith
			if( method.equals("contains")) // CONTAINS
			{
				whereFilter = " ( entity.member LIKE '%literal%' ESCAPE '\\' ) ".replace("member", member).replace("literal", literal ) ;

			}else if( method.equals("startswith") ) // STARTSWITH
			{
				whereFilter = " ( entity.member LIKE 'literal%' ESCAPE '\\' ) ".replace("member", member).replace("literal", literal ) ;

			}else if( method.equals("endswith") ) //ENDSWITH
			{
				whereFilter = " ( entity.member LIKE '%literal' ESCAPE '\\' ) ".replace("member", member).replace("literal", literal ) ;
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

	public int getEntitySetCount(EdmEntitySet edmEntitySet, FilterOption filterOption) throws ODataApplicationException {

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
			int statusCode = HttpStatusCode.BAD_REQUEST.getStatusCode();
			throw new ODataApplicationException("No Class found for " + entitySetName,statusCode , Locale.ROOT,String.valueOf(statusCode));		
		}
		if( filterOption != null )
		{
			Expression filterExpression = filterOption.getExpression();
			queryString = "SELECT COUNT(entity) FROM " + className + " entity WHERE PLACE_HOLDER";
			queryString = getQuery(filterExpression, "PLACE_HOLDER", queryString);
		}else
		{
			queryString = "SELECT COUNT(entity) FROM " + className + " entity" ;
		}

		LOG.info("Query: "+queryString);
		EntityManager entityManager = this.entityManagerFactory.createEntityManager();
		Query query = entityManager.createQuery(queryString);

		//Get result
		int count = (int) query.getSingleResult(); 

		return count;
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
			int statusCode = HttpStatusCode.BAD_REQUEST.getStatusCode();
			throw new ODataApplicationException("No Class found for " + entitySetName,statusCode , Locale.ROOT,String.valueOf(statusCode));		
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
		queryString = addOrderByOption(entitySetName,queryString, orderByOption);
		//queryString = addJoinOption(entitySetName,queryString);	
		LOG.debug("Query: "+queryString);
		EntityManager entityManager = this.entityManagerFactory.createEntityManager();

		Query query = entityManager.createQuery(queryString);

		//Set the top option
		if (topOption != null)
		{
			int topNumber = topOption.getValue();
			if (topNumber >= 0) {				
				query.setMaxResults(topOption.getValue());
				query.setFirstResult(0);
			} else {
				int statusCode = HttpStatusCode.BAD_REQUEST.getStatusCode();
				throw new ODataApplicationException("Invalid value for $top", statusCode, Locale.ROOT,String.valueOf(statusCode));
			}
		}
		//Set the skipOption
		if (skipOption != null)
		{
			int skipNumber = skipOption.getValue();
			if (skipNumber >= 0) {
				query.setFirstResult(skipNumber);
			} else {
				int statusCode = HttpStatusCode.BAD_REQUEST.getStatusCode();
				throw new ODataApplicationException("Invalid value for $skip", statusCode, Locale.ROOT,String.valueOf(statusCode));
			}
		}

		//Cast to destination class
		switch (entitySetName) {
		case Product.ES_NAME:			
			List<Product> products;
			try {
				LOG.debug("main prod");
				products = query.getResultList();
				LOG.debug("main prod done");
			} finally {
				entityManager.close();
			}						
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

		}
		return query;
	}

	public String addJoinOption(String entitySetName,String query)
	{
		switch (entitySetName) {
		case Product.ES_NAME:
			// By default fetch the checksum 
			query += " INNER JOIN entity.Checksum c1 ";
			break;
		default:
			break;
		}


		return query;
	}


	public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams) {
		String uuid = keyParams.get(0).getText();

		String queryString = "SELECT entity FROM " + Product.class.getName() + " entity WHERE entity.Id =  'uuid'".replace("uuid", uuid) ;

		EntityManager entityManager = this.entityManagerFactory.createEntityManager();

		Query query = entityManager.createQuery(queryString);
		Product product;
		try {
			product = (Product)query.getSingleResult();	
		} finally {
			entityManager.close();
		} 

		return product.getOdataEntity(false);
	}

	//Update Entity
	public void updateEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyPredicates, Entity requestEntity,
			HttpMethod httpMethod) {
		LOG.debug("Starting updateEntityData");
		if (edmEntitySet.getName().equals(Subscription.ES_NAME)) {
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
				entityManager.merge(subscription);
				if (transac.isActive()) {
					transac.commit();
				} else {
					transac.rollback();
				}	
				//Commit the change in database
				AuxipBeanUtil.getBean(StorageStatus.class).modified();
			} finally {                    
				entityManager.close();
			}			
		} else if (edmEntitySet.getName().equals(Product.ES_NAME)) {
			Product prod = new Product();
			prod.setId(UUID.fromString(requestEntity.getProperty("ID").getValue().toString()));
			prod.setContentLength((long) requestEntity.getProperty("ContentLength").getValue());
			prod.setName(requestEntity.getProperty("Name").getValue().toString());
			prod.setContentType(requestEntity.getProperty("ContentType").getValue().toString());
			prod.setOriginDate((Timestamp) requestEntity.getProperty("OriginDate").getValue());
			prod.setPublicationDate((Timestamp) requestEntity.getProperty("PublicationDate").getValue());
			prod.setEvictionDate((Timestamp) requestEntity.getProperty("EvictionDate").getValue());
			LOG.debug(requestEntity.getProperty("Checksum").getValueType().toString());
			List<Checksum> checksums = new ArrayList<>();
			List<?> checksums_in = requestEntity.getProperty("Checksum").asCollection();
			for (Object obj : checksums_in) {
				ComplexValue check_in = (ComplexValue)obj;
				Checksum check = new Checksum();
				for (Property pop : check_in.getValue())
				{
					switch (pop.getName()) {
					case "ChecksumDate":
						check.setChecksumDate((Timestamp) pop.getValue());
						break;
					case "Algorithm":
						check.setAlgorithm(pop.getValue().toString());
						break;
					case "Value":
						check.setValue(pop.getValue().toString());
						break;
					default:
						LOG.debug(pop.getName());
					}
				}
				checksums.add(check);
			}
			prod.setChecksum(checksums);
			TimeRange content_date = new TimeRange();
			ComplexValue tm_in = requestEntity.getProperty("ContentDate").asComplex();
			for (Property pop : tm_in.getValue())
			{
				LOG.debug(pop.getName());
				switch (pop.getName()) {
				case "End":
					content_date.setEnd((Timestamp) pop.getValue());
					break;
				case "Start":
					content_date.setStart((Timestamp) pop.getValue());
					break;
				default:
					break;
				}
			}
			prod.setContentDate(content_date);
			//COmmit to base
			EntityManager entityManager = this.entityManagerFactory.createEntityManager();
			EntityTransaction transac = entityManager.getTransaction();
			transac.begin();
			try {
				entityManager.merge(prod);
				if (transac.isActive()) {
					transac.commit();
				} else {
					transac.rollback();
				}		
				//Commit the change in database
				AuxipBeanUtil.getBean(StorageStatus.class).modified();
			} finally {                    
				entityManager.close();
			}
		}

	}

	//Delete entity
	public void deleteEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
			throws ODataApplicationException {
		
		if (edmEntitySet.getName().equals(Product.ES_NAME)) {
			EntityManager entityManager = this.entityManagerFactory.createEntityManager();
			EntityTransaction transaction = entityManager.getTransaction();

			Product prod = new Product();
			UUID uid = null;
			for (UriParameter p: keyParams)
			{
				LOG.debug(p.getName());
				if (p.getName() == "ID")
				{
					uid = UUID.fromString(p.getText());
				}
			}
			if (uid == null) {
				LOG.debug("No entity given to delete");
				int statusCode = HttpStatusCode.BAD_REQUEST.getStatusCode();
				throw new ODataApplicationException("No entity found to delete", statusCode, Locale.ROOT,String.valueOf(statusCode));
			}
			LOG.debug("Entity to delete : "+uid.toString());
			prod.setId(uid);
			try {
				transaction.begin();

				Object attached = entityManager.merge(prod);
				entityManager.remove(attached);
				if (transaction.isActive()) {
					transaction.commit();
				} else {
					transaction.rollback();
				}
				//Commit the change in database
				AuxipBeanUtil.getBean(StorageStatus.class).modified();
			} catch (PersistenceException e) {
				LOG.error("Could not remove entity: {}", prod);
				int statusCode =  HttpStatusCode.BAD_REQUEST.getStatusCode();
				throw new ODataApplicationException("No entity found to delete",statusCode, Locale.ROOT,String.valueOf(statusCode));
			} finally {                    
				entityManager.close();
			}		

		} else if(edmEntitySet.getName().equals(Subscription.ES_NAME)) {
			EntityManager entityManager = this.entityManagerFactory.createEntityManager();
			EntityTransaction transaction = entityManager.getTransaction();

			Subscription sub = new Subscription();
			UUID uid = null;
			for (UriParameter p: keyParams)
			{
				LOG.debug(p.getName());
				if (p.getName() == "Id")
				{
					uid = UUID.fromString(p.getText());
				}
			}
			if (uid == null) {
				LOG.debug("No entity given to delete");
				int statusCode =  HttpStatusCode.BAD_REQUEST.getStatusCode();
				throw new ODataApplicationException("No entity found to delete",statusCode, Locale.ROOT,String.valueOf(statusCode));
			}
			LOG.debug("Entity to delete : "+uid.toString());
			sub.setId(uid);
			try {
				transaction.begin();

				Object attached = entityManager.merge(sub);
				entityManager.remove(attached);
				if (transaction.isActive()) {
					transaction.commit();
				} else {
					transaction.rollback();
				}
				//Commit the change in database
				AuxipBeanUtil.getBean(StorageStatus.class).modified();
			} catch (PersistenceException e) {
				LOG.error("Could not remove entity: {}", sub);
				int statusCode =  HttpStatusCode.BAD_REQUEST.getStatusCode();
				throw new ODataApplicationException("No entity found to delete",statusCode, Locale.ROOT,String.valueOf(statusCode));
			} finally {                    
				entityManager.close();
			}	
		}
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
		LOG.debug("Starting getRelatedEntityCollection ...");

		EntityCollection navigationTargetEntityCollection = new EntityCollection();

		FullQualifiedName relatedEntityFqn = targetEntityType.getFullQualifiedName();
		String sourceEntityFqn = sourceEntity.getType();				
		if (sourceEntityFqn.equals(Product.FQN.getFullQualifiedNameAsString())
				&& relatedEntityFqn.equals(Attribute.FQN)) {
			String className = StringAttribute.class.getName();
			navigationTargetEntityCollection.setId(createId(sourceEntity, "ID", "Attributes"));
			String queryString1= "SELECT DISTINCT entity FROM com.csgroup.auxip.model.jpa.Product entity "
					+ "JOIN entity.StringAttributes e1 WHERE entity.Id = '"+sourceEntity.getProperty("ID").getValue().toString()+"'";
			String queryString2 = "SELECT DISTINCT entity FROM com.csgroup.auxip.model.jpa.Product entity "
					+ "JOIN entity.IntegerAttributes e2 WHERE entity.Id = '"+sourceEntity.getProperty("ID").getValue().toString()+"'";
			String queryString3 = "SELECT DISTINCT entity FROM com.csgroup.auxip.model.jpa.Product entity "
					+ "JOIN entity.DoubleAttributes p1 WHERE entity.Id = '"+sourceEntity.getProperty("ID").getValue().toString()+"'";
			String queryString4= "SELECT DISTINCT entity FROM com.csgroup.auxip.model.jpa.Product entity "
					+ "JOIN entity.DateTimeOffsetAttributes e4 WHERE  entity.Id = '"+sourceEntity.getProperty("ID").getValue().toString()+"'";
			Map<String, Object> queryParams_m = new HashMap<String,Object>();						
			//queryParams_m.put("productid", UUID.fromString(sourceEntity.getProperty("ID").getValue().toString()));
			EntityManager entityManager = this.entityManagerFactory.createEntityManager();
			try {
				List<Product> strAttribs;
				Query query_1 = entityManager.createQuery(queryString1);
				Query query_2 = entityManager.createQuery(queryString2);
				Query query_3 = entityManager.createQuery(queryString3);
				Query query_4 = entityManager.createQuery(queryString4);			

				strAttribs = query_1.getResultList();
				strAttribs = query_2.getResultList();
				strAttribs = query_3.getResultList();
				strAttribs = query_4.getResultList();
				for (Product s:strAttribs)
				{					
					for (StringAttribute a: s.getStringAttributes()) {
						navigationTargetEntityCollection.getEntities().add(a.getOdataEntity());
					}	
					for (IntegerAttribute i: s.getIntegerAttributes()) {
						navigationTargetEntityCollection.getEntities().add(i.getOdataEntity());
					}
					for (DoubleAttribute d: s.getDoubleAttributes()) {
						navigationTargetEntityCollection.getEntities().add(d.getOdataEntity());
					}
					for (DateTimeOffsetAttribute o: s.getDateTimeOffsetAttributes()) {
						navigationTargetEntityCollection.getEntities().add(o.getOdataEntity());
					}
				}				
			} catch (Exception e) {
				LOG.debug("Exception : "+e.getLocalizedMessage());
			}finally {                    
				entityManager.close();
			}			
		}

		if (navigationTargetEntityCollection.getEntities().isEmpty()) {
			int statusCode =  HttpStatusCode.BAD_REQUEST.getStatusCode();
			throw new ODataApplicationException("No related entity found",statusCode, Locale.ROOT,String.valueOf(statusCode));
			//return null;
		}
		LOG.debug("getRelatedEntityCollection Done : "+String.valueOf(navigationTargetEntityCollection.getEntities().size()));
		return navigationTargetEntityCollection;
	}

	public Entity createMediaEntity(EdmEntityType entityType, String contentTypeString, byte[] mediaContent) throws ODataApplicationException {
		Entity entity = null;
		LOG.debug(contentTypeString);
		LOG.debug(entityType.getName());
		if(entityType.getName().equals(Product.ET_NAME)) {
			Product prod = new Product();
			if (contentTypeString.equals("application/json"))
			{
				ObjectMapper mapper = new ObjectMapper();
				try {
					JsonNode actualObj = mapper.readTree(new String(mediaContent));
					prod.setId(UUID.fromString(actualObj.get("ID").asText()));
					prod.setContentLength(actualObj.get("ContentLength").asLong());
					prod.setName(actualObj.get("Name").asText());
					prod.setContentType(actualObj.get("ContentType").asText());
					prod.setOriginDate(convertFromISOString(actualObj.get("OriginDate").asText()));
					prod.setPublicationDate(convertFromISOString(actualObj.get("PublicationDate").asText()));
					prod.setEvictionDate(convertFromISOString(actualObj.get("EvictionDate").asText()));
					JsonNode checks_node = actualObj.get("Checksum");
					List<Checksum> checks_list = new ArrayList<>();
					if (checks_node.isArray()) {
						for (JsonNode check : checks_node) {
							Checksum ch = new Checksum();
							ch.setAlgorithm(check.get("Algorithm").asText());
							ch.setValue(check.get("Value").asText());
							ch.setChecksumDate(convertFromISOString(check.get("ChecksumDate").asText()));
							checks_list.add(ch);
						}
					}
					prod.setChecksum(checks_list);
					TimeRange content_date = new TimeRange();
					content_date.setStart(convertFromISOString(actualObj.get("ContentDate").get("Start").asText()));
					content_date.setEnd(convertFromISOString(actualObj.get("ContentDate").get("End").asText()));
					prod.setContentDate(content_date);
					JsonNode attribs_node = actualObj.get("Attributes");
					List<StringAttribute> strAttrib_list = new ArrayList<>();
					List<IntegerAttribute> intAttrib_list = new ArrayList<>();
					List<DoubleAttribute> doubleAttrib_list = new ArrayList<>();
					List<DateTimeOffsetAttribute> datetimeAttrib_list = new ArrayList<>();
					if (attribs_node.isArray()) {
						for (JsonNode attrib : attribs_node) {
							String type = attrib.get("ValueType").asText();
							switch (type) {
							case "String":
								StringAttribute att = new StringAttribute();
								att.setName(attrib.get("Name").asText());
								att.setValueType(attrib.get("ValueType").asText());
								att.setValue(attrib.get("Value").asText());
								strAttrib_list.add(att);
								break;
							case "Integer":
								IntegerAttribute atti = new IntegerAttribute();
								atti.setName(attrib.get("Name").asText());
								atti.setValueType(attrib.get("ValueType").asText());
								atti.setValue(attrib.get("Value").asLong());
								intAttrib_list.add(atti);
								break;
							case "Double":
								DoubleAttribute attd = new DoubleAttribute();
								attd.setName(attrib.get("Name").asText());
								attd.setValueType(attrib.get("ValueType").asText());
								attd.setValue(attrib.get("Value").asDouble());
								doubleAttrib_list.add(attd);
								break;
							case "DateTimeOffset":
								DateTimeOffsetAttribute attt = new DateTimeOffsetAttribute();
								attt.setName(attrib.get("Name").asText());
								attt.setValueType(attrib.get("ValueType").asText());
								attt.setValue(convertFromISOString(attrib.get("Value").asText()));
								datetimeAttrib_list.add(attt);
								break;
							default:
								break;
							}
						}
					}
					prod.setDoubleAttributes(doubleAttrib_list);
					prod.setIntegerAttributes(intAttrib_list);
					prod.setDateTimeOffsetAttributes(datetimeAttrib_list);
					prod.setStringAttributes(strAttrib_list);
					LOG.debug("Id: "+actualObj.get("ID").asText());
				} catch (JsonProcessingException e) {
					int statusCode = HttpStatusCode.BAD_REQUEST.getStatusCode() ;
					throw new ODataApplicationException("Can't parse body as JSON for product POST",statusCode ,Locale.ENGLISH,String.valueOf(statusCode));
							
				}
			} else {

				int statusCode = HttpStatusCode.BAD_REQUEST.getStatusCode() ;
				throw new ODataApplicationException("Bad contenttype for Product POST request",statusCode ,Locale.ENGLISH,String.valueOf(statusCode));

			}
			entity = prod.getOdataEntity(false);
			LOG.debug("Entity : "+entity.getType());
			EntityManager entityManager = this.entityManagerFactory.createEntityManager();
			EntityTransaction transac = entityManager.getTransaction();			
			transac.begin();
			try {
				entityManager.persist(prod);
				if (transac.isActive()) {
					transac.commit();
					//Commit the change in database
					AuxipBeanUtil.getBean(StorageStatus.class).modified();
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

	public EntityCollection getAttributes(String uuid, String attributesType )
	{

		LOG.debug("Starting getAttributes ..." );

		EntityCollection attributes = new EntityCollection();		

		String queryString = "SELECT DISTINCT entity FROM com.csgroup.auxip.model.jpa.Product entity WHERE entity.Id = 'uuid'";
		queryString = queryString.replace("uuid",uuid) ; 

		EntityManager entityManager = this.entityManagerFactory.createEntityManager();

		try {
			Query query = entityManager.createQuery(queryString);
			Product product = (Product)query.getSingleResult();
			// get attributes lazily by invoking the right get method
			String methodTobeInvoked = "get" + attributesType ;
			java.lang.reflect.Method getAttributesMethod = product.getClass().getMethod(methodTobeInvoked);
			List<Attribute> attributesList = (List<Attribute>)getAttributesMethod.invoke(product);

			// add all attributes to the entity collection
			for( Attribute att : attributesList)
			{
				attributes.getEntities().add( att.getOdataEntity() );
			}

		}catch (Exception e) {
				LOG.debug("Exception : "+e.getLocalizedMessage());
		}finally {                    
			entityManager.close();
		}			
		
		LOG.debug("getAttributes Done : "+String.valueOf(attributes.getEntities().size()));
		return attributes;		
	}
	private Timestamp convertFromISOString(final String str) {
		ZonedDateTime dt = ZonedDateTime.parse(str);
		return Timestamp.from(dt.toInstant());
	}



}
