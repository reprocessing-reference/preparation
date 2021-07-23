package com.csgroup.reprodatabaseline.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.csgroup.reprodatabaseline.datamodels.AuxFile;
import com.csgroup.reprodatabaseline.rules.RuleApplierFactory;
import com.csgroup.reprodatabaseline.rules.RuleApplierInterface;
import com.csgroup.reprodatabaseline.rules.RuleEnum;
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
import com.csgroup.reprodatabaseline.rules.ValIntersectRuleApplier;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = {
		RuleApplierFactory.class,
		RuleApplierInterface.class,
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
		ValIntersectRuleApplier.class,
		})
@ContextConfiguration
class RulesTests {
	
	ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
	ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 12, 21, 55, 12, 0,ZoneId.of("UTC"));

	public List<AuxFile> getAuxFiles(){
		List<AuxFile> auxFiles = new ArrayList<>();

		// AUX1
		AuxFile aux1 = new AuxFile();
		aux1.ValidityStart = ZonedDateTime.of(2019, 2, 12, 17, 55, 12, 0,ZoneId.of("UTC"));
		aux1.ValidityStop = ZonedDateTime.of(2019, 2, 12, 20, 35, 12, 0,ZoneId.of("UTC"));
		aux1.CreationDate = ZonedDateTime.of(2019, 2, 12, 1, 55, 12, 0,ZoneId.of("UTC"));

		aux1.FullName = "AUX1";
		auxFiles.add(aux1);

		// AUX2
		AuxFile aux2 = new AuxFile();
		aux2.ValidityStart = ZonedDateTime.of(2019, 2, 12, 18, 55, 12, 0,ZoneId.of("UTC"));
		aux2.ValidityStop = ZonedDateTime.of(2019, 2, 13, 13, 55, 12, 0,ZoneId.of("UTC"));
		aux2.CreationDate = ZonedDateTime.of(2019, 2, 12, 2, 55, 12, 0,ZoneId.of("UTC"));

		aux2.FullName = "AUX2";
		auxFiles.add(aux2);

		// AUX3
		AuxFile aux3 = new AuxFile();
		aux3.ValidityStart = ZonedDateTime.of(2019, 2, 12, 16, 55, 12, 0,ZoneId.of("UTC"));
		aux3.ValidityStop = ZonedDateTime.of(2019, 2, 13, 13, 55, 12, 0,ZoneId.of("UTC"));
		aux3.CreationDate = ZonedDateTime.of(2019, 2, 12, 3, 55, 12, 0,ZoneId.of("UTC"));
		

		aux3.FullName = "AUX3";
		auxFiles.add(aux3);

		// AUX4
		AuxFile aux4 = new AuxFile();
		aux4.ValidityStart = ZonedDateTime.of(2019, 2, 12, 20, 55, 12, 0,ZoneId.of("UTC"));
		aux4.ValidityStop = ZonedDateTime.of(2019, 2, 13, 23, 55, 12, 0,ZoneId.of("UTC"));
		aux4.CreationDate = ZonedDateTime.of(2019, 2, 12, 4, 55, 12, 0,ZoneId.of("UTC"));

		aux4.FullName = "AUX4";
		auxFiles.add(aux4);

		// AUX5
		AuxFile aux5 = new AuxFile();
		aux5.ValidityStart = ZonedDateTime.of(2019, 2, 11, 12, 55, 12, 0,ZoneId.of("UTC"));
		aux5.ValidityStop = ZonedDateTime.of(2019, 2, 12, 17, 55, 12, 0,ZoneId.of("UTC"));
		aux5.CreationDate = ZonedDateTime.of(2019, 2, 12, 5, 55, 12, 0,ZoneId.of("UTC"));
		
		aux5.FullName = "AUX5";
		auxFiles.add(aux5);
		

		// AUX6
		AuxFile aux6 = new AuxFile();
		aux6.ValidityStart = ZonedDateTime.of(2019, 2, 12, 23, 55, 12, 0,ZoneId.of("UTC"));
		aux6.ValidityStop = ZonedDateTime.of(2019, 2, 14, 23, 55, 12, 0,ZoneId.of("UTC"));
		aux6.CreationDate = ZonedDateTime.of(2019, 2, 12, 6, 55, 12, 0,ZoneId.of("UTC"));
		aux6.FullName = "AUX6";
		auxFiles.add(aux6);

		return auxFiles;
	}
	
	@Test
	void RuleFactoryTest() {
		for ( RuleEnum en : RuleEnum.values())
		{
			System.out.println("Testing : "+en.name());
			RuleApplierInterface rule = RuleApplierFactory.
					getRuleApplier(en);
			assertNotNull(rule);
		}
	}
	
	@Test
	void ClosestStartValidityRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.ClosestStartValidity).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should conains only one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX2");
	}

	@Test
	void ClosestStopValidityRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.ClosestStopValidity).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains only one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX1");
	}

	@Test
	void BestCentredCoverRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.BestCentredCover).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains only one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX3");

	}

	/*
	 * This mode gets all files that cover entirely time interval [t0 – dt0
		, t1 + dt1]. Using this query in the scenario exhibited in fig B-1, it
		will return records R2 and R3
	 */
	@Test
	void ValCoverRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.ValCover).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains 2 auxdata files:  AUX2 and AUX3
		assertEquals(rhAuxFiles.size(), 2);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX2");
		assertEquals(rhAuxFiles.get(1).FullName, "AUX3");
	}


	@Test
	void LatestValidityClosestRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();


		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.LatestValidityClosest).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX4");


	}


	@Test
	void LatestValIntersectRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();


		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.LatestValIntersect).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX4");


	}

	@Test
	void LatestValidityRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();


		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.LatestValidity).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX6");


	}

	@Test
	void LargestOverlapRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.LargestOverlap).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains 2 auxdata files:  AUX2 and AUX5
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX2");


	}
	
	@Test
	void LatestGenerationRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();


		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.LatestGeneration).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains one auxdata files:  AUX2 and AUX5
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX6");

	}     
	          
	@Test
	void LatestStopValidityRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.LatestStopValidity).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX6");

	}  
	
	@Test
	void LatestValCoverClosestRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();


		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.LatestValCoverClosest).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains 1 auxdata file:  AUX2
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX2");

	}   
	      
	@Test
	void LatestValCoverLatestValidityRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.LatestValCoverLatestValidity).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains 2 auxdata files:  AUX2 and AUX5
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX3");
	}
	         
	@Test
	void LatestValCoverRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();

		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.LatestValCover).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		// the output list should contains one auxdata file
		assertEquals(rhAuxFiles.size(), 1);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX3");

	} 
	    
	@Test
	void ValIntersectRuleApplier() {

		List<AuxFile> auxFiles = getAuxFiles();
		// adding an aux data which is completly inside selection time interval
		// ZonedDateTime t0 = ZonedDateTime.of(2019, 2, 12, 19, 55, 12, 0,ZoneId.of("UTC"));
		// ZonedDateTime t1 = ZonedDateTime.of(2019, 2, 12, 21, 55, 12, 0,ZoneId.of("UTC"));
		// AUX7
		AuxFile aux7 = new AuxFile();
		aux7.ValidityStart = ZonedDateTime.of(2019, 2, 12, 20, 30, 0, 0,ZoneId.of("UTC"));
		aux7.ValidityStop  = ZonedDateTime.of(2019, 2, 12, 21, 0, 0, 0,ZoneId.of("UTC"));
		aux7.CreationDate  = ZonedDateTime.of(2019, 2, 12, 6, 55, 12, 0,ZoneId.of("UTC"));
		aux7.FullName = "AUX7";
		auxFiles.add(aux7);

		List<AuxFile> rhAuxFiles =  RuleApplierFactory.
				getRuleApplier(RuleEnum.ValIntersectWithoutDuplicate).
				apply(auxFiles, t0, t1, Duration.ofSeconds(0), Duration.ofSeconds(0));
		for (AuxFile f: rhAuxFiles) {
			System.out.println(f.FullName);
		}
		
		// the output list should contains 4 auxdata files:  AUX1 to AUX4
		assertEquals(rhAuxFiles.size(),5);
		// this Aux File should be the closeset to t0
		assertEquals(rhAuxFiles.get(0).FullName, "AUX1");
		assertEquals(rhAuxFiles.get(1).FullName, "AUX2");
		assertEquals(rhAuxFiles.get(2).FullName, "AUX3");
		assertEquals(rhAuxFiles.get(3).FullName, "AUX4");
		assertEquals(rhAuxFiles.get(4).FullName, "AUX7");

	}           

}
