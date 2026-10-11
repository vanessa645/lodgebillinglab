
package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        Label title = new Label("ICT261 LODGE BILLING SYSTEM");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField guestNameField = new TextField();
        guestNameField.setPromptText("Enter guest name");

        TextField roomNumberField = new TextField();
        roomNumberField.setPromptText("Enter room number");

        ComboBox<String> roomTypeBox = new ComboBox<>();
        roomTypeBox.getItems().addAll(
                "Single Room",
                "Double Room",
                "Deluxe Room",
                "Suite"
        );
        roomTypeBox.setPromptText("Select room type");
        roomTypeBox.setMaxWidth(Double.MAX_VALUE);

        TextField nightsField = new TextField();
        nightsField.setPromptText("Enter number of nights");

        Button calculateButton = new Button("Calculate Bill");
        Button clearButton = new Button("Clear");

        Label resultLabel = new Label("Bill details will appear here.");
        resultLabel.setWrapText(true);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(15);

        form.add(new Label("Guest Name:"), 0, 0);
        form.add(guestNameField, 1, 0);

        form.add(new Label("Room Number:"), 0, 1);
        form.add(roomNumberField, 1, 1);

        form.add(new Label("Room Type:"), 0, 2);
        form.add(roomTypeBox, 1, 2);

        form.add(new Label("Number of Nights:"), 0, 3);
        form.add(nightsField, 1, 3);

        calculateButton.setOnAction(event -> {
            String guestName = guestNameField.getText().trim();
            String roomNumber = roomNumberField.getText().trim();
            String roomType = roomTypeBox.getValue();
            String nightsText = nightsField.getText().trim();

            if (guestName.isEmpty() || roomNumber.isEmpty()
                    || roomType == null || nightsText.isEmpty()) {
                showAlert("Missing Details",
                        "Please fill in all fields.");
                return;
            }

            int nights;

            try {
                nights = Integer.parseInt(nightsText);

                if (nights <= 0) {
                    showAlert("Invalid Nights",
                            "Number of nights must be greater than zero.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input",
                        "Enter a whole number for the nights.");
                return;
            }

            double rate;

            switch (roomType) {
                case "Single Room":
                    rate = 500;
                    break;
                case "Double Room":
                    rate = 800;
                    break;
                case "Deluxe Room":
                    rate = 1200;
                    break;
                case "Suite":
                    rate = 1800;
                    break;
                default:
                    rate = 0;
            }

            double total = rate * nights;

            resultLabel.setText(
                    "GUEST BILL\n\n"
                            + "Guest Name: " + guestName + "\n"
                            + "Room Number: " + roomNumber + "\n"
                            + "Room Type: " + roomType + "\n"
                            + "Number of Nights: " + nights + "\n"
                            + "Rate per Night: K" + String.format("%.2f", rate) + "\n"
                            + "TOTAL BILL: K" + String.format("%.2f", total)
            );
        });

        clearButton.setOnAction(event -> {
            guestNameField.clear();
            roomNumberField.clear();
            roomTypeBox.setValue(null);
            nightsField.clear();
            resultLabel.setText("Bill details will appear here.");
        });

        calculateButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white;");
        clearButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white;");

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(
                title, form,
                new VBox(12, calculateButton, clearButton, resultLabel)
        );
        root.setStyle("-fx-background-color: #F4F7FB;");

        Scene scene = new Scene(root, 600, 500);

        stage.setTitle("ICT261 Lodge Billing System");
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
