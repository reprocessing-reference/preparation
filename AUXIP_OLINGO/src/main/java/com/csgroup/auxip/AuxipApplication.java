package com.csgroup.auxip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.csgroup.auxip.config.ArchiveConfiguration;
import com.csgroup.auxip.config.JPADataSourceConfiguration;
import com.csgroup.auxip.config.MetricsConfiguration;
import com.csgroup.auxip.config.ODATAConfiguration;
import com.csgroup.auxip.config.QuotasConfiguration;
import com.csgroup.auxip.config.S3WasabiConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableScheduling
@Import({
		JPADataSourceConfiguration.class,
		S3WasabiConfiguration.class,
		QuotasConfiguration.class,
		MetricsConfiguration.class,
		ArchiveConfiguration.class,
		ODATAConfiguration.class
})
//@ComponentScan(basePackages={"com.csgroup.auxip.controller"})

public class AuxipApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(AuxipApplication.class, args);
	}

}
