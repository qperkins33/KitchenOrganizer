module org.example.kitchenorganizer {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.kitchenorganizer to javafx.fxml;
    exports org.example.kitchenorganizer;
    exports org.example.kitchenorganizer.classes;
    opens org.example.kitchenorganizer.classes to javafx.fxml;
    exports org.example.kitchenorganizer.notification;
    opens org.example.kitchenorganizer.notification to javafx.fxml;
}