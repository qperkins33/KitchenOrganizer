package org.example.kitchenorganizer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController {

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
        // Open settings view
    }
}
