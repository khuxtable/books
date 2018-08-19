/*
 * Copyright (c) 2008, 2014, Oracle and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kathrynhuxtable.books.service.DocumentType;
import org.kathrynhuxtable.books.service.SearchResult;
import org.kathrynhuxtable.books.ui.control.Popover;
import org.kathrynhuxtable.books.ui.control.PopoverTreeList;
import org.kathrynhuxtable.books.ui.controller.PageBrowserController;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Popover page that displays a list of search results.
 */
public class SearchResultPopoverList extends PopoverTreeList<SearchResult> implements Popover.Page {
	private Popover popover;
	private PageBrowserController pageBrowser;
	private Rectangle leftLine = new Rectangle(0, 0, 1, 1);
	private IconPane iconPane = new IconPane();
	private final Pane backgroundRectangle = new Pane();

	public SearchResultPopoverList(PageBrowserController pageBrowser) {
		this.pageBrowser = pageBrowser;
		leftLine.setFill(Color.web("#dfdfdf"));
		iconPane.setManaged(false);
		setFocusTraversable(false);
		backgroundRectangle.setId("PopoverBackground");
		setPlaceholder(backgroundRectangle);
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		if (leftLine.getParent() != this)
			getChildren().add(leftLine);
		if (iconPane.getParent() != this)
			getChildren().add(iconPane);
		leftLine.setLayoutX(40);
		leftLine.setLayoutY(0);
		leftLine.setHeight(getHeight());
		iconPane.setLayoutX(0);
		iconPane.setLayoutY(0);
		iconPane.resize(getWidth(), getHeight());
		backgroundRectangle.resize(getWidth(), getHeight());
	}

	@Override
	public void itemClicked(SearchResult item) {
		popover.hide();
		pageBrowser.goToPage(item.getDocumentType(), item.getId());
	}

	@Override
	public void setPopover(Popover popover) {
		this.popover = popover;
	}

	@Override
	public Popover getPopover() {
		return popover;
	}

	@Override
	public Node getPageNode() {
		return this;
	}

	@Override
	public String getPageTitle() {
		return "Results";
	}

	@Override
	public String leftButtonText() {
		return null;
	}

	@Override
	public void handleLeftButton() {
	}

	@Override
	public String rightButtonText() {
		return null;
	}

	@Override
	public void handleRightButton() {
	}

	@Override
	public void handleShown() {
	}

	@Override
	public void handleHidden() {
	}

	@Override
	public ListCell<SearchResult> call(ListView<SearchResult> p) {
		return new SearchResultListCell();
	}

	private class IconPane extends Pane {
		private Label authorIcon = new Label("A");
		private Label titleIcon = new Label("T");
		private Label volumeIcon = new Label("V");
		private Label borrowerIcon = new Label("B");
		private List<SearchResultListCell> allCells = new ArrayList<>();
		private Rectangle titleLine = new Rectangle(0, 0, 40, 1);
		private Rectangle volumeLine = new Rectangle(0, 0, 40, 1);
		private Rectangle borrowerLine = new Rectangle(0, 0, 40, 1);

		public IconPane() {
			getStyleClass().add("search-icon-pane");
			titleLine.setFill(Color.web("#dfdfdf"));
			volumeLine.setFill(Color.web("#dfdfdf"));
			borrowerLine.setFill(Color.web("#dfdfdf"));
			getChildren().addAll(authorIcon, titleIcon, volumeIcon, borrowerIcon, titleLine, volumeLine, borrowerLine);
			setMouseTransparent(true);
		}

		private Node getIconForDocType(DocumentType docType) {
			switch (docType) {
			case AUTHOR:
				return authorIcon;
			case BORROWER:
				return borrowerIcon;
			case TITLE:
				return titleIcon;
			case VOLUME:
				return volumeIcon;
			default:
				return null;
			}
		}

		private Rectangle getLineForDocType(DocumentType docType) {
			return null;
		}

		@Override
		protected void layoutChildren() {
			List<SearchResultListCell> visibleCells = new ArrayList<>(20);
			for (SearchResultListCell cell : allCells) {
				if (cell.isVisible())
					visibleCells.add(cell);
			}
			Collections.sort(visibleCells, (Node o1, Node o2) -> Double.compare(o1.getLayoutY(), o2.getLayoutY()));

			authorIcon.setLayoutX(8);
			authorIcon.resize(24, 24);
			titleIcon.setLayoutX(8);
			titleIcon.resize(24, 24);
			volumeIcon.setLayoutX(8);
			volumeIcon.resize(24, 24);
			borrowerIcon.setLayoutX(8);
			borrowerIcon.resize(24, 24);

			authorIcon.setVisible(false);
			titleIcon.setVisible(false);
			volumeIcon.setVisible(false);
			borrowerIcon.setVisible(false);
			titleLine.setVisible(false);
			volumeLine.setVisible(false);
			borrowerLine.setVisible(false);

			final int last = visibleCells.size() - 1;
			DocumentType lastDocType = null;
			for (int index = 0; index <= last; index++) {
				SearchResultListCell cell = visibleCells.get(index);
				DocumentType docType = getDocumentTypeForCell(cell);
				if (lastDocType != docType && docType != null) {
					// this is first of this doc type
					Node icon = getIconForDocType(docType);
					icon.setVisible(true);
					// calculate cell position relative to iconPane
					Point2D cell00 = cell.localToScene(0, 0);
					cell00 = sceneToLocal(cell00);
					// check if next is differnt
					if (index != last && getDocumentTypeForCell(visibleCells.get(index + 1)) != docType) {
						icon.setLayoutY(cell00.getY() + 8);
					} else {
						icon.setLayoutY(Math.max(8, cell00.getY() + 8));
					}
					// update line
					Rectangle line = getLineForDocType(docType);
					if (line != null) {
						line.setVisible(true);
						line.setLayoutY(cell00.getY());
					}
				}
				lastDocType = docType;
			}

		}

