package com.csgroup.reprodatabaseline.rules;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;

public class ValIntersectWithoutDuplicateRuleApplier implements RuleApplierInterface {

	/*
    This mode gets all files that cover partly time interval [t0 ? dt0 , t1 + dt1]. Using this query in the scenario exhibited in fig B-1, it
    will return records R1, R2, R3 and R4.
    */
	@Override
	public List<AuxFile> apply(List<AuxFile> files, 
			ZonedDateTime t0, ZonedDateTime t1,
			TemporalAmount dt0, TemporalAmount dt1) {
		List<AuxFile> res = new ArrayList<AuxFile>();
		
		for (AuxFile file : files) {
			boolean first = file.ValidityStart.isBefore(t0.minus(dt0)) &&
			        t0.minus(dt0).isBefore(file.ValidityStop);
			boolean second = file.ValidityStart.isBefore(t1.minus(dt1)) &&
			        t1.minus(dt1).isBefore(file.ValidityStop);
			
			if (first || second) {
	            res.add(file);
	        }
		}
		return res;
	}

}
