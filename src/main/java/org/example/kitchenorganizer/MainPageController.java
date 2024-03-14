package org.example.kitchenorganizer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.kitchenorganizer.classes.InventoryItem;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController {

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
        // Logout logic
    }
}

