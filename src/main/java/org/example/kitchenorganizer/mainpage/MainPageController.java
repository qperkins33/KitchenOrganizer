package org.example.kitchenorganizer.mainpage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.example.kitchenorganizer.classes.User;
import org.example.kitchenorganizer.database.DatabaseInitializer;
import org.example.kitchenorganizer.database.DatabaseMethods;
import org.example.kitchenorganizer.notification.Notification;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.example.kitchenorganizer.database.DatabaseMethods.*;

/**
 * TODO: Make check inventory check database contents, search, logout button, call check inventory when signed in
 */
public class MainPageController implements Initializable {

    public String currentCollectionName; //TODO: Make available in FoodDisplayController

    User user;
    int currentCollection;

    @FXML
    private ComboBox<String> kitchenSelectorComboBox;
    @FXML
    private VBox foodsCenterVBox; // VBox used to display main content in center of page (Foods)
    @FXML
    private Text userName;
    @FXML
    private Text pageInfoText;
    @FXML
    private ComboBox<String> sortBy;
    private FoodDisplayController foodDisplayController;
    private KitchenController kitchenController;
    private DialogController dialogController;
    /**
     * Initializes a test user and calls displayFoods() for current inventory
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentCollection = 0; // value to change displayed collection. (TEST)

        user = User.getCurrentUser(); // use in actual program (QUIN)

        foodDisplayController = new FoodDisplayController(foodsCenterVBox, sortBy, pageInfoText);
        kitchenController = new KitchenController(foodDisplayController, kitchenSelectorComboBox, this);
        dialogController = new DialogController(foodDisplayController, this);

        if (user != null) {
            userName.setText(user.getName());
            kitchenController.addKitchensToKitchenSelectorComboBox();

            if (!user.getFoodInventoryList().isEmpty()) {
                currentCollectionName = user.getFoodInventoryList().get(currentCollection).getCollectionName();
                foodDisplayController.updateFoodDisplayByCollectionName(currentCollectionName);
            }
        }
    }

    //*********************************************************************
    // KITCHEN COLLECTION
    @FXML
    private void handleKitchenSelectionComboBox() {
        kitchenController.handleKitchenSelectionComboBox();
    }
    @FXML
    private void showAddCollectionDialog() {
        kitchenController.showAddCollectionDialog();
    }

    @FXML
    private void showRemoveCollectionDialog() {
        kitchenController.showRemoveCollectionDialog();
    }

    //*********************************************************************
    @FXML
    private TextField searchBar;

    @FXML
    private void handleSearch() {
        foodDisplayController.search(searchBar.getText().trim(), currentCollectionName);
    }

    // TODO: incrementPageNum and decrementPageNum do not properly change page.
    //  The new page will not be up to date because user may have changed the contents of the database
    //  and the currentCollectionName will also not be correct. So if a user searches, the search result
    //  will be from page 1 and never from page 2. I commented out the sections that do not work.
    //  Either we can fix it or just delete them entirely because the Select Kitchen drop down box works.
    //  But currently more issues were created than solved when implementing previous page and next page.
    @FXML
    private void previousPage() {
        foodDisplayController.decrementPageNum();
    }
    @FXML
    private void nextPage() {
        foodDisplayController.incrementPageNum();
    }
    //*********************************************************************
    @FXML
    private void showSettingsDialog(ActionEvent event) {
        dialogController.showSettingsDialog(event);
    }
    //*********************************************************************
    @FXML
    private void showCheckAllInventoryDialog() { // Notify user about foods where Quantity < MinQuantity and foods where expDateDays < 0
        dialogController.showCheckAllInventoryDialog();
    }

    // TODO
    @FXML
    public void showCheckCurrentInventoryDialog(ActionEvent actionEvent) { // Notify user about foods where Quantity < MinQuantity and foods where expDateDays < 0
        dialogController.showCheckCurrentInventoryDialog(actionEvent);
    }
    //*********************************************************************
    // ADD NEW FOOD

    /**
     * Popup that allows user to enter new foods into inventory
     */

    private void populateCollectionNameComboBox(ComboBox<String> comboBox) {
        List<String> collectionNames = getCollectionNamesForUser(user.getId());
        comboBox.getItems().addAll(collectionNames);
    }
    @FXML
    private void showAddNewItemDialog() {
        dialogController.showAddNewItemDialog();
    }

    //*********************************************************************

    @FXML
    private void sort() {
        // Refresh display with sorted foods based on current kitchen and sort selection
        String selectedKitchen = kitchenSelectorComboBox.getSelectionModel().getSelectedItem();
        if (selectedKitchen != null) {
            foodDisplayController.updateFoodDisplayByCollectionName(selectedKitchen); // sorting is done in updateFoodDisplay()
        }
    }
}

