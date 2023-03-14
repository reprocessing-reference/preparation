package com.csgroup.reprodatabaseline;

import com.csgroup.reprodatabaseline.config.JPADataSourceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableScheduling
@Import({
		JPADataSourceConfiguration.class,
})
public class ReprobaselineApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReprobaselineApplication.class, args);
	}

}
