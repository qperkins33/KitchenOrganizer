package org.example.kitchenorganizer.mainpage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.kitchenorganizer.classes.User;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import static org.example.kitchenorganizer.database.DatabaseMethods.*;

public class MainPageController implements Initializable {

    public String currentCollectionName;

    User user;
    int currentCollection;

    @FXML
    private ComboBox<String> kitchenSelectorComboBox;
    @FXML
    private VBox foodsCenterVBox; // VBox used to display main content in center of page (Foods)
    @FXML
    private Text userName;
    @FXML
    private Text pageInfoText;
    @FXML
    private ComboBox<String> sortBy;
    private FoodDisplayController foodDisplayController;
    private KitchenController kitchenController;
    private DialogController dialogController;
    /**
     * Initializes a test user and calls displayFoods() for current inventory
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentCollection = 0; // value to change displayed collection. (TEST)

        user = User.getCurrentUser(); // use in actual program (QUIN)

        foodDisplayController = new FoodDisplayController(foodsCenterVBox, sortBy, pageInfoText);
        kitchenController = new KitchenController(foodDisplayController, kitchenSelectorComboBox, this);
        dialogController = new DialogController(foodDisplayController, this);

        if (user != null) {
            userName.setText(user.getName());
            kitchenController.addKitchensToKitchenSelectorComboBox();

            if (!user.getFoodInventoryList().isEmpty()) {
                currentCollectionName = user.getFoodInventoryList().get(currentCollection).getCollectionName();
                foodDisplayController.updateFoodDisplayByCollectionName(currentCollectionName);
            }
        }
    }

    //*********************************************************************
    // KITCHEN COLLECTION
    @FXML
    private void handleKitchenSelectionComboBox() {
        kitchenController.handleKitchenSelectionComboBox();
    }
    @FXML
    private void showAddCollectionDialog() {
        kitchenController.showAddCollectionDialog();
    }

    @FXML
    private void showRemoveCollectionDialog() {
        kitchenController.showRemoveCollectionDialog();
    }

    //*********************************************************************
    @FXML
    private TextField searchBar;

    @FXML
    private void handleSearch() {
        foodDisplayController.search(searchBar.getText().trim(), currentCollectionName);
    }
    @FXML
    private void previousPage() {
        foodDisplayController.decrementPageNum();
    }
    @FXML
    private void nextPage() {
        foodDisplayController.incrementPageNum();
    }
    //*********************************************************************
    @FXML
    private void showSettingsDialog(ActionEvent event) {
        dialogController.showSettingsDialog(event);
    }
    //*********************************************************************
    @FXML
    private void showCheckAllInventoryDialog() { // Notify user about foods where Quantity < MinQuantity and foods where expDateDays < 0
        dialogController.showCheckAllInventoryDialog();
    }

    @FXML
    public void showCheckCurrentInventoryDialog(ActionEvent actionEvent) { // Notify user about foods where Quantity < MinQuantity and foods where expDateDays < 0
        dialogController.showCheckCurrentInventoryDialog(actionEvent);
    }
    //*********************************************************************
    // ADD NEW FOOD

    /**
     * Popup that allows user to enter new foods into inventory
     */

    private void populateCollectionNameComboBox(ComboBox<String> comboBox) {
        List<String> collectionNames = getCollectionNamesForUser(user.getId());
        comboBox.getItems().addAll(collectionNames);
    }
    @FXML
    private void showAddNewItemDialog() {
        dialogController.showAddNewItemDialog();
    }

    //*********************************************************************

    @FXML
    private void sort() {
        // Refresh display with sorted foods based on current kitchen and sort selection
        String selectedKitchen = kitchenSelectorComboBox.getSelectionModel().getSelectedItem();
        if (selectedKitchen != null) {
            foodDisplayController.updateFoodDisplayByCollectionName(selectedKitchen); // sorting is done in updateFoodDisplay()
        }
    }
}

