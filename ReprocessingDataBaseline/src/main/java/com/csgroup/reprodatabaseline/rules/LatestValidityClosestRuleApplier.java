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
		List<AuxFile> results = new ArrayList<AuxFile>();
	    ZonedDateTime middle = RuleUtilities.getMeanTime(t0.minus(dt0), t1.plus(dt1));
	    for (AuxFile file : files) {
	    	if (file.ValidityStart.isAfter(middle)) {
	    		results.add(file);
	    	}
	    }
	    Collections.sort(results, 
				new Comparator<AuxFile>() {
			@Override
			public int compare(AuxFile a, AuxFile b ) {
				return a.ValidityStart.compareTo(b.ValidityStart);
			}
		});
	    
	    List<AuxFile> res = new ArrayList<AuxFile>();
		if (results.size() != 0) {
			res.add(results.get(results.size()-1));
		}
		return res;
	    
	}

}
