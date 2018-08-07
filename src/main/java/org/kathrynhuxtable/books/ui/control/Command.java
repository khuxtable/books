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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.kathrynhuxtable.books.ui.controller.PageBrowserController.CommandName;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.MenuItem;

public class Command {

	private BooleanProperty possible = new SimpleBooleanProperty(true);
	private EventHandler<ActionEvent> handler;

	public Command(EventHandler<ActionEvent> handler) {
		this.handler = handler;
	}

	public ReadOnlyBooleanProperty possibleProperty() {
		return possible;
	}

	public boolean isPossible() {
		return possible.get();
	}

	public void setPossible(boolean status) {
		possible.set(status);
	}

	public void bind(ButtonBase buttonBase) {
		buttonBase.setOnAction(handler);
		buttonBase.disableProperty().bind(this.possibleProperty().not());
	}

	public void bind(MenuItem menuItem) {
		menuItem.setOnAction(handler);
		menuItem.disableProperty().bind(this.possibleProperty().not());
	}

	public void execute() {
		handler.handle(new ActionEvent());
	}

	public static MapBuilder builder() {
		return new MapBuilder(((Supplier<Map<CommandName, Command>>) HashMap::new).get());
	}

	public static class MapBuilder {

		private final Map<CommandName, Command> map;

		public MapBuilder(Map<CommandName, Command> map) {
			this.map = map;
		}

		public MapBuilder put(CommandName key, EventHandler<ActionEvent> value) {
			map.put(key, new Command(value));
			return this;
		}

		public Map<CommandName, Command> build() {
			return Collections.unmodifiableMap(map);
		}

	}

}
