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

import org.kathrynhuxtable.books.persistence.domain.Author;
import org.kathrynhuxtable.books.persistence.domain.Borrower;
import org.kathrynhuxtable.books.persistence.domain.DomainObject;
import org.kathrynhuxtable.books.persistence.domain.Title;
import org.kathrynhuxtable.books.persistence.domain.Volume;
import org.kathrynhuxtable.books.service.BooksService;
import org.kathrynhuxtable.books.service.DocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.util.Callback;

@Component
public class SearchDialogController {

	@Autowired
	private BooksService booksService;

	@FXML
	private TextField searchText;

	@FXML
	private ListView<DomainObject> searchResults;

	private DocumentType documentType;
	private Callback<DomainObject, Void> callback;

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public void setCallback(Callback<DomainObject, Void> callback) {
		this.callback = callback;
	}

	@FXML
	public void submit() {
		if (searchResults.getItems().size() == 1) {
			searchText.getScene().getWindow().hide();
			callback.call(searchResults.getItems().get(0));
		} else {
			DomainObject selectedItem = searchResults.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				searchText.getScene().getWindow().hide();
				callback.call(selectedItem);
			}
		}
	}

	@FXML
	public void cancel() {
		searchText.getScene().getWindow().hide();
	}

	public void addListeners() {
		Platform.runLater(() -> {
			searchText.requestFocus();

			searchText.getScene().getWindow().addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
				if (KeyCode.ESCAPE == event.getCode()) {
					cancel();
				}
			});

			searchText.getScene().getWindow().addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
				if (KeyCode.ENTER == event.getCode()) {
					submit();
				}
			});
		});

		searchResults.setCellFactory(lv -> new ListCell<DomainObject>() {
			@Override
			protected void updateItem(DomainObject item, boolean empty) {
				super.updateItem(item, empty);
				setText(item == null ? null : item.toString() + " (" + item.getShortDescription() + ")");
			}
		});

		ChangeListener<String> textListener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
			switch (documentType) {
			case AUTHOR:
				List<Author> authors = booksService.findAuthorByName(newValue, false);
				if (authors == null || authors.size() == 0) {
					searchResults.getItems().clear();
				} else {
					searchResults.getItems().setAll(authors);
				}
				break;
			case TITLE:
				List<Title> titles = booksService.findTitleByName(newValue);
				if (titles == null || titles.size() == 0) {
					searchResults.getItems().clear();
				} else {
					searchResults.getItems().setAll(titles);
				}
				break;
			case VOLUME:
				List<Volume> volumes = booksService.findVolumeByName(newValue);
				if (volumes == null || volumes.size() == 0) {
					searchResults.getItems().clear();
				} else {
					searchResults.getItems().setAll(volumes);
				}
				break;
			case BORROWER:
				List<Borrower> borrowers = booksService.findCheckOutByName(newValue, false);
				if (borrowers == null || borrowers.size() == 0) {
					searchResults.getItems().clear();
				} else {
					searchResults.getItems().setAll(borrowers);
				}
				break;
			}
		};
		searchText.textProperty().addListener(textListener);
		textListener.changed(null, "", "");

		searchResults.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && event.getTarget() instanceof Text) {
				DomainObject selectedItem = searchResults.getSelectionModel().getSelectedItem();
				if (selectedItem != null) {
					callback.call(selectedItem);
					searchText.getScene().getWindow().hide();
				}
			}
		});
	}
}