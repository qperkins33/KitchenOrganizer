package org.example.kitchenorganizer.mainpage;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.kitchenorganizer.classes.Food;
import org.example.kitchenorganizer.classes.User;
import org.example.kitchenorganizer.database.DatabaseMethods;
import java.util.List;
import java.util.Optional;
import static org.example.kitchenorganizer.database.DatabaseMethods.*;

public class FoodDisplayController {

    private VBox foodsCenterVBox;
    private ComboBox<String> sortBy;
    private int pageNum;
    private int maxFoodsPerPage;
    private List<Food> currentFoodList;
    private Text pageInfoText;

    public FoodDisplayController(VBox foodsCenterVBox, ComboBox<String> sortBy, Text pageInfoText) {
        this.foodsCenterVBox = foodsCenterVBox;
        this.sortBy = sortBy;
        this.pageInfoText = pageInfoText;
        pageNum = 0;
        maxFoodsPerPage = 6;
        currentFoodList = null;
    }

    /**
     * Displays foods from user's current inventory on the main page
     * @param foods
     * This method was made by Quin. I simply moved it to this class.
     */
    private void displayFoods(List<Food> foods) {
        foodsCenterVBox.getChildren().clear();
        HBox currentRow = new HBox();
        currentRow.setAlignment(Pos.TOP_CENTER);
        currentRow.getStyleClass().add("foodRow");
        int count = 0;

        while (count < 6 && pageNum * maxFoodsPerPage + count < foods.size()) {
            Food food = foods.get(pageNum * maxFoodsPerPage + count);
            if (count % 3 == 0 && count > 0) { // rows of 3
                foodsCenterVBox.getChildren().add(currentRow);
                currentRow = new HBox();
                currentRow.setAlignment(Pos.TOP_CENTER);
                currentRow.getStyleClass().add("foodRow");
            }
            VBox foodCell = new VBox();
            foodCell.setAlignment(Pos.CENTER);
            if (food.getExpDate() <= 0) {
                foodCell.getStyleClass().add("foodCellExpired");
            }
            else if (food.getQuantity() < food.getMinQuantity()) {
                foodCell.getStyleClass().add("foodCellLowQuantity");
            }
            else {
                foodCell.getStyleClass().add("foodCell");
            }



            // Food info
            HBox foodNameBox = new HBox();
            foodNameBox.getStyleClass().add("foodName");
            foodNameBox.setAlignment(Pos.CENTER);
            Text foodName = new Text(food.getName());

            foodNameBox.getChildren().add(foodName);

            Text expDateText = new Text("Days to Expiration: " + food.getExpDate());
            Text quantityText = new Text(food.getMeasurementUnit() + ": " + String.format("%.2f", food.getQuantity())); // Format double to String
            Text minQuantityText = new Text("Min " + food.getMeasurementUnit() + ": " + food.getMinQuantity());

            //**************************************************
            HBox changeExpDate = new HBox();
            changeExpDate.setAlignment(Pos.CENTER);
            TextField changeExpDateTextField = new TextField();
            changeExpDateTextField.setPromptText("New Exp Days");
            Button changeExpDateButton = new Button("=");
            changeExpDate.getChildren().addAll(changeExpDateTextField, changeExpDateButton);

            changeExpDateButton.setOnAction(actionEvent -> {
                try {
                    // Attempt to parse the input as an integer
                    int newExpDateDays = Integer.parseInt(changeExpDateTextField.getText().trim());

                    // Check if the entered value is positive
                    if (newExpDateDays < 0) {
                        // Show an error alert if the value is not positive
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText("Invalid Expiration Days");
                        alert.setContentText("Please enter a positive number for expiration days.");
                        alert.showAndWait();
                        return;
                    }

                    DatabaseMethods.updateFoodExpDate(food.getFoodId(), newExpDateDays);
                    refreshDisplayFromWithinDisplay(food);

                } catch (NumberFormatException e) {
                    // Show an error alert if the input is not a valid integer
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Format Error");
                    alert.setContentText("Please enter a valid integer for expiration days.");
                    alert.showAndWait();
                }
            });

            //**************************************************

            // changeQuantityButtons
            HBox changeQuantityButtons = new HBox();
            changeQuantityButtons.setAlignment(Pos.CENTER);
            Button minus = new Button("-");
            TextField usedQuantity = new TextField();
            usedQuantity.setPromptText("Quantity"); // TODO: Center text within TextField
            Button plus = new Button("+");
            changeQuantityButtons.getChildren().addAll(minus, usedQuantity, plus);

            minus.setOnAction(event -> {
                try {
                    double quantityChange = Double.parseDouble(usedQuantity.getText());
                    if (quantityChange < 0) {
                        // Alert the user instead of negating the number
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText("Negative Quantity");
                        alert.setContentText("Please enter a positive number to subtract.");
                        alert.showAndWait();
                        return; // Do not proceed with the operation
                    }

                    double newQuantity = food.getQuantity() - quantityChange;
                    if (newQuantity < 0) {
                        // Notify user that the quantity cannot be negative
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Invalid Operation");
                        alert.setHeaderText("Invalid Used Quantity");
                        alert.setContentText("The entered quantity exceeds the current stock.\nNew quantity is now: 0");
                        alert.showAndWait();
                        newQuantity = 0;
                    }
                    DatabaseMethods.updateFoodQuantity(food.getFoodId(), newQuantity);
                    refreshDisplayFromWithinDisplay(food);
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Format Error");
                    alert.setContentText("Please enter a valid number.");
                    alert.showAndWait();
                }
            });

            plus.setOnAction(event -> {
                try {
                    double quantityChange = Double.parseDouble(usedQuantity.getText());
                    if (quantityChange < 0) {
                        // Alert the user about the negative input
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText("Negative Quantity");
                        alert.setContentText("Please enter a positive number to add.");
                        alert.showAndWait();
                        return; // Do not proceed with the operation
                    }

                    double newQuantity = food.getQuantity() + quantityChange;
                    DatabaseMethods.updateFoodQuantity(food.getFoodId(), newQuantity);
                    refreshDisplayFromWithinDisplay(food);
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Format Error");
                    alert.setContentText("Please enter a valid number.");
                    alert.showAndWait();
                }
            });

            //**************************************************
            HBox changeMinQuantity = new HBox();
            changeExpDate.setAlignment(Pos.CENTER);
            TextField changeMinQuantityTextField = new TextField();
            changeMinQuantityTextField.setPromptText("New Min QTY");
            Button changeMinQuantityButton = new Button("=");
            changeMinQuantity.getChildren().addAll(changeMinQuantityTextField, changeMinQuantityButton);

            changeMinQuantityButton.setOnAction(actionEvent -> {
                try {
                    // Attempt to parse the input as a double
                    double newMinQuantity = Double.parseDouble(changeMinQuantityTextField.getText().trim());

                    // Check if the entered value is positive
                    if (newMinQuantity < 0) {
                        // Show an error alert if the value is not positive
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText("Invalid Minimum Quantity");
                        alert.setContentText("Please enter a positive number for minimum quantity.");
                        alert.showAndWait();
                        return;
                    }

                    DatabaseMethods.updateMinQuantity(food.getFoodId(), newMinQuantity);
                    refreshDisplayFromWithinDisplay(food);

                } catch (NumberFormatException e) {
                    // Show an error alert if the input is not a valid integer
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Format Error");
                    alert.setContentText("Please enter a valid integer for minimum quantity.");
                    alert.showAndWait();
                }
            });
            //**************************************************

            Button delete = new Button("Delete");
            delete.setOnAction(actionEvent -> {
                // Display a confirmation dialog
                Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationDialog.setTitle("Confirm Action");
                confirmationDialog.setHeaderText("Delete " + food.getName());
                confirmationDialog.setContentText("Are you sure you want to delete " + food.getName() + "?");

                Optional<ButtonType> result = confirmationDialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    DatabaseMethods.deleteFoodByFoodId(food.getFoodId());
                }

                // refreshDisplay
                refreshDisplayFromWithinDisplay(food);
            });

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
        currentFoodList = fetchSortedFoods(changedFood.getCollectionId(), "name");
        displayFoods(currentFoodList);
    }

