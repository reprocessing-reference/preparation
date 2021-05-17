package com.csgroup.reprodatabaseline.rules;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;

public class ClosestStartValidityRuleApplier implements RuleApplierInterface {

	@Override
	public List<AuxFile> apply(List<AuxFile> files, 
			ZonedDateTime t0, ZonedDateTime t1,
			TemporalAmount dt0, TemporalAmount dt1) {
		// TODO Auto-generated method stub
		return null;
	}

}
