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
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.Operation;
import com.sdl.odata.api.mapper.EntityMapper;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.csgroup.rba.datasourcejpa.JPADataSource;
import com.csgroup.rba.datasourcejpa.ODataProxyProcessor;
import com.csgroup.rba.datasourcejpa.query.JPAQuery;
import com.csgroup.rba.model.AuxFile;

import static com.sdl.odata.api.processor.query.QueryResult.from;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author besquis
 */
@EdmFunction(name = "GetReproBaselineListForSensingBound", namespace = "OData.RBA", entitySetPath = "AuxFiles", isBound = true)
@EdmReturnType(type = "OData.RBA.AuxFile")
public class GetReproBaselineForSensing implements Operation<List<AuxFile>> {
	private static final Logger LOG = LoggerFactory.getLogger(GetReproBaselineForSensing.class);
	
	@EdmParameter
    private String Mission;

	@EdmParameter
    private String Unit;

    @EdmParameter
    private ZonedDateTime SensingTime;
    
    @EdmParameter
    private String ProductType;
    
    @Autowired
    private EntityMapper<Object, Object> entityMapper;
    
    @Override
    public List<AuxFile> doOperation(ODataRequestContext requestContext,
                              DataSourceFactory dataSourceFactory) throws ODataDataSourceException {    	
    	JPADataSource dataSource = (JPADataSource) dataSourceFactory.getDataSource(requestContext, "OData.RBA.AuxFile");
    	
    	
    	if (Mission == null)
    	{
    		throw new ODataDataSourceException("Mission not filled in function");
    	}
    	if (Unit == null)
    	{
    		throw new ODataDataSourceException("Unit not filled in function");
    	}
    	if (SensingTime == null)
    	{
    		throw new ODataDataSourceException("SensingTime not filled in function");
    	}
    	if (ProductType == null)
    	{
    		throw new ODataDataSourceException("ProductType not filled in function");
    	}
    	
    	EntityDataModel entityDataModel = requestContext.getEntityDataModel();
    	
    	String query_string ="SELECT DISTINCT e1 FROM com.csgroup.rba.model.jpa.AuxFileJPA e1 "
    			+ "JOIN e1.AuxType e2 "
    			+ "JOIN FETCH e2.ProductTypes "    			
    			+ "JOIN FETCH e2.ProductLevels "
    			+ "WHERE e1.SensingTimeApplicationStart < :e1SensingTimeApplicationStart "    	
    			+ "AND e1.SensingTimeApplicationStop > :e1SensingTimeApplicationStart "    			
    			+ "AND e2.Unit = :e2Unit ";
    	Map<String, Object> queryParams = new HashMap<String,Object>();
    	queryParams.put("e1Name",new String("S2MSI") );
    	queryParams.put("e2SensingTimeApplicationStart",SensingTime );    	
    	queryParams.put("e3Unit",Unit );
		JPAQuery query = new JPAQuery(query_string, queryParams);
		List<Object> result = dataSource.executeQueryListResult(query);
        LOG.info("Found: {} items for query: {}", result.size(), query);

        QueryResult q_result = from(dataSource.convert(entityDataModel, "OData.RBA.AuxFile", result));
        LOG.info("Levels :"+((List<AuxFile>) q_result.getData()).get(0).getAuxType().getProductTypes());
		return (List<AuxFile>) q_result.getData();
    	
    }    
}