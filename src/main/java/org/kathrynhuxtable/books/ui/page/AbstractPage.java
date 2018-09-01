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

import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.BACK;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.CANCEL;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.DELETE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.FORWARD;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.HOME;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.NEW;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.SAVE;

import java.io.IOException;
import java.util.List;

import org.kathrynhuxtable.books.AppResource;
import org.kathrynhuxtable.books.persistence.domain.DomainObject;
import org.kathrynhuxtable.books.service.BooksService;
import org.kathrynhuxtable.books.service.DocumentType;
import org.kathrynhuxtable.books.ui.control.Command;
import org.kathrynhuxtable.books.ui.controller.PageBrowserController;
import org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName;
import org.kathrynhuxtable.books.ui.controller.SearchDialogController;
import org.kathrynhuxtable.books.ui.util.NullCallback;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

/**
 * Base controller for showing a page.
 */
public abstract class AbstractPage {

	@Autowired
	protected AppResource appResource;

	@Autowired
	protected BooksService booksService;

	@Autowired
	protected PageBrowserController pageBrowser;

	@Autowired
	private SearchDialogController searchDialogController;

	private final DocumentType pageType;

	private final BooleanProperty newProperty = new SimpleBooleanProperty();
	private final BooleanProperty changedProperty = new SimpleBooleanProperty();
	private final ObjectProperty<Long> idProperty = new SimpleObjectProperty<>();
	private final StringProperty titleProperty = new SimpleStringProperty();

	public AbstractPage(DocumentType documentType) {
		this.pageType = documentType;
		setId(0L);
		setNew(true);
		setChanged(false);
	}

	public abstract String getSubTitle();

	protected abstract DomainObject getObject();

	protected abstract void clearObject(boolean deleteFlag);

	protected abstract void loadControls();

	protected abstract void loadObject();

	protected abstract boolean isChangedInternal();

	protected abstract void registerPageCommandUpdaters();

	public abstract void registerChangedListeners();

	public abstract void doBrowse();

	public abstract void doHelp();

	public void init() {
		if (getId() != 0L) {
			StringBuffer title = new StringBuffer();
			title.append(getDocumentType().getSingularDisplayName());

			String subTitle = getSubTitle();
			if (!subTitle.isEmpty()) {
				title.append(": ");
				title.append(subTitle);
			}

			setTitle(title.toString());

			loadControls();
			changedProperty().set(false);
		}
	}

	public ReadOnlyStringProperty titleProperty() {
		return titleProperty;
	}

	public DocumentType getDocumentType() {
		return pageType;
	}

	public Long getId() {
		return idProperty.get();
	}

	public void setId(Long id) {
		if (id == null) {
			id = 0L;
		}

		idProperty.set(id);

		StringBuffer title = new StringBuffer();
		title.append(pageType.getSingularDisplayName());
		if (id > 0) {
			title.append(":");
			title.append(id);
		}

		setTitle(title.toString());
		setNew(id == 0L);
	}

	public String getTitle() {
		return titleProperty.get();
	}

	public void setTitle(String title) {
		titleProperty.set(title);
	}

	public BooleanProperty newProperty() {
		return newProperty;
	}

	public boolean isNew() {
		return newProperty().get();
	}

	public void setNew(boolean empty) {
		this.newProperty().set(empty);
	}

	public BooleanProperty changedProperty() {
		return changedProperty;
	}

	public boolean isChanged() {
		return changedProperty().get();
	}

	public void setChanged(boolean changed) {
		this.changedProperty().set(changed);
	}

	protected Command getCommand(CommandName commandName) {
		return pageBrowser.getCommand(commandName);
	}

	public void doNew() {
		clearObject(false);
		loadControls();
		updateChangedStatus();
	}

	public void doCancel() {
		loadControls();
		updateChangedStatus();
	}

	public void doSave() {
		loadObject();
		updateChangedStatus();
	}

	public void doDelete() {
		clearObject(true);
		getCommand(BACK).execute();
	}

	protected void updateChangedStatus() {
		changedProperty().set(isChangedInternal());
	}

	protected <T extends DomainObject> boolean checkLists(List<T> a, List<T> b) {
		if (a == null && b == null) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		} else if (a.size() != b.size()) {
			return false;
		} else {
			for (int i = 0; i < a.size(); i++) {
				Long aId = a.get(i).getId();
				Long bId = b.get(i).getId();
				if (aId == null && bId == null) {
					continue;
				} else if (aId == null || aId == null && bId != null) {
					return false;
				} else if (!aId.equals(bId)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Registers an updater for a command's "possible" property.
	 * 
	 * @param commandName
	 *            the command name.
	 * @param property
	 *            the property to listen to.
	 * @param isPossible
	 *            a callback to set the "possible" property's value.
	 */
	protected <T> void registerCommandPossibleUpdater(CommandName commandName, ObservableValue<T> property, NullCallback<Boolean> isPossible) {
		property.addListener((observable, oldValue, newValue) -> {
			getCommand(commandName).setPossible(isPossible.call());
		});
		getCommand(commandName).setPossible(isPossible.call());
	}

	/**
	 * This method is equivalent to bind(ObjectBinding) as it would invoke updater immediately as well as on any change to
	 * changed property
	 * 
	 * @param updater
	 *            a method that updates when changed status changes
	 */
	void registerChangedUpdater(final Callback<Boolean, Void> updater) {
		changedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean pageInfo) -> {
			updater.call(pageInfo);
		});
		updater.call(changedProperty().get());
	}

	public void registerCommandUpdaters() {
		registerChangedUpdater(changed -> {
			getCommand(HOME).setPossible(!changed && !pageBrowser.isPastHistoryEmpty());
			getCommand(FORWARD).setPossible(!changed && !pageBrowser.isFutureHistoryEmpty());
			getCommand(BACK).setPossible(!changed && !pageBrowser.isPastHistoryEmpty());

			getCommand(NEW).setPossible(!changed && !isNew());
			getCommand(CANCEL).setPossible(changed);
			getCommand(SAVE).setPossible(changed);
			getCommand(DELETE).setPossible(!changed && !isNew());

			return null;
		});

		pageBrowser.deactivateIrrelevantControls(getDocumentType());
		registerPageCommandUpdaters();
	}

	public <T extends DomainObject> void addDoubleClickActionListener(ListView<T> listView) {
		listView.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && event.getTarget() instanceof Text) {
				if (isChangedInternal()) {
					appResource.getAlertSound().play();
					return;
				}
				T selectedItem = listView.getSelectionModel().getSelectedItem();
				if (selectedItem != null) {
					pageBrowser.goToPage(selectedItem.getDocumentType(), selectedItem.getId());
				} else {
					pageBrowser.playAlertSound();
				}
			}
		});
	}

	public void showSearchDialog(Window owner, DocumentType documentType, Callback<DomainObject, Void> callback) {
		try {
			final FXMLLoader loader = new FXMLLoader(SearchDialogController.class.getResource("/fxml/search-dialog.fxml"));
			searchDialogController.setDocumentType(documentType);
			searchDialogController.setCallback(callback);
			loader.setController(searchDialogController);
			final Parent root = loader.load();
			searchDialogController.addListeners();
			final Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Search " + documentType.getPluralDisplayName());
			stage.initOwner(owner);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
