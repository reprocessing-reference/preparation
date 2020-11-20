/**
 * Copyright (c) 2015 SDL Group
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
package com.csgroup.auxip.datasource.attributes;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.util.EdmUtil;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.DataSourceProvider;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.query.QueryOperation;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.processor.query.strategy.QueryOperationStrategy;
import com.sdl.odata.api.service.ODataRequestContext;
import com.csgroup.auxip.Attribute;
import com.csgroup.auxip.Product;
import com.csgroup.auxip.datasource.attributes.AttributesDataSource;
import com.csgroup.auxip.datasource.attributes.AttributesStrategyBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * This is an example data source provide that uses in memory structures to demonstrate how to provide
 * entities, storing and querying capabilities to the OData v4 framework.
 */
@Component
public class AttributesDataSourceProvider implements DataSourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AttributesDataSourceProvider.class);

    @Autowired
    private AttributesDataSource inDataSource;

    @Override
    public boolean isSuitableFor(ODataRequestContext oDataRequestContext, String entityType) throws ODataDataSourceException {
        return oDataRequestContext.getEntityDataModel().getType(entityType).getJavaType().isAssignableFrom(Attribute.class);
    }

    @Override
    public DataSource getDataSource(ODataRequestContext oDataRequestContext) {
        return inDataSource;
    }

    @Override
    public QueryOperationStrategy getStrategy(ODataRequestContext oDataRequestContext, QueryOperation queryOperation, TargetType targetType) throws ODataException {
        AttributesStrategyBuilder builder = new AttributesStrategyBuilder();
        List<Predicate<Attribute>> predicateList = builder.buildCriteria(queryOperation, oDataRequestContext);

        int limit = builder.getLimit();
        int skip = builder.getSkip();
        List<String> propertyNames = builder.getPropertyNames();

        return () -> {
            LOG.debug("Executing query against in memory data");
            Stream<Attribute> attribStream = inDataSource.getAttributeConcurrentMap().values().stream();

            List<Attribute> filteredAttributes = attribStream.filter(p -> predicateList.stream()
                    .allMatch(f -> f.test(p))).collect(Collectors.toList());

            long count = 0;
            if (builder.isCount() || builder.includeCount()) {
                count = filteredAttributes.size();
                LOG.debug("Counted {} persons matching query", count);

                if (builder.isCount()) {
                    return QueryResult.from(count);
                }
            }

            if (skip != 0 || limit != Integer.MAX_VALUE) {
            	filteredAttributes = filteredAttributes.stream().skip(skip).limit(limit).collect(Collectors.toList());
            }

            LOG.debug("Found {} attributes matching query", filteredAttributes.size());

            if (propertyNames != null && !propertyNames.isEmpty()) {
                try {
                    LOG.debug("Selecting {} properties of attributes", propertyNames);
                    return QueryResult.from(EdmUtil.getEdmPropertyValue(filteredAttributes.get(0), propertyNames.get(0)));
                } catch (IllegalAccessException e) {
                    LOG.error(e.getMessage(), e);
                    return QueryResult.from(Collections.emptyList());
                }
            }

            QueryResult result = QueryResult.from(filteredAttributes);
            if (builder.includeCount()) {
                result = result.withCount(count);
            }
            return result;
        };
    }
}
