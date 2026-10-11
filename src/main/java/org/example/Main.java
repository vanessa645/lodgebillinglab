
package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label title = new Label("ICT261 Lodge Billing System");
        Label message = new Label("Welcome to the Lodge Billing Lab!");

        VBox root = new VBox(15);
        root.getChildren().addAll(title, message);
        root.setStyle("-fx-padding: 30; -fx-alignment: center;");

        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("Lodge Billing System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
