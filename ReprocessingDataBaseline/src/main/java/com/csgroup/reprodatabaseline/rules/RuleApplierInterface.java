package com.csgroup.reprodatabaseline.rules;

import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;

public interface RuleApplierInterface {
	
	public AuxFile apply(List<AuxFile> files);

}
