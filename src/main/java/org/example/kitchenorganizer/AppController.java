package org.example.kitchenorganizer;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import org.example.kitchenorganizer.classes.Food;

public class AppController {
    String searchedText;

    @FXML
    private TextField searchBar;

    @FXML
    private void handleSearch() {
        searchedText = searchBar.getText();

        // Implement search logic
    }

    @FXML
    private void openSettings() {
        // Open settings view
    }
}
