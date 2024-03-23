package org.example.kitchenorganizer.classes;

public class Food extends InventoryItem {
    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    private int foodId;

    private int expDate;

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    private int collectionId;

    // Constructor
    public Food(String name, double quantity, String measurementUnit, double minQuantity, int expDate, int foodId, int collectionId) {
        super(name, quantity, measurementUnit, minQuantity); // Call the superclass constructor
        this.expDate = expDate;
        this.foodId = foodId;
        this.collectionId = collectionId;
    }

    // Methods
    public void setExpDate(int expDate) {
        this.expDate = expDate;
    }

    public int getExpDate() {
        return this.expDate;
    }

    // Implementing abstract methods from InventoryItem
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public double getQuantity() {
        return this.quantity;
    }

    @Override
    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    @Override
    public String getMeasurementUnit() {
        return this.measurementUnit;
    }

    @Override
    public void setMinQuantity(double minQuantity) {
        this.minQuantity = minQuantity;
    }

    @Override
    public double getMinQuantity() {
        return this.minQuantity;
    }
}