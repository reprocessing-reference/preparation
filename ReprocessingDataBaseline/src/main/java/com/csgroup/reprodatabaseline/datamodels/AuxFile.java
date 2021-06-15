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
package com.csgroup.reprodatabaseline.datamodels;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

/**
 * @author besquis
 */
public class AuxFile implements Comparable<AuxFile> {
	private static final Logger LOG = LoggerFactory.getLogger(AuxFile.class);
	
    public UUID Id;    
    public String AuxType;
    public String FullName;
    //public String Baseline;
    //public String IpfVersion;
    //public String ICID;
    public ZonedDateTime ValidityStart;
    public ZonedDateTime ValidityStop;
    //public ZonedDateTime SensingTimeApplicationStart;
    //public ZonedDateTime SensingTimeApplicationStop;
    public ZonedDateTime CreationDate;
    public String Band;
    public String Unit;
	public String AuxipUrl = null;


	public Entity getOdataEntity() {
		Entity entity = new Entity();

		Property name = new Property("String", "Name",ValueType.PRIMITIVE,this.FullName) ;
		Property auxipUrl = new Property("String", "AuxipLink",ValueType.PRIMITIVE,this.AuxipUrl) ;

		entity.addProperty( name );
		entity.addProperty( auxipUrl );
		entity.setType("OData.CSC.Product");

		return entity;
	}

	public static List<AuxFile> loadValues(final AuxType type, final String json_str) {
		List<AuxFile> res = new ArrayList<AuxFile>();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			JsonNode actualObj = mapper.readTree(json_str.replaceAll("@", ""));
			JsonNode valueNode = actualObj.get("value");
			if (valueNode.isArray()) {
				for (JsonNode value : valueNode) {
					AuxFile aux = new AuxFile();
					/*
					 * {"@odata.id":"AuxFiles(0001572e-cac4-4003-8757-b42962b35e9b)",
					 * "Id":"0001572e-cac4-4003-8757-b42962b35e9b",
					 * "AuxType":{"@odata.type":"#OData.RBA.AuxType",
					 * "LongName":"AUX_POEORB_S1","ShortName":"AUX_POE",
					 * "Format":"EOF","Mission":"S1SAR","Variability":"Dynamic","
					 * Validity":"ValidityPeriod","Rule":"LatestValCover",
					 * "Comments":"Precise Orbit Ephemeris from Copernicus POD service"},
					 * "FullName":"S1A_OPER_AUX_POEORB_OPOD_20151026T122329_V20151005T225943_20151007T005943.EOF.zip",
					 * "Baseline":"03.31","IpfVersion":"S1-IPF-03.31","ICID":null,
					 * "ValidityStart":"2015-10-05T22:59:43+02:00[Europe/Paris]",
					 * "ValidityStop":"2015-10-07T00:59:43+02:00[Europe/Paris]",
					 * "SensingTimeApplicationStart":"2015-10-05T22:59:43+02:00[Europe/Paris]",
					 * "SensingTimeApplicationStop":"2015-10-06T22:59:43+02:00[Europe/Paris]",
					 * "CreationDate":"2015-10-26T12:23:29+01:00[Europe/Paris]","Band":"BXX","Unit":"A"}
					 */
					aux.Id = UUID.fromString(value.get("Id").asText());    
					if (value.get("AuxType") != null) {
						aux.AuxType = value.get("AuxType").get("LongName").asText();
					}
					aux.FullName = value.get("FullName").asText();
					aux.ValidityStart = convertFromISOString(value.get("ValidityStart").asText());
					aux.ValidityStop = convertFromISOString(value.get("ValidityStop").asText());
					aux.CreationDate = convertFromISOString(value.get("CreationDate").asText());
					aux.Band = value.get("Band").asText();
					aux.Unit = value.get("Unit").asText();
					res.add(aux);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Cannot decode payload");
		}
		return res;
	}  
	
	private static ZonedDateTime convertFromISOString(final String str) {
		return ZonedDateTime.parse(str);
		
	}

	@Override
	public int compareTo(AuxFile arg0) {
		return CreationDate.compareTo(arg0.CreationDate);
	}
}
