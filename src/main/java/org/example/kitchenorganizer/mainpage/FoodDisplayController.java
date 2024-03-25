package org.example.kitchenorganizer.mainpage;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.kitchenorganizer.classes.Food;
import org.example.kitchenorganizer.classes.FoodCollection;
import org.example.kitchenorganizer.database.DatabaseMethods;

import java.util.ArrayList;
import java.util.List;

import static org.example.kitchenorganizer.database.DatabaseMethods.fetchSortedFoods;

public class FoodDisplayController {

    @FXML
    private VBox foodsCenterVBox;

    public FoodDisplayController(VBox foodsCenterVBox) {
        this.foodsCenterVBox = foodsCenterVBox;
    }

    /**
     * Displays foods from user's current inventory on the main page
     * @param foods
     */
    public void displayFoods(List<Food> foods) {
        foodsCenterVBox.getChildren().clear();
        HBox currentRow = new HBox();
        currentRow.setAlignment(Pos.TOP_CENTER);
        currentRow.getStyleClass().add("foodRow");
        int count = 0;

        for (Food food : foods) {
            if (count % 3 == 0 && count > 0) { // rows of 3
                foodsCenterVBox.getChildren().add(currentRow);
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

            Text expDateText = new Text("Days to Expiration: " + food.getExpDate());
            Text quantityText = new Text(food.getMeasurementUnit() + ": " + String.format("%.2f", food.getQuantity())); // Format double to String
            Text minQuantityText = new Text("Min " + food.getMeasurementUnit() + ": " + food.getMinQuantity());

            // TODO: Add change expiration
            HBox changeExpDate = new HBox();
            changeExpDate.setAlignment(Pos.CENTER);
            TextField changeExpDateTextField = new TextField();
            changeExpDateTextField.setPromptText("New Days To Exp");
            Button changeExpDateButton = new Button("=");
            changeExpDate.getChildren().addAll(changeExpDateTextField, changeExpDateButton);

            // changeQuantityButtons
            HBox changeQuantityButtons = new HBox();
            changeQuantityButtons.setAlignment(Pos.CENTER);
            Button minus = new Button("-");
            TextField usedQuantity = new TextField();
            usedQuantity.setPromptText("Quantity"); // TODO: Center text within TextField
            Button plus = new Button("+");
            changeQuantityButtons.getChildren().addAll(minus, usedQuantity, plus);

            minus.setOnAction(event -> {
                double quantityChange = Double.parseDouble(usedQuantity.getText()); // Negate to decrease quantity
                if (quantityChange < 0) { // quantityChange cannot be negative because that would add instead. (This check is done because a user may enter be confused and assume they need to enter a negative to subtract)
                    quantityChange *= -1;
                }

                double newQuantity = food.getQuantity() - quantityChange;
                if (newQuantity < 0) { // newQuantity cannot be negative
                    newQuantity = 0;
                }
                DatabaseMethods.updateFoodQuantity(food.getFoodId(), newQuantity);

                // refreshDisplay
                refreshDisplayFromWithinDisplay(food);
            });

            plus.setOnAction(event -> {
                double quantityChange = Double.parseDouble(usedQuantity.getText()); // Increase quantity
                double newQuantity = food.getQuantity() + quantityChange;

                if (newQuantity < 0) { // newQuantity cannot be negative (In case user was confused, entered a negative number, clicked "+" to subtract, and newQuantity ended up being negative)
                    newQuantity = 0;
                }

                DatabaseMethods.updateFoodQuantity(food.getFoodId(), newQuantity);

                // refreshDisplay
                refreshDisplayFromWithinDisplay(food);
            });

            Button delete = new Button("Delete Food");
            delete.setOnAction(actionEvent -> {
                DatabaseMethods.deleteFoodByFoodId(food.getFoodId());

                // refreshDisplay
                refreshDisplayFromWithinDisplay(food);
            });

            // TODO: Add change min quantity
            HBox changeMinQuantity = new HBox();
            changeExpDate.setAlignment(Pos.CENTER);
            TextField changeMinQuantityTextField = new TextField();
            changeMinQuantityTextField.setPromptText("New Min Quantity");
            Button changeMinQuantityButton = new Button("=");
            changeMinQuantity.getChildren().addAll(changeMinQuantityTextField, changeMinQuantityButton);

            // Add Food info and Buttons
            foodCell.getChildren().addAll(foodNameBox, expDateText, quantityText, minQuantityText, changeExpDate, changeQuantityButtons, changeMinQuantity, delete);
            // Add foodCell to currentRow
            currentRow.getChildren().add(foodCell);
            count++;

        }

        // Add the last row if not already added
        if (!currentRow.getChildren().isEmpty()) {
            foodsCenterVBox.getChildren().add(currentRow);
        }
    }

    public void refreshDisplayFromWithinDisplay(Food changedFood) {
        List<Food> refreshedFoods;
        refreshedFoods = fetchSortedFoods(changedFood.getCollectionId(), "name");
        displayFoods(refreshedFoods);
    }

    // TODO: Make compatible with database
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
