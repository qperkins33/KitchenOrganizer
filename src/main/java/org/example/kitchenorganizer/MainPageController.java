package org.example.kitchenorganizer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainPageController {
    @FXML
    private Label searchResult; // Reference to the Label added in FXML

    @FXML
    private TextField searchBar;

    @FXML
    private void handleSearch() {
        searchResult.setText("Search for: " + searchBar.getText());

        // Implement search logic
    }

    @FXML
    private void openSettings() {
        // Open settings view
    }
}
