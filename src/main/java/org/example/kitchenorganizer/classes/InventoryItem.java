package org.example.kitchenorganizer.classes;

public abstract class InventoryItem { // Abstraction allows for future implementation of different item types such as kitchen appliances

    protected String name;
    protected double quantity;
    protected String measurementUnit;
    protected double minQuantity;

    // Constructor
    public InventoryItem(String name, double quantity, String measurementUnit, double minQuantity) {
        this.name = name;
        this.quantity = quantity;
        this.measurementUnit = measurementUnit;
        this.minQuantity = minQuantity;
    }

    // Abstract Methods - to be implemented by subclasses
    public abstract void setName(String name);
    public abstract void setQuantity(double quantity);
    public abstract String getName();
    public abstract double getQuantity();

    // Concrete Methods
    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public double getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(double minQuantity) {
        this.minQuantity = minQuantity;
    }
}
