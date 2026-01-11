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

import java.io.IOException;
import java.net.URISyntaxException;

import static com.google.inject.Guice.createInjector;
import com.google.inject.Injector;

import client.config.Config;
import client.config.ConfigService;
import client.scenes.AddButtonCtrl;
import client.scenes.AddRecipeIngredientCtrl;
import client.scenes.EditTitleCtrl;
import client.scenes.MainCtrl;
import client.scenes.NutritionalValueCtrl;
import client.scenes.RecipeShoppingListCtrl;
import client.scenes.ShoppingListCtrl;
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

    /**
     * Starts the JavaFX application by initializing the main window and scenes.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if an error occurs during initialization
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Filepath for the config.json
        String configPath = "client/config.json";

        // Check whether the user has entered their own directory for the config file
        var args = getParameters().getRaw();
        for (int i = 0; i < args.size(); i++) {
            if ("--cfg".equals(args.get(i)) && i + 1 < args.size()) {
                configPath = args.get(i + 1);
            }
        }

        ConfigService.setConfigPath(configPath);

        Config config = ConfigService.getConfig();

        System.out.println("Using server: " + config.getServerUrl()); // Feedback to see which server (url) we are using

        var mainUserInterface = FXML.load(MainCtrl.class, "client", "scenes", "MainUserInterface.fxml");
        var mainUserInterfaceScene = new Scene(mainUserInterface.getValue());

        // preload shopping list scene
        var shoppingListInterface = FXML.load(ShoppingListCtrl.class, "client", "scenes", "ShoppingListInterface.fxml");
        var shoppingListScene = new Scene(shoppingListInterface.getValue());
        shoppingListScene.setUserData(shoppingListInterface.getKey()); // Store controller reference

        // preload recipe shopping list scene
        var recipeShoppingListInterface = 
            FXML.load(RecipeShoppingListCtrl.class, "client", "scenes", "RecipeShoppingList.fxml");
        var recipeShoppingListScene = new Scene(recipeShoppingListInterface.getValue());

        // preload nutritional values scene
        var nutritionInterface = 
            FXML.load(NutritionalValueCtrl.class, "client", "scenes", "NutrionalValueInterface.fxml");
        var nutritionScene = new Scene(nutritionInterface.getValue());

        //preload recipe add scene
        var recipeAddInterface
                = FXML.load(AddButtonCtrl.class, "client", "scenes", "AddRecipeInterface.fxml");
        var recipeAddScene = new Scene(recipeAddInterface.getValue());
        var recipeAddCtrl = recipeAddInterface.getKey(); // The instance of the controller

        // Load AddRecipeIngredientScene
        var recipeIngredientInterface
                = FXML.load(AddRecipeIngredientCtrl.class, "client", "scenes", "AddRecipeIngredientScene.fxml");
        var recipeIngredientScene = new Scene(recipeIngredientInterface.getValue());
        var recipeIngredientCtrl = recipeIngredientInterface.getKey();

        // Load EditTitleScene
        var editTitleInterface
                = FXML.load(EditTitleCtrl.class, "client", "scenes", "EditTitleScene.fxml");
        var editTitleScene = new Scene(editTitleInterface.getValue());
        var editTitleCtrl = editTitleInterface.getKey();

        // add stylesheets if needed
        String cssPath = getClass().getResource("/client/scenes/FoodPal.css").toExternalForm();
        String cssPathNutri = getClass().getResource("/client/scenes/NutritionalValue.css").toExternalForm();
        String cssPathRecipe = getClass().getResource("/client/scenes/AddRecipeInterface.css").toExternalForm();

        mainUserInterfaceScene.getStylesheets().add(cssPath);
        nutritionScene.getStylesheets().add(cssPathNutri);
        recipeAddScene.getStylesheets().add(cssPathRecipe);
        recipeIngredientScene.getStylesheets().add(cssPathRecipe);
        shoppingListScene.getStylesheets().add(cssPathNutri);
        recipeShoppingListScene.getStylesheets().add(cssPathNutri);

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);

        Font.loadFont(getClass().getResourceAsStream("/client/scenes/CascadiaCode-VariableFont_wght.ttf"), 12);

        primaryStage.setScene(mainUserInterfaceScene);
        primaryStage.setTitle("FoodPal");
        primaryStage.show();

        // pass both scenes into controller
        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        var recipeShoppingListCtrl = INJECTOR.getInstance(RecipeShoppingListCtrl.class);
        mainCtrl.initScenes(primaryStage, mainUserInterfaceScene, nutritionScene, recipeAddScene, 
            recipeIngredientScene, recipeIngredientCtrl, editTitleScene, shoppingListScene, 
            recipeShoppingListScene, recipeShoppingListCtrl);

        System.out.println("Window opened with width: " + primaryStage.getWidth()
                + " and height: " + primaryStage.getHeight());

        primaryStage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            System.out.println("Window width changed from " + oldWidth + " to " + newWidth);
        });

        primaryStage.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            System.out.println("Window height changed from " + oldHeight + " to " + newHeight);
        });

    }
}
