package com.csgroup.reprodatabaseline.http;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.csgroup.reprodatabaseline.config.UrlsConfiguration;
import com.csgroup.reprodatabaseline.datamodels.AuxFile;
import com.csgroup.reprodatabaseline.datamodels.AuxType;
import com.csgroup.reprodatabaseline.datamodels.AuxTypes;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ReproBaselineAccess {
	private static final Logger LOG = LoggerFactory.getLogger(ReproBaselineAccess.class);
	
	private final HttpHandler httpHandler;
	
	private final UrlsConfiguration config;
	
	public ReproBaselineAccess(HttpHandler handler, UrlsConfiguration conf) {
		this.httpHandler = handler;
		this.config = conf;
	}
	
	public AuxTypes getListOfAuxTypes(final String mission){
		String res = httpHandler.getPost(config.getReprocessing_baseline_url()+"/AuxTypes?$expand=ProductLevels&$filter=Mission eq \'"+mission+"\'");
		AuxTypes res_aux = AuxTypes.loadValues(res);
		LOG.info(String.valueOf(res_aux.getValues().size()));
		return res_aux;
	}
	
	public List<AuxFile> getListOfAuxFiles(final AuxType type, final String sat, final String unit){
		//Maybe it-s shortName on type ?
		String res = httpHandler.getPost(config.getReprocessing_baseline_url()+
				"/AuxFiles?$expand=AuxType&$filter=startswith(FullName,\'"+sat+"_\') and contains(FullName,\'"+type.LongName+"\')");
		List<AuxFile> res_aux = AuxFile.loadValues(type,res);
		res = httpHandler.getPost(config.getReprocessing_baseline_url()+
				"/AuxFiles?$expand=AuxType&$filter=startswith(FullName,\'"+sat+unit+"\') and contains(FullName,\'"+type.LongName+"\')");
		List<AuxFile> res_aux_unit = AuxFile.loadValues(type,res);
		res_aux.addAll(res_aux_unit);
		LOG.info(String.valueOf(res_aux.size()));
		return res_aux;
	}
	
	@Scheduled(fixedRate = 3600000, initialDelay = 5000)
	public void doMetrics() {
		LOG.info("Starting retireve");
		AuxTypes types = getListOfAuxTypes("S2MSI");
		List<AuxFile> files = getListOfAuxFiles(types.getValues().get(0),"S2","B");
		LOG.info("Retrieve done");
	}

}
