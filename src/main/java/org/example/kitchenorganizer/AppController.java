package org.example.kitchenorganizer;
import org.example.kitchenorganizer.classes.InventoryItem;
import org.example.kitchenorganizer.login.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AppController implements Initializable, LoginController.LoginListener {

    private List<InventoryItem> itemList;

    @FXML
    private Parent mainPageContent;
    @FXML
    private Parent loginFormContent;

    @FXML
    private org.example.kitchenorganizer.mainpage.MainPageController MainPageController;
    @FXML
    private LoginController LoginController;

    public void onLoginComplete() {
        this.loginFormContent.setVisible(false);
        this.mainPageContent.setVisible(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loginFormContent.setVisible(true);
        this.mainPageContent.setVisible(false);
    }

    /**
     * TODO: Implement logout (logout button located in settings popup)
     */
    public void logout() {
        this.loginFormContent.setVisible(true);
        this.mainPageContent.setVisible(false);
    }
}
