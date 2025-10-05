package com.grafos_colombia;

import java.io.IOException;

import com.grafos_colombia.database.DatabaseMigration;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        
        try {
            // Load FXML from the correct path
            Parent root = FXMLLoader.load(getClass().getResource("/com/grafos_colombia/main.fxml"));
            Scene scene = new Scene(root, 1200, 600); // Set initial size
            stage.setTitle("Ruta más corta con Dijkstra - Colombia");
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
            
            // Show a simple error scene if FXML fails to load
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label("Error loading application: " + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            Scene errorScene = new Scene(new javafx.scene.layout.StackPane(errorLabel), 400, 200);
            stage.setTitle("Error - Dijkstra App");
            stage.setScene(errorScene);
            stage.show();
        }
    }

    public static void main(String[] args) {

        DatabaseMigration.init();

        launch();
    }

}
