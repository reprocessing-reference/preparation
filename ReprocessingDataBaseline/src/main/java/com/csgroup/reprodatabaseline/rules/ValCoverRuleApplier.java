package com.csgroup.reprodatabaseline.rules;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;

public class ValCoverRuleApplier implements RuleApplierInterface {

	 /*
    This mode gets all files that cover entirely time interval [t0 ? dt0, t1 + dt1]. 
    Using this query in the scenario exhibited in fig B-1, it will return records R2 and R3
    */
	@Override
	public List<AuxFile> apply(List<AuxFile> files, 
			ZonedDateTime t0, ZonedDateTime t1,
			TemporalAmount dt0, TemporalAmount dt1) {
		List<AuxFile> res = new ArrayList<AuxFile>();
		for (AuxFile file : files) {
	        if ((file.ValidityStart.isBefore(t0.minus(dt0)) || file.ValidityStart.isEqual(t0.minus(dt0)) ) && 
	        		(file.ValidityStop.isAfter(t1.plus(dt1)) || file.ValidityStop.isEqual(t1.plus(dt1)) )) {
	            res.add(file);
	        }
		}
		return res;
	}

}
