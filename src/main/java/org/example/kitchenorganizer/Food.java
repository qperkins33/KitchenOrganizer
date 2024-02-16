package org.example.kitchenorganizer;

public class Food extends InventoryItem {

    private int expDate;

    // Constructor
    public Food(String name, double quantity, String measurementUnit, double minQuantity, int expDate) {
        super(name, quantity, measurementUnit, minQuantity); // Call the superclass constructor
        this.expDate = expDate;
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
