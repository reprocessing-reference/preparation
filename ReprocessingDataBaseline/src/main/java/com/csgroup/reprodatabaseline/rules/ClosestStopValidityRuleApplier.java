package com.csgroup.reprodatabaseline.rules;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;

/**
 * This mode gets the file with the nearest Validity Stop Time.
 */
public class ClosestStopValidityRuleApplier implements RuleApplierInterface {

	@Override
	public List<AuxFile> apply(List<AuxFile> files, 
			ZonedDateTime t0, ZonedDateTime t1,
			TemporalAmount dt0, TemporalAmount dt1) {


		Collections.sort(files, new Comparator<AuxFile>() {
			@Override
			public int compare(AuxFile a, AuxFile b ) {
				Long at1 = (Long)Math.abs(RuleUtilities.getDiff(a.ValidityStop,t1));
				Long bt1 = (Long)Math.abs(RuleUtilities.getDiff(b.ValidityStop,t1));
				return at1.compareTo(bt1) ;
			}});

		List<AuxFile> res = new ArrayList<AuxFile>();
		if (files.size() != 0) {
			res.add(files.get(0));
		}
		return res;
		
	}

}
