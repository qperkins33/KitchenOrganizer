package org.example.kitchenorganizer;
import org.example.kitchenorganizer.login.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable, LoginController.LoginListener {

    @FXML
    private Parent mainPageContent;
    @FXML
    private Parent loginFormContent;

    public void onLoginComplete() {
        this.loginFormContent.setVisible(false);
        this.mainPageContent.setVisible(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loginFormContent.setVisible(true);
        this.mainPageContent.setVisible(false);
    }

}
