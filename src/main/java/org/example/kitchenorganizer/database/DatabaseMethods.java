package org.example.kitchenorganizer.database;
import org.example.kitchenorganizer.classes.Food;
import org.example.kitchenorganizer.classes.User;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseMethods {

    /**
     * Used to search a through a user's food collection from the database.
     * (SEARCH BAR)
     *
     * @param userId
     * @param searchedFood
     * @param collectionName
     * @return List of Foods that partially or fully match user's search
     */
    public static List<Food> returnFoodsThatMatchSearch(int userId, String searchedFood, String collectionName) {
        List<Food> matchingFoods = new ArrayList<>();
        // Adjust the SQL query to filter by the collection name
        String sql = "SELECT f.* FROM Foods f " +
                "INNER JOIN FoodCollections fc ON f.collectionId = fc.id " +
                "WHERE fc.userId = ? AND fc.name = ? AND LOWER(f.name) LIKE LOWER(?)";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, collectionName); // Use the collectionName for filtering
            pstmt.setString(3, "%" + searchedFood + "%"); // Use wildcard % for partial matches

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate expDate = LocalDate.parse(rs.getString("expDate"));
                    long daysUntilExpiration = LocalDate.now().until(expDate, ChronoUnit.DAYS);

                    matchingFoods.add(new Food(
                            rs.getString("name"),
                            rs.getDouble("quantity"),
                            rs.getString("measurementUnit"),
                            rs.getDouble("minQuantity"),
                            (int) daysUntilExpiration, // Cast to int for days until expiration
                            rs.getInt("id"),
                            rs.getInt("collectionId")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error searching for foods: " + e.getMessage());
        }
        return matchingFoods;
    }

    /**
     * Returns a list of user's foods from a specific collection. Orders list based on Name or Expiration.
     * Used when changing display order of foods on main page.
     * (SORT BY)
     *
     * @param collectionId
     * @param sortOrder
     * @return Sorted Foods List
     */
    public static List<Food> fetchSortedFoods(int collectionId, String sortOrder) {
        List<Food> foods = new ArrayList<>();
        String sql = "SELECT * FROM Foods WHERE collectionId = ? ORDER BY " + sortOrder;

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, collectionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Convert the DATE string from the database to LocalDate
                    LocalDate expDate = LocalDate.parse(rs.getString("expDate"));
                    // Calculate the number of days from now until the expiration date
                    long daysUntilExpiration = LocalDate.now().until(expDate, ChronoUnit.DAYS);

                    foods.add(new Food(
                            rs.getString("name"),
                            rs.getDouble("quantity"),
                            rs.getString("measurementUnit"),
                            rs.getDouble("minQuantity"),
                            (int) daysUntilExpiration, // Cast to int
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

    /**
     * Adds new empty collection to user's database.
     *
     * @param collectionName
     * @param userId
     */
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

    /**
     * Removes existing collection from user's database.
     * Also, removes everything associated with the collection including foods.
     *
     * @param collectionName
     * @param userId
     */
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

    /**
     * Updates existing food's expiration date with new expiration date.
     * Converts int expDateDays into Date based on today's date.
     * Stores expiration date as future Date.
     *
     * @param foodId
     * @param expDateDays
     */
    public static void updateFoodExpDate(int foodId, int expDateDays) {
        // Calculate the expiration date by adding expDateDays to the current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, expDateDays);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String expDateStr = sdf.format(calendar.getTime());

        // SQL statement to update the expiration date of a specific food item
        String sql = "UPDATE Foods SET expDate = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set the new expiration date and food ID in the prepared statement
            pstmt.setString(1, expDateStr);
            pstmt.setInt(2, foodId);

            // Execute the update
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("The expiration date for food ID " + foodId + " was successfully updated.");
            } else {
                System.out.println("No food item found with ID: " + foodId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating food expiration date: " + e.getMessage());
        }
    }

    /**
     * Adds new food to the user's database.
     * Links food with correct collection name.
     *
     * @param collectionName
     * @param name
     * @param quantity
     * @param measurementUnit
     * @param minQuantity
     * @param expDateDays
     */
    public static void addFoodToCollection(String collectionName, String name, double quantity, String measurementUnit, double minQuantity, int expDateDays) {
        int userId = User.getCurrentUser().getId(); // This method should return the current user's ID
        int collectionId = findCollectionIdByNameAndUserId(collectionName, userId);

        // Calculate the expiration date by adding expDateDays to the current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, expDateDays);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String expDateStr = sdf.format(calendar.getTime());

        if (collectionId != -1) {
            String sql = "INSERT INTO Foods (collectionId, name, quantity, measurementUnit, minQuantity, expDate) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, collectionId);
                pstmt.setString(2, name);
                pstmt.setDouble(3, quantity);
                pstmt.setString(4, measurementUnit);
                pstmt.setDouble(5, minQuantity);
                pstmt.setString(6, expDateStr); // Use the formatted date string
                pstmt.executeUpdate();
                System.out.println("New food added successfully to the collection.");
                // Any necessary refresh operations would go here

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error adding new food: " + e.getMessage());
            }
        } else {
            System.out.println("Collection not found or access error.");
        }
    }

    /**
     * Finds collection id based on user's id and user's unique collection name.
     *
     * @param collectionName
     * @param userId
     * @return Desired collection's id
     */
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

    /**
     * Retrieves a list of a user's collection names from database.
     *
     * @param userID
     * @return List of collection names
     */
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

    /**
     * Updates existing food's food quantity.
     *
     * @param foodId
     * @param quantityChange
     */
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

    /**
     * Deletes food from the database
     *
     * @param foodId
     */
    public static void deleteFoodByFoodId(int foodId) {
        String sql = "DELETE FROM Foods WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, foodId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Food with ID " + foodId + " was successfully deleted.");
            } else {
                System.out.println("No food found with ID " + foodId + ".");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting food from the database: " + e.getMessage());
        }
    }

    /**
     * Deletes user and all user associated items from the database.
     *
     * @param userId
     */
    public static void deleteUserByUserId(int userId) {
        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             Statement stmt = conn.createStatement()) {

            // Step 1: Delete all Foods associated with the user's FoodCollections
            String deleteFoodsSql = "DELETE FROM Foods " +
                    "WHERE collectionId IN (SELECT id FROM FoodCollections WHERE userId = ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(deleteFoodsSql)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }

            // Step 2: Delete all FoodCollections associated with the user
            String deleteFoodCollectionsSql = "DELETE FROM FoodCollections WHERE userId = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(deleteFoodCollectionsSql)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }

            // Step 3: Delete the user
            String deleteUserSql = "DELETE FROM Users WHERE id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(deleteUserSql)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }

            System.out.println("User and all associated data deleted successfully.");
        } catch (Exception e) {
            System.out.println("Error deleting user and associated data: " + e.getMessage());
        }
    }

    /**
     * Updates existing food's food minimum quantity.
     *
     * @param foodId
     * @param minQuantity
     */
    public static void updateMinQuantity(int foodId, double minQuantity) {
        String sql = "UPDATE Foods SET minQuantity = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the parameters for the update statement
            pstmt.setDouble(1, minQuantity);
            pstmt.setInt(2, foodId);

            // Execute the update
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Food minimum quantity updated successfully.");
            } else {
                System.out.println("Error: Food item not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating food minimum quantity: " + e.getMessage());
        }
    }
}
