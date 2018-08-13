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
package org.kathrynhuxtable.books.ui.controller;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

@Component
public class HelpDialogController {

	@FXML
	private WebView helpWebView;

	@FXML
	private Button buttonClose;

	@FXML
	public void closeAbout() {
		buttonClose.getScene().getWindow().hide();
	}

	public void initialize() {
		String helpText = null;
		try (InputStream stream = getClass().getResourceAsStream("/help/mcdb.html")) {
			try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name())) {
				helpText = scanner.useDelimiter("\\A").next();
			}
		} catch (Exception e) {
			helpText = "Error reading help: " + e.getMessage();
		}
		final WebEngine webEngine = helpWebView.getEngine();
		webEngine.loadContent(helpText);
	}
}