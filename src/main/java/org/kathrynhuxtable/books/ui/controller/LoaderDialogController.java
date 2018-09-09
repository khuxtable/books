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

import java.util.List;

import org.kathrynhuxtable.books.service.DataLoaderResult;
import org.kathrynhuxtable.books.service.DataLoaderResult.DLRStatus;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

@Component
public class LoaderDialogController {

	@FXML
	private Label totals;

	@FXML
	private ListView<DataLoaderResult> resultsList;

	@FXML
	private Button buttonClose;

	public void initialize() {
		PseudoClass errorClass = PseudoClass.getPseudoClass("error");

		resultsList.setCellFactory(lv -> new ListCell<DataLoaderResult>() {
			@Override
			protected void updateItem(DataLoaderResult item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? null : item.text);
				pseudoClassStateChanged(errorClass, !empty && item.status == DLRStatus.ERROR);
			}
		});
	}

	@FXML
	public void closeResults() {
		buttonClose.getScene().getWindow().hide();
	}

	public void setResults(List<DataLoaderResult> results) {
		resultsList.setItems(FXCollections.observableArrayList(results));
		int success = 0;
		int error = 0;
		for (DataLoaderResult result : results) {
			if (result.status == DLRStatus.ERROR)
				error++;
			else
				success++;
		}
		totals.setText("Imports: " + success + "\nErrors: " + error);
	}
}