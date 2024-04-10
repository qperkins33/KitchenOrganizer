package org.example.kitchenorganizer.notification;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class Notification implements Notify {
    private final int userId;
    private String collectionName;
    private static final String URL = "jdbc:sqlite:mydatabase.db";
    private static final String EXPIRED_MESSAGE = " - Expired\n";
    private static final String LOW_QUANTITY_MESSAGE = " - Low inventory\n";
    private static final String SUFFICIENT_MESSAGE = "All items are well stocked.\n";

    /**
     * Used when searching through specific collection
     * @param userId
     * @param collectionName
     */
    public Notification(int userId, String collectionName) {
        this.userId = userId;
        this.collectionName = collectionName;
    }

    @Override
    public String gatherNotifications() {
        Set<String> expiredFoods = fetchExpiredFoods();
        StringBuilder notifications = appendNotifications(new StringBuilder(), expiredFoods, EXPIRED_MESSAGE);

        Set<String> lowInventoryFoods = fetchLowInventoryFoods(expiredFoods);
        appendNotifications(notifications, lowInventoryFoods, LOW_QUANTITY_MESSAGE);

        // if else
        return notifications.isEmpty() ? SUFFICIENT_MESSAGE : notifications.toString();
    }

    /**
     * Fetch expired foods. Check current inventory if collectionName != null. Otherwise, check all collections.
     * @return Set String expiredFoods
     */
    private Set<String> fetchExpiredFoods() {
        Set<String> expiredFoods = new HashSet<>();
        String sql = "SELECT f.name FROM Foods f INNER JOIN FoodCollections fc ON f.collectionId = fc.id WHERE fc.userId = ? " +
                (collectionName != null ? "AND fc.name = ? " : "") + "AND f.expDate <= CURRENT_DATE"; // Check if expired by using CURRENT_DATE. Also, if collectionName happens to be null, all collections are searched.

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = prepareStatement(conn, sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expiredFoods.add(rs.getString("name"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return expiredFoods;
    }

    /**
     * Fetch low inventory foods. Check current inventory if collectionName != null. Otherwise, check all collections.
     * @return Set String lowInventoryFoods
     */
    private Set<String> fetchLowInventoryFoods(Set<String> excludedFoods) {
        Set<String> lowInventoryFoods = new HashSet<>();
        String sql = "SELECT f.name FROM Foods f INNER JOIN FoodCollections fc ON f.collectionId = fc.id WHERE fc.userId = ? " +
                (collectionName != null ? "AND fc.name = ? " : "") + "AND f.quantity < f.minQuantity"; // If collectionName happens to be null, all collections are searched.

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = prepareStatement(conn, sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String foodName = rs.getString("name");
                    if (!excludedFoods.contains(foodName)) {
                        lowInventoryFoods.add(foodName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lowInventoryFoods;
    }

    /**
     * Prepare database statement
     * @param conn
     * @param sql
     * @return
     * @throws Exception
     */
    private PreparedStatement prepareStatement(Connection conn, String sql) throws Exception {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, this.userId);
        if (collectionName != null) {
            pstmt.setString(2, collectionName); // Second parameter will always be collectionName when needed
        }
        return pstmt;
    }

    private StringBuilder appendNotifications(StringBuilder builder, Set<String> items, String message) {
        items.forEach(item -> builder.append(item).append(message));
        return builder;
    }
}
