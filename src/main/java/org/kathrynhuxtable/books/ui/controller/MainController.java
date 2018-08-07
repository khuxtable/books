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

import org.kathrynhuxtable.books.service.BooksService;
import org.kathrynhuxtable.books.service.DocumentType;
import org.kathrynhuxtable.books.ui.control.SearchBox;
import org.kathrynhuxtable.books.ui.control.TitledToolBar;
import org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName;
import org.kathrynhuxtable.books.ui.element.SearchPopover;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleButton;
import jfxtras.scene.control.ToggleGroupValue;

@Component
public class MainController {

	// Spring components
	@Autowired
	private BooksService booksService;
	@Autowired
	private PageBrowserController pageBrowserController;

	// Main page elements
	@FXML
	private SearchPopover searchPopover;
	@FXML
	private SearchBox searchBox;
	@FXML
	private TitledToolBar toolBar;

	// Page buttons
	@FXML
	private ToggleButton buttonGotoAuthors;
	@FXML
	private ToggleButton buttonGotoTitles;
	@FXML
	private ToggleButton buttonGotoVolumes;
	@FXML
	private ToggleButton buttonGotoBorrowers;
	@FXML
	private Button buttonBack;
	@FXML
	private Button buttonForward;
	@FXML
	private Button buttonHome;
	@FXML
	private Button buttonBrowse;
	@FXML
	private Button buttonHelp;
	@FXML
	private Button buttonNew;
	@FXML
	private Button buttonCancel;
	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonDelete;

	@FXML
	private MenuItem rebuildIndexes;
	@FXML
	private MenuItem menuExit;

	@FXML
	private MenuItem menuNew;
	@FXML
	private MenuItem menuCancel;
	@FXML
	private MenuItem menuSave;
	@FXML
	private MenuItem menuDelete;
	@FXML
	private RadioMenuItem menuGotoAuthors;
	@FXML
	private RadioMenuItem menuGotoBorrowers;
	@FXML
	private RadioMenuItem menuGotoTitles;
	@FXML
	private RadioMenuItem menuGotoVolumes;

	@FXML
	private Menu authorsMenu;
	@FXML
	private MenuItem menuAuthorTitleAdd;
	@FXML
	private MenuItem menuAuthorTitleGoto;

	@FXML
	private Menu titlesMenu;
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

	@FXML
	private Menu volumesMenu;
	@FXML
	private MenuItem menuVolumeBorrowerCheckIn;
	@FXML
	private MenuItem menuVolumeBorrowerCheckOut;
	@FXML
	private MenuItem menuVolumeBorrowerGoto;
	@FXML
	private MenuItem menuVolumeTitleGoto;

	@FXML
	private Menu borrowersMenu;
	@FXML
	private MenuItem menuBorrowerVolumeGoto;

	@FXML
	private MenuItem menuHelpAbout;

	private ToggleGroupValue<DocumentType> menuPageToggleGroup = new ToggleGroupValue<>();
	private ToggleGroupValue<DocumentType> buttonPageToggleGroup = new ToggleGroupValue<>();

