package org.example.kitchenorganizer.mainpage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.example.kitchenorganizer.classes.User;
import org.example.kitchenorganizer.database.DatabaseInitializer;
import java.sql.*;
import java.util.List;
import static org.example.kitchenorganizer.database.DatabaseMethods.*;

public class KitchenController {
    //All methods here were implemented by Quin. I only moved them here for cohesion. -Austin
    private FoodDisplayController foodDisplayController;
    private ComboBox<String> kitchenSelectorComboBox;
    private MainPageController mainPageController;
    public KitchenController(FoodDisplayController foodDisplayController, ComboBox<String> kitchenSelectorComboBox,
                             MainPageController mainPageController) {
        this.foodDisplayController = foodDisplayController;
        this.kitchenSelectorComboBox = kitchenSelectorComboBox;
        this.mainPageController = mainPageController;
    }
    public void showRemoveCollectionDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Remove Collection");
        ButtonType submitButtonType = new ButtonType("Submit");
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // CSS
        grid.getStylesheets().add(getClass().getResource("/org/example/kitchenorganizer/MainPage.css").toExternalForm());
        grid.getStyleClass().add("accessibilityFontSize");

        ComboBox<String> collections = new ComboBox<>();
        collections.setPromptText("Select Collection");
        populateCollectionNameComboBox(collections);
        grid.add(new Label("Collection:"), 0, 0);
        grid.add(collections, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                String name = collections.getValue();

                // Ensure the name field is not empty and a user is signed in
                if (!name.isEmpty() && User.getCurrentUser() != null) {
                    removeCollectionFromSignedInUsersDatabase(name, User.getCurrentUser().getId());

                    foodDisplayController.updateFoodDisplayByCollectionName(mainPageController.currentCollectionName);
                    refreshKitchenSelectorComboBox();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
    public void showAddCollectionDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Collection");
        ButtonType submitButtonType = new ButtonType("Submit");
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // CSS
        grid.getStylesheets().add(getClass().getResource("/org/example/kitchenorganizer/MainPage.css").toExternalForm());
        grid.getStyleClass().add("accessibilityFontSize");

        TextField collectionNameField = new TextField();
        collectionNameField.setPromptText("Collection Name");

        grid.add(new Label("New Collection Name:"), 0, 0);
        grid.add(collectionNameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                String name = collectionNameField.getText();

                // Call the method to add the collection to the database
                if (User.getCurrentUser() != null && !name.isEmpty()) {
                    addCollectionToUserDatabase(name, User.getCurrentUser().getId());

                    foodDisplayController.updateFoodDisplayByCollectionName(mainPageController.currentCollectionName);
                    refreshKitchenSelectorComboBox();
                }

            }
            return null;
        });

        dialog.showAndWait();
    }
    private void refreshKitchenSelectorComboBox() {
        ObservableList<String> kitchens = FXCollections.observableArrayList();
        String sql = "SELECT name FROM FoodCollections WHERE userId = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, User.getCurrentUser().getId()); // Assuming user ID is accessible
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                kitchens.add(rs.getString("name"));
            }

            kitchenSelectorComboBox.setItems(kitchens);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void handleKitchenSelectionComboBox() {
        mainPageController.currentCollectionName = kitchenSelectorComboBox.getSelectionModel().getSelectedItem();

        if (mainPageController.currentCollectionName != null) {
            System.out.println("CURRENT KITCHEN: " + mainPageController.currentCollectionName);
            foodDisplayController.updateFoodDisplayByCollectionName(mainPageController.currentCollectionName);
        }
    }
    public void addKitchensToKitchenSelectorComboBox() {
        ObservableList<String> kitchens = FXCollections.observableArrayList();
        // Fetch kitchen names from the database
        try (Connection conn = DriverManager.getConnection(DatabaseInitializer.URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT name FROM FoodCollections WHERE userId = ?")) {
            pstmt.setInt(1, User.getCurrentUser().getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                kitchens.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        kitchenSelectorComboBox.setItems(kitchens);
        if (!kitchens.isEmpty()) {
            kitchenSelectorComboBox.getSelectionModel().selectFirst();
            handleKitchenSelectionComboBox(); // Automatically display foods for the first kitchen
        }
    }
    private void populateCollectionNameComboBox(ComboBox<String> comboBox) {
        List<String> collectionNames = getCollectionNamesForUser(User.getCurrentUser().getId());
        comboBox.getItems().addAll(collectionNames);
    }
}
