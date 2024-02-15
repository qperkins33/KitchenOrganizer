module org.example.kitchenorganizer {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.kitchenorganizer to javafx.fxml;
    exports org.example.kitchenorganizer;
}