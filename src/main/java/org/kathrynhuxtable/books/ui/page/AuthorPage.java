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
package org.kathrynhuxtable.books.ui.page;

import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.AUTHOR_TITLE_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.BROWSE;

import java.util.ArrayList;
import java.util.Optional;
import java.util.TreeSet;

import org.kathrynhuxtable.books.persistence.domain.Author;
import org.kathrynhuxtable.books.persistence.domain.DomainObject;
import org.kathrynhuxtable.books.persistence.domain.Title;
import org.kathrynhuxtable.books.service.DocumentType;
import org.kathrynhuxtable.books.service.TitlePropertyComparator.TitleComparator;
import org.kathrynhuxtable.books.ui.util.NullCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

@Component
public class AuthorPage extends AbstractPage {

	@FXML
	private TextField lastName;
	@FXML
	private TextField firstName;
	@FXML
	private TextField nationality;
	@FXML
	private TextField birthPlace;
	@FXML
	private DatePicker birthDate;
	@FXML
	private DatePicker deathDate;
	@FXML
	private TextArea note;
	@FXML
	private ListView<Title> titles;

	@FXML
	private MenuItem menuAuthorTitleGoto;

	private ObservableList<Title> observableTitles;

	private Author author;

	public AuthorPage() {
		super(DocumentType.AUTHOR);
	}

	public void initialize() {
		addDoubleClickActionListener(titles);
	}

	@Override
	public String getSubTitle() {
		retrieveItem();
		if (author != null) {
			return author.getName();
		} else {
			return "";
		}
	}

	@Override
	public void loadControls() {
		retrieveItem();

		if (author != null) {
			lastName.setText(author.getLastName());
			firstName.setText(author.getFirstName());
			nationality.setText(author.getNationality());
			birthPlace.setText(author.getBirthPlace());
			birthDate.setValue(author.getBirthDate());
			deathDate.setValue(author.getDeathDate());
			note.setText(author.getNote());

			observableTitles = FXCollections.observableArrayList(new ArrayList<>(author.getTitles()));
			titles.setItems(new SortedList<Title>(observableTitles, new TitleComparator()));
		} else {
			lastName.setText("");
			firstName.setText("");
			nationality.setText("");
			birthPlace.setText("");
			birthDate.setValue(null);
			deathDate.setValue(null);
			note.setText("");

			titles.getItems().clear();
		}
	}

	private void retrieveItem() {
		Long id = getId();
		if (id > 0) {
			Optional<Author> optAuthor = booksService.getAuthorById(id);
			if (optAuthor.isPresent()) {
				author = optAuthor.get();
				return;
			}
		}
		author = null;
	}

	public void registerChangedListeners() {
		lastName.textProperty().addListener((observable, oldDate, newDate) -> changedProperty().set(isChangedInternal()));
		firstName.textProperty().addListener((observable, oldDate, newDate) -> changedProperty().set(isChangedInternal()));
		nationality.textProperty().addListener((observable, oldDate, newDate) -> changedProperty().set(isChangedInternal()));
		birthPlace.textProperty().addListener((observable, oldDate, newDate) -> changedProperty().set(isChangedInternal()));
		birthDate.valueProperty().addListener((observable, oldDate, newDate) -> changedProperty().set(isChangedInternal()));
		deathDate.valueProperty().addListener((observable, oldDate, newDate) -> changedProperty().set(isChangedInternal()));
		note.textProperty().addListener((observable, oldDate, newDate) -> changedProperty().set(isChangedInternal()));

		pageBrowser.bindCommand(menuAuthorTitleGoto, AUTHOR_TITLE_GOTO);
	}

	@Override
	protected void clearObject(boolean deleteFlag) {
		if (deleteFlag) {
			booksService.delete(author);
		}
		author = null;
	}

	@Override
	protected DomainObject getObject() {
		return author;
	}

	@Override
	public void loadObject() {
		if (author == null) {
			author = new Author();
		}

		author.setLastName(lastName.getText());
		author.setFirstName(firstName.getText());
		author.setNationality(nationality.getText());
		author.setBirthPlace(birthPlace.getText());
		author.setBirthDate(birthDate.getValue());
		author.setDeathDate(deathDate.getValue());
		author.setNote(note.getText());
		author.setTitles(new TreeSet<Title>(titles.getItems()));

		author = booksService.save(author);
	}

	public boolean isChangedInternal() {
		if (author != null) {
			if (!lastName.getText().equals(author.getLastName())) {
				return true;
			}
			if (!firstName.getText().equals(author.getFirstName())) {
				return true;
			}
			if (!nationality.getText().equals(author.getNationality())) {
				return true;
			}
			if (!birthPlace.getText().equals(author.getBirthPlace())) {
				return true;
			}
			if (birthDate.getValue() == null && author.getBirthDate() != null) {
				return true;
			} else if (birthDate.getValue() != null && !birthDate.getValue().equals(author.getBirthDate())) {
				return true;
			}
			if (deathDate.getValue() == null && author.getDeathDate() != null) {
				return true;
			} else if (deathDate.getValue() != null && !deathDate.getValue().equals(author.getDeathDate())) {
				return true;
			}
			if (StringUtils.isEmpty(note.getText()) && !StringUtils.isEmpty(author.getNote())) {
				return true;
			} else if (!StringUtils.isEmpty(note.getText()) && !note.getText().equals(author.getNote())) {
				return true;
			}
			if (!checkLists(titles.getItems(), new ArrayList<>(author.getTitles()))) {
				return true;
			}
		} else {
			if (!StringUtils.isEmpty(lastName.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(lastName.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(firstName.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(nationality.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(birthPlace.getText())) {
				return true;
			}
			if (birthDate.getValue() != null) {
				return true;
			}
			if (deathDate.getValue() != null) {
				return true;
			}
			if (!StringUtils.isEmpty(note.getText())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void registerPageCommandUpdaters() {
//		titles.getSelectionModel().selectedItemProperty().isNull().not().and(changedProperty());
		NullCallback<Boolean> titleGotoPossible = () -> (!titles.getSelectionModel().isEmpty() && !isChangedInternal());
		registerCommandPossibleUpdater(AUTHOR_TITLE_GOTO, titles.getSelectionModel().selectedItemProperty(), titleGotoPossible);
		registerCommandPossibleUpdater(AUTHOR_TITLE_GOTO, changedProperty(), titleGotoPossible);

		NullCallback<Boolean> browsePossible = () -> !isChangedInternal();
		registerCommandPossibleUpdater(BROWSE, changedProperty(), browsePossible);
	}

	public void doTitleGoto() {
		pageBrowser.goToPage(DocumentType.TITLE, titles.getSelectionModel().getSelectedItem().getId());
	}

	@Override
	public void doBrowse() {
		showSearchDialog(lastName.getScene().getWindow(), DocumentType.AUTHOR, author -> {
			if (author != null) {
				pageBrowser.goToPage(DocumentType.AUTHOR, author.getId());
			}
			return null;
		});
	}

	@Override
	public void doHelp() {
		// TODO Auto-generated method stub
	}
}
