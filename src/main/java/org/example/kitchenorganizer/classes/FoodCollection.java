package org.example.kitchenorganizer.classes;
import java.util.ArrayList;
import java.util.List;

public class FoodCollection {
    private List<Food> items;
    private String collectionName;

    // Constructors
    public FoodCollection() {
        this.items = new ArrayList<>();
    }

    // Methods
    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public List<Food> getItemsList() {
        return new ArrayList<>(items);
    }
}
