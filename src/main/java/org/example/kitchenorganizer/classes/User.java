package org.example.kitchenorganizer.classes;
import java.util.ArrayList;
import java.util.List;

public class User {
    /**
     * Used to set current user for app session
     */
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private List<FoodCollection> foodInventoryList;

    // Constructors
    public User(String firstName, String lastName) { // used for example user
        this.firstName = firstName;
        this.lastName = lastName;
        this.foodInventoryList = new ArrayList<>();
    }

    public User(int id, String username, String firstName, String lastName) { // used in actual app
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.foodInventoryList = new ArrayList<>();
    }

    // Methods

    public List<FoodCollection> getFoodInventoryList() {
        return foodInventoryList;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public void setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return this.username;
    }

    public int getId() {
        return id;
    }

}
