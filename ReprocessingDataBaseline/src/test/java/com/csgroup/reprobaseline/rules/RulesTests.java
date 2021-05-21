package com.csgroup.reprobaseline.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;
import com.csgroup.reprodatabaseline.rules.BestCentredCoverRuleApplier;
import com.csgroup.reprodatabaseline.rules.ClosestStartValidityRuleApplier;
import com.csgroup.reprodatabaseline.rules.ClosestStopValidityRuleApplier;
import com.csgroup.reprodatabaseline.rules.LargestOverlapRuleApplier;
import com.csgroup.reprodatabaseline.rules.LatestGenerationRuleApplier;
import com.csgroup.reprodatabaseline.rules.LatestStopValidityRuleApplier;
import com.csgroup.reprodatabaseline.rules.LatestValCoverClosestRuleApplier;
import com.csgroup.reprodatabaseline.rules.LatestValCoverLatestValidityRuleApplier;
import com.csgroup.reprodatabaseline.rules.LatestValCoverRuleApplier;
import com.csgroup.reprodatabaseline.rules.LatestValIntersectRuleApplier;
import com.csgroup.reprodatabaseline.rules.LatestValidityClosestRuleApplier;
import com.csgroup.reprodatabaseline.rules.LatestValidityRuleApplier;
import com.csgroup.reprodatabaseline.rules.ValCoverRuleApplier;
import com.csgroup.reprodatabaseline.rules.ValIntersectWithoutDuplicateRuleApplier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = {
	BestCentredCoverRuleApplier.class,              
	LatestValidityClosestRuleApplier.class,
	ClosestStartValidityRuleApplier.class,          
	LatestValidityRuleApplier.class,
	ClosestStopValidityRuleApplier.class,          
	LatestValIntersectRuleApplier.class,
	LargestOverlapRuleApplier.class,                
	LatestGenerationRuleApplier.class,              
	LatestStopValidityRuleApplier.class,            
	LatestValCoverClosestRuleApplier.class,         
	LatestValCoverLatestValidityRuleApplier.class,  
	ValCoverRuleApplier.class,
	LatestValCoverRuleApplier.class,                
	ValIntersectWithoutDuplicateRuleApplier.class,
	})
@ContextConfiguration
class RulesTests {

	@Autowired
	BestCentredCoverRuleApplier	bestCentredCoverRuleApplier;
	@Autowired              
	LatestValidityClosestRuleApplier latestValidityClosestRuleApplier;
	@Autowired
	ClosestStartValidityRuleApplier closestStartValidityRuleApplier;
	@Autowired         
	LatestValidityRuleApplier latestValidityRuleApplier;
	@Autowired
	ClosestStopValidityRuleApplier closestStopValidityRuleApplier; 
	@Autowired         
	LatestValIntersectRuleApplier latestValIntersectRuleApplier;
	@Autowired
	LargestOverlapRuleApplier largestOverlapRuleApplier ;
	@Autowired                
	LatestGenerationRuleApplier latestGenerationRuleApplier;
	@Autowired 
	LatestStopValidityRuleApplier latestStopValidityRuleApplier;
	@Autowired           
	LatestValCoverClosestRuleApplier latestValCoverClosestRuleApplier;
	@Autowired         
	LatestValCoverLatestValidityRuleApplier latestValCoverLatestValidityRuleApplier ;
	@Autowired  
	ValCoverRuleApplier valCoverRuleApplier ;
	@Autowired
	LatestValCoverRuleApplier latestValCoverRuleApplier;
	@Autowired               
	ValIntersectWithoutDuplicateRuleApplier valIntersectWithoutDuplicateRuleApplier;

