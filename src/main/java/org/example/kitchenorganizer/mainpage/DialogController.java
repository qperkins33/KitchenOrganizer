package org.example.kitchenorganizer.mainpage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.kitchenorganizer.classes.User;
import org.example.kitchenorganizer.database.DatabaseMethods;
import org.example.kitchenorganizer.notification.Notification;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import static org.example.kitchenorganizer.database.DatabaseMethods.addFoodToCollection;
import static org.example.kitchenorganizer.database.DatabaseMethods.getCollectionNamesForUser;

public class DialogController {
    //All methods here were implemented by Quin. I only moved them here for cohesion. -Austin
    private MainPageController mainPageController;
    private FoodDisplayController foodDisplayController;
    public DialogController(FoodDisplayController foodDisplayController, MainPageController mainPageController) {
        this.foodDisplayController = foodDisplayController;
        this.mainPageController = mainPageController;
    }
    public void showCheckAllInventoryDialog() { // Notify user about foods where Quantity < MinQuantity and foods where expDateDays < 0
        Notification notification = new Notification(User.getCurrentUser().getId()); // Create an instance of Notification
        String lowInventoryNotifications = notification.gatherNotifications(); // Get the low inventory foods

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Low Inventory Notification");

        VBox content = new VBox();
        TextArea textArea = new TextArea(lowInventoryNotifications); // Use the notifications string directly
        textArea.setEditable(false);
        content.getChildren().add(textArea);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.showAndWait();
    }
    public void showCheckCurrentInventoryDialog(ActionEvent actionEvent) { // Notify user about foods where Quantity < MinQuantity and foods where expDateDays < 0
        Notification notification = new Notification(User.getCurrentUser().getId(), mainPageController.currentCollectionName); // Create an instance of Notification
        String lowInventoryNotifications = notification.gatherNotifications(); // Get the low inventory foods

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Low Inventory Notification");

        VBox content = new VBox();
        TextArea textArea = new TextArea(lowInventoryNotifications); // Use the notifications string directly
        textArea.setEditable(false);
        content.getChildren().add(textArea);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.showAndWait();
    }
    public void showAddNewItemDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Item");
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<String> collections = new ComboBox<>();
        collections.setPromptText("Select Collection");
        populateCollectionNameComboBox(collections);
        grid.add(new Label("Collection:"), 0, 0);
        grid.add(collections, 1, 0);

        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");

        ComboBox<String> measurementUnitDropdown = new ComboBox<>();
        measurementUnitDropdown.getItems().addAll(
                "Quantity",
                "Grams (g)",
                "Kilograms (kg)",
                "Ounces (oz)",
                "Pounds (lbs)",
                "Liters (L)",
                "Milliliters (mL)",
                "Cups",
                "Fluid Ounces (fl oz)",
                "Tablespoons (tbsp)",
                "Teaspoons (tsp)",
                "Gallons (gal)",
                "Quarts (qt)",
                "Pints (pt)"
        );
        measurementUnitDropdown.setPromptText("Select Measurement Unit");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        TextField minQuantityField = new TextField();
        minQuantityField.setPromptText("Minimum Quantity");
        TextField expDateField = new TextField();
        expDateField.setPromptText("Days Until Expiration");

        grid.add(new Label("Item Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Measurement Unit:"), 0, 2);
        grid.add(measurementUnitDropdown, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Minimum Quantity:"), 0, 4);
        grid.add(minQuantityField, 1, 4);
        grid.add(new Label("Days Until Expiration:"), 0, 5);
        grid.add(expDateField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) { // Empty form check
                // Checks if all fields are filled
                if (collections.getValue() == null ||
                        nameField.getText().trim().isEmpty() ||
                        measurementUnitDropdown.getValue() == null ||
                        quantityField.getText().trim().isEmpty() ||
                        minQuantityField.getText().trim().isEmpty() ||
                        expDateField.getText().trim().isEmpty()) {

                    // Show an alert dialog to inform the user that all fields are required
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Incomplete Form");
                    alert.setHeaderText("Missing Information");
                    alert.setContentText("Please fill out all fields before submitting.");
                    alert.showAndWait();
                    return null;
                }

                try {
                    String collection = collections.getValue();
                    String name = nameField.getText().trim();
                    double quantity = Double.parseDouble(quantityField.getText().trim());
                    String measurementUnit = measurementUnitDropdown.getValue();
                    double minQuantity = Double.parseDouble(minQuantityField.getText().trim());
                    int expDate = Integer.parseInt(expDateField.getText().trim());

                    if (quantity < 0 || minQuantity < 0 || expDate < 0) { // Negative number check
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Input Error");
                        alert.setHeaderText("Invalid Input Format");
                        alert.setContentText("Please ensure \"Quantity\", \"Minimum Quantity\", and \"Days Until Expiration\" fields contain valid POSITIVE numbers.");
                        alert.showAndWait();
                        return null;
                    }

                    // Add the food to the specified collection in the database
                    addFoodToCollection(collection, name, quantity, measurementUnit, minQuantity, expDate);
                    // Refresh the display
                    foodDisplayController.updateFoodDisplayByCollectionName(mainPageController.currentCollectionName);

                } catch (NumberFormatException e) {
                    // Show an alert dialog to inform the user about the input error
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid Input Format");
                    alert.setContentText("Please ensure numerical fields contain valid numbers.");
                    alert.showAndWait();
                    return null;
                }
            }
            return null; // This will be reached if the cancel button is pressed
        });

        dialog.showAndWait();
    }
    public void showSettingsDialog(ActionEvent event) {
        Dialog<Void> settingsPopup = new Dialog<>();
//        settingsPopup.setTitle("Settings");

        // Set the custom dialog layout
        VBox layout = new VBox();

        Text settingsText = new Text("Settings");

        Button deleteAccountButton = new Button("Delete Account");

        Button logoutButton = new Button("Logout");

        layout.getChildren().addAll(settingsText, deleteAccountButton, logoutButton);
        settingsPopup.getDialogPane().setContent(layout);
        settingsPopup.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE); // Add a close button

        deleteAccountButton.setOnAction(actionEvent -> {
            // Display a confirmation dialog
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirm Action");
            confirmationDialog.setHeaderText("Delete Account");
            confirmationDialog.setContentText("Are you sure you want to delete your account?");

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                DatabaseMethods.deleteUserByUserId(User.getCurrentUser().getId());
                handleLogout(event);
                settingsPopup.close();
            }
        });

        // Handle the logout button action
        logoutButton.setOnAction(e -> {
            handleLogout(event);
            settingsPopup.close();
        });

        settingsPopup.showAndWait();
    }
    @FXML
    private void handleLogout(ActionEvent event) {
        // Clear the current user session
        User.setCurrentUser(null);

        try {
            // Load login page FXML
            Parent loader = FXMLLoader.load(getClass().getResource("/org/example/kitchenorganizer/LoginForm.fxml"));
            Scene scene = new Scene(loader, 1300, 800); // Set to your desired size

            // Get the current stage from event's source
            Node source = (Node) event.getSource();
            if (source != null) {
                Stage stage = (Stage) source.getScene().getWindow();

                if (stage != null) {
                    stage.setScene(scene);
                    // Set size (FIXES resize problem)
                    stage.setWidth(1300);
                    stage.setHeight(800);
                    stage.show();
                } else {
                    System.out.println("Stage is null");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateCollectionNameComboBox(ComboBox<String> comboBox) {
        List<String> collectionNames = getCollectionNamesForUser(User.getCurrentUser().getId());
        comboBox.getItems().addAll(collectionNames);
    }
}
