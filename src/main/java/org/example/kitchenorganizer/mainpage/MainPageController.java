package org.example.kitchenorganizer.mainpage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.kitchenorganizer.ExampleUser;
import org.example.kitchenorganizer.classes.Food;
import org.example.kitchenorganizer.classes.User;
import org.example.kitchenorganizer.notification.Notification;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * TODO: In Milestone 4, increase cohesion. Currently, this class has too many different jobs.
 */
public class MainPageController implements Initializable {

    User user;
    int currentCollection;

    @FXML
    private VBox centerVBox; // VBox used to display main content in center of page (Foods)
    @FXML
    private Text userName;

    /**
     * Initializes a test user and calls displayFoods() for current inventory
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        user = ExampleUser.testUser(); // Obtain test user
        currentCollection = 0;

        userName.setText(user.getName());
        // Display the collection 1 at index 0
        if (!user.getFoodInventoryList().isEmpty()) {
            displayFoods(user.getFoodInventoryList().get(currentCollection).getItemsList());
        }
    }

    /**
     * Displays foods from user's current inventory on the main page
     * @param foods
     */
    private void displayFoods(List<Food> foods) {
        HBox currentRow = new HBox();
        currentRow.setAlignment(Pos.TOP_CENTER);
        currentRow.getStyleClass().add("foodRow");
        int count = 0;

        for (Food food : foods) {
            if (count % 3 == 0 && count > 0) { // rows of 3
                centerVBox.getChildren().add(currentRow);
                currentRow = new HBox();
                currentRow.setAlignment(Pos.TOP_CENTER);
                currentRow.getStyleClass().add("foodRow");
            }

            VBox foodCell = new VBox();
            foodCell.setAlignment(Pos.CENTER);
            foodCell.getStyleClass().add("foodCell");

            // Food info
            HBox foodNameBox = new HBox();
            foodNameBox.getStyleClass().add("foodName");
            foodNameBox.setAlignment(Pos.CENTER);
            Text foodName = new Text(food.getName());

            foodNameBox.getChildren().add(foodName);

            Text expDateText = new Text("Days to Expiration: " + String.valueOf(food.getExpDate())); // Convert int to String
            Text quantityText = new Text(food.getMeasurementUnit() + ": " + String.format("%.2f", food.getQuantity())); // Format double to String
            Text minQuantityText = new Text("Min " + food.getMeasurementUnit() + ": " + food.getMinQuantity());

            // Buttons
            HBox buttons = new HBox();
            buttons.setAlignment(Pos.CENTER);
            Button minus = new Button("-");
            Button plus = new Button("+");
            buttons.getChildren().addAll(minus, plus);

            // Add Food info and Buttons
            foodCell.getChildren().addAll(foodNameBox, expDateText, quantityText, minQuantityText, buttons);

            // Add foodCell to currentRow
            currentRow.getChildren().add(foodCell);
            count++;
        }

        // Add the last row if not already added
        if (!currentRow.getChildren().isEmpty()) {
            centerVBox.getChildren().add(currentRow);
        }
    }

    @FXML
    private Label searchResult; // Reference to the Label added in FXML (using to test if searchbar works)

    @FXML
    private TextField searchBar;

    @FXML
    private void handleSearch() {
        searchResult.setText(" Search for: " + searchBar.getText()); //test
        // Implement search logic
    }

    /**
     * Popup for settings
     */
    @FXML
    private void openSettings() {
        Dialog<Void> settingsPopup = new Dialog<>();
        settingsPopup.setTitle("Settings");

        // Set the custom dialog layout
        VBox layout = new VBox(20);
        CheckBox toggleNotifications = new CheckBox("Notifications on/off");
        Button logoutButton = new Button("Logout");
        layout.getChildren().addAll(toggleNotifications, logoutButton);

        settingsPopup.getDialogPane().setContent(layout);
        settingsPopup.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE); // Add a close button

        // Handle the logout button action
        logoutButton.setOnAction(e -> {
            logout();
            settingsPopup.close();
        });

        settingsPopup.showAndWait();
    }

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

    /**
     * Logout button located within settings
     */
    private void logout() {
        // TODO: Make settings logout button work
    }
}

