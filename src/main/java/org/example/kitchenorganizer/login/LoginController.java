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
import java.io.IOException;

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
    private Label loginMessageLabel;

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

