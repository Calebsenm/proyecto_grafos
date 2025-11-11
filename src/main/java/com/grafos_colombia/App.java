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
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/grafos_colombia/main.fxml"));
            
            if (loader.getLocation() == null) {
                System.err.println("Error: No se encontró main.fxml en /com/grafos_colombia/main.fxml");
                System.err.println("Intentando ruta alternativa...");
                loader.setLocation(getClass().getResource("main.fxml"));
            }
            
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 600);
            stage.setTitle("Visualizador de Rutas de Colombia");
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(500);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error cargando FXML: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        DatabaseMigration.init();
        launch();
    }

}
