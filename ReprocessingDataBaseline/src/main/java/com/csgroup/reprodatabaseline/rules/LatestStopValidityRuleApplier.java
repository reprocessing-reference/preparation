package com.csgroup.reprodatabaseline.rules;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;

public class LatestStopValidityRuleApplier implements RuleApplierInterface {

	/*
    This mode gets a product with the latest Validity Stop Time. In Figure B-1 this would be product R6.
    */
	@Override
	public List<AuxFile> apply(List<AuxFile> files, 
			ZonedDateTime t0, ZonedDateTime t1,
			TemporalAmount dt0, TemporalAmount dt1) {
		Collections.sort(files,
				new Comparator<AuxFile>() {
			@Override
			public int compare(AuxFile a, AuxFile b ) {
				return a.ValidityStop.compareTo(b.ValidityStop);
			}
		});
		List<AuxFile> res = new ArrayList<AuxFile>();
		if (files.size() != 0) {
			res.add(files.get(files.size()-1));
		}
		return res;
	}

}
