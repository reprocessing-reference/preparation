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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author besquis
 */
@EdmFunction(name = "GetReproBaselineListForPeriodUnBound", namespace = "OData.RBA", isBound = false)
@EdmReturnType(type = "OData.RBA.AuxFile")
public class GetReproBaselineForPeriod implements Operation<List<AuxFile>> {
	private static final Logger LOG = LoggerFactory.getLogger(GetReproBaselineForPeriod.class);
	
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
    public List<AuxFile> doOperation(ODataRequestContext requestContext,
                              DataSourceFactory dataSourceFactory) throws ODataDataSourceException {    	
    	JPADataSource dataSource = (JPADataSource) dataSourceFactory.getDataSource(requestContext, "OData.RBA.AuxFile");
    	boolean hasMission = true; 
    	boolean isS3SRAL = false;
    	boolean isS3 = false;
    	boolean hasUnit = true;
    	boolean hasSensingStart = true;
    	boolean hasSensingStop = true;
    	boolean hasProductType = true;
    	
    	if (Mission == null)
    	{
    		hasMission = false;    		
    	} else {
    		if (Mission.equals("S3SRAL"))
    		{
    			isS3SRAL = true;
    		}
    		if (Mission.startsWith("S3") && !Mission.equals("S3ALL"))
    		{
    			isS3 = true;
    		}
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
    	String query_string = getQuery(hasMission, Mission, hasUnit,Unit,hasSensingStart, hasSensingStop, hasProductType,
				queryParams);
        
    	
    	JPAQuery query = new JPAQuery(query_string, queryParams);
		List<Object> result = dataSource.executeQueryListResult(query);
        LOG.info("Found: {} items for query: {}", result.size(), query);
        
        if (isS3SRAL) {
        	Map<String, Object> queryS3MWRParams = new HashMap<String,Object>();
        	String query_S3MWR_string = getQuery(true, "S3MWR" , hasUnit, Unit,hasSensingStart, hasSensingStop, hasProductType,
        			queryS3MWRParams);        	
			JPAQuery query_S3MWR = new JPAQuery(query_S3MWR_string, queryS3MWRParams);
			List<Object> result_S3MWR = dataSource.executeQueryListResult(query_S3MWR);
	        LOG.info("Found: {} items for query: {}", result_S3MWR.size(), query_S3MWR);
	        //Concatenate both results
	        result.addAll(result_S3MWR);        
        }
        if (isS3) {
        	Map<String, Object> queryS3ALLParams = new HashMap<String,Object>();
        	String query_S3ALL_string = getQuery(true, "S3ALL" , hasUnit, Unit,hasSensingStart, hasSensingStop, hasProductType,
        			queryS3ALLParams);        	
			JPAQuery query_S3ALL = new JPAQuery(query_S3ALL_string, queryS3ALLParams);
			List<Object> result_S3ALL = dataSource.executeQueryListResult(query_S3ALL);
	        LOG.info("Found: {} items for query: {}", result_S3ALL.size(), query_S3ALL);
	        //Concatenate both results
	        result.addAll(result_S3ALL);        
        }

        
        if (hasUnit) {
        	Map<String, Object> queryAllParams = new HashMap<String,Object>();
        	String query_all_string = getQuery(hasMission, Mission, true, "X",hasSensingStart, hasSensingStop, hasProductType,
        			queryAllParams);        	
			JPAQuery query_all = new JPAQuery(query_all_string, queryAllParams);
			List<Object> result_all = dataSource.executeQueryListResult(query_all);
	        LOG.info("Found: {} items for query: {}", result_all.size(), query_all);
	        //Concatenate both results
	        result.addAll(result_all);
	        if (isS3SRAL) {
	        	Map<String, Object> queryS3MWRXParams = new HashMap<String,Object>();
	        	String query_S3MWRX_string = getQuery(true, "S3MWR" , true, "X",hasSensingStart, hasSensingStop, hasProductType,
	        			queryS3MWRXParams);        	
				JPAQuery query_S3MWRX = new JPAQuery(query_S3MWRX_string, queryS3MWRXParams);
				List<Object> result_S3MWRX = dataSource.executeQueryListResult(query_S3MWRX);
		        LOG.info("Found: {} items for query: {}", result_S3MWRX.size(), query_S3MWRX);
		        //Concatenate both results
		        result.addAll(result_S3MWRX);        
	        }
	        if (isS3) {
	        	Map<String, Object> queryS3ALLXParams = new HashMap<String,Object>();
	        	String query_S3ALLX_string = getQuery(true, "S3ALL" , true, "X",hasSensingStart, hasSensingStop, hasProductType,
	        			queryS3ALLXParams);        	
				JPAQuery query_S3ALLX = new JPAQuery(query_S3ALLX_string, queryS3ALLXParams);
				List<Object> result_S3ALLX = dataSource.executeQueryListResult(query_S3ALLX);
		        LOG.info("Found: {} items for query: {}", result_S3ALLX.size(), query_S3ALLX);
		        //Concatenate both results
		        result.addAll(result_S3ALLX);        
	        }
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
       
		return aux_result.stream()
			     .distinct()
			     .collect(Collectors.toList());
    	
    }

	private String getQuery(boolean hasMission, String mission, boolean hasUnit, String unit, boolean hasSensingStart, boolean hasSensingStop,
			boolean hasProductType, Map<String, Object> queryParams) {
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
    		queryParams.put("e1Unit",unit );			
    		
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
    		queryParams.put("e2Mission",mission );	    		
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
		return query_string;
	}    
}