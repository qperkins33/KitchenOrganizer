package org.example.kitchenorganizer;

public abstract class InventoryItem {

    // Attributes common to all inventory items
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

    // Abstract methods - to be implemented by subclasses
    public abstract void setName(String name);
    public abstract void setQuantity(double quantity);
    public abstract String getName();
    public abstract double getQuantity();

    // Concrete methods - common implementation across all subclasses
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
