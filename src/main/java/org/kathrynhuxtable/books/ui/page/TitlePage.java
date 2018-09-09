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

import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.BROWSE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_AUTHOR_ADD;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_AUTHOR_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_AUTHOR_MOVE_DOWN;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_AUTHOR_MOVE_UP;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_AUTHOR_REMOVE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_COLLECTION_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_CONTENT_ADD;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_CONTENT_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_CONTENT_REMOVE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_VOLUME_ADD;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_VOLUME_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_VOLUME_REMOVE;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;

import org.kathrynhuxtable.books.AppResource;
import org.kathrynhuxtable.books.persistence.domain.Author;
import org.kathrynhuxtable.books.persistence.domain.DomainObject;
import org.kathrynhuxtable.books.persistence.domain.Title;
import org.kathrynhuxtable.books.persistence.domain.Volume;
import org.kathrynhuxtable.books.service.DocumentType;
import org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName;
import org.kathrynhuxtable.books.ui.util.NullCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

@Component
public class TitlePage extends AbstractPage {

	@Autowired
	private AppResource appResource;

	@FXML
	private TextField title;
	@FXML
	private ListView<Author> authors;
	@FXML
	private ComboBox<String> category;
	@FXML
	private ComboBox<String> form;
	@FXML
	private TextField published;
	@FXML
	private CheckBox haveRead;
	@FXML
	private ListView<Volume> volumes;
	@FXML
	private TextArea note;
	@FXML
	private ListView<Title> contents;
	@FXML
	private ListView<Title> collectedIn;

	@FXML
	private MenuItem menuTitleAuthorMoveUp;
	@FXML
	private MenuItem menuTitleAuthorMoveDown;
	@FXML
	private MenuItem menuTitleAuthorAdd;
	@FXML
	private MenuItem menuTitleAuthorRemove;
	@FXML
	private MenuItem menuTitleAuthorGoto;
	@FXML
	private MenuItem menuTitleVolumeAdd;
	@FXML
	private MenuItem menuTitleVolumeRemove;
	@FXML
	private MenuItem menuTitleVolumeGoto;
	@FXML
	private MenuItem menuTitleContentMoveUp;
	@FXML
	private MenuItem menuTitleContentMoveDown;
	@FXML
	private MenuItem menuTitleContentAdd;
	@FXML
	private MenuItem menuTitleContentRemove;
	@FXML
	private MenuItem menuTitleContentGoto;
	@FXML
	private MenuItem menuTitleCollectionGoto;

	private Title titleObj;

	public TitlePage() {
		super(DocumentType.TITLE);
	}

	public void initialize() {
		addDoubleClickActionListener(authors);
		addDoubleClickActionListener(volumes);
		addDoubleClickActionListener(contents);
		addDoubleClickActionListener(collectedIn);

		category.setItems(FXCollections.observableList(appResource.getCategories()));
		form.setItems(FXCollections.observableList(appResource.getForms()));
	}

	@Override
	public String getSubTitle() {
		retrieveItem();
		if (titleObj != null) {
			return titleObj.getTitle();
		} else {
			return "";
		}
	}

	@Override
	public void loadControls() {
		retrieveItem();

		if (titleObj != null) {
			title.setText(titleObj.getTitle());
			authors.getItems().clear();
			authors.getItems().addAll(titleObj.getAuthors());
			category.setValue(titleObj.getCategory());
			form.setValue(titleObj.getForm());
			published.setText(titleObj.getPublicationYear() == 0 ? "" : Integer.toString(titleObj.getPublicationYear()));
			haveRead.setSelected(titleObj.isHaveRead());
			volumes.getItems().clear();
			volumes.getItems().addAll(titleObj.getVolumes());
			note.setText(titleObj.getNote());
			contents.getItems().clear();
			contents.getItems().addAll(titleObj.getContents());
			collectedIn.getItems().clear();
			collectedIn.getItems().addAll(titleObj.getCollectedIn());
		} else {
			title.setText("");
			authors.getItems().clear();
			category.setValue("");
			form.setValue("");
			published.setText("");
			haveRead.setSelected(false);
			volumes.getItems().clear();
			note.setText("");
			contents.getItems().clear();
			collectedIn.getItems().clear();
		}
	}

	private void retrieveItem() {
		Long id = getId();
		if (id > 0) {
			Optional<Title> optTitle = booksService.getTitleById(id);
			if (optTitle.isPresent()) {
				titleObj = optTitle.get();
				return;
			}
		}
		titleObj = null;
	}

