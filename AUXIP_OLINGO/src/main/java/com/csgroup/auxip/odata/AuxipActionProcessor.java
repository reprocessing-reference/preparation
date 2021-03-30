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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.csgroup.auxip.model.jpa.Globals;
import com.csgroup.auxip.model.jpa.Subscription;
import com.csgroup.auxip.model.repository.Storage;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Builder;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.prefer.Preferences.Return;
import org.apache.olingo.server.api.prefer.PreferencesApplied;
import org.apache.olingo.server.api.processor.ActionEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionEntityProcessor;
import org.apache.olingo.server.api.processor.ActionPrimitiveProcessor;
import org.apache.olingo.server.api.processor.ActionVoidProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;


public class AuxipActionProcessor implements ActionVoidProcessor, ActionEntityCollectionProcessor, ActionEntityProcessor {

  private OData odata;
  private Storage storage;
  private ServiceMetadata serviceMetadata;
  
  public AuxipActionProcessor(final Storage storage) {
    this.storage = storage;
  }

  @Override
  public void init(final OData odata, final ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }



  @Override
  public void processActionVoid(ODataRequest request, ODataResponse response, UriInfo uriInfo,
      ContentType requestFormat) throws ODataApplicationException, ODataLibraryException {


        // the first UriResource part should be Subscriptions
        UriResource uriResource = uriInfo.asUriInfoResource().getUriResourceParts().get(0);
        // Check if this action is associated to the right EntitySet = Subscriptions 
        if( uriResource.getSegmentValue().equals(Subscription.ES_NAME) )
        {
          // Get the UriResourceEntitySet
          final UriResourceEntitySet subscriptionSet = (UriResourceEntitySet)uriResource;
          //Get the Id of subscription to be upadted  ( only Id is passed as prédicate)
          final String uuid = subscriptionSet.getKeyPredicates().get(0).getText(); 
          // Get the action to be executed ( The second part of Uri resource part
          final String actionName = ((UriResourceAction) uriInfo.asUriInfoResource().getUriResourceParts()
                                                                                .get(1)).getAction().getName();
          
          if( storage.subscriptionAction(actionName,uuid).equals(Globals.OK) )
          {
            response.setStatusCode(HttpStatusCode.OK.getStatusCode());
          }else{
            response.setStatusCode(HttpStatusCode.NOT_MODIFIED.getStatusCode());
          }

        }
       
  }




  @Override
  public void processActionEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
      ContentType requestFormat, ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    // TODO Auto-generated method stub
  }
  

  @Override
  public void processActionEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo,
      ContentType requestFormat, ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    // TODO Auto-generated method stub
    
  }

}
