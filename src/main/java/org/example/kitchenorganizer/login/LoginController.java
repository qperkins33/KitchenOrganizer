package org.example.kitchenorganizer.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;
import org.example.kitchenorganizer.database.DatabaseInitializer;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import static org.example.kitchenorganizer.database.DatabaseInitializer.URL;

public class LoginController {

    public interface LoginListener {
        void onLoginComplete();
    }

    private LoginListener loginListener;
    private static final String CORRECT_USERNAME = "user";
    private static final String CORRECT_PASSWORD = "pass";

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField firstNameField;  // Make sure this fx:id is defined in FXML

    @FXML
    private TextField lastNameField;   // Make sure this fx:id is defined in FXML

    @FXML
    private TextField newUsernameField;  // Make sure this fx:id is defined in FXML

    @FXML
    private PasswordField newPasswordField;  // Make sure this fx:id is defined in FXML

    @FXML
    private Label loginMessageLabel;

    @FXML
    private Label createAccountMessageLabel;

    @FXML
    public void handleCreateAccountButtonAction(ActionEvent event) {
        // Collect data from input fields
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = newUsernameField.getText().trim();
        String password = newPasswordField.getText().trim(); // In a real-world scenario, you should hash the password

        // Validate input fields
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            createAccountMessageLabel.setTextFill(Color.RED);
            createAccountMessageLabel.setText("Please fill in all fields.");
            return;
        }

        // Insert new user into the database
        String sql = "INSERT INTO Users (username, password, firstName, lastName) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // Store a hashed password instead
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
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                createAccountMessageLabel.setTextFill(Color.RED);
                createAccountMessageLabel.setText("Username already exists. Choose another.");
            } else {
                createAccountMessageLabel.setTextFill(Color.RED);
                createAccountMessageLabel.setText("An error occurred: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleLoginButtonAction(ActionEvent event) {
        String enteredUsername = usernameField.getText();
        String enteredPassword = passwordField.getText();

        if (enteredUsername.equals(CORRECT_USERNAME) && enteredPassword.equals(CORRECT_PASSWORD)) {
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
    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }
    private void switchToMainPage(ActionEvent event) {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/org/example/kitchenorganizer/MainPage.fxml"));
            // Change the scene to main page
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException occurred: " + e.getMessage());
        }
    }
}

