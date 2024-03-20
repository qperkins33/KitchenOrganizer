package org.example.kitchenorganizer.classes;

import org.example.kitchenorganizer.classes.FoodCollection;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String firstName;
    private String lastName;
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

    public void addCollectionToInventoryList(List<FoodCollection> newCollection) {
        this.foodInventoryList = newCollection;
    }
}
