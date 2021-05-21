package com.csgroup.reprodatabaseline.rules;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;
/**
 * This mode gets the file with the nearest Validity Start Time.
 */
public class ClosestStartValidityRuleApplier implements RuleApplierInterface {

	@Override
	public List<AuxFile> apply(List<AuxFile> files, 
			ZonedDateTime t0, ZonedDateTime t1,
			TemporalAmount dt0, TemporalAmount dt1) {

		Collections.sort(files, new Comparator<AuxFile>() {
			@Override
			public int compare(AuxFile a, AuxFile b ) {
				Long at0 = (Long)Math.abs(RuleUtilities.getDiff(a.ValidityStart,t0));
				Long bt0 = (Long)Math.abs(RuleUtilities.getDiff(b.ValidityStart,t0));
				return at0.compareTo(bt0) ;
			}});
		
		List<AuxFile> res = new ArrayList<AuxFile>();
		if (files.size() != 0) {
			res.add(files.get(0));
		}
		return res;
	}

}
