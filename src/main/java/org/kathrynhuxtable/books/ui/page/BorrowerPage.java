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

import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.BORROWER_VOLUME_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.BROWSE;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import org.kathrynhuxtable.books.persistence.domain.Borrower;
import org.kathrynhuxtable.books.persistence.domain.DomainObject;
import org.kathrynhuxtable.books.persistence.domain.Volume;
import org.kathrynhuxtable.books.service.DocumentType;
import org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName;
import org.kathrynhuxtable.books.ui.util.NullCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

@Component
public class BorrowerPage extends AbstractPage {

	@FXML
	private TextField lastName;
	@FXML
	private TextField firstName;
	@FXML
	private TextField checkOutDate;
	@FXML
	private TextArea note;
	@FXML
	private ListView<Volume> volumes;

	@FXML
	private MenuItem menuBorrowerVolumeGoto;

	private Borrower borrower;

	public BorrowerPage() {
		super(DocumentType.BORROWER);
	}

	public void initialize() {
		addDoubleClickActionListener(volumes);
	}

	@Override
	public String getSubTitle() {
		retrieveItem();
		if (borrower != null) {
			return borrower.getName();
		} else {
			return "";
		}
	}

	@Override
	public void loadControls() {
		retrieveItem();

		if (borrower != null) {
			lastName.setText(borrower.getLastName());
			firstName.setText(borrower.getFirstName());
			checkOutDate.setText(borrower.getCheckOutDate());
			note.setText(borrower.getNote());
			volumes.getItems().clear();
			volumes.getItems().addAll(borrower.getVolumes());
		} else {
			lastName.setText("");
			firstName.setText("");
			checkOutDate.setText("");
			note.setText("");
			volumes.getItems().clear();
		}
	}

	private void retrieveItem() {
		Long id = getId();
		if (id > 0) {
			Optional<Borrower> optBorrower = booksService.getBorrowerById(id);
			if (optBorrower.isPresent()) {
				borrower = optBorrower.get();
				return;
			}
		}
		borrower = null;
	}

	public void registerChangedListeners() {
		lastName.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		firstName.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		checkOutDate.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());
		note.textProperty().addListener((observable, oldDate, newDate) -> updateChangedStatus());

		pageBrowser.bindCommand(menuBorrowerVolumeGoto, CommandName.BORROWER_VOLUME_GOTO);
	}

	@Override
	protected void clearObject(boolean deleteFlag) {
		if (deleteFlag) {
			booksService.delete(borrower);
		}
		borrower = null;
		setId(null);
	}

	@Override
	protected DomainObject getObject() {
		return borrower;
	}

	@Override
	protected void setObject(DomainObject object) {
		borrower = (Borrower) object;
	}

	@Override
	protected void loadObject() {
		if (borrower == null) {
			borrower = new Borrower();
		}

		borrower.setLastName(lastName.getText());
		borrower.setFirstName(firstName.getText());
		borrower.setCheckOutDate(checkOutDate.getText());
		borrower.setNote(note.getText());
		borrower.setVolumes(new ArrayList<>(volumes.getItems()));
	}

	@Override
	protected boolean isChangedInternal() {
		if (borrower != null) {
			if (!Objects.equals(lastName.getText(), borrower.getLastName())) {
				return true;
			}
			if (!Objects.equals(firstName.getText(), borrower.getFirstName())) {
				return true;
			}
			if (!Objects.equals(checkOutDate.getText(), borrower.getCheckOutDate())) {
				return true;
			}
			if (!Objects.equals(note.getText(), borrower.getNote())) {
				return true;
			}
			if (!checkLists(volumes.getItems(), new ArrayList<>(borrower.getVolumes()))) {
				return true;
			}
		} else {
			if (!StringUtils.isEmpty(lastName.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(firstName.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(checkOutDate.getText())) {
				return true;
			}
			if (!StringUtils.isEmpty(note.getText())) {
				return true;
			}
			if (volumes.getItems().size() > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void registerPageCommandUpdaters() {
		NullCallback<Boolean> volumeGotoPossible = () -> !volumes.getSelectionModel().isEmpty() && !isChangedInternal();

		registerCommandPossibleUpdater(BORROWER_VOLUME_GOTO, volumes.getSelectionModel().selectedItemProperty(), volumeGotoPossible);
		registerCommandPossibleUpdater(BORROWER_VOLUME_GOTO, changedProperty(), volumeGotoPossible);

		NullCallback<Boolean> browsePossible = () -> !isChangedInternal();
		registerCommandPossibleUpdater(BROWSE, changedProperty(), browsePossible);
	}

	public void doVolumeGoto() {
		pageBrowser.goToPage(DocumentType.VOLUME, volumes.getSelectionModel().getSelectedItem().getId());
	}

	@Override
	public void doBrowse() {
		showSearchDialog(lastName.getScene().getWindow(), DocumentType.BORROWER, borrower -> {
			if (borrower != null) {
				pageBrowser.goToPage(DocumentType.BORROWER, borrower.getId());
			}
			return null;
		});
	}

	@Override
	public void doHelp() {
		// TODO Auto-generated method stub
	}
}
