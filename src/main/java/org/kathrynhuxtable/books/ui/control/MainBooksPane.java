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
package org.kathrynhuxtable.books.ui.control;

import java.util.List;

import org.kathrynhuxtable.books.ui.element.SearchPopover;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public final class MainBooksPane extends Pane {

	// MenuBar
	public final ObjectProperty<Node> menuBarProperty() {
		if (menuBar == null) {
			menuBar = new BookPaneComponentProperty<Node>("menuBar");
		}
		return menuBar;
	}

	private ObjectProperty<Node> menuBar;

	public final void setMenuBar(Node value) {
		menuBarProperty().set(value);
	}

	public final Node getMenuBar() {
		return menuBar == null ? null : menuBar.get();
	}

	// ToolBar
	public final ObjectProperty<Node> toolBarProperty() {
		if (toolBar == null) {
			toolBar = new BookPaneComponentProperty<Node>("toolBar");
		}
		return toolBar;
	}

	private ObjectProperty<Node> toolBar;

	public final void setToolBar(Node value) {
		toolBarProperty().set(value);
	}

	public final Node getToolBar() {
		return toolBar == null ? null : toolBar.get();
	}
	
	// TopButtons
	public final ObjectProperty<Node> topButtonsProperty() {
		if (topButtons == null) {
			topButtons = new BookPaneComponentProperty<Node>("topButtons");
		}
		return topButtons;
	}
	
	private ObjectProperty<Node> topButtons;
	
	public final void setTopButtons(Node value) {
		topButtonsProperty().set(value);
	}
	
	public final Node getTopButtons() {
		return topButtons == null ? null : topButtons.get();
	}

	// PageBrowser
	public final ObjectProperty<StackPane> pageBrowserProperty() {
		if (pageBrowser == null) {
			pageBrowser = new BookPaneComponentProperty<StackPane>("pageBrowser");
		}
		return pageBrowser;
	}

	private ObjectProperty<StackPane> pageBrowser;

	public final void setPageBrowser(StackPane value) {
		pageBrowserProperty().set(value);
	}

	public final StackPane getPageBrowser() {
		return pageBrowser == null ? null : pageBrowser.get();
	}

	// SearchPopover
	public final ObjectProperty<SearchPopover> searchPopoverProperty() {
		if (searchPopover == null) {
			searchPopover = new BookPaneComponentProperty<SearchPopover>("searchPopover");
		}
		return searchPopover;
	}

	private ObjectProperty<SearchPopover> searchPopover;

	public final void setSearchPopover(SearchPopover value) {
		searchPopoverProperty().set(value);
	}

	public final SearchPopover getSearchPopover() {
		return searchPopover == null ? null : searchPopover.get();
	}

	// BottomButtons
	public final ObjectProperty<Node> bottomButtonsProperty() {
		if (bottomButtons == null) {
			bottomButtons = new BookPaneComponentProperty<Node>("bottomButtons");
		}
		return bottomButtons;
	}

	private ObjectProperty<Node> bottomButtons;

	public final void setBottomButtons(Node value) {
		bottomButtonsProperty().set(value);
	}

	public final Node getBottomButtons() {
		return bottomButtons == null ? null : bottomButtons.get();
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		final double w = getWidth();
		final double h = getHeight();
		final double menuHeight = getMenuBar().prefHeight(w);
		final double toolBarHeight = getToolBar().prefHeight(w);
		final double topButtonsHeight = getTopButtons().prefHeight(w);
		if (getMenuBar() != null) {
			getMenuBar().resize(w, menuHeight);
		}
		getToolBar().resizeRelocate(0, menuHeight, w, toolBarHeight);
		getTopButtons().resizeRelocate(0, menuHeight + toolBarHeight, w, topButtonsHeight);
		getPageBrowser().setLayoutY(toolBarHeight + topButtonsHeight + menuHeight);
		getPageBrowser().resize(w, h - 3 * toolBarHeight - menuHeight);
		Point2D searchBoxBottomRight = getToolBar().localToScene(((HBox) getToolBar()).getWidth(), ((HBox) getToolBar()).getHeight());
		getSearchPopover().setLayoutX((int) searchBoxBottomRight.getX() - getSearchPopover().getLayoutBounds().getWidth() - 20);
		getSearchPopover().setLayoutY((int) searchBoxBottomRight.getY() + 20);
		getBottomButtons().resizeRelocate(0, h - toolBarHeight, w, toolBarHeight);
	}

	private final class BookPaneComponentProperty<T> extends ObjectPropertyBase<T> {
		private Node oldValue = null;
		private final String propertyName;
		private boolean isBeingInvalidated;

		BookPaneComponentProperty(String propertyName) {
			this.propertyName = propertyName;
			getChildren().addListener(new ListChangeListener<Node>() {

				@Override
				public void onChanged(ListChangeListener.Change<? extends Node> c) {
					if (oldValue == null || isBeingInvalidated) {
						return;
					}
					while (c.next()) {
						if (c.wasRemoved()) {
							List<? extends Node> removed = c.getRemoved();
							for (int i = 0, sz = removed.size(); i < sz; ++i) {
								if (removed.get(i) == oldValue) {
									oldValue = null; // Do not remove again in invalidated
									set(null);
								}
							}
						}
					}
				}
			});
		}

		@Override
		protected void invalidated() {
			final List<Node> children = getChildren();

			isBeingInvalidated = true;
			try {
				if (oldValue != null) {
					children.remove(oldValue);
				}

				final Node _value = (Node) get();
				this.oldValue = _value;

				if (_value != null) {
					children.add(_value);
				}
			} finally {
				isBeingInvalidated = false;
			}
		}

		@Override
		public Object getBean() {
			return MainBooksPane.this;
		}

		@Override
		public String getName() {
			return propertyName;
		}
	}
}