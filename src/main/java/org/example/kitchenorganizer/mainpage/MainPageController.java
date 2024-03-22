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
import org.example.kitchenorganizer.classes.User;
import org.example.kitchenorganizer.database.DatabaseInitializer;
import org.example.kitchenorganizer.notification.Notification;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

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
        user = ExampleUser.testUser(); // Obtain example test user (TEST)

        /**
         * Use this to user the actual signed in user.
         * It works, but I commented it out because it's empty
         * and so that Austin can test the foodDisplayController on the Example User.
         */
//        user = User.getCurrentUser(); // use in actual program (QUIN)
        foodDisplayController = new FoodDisplayController(sortBy, centerVBox);

        if (user != null) {
            userName.setText(user.getName());
//            addKitchensToKitchenSelector(); // use in actual program (QUIN)

//             Test to Display the collection 1 at index 0
            if (!user.getFoodInventoryList().isEmpty()) { // not used in actual program (TEST)
                foodDisplayController.displayFoods(user.getFoodInventoryList().get(currentCollection).getItemsList());
            }
        }
    }

    //*********************************************************************
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
        updateFoodDisplay(selectedKitchen);
    }

    private void updateFoodDisplay(String selectedKitchen) {
        // Code to update the displayed food based on selected kitchen...
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

                // ******************************************
                // TODO: Add  method to add the collection to user's database
                // ******************************************
            }
            return null;
        });

        dialog.showAndWait();
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

                // ******************************************
                // TODO: Add  method to remove the collection from user's database
                // ******************************************
            }
            return null;
        });

        dialog.showAndWait();
    }
    //*********************************************************************





    //*********************************************************************
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
    /**
     * Popup for settings
     */
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
    /**
     * Popup that displays food from all inventories that are low quantity
     */
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
    /**
     * Popup that allows user to enter new foods into inventory
     */
    @FXML
    public void showAddFoodDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Food");
        ButtonType submitButtonType = new ButtonType("Submit");
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

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

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Quantity:"), 0, 1);
        grid.add(quantityField, 1, 1);
        grid.add(new Label("Measurement Unit:"), 0, 2);
        grid.add(measurementUnitField, 1, 2);
        grid.add(new Label("Minimum Quantity:"), 0, 3);
        grid.add(minQuantityField, 1, 3);
        grid.add(new Label("Days Until Expiration:"), 0, 4);
        grid.add(expDateField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                String name = nameField.getText();
                double quantity = Double.parseDouble(quantityField.getText());
                String measurementUnit = measurementUnitField.getText();
                double minQuantity = Double.parseDouble(minQuantityField.getText());
                int expDate = Integer.parseInt(expDateField.getText());

                // ******************************************
                // TODO: Add  method to add the food to your inventory
                // ******************************************
            }
            return null;
        });

        dialog.showAndWait();
    }

    //*********************************************************************
    @FXML
    public void sort() {
        foodDisplayController.sort(user.getFoodInventoryList().get(currentCollection));
        foodDisplayController.displayFoods(user.getFoodInventoryList().get(currentCollection).getItemsList());
    }

    //*********************************************************************
    /**
     * Logout button located within settings
     */
    private void logout() {
        // TODO: Make settings logout button work
    }
}