    public void search(String searchedFood, String currentCollectionName) {
        currentFoodList = returnFoodsThatMatchSearch(User.getCurrentUser().getId(), searchedFood, currentCollectionName);
        pageNum = 0;
        pageInfoText.setText(String.format(" Page %d of %d ", pageNum + 1,
                //To ensure an extra page is not added when the food list is divisible by maxFoodsPerPage,
                //and to ensure that the max page is not 0 when the collection is empty.
                (currentFoodList.size() % maxFoodsPerPage == 0 && !currentFoodList.isEmpty())
                        ? currentFoodList.size() / maxFoodsPerPage: currentFoodList.size() / maxFoodsPerPage + 1));
        displayFoods(currentFoodList); //TODO: Make usable
    }

    // TODO: incrementPageNum and decrementPageNum do not properly change page.
    //  The new page will not be up to date because user may have changed the contents of the database
    //  and the currentCollectionName will also not be correct. So if a user searches, the search result
    //  will be from page 1 and never from page 2. I commented out the sections that do not work.
    //  Either we can fix it or just delete them entirely.
    public void incrementPageNum() {
        if ((pageNum + 1) * maxFoodsPerPage < currentFoodList.size()) {
            pageNum++;
            pageInfoText.setText(String.format(" Page %d of %d ", pageNum + 1,
                    //To ensure an extra page is not added when the food list is divisible by maxFoodsPerPage,
                    //and to ensure that the max page is not 0 when the collection is empty.
                    (currentFoodList.size() % maxFoodsPerPage == 0 && !currentFoodList.isEmpty())
                            ? currentFoodList.size() / maxFoodsPerPage: currentFoodList.size() / maxFoodsPerPage + 1));
            displayFoods(currentFoodList);
        }
    }
    public void decrementPageNum() {
        if (pageNum > 0) {
            pageNum--;
            pageInfoText.setText(String.format(" Page %d of %d ", pageNum + 1,
                    //To ensure an extra page is not added when the food list is divisible by maxFoodsPerPage,
                    //and to ensure that the max page is not 0 when the collection is empty.
                    (currentFoodList.size() % maxFoodsPerPage == 0 && !currentFoodList.isEmpty())
                            ? currentFoodList.size() / maxFoodsPerPage: currentFoodList.size() / maxFoodsPerPage + 1));
            displayFoods(currentFoodList);
        }
    }

    public void updateFoodDisplayByCollectionName(String selectedKitchen) {
        int userId = User.getCurrentUser().getId();
        int collectionId = findCollectionIdByNameAndUserId(selectedKitchen, userId);

        String sortOrder = "name"; // Default sort order
        if (sortBy.getValue() != null && sortBy.getValue().equals("Expiration")) {
            sortOrder = "expDate";
        }

        pageNum = 0;
        currentFoodList = fetchSortedFoods(collectionId, sortOrder);
        pageInfoText.setText(String.format(" Page %d of %d ", pageNum + 1,
                //To ensure an extra page is not added when the food list is divisible by maxFoodsPerPage,
                //and to ensure that the max page is not 0 when the collection is empty.
                (currentFoodList.size() % maxFoodsPerPage == 0 && !currentFoodList.isEmpty())
                ? currentFoodList.size() / maxFoodsPerPage: currentFoodList.size() / maxFoodsPerPage + 1));
        displayFoods(currentFoodList);
    }
}
