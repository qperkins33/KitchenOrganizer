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

import org.example.kitchenorganizer.classes.Food;
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

    public String currentCollectionName;

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

    /**
     * Initializes a test user and calls displayFoods() for current inventory
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentCollection = 0; // value to change displayed collection. (TEST)

        user = User.getCurrentUser(); // use in actual program (QUIN)

        foodDisplayController = new FoodDisplayController(foodsCenterVBox);

        if (user != null) {
            userName.setText(user.getName());
            addKitchensToKitchenSelectorComboBox();

            if (!user.getFoodInventoryList().isEmpty()) {
                currentCollectionName = user.getFoodInventoryList().get(currentCollection).getCollectionName();
                foodDisplayController.displayFoods(user.getFoodInventoryList().get(currentCollection).getItemsList());
            }
        }
    }

    //*********************************************************************
    // KITCHEN COLLECTION

    private void addKitchensToKitchenSelectorComboBox() {
        ObservableList<String> kitchens = FXCollections.observableArrayList();
        // Fetch kitchen names from the database
        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT name FROM FoodCollections WHERE userId = ?")) {
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                kitchens.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        kitchenSelectorComboBox.setItems(kitchens);
        if (!kitchens.isEmpty()) {
            kitchenSelectorComboBox.getSelectionModel().selectFirst();
            handleKitchenSelectionComboBox(); // Automatically display foods for the first kitchen
        }
    }

    @FXML
    private void handleKitchenSelectionComboBox() {
        currentCollectionName = kitchenSelectorComboBox.getSelectionModel().getSelectedItem();

        if (currentCollectionName != null) {
            System.out.println("CURRENT KITCHEN: " + currentCollectionName);
            updateFoodDisplayByCollectionName(currentCollectionName);
        }
    }

    private void updateFoodDisplayByCollectionName(String selectedKitchen) {
        int userId = User.getCurrentUser().getId();
        int collectionId = findCollectionIdByNameAndUserId(selectedKitchen, userId);

        String sortOrder = "name"; // Default sort order
        if (sortBy.getValue() != null && sortBy.getValue().equals("Expiration")) {
            sortOrder = "expDate";
        }

        List<Food> foods = fetchSortedFoods(collectionId, sortOrder);
        foodDisplayController.displayFoods(foods);
    }

    private void refreshKitchenSelectorComboBox() {
        ObservableList<String> kitchens = FXCollections.observableArrayList();
        String sql = "SELECT name FROM FoodCollections WHERE userId = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getId()); // Assuming user ID is accessible
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                kitchens.add(rs.getString("name"));
            }

            kitchenSelectorComboBox.setItems(kitchens);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showAddCollectionDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Collection");
        ButtonType submitButtonType = new ButtonType("Submit");
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField collectionNameField = new TextField();
        collectionNameField.setPromptText("Collection Name");

        grid.add(new Label("New Collection Name:"), 0, 0);
        grid.add(collectionNameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                String name = collectionNameField.getText();

                // Call the method to add the collection to the database
                if (user != null && !name.isEmpty()) {
                    addCollectionToUserDatabase(name, user.getId());

                    updateFoodDisplayByCollectionName(currentCollectionName);
                    refreshKitchenSelectorComboBox();
                }

            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void showRemoveCollectionDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Remove Collection");
        ButtonType submitButtonType = new ButtonType("Submit");
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<String> collections = new ComboBox<>();
        collections.setPromptText("Select Collection");
        populateCollectionNameComboBox(collections);
        grid.add(new Label("Collection:"), 0, 0);
        grid.add(collections, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                String name = collections.getValue();

                // Ensure the name field is not empty and a user is signed in
                if (!name.isEmpty() && user != null) {
                    removeCollectionFromSignedInUsersDatabase(name, user.getId());

                    updateFoodDisplayByCollectionName(currentCollectionName);
                    refreshKitchenSelectorComboBox();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    //*********************************************************************
    // TODO SEARCH: Update to work with database, make popup that displays search results

    @FXML
    private Label searchResult; // Reference to the Label added in FXML (using to test if searchbar works)

    @FXML
    private TextField searchBar;

    @FXML
    private void handleSearch() {
        foodDisplayController.search(searchBar.getText(), user.getFoodInventoryList().get(currentCollection));

        searchResult.setText(" Search for: " + searchBar.getText()); //test
        // TODO: Implement search logic
    }
    //*********************************************************************
    // TODO SETTINGS POPUP: enlarge popup and implement logout
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
                handleLogout(event); // TODO: Implement the logout method
                settingsPopup.close();
            }
        });

        // Handle the logout button action
        logoutButton.setOnAction(e -> {
            handleLogout(event); // TODO: Add method
            settingsPopup.close();
        });

        settingsPopup.showAndWait();
    }
    //*********************************************************************
    @FXML
    private void showCheckInventoryDialog() { // Notify user about foods where Quantity < MinQuantity and foods where expDateDays < 0
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
        // TODO: Add better measurement units
        measurementUnitDropdown.getItems().addAll(
                "Quantity", "Grams (g)", "Liters (L)", "Teaspoons (tsp)", "Cups"
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
                // Check if all fields are filled
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
                    updateFoodDisplayByCollectionName(currentCollectionName);

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
            updateFoodDisplayByCollectionName(selectedKitchen); // sorting is done in updateFoodDisplay()
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

