package com.csgroup.reprodatabaseline.rules;

public class RuleApplierFactory {
	
	public static RuleApplierInterface getRuleApplier(RuleEnum rule) {
		switch (rule) {
		case ValIntersectWithoutDuplicate:
			return new ValIntersectWithoutDuplicateRuleApplier();
		case LatestValIntersect:
			return new LatestValIntersectRuleApplier();
		case ValCover:
			return new ValCoverRuleApplier();
		case LatestValCover:
			return new LatestValCoverRuleApplier();
		case LatestValidity:
			return new LatestValidityRuleApplier();
		case LatestValCoverLatestValidity:
			return new LatestValCoverLatestValidityRuleApplier();
		case LatestValidityClosest:
			return new LatestValidityClosestRuleApplier();
		case BestCentredCover:
			return new BestCentredCoverRuleApplier();
		case LatestValCoverClosest:
			return new LatestValCoverClosestRuleApplier();
		case LargestOverlap:
			return new LargestOverlapRuleApplier();
		case LatestGeneration:
			return new LatestGenerationRuleApplier();
		case ClosestStartValidity:
			return new ClosestStartValidityRuleApplier();
		case ClosestStopValidity:
			return new ClosestStopValidityRuleApplier();
		case LatestStopValidity:
			return new LatestStopValidityRuleApplier();
		default:
			return null;
		}
	}

}
