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

import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.AUTHOR_TITLE_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.BACK;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.BORROWER_VOLUME_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.BROWSE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.CANCEL;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.DELETE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.FORWARD;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.GOTO_AUTHORS_PAGE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.GOTO_BORROWERS_PAGE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.GOTO_TITLES_PAGE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.GOTO_VOLUMES_PAGE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.HOME;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.NEW;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.SAVE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_AUTHOR_ADD;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_AUTHOR_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_AUTHOR_MOVE_DOWN;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_AUTHOR_MOVE_UP;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_AUTHOR_REMOVE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_COLLECTION_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_CONTENT_ADD;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_CONTENT_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_CONTENT_MOVE_DOWN;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_CONTENT_MOVE_UP;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_CONTENT_REMOVE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_VOLUME_ADD;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_VOLUME_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.TITLE_VOLUME_REMOVE;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.VOLUME_BORROWER_CHECK_IN;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.VOLUME_BORROWER_CHECK_OUT;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.VOLUME_BORROWER_GOTO;
import static org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName.VOLUME_TITLE_GOTO;

import java.util.LinkedList;
import java.util.Map;

import org.kathrynhuxtable.books.AppResource;
import org.kathrynhuxtable.books.service.DocumentType;
import org.kathrynhuxtable.books.ui.control.Command;
import org.kathrynhuxtable.books.ui.page.AbstractPage;
import org.kathrynhuxtable.books.ui.page.AuthorPage;
import org.kathrynhuxtable.books.ui.page.BorrowerPage;
import org.kathrynhuxtable.books.ui.page.TitlePage;
import org.kathrynhuxtable.books.ui.page.VolumePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

/**
 * Page navigation with history.
 */
@Component
public class PageBrowserController {

	Logger LOG = LoggerFactory.getLogger(PageBrowserController.class);

	@Autowired
	private AppResource appResource;
	@Autowired
	private MainController mainController;
	@Autowired
	private AuthorPage authorPage;
	@Autowired
	private TitlePage titlePage;
	@Autowired
	private VolumePage volumePage;
	@Autowired
	private BorrowerPage borrowerPage;

	@FXML
	private StackPane browser;
	@FXML
	private StackPane authorNode;
	@FXML
	private StackPane titleNode;
	@FXML
	private StackPane volumeNode;
	@FXML
	private StackPane borrowerNode;

	private LinkedList<PageHistory> pastHistory = new LinkedList<>();
	private LinkedList<PageHistory> futureHistory = new LinkedList<>();

	private StringProperty currentPageTitle = new SimpleStringProperty(null);
	private ObjectProperty<DocumentType> documentTypeProperty = new SimpleObjectProperty<>();
	private ObjectProperty<AbstractPage> currentPageProperty = new SimpleObjectProperty<>();

	private Map<CommandName, Command> commandMap = buildCommandMap();

	public void initialize() {
		// Set initial page.
		goToPageInternal(DocumentType.AUTHOR, 0L, false);
	}

	public ObjectProperty<DocumentType> documentTypeProperty() {
		return documentTypeProperty;
	}

	public ReadOnlyStringProperty currentPageTitleProperty() {
		return currentPageTitle;
	}

	public ObjectProperty<AbstractPage> currentPageProperty() {
		return currentPageProperty;
	}

	public AuthorPage getAuthorPage() {
		return (AuthorPage) currentPageProperty().get();
	}

	public TitlePage getTitlePage() {
		return (TitlePage) currentPageProperty().get();
	}

	public VolumePage getVolumePage() {
		return (VolumePage) currentPageProperty().get();
	}

	public BorrowerPage getBorrowerPage() {
		return (BorrowerPage) currentPageProperty().get();
	}

	public void gotoAuthorsPage() {
		if (currentPageProperty().get().isChanged()) {
			documentTypeProperty().set(currentPageProperty().get().getDocumentType());
			playAlertSound();
			return;
		}

		goToPage(DocumentType.AUTHOR, 0L);
	}

	public void gotoBorrowersPage() {
		if (currentPageProperty().get().isChanged()) {
			documentTypeProperty().set(currentPageProperty().get().getDocumentType());
			playAlertSound();
			return;
		}

		goToPage(DocumentType.BORROWER, 0L);
	}

	public void gotoTitlesPage() {
		if (currentPageProperty().get().isChanged()) {
			documentTypeProperty().set(currentPageProperty().get().getDocumentType());
			playAlertSound();
			return;
		}

		goToPage(DocumentType.TITLE, 0L);
	}

	public void gotoVolumesPage() {
		if (currentPageProperty().get().isChanged()) {
			documentTypeProperty().set(currentPageProperty().get().getDocumentType());
			playAlertSound();
			return;
		}

		goToPage(DocumentType.VOLUME, 0L);
	}

	public void playAlertSound() {
		appResource.getAlertSound().play();
	}

	private void doHome() {
		while (!isPastHistoryEmpty()) {
			doBack();
		}
	}

