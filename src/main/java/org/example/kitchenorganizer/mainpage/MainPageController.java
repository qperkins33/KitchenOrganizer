package org.example.kitchenorganizer.mainpage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.kitchenorganizer.ExampleUser;
import org.example.kitchenorganizer.classes.Food;
import org.example.kitchenorganizer.classes.FoodCollection;
import org.example.kitchenorganizer.classes.User;
import org.example.kitchenorganizer.database.DatabaseInitializer;
import org.example.kitchenorganizer.notification.Notification;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * TODO: In Milestone 4, increase cohesion. Currently, this class has too many different jobs.
 */
public class MainPageController implements Initializable {

    User user;
    int currentCollection;

    @FXML
    private ComboBox<String> kitchenSelector;
    @FXML
    private VBox centerVBox; // VBox used to display main content in center of page (Foods)
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

        /**
         * Use this to user the actual signed in user.
         * It works, but I commented it out because it's empty
         * and so that Austin can test the foodDisplayController on the Example User.
         */
        user = User.getCurrentUser(); // use in actual program (QUIN)

        foodDisplayController = new FoodDisplayController(sortBy, centerVBox);

        if (user != null) {
            userName.setText(user.getName());
            addKitchensToKitchenSelector(); // use in actual program (QUIN)

            if (!user.getFoodInventoryList().isEmpty()) { // not used in actual program (TEST)
                foodDisplayController.displayFoods(user.getFoodInventoryList().get(currentCollection).getItemsList());
            }
        }
    }

    //*********************************************************************
    // KITCHEN COLLECTION

    private void addKitchensToKitchenSelector() {
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
        kitchenSelector.setItems(kitchens);
        if (!kitchens.isEmpty()) {
            kitchenSelector.getSelectionModel().selectFirst();
            handleKitchenSelection(); // Automatically display foods for the first kitchen
        }
    }

    @FXML
    private void handleKitchenSelection() {
        String selectedKitchen = kitchenSelector.getSelectionModel().getSelectedItem();
        if (selectedKitchen != null) {
            updateFoodDisplay(selectedKitchen);
        }
    }

    private void updateFoodDisplay(String selectedKitchen) {
        int userId = User.getCurrentUser().getId();
        int collectionId = findCollectionIdByNameAndUserId(selectedKitchen, userId);

        String sortOrder = "name"; // Default sort order
        if (sortBy.getValue() != null && sortBy.getValue().equals("Expiration")) {
            sortOrder = "expDate";
        }

        List<Food> foods = fetchSortedFoods(collectionId, sortOrder);
        // Assuming FoodDisplayController is correctly set up to display these foods
        foodDisplayController.displayFoods(foods);
    }

    private List<Food> fetchSortedFoods(int collectionId, String sortOrder) {
        List<Food> foods = new ArrayList<>();
        String sql = "SELECT * FROM Foods WHERE collectionId = ? ORDER BY " + sortOrder;

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, collectionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    foods.add(new Food(
                            rs.getString("name"),
                            rs.getDouble("quantity"),
                            rs.getString("measurementUnit"),
                            rs.getDouble("minQuantity"),
                            rs.getInt("expDate")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foods;
    }

    public void refreshKitchenSelector() {
        ObservableList<String> kitchens = FXCollections.observableArrayList();
        String sql = "SELECT name FROM FoodCollections WHERE userId = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getId()); // Assuming user ID is accessible
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                kitchens.add(rs.getString("name"));
            }

            kitchenSelector.setItems(kitchens);

            // Optionally, select a default or newly added kitchen
            if (!kitchens.isEmpty()) {
                kitchenSelector.getSelectionModel().selectFirst();
                // You may want to update the display based on the newly selected kitchen
                updateFoodDisplay(kitchenSelector.getSelectionModel().getSelectedItem());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showAddCollectionDialog() {
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
                    addCollectionToSignedInUsersDatabase(name, user.getId());
                    refreshKitchenSelector();
                }

            }
            return null;
        });

        dialog.showAndWait();
    }

    private void addCollectionToSignedInUsersDatabase(String collectionName, int userId) {
        String sql = "INSERT INTO FoodCollections (name, userId) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collectionName);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();

            System.out.println("New collection added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding new collection: " + e.getMessage());
        }
    }

    @FXML
    public void showRemoveCollectionDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Remove Collection");
        ButtonType submitButtonType = new ButtonType("Submit");
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField collectionNameField = new TextField();
        collectionNameField.setPromptText("Collection Name");

        grid.add(new Label("Remove Collection (Name):"), 0, 0);
        grid.add(collectionNameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                String name = collectionNameField.getText();

                // Ensure the name field is not empty and a user is signed in
                if (!name.isEmpty() && user != null) {
                    removeCollectionFromSignedInUsersDatabase(name, user.getId());
                    refreshKitchenSelector();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void removeCollectionFromSignedInUsersDatabase(String collectionName, int userId) {
        String sql = "DELETE FROM FoodCollections WHERE name = ? AND userId = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collectionName);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Collection '" + collectionName + "' removed successfully.");
            } else {
                System.out.println("No collection found with the specified name for this user.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error removing collection: " + e.getMessage());
        }
    }

    //*********************************************************************
    // SEARCH

    @FXML
    private Label searchResult; // Reference to the Label added in FXML (using to test if searchbar works)

    @FXML
    private TextField searchBar;

    @FXML
    private void handleSearch() {
        foodDisplayController.search(searchBar.getText(), user.getFoodInventoryList().get(currentCollection));

        searchResult.setText(" Search for: " + searchBar.getText()); //test
        // Implement search logic
    }
    //*********************************************************************
    // SETTINGS POPUP

    @FXML
    private void openSettings() {
        Dialog<Void> settingsPopup = new Dialog<>();
        settingsPopup.setTitle("Settings");

        // Set the custom dialog layout
        VBox layout = new VBox(20);

        Button logoutButton = new Button("Logout");
        layout.getChildren().addAll(logoutButton);

        settingsPopup.getDialogPane().setContent(layout);
        settingsPopup.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE); // Add a close button

        // Handle the logout button action
        logoutButton.setOnAction(e -> {
            logout();
            settingsPopup.close();
        });

        settingsPopup.showAndWait();
    }
    //*********************************************************************
    // NOTIFICATIONS

    // TODO: INTERACT WITH USER DATABASE
    @FXML
    public void showCheckInventoryDialog() {
        Notification notification = new Notification(user); // Create an instance of Notification
        String lowInventoryNotifications = notification.gatherLowInventoryFoods(); // Get the low inventory foods

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
    @FXML
    public void showAddFoodDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Food");
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField collectionNameField = new TextField();
        collectionNameField.setPromptText("Collection Name");
        grid.add(new Label("Collection Name:"), 0, 0);
        grid.add(collectionNameField, 1, 0);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        TextField measurementUnitField = new TextField();
        measurementUnitField.setPromptText("Measurement Unit");
        TextField minQuantityField = new TextField();
        minQuantityField.setPromptText("Minimum Quantity");
        TextField expDateField = new TextField();
        expDateField.setPromptText("Days Until Expiration");

        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(new Label("Measurement Unit:"), 0, 3);
        grid.add(measurementUnitField, 1, 3);
        grid.add(new Label("Minimum Quantity:"), 0, 4);
        grid.add(minQuantityField, 1, 4);
        grid.add(new Label("Days Until Expiration:"), 0, 5);
        grid.add(expDateField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                String collection = collectionNameField.getText();
                String name = nameField.getText();
                double quantity = Double.parseDouble(quantityField.getText());
                String measurementUnit = measurementUnitField.getText();
                double minQuantity = Double.parseDouble(minQuantityField.getText());
                int expDate = Integer.parseInt(expDateField.getText());

                // Add the food to the specified collection in the database
                addFoodToCollection(collection, name, quantity, measurementUnit, minQuantity, expDate);
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void addFoodToCollection(String collectionName, String name, double quantity, String measurementUnit, double minQuantity, int expDate) {
        int userId = User.getCurrentUser().getId(); // This method should return the current user's ID
        int collectionId = findCollectionIdByNameAndUserId(collectionName, userId);

        if (collectionId != -1) {
            String sql = "INSERT INTO Foods (collectionId, name, quantity, measurementUnit, minQuantity, expDate) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, collectionId);
                pstmt.setString(2, name);
                pstmt.setDouble(3, quantity);
                pstmt.setString(4, measurementUnit);
                pstmt.setDouble(5, minQuantity);
                pstmt.setInt(6, expDate); // Adjust if you're converting to a different date format
                pstmt.executeUpdate();
                System.out.println("New food added successfully to the collection.");

                //TODO: add refresh
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error adding new food: " + e.getMessage());
            }
        } else {
            System.out.println("Collection not found or access error.");
        }
    }

    private int findCollectionIdByNameAndUserId(String collectionName, int userId) {
        String sql = "SELECT id FROM FoodCollections WHERE name = ? AND userId = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, collectionName);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error finding collection ID: " + e.getMessage());
        }
        return -1; // Return -1 if not found or error
    }



    //*********************************************************************
//    I made a new sort() with interacts with the database

//    @FXML
//    public void sort() {
//
//        foodDisplayController.sort(user.getFoodInventoryList().get(currentCollection));
//        foodDisplayController.displayFoods(user.getFoodInventoryList().get(currentCollection).getItemsList());
//    }

    @FXML
    public void sort() {
        // Refresh display with sorted foods based on current kitchen and sort selection
        String selectedKitchen = kitchenSelector.getSelectionModel().getSelectedItem();
        if (selectedKitchen != null) {
            updateFoodDisplay(selectedKitchen);
        }
    }


    //*********************************************************************
    /**
     * Logout button located within settings
     */
    private void logout() {
        // TODO: Make settings logout button work
    }
}

