/**
 * Copyright (c) 2015 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.csgroup.rba.service;

import com.sdl.odata.api.edm.annotations.EdmFunction;
import com.sdl.odata.api.edm.annotations.EdmParameter;
import com.sdl.odata.api.edm.annotations.EdmReturnType;
import com.sdl.odata.api.edm.model.Operation;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.service.ODataRequestContext;
import com.csgroup.rba.datasourcejpa.JPADataSource;
import com.csgroup.rba.model.AuxFile;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author besquis
 */
@EdmFunction(name = "GetReproBaselineUnbound", namespace = "OData.RBA", entitySetPath = "Units", isBound = true)
@EdmReturnType(type = "Edm.String")
public class GetReproBaselineUnbound implements Operation<List<AuxFile>> {
	private static final Logger LOG = LoggerFactory.getLogger(GetReproBaselineUnbound.class);

	@EdmParameter
    private String Unit;

    @EdmParameter
    private ZonedDateTime SensingTime;
    
    @EdmParameter
    private String ProductType;

    @Override
    public List<AuxFile> doOperation(ODataRequestContext requestContext,
                              DataSourceFactory dataSourceFactory) throws ODataDataSourceException {
    	JPADataSource dataSource = (JPADataSource) dataSourceFactory.getDataSource(requestContext, "OData.RBA.AuxFiles");        
        return null;

    }
}