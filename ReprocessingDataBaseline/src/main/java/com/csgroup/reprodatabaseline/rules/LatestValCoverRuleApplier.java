package com.csgroup.reprodatabaseline.rules;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;

public class LatestValCoverRuleApplier implements RuleApplierInterface {

	 /*
    This mode gets the latest file that covers entirely time interval [t0 ? dt0 , t1 + dt1]. The latest record is the one with the more
    recent Generation Date. Using this query in the scenario exhibited in fig B-1, it will return record R3
    */
	@Override
	public List<AuxFile> apply(List<AuxFile> files, 
			ZonedDateTime t0, ZonedDateTime t1,
			TemporalAmount dt0, TemporalAmount dt1) {
		ValCoverRuleApplier valcover_rule = new ValCoverRuleApplier();
		List<AuxFile> initial_res = valcover_rule.apply(files, t0, t1, dt0, dt1);
		Collections.sort(initial_res);
		
		List<AuxFile> res = new ArrayList<AuxFile>();
		if (initial_res.size() != 0) {
			res.add(initial_res.get(initial_res.size()-1));
		}
		return res;
	}

}
