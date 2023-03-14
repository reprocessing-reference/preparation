package com.csgroup.reprodatabaseline.rules;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;

public class LatestValidityClosestRuleApplier implements RuleApplierInterface {

	/*
    This mode gets the latest file which is nearest to ((t0-dt0)+(t1+dt1))/2. Using this query in the scenario exhibited in fig B-1, it will return record R4
    */
	@Override
	public List<AuxFile> apply(List<AuxFile> files, 
			ZonedDateTime t0, ZonedDateTime t1,
			TemporalAmount dt0, TemporalAmount dt1) {
	    ZonedDateTime middle = RuleUtilities.getMeanTime(t0.minus(dt0), t1.plus(dt1));
	    // System.out.println(t0.toString());
	    // System.out.println(middle.toString());
	    // System.out.println(t1.toString());
	    Collections.sort(files, 
				new Comparator<AuxFile>() {
			@Override
			public int compare(AuxFile a, AuxFile b ) {
				double dista = 0;
				if (a.ValidityStart.isAfter(middle))
				{
					dista = RuleUtilities.getDiff(middle, a.ValidityStart);
				} else {
					dista = RuleUtilities.getDiff(a.ValidityStart, middle);
				}				
				double distb = 0;
				if (b.ValidityStart.isAfter(middle))
				{
					distb = RuleUtilities.getDiff(middle, b.ValidityStart);
				} else {
					distb = RuleUtilities.getDiff(b.ValidityStart, middle);
				}
				if (Double.compare(dista, distb) == 0) {
					return a.CreationDate.compareTo(b.CreationDate);
				} else {
					return Double.compare(dista, distb);
				}
			}
		});
	    List<AuxFile> res = new ArrayList<AuxFile>();
		if (files.size() != 0) {
			res.add(files.get(0));
		}
		return res;
	    
	}

}
