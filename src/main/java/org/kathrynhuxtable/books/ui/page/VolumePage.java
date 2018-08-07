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
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.VOLUME_BORROWER_CHECK_IN;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.VOLUME_BORROWER_CHECK_OUT;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.VOLUME_BORROWER_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.VOLUME_TITLE_GOTO;

import java.util.Objects;
import java.util.Optional;

import org.kathrynhuxtable.books.persistence.domain.Borrower;
import org.kathrynhuxtable.books.persistence.domain.DomainObject;
import org.kathrynhuxtable.books.persistence.domain.Title;
import org.kathrynhuxtable.books.persistence.domain.Volume;
import org.kathrynhuxtable.books.service.DocumentType;
import org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName;
import org.kathrynhuxtable.books.ui.util.NullCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

@Component
public class VolumePage extends AbstractPage {

	@FXML
	private TextField title;
	@FXML
	private TextField binding;
	@FXML
	private TextField publisher;
	@FXML
	private TextField published;
	@FXML
	private TextField isbn;
	@FXML
	private TextField libraryOfCongress;
	@FXML
	private TextField borrower;
	@FXML
	private TextArea note;

	@FXML
	private MenuItem menuVolumeTitleGoto;
	@FXML
	private MenuItem menuVolumeBorrowerCheckOut;
	@FXML
	private MenuItem menuVolumeBorrowerCheckIn;
	@FXML
	private MenuItem menuVolumeBorrowerGoto;

	private Volume volume;
	private Title entry;
	private Borrower checkOut;

	public VolumePage() {
		super(DocumentType.VOLUME);
	}

	public void setEntry(Title titleObj) {
		this.entry = titleObj;
		title.setText(entry.getTitle());
		updateChangedStatus();
	}

