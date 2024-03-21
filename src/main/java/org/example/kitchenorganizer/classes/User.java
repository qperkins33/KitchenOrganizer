package org.example.kitchenorganizer.classes;

import org.example.kitchenorganizer.classes.FoodCollection;

import java.util.ArrayList;
import java.util.List;

public class User {
    /**
     * Used to set current user for app session
     */
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
//    *********************

    private String firstName;
    private String lastName;

    private String username;
    private List<FoodCollection> foodInventoryList;

    // Constructor
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.foodInventoryList = new ArrayList<>();
    }

    // Methods
    public String getName() {
        return firstName + " " + lastName;
    }

    public List<FoodCollection> getFoodInventoryList() {
        return foodInventoryList;
    }

    public void setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addCollectionToInventoryList(List<FoodCollection> newCollection) {
        this.foodInventoryList = newCollection;
    }
}
