package com.csgroup.reprodatabaseline.rules;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;

public class BestCentredCoverRuleApplier implements RuleApplierInterface {

	/*
    This mode gets the latest file which covers entirely time interval [t0 - dt0 , t1 + dt1], and for which is maximized the minimum
    distance of his extremes from the time interval borders. That is, if we name A and B the left and right endpoint of the file
    validity interval, the selected file is the one corresponding to maxi(min(Ai ? (t0 ? dt0 ), Bi ? (t1 + dt1 )). Using this query in
    the scenario exhibited in fig B-1, it will return record R3.
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
				Long min_a = Math.min(RuleUtilities.getDiff(t0.minus(dt0), a.ValidityStart),
						RuleUtilities.getDiff(a.ValidityStop, t1.plus(dt1)));
				Long min_b = Math.min(RuleUtilities.getDiff(t0.minus(dt0), b.ValidityStart),
						RuleUtilities.getDiff(b.ValidityStop, t1.plus(dt1)));
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
