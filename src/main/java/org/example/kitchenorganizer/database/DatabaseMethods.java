package org.example.kitchenorganizer.database;

import org.example.kitchenorganizer.classes.Food;
import org.example.kitchenorganizer.classes.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseMethods {

    public static List<Food> fetchSortedFoods(int collectionId, String sortOrder) {
        List<Food> foods = new ArrayList<>();
        String sql = "SELECT * FROM Foods WHERE collectionId = ? ORDER BY " + sortOrder;

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, collectionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    foods.add(new Food(
                            rs.getString("name"),
                            rs.getDouble("quantity"),
                            rs.getString("measurementUnit"),
                            rs.getDouble("minQuantity"),
                            rs.getInt("expDate"), //TODO: Change to Date
                            rs.getInt("id"),
                            rs.getInt("collectionId")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foods;
    }

    public static void addCollectionToUserDatabase(String collectionName, int userId) {
        // Check if the collection name already exists for the user
        String checkSql = "SELECT id FROM FoodCollections WHERE name = ? AND userId = ?";
        String insertSql = "INSERT INTO FoodCollections (name, userId) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            checkStmt.setString(1, collectionName);
            checkStmt.setInt(2, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("A collection with this name already exists for the user.");
            } else {
                insertStmt.setString(1, collectionName);
                insertStmt.setInt(2, userId);
                insertStmt.executeUpdate();
                System.out.println("New collection added successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding new collection: " + e.getMessage());
        }
    }

    public static void removeCollectionFromSignedInUsersDatabase(String collectionName, int userId) {
        // Step 1: Find the collection ID
        int collectionId = findCollectionIdByNameAndUserId(collectionName, userId);
        if (collectionId == -1) {
            System.out.println("Collection not found or access error.");
            return;
        }

        // Step 2: Delete all foods in the collection
        String deleteFoodsSql = "DELETE FROM Foods WHERE collectionId = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmtFoods = conn.prepareStatement(deleteFoodsSql)) {
            pstmtFoods.setInt(1, collectionId);
            int foodsDeleted = pstmtFoods.executeUpdate();
            System.out.println("Deleted " + foodsDeleted + " food items from the collection.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error removing foods from the collection: " + e.getMessage());
        }

        // Step 3: Delete collection

        String sql = "DELETE FROM FoodCollections WHERE name = ? AND userId = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collectionName);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Collection '" + collectionName + "' removed successfully.");
            } else {
                System.out.println("No collection found with the specified name for this user.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error removing collection: " + e.getMessage());
        }
    }

    public static void addFoodToCollection(String collectionName, String name, double quantity, String measurementUnit, double minQuantity, int expDate) {
        int userId = User.getCurrentUser().getId(); // This method should return the current user's ID
        int collectionId = findCollectionIdByNameAndUserId(collectionName, userId);

        if (collectionId != -1) {
            String sql = "INSERT INTO Foods (collectionId, name, quantity, measurementUnit, minQuantity, expDate) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, collectionId);
                pstmt.setString(2, name);
                pstmt.setDouble(3, quantity);
                pstmt.setString(4, measurementUnit);
                pstmt.setDouble(5, minQuantity);
                pstmt.setInt(6, expDate); // TODO: Adjust to convert to a date format
                pstmt.executeUpdate();
                System.out.println("New food added successfully to the collection.");
                //TODO: add refresh?

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error adding new food: " + e.getMessage());
            }
        } else {
            System.out.println("Collection not found or access error.");
        }
    }

    public static int findCollectionIdByNameAndUserId(String collectionName, int userId) {
        String sql = "SELECT id FROM FoodCollections WHERE name = ? AND userId = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, collectionName);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error finding collection ID: " + e.getMessage());
        }
        return -1; // Return -1 if not found or error
    }

    public static List<String> getCollectionNamesForUser(int userID) {
        List<String> collections = new ArrayList<>();
        // Correct the SQL query by removing the concatenation with userID, as you're setting it via PreparedStatement
        String sql = "SELECT name FROM FoodCollections WHERE userId = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Correctly close the parentheses
                    collections.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return collections;
    }

    public static void updateFoodQuantity(int foodId, double quantityChange) {
        String sql = "UPDATE Foods SET quantity = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the parameters for the update statement
            pstmt.setDouble(1, quantityChange);
            pstmt.setInt(2, foodId);

            // Execute the update
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Food quantity updated successfully.");
            } else {
                System.out.println("Error: Food item not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating food quantity: " + e.getMessage());
        }
    }
}