	private void doBack() {
		if (pastHistory.isEmpty()) {
			goToPageInternal(DocumentType.AUTHOR, 0L, false);
		} else {
			PageHistory pageHistory = pastHistory.pop();
			if (currentPageProperty().get() != null && currentPageProperty.get().getId() > 0) {
				futureHistory.push(new PageHistory(currentPageProperty().get()));
			}
			goToPageInternal(pageHistory.documentType, pageHistory.id, false);
		}
	}

	private void doForward() {
		PageHistory pageHistory = futureHistory.pop();
		if (currentPageProperty().get() != null && currentPageProperty.get().getId() > 0) {
			pastHistory.push(new PageHistory(currentPageProperty().get()));
		}
		goToPageInternal(pageHistory.documentType, pageHistory.id, false);
	}

	public void deactivateIrrelevantControls(DocumentType pageType) {
		mainController.setAuthorsMenuVisible(pageType == DocumentType.AUTHOR);
		if (pageType != DocumentType.AUTHOR) {
			getCommand(AUTHOR_TITLE_GOTO).setPossible(false);
		}

		mainController.setTitlesMenuVisible(pageType == DocumentType.TITLE);
		if (pageType != DocumentType.TITLE) {
			getCommand(TITLE_AUTHOR_MOVE_UP).setPossible(false);
			getCommand(TITLE_AUTHOR_MOVE_DOWN).setPossible(false);
			getCommand(TITLE_AUTHOR_ADD).setPossible(false);
			getCommand(TITLE_AUTHOR_REMOVE).setPossible(false);
			getCommand(TITLE_AUTHOR_GOTO).setPossible(false);
			getCommand(TITLE_VOLUME_ADD).setPossible(false);
			getCommand(TITLE_VOLUME_GOTO).setPossible(false);
			getCommand(TITLE_VOLUME_REMOVE).setPossible(false);
			getCommand(TITLE_CONTENT_MOVE_UP).setPossible(false);
			getCommand(TITLE_CONTENT_MOVE_DOWN).setPossible(false);
			getCommand(TITLE_CONTENT_ADD).setPossible(false);
			getCommand(TITLE_CONTENT_REMOVE).setPossible(false);
			getCommand(TITLE_CONTENT_GOTO).setPossible(false);
			getCommand(TITLE_COLLECTION_GOTO).setPossible(false);
		}

		mainController.setVolumesMenuVisible(pageType == DocumentType.VOLUME);
		if (pageType != DocumentType.VOLUME) {
			getCommand(VOLUME_BORROWER_CHECK_IN).setPossible(false);
			getCommand(VOLUME_BORROWER_CHECK_OUT).setPossible(false);
			getCommand(VOLUME_BORROWER_GOTO).setPossible(false);
			getCommand(VOLUME_TITLE_GOTO).setPossible(false);
		}

		mainController.setBorrowersMenuVisible(pageType == DocumentType.BORROWER);
		if (pageType != DocumentType.BORROWER) {
			getCommand(BORROWER_VOLUME_GOTO).setPossible(false);
		}
	}

	public void bindCommand(Button button, CommandName commandName) {
		getCommand(commandName).bind(button);
	}

	public void bindCommand(ToggleButton button, CommandName commandName) {
		getCommand(commandName).bind(button);
	}

	public void bindCommand(MenuItem menuItem, CommandName commandName) {
		getCommand(commandName).bind(menuItem);
	}

	public void goToPage(DocumentType documentType, Long id) {
		goToPageInternal(documentType, id, true);
	}

	private void goToPageInternal(DocumentType documentType, Long id, boolean updateHistory) {
		// get node for page
		if (documentType == null) {
			documentType = DocumentType.AUTHOR;
			id = 0L;
		}

		// update history
		if (updateHistory) {
			if (currentPageProperty().get() != null && currentPageProperty.get().getId() > 0) {
				pastHistory.push(new PageHistory(currentPageProperty().get()));
			}
			futureHistory.clear();
		}

		Node node = getNode(documentType);
		AbstractPage nextPage = getPageController(documentType);
		nextPage.setId(id);
		nextPage.init();
		nextPage.registerCommandUpdaters();
		nextPage.registerChangedListeners();

		// Set the page node to display.
		browser.getChildren().setAll(node);

		// update properties
		getCommand(HOME).setPossible(!isPastHistoryEmpty());
		getCommand(FORWARD).setPossible(!isFutureHistoryEmpty());
		getCommand(BACK).setPossible(!isPastHistoryEmpty());

		currentPageProperty.set(nextPage);
		currentPageTitle.bind(nextPage.titleProperty());
		documentTypeProperty.setValue(documentType);
	}

	private Node getNode(DocumentType documentType) {
		switch (documentType) {
		case AUTHOR:
			return authorNode;
		case TITLE:
			return titleNode;
		case VOLUME:
			return volumeNode;
		case BORROWER:
			return borrowerNode;
		default:
			return null;
		}
	}

	private AbstractPage getPageController(DocumentType documentType) {
		switch (documentType) {
		case AUTHOR:
			return authorPage;
		case TITLE:
			return titlePage;
		case VOLUME:
			return volumePage;
		case BORROWER:
			return borrowerPage;
		default:
			return null;
		}
	}