		private final DocumentType getDocumentTypeForCell(SearchResultListCell cell) {
			SearchResult searchResult = cell.getItem();
			return searchResult == null ? null : searchResult.getDocumentType();
		}
	}

	private class SearchResultListCell extends ListCell<SearchResult> implements Skin<SearchResultListCell>, EventHandler<Event> {
		private static final int TEXT_GAP = 6;
		private Label title = new Label();
		private Label details = new Label();
		private Rectangle topLine = new Rectangle(0, 0, 1, 1);

		private SearchResultListCell() {
			super();
			// we don't need any of the labeled functionality of the default cell skin, so we replace skin with our own
			// in this case using this same class as it saves memory. This skin is very simple its just a HBox container
			setSkin(this);
			getStyleClass().setAll("search-result-cell");
			title.getStyleClass().setAll("title");
			details.getStyleClass().setAll("details");
			topLine.setFill(Color.web("#dfdfdf"));
			getChildren().addAll(title, details, topLine);
			setOnMouseClicked(this);

			// listen to changes of this cell being added and removed from list
			// and when it or its parent is moved. If any of those things change
			// then update the iconPane's layout. requestLayout() will be called
			// many times for any change of cell layout in the list but that
			// dosn't matter as they will all be batched up by layout mechanism
			// and iconPane.layoutChildren() will only be called once per frame.
			final ChangeListener<Bounds> boundsChangeListener = (ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) -> {
				iconPane.requestLayout();
			};
			parentProperty().addListener((ObservableValue<? extends Parent> ov, Parent oldParent, Parent newParent) -> {
				if (oldParent != null) {
					oldParent.layoutBoundsProperty().removeListener(boundsChangeListener);
				}
				if (newParent != null && newParent.isVisible()) {
					iconPane.allCells.add(SearchResultListCell.this);
					newParent.layoutBoundsProperty().addListener(boundsChangeListener);
				} else {
					iconPane.allCells.remove(SearchResultListCell.this);
				}
				iconPane.requestLayout();
			});
		}

		@Override
		protected double computeMinWidth(double height) {
			final Insets insets = getInsets();
			final double h = height = insets.getBottom() - insets.getTop();
			return (int) ((insets.getLeft() + title.minWidth(h) + TEXT_GAP + details.minWidth(h) + insets.getRight()) + 0.5d);
		}

		@Override
		protected double computePrefWidth(double height) {
			final Insets insets = getInsets();
			final double h = height = insets.getBottom() - insets.getTop();
			return (int) ((insets.getLeft() + title.prefWidth(h) + TEXT_GAP + details.prefWidth(h) + insets.getRight()) + 0.5d);
		}

		@Override
		protected double computeMaxWidth(double height) {
			final Insets insets = getInsets();
			final double h = height = insets.getBottom() - insets.getTop();
			return (int) ((insets.getLeft() + title.maxWidth(h) + TEXT_GAP + details.maxWidth(h) + insets.getRight()) + 0.5d);
		}

		@Override
		protected double computeMinHeight(double width) {
			final Insets insets = getInsets();
			final double w = width - insets.getLeft() - insets.getRight();
			return (int) ((insets.getTop() + title.minHeight(w) + TEXT_GAP + details.minHeight(w) + insets.getBottom()) + 0.5d);
		}

		@Override
		protected double computePrefHeight(double width) {
			final Insets insets = getInsets();
			final double w = width - insets.getLeft() - insets.getRight();
			return (int) ((insets.getTop() + title.prefHeight(w) + TEXT_GAP + details.prefHeight(w) + insets.getBottom()) + 0.5d);
		}

		@Override
		protected double computeMaxHeight(double width) {
			final Insets insets = getInsets();
			final double w = width - insets.getLeft() - insets.getRight();
			return (int) ((insets.getTop() + title.maxHeight(w) + TEXT_GAP + details.maxHeight(w) + insets.getBottom()) + 0.5d);
		}

		@Override
		protected void layoutChildren() {
			final Insets insets = getInsets();
			final double left = insets.getLeft();
			final double top = insets.getTop();
			final double w = getWidth() - left - insets.getRight();
			final double titleHeight = title.prefHeight(w);
			title.setLayoutX(left);
			title.setLayoutY(top);
			title.resize(w, titleHeight);
			final double detailsHeight = details.prefHeight(w);
			details.setLayoutX(left);
			details.setLayoutY(top + titleHeight + TEXT_GAP);
			details.resize(w, detailsHeight);
			topLine.setLayoutX(left - 5);
			topLine.setWidth(getWidth() - left + 5);
		}

		// CELL METHODS
		@Override
		protected void updateItem(SearchResult result, boolean empty) {
			super.updateItem(result, empty);
			if (result == null) { // empty item
				title.setVisible(false);
				details.setVisible(false);
			} else {
				title.setVisible(true);
				details.setVisible(true);
				title.setText(result.getName());
				details.setText(result.getShortDescription());
			}
		}

		// SKIN METHODS
		@Override
		public SearchResultListCell getSkinnable() {
			return this;
		}

		@Override
		public Node getNode() {
			return null;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void handle(Event t) {
			itemClicked(getItem());
		}
	}
}