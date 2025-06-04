package com.avaruusstudios.vmdb;

import com.avaruusstudios.vmdb.db.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/avaruusstudios/vmdb/views/main.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        scene.getStylesheets().add(getClass().getResource("styles/styles.css").toExternalForm());
        stage.setTitle("Vanpool Management Database");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Initialize the database (create if it doesn't exist)
        Database.createDatabase();
        launch();
    }
}