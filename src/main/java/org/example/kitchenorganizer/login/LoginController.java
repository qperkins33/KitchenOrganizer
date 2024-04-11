package org.example.kitchenorganizer.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;
import org.example.kitchenorganizer.classes.User;
import org.example.kitchenorganizer.database.DatabaseInitializer;

import java.io.IOException;
import java.sql.*;

import static org.example.kitchenorganizer.database.DatabaseInitializer.URL;

public class LoginController {

    public interface LoginListener {
        void onLoginComplete();
    }

    private LoginListener loginListener;

    /**
     * Used in Login Form
     */
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    /**
     * Used in Create Account Form
     */
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField newUsernameField;

    @FXML
    private PasswordField newPasswordField;

    /**
     * Messages for both forms
     */
    @FXML
    private Label loginMessageLabel;

    @FXML
    private Label createAccountMessageLabel;


    @FXML
    private VBox loginContainer;

    @FXML
    private VBox createAccountContainer;

    @FXML
    public void handleCreateAccountButtonAction(ActionEvent event) {
        // Collect data from input fields
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = newUsernameField.getText().trim();
        String password = newPasswordField.getText().trim();

        // Validate inputs and add error state if not valid
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            createAccountMessageLabel.setTextFill(Color.RED);
            createAccountMessageLabel.setText("Please fill in all fields.");
            return;
        }

        // Check if the username already exists in the database
        if (usernameExists(username)) {
            createAccountMessageLabel.setTextFill(Color.RED);
            createAccountMessageLabel.setText("Username already exists.");
            return;
        }

        // Insert new user into the database
        String sql = "INSERT INTO Users (username, password, firstName, lastName) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // User was added successfully
                createAccountMessageLabel.setTextFill(Color.GREEN);
                createAccountMessageLabel.setText("Account created successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            createAccountMessageLabel.setTextFill(Color.RED);
            createAccountMessageLabel.setText("An error occurred: " + e.getMessage());
        }
    }

    private boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
    
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, username);
    
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }

    private boolean checkCredentials(String username, String password) {
        String sql = "SELECT id FROM Users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet results = preparedStatement.executeQuery();

            return results.next(); // If the result set has an entry, the user and pass are correct.

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private User fetchUserFromDatabase(String username) {
        User user = null;
        String sql = "SELECT * FROM Users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                int id = resultSet.getInt("id");

                user = new User(id, username, firstName, lastName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    @FXML
    public void handleLoginButtonAction(ActionEvent event) {
        String enteredUsername = usernameField.getText();
        String enteredPassword = passwordField.getText();

        if (checkCredentials(enteredUsername, enteredPassword)) {
            // Set current user for login session
            User user = fetchUserFromDatabase(enteredUsername);
            User.setCurrentUser(user);
            switchToMainPage(event);

            loginMessageLabel.setTextFill(Color.GREEN);
            loginMessageLabel.setText("Logged in successfully");

            // If the entered username and password are correct, switch to the main page
            switchToMainPage(event);

            if(loginListener != null) {
                loginListener.onLoginComplete();
            }

        } else {
            loginMessageLabel.setTextFill(Color.RED);
            loginMessageLabel.setText("Incorrect username or password");
        }
    }

    @FXML
    public void handleToggleFormButtonAction(ActionEvent event) {
        if (createAccountContainer.isVisible()) {
            // Switch to login form
            loginContainer.setVisible(true);
            loginContainer.setManaged(true);
            createAccountContainer.setVisible(false);
            createAccountContainer.setManaged(false);
        } else {
            // Switch to create account form
            loginContainer.setVisible(false);
            loginContainer.setManaged(false);
            createAccountContainer.setVisible(true);
            createAccountContainer.setManaged(true);
        }
    }

    private void switchToMainPage(ActionEvent event) {
        try {
            // Load the FXML for the main page
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/kitchenorganizer/MainPage.fxml"));

            // Get the current stage from the event's source
            Node source = (Node) event.getSource();
            if (source != null) {
                Stage stage = (Stage) source.getScene().getWindow();

                // Check if stage is not null before proceeding
                if (stage != null) {
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setWidth(1300); // Set the desired width
                    stage.setHeight(800); // Set the desired height
                    stage.show();
                } else {
                    System.out.println("Stage is null, can't switch scenes");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException occurred: " + e.getMessage());
        }
    }

}

