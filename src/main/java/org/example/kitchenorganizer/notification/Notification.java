package org.example.kitchenorganizer.notification;

import org.example.kitchenorganizer.classes.Food;
import org.example.kitchenorganizer.classes.FoodCollection;
import org.example.kitchenorganizer.classes.User;

public class Notification implements Notify {
    private final User currentUser;
    private String lowQuantityMessage =  " - Low inventory\n";
    private String sufficientQuantityMessage= "All items are well stocked.";

    public Notification(User u) {
        this.currentUser = u;
    }

    @Override
    public String gatherLowInventoryFoods() {
        StringBuilder lowInventoryFoods = new StringBuilder();

        // Loop through all food collections
        for (FoodCollection collection : currentUser.getFoodInventoryList()) {
            // Check each food item for low inventory
            for (Food food : collection.getItemsList()) {
                if (food.getQuantity() < food.getMinQuantity()) {
                    lowInventoryFoods.append(food.getName()).append(lowQuantityMessage);
                }
            }
        }

        if (!lowInventoryFoods.isEmpty()) {
            return lowInventoryFoods.toString();
        } else {
            return sufficientQuantityMessage;
        }
    }
}
