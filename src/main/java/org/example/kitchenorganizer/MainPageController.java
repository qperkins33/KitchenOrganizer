package org.example.kitchenorganizer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.kitchenorganizer.classes.Food;
import org.example.kitchenorganizer.classes.FoodCollection;
import org.example.kitchenorganizer.classes.InventoryItem;
import org.example.kitchenorganizer.classes.User;
import org.example.kitchenorganizer.notification.Notification;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    @FXML
    private VBox centerVBox;
    @FXML
    private Text userName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User user = ExampleUser.testUser(); // Obtain test user
        userName.setText(user.getName());
        // Display the collection 1 at index 0
        if (!user.getFoodInventoryList().isEmpty()) {
            displayFoods(user.getFoodInventoryList().get(0).getItemsList());
        }
    }

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
    private Label loginMessageLabel;

    @FXML
    private Label searchResult; // Reference to the Label added in FXML (using to test if searchbar works)

    @FXML
    private TextField searchBar;

    @FXML
    private void handleSearch() {
        searchResult.setText("Search for: " + searchBar.getText());

        // Implement search logic
    }

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

    @FXML
    public void showCheckInventoryDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Low Inventory Notification");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        dialog.showAndWait();
    }

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
                // Add  method to add the food to your inventory
                // ******************************************
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void logout() {
        // Implement logout logic
    }
}

