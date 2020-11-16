package com.csgroup.rba.model;
import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.sdl.odata.api.edm.annotations.EdmEnum;
import com.sdl.odata.api.edm.model.PrimitiveType;


@ODataJPAEntity(value = "com.csgroup.rba.model.jpa.RuleJPA")
@EdmEnum(namespace = "OData.RBA", name = "Rule", underlyingType = PrimitiveType.INT32, flags = false)
public enum Rule {
	ValIntersectWithoutDuplicate,
	LatestValIntersect,
	ValCover,
	LatestValCover,
	LatestValidity,
	LatestValCoverLatestValidity,
	LatestValidityClosest,
	BestCentredCover,
	LatestValCoverClosest,
	LargestOverlap,
	LatestGeneration,
	ClosestStartValidity,
	ClosestStopValidity,
	LatestStopValidity
}
