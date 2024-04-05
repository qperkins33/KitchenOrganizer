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
    private ComboBox<String> sortBy;
    private FoodDisplayController foodDisplayController;
    private KitchenController kitchenController;
    /**
     * Initializes a test user and calls displayFoods() for current inventory
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentCollection = 0; // value to change displayed collection. (TEST)

        user = User.getCurrentUser(); // use in actual program (QUIN)

        foodDisplayController = new FoodDisplayController(foodsCenterVBox, sortBy);
        kitchenController = new KitchenController(foodDisplayController, kitchenSelectorComboBox);

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
        Dialog<Void> settingsPopup = new Dialog<>();
//        settingsPopup.setTitle("Settings");

        // Set the custom dialog layout
        VBox layout = new VBox();

        Text settingsText = new Text("Settings");

        Button deleteAccountButton = new Button("Delete Account");

        Button logoutButton = new Button("Logout");

        layout.getChildren().addAll(settingsText, deleteAccountButton, logoutButton);
        settingsPopup.getDialogPane().setContent(layout);
        settingsPopup.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE); // Add a close button

        deleteAccountButton.setOnAction(actionEvent -> {
            // Display a confirmation dialog
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirm Action");
            confirmationDialog.setHeaderText("Delete Account");
            confirmationDialog.setContentText("Are you sure you want to delete your account?");

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                DatabaseMethods.deleteUserByUserId(user.getId());
                handleLogout(event);
                settingsPopup.close();
            }
        });

        // Handle the logout button action
        logoutButton.setOnAction(e -> {
            handleLogout(event);
            settingsPopup.close();
        });

        settingsPopup.showAndWait();
    }
    //*********************************************************************
    @FXML
    private void showCheckAllInventoryDialog() { // Notify user about foods where Quantity < MinQuantity and foods where expDateDays < 0
        Notification notification = new Notification(user.getId()); // Create an instance of Notification
        String lowInventoryNotifications = notification.gatherNotifications(); // Get the low inventory foods

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Low Inventory Notification");

        VBox content = new VBox();
        TextArea textArea = new TextArea(lowInventoryNotifications); // Use the notifications string directly
        textArea.setEditable(false);
        content.getChildren().add(textArea);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.showAndWait();
    }

    // TODO
    @FXML
    public void showCheckCurrentInventoryDialog(ActionEvent actionEvent) { // Notify user about foods where Quantity < MinQuantity and foods where expDateDays < 0
        Notification notification = new Notification(user.getId(), currentCollectionName); // Create an instance of Notification
        String lowInventoryNotifications = notification.gatherNotifications(); // Get the low inventory foods

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Low Inventory Notification");

        VBox content = new VBox();
        TextArea textArea = new TextArea(lowInventoryNotifications); // Use the notifications string directly
        textArea.setEditable(false);
        content.getChildren().add(textArea);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.showAndWait();
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
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Item");
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<String> collections = new ComboBox<>();
        collections.setPromptText("Select Collection");
        populateCollectionNameComboBox(collections);
        grid.add(new Label("Collection:"), 0, 0);
        grid.add(collections, 1, 0);

        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");

        ComboBox<String> measurementUnitDropdown = new ComboBox<>();
        measurementUnitDropdown.getItems().addAll(
                "Quantity",
                "Grams (g)",
                "Kilograms (kg)",
                "Ounces (oz)",
                "Pounds (lbs)",
                "Liters (L)",
                "Milliliters (mL)",
                "Cups",
                "Fluid Ounces (fl oz)",
                "Tablespoons (tbsp)",
                "Teaspoons (tsp)",
                "Gallons (gal)",
                "Quarts (qt)",
                "Pints (pt)"
        );
        measurementUnitDropdown.setPromptText("Select Measurement Unit");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        TextField minQuantityField = new TextField();
        minQuantityField.setPromptText("Minimum Quantity");
        TextField expDateField = new TextField();
        expDateField.setPromptText("Days Until Expiration");

        grid.add(new Label("Item Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Measurement Unit:"), 0, 2);
        grid.add(measurementUnitDropdown, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Minimum Quantity:"), 0, 4);
        grid.add(minQuantityField, 1, 4);
        grid.add(new Label("Days Until Expiration:"), 0, 5);
        grid.add(expDateField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) { // Empty form check
                // Checks if all fields are filled
                if (collections.getValue() == null ||
                        nameField.getText().trim().isEmpty() ||
                        measurementUnitDropdown.getValue() == null ||
                        quantityField.getText().trim().isEmpty() ||
                        minQuantityField.getText().trim().isEmpty() ||
                        expDateField.getText().trim().isEmpty()) {

                    // Show an alert dialog to inform the user that all fields are required
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Incomplete Form");
                    alert.setHeaderText("Missing Information");
                    alert.setContentText("Please fill out all fields before submitting.");
                    alert.showAndWait();
                    return null;
                }

                try {
                    String collection = collections.getValue();
                    String name = nameField.getText().trim();
                    double quantity = Double.parseDouble(quantityField.getText().trim());
                    String measurementUnit = measurementUnitDropdown.getValue();
                    double minQuantity = Double.parseDouble(minQuantityField.getText().trim());
                    int expDate = Integer.parseInt(expDateField.getText().trim());

                    if (quantity < 0 || minQuantity < 0 || expDate < 0) { // Negative number check
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Input Error");
                        alert.setHeaderText("Invalid Input Format");
                        alert.setContentText("Please ensure \"Quantity\", \"Minimum Quantity\", and \"Days Until Expiration\" fields contain valid POSITIVE numbers.");
                        alert.showAndWait();
                        return null;
                    }

                    // Add the food to the specified collection in the database
                    addFoodToCollection(collection, name, quantity, measurementUnit, minQuantity, expDate);
                    // Refresh the display
                    foodDisplayController.updateFoodDisplayByCollectionName(currentCollectionName);

                } catch (NumberFormatException e) {
                    // Show an alert dialog to inform the user about the input error
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid Input Format");
                    alert.setContentText("Please ensure numerical fields contain valid numbers.");
                    alert.showAndWait();
                    return null;
                }
            }
            return null; // This will be reached if the cancel button is pressed
        });

        dialog.showAndWait();
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

    @FXML
    private void handleLogout(ActionEvent event) {
        // Clear the current user session
        User.setCurrentUser(null);

        // Navigate back to the login page
        try {
            Parent loader = FXMLLoader.load(getClass().getResource("/org/example/kitchenorganizer/LoginForm.fxml"));
            // Get the current stage from the event's source
            Node source = (Node) event.getSource();
            if (source != null) {
                Stage stage = (Stage) source.getScene().getWindow();

                // Check if stage is not null before proceeding
                if (stage != null) {
                    Scene scene = new Scene(loader);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    System.out.println("Stage is null, can't switch scenes");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

