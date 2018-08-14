package org.kathrynhuxtable.books;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YAMLConfig {

	@Value("${mcdb.help-location}")
	private String helpLocation;

	public String getHelpLocation() {
		return helpLocation;
	}

	public void setHelpLocation(String helpLocation) {
		this.helpLocation = helpLocation;
	}

}