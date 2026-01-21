/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * You may not use this file except in compliance with the License.
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
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * the main method of the application
     * @param args
     * @throws URISyntaxException
     * @throws IOException
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch(args); // args should be passed as a parameter
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
        String configPath = Paths.get("client", "config.json").toString();

        // Check whether the user has entered their own directory for the config file
        var args = getParameters().getRaw();
        for (int i = 0; i < args.size(); i++) {
            if ("--cfg".equals(args.get(i)) && i + 1 < args.size()) {
                configPath = args.get(i + 1);
            }
        }

        Injector injector = createInjector(new MyModule(configPath));
        MyFXML fxml = new MyFXML(injector);

        // Load config
        Config config = injector.getInstance(ConfigService.class).getConfig();

        // Step 1: Load the ResourceBundle based on saved language
        String langCode = config.getLanguage(); // e.g., "en", "nl", "ro", "es"
        System.out.println("Loaded language from config: " + langCode);

        ResourceBundle bundle = ResourceBundle.getBundle("client.scenes.Translations", new Locale(langCode));

        var loader = new FXMLLoader(getClass().getResource("/client/scenes/MainUserInterface.fxml"));
        loader.setResources(bundle);
        loader.setControllerFactory(injector::getInstance);
        Parent root = loader.load();

        MainCtrl controller = loader.getController();
        var mainUserInterface = new javafx.util.Pair<>(controller, root);

        var mainUserInterfaceScene = new Scene(mainUserInterface.getValue());

        loadScenesAndStyles(fxml, bundle, mainUserInterfaceScene, primaryStage, controller);

        // pass both scenes into controller

        System.out.println("Window opened with width: " + primaryStage.getWidth()
                + " and height: " + primaryStage.getHeight());

        primaryStage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            System.out.println("Window width changed from " + oldWidth + " to " + newWidth);
        });

        primaryStage.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            System.out.println("Window height changed from " + oldHeight + " to " + newHeight);
        });
    }

    private void loadScenesAndStyles(
            MyFXML fxml, ResourceBundle bundle, Scene mainUserInterfaceScene, Stage primaryStage, MainCtrl controller) {
        // preload shopping list scene
        var shoppingListInterface =
                fxml.load(ShoppingListCtrl.class, bundle, "client", "scenes", "ShoppingListInterface.fxml");
        var shoppingListScene = new Scene(shoppingListInterface.getValue());
        shoppingListScene.setUserData(shoppingListInterface.getKey()); // Store controller reference

        // preload recipe shopping list scene
        var recipeShoppingListInterface =
            fxml.load(RecipeShoppingListCtrl.class, bundle, "client", "scenes", "RecipeShoppingList.fxml");
        var recipeShoppingListScene = new Scene(recipeShoppingListInterface.getValue());
        var recipeShoppingListCtrl = recipeShoppingListInterface.getKey();

        // preload nutritional values scene
        var nutritionInterface =
                fxml.load(NutritionalValueCtrl.class, bundle, "client", "scenes", "NutrionalValueInterface.fxml");
        var nutritionScene = new Scene(nutritionInterface.getValue());
        var nutritionCtrl = nutritionInterface.getKey();

        //preload recipe add scene
        var recipeAddInterface =
                fxml.load(AddButtonCtrl.class, bundle, "client", "scenes", "AddRecipeInterface.fxml");
        var recipeAddScene = new Scene(recipeAddInterface.getValue());

        // Load AddRecipeIngredientScene
        var recipeIngredientInterface =
                fxml.load(AddRecipeIngredientCtrl.class, bundle, "client", "scenes", "AddRecipeIngredientScene.fxml");
        var recipeIngredientScene = new Scene(recipeIngredientInterface.getValue());
        var recipeIngredientCtrl = recipeIngredientInterface.getKey();

        // Load EditTitleScene
        var editTitleInterface =
                fxml.load(EditTitleCtrl.class, bundle, "client", "scenes", "EditTitleScene.fxml");
        var editTitleScene = new Scene(editTitleInterface.getValue());

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

        controller.initScenes(primaryStage, nutritionScene, recipeAddScene,
                recipeIngredientScene, recipeIngredientCtrl, editTitleScene,
                shoppingListScene, recipeShoppingListScene, nutritionCtrl, recipeShoppingListCtrl);
    }
}