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

import com.csgroup.jpadatasource.query.JPAQuery;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.mapper.EntityMapper;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.datasource.TransactionalDataSource;
import com.sdl.odata.api.processor.link.ODataLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import scala.Option;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import static com.sdl.odata.api.parser.ODataUriUtil.extractEntityWithKeys;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * The default JPA datasource, this datasource by default will create a transaction per operation.
 *
 * @author Renze de Vries
 */
@Component
@Primary
public class JPADataSource implements DataSource {
    private static final Logger LOG = LoggerFactory.getLogger(JPADataSource.class);

    @Autowired
    private EntityMapper<Object, Object> entityMapper;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private ODataProxyProcessor proxyProcessor;
    
    @Autowired
    private StorageStatus status;

    @Override
    public Object create(ODataUri uri, Object entity, EntityDataModel entityDataModel) throws ODataException {
        Object jpaEntity = entityMapper.convertODataEntityToDS(entity, entityDataModel);
        EntityManager entityManager = getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            LOG.info("Persisting entity: {}", jpaEntity);
            entityManager.persist(jpaEntity);
            
            if (transaction.isActive()) {
                transaction.commit();
            } else {
                transaction.rollback();
            }
            status.modified();
            return entityMapper.convertDSEntityToOData(jpaEntity, entity.getClass(), entityDataModel);
        } finally {            
            entityManager.close();
        }
        
    }

    @Override
    public Object update(ODataUri uri, Object entity, EntityDataModel entityDataModel) throws ODataException {        
        Object jpaEntity = entityMapper.convertODataEntityToDS(entity, entityDataModel);
        if (jpaEntity != null) {
            EntityManager entityManager = getEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();

                Object attached = entityManager.merge(jpaEntity);                
                if (transaction.isActive()) {
                    transaction.commit();
                } else {
                    transaction.rollback();
                }
                Object unproxied = proxyProcessor.process(attached); 
                Class<?> javaType = entityDataModel.getType(entity.getClass()).getJavaType();
                status.modified();
                return entityMapper.convertDSEntityToOData(unproxied, javaType, entityDataModel);
            } catch (PersistenceException e) {
                LOG.error("Could not update entity: {}", entity);
                throw new ODataDataSourceException("Could not update entity", e);
            } finally {                    
                entityManager.close();
            }
        } else {
        	LOG.error("Could not update entity: {}", entity);
            throw new ODataDataSourceException("Could not update entity "+ entity);
        }
		
    }

    @Override
    public void delete(ODataUri uri, EntityDataModel entityDataModel) throws ODataException {
        Option<Object> entity = extractEntityWithKeys(uri, entityDataModel);

        if (entity.isDefined()) {
            Object jpaEntity = entityMapper.convertODataEntityToDS(entity.get(), entityDataModel);
            if (jpaEntity != null) {
                EntityManager entityManager = getEntityManager();
                EntityTransaction transaction = entityManager.getTransaction();
                try {
                    transaction.begin();

                    Object attached = entityManager.merge(jpaEntity);
                    entityManager.remove(attached);
                    if (transaction.isActive()) {
                        transaction.commit();
                    } else {
                        transaction.rollback();
                    }
                } catch (PersistenceException e) {
                    LOG.error("Could not remove entity: {}", entity);
                    throw new ODataDataSourceException("Could not remove entity", e);
                } finally {                    
                    entityManager.close();
                }
            } else {
                throw new ODataDataSourceException("Could not remove entity, could not be loaded");
            }
            status.modified();
        }
    }

    @Override
    public void createLink(ODataUri uri, ODataLink link, EntityDataModel entityDataModel) throws ODataException {

    }

    @Override
    public void deleteLink(ODataUri uri, ODataLink link, EntityDataModel entityDataModel) throws ODataException {

    }
    
    public <T> List<T> executeQueryListResult(JPAQuery jpaQuery) {
        EntityManager em = entityManagerFactory.createEntityManager();

        String queryString = jpaQuery.getQueryString();
        LOG.debug("Query string : "+queryString);
        Query query = em.createQuery(queryString);
        int nrOfResults = jpaQuery.getLimitCount();
        int startPosition = jpaQuery.getSkipCount();
        Map<String, Object> queryParams = jpaQuery.getQueryParams();

        try {
            if (nrOfResults > 0) {
                query.setMaxResults(nrOfResults);
            }

            if (startPosition > 0) {
                query.setFirstResult(startPosition);
            }

            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                query.setParameter(entry.getKey(), tryConvert(entry.getValue()));
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }    
    
    public List<?> convert(EntityDataModel entityDataModel, String expectedType, List<?> jpaEntities) {
        Class<?> javaType = entityDataModel.getType(expectedType).getJavaType();

        return jpaEntities.stream().map(j -> {
            try {
                Object unproxied = proxyProcessor.process(j);
                return entityMapper.convertDSEntityToOData(unproxied, javaType, entityDataModel);
            } catch (ODataDataSourceException e) {
                LOG.error("Could not convert entity", e);
                return null;
            }
        }).collect(Collectors.toList());
    }

    private Object tryConvert(Object parameterType) {
        if (parameterType instanceof scala.math.BigDecimal) {
            return ((scala.math.BigDecimal) parameterType).intValue();
        }

        return parameterType;
    }
    

    protected EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    @Override
    public TransactionalDataSource startTransaction() {
        return applicationContext.getBean(JPATransactionDataSource.class);
    }

    protected EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
    
    

}
