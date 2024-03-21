package org.example.kitchenorganizer.mainpage;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.example.kitchenorganizer.classes.FoodCollection;


public class SearchAndSortController {

    @FXML
    private ComboBox<String> sortBy;

    public SearchAndSortController(ComboBox<String> sortBy) {
        this.sortBy = sortBy;
    }

    @FXML
    public void sort(FoodCollection foodList) {
        if (sortBy.getValue().equals("Name")) {
            foodList.sortByName();
        }
        else if (sortBy.getValue().equals("Expiration")) {
            foodList.sortByExpiration();
        }
    }
}
