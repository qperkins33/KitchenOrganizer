package org.example.kitchenorganizer;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private List<FoodCollection> foodInventoryList;

    // Constructor
    public User(String name) {
        this.name = name;
        this.foodInventoryList = new ArrayList<>();
    }

    // Methods
    public String getName() {
        return name;
    }

    public List<FoodCollection> getFoodInventoryList() {
        return foodInventoryList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCollectionToInventoryList(List<FoodCollection> newCollection) {
        this.foodInventoryList = newCollection;
    }
}
