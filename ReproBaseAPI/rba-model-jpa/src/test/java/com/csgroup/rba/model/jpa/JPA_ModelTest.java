package com.csgroup.rba.model.jpa;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.csgroup.rba.model.jpa.AuxFileTypeJPA;
import com.csgroup.rba.model.jpa.BaselineJPA;
import com.csgroup.rba.model.jpa.SensorJPA;


/**
 * @author besquis
 */
@RunWith(SpringRunner.class)
@SpringBootTest

public class JPA_ModelTest {
    
    @Autowired
    private SensorRepository sensorRepository;
    
    @Autowired
    private BaselineRepository baselineRepository;
    
    @Autowired
    private AuxFileTypeRepository auxFileTypeRepository;
    @Autowired
    private AuxFileRepository auxFileRepository;
    @Autowired
    private BandRepository bandsRepository;
    @Autowired
    private ProductLevelRepository productLevelRepository;
    
    @Test
    public void whenFindingCustomerById_thenCorrect() throws Exception {
    	
    	ProductLevelJPA level = new ProductLevelJPA();
    	level.setLevel("L1");
    	productLevelRepository.save(level);
    	assertEquals(productLevelRepository.findById("L1").get().getLevel(),"L1") ;
    	
    	BandJPA band = new BandJPA();
    	band.setId(UUID.randomUUID());
    	band.setName("B00");
    	bandsRepository.save(band);
    	assertEquals(bandsRepository.findById(band.getId()).get().getName(),"B00") ;
    	
    	
    	SensorJPA sens = new SensorJPA();
    	sens.setFullName("SENTINEL2A_S2MSI");
    	sens.setSatellite("SENTINEL2A");
    	sens.setShortName("S2MSI");
    	sensorRepository.save(sens);
    	assertEquals(sensorRepository.findById("SENTINEL2A_S2MSI").get().getFullName(),"SENTINEL2A_S2MSI") ;
    	
    	BaselineJPA base = new BaselineJPA();
    	base.setDate(ZonedDateTime.now());
    	base.setName("02.09");
    	baselineRepository.save(base);    	
    	
    	AuxFileTypeJPA auxFileType = new AuxFileTypeJPA();
    	auxFileType.setDescription("machin");
    	auxFileType.setFormat("xml");
    	auxFileType.setOrigin("esa");
    	auxFileType.setLongName("GIP_OLQCPA");
    	auxFileType.setShortName("GIP_OLQCPA");
    	auxFileType.setProductLevelApplicability(Lists.newArrayList(level));
    	auxFileType.setRule(RuleJPA.BestCentredCover);
    	auxFileType.setVariability(VariabilityJPA.Static);
    	auxFileTypeRepository.save(auxFileType);
    	
    	AuxFileTypeJPA copytype = auxFileTypeRepository.findById("GIP_OLQCPA").get();
    	
    	if (copytype.getProductLevelApplicability() == null)
    	{
    		throw new Exception("Procuct level applicability not saved correclty");
    	}
    	
    	
    	AuxFileJPA auxFile = new AuxFileJPA();
    	auxFile.setBands(Lists.newArrayList(band));
    	auxFile.setBaseline(base);
    	ChecksumJPA check = new ChecksumJPA();
    	check.setAlgorithm("md5");
    	check.setChecksumDate(ZonedDateTime.now());
    	check.setId(UUID.randomUUID());
    	check.setValue("ooijaoijaoijaoij");
    	auxFile.setChecksum(check);
    	auxFile.setCreationDate(ZonedDateTime.now());
    	auxFile.setFileType(auxFileType);
    	auxFile.setFullName("saszdssd");
    	auxFile.setIdentifier(UUID.randomUUID());
    	auxFile.setSensors(Lists.newArrayList(sens));
    	TimeRangeJPA time = new TimeRangeJPA();
    	time.setStop(ZonedDateTime.now());
    	time.setStart(ZonedDateTime.now());
    	auxFile.setValidity(time);
    	
    	auxFileRepository.save(auxFile);
    	
    	auxFileRepository.count();
    	
    	AuxFileJPA copy = auxFileRepository.findById(auxFile.getIdentifier()).get();
    	
    	System.out.println(copy.getBaseline().getName());
    	
    	
    	
    } 
    
}