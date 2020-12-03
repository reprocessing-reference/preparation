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

import com.csgroup.rba.model.jpa.AuxTypeJPA;



/**
 * @author besquis
 */
@RunWith(SpringRunner.class)
@SpringBootTest

public class JPA_ModelTest {
    
     
    @Autowired
    private AuxFileTypeRepository auxFileTypeRepository;
    @Autowired
    private AuxFileRepository auxFileRepository;
    
    @Autowired
    private ProductTypeRepository productTypeRepository;
    
    @Autowired
    private ProductLevelRepository productLevelRepository;
    
    @Test
    public void whenFindingCustomerById_thenCorrect() throws Exception {
    	
    	ProductLevelJPA level = new ProductLevelJPA();
    	level.setLevel("L1");
    	productLevelRepository.save(level);
    	assertEquals(productLevelRepository.findById("L1").get().getLevel(),"L1") ;    	
    	
    	ProductTypeJPA type = new ProductTypeJPA();
    	type.setType("L1");
    	productTypeRepository.save(type);
    	assertEquals(productTypeRepository.findById("L1").get().getType(),"L1") ;   	
    	
    	
    	
    	AuxTypeJPA auxFileType = new AuxTypeJPA();
    	auxFileType.setComments("machin");
    	auxFileType.setFormat("xml");
    	auxFileType.setOrigin("esa");
    	auxFileType.setLongName("GIP_OLQCPA");
    	auxFileType.setShortName("GIP_OLQCPA");
    	auxFileType.setProductTypes(Lists.newArrayList(type));
    	auxFileType.setRule(RuleJPA.BestCentredCover);
    	auxFileType.setVariability(VariabilityJPA.Static);
    	auxFileTypeRepository.save(auxFileType);
    	
    	AuxTypeJPA copytype = auxFileTypeRepository.findById("GIP_OLQCPA").get();
    	
    	if (copytype.getProductTypes() == null)
    	{
    		throw new Exception("Procuct level applicability not saved correclty");
    	}
    	
    	
    	AuxFileJPA auxFile = new AuxFileJPA();
    	auxFile.setBand("B00");
    	auxFile.setBaseline("01.09");    	
    	auxFile.setCreationDate(ZonedDateTime.now());
    	auxFile.setAuxType(auxFileType);
    	auxFile.setFullName("saszdssd");
    	auxFile.setIdentifier(UUID.randomUUID());
    	auxFile.setValidityStart(ZonedDateTime.now());
    	auxFile.setValidityStop(ZonedDateTime.now());
    	
    	auxFileRepository.save(auxFile);
    	
    	auxFileRepository.count();
    	
    	AuxFileJPA copy = auxFileRepository.findById(auxFile.getIdentifier()).get();
    	
    	System.out.println(copy.getBaseline());
    	
    	
    	
    } 
    
}