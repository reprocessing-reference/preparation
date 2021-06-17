package com.csgroup.reprodatabaseline.rules;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;

public class LatestValCoverClosestRuleApplier implements RuleApplierInterface {

	 /*
    This mode gets the file that :
    * covers entirely time interval interval [t0 ? dt0 , t1 + dt1]
    and
    * has got the start time closest to to-dt0.
    Basically the outcomes of this mode is the same as the following sequence is applied:
    * Get files with ?ValCover? mode
    * Among the returned files select the one with start time closes to to-dt0.
    In Figure B-1 this would be product R2. If there are several files with the same start time choose the most recent ingestion time
    */
	@Override
	public List<AuxFile> apply(List<AuxFile> files, 
			ZonedDateTime t0, ZonedDateTime t1,
			TemporalAmount dt0, TemporalAmount dt1) {
		ValCoverRuleApplier valcover_rule = new ValCoverRuleApplier();
		List<AuxFile> initial_result = valcover_rule.apply(files, t0, t1, dt0, dt1);
		Collections.sort(initial_result);
		Collections.reverse(initial_result);
		Collections.sort(initial_result,
				new Comparator<AuxFile>() {
			@Override
			public int compare(AuxFile a, AuxFile b ) {
				Long min_a = RuleUtilities.getDiff(t0.minus(dt0), a.ValidityStart);
				Long min_b = RuleUtilities.getDiff(t0.minus(dt0), b.ValidityStart);
				return min_a.compareTo(min_b);
			}
		});
		List<AuxFile> res = new ArrayList<AuxFile>();
		if (initial_result.size() != 0) {
			res.add(initial_result.get(initial_result.size()-1));
		}
		return res;
	}

}
