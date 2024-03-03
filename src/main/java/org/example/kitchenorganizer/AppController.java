package org.example.kitchenorganizer;

import org.example.kitchenorganizer.MainPageController;
import org.example.kitchenorganizer.login.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

import java.net.URL;
import java.util.ResourceBundle;

//Use this when login is made
//public class AppController implements Initializable, LoginController.LoginListener, MainPageController.ContentListener {

public class AppController implements Initializable {
    @FXML
    private Parent mainPageContent;
    @FXML
    private Parent loginFormContent;

    @FXML
    private MainPageController MainPageController;
    @FXML
    private LoginController LoginController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loginFormContent.setVisible(true);
        this.mainPageContent.setVisible(false);
    }
}
