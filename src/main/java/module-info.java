module org.example.kitchenorganizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports org.example.kitchenorganizer.login to javafx.fxml;

    opens org.example.kitchenorganizer.login to javafx.fxml;

    opens org.example.kitchenorganizer to javafx.fxml;
    exports org.example.kitchenorganizer;
    exports org.example.kitchenorganizer.classes;
    opens org.example.kitchenorganizer.classes to javafx.fxml;
    exports org.example.kitchenorganizer.notification;
    opens org.example.kitchenorganizer.notification to javafx.fxml;
    exports org.example.kitchenorganizer.mainpage;
    opens org.example.kitchenorganizer.mainpage to javafx.fxml;
    exports org.example.kitchenorganizer.database;
    opens org.example.kitchenorganizer.database to javafx.fxml;
}