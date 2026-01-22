package client.scenes;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.config.ConfigService;
import client.utils.IngredientServerUtils;
import client.utils.RecipeServerUtils;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import jakarta.ws.rs.ProcessingException;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class NutritionalValueCtrl {

    private final IngredientServerUtils ingredientServerUtils;
    private final RecipeServerUtils recipeServerUtils;
    private ConfigService configService;

    @FXML
    private Label RecipeUsageValueLabel;

    @FXML 
    private Label nutritionalValueTitle;

    @FXML
    private Button addIngredientButton;

    @FXML
    private Button addIngredientButton2;

    @FXML
    private AnchorPane addIngredientPane;

    @FXML
    private Button cancelButton;

    @FXML
    private Button cancelButton2;

    @FXML
    private TextField carbValue;

    @FXML
    private Button confirmButton;

    @FXML
    private Button editIngredientButton;

    @FXML
    private TextField fatValue;

    @FXML
    private Button healthButton;

    @FXML
    private Label ingredientCarb;

    @FXML
    private AnchorPane ingredientDetailsPane;

    @FXML
    private Label ingredientFat;

    @FXML
    private Label proteinLabel;

    @FXML
    private Label fatLabel;

    @FXML
    private Label carbsLabel;

    @FXML
    private Label caloriesLabel;

    @FXML
    private TextField ingredientName;

    @FXML
    private Label ingredientProtein;

    @FXML 
    private Label enterIngredientDetailsLabel;

    @FXML 
    private Label allFieldsRequired;

    @FXML
    private Label kcalValueLabel;

    @FXML
    private Label ingredientNameLabel;

    @FXML
    private TextField newIngCarbs;

    @FXML
    private TextField newIngFats;

    @FXML
    private TextField newIngName;

    @FXML
    private TextField newIngProteins;

    @FXML
    private AnchorPane noIngredientPane;

    @FXML
    private Label noIngredientSubtitle;

    @FXML
    private Label noIngredientTitle;

    @FXML
    private TextField proteinValue;

    @FXML
    private Button removeIngredientButton;

    @FXML
    private ListView<Ingredient> sidebarList;

    /**
     * Constructor for the nutritional value controller class, using GUICE injection.
     */
    @Inject
    public NutritionalValueCtrl(IngredientServerUtils ingredientServerUtils,
                                RecipeServerUtils recipeServerUtils, ConfigService configService) {
        this.ingredientServerUtils = ingredientServerUtils;
        this.recipeServerUtils = recipeServerUtils;
        this.configService = configService;
    }
    
    /**
     * This method initializes the NutritionalValueUI controller
     *
     * This method is automatically called by FXML loader after the controller's
     * root element has been completely processed. It loads the ingredients from
     * the server and places them on the sideBar. It selects the corresponding
     * pane depending on the amount of Ingredients in the app.
     */
    @FXML
    public void initialize() {
        // Initially show/hide panes based on list selection
        updateStackPaneVisibility();

        defaultMode();

        // Listen for selection changes
        sidebarList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateSelectedIngredient();
            updateStackPaneVisibility();
        });

        // Listen for items added/removed in the list
        sidebarList.getItems().addListener((ListChangeListener<Object>) change -> {
            updateStackPaneVisibility();
        });

        bindLabelFontSize(noIngredientTitle, 70);
        bindLabelFontSize(noIngredientSubtitle, 80);

        loadIngredientsFromServer();
    }

    /**
     * Loads translated texts for all static UI elements.
     */
    @FXML
    public void loadLanguages() {
        ResourceBundle props = getTranslationsBundle();

        nutritionalValueTitle.setText(props.getString("label.nutritionalValuesTitle"));

        // Window / titles
        noIngredientTitle.setText(props.getString("label.noIngredients"));
        noIngredientSubtitle.setText(props.getString("label.noIngredientsSubtitle"));
        healthButton.setText(props.getString("label.healthOverview"));

        enterIngredientDetailsLabel.setText(props.getString("label.enterIngredientDetails"));
        allFieldsRequired.setText(props.getString("label.allFieldsRequired"));
        ingredientNameLabel.setText(props.getString("label.ingredientNameLabel"));
        ingredientProtein.setText(props.getString("label.proteinLabel"));
        ingredientFat.setText(props.getString("label.fatLabel"));
        ingredientCarb.setText(props.getString("label.carbohydratesLabel"));
        cancelButton2.setText(props.getString("label.cancelButton"));
        addIngredientButton2.setText(props.getString("label.addIngredientButton"));

        ingredientName.setPromptText(props.getString("label.ingredientNameLabel"));
        proteinLabel.setText(props.getString("label.proteinLabel"));
        fatLabel.setText(props.getString("label.fatLabel"));
        carbsLabel.setText(props.getString("label.carbohydratesLabel"));
        caloriesLabel.setText(props.getString("label.caloriesLabel"));
        cancelButton.setText(props.getString("label.cancelButton"));
        editIngredientButton.setText(props.getString("label.editButton"));
        confirmButton.setText(props.getString("label.confirmButton"));

    }



    private void updateSelectedIngredient() {
        Ingredient selected = sidebarList.getSelectionModel().getSelectedItem();
        showIngredientDetails(selected);
        updateUsageLabel(selected);
    }


    /**
     * Loads all the ingredients from the server into the UI. It places them in
     * the sideBarList.
     */
    private void loadIngredientsFromServer() {
        ResourceBundle props = getTranslationsBundle();

        try {
            var ingredients = ingredientServerUtils.getAllIngredients();

            sidebarList.getItems().setAll(ingredients);

            if (!ingredients.isEmpty()) {
                Ingredient first = ingredients.get(0);
                sidebarList.getSelectionModel().select(first);
                showIngredientDetails(first);
                updateUsageLabel(first);
            } else {
                showIngredientDetails(null);
            }

            updateStackPaneVisibility();
        } catch (ProcessingException e) {
            showError(props.getString("alert.problemWithIngredients"),
                    props.getString("alert.problemWithIngredients.body") + e.getMessage());
            sidebarList.getItems().clear();
            showIngredientDetails(null);
            updateStackPaneVisibility();
        }
    }

    /**
     * It sets the panes depending on the amount of ingredients that are in the
     * database.
     *
     */
    private void updateStackPaneVisibility() {
        boolean hasSelection = sidebarList.getSelectionModel().getSelectedItem() != null;
        boolean hasItems = !sidebarList.getItems().isEmpty();

        if (!hasItems) {
            noIngredientPane.setVisible(true);
            ingredientDetailsPane.setVisible(false);
            addIngredientPane.setVisible(false);
        } else {
            noIngredientPane.setVisible(false);
            addIngredientPane.setVisible(false);
            ingredientDetailsPane.setVisible(true);
            confirmButton.setVisible(false);
            cancelButton.setVisible(false);
        }
    }

    /**
     * It sets the corresponding pane and shows the details of the ingredient
     * that is selected. It makes the TextFields accessible for the user and
     * makes visible and accessible the edit, add and remove buttons. It also
     * sets to normal the status of the remove button if it was in the add
     * ingredient mode.
     */
    private void defaultMode() {
        updateStackPaneVisibility();
        updateSelectedIngredient();

        ingredientName.setDisable(true);
        fatValue.setDisable(true);
        proteinValue.setDisable(true);
        carbValue.setDisable(true);
        sidebarList.setDisable(false);

        addIngredientButton.setDisable(false);
        removeIngredientButton.setDisable(false);
        removeIngredientButton.setOnAction(event -> removeIngredient());
        removeIngredientButton.setText("-");

        editIngredientButton.setVisible(true);
        cancelButton.setVisible(false);
        confirmButton.setVisible(false);
    }

    /**
     * It it actioned when the user wants to add an ingredient.
     *
     * It makes all panes invisibles except the addIngredientPane and changes
     * the remove button function to the defaultMode method and the text of
     * button to a back arrow.
     */
    private void addMode() {
        if (!addIngredientPane.isVisible()) {
            noIngredientPane.setVisible(false);
            ingredientDetailsPane.setVisible(false);
            addIngredientPane.setVisible(true);
            sidebarList.setDisable(true);
        }
    }

    /**
     * It's activated when the suer wants to edit an ingredient
     *
     * All buttons except the close button are disabled and a confirm and cancel
     * button appear. The edit button disapears
     */
    private void editMode() {
        ingredientName.setDisable(false);
        fatValue.setDisable(false);
        proteinValue.setDisable(false);
        carbValue.setDisable(false);
        sidebarList.setDisable(true);
        addIngredientButton.setDisable(true);
        removeIngredientButton.setDisable(true);

        editIngredientButton.setVisible(false);
        confirmButton.setVisible(true);
        cancelButton.setVisible(true);
    }

    /**
     * Shows the values of the selected ingredient in the corresponding fields.
     *
     * @param ingredient the ingredient that is selected
     */
    private void showIngredientDetails(Ingredient ingredient) {
        ResourceBundle props = getTranslationsBundle();
        if (ingredient == null) {
            ingredientName.clear();
            proteinValue.clear();
            fatValue.clear();
            carbValue.clear();
            kcalValueLabel.setText("0");
            RecipeUsageValueLabel.setText(props.getString("label.usedInUnknown"));
            return;
        }

        ingredientName.setText(ingredient.getName());
        proteinValue.setText(Double.toString(ingredient.getProteinPer100g()));
        fatValue.setText(Double.toString(ingredient.getFatPer100g()));
        carbValue.setText(Double.toString(ingredient.getCarbsPer100g()));
        kcalValueLabel.setText(Double.toString(ingredient.getKcalPer100g()));

        updateUsageLabel(ingredient);
    }

    // Dynamic font sizing.
    private void bindLabelFontSize(Label label, double growthFactor) {
        double baseFontSize = 9;

        label.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double size = baseFontSize + 
                Math.min(noIngredientPane.getWidth(), noIngredientPane.getHeight()) / growthFactor;
            return new Font("System", size);
        }, noIngredientPane.widthProperty(), noIngredientPane.heightProperty()));
    }

    /**
     * A method that creates an error alert with a heading and content
     *
     * @param header The header of the error
     * @param content the content of the error
     */
    private void showError(String header, String content) {
        ResourceBundle props = getTranslationsBundle();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(props.getString("alert.error"));
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * When the add button is pressed for a second time after entering the
     * values in the boxes it creates a new ingredient and also updates the
     * sideBar with the new ingredient.
     */
    @FXML
    void addIngredient() {
        ResourceBundle props = getTranslationsBundle();

        if (!addIngredientPane.isVisible()) {
            addMode();
            return;
        }

        try {
            String name = newIngName.getText().trim();
            double protein = Double.parseDouble(newIngProteins.getText().trim());
            double carb = Double.parseDouble(newIngCarbs.getText().trim());
            double fat = Double.parseDouble(newIngFats.getText().trim());

            if (name.isEmpty()) {
                showError(props.getString("alert.error"), props.getString("label.ingredientNameLabel"));
                return;
            }
            if (protein < 0 || carb < 0 || fat < 0) {
                showError(props.getString("alert.error"), props.getString("alert.illegalInput.body"));
                return;
            }

            Ingredient toCreate = new Ingredient(name, protein, fat, carb);
            System.out.println("Creating ingredient: " + toCreate.getName());

            Long id = ingredientServerUtils.createIngredient(toCreate);
            System.out.println("Created ingredient with ID: CTRL " + id);
            toCreate.setId(id);

            System.out.println("Saved ingredient ID: " + toCreate.getId());

            sidebarList.getItems().add(toCreate);
            sidebarList.getSelectionModel().select(toCreate);

            newIngName.clear();
            newIngProteins.clear();
            newIngFats.clear();
            newIngCarbs.clear();
            updateSelectedIngredient();
            defaultMode();

        } catch (NumberFormatException e) {
            showError(props.getString("label.invalidInput"), props.getString("label.enterValidNumbers"));
        } catch (IllegalArgumentException e) {
            showError(props.getString("label.invalidIngredient"), e.getMessage());
        } catch (ProcessingException e) {
            showError(props.getString("label.serverError"), e.getMessage());
        }
    }

    /**
     * It removes the selected ingredient from the database and from the
     * sideBar. Before deleting it asks the user for confirmation.
     */
    @FXML
    void removeIngredient() {
        ResourceBundle props = getTranslationsBundle();
        Ingredient selected = sidebarList.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showError(props.getString("label.noIngredientSelected"), 
                     props.getString("label.selectIngredientToRemove"));
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(props.getString("label.deleteIngredientTitle"));
        confirm.setHeaderText(props.getString("label.deleteIngredientConfirm") + " " + selected.getName() + "?");
        
        int usageCount = getUsageCount(selected.getId());
        confirm.setContentText(props.getString("label.deleteIngredientInfo") + "\n"
            + props.getString("label.deleteIngredientUsedIn") + " " + usageCount + " " 
            + props.getString("label.recipes") + ".");
        
        var result = confirm.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            ingredientServerUtils.deleteIngredient(selected.getId());
            sidebarList.getItems().remove(selected);
        } catch (NoSuchElementException e) {
            showError(props.getString("label.ingredientNotFound"), 
                     props.getString("label.ingredientAlreadyRemoved"));
            sidebarList.getItems().remove(selected);
        } catch (ProcessingException e) {
            showError(props.getString("label.serverError"), e.getMessage());
        }
    }

    /**
     * It enters edit mode and shows the details of the selected ingredient.
     */
    @FXML
    void editIngredient() {
        ResourceBundle props = getTranslationsBundle();
        Ingredient selected = sidebarList.getSelectionModel().getSelectedItem();
        updateSelectedIngredient();

        if (selected == null) {
            showError(props.getString("label.noIngredientSelected"), 
                     props.getString("label.selectIngredientToEdit"));
            return;
        }

        editMode();
    }

    /**
     * When the user presses confirm the new values for the ingredient replace
     * the old values. If the name is changed it will also update it in the
     * sideBar
     */
    @FXML
    void confirmEdit() {
        ResourceBundle props = getTranslationsBundle();
        Ingredient selected = sidebarList.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showError(props.getString("label.noIngredientSelected"), 
                     props.getString("label.selectIngredientToEdit"));
            return;
        }

        try {
            String name = ingredientName.getText().trim();
            double protein = Double.parseDouble(proteinValue.getText().trim());
            double carb = Double.parseDouble(carbValue.getText().trim());
            double fat = Double.parseDouble(fatValue.getText().trim());

            if (name.isEmpty()) {
                throw new IllegalArgumentException(props.getString("label.ingredientNameLabel"));
            }
            if (protein < 0 || carb < 0 || fat < 0) {
                throw new IllegalArgumentException(props.getString("alert.illegalInput.body"));
            }

            selected.setName(name);
            selected.setProteinPer100g(protein);
            selected.setCarbsPer100g(carb);
            selected.setFatPer100g(fat);

            ingredientServerUtils.replaceIngredient(selected.getId(), selected);

            sidebarList.getItems().clear();
            sidebarList.getItems().addAll(ingredientServerUtils.getAllIngredients());

            sidebarList.getSelectionModel().select(selected);

            updateSelectedIngredient();

            defaultMode();

        } catch (NumberFormatException e) {
            showError(props.getString("label.invalidInput"), 
                     props.getString("label.enterValidNumbers"));
        } catch (IllegalArgumentException e) {
            showError(props.getString("label.invalidIngredient"), e.getMessage());
        } catch (ProcessingException e) {
            showError(props.getString("label.serverError"), e.getMessage());
        }
    }

    /**
     * When the user presses cancel, the user gets out of the editing mode to
     * the default mode.
     */
    @FXML
    void cancelEdit() {
        updateSelectedIngredient();

        defaultMode();
    }

    /**
     * This method updates the usageLabel to the one from the ingredient that is
     * selected.
     *
     * @param ingredient The ingredient to count in recipes
     */
    private void updateUsageLabel(Ingredient ingredient) {
        ResourceBundle props = getTranslationsBundle();
        
        if (ingredient == null || ingredient.getId() == null) {
            RecipeUsageValueLabel.setText(props.getString("label.usedIn") + " 0 " 
                + props.getString("label.recipes"));
            return;
        }

        try {
            int usage = getUsageCount(ingredient.getId());
            RecipeUsageValueLabel.setText(props.getString("label.usedIn") + " " + usage + " " 
                + props.getString("label.recipes"));
        } catch (NoSuchElementException e) {
            RecipeUsageValueLabel.setText(props.getString("label.usedIn") + " 0 " 
                + props.getString("label.recipes"));
        } catch (ProcessingException e) {
            RecipeUsageValueLabel.setText(props.getString("label.usedInUnknown"));
        }
    }

    private int getUsageCount(Long ingredientId) {
        int usage = 0;

        List<Recipe> recipes = recipeServerUtils.getAllRecipes();

        for (Recipe r : recipes) {
            if (r.getIngredients() == null) continue;

            for (RecipeIngredient rIng : r.getIngredients()) {
                if (rIng.getIngredient() != null && Objects.equals(rIng.getIngredient().getId(), ingredientId)) {
                    usage++;
                    break;
                }
            }
        }

        return usage;
    }



    @FXML
    void updateIngredientName() {
        System.out.println("User wants to update ingredient name.");
    }

    @FXML
    void updateIngredientProtein() {
        System.out.println("User wants to update ingredient protein.");
    }

    @FXML
    void updateIngredientFat() {
        System.out.println("User wants to update ingredient fat.");
    }

    @FXML
    void updateIngredientCarbs() {
        System.out.println("User wants to update ingredient carbs.");
    }

    @FXML
    void seeSelectedIngredient() {
        Ingredient selected = sidebarList.getSelectionModel().getSelectedItem();
        System.out.println("Selected ingredient: " + selected.getId());
    }

    @FXML
    void showHealthChart() {
        ResourceBundle props = getTranslationsBundle();
        Ingredient selectedIngredient = sidebarList.getSelectionModel().getSelectedItem();

        if (selectedIngredient == null) {
            showError(props.getString("label.noSelection"), 
                     props.getString("label.selectIngredientFirst"));
            return;
        }

        showPieChart(selectedIngredient);
    }

    void showPieChart(Ingredient ingredient) {
        ResourceBundle props = getTranslationsBundle();
        
        PieChart.Data protein = new PieChart.Data(
                props.getString("label.proteinLabel"), ingredient.getProteinPer100g());
        PieChart.Data fat = new PieChart.Data(
                props.getString("label.fatLabel"), ingredient.getFatPer100g());
        PieChart.Data carbs = new PieChart.Data(
                props.getString("label.carbohydratesLabel"), ingredient.getCarbsPer100g());

        PieChart pieChart = new PieChart();
        pieChart.getData().addAll(protein, fat, carbs);
        pieChart.setTitle(props.getString("label.healthinessOf") + " " + ingredient.getName());

        VBox root = new VBox(pieChart);

        Label healthiness = new Label();
        Label kcalInfo = new Label();

        double kcals = ingredient.getKcalPer100g();
        kcalInfo.setText(props.getString("label.kcalInfo") + " " + kcals + " " 
            + props.getString("label.kcalUnit"));

        if (kcals < 200) {
            healthiness.setText(props.getString("label.ingredientHealthy"));
        } else if (kcals < 500) {
            healthiness.setText(props.getString("label.ingredientModerate"));
        } else {
            healthiness.setText(props.getString("label.ingredientUnhealthy"));
        }

        root.setAlignment(Pos.CENTER);
        root.getChildren().add(kcalInfo);
        root.getChildren().add(healthiness);

        Stage popup = new Stage();
        popup.setTitle(props.getString("label.healthOverview"));
        popup.setScene(new Scene(root, 400, 400));
        popup.setResizable(false);
        popup.show();
    }



    private ResourceBundle getTranslationsBundle() {
        try {
            String languageCode = configService.getLanguage();
            return java.util.ResourceBundle.getBundle("client.scenes.Translations", new java.util.Locale(languageCode));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load language bundle for code: " + configService.getLanguage());
            return java.util.ResourceBundle.getBundle("client.scenes.Translations", java.util.Locale.ENGLISH);
        }
    }
}