	public void initialize() {

		// Initialize popover.
		searchPopover.setSearchBox(searchBox);
		searchPopover.setPageBrowser(pageBrowserController);
		searchPopover.setBooksService(booksService);
		searchPopover.initData();

		// Bind title to what is displayed in page browser.
		toolBar.titleTextProperty().bind(pageBrowserController.currentPageTitleProperty());

		// Wire exit and help.
		rebuildIndexes.setOnAction(event -> booksService.rebuildIndexes());
		// TODO menuExit
		// TODO buttonHelp
		// TODO menuHelpAbout

		// Wire page goto buttons and menu items.
		pageBrowserController.bindCommand(menuGotoAuthors, CommandName.GOTO_AUTHORS_PAGE);
		pageBrowserController.bindCommand(buttonGotoAuthors, CommandName.GOTO_AUTHORS_PAGE);

		pageBrowserController.bindCommand(menuGotoBorrowers, CommandName.GOTO_BORROWERS_PAGE);
		pageBrowserController.bindCommand(buttonGotoBorrowers, CommandName.GOTO_BORROWERS_PAGE);

		pageBrowserController.bindCommand(menuGotoTitles, CommandName.GOTO_TITLES_PAGE);
		pageBrowserController.bindCommand(buttonGotoTitles, CommandName.GOTO_TITLES_PAGE);

		pageBrowserController.bindCommand(menuGotoVolumes, CommandName.GOTO_VOLUMES_PAGE);
		pageBrowserController.bindCommand(buttonGotoVolumes, CommandName.GOTO_VOLUMES_PAGE);

		// Connect the toggles for goto buttons so that they sync.
		menuPageToggleGroup.add(menuGotoAuthors, DocumentType.AUTHOR);
		menuPageToggleGroup.add(menuGotoBorrowers, DocumentType.BORROWER);
		menuPageToggleGroup.add(menuGotoTitles, DocumentType.TITLE);
		menuPageToggleGroup.add(menuGotoVolumes, DocumentType.VOLUME);
		menuPageToggleGroup.valueProperty().bindBidirectional(pageBrowserController.documentTypeProperty());

		buttonPageToggleGroup.add(buttonGotoAuthors, DocumentType.AUTHOR);
		buttonPageToggleGroup.add(buttonGotoBorrowers, DocumentType.BORROWER);
		buttonPageToggleGroup.add(buttonGotoTitles, DocumentType.TITLE);
		buttonPageToggleGroup.add(buttonGotoVolumes, DocumentType.VOLUME);
		buttonPageToggleGroup.valueProperty().bindBidirectional(pageBrowserController.documentTypeProperty());

		// wire nav buttons.
		pageBrowserController.bindCommand(buttonBrowse, CommandName.BROWSE);
		pageBrowserController.bindCommand(buttonHome, CommandName.HOME);
		pageBrowserController.bindCommand(buttonBack, CommandName.BACK);
		pageBrowserController.bindCommand(buttonForward, CommandName.FORWARD);

		// wire record buttons and menu items.
		pageBrowserController.bindCommand(buttonNew, CommandName.NEW);
		pageBrowserController.bindCommand(menuNew, CommandName.NEW);
		pageBrowserController.bindCommand(buttonCancel, CommandName.CANCEL);
		pageBrowserController.bindCommand(menuCancel, CommandName.CANCEL);
		pageBrowserController.bindCommand(buttonSave, CommandName.SAVE);
		pageBrowserController.bindCommand(menuSave, CommandName.SAVE);
		pageBrowserController.bindCommand(buttonDelete, CommandName.DELETE);
		pageBrowserController.bindCommand(menuDelete, CommandName.DELETE);

		// Wire page actions.
		pageBrowserController.bindCommand(menuAuthorTitleGoto, CommandName.AUTHOR_TITLE_GOTO);

		pageBrowserController.bindCommand(menuTitleAuthorMoveUp, CommandName.TITLE_AUTHOR_MOVE_UP);
		pageBrowserController.bindCommand(menuTitleAuthorMoveDown, CommandName.TITLE_AUTHOR_MOVE_DOWN);
		pageBrowserController.bindCommand(menuTitleAuthorAdd, CommandName.TITLE_AUTHOR_ADD);
		pageBrowserController.bindCommand(menuTitleAuthorRemove, CommandName.TITLE_AUTHOR_REMOVE);
		pageBrowserController.bindCommand(menuTitleAuthorGoto, CommandName.TITLE_AUTHOR_GOTO);
		pageBrowserController.bindCommand(menuTitleVolumeAdd, CommandName.TITLE_VOLUME_ADD);
		pageBrowserController.bindCommand(menuTitleVolumeRemove, CommandName.TITLE_VOLUME_REMOVE);
		pageBrowserController.bindCommand(menuTitleVolumeGoto, CommandName.TITLE_VOLUME_GOTO);
		pageBrowserController.bindCommand(menuTitleContentMoveUp, CommandName.TITLE_CONTENT_MOVE_UP);
		pageBrowserController.bindCommand(menuTitleContentMoveDown, CommandName.TITLE_CONTENT_MOVE_DOWN);
		pageBrowserController.bindCommand(menuTitleContentAdd, CommandName.TITLE_CONTENT_ADD);
		pageBrowserController.bindCommand(menuTitleContentRemove, CommandName.TITLE_CONTENT_REMOVE);
		pageBrowserController.bindCommand(menuTitleContentGoto, CommandName.TITLE_CONTENT_GOTO);
		pageBrowserController.bindCommand(menuTitleCollectionGoto, CommandName.TITLE_COLLECTION_GOTO);

		pageBrowserController.bindCommand(menuVolumeBorrowerCheckIn, CommandName.VOLUME_BORROWER_CHECK_IN);
		pageBrowserController.bindCommand(menuVolumeBorrowerCheckOut, CommandName.VOLUME_BORROWER_CHECK_OUT);
		pageBrowserController.bindCommand(menuVolumeBorrowerGoto, CommandName.VOLUME_BORROWER_GOTO);
		pageBrowserController.bindCommand(menuVolumeTitleGoto, CommandName.VOLUME_TITLE_GOTO);

		pageBrowserController.bindCommand(menuBorrowerVolumeGoto, CommandName.BORROWER_VOLUME_GOTO);
	}

	public void setAuthorsMenuVisible(boolean show) {
		authorsMenu.setVisible(show);
	}

	public void setTitlesMenuVisible(boolean show) {
		titlesMenu.setVisible(show);
	}

	public void setVolumesMenuVisible(boolean show) {
		volumesMenu.setVisible(show);
	}

	public void setBorrowersMenuVisible(boolean show) {
		borrowersMenu.setVisible(show);
	}
}
