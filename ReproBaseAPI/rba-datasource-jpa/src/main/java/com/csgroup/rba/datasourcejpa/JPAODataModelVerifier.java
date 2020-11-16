/**
 * Copyright (c) 2016 All Rights Reserved by the CS Group.
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
package com.csgroup.rba.datasourcejpa;

import com.csgroup.rba.datasourcejpa.mapper.JPAPropertyVisitor;
import com.csgroup.rba.model.annotations.ODataJPAEntity;
import static com.csgroup.rba.datasourcejpa.util.JPAMetadataUtil.getJPAEntityClass;
import static com.csgroup.rba.datasourcejpa.util.ReflectionUtil.getField;
import static com.csgroup.rba.datasourcejpa.util.ReflectionUtil.newInstance;
import static com.csgroup.rba.datasourcejpa.util.ReflectionUtil.writeField;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.registry.ODataEdmRegistry;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static com.sdl.odata.util.AnnotationsUtil.getAnnotation;
import static com.sdl.odata.util.edm.EntityDataModelUtil.createPropertyCollection;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getPropertyType;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getPropertyValue;
import static com.sdl.odata.util.edm.EntityDataModelUtil.isStructuredType;
import static com.sdl.odata.util.edm.EntityDataModelUtil.visitProperties;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

/**
 * @author besquis
 * 
 * Verify that the JPA model can be matched to the OData model
 */
@Component
public class JPAODataModelVerifier {
	private static final Logger LOG = LoggerFactory.getLogger(JPAODataModelVerifier.class);

	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Autowired
    private ODataEdmRegistry edmRegistry;

	public void verifyODataEntityClasses(List<Class<?>> odataEntities) throws ODataException,ODataDataSourceException {

		List<Class<?>> jpaEntities = discoverEntities();


		LOG.info("Verifying JPA entities for {} Odata entities", odataEntities.size());
		for (Class<?> odataEntity : odataEntities) {
			if (odataEntity.isAnnotationPresent(EdmEntity.class))
			{
				LOG.info("Verifying JPA for Odata Entity: {}", odataEntity.getName());            
				ODataJPAEntity jpaEntityAnno = getAnnotation(odataEntity, ODataJPAEntity.class);
				getJPAEntityClass(odataEntity);
				odataVerifyJPA(odataEntity, edmRegistry.getEntityDataModel(), new HashMap<>());
				LOG.info("Odata entity {} Ok", odataEntity.getName());	
			}
		}
	}

	private void odataVerifyJPA(final Class<?> odataClass, final EntityDataModel entityDataModel, final Map<Class<?>, Class<?>> visitedEntities)
					throws ODataDataSourceException {
		// If we already have entity in map, then it is a cyclic link, just return stored entity
		if (visitedEntities.containsKey(odataClass)) {
			return;
		}

		ODataJPAEntity jpaEntityAnno = getAnnotation(odataClass, ODataJPAEntity.class);

		final Class<?> targetClass = getJPAEntityClass(odataClass);
		LOG.debug("Mapping OData entity to JPA: {} => {}", odataClass.getName(), targetClass);

		// Create new instance of JPA entity
		final Object jpaEntity = newInstance(targetClass);

		// Put entity to map of already visited
		visitedEntities.put(odataClass, targetClass);

		StructuredType structType = (StructuredType) entityDataModel.getType(odataClass);

		// verify field values from OData entity to JPA entity
		visitProperties(entityDataModel, structType, new JPAPropertyVisitor() {
		@Override
			public void visit(StructuralProperty property, String jpaFieldName) throws ODataDataSourceException {
				 //If the property is of a structured type, then map value(s) recursively
				 if (isStructuredType(getPropertyType(entityDataModel, property))) {
					LOG.debug("is Structured "+jpaFieldName+ " "+property.getName());
					odataVerifyJPA(getPropertyType(entityDataModel, property).getJavaType(), entityDataModel, visitedEntities);				
				}
			}
		});		
	}

	private List<Class<?>> discoverEntities() {
		Map<String, Class<?>> foundEntities = new HashMap<>();

		Metamodel metamodel = entityManagerFactory.getMetamodel();
		for (EntityType t : metamodel.getEntities()) {
			LOG.debug("We have a JPA Entity type: {}", t.getBindableJavaType().getCanonicalName());

			Class<?> entityType = t.getJavaType();
			for (Field f : entityType.getDeclaredFields())
			{
				LOG.debug("Found jpa attribute :" + f.toString());	
			}
			

			foundEntities.put(entityType.getName(), entityType);
		}

		return new ArrayList<>(foundEntities.values());
	}

}
