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

import java.util.List;
import java.util.Locale;

import com.csgroup.auxip.model.jpa.User;
import com.csgroup.auxip.model.repository.Storage;
import com.csgroup.auxip.model.security.AccessControl;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
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
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.keycloak.TokenVerifier;
import org.keycloak.representations.AccessToken;


public class AuxipEntityCollectionProcessor implements EntityCollectionProcessor {

  private OData odata;
  private ServiceMetadata srvMetadata;
  // our database-mock
  private Storage storage;

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

  
    // Check the client access role 
    
    if ( !AccessControl.userCanDealWith(request, uriInfo) )
    {
      throw new ODataApplicationException("Unauthorized Request !",
      HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ROOT);
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
      throw new ODataApplicationException("Only EntitySet is supported",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
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

    if (segmentCount == 1) 
    { 
      // this is the case for: odata/Products  or odata/Subscriptions
      responseEdmEntitySet = startEdmEntitySet; // the response body is built from the first (and only) entitySet
      // apply system query options
      
      responseEntityCollection = storage.readEntitySetData(startEdmEntitySet,filterOption,expandOption,orderByOption);

    } else { // this would be the case for e.g. Products(uuid)/Attributes
      throw new ODataApplicationException("Not supported",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    // 3rd: apply System Query Options
    // modify the result set according to the query options, specified by the end user
    List<Entity> entityList = responseEntityCollection.getEntities();
    EntityCollection returnEntityCollection = new EntityCollection();

    // handle $count: return the original number of entities, ignore $top and $skip
    
    if (countOption != null) {
        boolean isCount = countOption.getValue();
        if(isCount){
            returnEntityCollection.setCount(entityList.size());
        }
    }

    // handle $skip
    if (skipOption != null) {
        int skipNumber = skipOption.getValue();
        if (skipNumber >= 0) {
            if(skipNumber <= entityList.size()) {
                entityList = entityList.subList(skipNumber, entityList.size());
            } else {
                // The client skipped all entities
                entityList.clear();
            }
        } else {
            throw new ODataApplicationException("Invalid value for $skip", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
        }
    }

    // handle $top
    if (topOption != null) {
        int topNumber = topOption.getValue();
        if (topNumber >= 0) {
            if(topNumber <= entityList.size()) {
                entityList = entityList.subList(0, topNumber);
            }  // else the client has requested more entities than available => return what we have
        } else {
            throw new ODataApplicationException("Invalid value for $top", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
        }
    }

    // after applying the query options, create EntityCollection based on the reduced list
    for(Entity entity : entityList){
        returnEntityCollection.getEntities().add(entity);
    }

    // 4th: serialize
    EdmEntityType edmEntityType = responseEdmEntitySet.getEntityType();
    // we need the property names of the $select, in order to build the context URL
    String selectList = odata.createUriHelper().buildContextURLSelectList(edmEntityType, expandOption, selectOption);
    ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).selectList(selectList).build();

    // adding the selectOption to the serializerOpts will actually tell the lib to do the job
    final String id = request.getRawBaseUri() + "/" + responseEdmEntitySet.getName();
    EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
        .contextURL(contextUrl)
        .expand(expandOption)
        .select(selectOption)
        .id(id)
        .count(countOption)
        .build();

    ODataSerializer serializer = odata.createSerializer(responseFormat);
    SerializerResult serializerResult = serializer.entityCollection(srvMetadata, edmEntityType, returnEntityCollection, opts);

    // 5th: configure the response object: set the body, headers and status code
    response.setContent(serializerResult.getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());

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
