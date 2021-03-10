/**
 * Copyright (c) 2016 All Rights Reserved by the SDL Group.
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
package com.csgroup.jpadatasource;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.mapper.EntityMapper;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.DataSourceProvider;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.query.QueryOperation;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.processor.query.strategy.QueryOperationStrategy;
import com.sdl.odata.api.service.ODataRequestContext;
import com.csgroup.jpadatasource.query.JPAQuery;
import com.csgroup.jpadatasource.query.JPAQueryStrategyBuilder;
import com.csgroup.jpadatasource.annotations.ODataJPAEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sdl.odata.api.processor.query.QueryResult.from;

/**
 * @author Renze de Vries
 */
@Component
public class JPADatasourceProvider implements DataSourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(JPADatasourceProvider.class);

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private JPADataSource jpaDataSource;

    @Autowired
    private EntityMapper<Object, Object> entityMapper;

    /**
     * Check if the given JPA entity class is a valid entity type.
     *
     * @param jpaEntityClass The given JPA entity class.
     * @return {@code true} if it is valid, {@code false} otherwise.
     */
    private boolean isValidEntityType(String jpaEntityClass) {

        Set<EntityType<?>> entityTypes = entityManagerFactory.getMetamodel().getEntities();
        // Check if there is one JPA entity type for which the JPA entity class is the same
        for (EntityType entityType : entityTypes) {
            if (entityType.getJavaType().getName().equals(jpaEntityClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSuitableFor(ODataRequestContext requestContext, String entityType)
            throws ODataDataSourceException {
        Class<?> odataType = requestContext.getEntityDataModel().getType(entityType).getJavaType();
        LOG.debug(String.format("Looking for jpa equivalent to %s", odataType.getName()));
        ODataJPAEntity jpaAnnotation = odataType.getAnnotation(ODataJPAEntity.class);        
        if (jpaAnnotation != null) {
            String jpaType = jpaAnnotation.value();
            return isValidEntityType(jpaType);
        }
        LOG.debug("JPA equivalent not found");
        return false;
    }

    @Override
    public DataSource getDataSource(ODataRequestContext requestContext) {
        return jpaDataSource;
    }
    
    @Override
    public QueryOperationStrategy getStrategy(ODataRequestContext requestContext, QueryOperation operation,
                                              TargetType expectedODataEntityType) throws ODataException {
        EntityDataModel entityDataModel = requestContext.getEntityDataModel();
        JPAQueryStrategyBuilder builder = new JPAQueryStrategyBuilder(entityDataModel); 
        final JPAQuery query = builder.build(operation, requestContext);
        boolean is_value_query = requestContext.getRequest().getUri().endsWith("/$value");
        
        LOG.debug("JPA Query: {}", query);
        
        return () -> {

            List<Object> result = jpaDataSource.executeQueryListResult(query);
            LOG.info("Found: {} items for query: {}", result.size(), query);
            long count = 0;
            if (is_value_query) {
            	return QueryResult.from(new String("www.google.fr"));
            }
            
            if (builder.isCount()) {
                count = result.size();                
                if (builder.isCount() && !builder.includeCount()) {
                	return QueryResult.from(count);
                }
            }
            QueryResult query_res = from(jpaDataSource.convert(entityDataModel, expectedODataEntityType.typeName(), result));
            if (builder.isCount() && builder.includeCount()) {
            	query_res = query_res.withCount(count);
            }
            return query_res; 
        };
    }

    

   
}
