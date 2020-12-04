package com.csgroup.rba.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    	PasswordEncoder encoder = 
	          PasswordEncoderFactories.createDelegatingPasswordEncoder();
	    	auth
	          .inMemoryAuthentication()
	          .withUser("user")
	          .password(encoder.encode("DTmgBS$J7GiiS6Zd"))
	          .roles("USER")
	          .and()
	          .withUser("admin")
	          .password(encoder.encode("VC&&v*9rS4cFVSMW"))
	          .roles("USER", "ADMIN");
	    }

	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	    	http
	    	.httpBasic().and()
	        .authorizeRequests()
	          .antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
	          .antMatchers(HttpMethod.PUT, "/**").hasRole("ADMIN")
	          .antMatchers(HttpMethod.PATCH, "/**").hasRole("ADMIN")
	          .and().csrf().disable();
	          
	    }
}
