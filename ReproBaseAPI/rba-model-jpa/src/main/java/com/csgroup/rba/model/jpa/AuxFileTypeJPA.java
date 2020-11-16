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
package com.csgroup.rba.model.jpa;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;


/**
 * @author besquis
 */
@Entity(name = "AuxFileTypes")
public class AuxFileTypeJPA {

    @Id
    private String LongName;
    
    private String ShortName;
    
    private String Format;
    
    private String Origin;
    
    @ManyToMany
    private List<ProductLevelJPA> ProductLevelApplicability;
    
    @Enumerated(EnumType.STRING)
    private VariabilityJPA Variability;
    
    @Enumerated(EnumType.STRING)
    private RuleJPA Rule;
    
    private String Description;
   	
	public AuxFileTypeJPA() {
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

	public List<ProductLevelJPA> getProductLevelApplicability() {
		return ProductLevelApplicability;
	}

	public void setProductLevelApplicability(List<ProductLevelJPA> productLevelApplicability) {
		ProductLevelApplicability = productLevelApplicability;
	}

	public VariabilityJPA getVariability() {
		return Variability;
	}

	public void setVariability(VariabilityJPA variability) {
		Variability = variability;
	}

	public RuleJPA getRule() {
		return Rule;
	}

	public void setRule(RuleJPA rule) {
		Rule = rule;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}
	
	
   
}
