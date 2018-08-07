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
package org.kathrynhuxtable.books.ui.control;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 * A simple tool bar that has left and right justified items with a title in the center if there is enough space for it.
 */
public class TitledToolBar extends HBox {

	private Label titleLabel = new Label("");

	private StringProperty titleText = new SimpleStringProperty(null);

	public StringProperty titleTextProperty() {
		return titleText;
	};

	public String getTitleText() {
		return titleText.get();
	}

	public void setTitleText(String text) {
		titleText.set(text);
	}

	private List<Node> leftItems = new LameListWrapperLeft();
	private List<Node> rightItems = new LameListWrapperRight();

	public TitledToolBar() {
		getStyleClass().addAll("tool-bar", "books-tool-bar");
		titleLabel.getStyleClass().add("title");
		titleLabel.setManaged(false);
		titleLabel.textProperty().bind(titleText);
		getChildren().add(titleLabel);
		Pane spacer = new Pane();
		setHgrow(spacer, Priority.ALWAYS);
		getChildren().add(spacer);
	}

	public List<Node> getLeftItems() {
		return leftItems;
	}

	public List<Node> getRightItems() {
		return rightItems;
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		final double w = getWidth();
		final double h = getHeight();
		final double titleWidth = titleLabel.prefWidth(h);
		double leftItemsWidth = getPadding().getLeft();
		for (Node item : getChildren()) {
			if (item == titleLabel)
				break;
			leftItemsWidth += item.getLayoutBounds().getWidth();
			Insets margins = getMargin(item);
			if (margins != null)
				leftItemsWidth += margins.getLeft() + margins.getRight();
			leftItemsWidth += getSpacing();
		}
		if ((leftItemsWidth + (titleWidth / 2)) < (w / 2)) {
			titleLabel.setVisible(true);
			layoutInArea(titleLabel, 0, 0, getWidth(), h, 0, HPos.CENTER, VPos.CENTER);
		} else {
			titleLabel.setVisible(false);
		}
	}

	public abstract class LameListWrapper implements List<Node> {

		@Override
		public boolean isEmpty() {
			return getChildren().isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return getChildren().contains(o);
		}

		@Override
		public Iterator<Node> iterator() {
			return getChildren().iterator();
		}

		@Override
		public Object[] toArray() {
			return null;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return null;
		}

		@Override
		public boolean remove(Object o) {
			return getChildren().remove(o);
		}

		@SuppressWarnings("unlikely-arg-type")
		@Override
		public boolean containsAll(Collection<?> c) {
			return getChildren().contains(c);
		}

		@Override
		public boolean addAll(int index, Collection<? extends Node> c) {
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return false;
		}

		@Override
		public void clear() {
		}

		@Override
		public Node get(int index) {
			return null;
		}

		@Override
		public Node set(int index, Node element) {
			return null;
		}

		@Override
		public void add(int index, Node element) {
		}

		@Override
		public Node remove(int index) {
			return null;
		}

		@Override
		public int indexOf(Object o) {
			return 0;
		}

		@Override
		public int lastIndexOf(Object o) {
			return 0;
		}

		@Override
		public ListIterator<Node> listIterator() {
			return null;
		}

		@Override
		public ListIterator<Node> listIterator(int index) {
			return null;
		}

		@Override
		public List<Node> subList(int fromIndex, int toIndex) {
			return null;
		}
	}

	public final class LameListWrapperLeft extends LameListWrapper {
		@Override
		public int size() {
			int i = 0;
			for (Node child : getChildren()) {
				if (child == titleLabel) {
					return i;
				}
				i++;
			}
			return i;
		}

		@Override
		public boolean add(Node e) {
			getChildren().add(0, e);
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends Node> c) {
			return getChildren().addAll(0, c);
		}
	}

	public final class LameListWrapperRight extends LameListWrapper {
		@Override
		public int size() {
			int i = 0;
			boolean foundText = false;
			for (Node child : getChildren()) {
				if (!foundText && child == titleLabel) {
					foundText = true;
				} else if (foundText) {
					i++;
				}
			}
			return i;
		}

		@Override
		public boolean add(Node e) {
			return getChildren().add(e);
		}

		@Override
		public boolean addAll(Collection<? extends Node> c) {
			return getChildren().addAll(c);
		}
	}
}
