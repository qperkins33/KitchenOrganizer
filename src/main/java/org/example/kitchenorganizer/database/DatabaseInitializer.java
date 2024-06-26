package org.example.kitchenorganizer.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInitializer {

    public static final String URL = "jdbc:sqlite:mydatabase.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // Create Users table with first name and last name
            String createUsersTable = """
                    CREATE TABLE IF NOT EXISTS Users (
                    id INTEGER PRIMARY KEY,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    firstName TEXT NOT NULL,
                    lastName TEXT NOT NULL
                    );
                    """;
            stmt.execute(createUsersTable);

            // Create FoodCollections table
            String createFoodCollectionsTable = """
                    CREATE TABLE IF NOT EXISTS FoodCollections (
                    id INTEGER PRIMARY KEY,
                    userId INTEGER,
                    name TEXT NOT NULL,
                    FOREIGN KEY(userId) REFERENCES Users(id)
                    UNIQUE(userId, name)
                    );
                    """;
            stmt.execute(createFoodCollectionsTable);

            // Create Foods table
            String createFoodsTable = """
                    CREATE TABLE IF NOT EXISTS Foods (
                    id INTEGER PRIMARY KEY,
                    collectionId INTEGER,
                    name TEXT NOT NULL,
                    quantity DOUBLE NOT NULL,
                    measurementUnit TEXT NOT NULL,
                    minQuantity DOUBLE NOT NULL,
                    expDate DATE NOT NULL,
                    FOREIGN KEY(collectionId) REFERENCES FoodCollections(id)
                    );
                    """;
            stmt.execute(createFoodsTable);

            System.out.println("Database and tables created successfully.");

        } catch (Exception e) {
            System.out.println("Database setup failed: " + e.getMessage());
        }
    }

    // Test method that displays all users and passwords to console
    public static void displayAllUsers() {
        String sql = "SELECT * FROM Users";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Username: " + rs.getString("username") + ", Password: " + rs.getString("password") +
                        ", First Name: " + rs.getString("firstName") + ", Last Name: " + rs.getString("lastName"));
            }
        } catch (Exception e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
    }

}