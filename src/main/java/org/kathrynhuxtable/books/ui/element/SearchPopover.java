/*
 * Copyright (c) 2008, 2017, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.kathrynhuxtable.books.ui.element;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.kathrynhuxtable.books.service.BooksService;
import org.kathrynhuxtable.books.service.DocumentType;
import org.kathrynhuxtable.books.service.SearchResult;
import org.kathrynhuxtable.books.ui.control.Popover;
import org.kathrynhuxtable.books.ui.control.SearchBox;
import org.kathrynhuxtable.books.ui.controller.PageBrowserController;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

/**
 * Implementation of popover to show search results
 */
public class SearchPopover extends Popover {
	private SearchBox searchBox;
	private PageBrowserController pageBrowser;

	private Tooltip searchErrorTooltip = null;
	private Timeline searchErrorTooltipHidder = null;
	private SearchResultPopoverList searchResultPopoverList;

	private BooksService booksService;

	public SearchPopover() {
		super();
		getStyleClass().add("right-tooth");
		setPrefWidth(600);
	}

	public void setSearchBox(SearchBox searchBox) {
		this.searchBox = searchBox;
	}

	public void setPageBrowser(PageBrowserController pageBrowser) {
		this.pageBrowser = pageBrowser;
	}

	public void setBooksService(BooksService booksService) {
		this.booksService = booksService;
	}

	public void initData() {
		pageBrowser.currentPageProperty().addListener((observable, oldValue, newValue) -> {
			searchBox.disableProperty().bind(newValue.changedProperty());
		});
		searchBox.disableProperty().bind(pageBrowser.currentPageProperty().get().changedProperty());

		searchBox.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
			updateResults();
		});

		searchBox.addEventFilter(KeyEvent.ANY, (KeyEvent t) -> {
			if (t.getCode() == KeyCode.DOWN || t.getCode() == KeyCode.UP || t.getCode() == KeyCode.PAGE_DOWN
					|| (t.getCode() == KeyCode.HOME && (t.isControlDown() || t.isMetaDown()))
					|| (t.getCode() == KeyCode.END && (t.isControlDown() || t.isMetaDown())) || t.getCode() == KeyCode.PAGE_UP) {
				searchResultPopoverList.fireEvent(t);
				t.consume();
			} else if (t.getCode() == KeyCode.ENTER) {
				t.consume();
				if (t.getEventType() == KeyEvent.KEY_PRESSED) {
					SearchResult selectedItem = searchResultPopoverList.getSelectionModel().getSelectedItem();
					if (selectedItem != null)
						searchResultPopoverList.itemClicked(selectedItem);
				} 
			} else if (t.getCode() == KeyCode.ESCAPE) {
				hide();
				searchBox.setText("");
			}
		});
		searchResultPopoverList = new SearchResultPopoverList(pageBrowser);
		// if list gets focus then send back to search box
		searchResultPopoverList.focusedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean hasFocus) -> {
			if (hasFocus) {
				searchBox.requestFocus();
				searchBox.selectPositionCaret(searchBox.getText().length());
			}
		});
	}

	private void updateResults() {
		if (searchBox.getText() == null || searchBox.getText().isEmpty()) {
			populateMenu(new EnumMap<DocumentType, List<SearchResult>>(DocumentType.class));
			return;
		}
		boolean haveResults = false;
		Map<DocumentType, List<SearchResult>> results = booksService.searchAll(searchBox.getText());
		// check if we have any results
		for (List<SearchResult> categoryResults : results.values()) {
			if (categoryResults.size() > 0) {
				haveResults = true;
				break;
			}
		}
		if (haveResults) {
			showError(null);
			populateMenu(results);
			show();
		} else {
			if (searchErrorTooltip == null || searchErrorTooltip.getText() == null)
				showError("No matches");
			hide();
		}
	}

	private void showError(String message) {
		if (searchErrorTooltip == null) {
			searchErrorTooltip = new Tooltip();
		}
		searchErrorTooltip.setText(message);
		if (searchErrorTooltipHidder != null)
			searchErrorTooltipHidder.stop();
		if (message != null) {
			Point2D toolTipPos = searchBox.localToScene(0, searchBox.getLayoutBounds().getHeight());
			double x = toolTipPos.getX() + searchBox.getScene().getX() + searchBox.getScene().getWindow().getX();
			double y = toolTipPos.getY() + searchBox.getScene().getY() + searchBox.getScene().getWindow().getY();
			searchErrorTooltip.show(searchBox.getScene().getWindow(), x, y);
			searchErrorTooltipHidder = new Timeline();
			searchErrorTooltipHidder.getKeyFrames().add(new KeyFrame(Duration.seconds(3), (ActionEvent t) -> {
				searchErrorTooltip.hide();
				searchErrorTooltip.setText(null);
			}));
			searchErrorTooltipHidder.play();
		} else {
			searchErrorTooltip.hide();
		}
	}

	private void populateMenu(Map<DocumentType, List<SearchResult>> results) {
		searchResultPopoverList.getItems().clear();
		for (Map.Entry<DocumentType, List<SearchResult>> entry : results.entrySet()) {
			searchResultPopoverList.getItems().addAll(entry.getValue());
		}
		clearPages();
		pushPage(searchResultPopoverList);
	}
	
	@Override
	public void hide() {
		super.hide();
		searchBox.setText("");
	}
}
