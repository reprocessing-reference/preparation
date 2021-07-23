/**
 * Copyright (c) 2016 All Rights Reserved by the SDL Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.csgroup.rba.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * The Archive configuration.
 * @author Esquis Benjamin
 */
@Configuration
@ComponentScan
public class AuxFilesUpdateConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AuxFilesUpdateConfiguration.class);

    @Value("${update.active}")
    private Boolean active;
        
    @Value("${update.temp_folder}")
    private String tempFolder;
    
	public Boolean getActive() {
		return active;
	}
	
	public String getTempFolder() {
		return tempFolder;
	}
    
    
}
