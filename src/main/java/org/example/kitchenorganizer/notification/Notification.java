package org.example.kitchenorganizer.notification;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class Notification {
    private final int userId;
    private static final String URL = "jdbc:sqlite:mydatabase.db"; // Adjust your DB path as needed
    private String expiredMessage = " - Expired\n";
    private String lowQuantityMessage = " - Low inventory\n";
    private String sufficientMessage = "All items are well stocked.";

    public Notification(int userId) {
        this.userId = userId;
    }

    public String gatherNotifications() {
        StringBuilder notifications = new StringBuilder();

        // Collect expired foods
        Set<String> expiredFoods = fetchExpiredFoods();

        // Add expired foods to notifications
        expiredFoods.forEach(foodName -> notifications.append(foodName).append(expiredMessage));

        // Collect and add low inventory foods, excluding those already listed as expired
        notifications.append(fetchLowInventoryFoods(expiredFoods));

        if (notifications.length() == 0) {
            return sufficientMessage;
        } else {
            return notifications.toString();
        }
    }

    private Set<String> fetchExpiredFoods() {
        Set<String> expiredFoods = new HashSet<>();
        String sql = "SELECT f.name FROM Foods f INNER JOIN FoodCollections fc ON f.collectionId = fc.id WHERE fc.userId = ? AND f.expDate < ?";

        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE);

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.userId);
            pstmt.setString(2, todayStr);
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

    private String fetchLowInventoryFoods(Set<String> excludedFoods) {
        StringBuilder lowInventoryFoods = new StringBuilder();
        String sql = "SELECT f.name FROM Foods f INNER JOIN FoodCollections fc ON f.collectionId = fc.id WHERE fc.userId = ? AND f.quantity < f.minQuantity";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String foodName = rs.getString("name");
                    if (!excludedFoods.contains(foodName)) { // Only add if not already reported as expired
                        lowInventoryFoods.append(foodName).append(lowQuantityMessage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lowInventoryFoods.toString();
    }
}
