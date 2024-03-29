/**
 * Copyright (c) 2020 CS Group
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

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.registry.ODataEdmRegistry;
import com.csgroup.jpadatasource.JPAODataModelVerifier;
import com.csgroup.rba.model.AuxFile;
import com.csgroup.rba.model.AuxType;
import com.csgroup.rba.model.ProductLevel;
import com.csgroup.rba.model.ProductType;
import com.csgroup.rba.model.Rule;
import com.csgroup.rba.model.TimeValidity;
import com.csgroup.rba.model.Variability;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

/**
 * @author besquis
 */
@Component
public class EntityServiceRegistar {
    private static final Logger LOG = LoggerFactory.getLogger(EntityServiceRegistar.class);
    
    @Autowired
    private ODataEdmRegistry oDataEdmRegistry;
    
    @Autowired
    private JPAODataModelVerifier verifier;
    

    private static final List<Class<?>> ENTITIES = new ArrayList<Class<?>>() { {
    	add(AuxType.class);
    	add(AuxFile.class);
    	add(Variability.class);
        add(Rule.class);
        add(ProductLevel.class);
        add(ProductType.class);
        add(TimeValidity.class);        
        add(GetReproBaselineForPeriod.class);
        add(GetReproBaselineForPeriodImport.class);
        add(GetReproBaselineNamesForPeriod.class);
        add(GetReproBaselineNamesForPeriodImport.class);        
    }};
    
    
    @PostConstruct
    public void registerEntities() throws ODataException {
        LOG.debug("Registering entities");

        oDataEdmRegistry.registerClasses(ENTITIES);        
        
        verifier.verifyODataEntityClasses(ENTITIES);
        
    }
}
