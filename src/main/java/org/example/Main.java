
package org.example;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;

public class Main extends Application {

    private final ObservableList<GuestBill> bills =
            FXCollections.observableArrayList();

    private TableView<GuestBill> table;
    private Label messageLabel;

    @Override
    public void start(Stage stage) {

        // Prepare the database and load previously saved bills.
        DatabaseHelper.createTable();
        loadSavedBills();

        Label title = new Label("ICT261 LODGE BILLING SYSTEM");
        title.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1976D2;"
        );

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
        nightsField.setPromptText("Number of nights");

        TextField amountPaidField = new TextField();
        amountPaidField.setPromptText("Amount paid (K)");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);

        form.add(new Label("Guest Name:"), 0, 0);
        form.add(guestNameField, 1, 0);

        form.add(new Label("Room Number:"), 0, 1);
        form.add(roomNumberField, 1, 1);

        form.add(new Label("Room Type:"), 0, 2);
        form.add(roomTypeBox, 1, 2);

        form.add(new Label("Nights:"), 0, 3);
        form.add(nightsField, 1, 3);

        form.add(new Label("Amount Paid (K):"), 0, 4);
        form.add(amountPaidField, 1, 4);

        Button calculateButton = new Button("Add Guest Bill");
        Button deleteButton = new Button("Delete Selected");
        Button clearButton = new Button("Clear Fields");

        calculateButton.setStyle(
                "-fx-background-color: #1976D2;" +
                        "-fx-text-fill: white;"
        );

        deleteButton.setStyle(
                "-fx-background-color: #C62828;" +
                        "-fx-text-fill: white;"
        );

        messageLabel = new Label(
                "Enter guest details and click Add Guest Bill."
        );
        messageLabel.setWrapText(true);

        // Create the guest bills table.
        table = new TableView<>();
        table.setPlaceholder(
                new Label("No guest bills have been added yet.")
        );

        TableColumn<GuestBill, String> nameColumn =
                new TableColumn<>("Guest Name");
        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("guestName")
        );

        TableColumn<GuestBill, String> roomColumn =
                new TableColumn<>("Room No.");
        roomColumn.setCellValueFactory(
                new PropertyValueFactory<>("roomNumber")
        );

        TableColumn<GuestBill, String> typeColumn =
                new TableColumn<>("Room Type");
        typeColumn.setCellValueFactory(
                new PropertyValueFactory<>("roomType")
        );

        TableColumn<GuestBill, String> nightsColumn =
                new TableColumn<>("Nights");
        nightsColumn.setCellValueFactory(
                new PropertyValueFactory<>("nights")
        );

        TableColumn<GuestBill, String> totalColumn =
                new TableColumn<>("Total (K)");
        totalColumn.setCellValueFactory(
                new PropertyValueFactory<>("totalText")
        );

        TableColumn<GuestBill, String> paidColumn =
                new TableColumn<>("Paid (K)");
        paidColumn.setCellValueFactory(
                new PropertyValueFactory<>("paidText")
        );

        TableColumn<GuestBill, String> balanceColumn =
                new TableColumn<>("Balance (K)");
        balanceColumn.setCellValueFactory(
                new PropertyValueFactory<>("balanceText")
        );

        TableColumn<GuestBill, String> statusColumn =
                new TableColumn<>("Status");
        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("paymentStatus")
        );

        table.getColumns().addAll(
                nameColumn,
                roomColumn,
                typeColumn,
                nightsColumn,
                totalColumn,
                paidColumn,
                balanceColumn,
                statusColumn
        );

        table.setItems(bills);
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        // Add a new guest bill and save it to SQLite.
        calculateButton.setOnAction(event -> {

            String name = guestNameField.getText().trim();
            String room = roomNumberField.getText().trim();
            String type = roomTypeBox.getValue();
            String nightsText = nightsField.getText().trim();
            String paidText = amountPaidField.getText().trim();

            if (name.isEmpty() || room.isEmpty() || type == null
                    || nightsText.isEmpty() || paidText.isEmpty()) {
                showAlert(
                        "Missing Details",
                        "Please fill in all fields."
                );
                return;
            }

            int nights;

            try {
                nights = Integer.parseInt(nightsText);

                if (nights <= 0) {
                    showAlert(
                            "Invalid Nights",
                            "Nights must be greater than zero."
                    );
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert(
                        "Invalid Nights",
                        "Enter a whole number for nights."
                );
                return;
            }

            double rate;

            switch (type) {
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
                    showAlert(
                            "Invalid Room",
                            "Select a valid room type."
                    );
                    return;
            }

            double total = rate * nights;
            double paid;

            try {
                paid = Double.parseDouble(paidText);

                if (!Double.isFinite(paid)
                        || paid < 0 || paid > total) {
                    showAlert(
                            "Invalid Payment",
                            "Payment must be between K0 and the total bill."
                    );
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert(
                        "Invalid Payment",
                        "Enter a valid amount paid."
                );
                return;
            }

            double balance = total - paid;

            String status;
            if (balance == 0) {
                status = "PAID IN FULL";
            } else if (paid > 0) {
                status = "PARTIALLY PAID";
            } else {
                status = "NOT PAID";
            }

            // Save the bill first. Only display it if saving succeeds.
            long id = DatabaseHelper.insertBill(
                    name, room, type, nights,
                    total, paid, balance, status
            );

            if (id < 0) {
                showAlert(
                        "Database Error",
                        "The bill could not be saved. " +
                                "Please check the console for details."
                );
                return;
            }

            GuestBill bill = new GuestBill(
                    id, name, room, type, nights,
                    total, paid, balance, status
            );

            bills.add(bill);

            messageLabel.setText(
                    "Bill saved for " + name +
                            ". Total: K" + money(total)
            );

            guestNameField.clear();
            roomNumberField.clear();
            roomTypeBox.setValue(null);
            nightsField.clear();
            amountPaidField.clear();
        });

        // Delete the selected bill from SQLite and the table.
        deleteButton.setOnAction(event -> {

            GuestBill selected = table.getSelectionModel()
                    .getSelectedItem();

            if (selected == null) {
                showAlert(
                        "No Selection",
                        "Please select a guest bill to delete."
                );
                return;
            }

            Alert confirmation = new Alert(
                    Alert.AlertType.CONFIRMATION
            );
            confirmation.setTitle("Confirm Deletion");
            confirmation.setHeaderText(null);
            confirmation.setContentText(
                    "Delete the bill for " +
                            selected.getGuestName() + "?"
            );

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {

                    boolean deleted =
                            DatabaseHelper.deleteBill(selected.getId());

                    if (deleted) {
                        bills.remove(selected);
                        messageLabel.setText(
                                "Guest bill deleted successfully."
                        );
                    } else {
                        showAlert(
                                "Database Error",
                                "The bill could not be deleted."
                        );
                    }
                }
            });
        });

        clearButton.setOnAction(event -> {
            guestNameField.clear();
            roomNumberField.clear();
            roomTypeBox.setValue(null);
            nightsField.clear();
            amountPaidField.clear();
            messageLabel.setText("Fields cleared.");
        });

        HBox buttons = new HBox(
                10, calculateButton, deleteButton, clearButton
        );
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(
                18, title, form, buttons, messageLabel, table
        );
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F7FB;");

        Scene scene = new Scene(root, 1100, 650);

        stage.setTitle("ICT261 Lodge Billing System");
        stage.setScene(scene);
        stage.setMinWidth(850);
        stage.setMinHeight(550);
        stage.show();
    }

    private void loadSavedBills() {
        try {
            for (DatabaseHelper.BillRecord record
                    : DatabaseHelper.loadBills()) {

                bills.add(new GuestBill(
                        record.id,
                        record.guestName,
                        record.roomNumber,
                        record.roomType,
                        record.nights,
                        record.totalAmount,
                        record.amountPaid,
                        record.balance,
                        record.status
                ));
            }
        } catch (Exception e) {
            System.err.println(
                    "Could not load saved bills: " + e.getMessage()
            );
        }
    }

    private static String money(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class GuestBill {

        private final long id;
        private final SimpleStringProperty guestName;
        private final SimpleStringProperty roomNumber;
        private final SimpleStringProperty roomType;
        private final SimpleStringProperty nights;
        private final SimpleStringProperty totalText;
        private final SimpleStringProperty paidText;
        private final SimpleStringProperty balanceText;
        private final SimpleStringProperty paymentStatus;

        public GuestBill(
                long id,
                String guestName,
                String roomNumber,
                String roomType,
                int nights,
                double total,
                double paid,
                double balance,
                String paymentStatus
        ) {
            this.id = id;
            this.guestName = new SimpleStringProperty(guestName);
            this.roomNumber = new SimpleStringProperty(roomNumber);
            this.roomType = new SimpleStringProperty(roomType);
            this.nights = new SimpleStringProperty(
                    String.valueOf(nights)
            );
            this.totalText = new SimpleStringProperty(money(total));
            this.paidText = new SimpleStringProperty(money(paid));
            this.balanceText = new SimpleStringProperty(money(balance));
            this.paymentStatus =
                    new SimpleStringProperty(paymentStatus);
        }

        public long getId() {
            return id;
        }

        public String getGuestName() {
            return guestName.get();
        }

        public String getRoomNumber() {
            return roomNumber.get();
        }

        public String getRoomType() {
            return roomType.get();
        }

        public String getNights() {
            return nights.get();
        }

        public String getTotalText() {
            return totalText.get();
        }

        public String getPaidText() {
            return paidText.get();
        }

        public String getBalanceText() {
            return balanceText.get();
        }

        public String getPaymentStatus() {
            return paymentStatus.get();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