	public boolean isPastHistoryEmpty() {
		return pastHistory.isEmpty();
	}

	public boolean isFutureHistoryEmpty() {
		return futureHistory.isEmpty();
	}

	public Command getCommand(CommandName commandName) {
		return commandMap.get(commandName);
	}

	private Map<CommandName, Command> buildCommandMap() {
		return Command.builder().
// @formatter:off
				put(HOME,    e -> doHome()).
				put(BACK,    e -> doBack()).
				put(FORWARD, e -> doForward()).
				put(BROWSE,  e -> currentPageProperty().get().doBrowse()).

				put(GOTO_AUTHORS_PAGE,   e -> gotoAuthorsPage()).
				put(GOTO_BORROWERS_PAGE, e -> gotoBorrowersPage()).
				put(GOTO_TITLES_PAGE,    e -> gotoTitlesPage()).
				put(GOTO_VOLUMES_PAGE,   e -> gotoVolumesPage()).

				put(NEW,    e -> currentPageProperty().get().doNew()).
				put(CANCEL, e -> currentPageProperty().get().doCancel()).
				put(SAVE,   e -> currentPageProperty().get().doSave()).
				put(DELETE, e -> currentPageProperty().get().doDelete()).

				put(AUTHOR_TITLE_GOTO,         e -> getAuthorPage().doTitleGoto()).

				put(TITLE_AUTHOR_MOVE_UP,      e -> getTitlePage().doAuthorMoveUp()).
				put(TITLE_AUTHOR_MOVE_DOWN,    e -> getTitlePage().doAuthorMoveDown()).
				put(TITLE_AUTHOR_ADD,          e -> getTitlePage().doAuthorAdd()).
				put(TITLE_AUTHOR_REMOVE,       e -> getTitlePage().doAuthorRemove()).
				put(TITLE_AUTHOR_GOTO,         e -> getTitlePage().doAuthorGoto()).
				put(TITLE_VOLUME_REMOVE,       e -> getTitlePage().doVolumeRemove()).
				put(TITLE_VOLUME_ADD,          e -> getTitlePage().doVolumeAdd()).
				put(TITLE_VOLUME_GOTO,         e -> getTitlePage().doVolumeGoto()).
				put(TITLE_CONTENT_MOVE_UP,     e -> getTitlePage().doContentMoveUp()).
				put(TITLE_CONTENT_MOVE_DOWN,   e -> getTitlePage().doContentMoveDown()).
				put(TITLE_CONTENT_ADD,         e -> getTitlePage().doContentAdd()).
				put(TITLE_CONTENT_REMOVE,      e -> getTitlePage().doContentRemove()).
				put(TITLE_CONTENT_GOTO,        e -> getTitlePage().doContentGoto()).
				put(TITLE_COLLECTION_GOTO,     e -> getTitlePage().doCollectionGoto()).

				put(VOLUME_BORROWER_CHECK_IN,  e -> getVolumePage().doBorrowerCheckIn()).
				put(VOLUME_BORROWER_CHECK_OUT, e -> getVolumePage().doBorrowerCheckOut()).
				put(VOLUME_BORROWER_GOTO,      e -> getVolumePage().doBorrowerGoto()).
				put(VOLUME_TITLE_GOTO,         e -> getVolumePage().doTitleGoto()).

				put(BORROWER_VOLUME_GOTO,      e -> getBorrowerPage().doVolumeGoto()).
// @formatter:on
				build();
	}

	/**
	 * Save the type and id of the current page.
	 */
	private static class PageHistory {
		public final DocumentType documentType;
		public final Long id;

		public PageHistory(AbstractPage page) {
			this.documentType = page.getDocumentType();
			this.id = page.getId();
		}
	}

	/**
	 * Names of the commands implemented by buttons, menu items, context menu, and double clicks.
	 */
	public enum CommandName {
// @formatter:off
		HOME, BACK, FORWARD, BROWSE,
		
		GOTO_AUTHORS_PAGE, GOTO_BORROWERS_PAGE, GOTO_TITLES_PAGE, GOTO_VOLUMES_PAGE,

		NEW, CANCEL, SAVE, DELETE,

		AUTHOR_TITLE_GOTO,

		TITLE_AUTHOR_MOVE_UP, TITLE_AUTHOR_MOVE_DOWN, TITLE_AUTHOR_ADD, TITLE_AUTHOR_REMOVE, TITLE_AUTHOR_GOTO,
		TITLE_VOLUME_ADD, TITLE_VOLUME_REMOVE, TITLE_VOLUME_GOTO,
		TITLE_CONTENT_MOVE_UP, TITLE_CONTENT_MOVE_DOWN, TITLE_CONTENT_ADD, TITLE_CONTENT_REMOVE, TITLE_CONTENT_GOTO,
		TITLE_COLLECTION_GOTO,

		VOLUME_BORROWER_CHECK_IN, VOLUME_BORROWER_CHECK_OUT, VOLUME_BORROWER_GOTO, VOLUME_TITLE_GOTO,

		BORROWER_VOLUME_GOTO
// @formatter:on
	}
}
