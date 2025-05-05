package model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/simulation_view.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        primaryStage.setTitle("Health Center Simulation");
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(900);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
