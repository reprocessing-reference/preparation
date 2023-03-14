package com.csgroup.reprodatabaseline.rules;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;

public class LatestValCoverLatestValidityRuleApplier implements RuleApplierInterface {

    /*
    This mode applies first ?LatestValCover?. If no file is returned then it applies "Latest Validity"
    TODO : not sure of the implementation
    */
	
	@Override
	public List<AuxFile> apply(List<AuxFile> files, 
			ZonedDateTime t0, ZonedDateTime t1,
			TemporalAmount dt0, TemporalAmount dt1) {
		LatestValCoverRuleApplier valcover_rule = new LatestValCoverRuleApplier();
		List<AuxFile> initial_result = valcover_rule.apply(files, t0, t1, dt0, dt1);
		if (initial_result.isEmpty()) {
			LatestValidityRuleApplier latest_rule = new LatestValidityRuleApplier();
			return latest_rule.apply(files, t0, t1, dt0, dt1);
		}
		return initial_result;
	}

}
