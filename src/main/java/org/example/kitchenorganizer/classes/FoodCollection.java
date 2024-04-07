package org.example.kitchenorganizer.classes;
import java.util.ArrayList;
import java.util.Comparator;
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

    public void sortByName() {
        items.sort(Comparator.comparing(Food::getName));
    }

    public void sortByExpiration() {
        items.sort(Comparator.comparing(Food::getExpDate));
    }

    public void addItem(Food item) {
        this.items.add(item);
    }

    public void removeItem(Food item) {
        this.items.remove(item);
    }

    public List<Food> getItemsList() {
        return new ArrayList<>(items);
    }
}
