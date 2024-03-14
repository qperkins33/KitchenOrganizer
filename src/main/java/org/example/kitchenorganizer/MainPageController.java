package org.example.kitchenorganizer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.kitchenorganizer.classes.Food;
import org.example.kitchenorganizer.classes.FoodCollection;
import org.example.kitchenorganizer.classes.InventoryItem;
import org.example.kitchenorganizer.classes.User;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    @FXML
    private VBox centerVBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User user = ExampleUser.testUser(); // Obtain test user
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
            Text foodName = new Text(food.getName());
            Text expDateText = new Text("Days to Exp: " + String.valueOf(food.getExpDate())); // Convert int to String
            Text quantityText = new Text("Quantity: " + String.format("%.2f", food.getQuantity())); // Format double to String

            // Buttons
            HBox buttons = new HBox();
            buttons.setAlignment(Pos.CENTER);
            Button minus = new Button("-");
            Button plus = new Button("+");
            buttons.getChildren().addAll(minus, plus);

            // Add Food info and Buttons
            foodCell.getChildren().addAll(foodName, expDateText, quantityText, buttons);

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

    private void logout() {
        // Implement logout logic
    }
}

