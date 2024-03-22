package org.example.kitchenorganizer.mainpage;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.kitchenorganizer.classes.Food;
import org.example.kitchenorganizer.classes.FoodCollection;

import java.util.ArrayList;
import java.util.List;


public class FoodDisplayController {

    @FXML
    private ComboBox<String> sortBy;
    @FXML
    private VBox centerVBox;
    @FXML
    private List<VBox> foodCells;

    public FoodDisplayController(ComboBox<String> sortBy, VBox centerVBox) {
        this.sortBy = sortBy;
        this.centerVBox = centerVBox;
    }

    /**
     * Displays foods from user's current inventory on the main page
     * @param foods
     */
    public void displayFoods(List<Food> foods) {
        centerVBox.getChildren().clear();
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
    public void sort(FoodCollection foodList) {
        if (sortBy.getValue().equals("Name")) {
            foodList.sortByName();
        }
        else if (sortBy.getValue().equals("Expiration")) {
            foodList.sortByExpiration();
        }
    }
    @FXML
    public void search(String query, FoodCollection foodList) {
        List<Food> matchingFoods = new ArrayList<Food>();
        for (Food f : foodList.getItemsList()) {
            //Using toLowerCase to effectively ignore case
            if (f.getName().toLowerCase().contains(query.toLowerCase())) {
                matchingFoods.add(f);
            }
        }
        displayFoods(matchingFoods);
    }
}
