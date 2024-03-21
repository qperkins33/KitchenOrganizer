package org.example.kitchenorganizer;

import org.example.kitchenorganizer.classes.Food;
import org.example.kitchenorganizer.classes.FoodCollection;
import org.example.kitchenorganizer.classes.User;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an example instance of a test filled with test values.
 */
public class ExampleUser {
    public static User testUser() {
        User user = new User("FirstName", "LastName");

        FoodCollection foodCollection = new FoodCollection();

        Food food1 = new Food("Burger", 55, "Quantity", 4, 14);
        Food food2 = new Food("Pear", 3, "Quantity", 4, 8);
        Food food3 = new Food("Flour", 1.15, "Lbs", 4, 5);
        Food food4 = new Food("Banana", 500, "Quantity", 4, 10);
        Food food5 = new Food("Egg", 10, "Quantity", 4, 12);
        Food food6 = new Food("Milk", 1, "Quantity", 4, 6);
        Food food7 = new Food("Sugar", 54, "Cups", 4, 4);
        Food food8 = new Food("Cake", 2, "Quantity", 4, 31);

        foodCollection.addItem(food1);
        foodCollection.addItem(food2);
        foodCollection.addItem(food3);
        foodCollection.addItem(food4);
        foodCollection.addItem(food5);
        foodCollection.addItem(food6);
        foodCollection.addItem(food7);
        foodCollection.addItem(food8);


        List<FoodCollection> list = new ArrayList<>();

        list.add(foodCollection);

        user.addCollectionToInventoryList(list);

        // Return user with values
        return user;
    }
}