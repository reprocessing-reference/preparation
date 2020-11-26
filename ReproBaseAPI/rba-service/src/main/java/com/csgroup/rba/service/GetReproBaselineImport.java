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
import com.sdl.odata.api.edm.annotations.EdmFunctionImport;
import com.sdl.odata.api.edm.annotations.EdmParameter;
import com.sdl.odata.api.edm.annotations.EdmReturnType;
import com.sdl.odata.api.edm.model.Operation;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.service.ODataRequestContext;

import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author besquis
 */
@EdmFunctionImport(function = "GetReproBaselineUnbound", includeInServiceDocument = true,
name = "GetReproBaseline", namespace = "OData.RBA")
public class GetReproBaselineImport {
	private static final Logger LOG = LoggerFactory.getLogger(GetReproBaselineImport.class);

    @EdmParameter
    private String Unit;

    @EdmParameter
    private ZonedDateTime SensingTime;
    
    @EdmParameter
    private String ProductType;

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public ZonedDateTime getSensingTime() {
		return SensingTime;
	}

	public void setSensingTime(ZonedDateTime sensingTime) {
		SensingTime = sensingTime;
	}

	public String getProductType() {
		return ProductType;
	}

	public void setProductType(String productType) {
		ProductType = productType;
	}

       
}