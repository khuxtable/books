/*
 * Copyright 2002-2018 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kathrynhuxtable.books;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Read configuration from application.yml file.
 */
@Configuration
public class YAMLConfig {

	@Value("${mcdb.app-name}")
	private String appName;

	@Value("${mcdb.data-directory}")
	private String dataDirectory;

	@Value("${mcdb.help-destination}")
	private String helpDestination;

	@Value("${mcdb.help-url}")
	private String helpUrl;

	@Value("${mcdb.form-file}")
	private String formFile;

	@Value("${mcdb.category-file}")
	private String categoryFile;

	@Value("${mcdb.alert-file}")
	private String alertFile;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getDataDirectory() {
		return dataDirectory;
	}

	public void setDataDirectory(String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}

	public String getHelpDestination() {
		return helpDestination;
	}

	public void setHelpDestination(String helpDestination) {
		this.helpDestination = helpDestination;
	}

	public String getHelpUrl() {
		return helpUrl;
	}

	public void setHelpUrl(String helpUrl) {
		this.helpUrl = helpUrl;
	}

	public String getFormFile() {
		return formFile;
	}

	public void setFormFile(String formFile) {
		this.formFile = formFile;
	}

	public String getCategoryFile() {
		return categoryFile;
	}

	public void setCategoryFile(String categoryFile) {
		this.categoryFile = categoryFile;
	}

	public String getAlertFile() {
		return alertFile;
	}

	public void setAlertFile(String alertFile) {
		this.alertFile = alertFile;
	}
}