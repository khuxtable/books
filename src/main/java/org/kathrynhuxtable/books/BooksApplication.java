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
package org.kathrynhuxtable.books;

import java.util.Locale;

import org.kathrynhuxtable.books.ui.control.MainBooksPane;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Main entry point for the Spring Boot application.
 */
@SpringBootApplication
public class BooksApplication extends Application {
	private static final String OS_NAME = System.getProperty("books.os.name", System.getProperty("os.name"));
	private static final String OS_ARCH = System.getProperty("books.os.arch", System.getProperty("os.arch"));
	public static final boolean IS_IPHONE = false;
	public static final boolean IS_IOS = OS_NAME.startsWith("iOS");
	public static final boolean IS_ANDROID = "android".equals(System.getProperty("javafx.platform")) || "Dalvik".equals(System.getProperty("java.vm.name"));
	public static final boolean IS_EMBEDDED = "arm".equals(OS_ARCH) && !IS_IOS && !IS_ANDROID;
	public static final boolean IS_DESKTOP = !IS_EMBEDDED && !IS_IOS && !IS_ANDROID;
	public static final boolean IS_MAC = OS_NAME.startsWith("Mac");
	public static final boolean PRELOAD_PREVIEW_IMAGES = true;
	public static final boolean SHOW_HIGHLIGHTS = IS_DESKTOP;
	public static final boolean SHOW_MENU = Boolean.getBoolean("books.menu.show");
	public static final boolean SELECT_IOS_THEME = false;
	public static final boolean START_FULL_SCREEN = IS_EMBEDDED || IS_IOS || IS_ANDROID;

	private ConfigurableApplicationContext springContext;

	private Scene scene;
	private MainBooksPane root;

	public static void main(String[] args) {
		System.setProperty("javafx.preloader", "org.kathrynhuxtable.books.BooksAppPreloader");
		System.setProperty("derby.stream.error.method", "org.kathrynhuxtable.books.persistence.util.DerbySlf4jBridge.bridge");
		launch(args);
	}

	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(BooksApplication.class);

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
		fxmlLoader.setControllerFactory(springContext::getBean);
		fxmlLoader.setResources(new MessageSourceResourceBundle(springContext.getBean(MessageSource.class), Locale.getDefault()));
		root = fxmlLoader.load();
	}

	@Override
	public void start(final Stage stage) throws Exception {
		// CREATE SCENE
		// #f4f4f4 is the background color defined in the css resource and SourceTab.java
		scene = new Scene(root, 1024, 768, Color.web("#f4f4f4"));
		if (IS_EMBEDDED || IS_ANDROID) {
			new ScrollEventSynthesizer(scene);
		}
		stage.setScene(scene);
		// START FULL SCREEN IF WANTED
		if (START_FULL_SCREEN) {
			Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
			stage.setX(primaryScreenBounds.getMinX());
			stage.setY(primaryScreenBounds.getMinY());
			stage.setWidth(primaryScreenBounds.getWidth());
			stage.setHeight(primaryScreenBounds.getHeight());
		}
		stage.setTitle("Media Collection Database");
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		springContext.stop();
		System.exit(0);
	}
}