	public void registerChangedListeners() {
		title.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		category.valueProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		form.valueProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		published.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		haveRead.selectedProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		note.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());

		pageBrowser.bindCommand(menuTitleAuthorMoveUp, CommandName.TITLE_AUTHOR_MOVE_UP);
		pageBrowser.bindCommand(menuTitleAuthorMoveDown, CommandName.TITLE_AUTHOR_MOVE_DOWN);
		pageBrowser.bindCommand(menuTitleAuthorAdd, CommandName.TITLE_AUTHOR_ADD);
		pageBrowser.bindCommand(menuTitleAuthorRemove, CommandName.TITLE_AUTHOR_REMOVE);
		pageBrowser.bindCommand(menuTitleAuthorGoto, CommandName.TITLE_AUTHOR_GOTO);
		pageBrowser.bindCommand(menuTitleVolumeAdd, CommandName.TITLE_VOLUME_ADD);
		pageBrowser.bindCommand(menuTitleVolumeRemove, CommandName.TITLE_VOLUME_REMOVE);
		pageBrowser.bindCommand(menuTitleVolumeGoto, CommandName.TITLE_VOLUME_GOTO);
		pageBrowser.bindCommand(menuTitleContentMoveUp, CommandName.TITLE_CONTENT_MOVE_UP);
		pageBrowser.bindCommand(menuTitleContentMoveDown, CommandName.TITLE_CONTENT_MOVE_DOWN);
		pageBrowser.bindCommand(menuTitleContentAdd, CommandName.TITLE_CONTENT_ADD);
		pageBrowser.bindCommand(menuTitleContentRemove, CommandName.TITLE_CONTENT_REMOVE);
		pageBrowser.bindCommand(menuTitleContentGoto, CommandName.TITLE_CONTENT_GOTO);
		pageBrowser.bindCommand(menuTitleCollectionGoto, CommandName.TITLE_COLLECTION_GOTO);
	}

	@Override
	protected DomainObject getObject() {
		return titleObj;
	}

	@Override
	protected void clearObject(boolean deleteFlag) {
		if (deleteFlag) {
			booksService.delete(getObject());
		}

		titleObj = null;
		setId(null);
	}

	@Override
	protected void loadObject() {
		if (titleObj == null) {
			titleObj = new Title();
		}

		titleObj.setTitle(title.getText());
		titleObj.setAuthors(new ArrayList<>(authors.getItems()));
		titleObj.setCategory(category.getValue());
		titleObj.setForm(form.getValue());
		titleObj.setPublicationYear(published.getText().isEmpty() ? 0 : Integer.parseInt(published.getText()));
		titleObj.setHaveRead(haveRead.isSelected());
		titleObj.setVolumes(new TreeSet<>(volumes.getItems()));
		titleObj.setNote(note.getText());
		titleObj.setContents(new ArrayList<>(contents.getItems()));

		titleObj = booksService.save(titleObj);
	}

	@Override
	protected boolean isChangedInternal() {
		if (titleObj != null) {
			if (!Objects.equals(title.getText(), titleObj.getTitle())) {
				return true;
			}
			if (!checkLists(authors.getItems(), titleObj.getAuthors())) {
				return true;
			}
			if (!Objects.equals(category.getValue(), titleObj.getCategory())) {
				return true;
			}
			if (!Objects.equals(form.getValue(), titleObj.getForm())) {
				return true;
			}
			if (!published.getText().equals(titleObj.getPublicationYear() == 0 ? "" : Integer.toString(titleObj.getPublicationYear()))) {
				return true;
			}
			if (haveRead.isSelected() != titleObj.isHaveRead()) {
				return true;
			}
			if (!checkLists(volumes.getItems(), new ArrayList<>(titleObj.getVolumes()))) {
				return true;
			}
			if (!Objects.equals(note.getText(), titleObj.getNote())) {
				return true;
			}
			if (!checkLists(contents.getItems(), titleObj.getContents())) {
				return true;
			}
		} else {
			if (!StringUtils.isEmpty(title.getText())) {
				return true;
			}
			if (authors.getItems().size() > 0) {
				return true;
			}
			if (!StringUtils.isEmpty(category.getValue())) {
				return true;
			}
			if (!StringUtils.isEmpty(form.getValue())) {
				return true;
			}
			if (!StringUtils.isEmpty(published.getText())) {
				return true;
			}
			if (haveRead.isSelected()) {
				return true;
			}
			if (volumes.getItems().size() > 0) {
				return true;
			}
			if (!StringUtils.isEmpty(note.getText())) {
				return true;
			}
			if (contents.getItems().size() > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void registerPageCommandUpdaters() {
		// Create callbacks and register them for authors commands
		NullCallback<Boolean> authorRemovePossible = () -> !authors.getSelectionModel().isEmpty();
		NullCallback<Boolean> authorGotoPossible = () -> !authors.getSelectionModel().isEmpty() && !isChanged();
		NullCallback<Boolean> authorMoveDownPossible = () -> !authors.getSelectionModel().isEmpty()
				&& authors.getSelectionModel().getSelectedIndex() < authors.getItems().size() - 1;
		NullCallback<Boolean> authorMoveUpPossible = () -> !authors.getSelectionModel().isEmpty() && authors.getSelectionModel().getSelectedIndex() > 0;

		registerCommandPossibleUpdater(TITLE_AUTHOR_MOVE_UP, authors.getSelectionModel().selectedItemProperty(), authorMoveUpPossible);
		registerCommandPossibleUpdater(TITLE_AUTHOR_MOVE_DOWN, authors.getSelectionModel().selectedItemProperty(), authorMoveDownPossible);
		registerCommandPossibleUpdater(TITLE_AUTHOR_REMOVE, authors.getSelectionModel().selectedItemProperty(), authorRemovePossible);
		registerCommandPossibleUpdater(TITLE_AUTHOR_GOTO, authors.getSelectionModel().selectedItemProperty(), authorGotoPossible);
		registerCommandPossibleUpdater(TITLE_AUTHOR_GOTO, changedProperty(), authorGotoPossible);
		getCommand(TITLE_AUTHOR_ADD).setPossible(true);

		// Create callbacks and register them for volumes commands
		NullCallback<Boolean> titleVolumeRemovePossible = () -> !volumes.getSelectionModel().isEmpty();
		NullCallback<Boolean> volumeGotoPossible = () -> !volumes.getSelectionModel().isEmpty() && !isChangedInternal();

		registerCommandPossibleUpdater(TITLE_VOLUME_REMOVE, volumes.getSelectionModel().selectedItemProperty(), titleVolumeRemovePossible);
		registerCommandPossibleUpdater(TITLE_VOLUME_GOTO, volumes.getSelectionModel().selectedItemProperty(), volumeGotoPossible);
		registerCommandPossibleUpdater(TITLE_VOLUME_GOTO, changedProperty(), volumeGotoPossible);
		getCommand(TITLE_VOLUME_ADD).setPossible(true);

		// Create callbacks and register them for contents commands
		NullCallback<Boolean> titleContentMoveUpPossible = () -> !contents.getSelectionModel().isEmpty() && contents.getSelectionModel().getSelectedIndex() > 0;
		NullCallback<Boolean> titleContentMoveDownPossible = () -> !contents.getSelectionModel().isEmpty()
				&& contents.getSelectionModel().getSelectedIndex() < contents.getItems().size() - 1;
		NullCallback<Boolean> titleContentRemovePossible = () -> !contents.getSelectionModel().isEmpty();
		NullCallback<Boolean> contentChangedGotoPossible = () -> !contents.getSelectionModel().isEmpty() && !isChangedInternal();

		registerCommandPossibleUpdater(CommandName.TITLE_CONTENT_MOVE_UP, contents.getSelectionModel().selectedItemProperty(), titleContentMoveUpPossible);
		registerCommandPossibleUpdater(CommandName.TITLE_CONTENT_MOVE_DOWN, contents.getSelectionModel().selectedItemProperty(), titleContentMoveDownPossible);
		getCommand(TITLE_CONTENT_ADD).setPossible(true);
		registerCommandPossibleUpdater(TITLE_CONTENT_REMOVE, contents.getSelectionModel().selectedItemProperty(), titleContentRemovePossible);
		registerCommandPossibleUpdater(TITLE_CONTENT_GOTO, contents.getSelectionModel().selectedItemProperty(), contentChangedGotoPossible);
		registerCommandPossibleUpdater(TITLE_CONTENT_GOTO, changedProperty(), contentChangedGotoPossible);

		// Create callbacks and register for collections commands
		NullCallback<Boolean> collectionGotoPossible = () -> !collectedIn.getSelectionModel().isEmpty() && !isChangedInternal();

		registerCommandPossibleUpdater(TITLE_COLLECTION_GOTO, collectedIn.getSelectionModel().selectedItemProperty(), collectionGotoPossible);
		registerCommandPossibleUpdater(TITLE_COLLECTION_GOTO, changedProperty(), collectionGotoPossible);

		NullCallback<Boolean> browsePossible = () -> !isChangedInternal();
		registerCommandPossibleUpdater(BROWSE, changedProperty(), browsePossible);
	}

	public void doAuthorMoveUp() {
		if (authors.getSelectionModel().getSelectedIndices().size() == 1) {
			int index = authors.getSelectionModel().getSelectedIndex();
			Author author = authors.getSelectionModel().getSelectedItem();
			authors.getItems().remove(index);
			authors.getItems().add(index - 1, author);
			authors.getSelectionModel().select(index - 1);
			updateChangedStatus();
		}
	}

	public void doAuthorMoveDown() {
		if (authors.getSelectionModel().getSelectedIndices().size() == 1) {
			int index = authors.getSelectionModel().getSelectedIndex();
			Author author = authors.getSelectionModel().getSelectedItem();
			authors.getItems().remove(index);
			authors.getItems().add(index + 1, author);
			authors.getSelectionModel().select(index + 1);
			updateChangedStatus();
		}
	}

	public void doAuthorAdd() {
		showSearchDialog(title.getScene().getWindow(), DocumentType.AUTHOR, author -> {
			if (author != null) {
				authors.getItems().add((Author) author);
				updateChangedStatus();
			}
			return null;
		});
	}

	public void doAuthorRemove() {
		if (authors.getSelectionModel().getSelectedIndices().size() == 1) {
			authors.getItems().remove(authors.getSelectionModel().getSelectedIndex());
			updateChangedStatus();
		}
	}

	public void doAuthorGoto() {
		pageBrowser.goToPage(DocumentType.AUTHOR, authors.getSelectionModel().getSelectedItem().getId());
	}

	public void doVolumeAdd() {
		pageBrowser.gotoVolumesPage();

		VolumePage volumePage = pageBrowser.getVolumePage();
		volumePage.doNew();
		volumePage.setEntry(titleObj);
	}

	public void doVolumeRemove() {
		Volume volume = volumes.getSelectionModel().getSelectedItem();
		if (volume != null) {
			volumes.getItems().remove(volumes.getSelectionModel().getSelectedIndex());
			updateChangedStatus();
		}
	}

	public void doVolumeGoto() {
		pageBrowser.goToPage(DocumentType.VOLUME, volumes.getSelectionModel().getSelectedItem().getId());
	}

	public void doContentMoveUp() {
		if (contents.getSelectionModel().getSelectedIndices().size() == 1) {
			int index = contents.getSelectionModel().getSelectedIndex();
			Title title = contents.getSelectionModel().getSelectedItem();
			contents.getItems().remove(index);
			contents.getItems().add(index - 1, title);
			contents.getSelectionModel().select(index - 1);
			updateChangedStatus();
		}
	}

	public void doContentMoveDown() {
		if (contents.getSelectionModel().getSelectedIndices().size() == 1) {
			int index = contents.getSelectionModel().getSelectedIndex();
			Title title = contents.getSelectionModel().getSelectedItem();
			contents.getItems().remove(index);
			contents.getItems().add(index + 1, title);
			contents.getSelectionModel().select(index + 1);
			updateChangedStatus();
		}
	}

	public void doContentAdd() {
		showSearchDialog(title.getScene().getWindow(), DocumentType.TITLE, title -> {
			if (title != null) {
				contents.getItems().add((Title) title);
				updateChangedStatus();
			}
			return null;
		});
	}

	public void doContentRemove() {
		Title title = contents.getSelectionModel().getSelectedItem();
		if (title != null) {
			contents.getItems().remove(contents.getSelectionModel().getSelectedIndex());
			updateChangedStatus();
		}
	}

	public void doContentGoto() {
		pageBrowser.goToPage(DocumentType.TITLE, contents.getSelectionModel().getSelectedItem().getId());
	}

	public void doCollectionGoto() {
		pageBrowser.goToPage(DocumentType.TITLE, collectedIn.getSelectionModel().getSelectedItem().getId());
	}

	@Override
	public void doBrowse() {
		showSearchDialog(title.getScene().getWindow(), DocumentType.TITLE, title -> {
			if (title != null) {
				pageBrowser.goToPage(DocumentType.TITLE, title.getId());
			}
			return null;
		});
	}

	@Override
	public void doHelp() {
		// TODO
	}
}