	public void initialize() {
		title.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
				if (volume != null && volume.getEntry() != null) {
					pageBrowser.goToPage(DocumentType.TITLE, volume.getEntry().getId());
				} else {
					pageBrowser.playAlertSound();
				}
			}
		});

		borrower.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
				if (volume != null && volume.getBorrower() != null) {
					pageBrowser.goToPage(DocumentType.BORROWER, volume.getBorrower().getId());
				} else {
					pageBrowser.playAlertSound();
				}
			}
		});
	}

	@Override
	public String getSubTitle() {
		retrieveItem();
		if (volume != null) {
			return volume.getEntry().getTitle();
		} else {
			return "";
		}
	}

	@Override
	public void loadControls() {
		retrieveItem();

		if (volume != null) {
			entry = volume.getEntry();
			title.setText(volume.getEntry().getTitle());
			binding.setText(volume.getBinding() == null ? "" : volume.getBinding());
			publisher.setText(volume.getPublisher() == null ? "" : volume.getPublisher());
			published.setText(volume.getPublicationDate() == null ? "" : volume.getPublicationDate());
			isbn.setText(volume.getIsbn() == null ? "" : volume.getIsbn());
			libraryOfCongress.setText(volume.getLibraryOfCongress() == null ? "" : volume.getLibraryOfCongress());
			borrower.setText(volume.getBorrower() == null ? "" : volume.getBorrower().getName());
			note.setText(volume.getNote() == null ? "" : volume.getNote());
			checkOut = volume.getBorrower();
		} else {
			entry = null;
			title.setText("");
			binding.setText("");
			publisher.setText("");
			published.setText("");
			isbn.setText("");
			libraryOfCongress.setText("");
			borrower.setText("");
			note.setText("");
			checkOut = null;
		}
	}

	private void retrieveItem() {
		Long id = getId();
		if (id > 0) {
			Optional<Volume> optVolume = booksService.getVolumeById(id);
			if (optVolume.isPresent()) {
				volume = optVolume.get();
				return;
			}
		}
		volume = null;
	}

	public void registerChangedListeners() {
		title.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		binding.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		publisher.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		published.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		isbn.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		libraryOfCongress.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());

		pageBrowser.bindCommand(menuVolumeBorrowerCheckIn, CommandName.VOLUME_BORROWER_CHECK_IN);
		pageBrowser.bindCommand(menuVolumeBorrowerCheckOut, CommandName.VOLUME_BORROWER_CHECK_OUT);
		pageBrowser.bindCommand(menuVolumeBorrowerGoto, CommandName.VOLUME_BORROWER_GOTO);
		pageBrowser.bindCommand(menuVolumeTitleGoto, CommandName.VOLUME_TITLE_GOTO);

	}

	@Override
	protected void clearObject(boolean deleteFlag) {
		if (deleteFlag) {
			booksService.delete(volume);
		}
		volume = null;
	}

	@Override
	protected DomainObject getObject() {
		return volume;
	}

	@Override
	protected void loadObject() {
		if (volume == null) {
			volume = new Volume();
			volume.setEntry(entry);
		}

		// Entry and Borrower must be set from the Titles and Borrowers pages.
		volume.setBinding(binding.getText());
		volume.setPublisher(publisher.getText());
		volume.setPublicationDate(published.getText());
		volume.setIsbn(isbn.getText());
		volume.setLibraryOfCongress(libraryOfCongress.getText());
		volume.setNote(note.getText());
		volume.setBorrower(checkOut);

		volume = booksService.save(volume);
	}

	@Override
	protected boolean isChangedInternal() {
		if (volume != null) {
			if (!title.getText().equals(volume.getEntry() == null ? volume : volume.getEntry().getTitle())) {
				return true;
			}
			if (!Objects.equals(binding.getText(), volume.getBinding())) {
				return true;
			}
			if (!Objects.equals(publisher.getText(), volume.getPublisher())) {
				return true;
			}
			if (!Objects.equals(published.getText(), volume.getPublicationDate())) {
				return true;
			}
			if (!Objects.equals(isbn.getText(), volume.getIsbn())) {
				return true;
			}
			if (!Objects.equals(libraryOfCongress.getText(), volume.getLibraryOfCongress())) {
				return true;
			}
			if (!Objects.equals(note.getText(), volume.getNote())) {
				return true;
			}
			if (!borrower.getText().equals(volume.getBorrower() == null ? "" : volume.getBorrower().getName())) {
				return true;
			}
		} else {
			if (!StringUtils.isEmpty(title.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(binding.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(publisher.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(published.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(isbn.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(libraryOfCongress.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(note.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(borrower.getText())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void registerPageCommandUpdaters() {
		NullCallback<Boolean> titleGotoPossible = () -> !isChangedInternal();
		NullCallback<Boolean> borrowerCheckInPossible = () -> !borrower.getText().isEmpty();
		NullCallback<Boolean> borrowerGotoPossible = () -> !borrower.getText().isEmpty() && !isChangedInternal();

		registerCommandPossibleUpdater(VOLUME_TITLE_GOTO, changedProperty(), titleGotoPossible);
		registerCommandPossibleUpdater(VOLUME_BORROWER_CHECK_IN, borrower.textProperty().isEmpty(), borrowerCheckInPossible);
		getCommand(VOLUME_BORROWER_CHECK_OUT).setPossible(true);
		registerCommandPossibleUpdater(VOLUME_BORROWER_GOTO, borrower.textProperty(), borrowerGotoPossible);
		registerCommandPossibleUpdater(VOLUME_BORROWER_GOTO, changedProperty(), borrowerGotoPossible);

		NullCallback<Boolean> browsePossible = () -> !isChangedInternal();
		registerCommandPossibleUpdater(BROWSE, changedProperty(), browsePossible);
	}

	public void doBorrowerCheckIn() {
		if (!StringUtils.isEmpty(borrower.getText())) {
			borrower.setText("");
			checkOut = null;
			updateChangedStatus();
		}
	}

	public void doBorrowerCheckOut() {
		showSearchDialog(title.getScene().getWindow(), DocumentType.BORROWER, checkOut -> {
			if (checkOut != null) {
				this.checkOut = (Borrower) checkOut;
				borrower.setText(this.checkOut.getName());
				updateChangedStatus();
			}
			return null;
		});
	}

	public void doBorrowerGoto() {
		pageBrowser.goToPage(DocumentType.BORROWER, volume.getBorrower().getId());
	}

	public void doTitleGoto() {
		pageBrowser.goToPage(DocumentType.TITLE, volume.getEntry().getId());
	}

	@Override
	public void doBrowse() {
		showSearchDialog(title.getScene().getWindow(), DocumentType.VOLUME, volume -> {
			if (volume != null) {
				pageBrowser.goToPage(DocumentType.VOLUME, volume.getId());
			}
			return null;
		});
	}

	@Override
	public void doHelp() {
		// TODO Auto-generated method stub
	}
}
