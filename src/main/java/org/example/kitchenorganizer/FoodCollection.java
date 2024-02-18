package org.example.kitchenorganizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FoodCollection {
    private List<Food> items;
    private String collectionName; // add getter and setter

    // Constructors
    public FoodCollection() {
        this.items = new ArrayList<Food>();
    }
    public FoodCollection(String name) {
        this.collectionName = name;
        this.items = new ArrayList<Food>();
    }

    // Methods
    public void sortByName() {
        items.sort(Comparator.comparing(Food::getName));
    }


//    + SortByExpiration()
//
//    + AddItem(item: Food): void
//
//    + RemoveItem(item: Food): void
//
//    + GetItems(items: String):
//
//    + GetItemList: List<Food>
}
