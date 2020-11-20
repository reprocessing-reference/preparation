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
package com.csgroup.auxip.service;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.registry.ODataEdmRegistry;
import com.csgroup.auxip.Attribute;
import com.csgroup.auxip.Checksum;
import com.csgroup.auxip.DateTimeOffsetAttribute;
import com.csgroup.auxip.DoubleAttribute;
import com.csgroup.auxip.EnumType;
import com.csgroup.auxip.IntegerAttribute;
import com.csgroup.auxip.Metric;
//import com.sdl.odata.datasource.jpa.JPADataSource;
import com.csgroup.auxip.Product;
import com.csgroup.auxip.Property;
import com.csgroup.auxip.StringAttribute;
import com.csgroup.auxip.Subscription;
import com.csgroup.auxip.SubscriptionEvent;
import com.csgroup.auxip.SubscriptionStatus;
import com.csgroup.auxip.TimeRange;
import com.csgroup.auxip.datasource.product.ProductDataSource;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author rdevries
 */
@Component
public class EntityServiceRegistar {
    private static final Logger LOG = LoggerFactory.getLogger(EntityServiceRegistar.class);

    @Autowired
    private ODataEdmRegistry oDataEdmRegistry;   
    
    @Autowired
    private ProductDataSource inDataSource;

    @PostConstruct
    public void registerEntities() throws ODataException {
        LOG.debug("Registering entities");

        oDataEdmRegistry.registerClasses(Lists.newArrayList(
                Product.class,
                DateTimeOffsetAttribute.class,
                Subscription.class,
                Attribute.class,
                StringAttribute.class,
                IntegerAttribute.class,
                DoubleAttribute.class,
                Metric.class,
                SubscriptionStatus.class,
                SubscriptionEvent.class,
                Checksum.class,
                EnumType.class,
                Property.class,
                TimeRange.class
        ));
        
        Checksum check = new Checksum("algo", "value", ZonedDateTime.now());
        TimeRange time_range = new TimeRange(ZonedDateTime.now(),ZonedDateTime.now());
        StringAttribute str_attrib = new StringAttribute("name", "valueType", "value");
        DoubleAttribute db_attrib = new DoubleAttribute("name", "valueType", 1.0);
        List<Checksum> checks = Lists.newArrayList(check);
        List<Attribute> attribs = Lists.newArrayList(str_attrib);
        List<StringAttribute> strattribs = Lists.newArrayList(str_attrib);
        List<DoubleAttribute> dbattribs = Lists.newArrayList(db_attrib);
        
        Product test_product = new Product(UUID.randomUUID(), "dudule", "application/raw",125582 , ZonedDateTime.now(),
        		ZonedDateTime.now(), ZonedDateTime.now(), checks,time_range);
        //test_product.setAttributes(attribs);
        //test_product.setStringAttributes(strattribs);
        test_product.setDoubleAttributes(dbattribs);
        
        inDataSource.create(null, test_product, null);
    }
}
