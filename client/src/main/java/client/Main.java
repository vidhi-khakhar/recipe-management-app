/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.AddButtonCtrl;
import client.scenes.AddRecipeIngredientCtrl;
import com.google.inject.Injector;

import client.scenes.MainCtrl;
import client.scenes.NutritionalValueCtrl;
import client.utils.ServerUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

	private static final Injector INJECTOR = createInjector(new MyModule());
	private static final MyFXML FXML = new MyFXML(INJECTOR);

	public static void main(String[] args) throws URISyntaxException, IOException {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

    var mainUserInterface = FXML.load(MainCtrl.class, "client", "scenes", "MainUserInterface.fxml");
    var mainUserInterfaceScene = new Scene(mainUserInterface.getValue());

    // preload nutritional values scene
    var nutritionInterface = FXML.load(NutritionalValueCtrl.class, "client", "scenes", "NutrionalValueInterface.fxml");
    var nutritionScene = new Scene(nutritionInterface.getValue());

    //preload recipe add scene
    var recipeAddInterface = FXML.load(AddButtonCtrl.class, "client", "scenes", "AddRecipeInterface.fxml");
    var recipeAddScene = new Scene(recipeAddInterface.getValue());
    var recipeAddCtrl = recipeAddInterface.getKey(); // The instance of the controller

    // Load AddRecipeIngredientScene
    var recipeIngredientInterface = FXML.load(AddRecipeIngredientCtrl.class, "client", "scenes", "AddRecipeIngredientScene.fxml");
    var recipeIngredientScene = new Scene(recipeIngredientInterface.getValue());
    var recipeIngredientCtrl = recipeIngredientInterface.getKey();

    // add stylesheets if needed
    String cssPath = getClass().getResource("/client/scenes/FoodPal.css").toExternalForm();
    String cssPathNutri = getClass().getResource("/client/scenes/NutritionalValue.css").toExternalForm();
    String cssPathRecipe = getClass().getResource("/client/scenes/AddRecipeInterface.css").toExternalForm();

    mainUserInterfaceScene.getStylesheets().add(cssPath);
    nutritionScene.getStylesheets().add(cssPathNutri);
    recipeAddScene.getStylesheets().add(cssPathRecipe);

    primaryStage.setMinWidth(600);
    primaryStage.setMinHeight(500);

    Font.loadFont(getClass().getResourceAsStream("/client/CascadiaCode-VariableFont_wght.ttf"), 12);


    primaryStage.setScene(mainUserInterfaceScene);
    primaryStage.setTitle("FoodPal");
    primaryStage.show();

    // pass both scenes into controller
    var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
    mainCtrl.initScenes(primaryStage, mainUserInterfaceScene, nutritionScene, recipeAddScene, recipeIngredientScene);

    System.out.println("Window opened with width: " + primaryStage.getWidth() +
                " and height: " + primaryStage.getHeight());

        primaryStage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            System.out.println("Window width changed from " + oldWidth + " to " + newWidth);
        });

        primaryStage.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            System.out.println("Window height changed from " + oldHeight + " to " + newHeight);
        });


	}
}