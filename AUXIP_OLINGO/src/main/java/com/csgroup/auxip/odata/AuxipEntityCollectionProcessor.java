/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.csgroup.auxip.odata;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import com.csgroup.auxip.config.ODATAConfiguration;
import com.csgroup.auxip.model.jpa.User;
import com.csgroup.auxip.model.repository.Storage;
import org.apache.olingo.commons.api.Constants;
import com.csgroup.auxip.model.security.AccessControl;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.core.uri.queryoption.TopOptionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class AuxipEntityCollectionProcessor implements EntityCollectionProcessor {
	
  private static final int MAX_RESULTS = 200;
	
  private static final Logger LOG = LoggerFactory.getLogger(AuxipEntityCollectionProcessor.class);

  private OData odata;
  private ServiceMetadata srvMetadata;
  // our database-mock
  private Storage storage;
  @Autowired
  private ODATAConfiguration configuration;

  public AuxipEntityCollectionProcessor(Storage storage) {
    this.storage = storage;
  }

  public void init(OData odata, ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.srvMetadata = serviceMetadata;
  }

  /*
   * This method is invoked when a collection of entities has to be read. ( Products / Subscriptions) 
   */
  public void readEntityCollection(ODataRequest request, ODataResponse response,
      UriInfo uriInfo, ContentType responseFormat)
      throws ODataApplicationException, SerializerException {

	LOG.info("Starting readEntityCollection");
	
	int max_results = MAX_RESULTS;
	if( this.configuration == null) {
		LOG.warn("OData configuration not found, using default values");
	} else {
		max_results = this.configuration.getMaxResults();
	}
 
    // Check the client access role 
    if ( !AccessControl.userCanDealWith(request, uriInfo) )
    {
      int statusCode = HttpStatusCode.UNAUTHORIZED.getStatusCode();
      throw new ODataApplicationException("Unauthorized Request !",statusCode, Locale.ROOT,String.valueOf(statusCode));
    }
    EdmEntitySet responseEdmEntitySet = null; // we'll need this to build the ContextURL
    EntityCollection responseEntityCollection = null; // we'll need this to set the response body
    EdmEntityType responseEdmEntityType = null;

    // 1st retrieve the requested EntitySet from the uriInfo (representation of the parsed URI)
    List<UriResource> resourceParts = uriInfo.getUriResourceParts();
    int segmentCount = resourceParts.size();

    UriResource uriResource = resourceParts.get(0); 
    if (!(uriResource instanceof UriResourceEntitySet)) 
    {
      int statusCode = HttpStatusCode.NOT_IMPLEMENTED.getStatusCode();
      throw new ODataApplicationException("Only EntitySet is supported",statusCode, Locale.ROOT,String.valueOf(statusCode));
    }

    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
    EdmEntitySet startEdmEntitySet = uriResourceEntitySet.getEntitySet();

    SelectOption selectOption = uriInfo.getSelectOption();
    ExpandOption expandOption = uriInfo.getExpandOption();
    FilterOption filterOption = uriInfo.getFilterOption();
    OrderByOption orderByOption = uriInfo.getOrderByOption();

    // “The $count system query option ignores any $top, $skip, or $expand query options, 
    // and returns the total count of results across all pages including only those results 
    // matching any specified $filter and $search.”
    CountOption countOption = uriInfo.getCountOption();
    
    // “Where $top and $skip are used together, $skip MUST be applied before $top, 
    // regardless of the order in which they appear in the request.”
    SkipOption skipOption = uriInfo.getSkipOption();
    TopOption topOption = uriInfo.getTopOption();
    
    if (topOption == null)
    {
    	topOption = new TopOptionImpl().setValue(max_results);
    } else {
    	if (topOption.getValue() > max_results)
    	{
    		topOption = new TopOptionImpl().setValue(max_results);
    	}
    }

    if (segmentCount == 1) 
    { 
      // this is the case for: odata/Products  or odata/Subscriptions
      responseEdmEntitySet = startEdmEntitySet; // the response body is built from the first (and only) entitySet
      // apply system query options
      
      responseEntityCollection = storage.readEntitySetData(startEdmEntitySet,filterOption,expandOption,orderByOption,
    		  skipOption, topOption);

    } else { 
            
        // Products(uuid)/Attributes ,Products(uuid)/StringAttributes , ...
        // get the second part of the uri resource
        UriResource attributesUriResource = resourceParts.get(1);

        UriResourceNavigation uriResourceNavigation =  (UriResourceNavigation)(attributesUriResource);
        String attributesType = uriResourceNavigation.getProperty().getName();
        String firstKeyPredicate = uriResourceEntitySet.getKeyPredicates().get(0).getName() ; 
        String productUuid = uriResourceEntitySet.getKeyPredicates().get(0).getText() ; 
        if ( !firstKeyPredicate.equals("Id")) 
        {
          int statusCode = HttpStatusCode.BAD_REQUEST.getStatusCode();
          throw new ODataApplicationException("Bad request => a valid uuid is needed ! ", statusCode, Locale.ROOT,String.valueOf(statusCode));
        }
        // Set the response EdmEntitySet from the related target of NavigationPropertyBinding 
        EdmBindingTarget edmBindingTarget = startEdmEntitySet.getRelatedBindingTarget(attributesType);
        responseEdmEntitySet = (EdmEntitySet)edmBindingTarget;
        
        responseEntityCollection = storage.getAttributes(productUuid, attributesType);
    }    

    // 3rd: apply System Query Options
    // modify the result set according to the query options, specified by the end user    
    // in our example: http://localhost:8080/DemoService/DemoService.svc/Categories/$expand=Products
    // or http://localhost:8080/DemoService/DemoService.svc/Products?$expand=Category
    if (expandOption != null) {
      // retrieve the EdmNavigationProperty from the expand expression
      // Note: in our example, we have only one NavigationProperty, so we can directly access it
      EdmNavigationProperty edmNavigationProperty = null;
      ExpandItem expandItem = expandOption.getExpandItems().get(0);
      if(expandItem.isStar()) {
        List<EdmNavigationPropertyBinding> bindings = responseEdmEntitySet.getNavigationPropertyBindings();
        // we know that there are navigation bindings
        // however normally in this case a check if navigation bindings exists is done
        if(!bindings.isEmpty()) {
          // can in our case only be 'Category' or 'Products', so we can take the first
          EdmNavigationPropertyBinding binding = bindings.get(0);
          EdmElement property = responseEdmEntitySet.getEntityType().getProperty(binding.getPath());
          // we don't need to handle error cases, as it is done in the Olingo library
          if(property instanceof EdmNavigationProperty) {
            edmNavigationProperty = (EdmNavigationProperty) property;
          }
        }
      } else {
        // can be 'Category' or 'Products', no path supported
        UriResource uriResource_expand = expandItem.getResourcePath().getUriResourceParts().get(0);
        // we don't need to handle error cases, as it is done in the Olingo library
        if(uriResource_expand instanceof UriResourceNavigation) {
          edmNavigationProperty = ((UriResourceNavigation) uriResource_expand).getProperty();
        }
      }

      // can be 'Category' or 'Products', no path supported
      // we don't need to handle error cases, as it is done in the Olingo library
      if(edmNavigationProperty != null) {
        String navPropName = edmNavigationProperty.getName();
        EdmEntityType expandEdmEntityType = edmNavigationProperty.getType();

        List<Entity> entityList = responseEntityCollection.getEntities();
        for (Entity entity : entityList) {
          Link link = new Link();
          link.setTitle(navPropName);
          link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
          link.setRel(Constants.NS_ASSOCIATION_LINK_REL + navPropName);

          if (edmNavigationProperty.isCollection()) { // in case of Categories/$expand=Products
            // fetch the data for the $expand (to-many navigation) from backend
        	EntityCollection expandEntityCollection = storage.getRelatedEntityCollection(entity, expandEdmEntityType);
            if (expandEntityCollection != null) {
            	link.setInlineEntitySet(expandEntityCollection);
            	link.setHref(expandEntityCollection.getId().toASCIIString());
            }
          } else { // in case of Products?$expand=Category
            // fetch the data for the $expand (to-one navigation) from backend
            // here we get the data for the expand
        	Entity expandEntity = storage.getRelatedEntity(entity, expandEdmEntityType);
            if (expandEntity != null) {
            	link.setInlineEntity(expandEntity);
            	link.setHref(expandEntity.getId().toASCIIString());
            }
          }

          // set the link - containing the expanded data - to the current entity
          entity.getNavigationLinks().add(link);
        }
      }
    }

    //Add the count option
    if(countOption != null && countOption.getValue())
    {
    	responseEntityCollection.setCount(storage.getEntitySetCount(startEdmEntitySet,filterOption));
    	LOG.info(String.valueOf(responseEntityCollection.getCount()));
    }
    LOG.info(String.valueOf(responseEntityCollection.getCount()));
    
    // 4th: serialize
    EdmEntityType edmEntityType = responseEdmEntitySet.getEntityType();
    // we need the property names of the $select, in order to build the context URL
    String selectList = odata.createUriHelper().buildContextURLSelectList(edmEntityType, expandOption, selectOption);
    ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).selectList(selectList).build();
    LOG.info("Building response");
    // adding the selectOption to the serializerOpts will actually tell the lib to do the job
    final String id = request.getRawBaseUri() + "/" + responseEdmEntitySet.getName();
    EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
        .contextURL(contextUrl)
        .expand(expandOption)
        .select(selectOption)
        .id(id)
        .count(countOption)
        .build();
    LOG.info("Serializing "+edmEntityType.toString());
    ODataSerializer serializer = odata.createSerializer(responseFormat);
    SerializerResult serializerResult = serializer.entityCollection(srvMetadata, edmEntityType, responseEntityCollection, opts);    
    // 5th: configure the response object: set the body, headers and status code
    response.setContent(serializerResult.getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    LOG.info("Done");
  }

  private boolean isContNav(UriInfo uriInfo) {
    List<UriResource> resourceParts = uriInfo.getUriResourceParts();
    for (UriResource resourcePart : resourceParts) {
      if (resourcePart instanceof UriResourceNavigation) {
        UriResourceNavigation navResource = (UriResourceNavigation) resourcePart;
        if (navResource.getProperty().containsTarget()) {
          return true;
        }
      }
    }
    return false;
  }

}
