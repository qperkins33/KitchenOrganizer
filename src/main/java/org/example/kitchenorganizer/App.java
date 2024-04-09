package org.example.kitchenorganizer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.kitchenorganizer.database.DatabaseInitializer;

/**
 * [START APP IN THIS FILE]
 *
 * Login Info:
 * Username: user
 * Password: pass
 *
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1300, 800);
        scene.getStylesheets().add(getClass().getResource("LoginForm.css").toExternalForm());

        primaryStage.setTitle("Kitchen Organizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        DatabaseInitializer.initializeDatabase();
        DatabaseInitializer.displayAllUsers(); // this line displays users in terminal (Testing purposes)

        launch(args);
    }
}
