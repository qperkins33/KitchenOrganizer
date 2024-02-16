package org.example.kitchenorganizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FoodCollection {
    private List<Food> items;

    public FoodCollection() {
        this.items = new ArrayList<Food>();
    }

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
