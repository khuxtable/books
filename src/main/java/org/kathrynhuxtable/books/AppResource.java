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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.media.AudioClip;

/**
 * Deliver cached application resources.
 */
@Component
public class AppResource {

	@Autowired
	private YAMLConfig myConfig;

	private List<String> forms;
	private List<String> categories;
	private AudioClip alertSound;

	public List<String> getForms() {
		if (forms == null) {
			forms = parseFile(myConfig.getFormFile());
		}
		return forms;
	}

	public List<String> getCategories() {
		if (categories == null) {
			categories = parseFile(myConfig.getCategoryFile());
		}
		return categories;
	}

	public AudioClip getAlertSound() {
		if (alertSound == null) {
			alertSound = new AudioClip("file://" + myConfig.getAlertFile());
		}

		return alertSound;
	}

	/**
	 * Read a file of text values.
	 * 
	 * @param file
	 *            the file.
	 * 
	 * @return a List of String values.
	 */
	private List<String> parseFile(String file) {
		List<String> list = new ArrayList<>();
		Path path = Paths.get(file);
		if (path.toFile().exists()) {
			try {
				for (String line : Files.readAllLines(path)) {
					String value = line.trim();
					if (!value.isEmpty() && !line.startsWith("#")) {
						list.add(value);
					}
				}
			} catch (IOException e) {
				// Ignore exception.
			}
		}
		return list;
	}
}
