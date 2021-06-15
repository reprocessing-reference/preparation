package com.csgroup.reprodatabaseline.datamodels;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csgroup.reprodatabaseline.rules.RuleEnum;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class AuxTypes {
	private static final Logger LOG = LoggerFactory.getLogger(AuxTypes.class);

	private List<AuxType> values = new ArrayList<AuxType>();

	public static AuxTypes loadValues(final String json_str) {
		AuxTypes res = new AuxTypes();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			JsonNode actualObj = mapper.readTree(json_str.replaceAll("@", ""));
			JsonNode valueNode = actualObj.get("value");
			if (valueNode.isArray()) {
				for (JsonNode value : valueNode) {
					AuxType aux = new AuxType();
					/*{@odata.id=AuxTypes('GIP_G2PARA'), LongName=GIP_G2PARA, ShortName=GIP_G2PARA, 
							Format=EOF, Mission=S2MSI, ProductLevels=[{@odata.type=#OData.RBA.ProductLevel, 
							Level=L1}], Variability=Static, Validity=AnyTime, Rule=LatestGeneration, 
							Comments=geometric parameters for IPF L1 processor}*/
					aux.LongName = value.get("LongName").asText();
					aux.ShortName= value.get("ShortName").asText();
					aux.Format= value.get("Format").asText();
					aux.Mission= value.get("Mission").asText();
				    // depending of the requested expand : ProductLevels or ProductTypes
					try {
						JsonNode levelNode = value.get("ProductLevels");
						if (levelNode.isArray()) {
							for (JsonNode level : levelNode) {
								aux.ProductLevels.add(level.get("Level").asText());
							}
						}
					} catch (Exception e) {
						//TODO: handle exception
						LOG.debug("ProductLevels node not found : maybe not expanded");
					}

					try {
						JsonNode typeNode = value.get("ProductTypes");
						if (typeNode.isArray()) {
							for (JsonNode type : typeNode) {
								aux.ProductTypes.add(type.get("Type").asText());
							}
						}
					} catch (Exception e) {
						//TODO: handle exception
						LOG.debug("ProductTypes node not found : maybe not expanded");
					}
					

					aux.Variability= value.get("Variability").asText();
					aux.Validity= value.get("LongName").asText();
					aux.Rule= RuleEnum.valueOf(value.get("Rule").asText());
					aux.Comments= value.get("Comments").asText();
					res.values.add(aux);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Cannot decode payload");
		}
		return res;
	}

	public List<AuxType> getValues() {
		return values;
	}

}
