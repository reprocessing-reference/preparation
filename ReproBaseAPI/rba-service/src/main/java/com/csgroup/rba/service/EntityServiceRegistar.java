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
import com.csgroup.rba.datasourcejpa.JPAODataModelVerifier;
import com.csgroup.rba.model.AuxFile;
import com.csgroup.rba.model.AuxFileType;
import com.csgroup.rba.model.Band;
import com.csgroup.rba.model.Baseline;
import com.csgroup.rba.model.Checksum;
import com.csgroup.rba.model.ProductLevel;
import com.csgroup.rba.model.Rule;
import com.csgroup.rba.model.Sensor;
import com.csgroup.rba.model.TimeRange;
import com.csgroup.rba.model.Variability;
import com.csgroup.rba.model.jpa.ProductLevelJPA;
import com.google.common.collect.Lists;


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
    	add(AuxFileType.class);
    	add(AuxFile.class);
    	add(Baseline.class);
        add(Sensor.class);
        add(Variability.class);
        add(Rule.class);
        add(Checksum.class);
        add(TimeRange.class);
        add(ProductLevel.class);
        add(Band.class);
    }};
    
    
    @PostConstruct
    public void registerEntities() throws ODataException {
        LOG.debug("Registering entities");

        oDataEdmRegistry.registerClasses(ENTITIES);        
        
        verifier.verifyODataEntityClasses(ENTITIES);
        
    }
}
