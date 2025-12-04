package client.scenes;

import com.google.inject.Inject;
import commons.Recipe;
import commons.RecipeCollection;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddButtonCtrl {
    private MainCtrl mainCtrl;

    @FXML
    private Button recipeAddButton;

    @FXML
    private TextField recipeInput;

    @FXML
    private Label statusLabel;

    //The connection between MainCtrl and AddButtonCtrl
    @Inject
    public AddButtonCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    //Adding Recipe methods
    @FXML
    private void recipeAddRequest() {
        String query = recipeInput.getText().trim();
        if (query == null || query.isEmpty()) {
            System.out.println("Please enter a name."); // Can be removed in the future
            statusLabel.setText("Please enter a name.");
            statusLabel.setStyle("-fx-text-fill: red;");
        } else {
            statusLabel.setText("");
            System.out.println("Adding request for: " + query); // Can be removed in the future
            recipeValidation(query);
        }
    }

    /**
     * Checks whether the given input suffices the criteria
     * Also makes sure the first letter is a capital
     * @param query the given recipe name by the user
     */
    private void recipeValidation(String query) {
        if(query.length()>20){
            statusLabel.setText("The name is longer than 20 characters.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        if(query.matches(".*[\\*\"'\\`@].*")){
            statusLabel.setText("The name contains illegal characters.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        query = query.substring(0, 1).toUpperCase() + query.substring(1);
        Recipe recipe = new Recipe(query);
        addToDb(recipe); // add to database
        mainCtrl.addRecipe(recipe); // update the UI


        recipeInput.clear();
        statusLabel.setText("Recipe added!");
        statusLabel.setStyle("-fx-text-fill: green;");;
    }

    public void addToDb(Recipe recipe){

    }

}

