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

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BooksAppPreloader extends Preloader {

	Stage splashScreen;

	private Scene createScene() throws Exception {
		StackPane root = new StackPane();
		ImageView carImageView = new ImageView(new Image(BooksAppPreloader.class.getResourceAsStream("/images/splash-screen.jpg")));
		root.getChildren().addAll(carImageView);
		Scene scene = new Scene(root, 413, 275);
		scene.getStylesheets().add(BooksAppPreloader.class.getResource("/preloader.css").toExternalForm());
		return scene;
	}

	@Override
	public void start(Stage stage) throws Exception {
		splashScreen = stage;
		splashScreen.initStyle(StageStyle.UNDECORATED);
		if (!BooksApplication.IS_MAC) {
			stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/mcdb.png")));
		}
		splashScreen.setScene(createScene());
		splashScreen.show();
	}

	@Override
	public void handleStateChangeNotification(StateChangeNotification notification) {
		if (notification.getType() == StateChangeNotification.Type.BEFORE_START) {
			splashScreen.hide();
		}
	}

}
