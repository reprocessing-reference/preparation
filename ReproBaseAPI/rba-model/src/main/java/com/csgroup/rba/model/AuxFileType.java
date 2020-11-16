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
package com.csgroup.rba.model;

import java.util.List;

import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.csgroup.rba.model.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmNavigationProperty;
import com.sdl.odata.api.edm.annotations.EdmProperty;

/**
 * @author besquis
 */
@EdmEntity(namespace = "OData.RBA", key = "LongName", containerName = "Container")
@EdmEntitySet("AuxFileTypes")
@ODataJPAEntity(value = "com.csgroup.rba.model.jpa.AuxFileTypeJPA")
public class AuxFileType {

	@ODataJPAProperty
    @EdmProperty(name = "LongName", nullable = false)
    private String LongName;
    
	@ODataJPAProperty
    @EdmProperty(name = "ShortName", nullable = false)
    private String ShortName;
    
	@ODataJPAProperty
    @EdmProperty(name = "Format", nullable = false)
    private String Format;
    
	@ODataJPAProperty
    @EdmProperty(name = "Origin", nullable = false)
    private String Origin;
    
	@ODataJPAProperty
	@EdmNavigationProperty(name = "ProductLevelApplicability", nullable = false)
    private List<ProductLevel> ProductLevelApplicability;
    
	@ODataJPAProperty
    @EdmProperty(name = "Variability", nullable = false)
    private Variability Variability;
    
	@ODataJPAProperty
    @EdmProperty(name = "Rule", precision = 3, nullable = false)
    private Rule Rule;
    
	@ODataJPAProperty
    @EdmProperty(name = "Description", nullable = false)
    private String Description;
   	
	public AuxFileType() {
		// TODO Auto-generated constructor stub
	}

	public String getLongName() {
		return LongName;
	}

	public void setLongName(String longName) {
		LongName = longName;
	}

	public String getShortName() {
		return ShortName;
	}

	public void setShortName(String shortName) {
		ShortName = shortName;
	}

	public String getFormat() {
		return Format;
	}

	public void setFormat(String format) {
		Format = format;
	}
	
	public String getOrigin() {
		return Origin;
	}

	public void setOrigin(String origin) {
		Origin = origin;
	}

	public List<ProductLevel> getProductLevelApplicability() {
		return ProductLevelApplicability;
	}

	public void setProductLevelApplicability(List<ProductLevel> productLevelApplicability) {
		ProductLevelApplicability = productLevelApplicability;
	}

	public Variability getVariability() {
		return Variability;
	}

	public void setVariability(Variability variability) {
		Variability = variability;
	}

	public Rule getRule() {
		return Rule;
	}

	public void setRule(Rule rule) {
		Rule = rule;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}
	
	
   
}
