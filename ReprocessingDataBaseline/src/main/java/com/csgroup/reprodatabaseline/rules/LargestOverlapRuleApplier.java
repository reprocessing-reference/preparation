package com.csgroup.reprodatabaseline.rules;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Collections;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;

public class LargestOverlapRuleApplier implements RuleApplierInterface {

	 /*
    This mode gets the file (only one) that satisfies both the following conditions:
    * covers entirely time interval interval [t0 ? dt0 , t1 + dt1]
    * has got the largest overlap.
    Basically the outcomes of this mode is the same as the following sequence is applied:
    * Get files with ?ValCover? mode
    * Among the returned files select the one with the largest overlap.
    If there are several products with the same overlap (e.g. full coverage), the product with the start time that is closest to TOTO
    is chosen. Note that in the full coverage case the result is identical to "ValCoverClosest".
    */
	@Override
	public List<AuxFile> apply(List<AuxFile> files, 
			ZonedDateTime t0, ZonedDateTime t1,
			TemporalAmount dt0, TemporalAmount dt1) {
		ValCoverRuleApplier valcover_rule = new ValCoverRuleApplier();
		List<AuxFile> initial_result = valcover_rule.apply(files, t0, t1, dt0, dt1);
		Collections.sort(initial_result);
		Collections.reverse(initial_result);
		if (initial_result.size() != 0) {
			LatestValCoverClosestRuleApplier latest_rule = new LatestValCoverClosestRuleApplier();
			return latest_rule.apply(initial_result, t0, t1, dt0, dt1);
		}
		return initial_result;
	}

}
