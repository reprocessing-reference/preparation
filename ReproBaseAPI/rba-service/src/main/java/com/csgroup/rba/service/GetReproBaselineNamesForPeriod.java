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
import com.csgroup.jpadatasource.JPADataSource;
import com.csgroup.jpadatasource.query.JPAQuery;
import com.csgroup.rba.model.AuxFile;
import com.google.common.collect.Lists;

import static com.sdl.odata.api.processor.query.QueryResult.from;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author besquis
 */
@EdmFunction(name = "GetReproBaselineNamesForPeriodUnBound", namespace = "OData.RBA", isBound = false)
@EdmReturnType(type = "Collection(Edm.String)")
public class GetReproBaselineNamesForPeriod implements Operation<List<String>> {
	private static final Logger LOG = LoggerFactory.getLogger(GetReproBaselineNamesForPeriod.class);
	
	@EdmParameter
    private String Mission;

	@EdmParameter
    private String Unit;

    @EdmParameter
    private ZonedDateTime SensingTimeStart;
    
    @EdmParameter
    private ZonedDateTime SensingTimeStop;
    
    @EdmParameter
    private String ProductType;
    
    @Override
    public List<String> doOperation(ODataRequestContext requestContext,
                              DataSourceFactory dataSourceFactory) throws ODataDataSourceException {    	
    	JPADataSource dataSource = (JPADataSource) dataSourceFactory.getDataSource(requestContext, "OData.RBA.AuxFile");
    	boolean hasMission = true; 
    	boolean hasUnit = true;
    	boolean hasSensingStart = true;
    	boolean hasSensingStop = true;
    	boolean hasProductType = true;
    	
    	if (Mission == null)
    	{
    		hasMission = false;
    	}
    	if (Unit == null)
    	{
    		hasUnit = false;
    	}
    	if (SensingTimeStart == null)
    	{
    		hasSensingStart = false;    		
    	}
    	if (SensingTimeStop == null)
    	{
    		hasSensingStop = false;    		
    	}
    	if (ProductType == null)
    	{
    		hasProductType = false;
    	}
    	Map<String, Object> queryParams = new HashMap<String,Object>();
    	EntityDataModel entityDataModel = requestContext.getEntityDataModel();
    	boolean firstWhere = true;
    	String query_string ="SELECT DISTINCT e1 FROM com.csgroup.rba.model.jpa.AuxFileJPA e1 "
    			+ "JOIN e1.AuxType e2 "
    			+ "JOIN e2.ProductTypes e3 ";    			
    	if (hasSensingStart && hasSensingStop)
    	{
    		if (firstWhere) {
    			query_string = query_string.concat("WHERE ");
    			firstWhere = false;
    		}
    		query_string = query_string.concat("e1.SensingTimeApplicationStart < :e1SensingTimeApplicationStop "
    				+ "AND e1.SensingTimeApplicationStop > :e1SensingTimeApplicationStart ");
    		queryParams.put("e1SensingTimeApplicationStart",SensingTimeStart );    	
        	queryParams.put("e1SensingTimeApplicationStop",SensingTimeStop );
    	}
    	else if (hasSensingStart)
    	{
    		if (firstWhere) {
    			query_string = query_string.concat("WHERE ");
    			firstWhere = false;
    		}
    		query_string = query_string.concat("e1.SensingTimeApplicationStart <= :e1SensingTimeApplicationStart "
    				+ "AND e1.SensingTimeApplicationStop > :e1SensingTimeApplicationStart ");
    		queryParams.put("e1SensingTimeApplicationStart",SensingTimeStart );
    	}
    	if (hasUnit)
    	{
    		if (firstWhere) {
    			query_string = query_string.concat("WHERE ");
    			firstWhere = false;
    		} else {
    			query_string = query_string.concat("AND ");
    		}
    		query_string = query_string.concat("e1.Unit = :e1Unit ");
    		queryParams.put("e1Unit",Unit );			
    		
    	}
    	if (hasMission)
    	{
    		if (firstWhere) {
    			query_string = query_string.concat("WHERE ");
    			firstWhere = false;
    		} else {
    			query_string = query_string.concat("AND ");
    		}
    		query_string = query_string.concat("e2.Mission = :e2Mission ");
    		queryParams.put("e2Mission",Mission );	    		
    	}
    	if (hasProductType)
    	{
    		if (firstWhere) {
    			query_string = query_string.concat("WHERE ");
    			firstWhere = false;
    		} else {
    			query_string = query_string.concat("AND ");
    		}
    		query_string = query_string.concat("e3.Type = :e3Type ");
    		queryParams.put("e3Type",ProductType);	    		
    	}
    	
    	
		JPAQuery query = new JPAQuery(query_string, queryParams);
		List<Object> result = dataSource.executeQueryListResult(query);
        LOG.info("Found: {} items for query: {}", result.size(), query);
        
        if (hasUnit) {
        	Map<String, Object> queryAllParams = new HashMap<String,Object>();
        	boolean firstWhereAll = true;
        	String query_all_string ="SELECT DISTINCT e1 FROM com.csgroup.rba.model.jpa.AuxFileJPA e1 "
        			+ "JOIN e1.AuxType e2 "
        			+ "JOIN e2.ProductTypes e3 ";    			
        	if (hasSensingStart && hasSensingStop)
        	{
        		if (firstWhereAll) {
        			query_all_string = query_all_string.concat("WHERE ");
        			firstWhereAll = false;
        		}
        		query_all_string = query_all_string.concat("e1.SensingTimeApplicationStart < :e1SensingTimeApplicationStop "
        				+ "AND e1.SensingTimeApplicationStop > :e1SensingTimeApplicationStart ");
        		queryAllParams.put("e1SensingTimeApplicationStart",SensingTimeStart );    	
        		queryAllParams.put("e1SensingTimeApplicationStop",SensingTimeStop );
        	}
        	if (hasUnit)
        	{
        		if (firstWhereAll) {
        			query_all_string = query_all_string.concat("WHERE ");
        			firstWhereAll = false;
        		} else {
        			query_all_string = query_all_string.concat("AND ");
        		}
        		query_all_string = query_all_string.concat("e1.Unit = :e1Unit ");
        		queryAllParams.put("e1Unit","X" );			
        		
        	}
        	if (hasMission)
        	{
        		if (firstWhereAll) {
        			query_all_string = query_all_string.concat("WHERE ");
        			firstWhereAll = false;
        		} else {
        			query_all_string = query_all_string.concat("AND ");
        		}
        		query_all_string = query_all_string.concat("e2.Mission = :e2Mission ");
        		queryAllParams.put("e2Mission",Mission );	    		
        	}
        	if (hasProductType)
        	{
        		if (firstWhereAll) {
        			query_all_string = query_all_string.concat("WHERE ");
        			firstWhereAll = false;
        		} else {
        			query_all_string = query_all_string.concat("AND ");
        		}
        		query_all_string = query_all_string.concat("e3.Type = :e3Type ");
        		queryAllParams.put("e3Type",ProductType);	    		
        	}
			JPAQuery query_all = new JPAQuery(query_all_string, queryAllParams);
			List<Object> result_all = dataSource.executeQueryListResult(query_all);
	        LOG.info("Found: {} items for query: {}", result_all.size(), query_all);
	        //Concatenate both results
	        result.addAll(result_all);        
        }
        
        //Convert and sort
        QueryResult q_result = from(dataSource.convert(entityDataModel, "OData.RBA.AuxFile", result));
        List<Object> obj_result = (List<Object>)q_result.getData();
        List<AuxFile> aux_result = Lists.newArrayList();
        for (Object obj : obj_result)
        {
        	if (obj instanceof AuxFile)
        	{
        		aux_result.add((AuxFile)obj);
        	}
        }
       
		Stream<AuxFile> tmp_aux = aux_result.stream().distinct();
		return tmp_aux.map(w -> w.getFullName()).collect(Collectors.toList());
    	
    }    
}