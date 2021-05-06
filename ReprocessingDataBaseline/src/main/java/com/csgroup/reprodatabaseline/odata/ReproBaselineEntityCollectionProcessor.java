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
package com.csgroup.reprodatabaseline.odata;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
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
import org.apache.olingo.server.api.uri.UriResourceFunction;

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
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReproBaselineEntityCollectionProcessor implements EntityCollectionProcessor {
	
  private static final Logger LOG = LoggerFactory.getLogger(ReproBaselineEntityCollectionProcessor.class);

  private OData odata;
  private ServiceMetadata srvMetadata;
  // our database-mock
  // private Storage storage;

  // public ReproBaselineEntityCollectionProcessor(Storage storage) {
  //   this.storage = storage;
  // }

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
 
    // Check the client access role 
    // if ( !AccessControl.userCanDealWith(request, uriInfo) )
    // {
    //   throw new ODataApplicationException("Unauthorized Request !",
    //   HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ROOT);
    // }


    EdmEntitySet responseEdmEntitySet = null; // we'll need this to build the ContextURL
    EntityCollection responseEntityCollection = null; // we'll need this to set the response body
    EdmEntityType responseEdmEntityType = null;

    // 1st retrieve the requested EntitySet from the uriInfo (representation of the parsed URI)
    List<UriResource> resourceParts = uriInfo.getUriResourceParts();
    int segmentCount = resourceParts.size();

    UriResource uriResource = resourceParts.get(0); 
    if (!(uriResource instanceof UriResourceFunction)) 
    {
      int statusCode= HttpStatusCode.NOT_IMPLEMENTED.getStatusCode();
      throw new ODataApplicationException("Only function call is supported, see => /$metadata",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT,String.valueOf(statusCode));
    }
    final UriResourceFunction uriResourceFunction = (UriResourceFunction) uriResource;
    final EntityCollection entityCol = getReprocessingDataBaseline((UriResourceFunction)uriResourceFunction);

        // 2nd step: Serialize the response entity
    final EdmEntityType edmEntityType = (EdmEntityType) uriResourceFunction.getFunction().getReturnType().getType();
    final ContextURL contextURL = ContextURL.with().asCollection().type(edmEntityType).build();
    EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().contextURL(contextURL).build();
    final ODataSerializer serializer = odata.createSerializer(responseFormat);
    final SerializerResult serializerResult = serializer.entityCollection(srvMetadata, edmEntityType, entityCol, opts);

    // 3rd configure the response object
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

  public EntityCollection getReprocessingDataBaseline(final UriResourceFunction uriResourceFunction) throws ODataApplicationException {

    LOG.info("Starting >> getReprocessingDataBaseline");

    if("getReprocessingDataBaseline".equals(uriResourceFunction.getFunctionImport().getName())) {
      // Get the parameter of the function
      final UriParameter satelliteUnit = uriResourceFunction.getParameters().get(0);
      final UriParameter productType = uriResourceFunction.getParameters().get(1);
      final UriParameter dataTakeId = uriResourceFunction.getParameters().get(2);
  
      System.out.println("satelliteUnit =>" + satelliteUnit.getText());
      System.out.println("productType =>" + productType.getText());
      System.out.println("dataTakeId =>" + dataTakeId.getText());

      // get aux data files listing from the reprocessing configuration baseline
      //getAuxDataFiles();
      // request="https://reprocessing-preparation.ml/reprocessing.svc/GetReproBaselineNamesForPeriod(Mission='%s',Unit='%s',SensingTimeStart='%s',SensingTimeStop='%s')" % (mission,unit,start,stop)
      // apply rules selection 
      //applyRules();
      // get links from auxip service
      //getCloudlinks();
      // Try to convert the parameter to an Integer.
      // We have to take care, that the type of parameter fits to its EDM declaration
      // int amount;
      // try {
      //   amount = Integer.parseInt(parameterAmount.getText());
      // } catch(NumberFormatException e) {
      //   throw new ODataApplicationException("Type of parameter Amount must be Edm.Int32",
      //     HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
      // }
  
      // final EdmEntityType productEntityType = serviceMetadata.getEdm().getEntityType(DemoEdmProvider.ET_PRODUCT_FQN);
      final List<Entity> resultEntityList = new ArrayList<Entity>();
  
      // // Loop over all categories and check how many products are linked
      // for(final Entity category : categoryList) {
      //   final EntityCollection products = getRelatedEntityCollection(category, productEntityType);
      //   if(products.getEntities().size() == amount) {
      //     resultEntityList.add(category);
      //   }
      // }
  
      final EntityCollection resultCollection = new EntityCollection();
      resultCollection.getEntities().addAll(resultEntityList);

      LOG.info("Ending << getReprocessingDataBaseline");
      return resultCollection;
     
    } else {

        throw new ODataApplicationException("Function not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
      Locale.ROOT);
    }



  }
  



}