	public List<AuxFile> getAuxFiles(){
		List<AuxFile> auxFiles = new ArrayList<>();

		// AUX1
		AuxFile aux1 = new AuxFile();
		aux1.ValidityStart = ZonedDateTime.of(2019, 2, 12, 1, 55, 12, 0,ZoneId.of("UTC"));
		aux1.ValidityStop = ZonedDateTime.of(2019, 2, 13, 1, 55, 12, 0,ZoneId.of("UTC"));
		aux1.CreationDate = ZonedDateTime.of(2019, 2, 12, 1, 55, 12, 0,ZoneId.of("UTC"));

		aux1.FullName = "AUX1";
		auxFiles.add(aux1);

		// AUX2
		AuxFile aux2 = new AuxFile();
		aux2.ValidityStart = ZonedDateTime.of(2019, 2, 12, 13, 55, 12, 0,ZoneId.of("UTC"));
		aux2.ValidityStop = ZonedDateTime.of(2019, 2, 13, 13, 55, 12, 0,ZoneId.of("UTC"));
		aux2.CreationDate = ZonedDateTime.of(2019, 2, 12, 13, 55, 12, 0,ZoneId.of("UTC"));

		aux2.FullName = "AUX2";
		auxFiles.add(aux2);

		// AUX3
		AuxFile aux3 = new AuxFile();
		aux3.ValidityStart = ZonedDateTime.of(2019, 2, 12, 21, 55, 12, 0,ZoneId.of("UTC"));
		aux3.ValidityStop = ZonedDateTime.of(2019, 2, 13, 23, 55, 12, 0,ZoneId.of("UTC"));
		aux3.CreationDate = ZonedDateTime.of(2019, 2, 12, 21, 55, 12, 0,ZoneId.of("UTC"));

		aux3.FullName = "AUX3";
		auxFiles.add(aux3);

		// AUX4
		AuxFile aux4 = new AuxFile();
		aux4.ValidityStart = ZonedDateTime.of(2019, 2, 12, 21, 55, 12, 0,ZoneId.of("UTC"));
		aux4.ValidityStop = ZonedDateTime.of(2021, 2, 13, 23, 55, 12, 0,ZoneId.of("UTC"));
		aux4.CreationDate = ZonedDateTime.of(2019, 2, 12, 21, 55, 40, 0,ZoneId.of("UTC"));

		aux4.FullName = "AUX4";
		auxFiles.add(aux4);

		// AUX5
		AuxFile aux5 = new AuxFile();
		aux5.ValidityStart = ZonedDateTime.of(2019, 2, 11, 12, 55, 12, 0,ZoneId.of("UTC"));
		aux5.ValidityStop = ZonedDateTime.of(2021, 2, 14, 23, 55, 12, 0,ZoneId.of("UTC"));
		aux5.CreationDate = ZonedDateTime.of(2019, 2, 11, 12, 55, 12, 0,ZoneId.of("UTC"));

		aux5.FullName = "AUX5";
		auxFiles.add(aux5);

		return auxFiles;
	}
	@Test
	void ClosestStartValidityRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.closestStartValidityRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should conains only one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX3");

		t0 = ZonedDateTime.of(2019, 2, 12, 5, 3, 12, 0,ZoneId.of("UTC"));
		rhAuxFiles =  this.closestStartValidityRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		assertEquals(rhAuxFiles.get(0).FullName, "AUX1");

	}

	@Test
	void ClosestStopValidityRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.closestStopValidityRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains only one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX2");

		t1 = ZonedDateTime.of(2019, 2, 13, 21, 3, 12, 0,ZoneId.of("UTC"));
		rhAuxFiles =  this.closestStopValidityRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		assertEquals(rhAuxFiles.get(0).FullName, "AUX3");

	}

	@Test
	void BestCentredCoverRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.bestCentredCoverRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains only one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX5");

	}


	@Test
	void ValCoverRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.valCoverRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains 2 auxdata files:  AUX2 and AUX5
		assertEquals(rhAuxFiles.size(), 2);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX2");
		assertEquals(rhAuxFiles.get(1).FullName, "AUX5");

		// t1 = ZonedDateTime.of(2019, 2, 13, 21, 3, 12, 0,ZoneId.of("UTC"));
		// rhAuxFiles =  this.closestStopValidityRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// assertEquals(rhAuxFiles.get(0).FullName, "AUX3");

	}


	@Test
	void LatestValidityClosestRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.latestValidityClosestRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "TOBE_FIXED");


	}



	@Test
	void LatestValIntersectRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.latestValIntersectRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "TOBE_FIXED");


	}


	@Test
	void LatestValidityRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.latestValidityRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "TOBE_FIXED");


	}


	@Test
	void LargestOverlapRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.largestOverlapRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains 2 auxdata files:  AUX2 and AUX5
		assertEquals(rhAuxFiles.size(), 2);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "TOBE_FIXED");


	}

	@Test
	void LatestGenerationRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.latestGenerationRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains one auxdata files:  AUX2 and AUX5
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX4");

	}               
	@Test
	void LatestStopValidityRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.latestStopValidityRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX5");

	}  
	@Test
	void LatestValCoverClosestRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.latestValCoverClosestRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains 2 auxdata files:  AUX2 and AUX5
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "TOBE_FIXED");

	}         
	@Test
	void LatestValCoverLatestValidityRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.latestValCoverLatestValidityRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains 2 auxdata files:  AUX2 and AUX5
		assertEquals(rhAuxFiles.size(), 2);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "TOBE_FIXED");


	}          
	@Test
	void LatestValCoverRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.latestValCoverRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX2");

	}     
	@Test
	void ValIntersectWithoutDuplicateRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 13, 12, 55, 12, 0,ZoneId.of("UTC"));

		List<AuxFile> rhAuxFiles =  this.valIntersectWithoutDuplicateRuleApplier.apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains 2 auxdata files:  AUX2 and AUX5
		// assertEquals(rhAuxFiles.size(), 2);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "TOBE_FIXED");


	}           


}
