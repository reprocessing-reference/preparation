package com.csgroup.reprodatabaseline.datamodels;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class AuxType{
	public String LongName;

	public String ShortName;

	public String Format;

	public String Mission;

	public List<String> ProductLevels = new ArrayList<String>();

	public List<String> ProductTypes = new ArrayList<String>();

	public String Variability;

	public String Validity;

	public RuleEnum Rule;

	public String Comments;

}


