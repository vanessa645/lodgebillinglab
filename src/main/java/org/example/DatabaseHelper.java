package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String DATABASE_URL =
            "jdbc:sqlite:lodge_billing.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS guest_bills (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    guest_name TEXT NOT NULL,
                    room_number TEXT NOT NULL,
                    room_type TEXT NOT NULL,
                    nights INTEGER NOT NULL,
                    total_amount REAL NOT NULL,
                    amount_paid REAL NOT NULL,
                    balance REAL NOT NULL,
                    status TEXT NOT NULL
                )
                """;

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);
            System.out.println("Database is ready.");

        } catch (SQLException e) {
            System.err.println(
                    "Could not create database table: "
                            + e.getMessage()
            );
        }
    }

    public static long insertBill(
            String guestName,
            String roomNumber,
            String roomType,
            int nights,
            double totalAmount,
            double amountPaid,
            double balance,
            String status) {

        String sql = """
                INSERT INTO guest_bills
                (guest_name, room_number, room_type, nights,
                 total_amount, amount_paid, balance, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, guestName);
            statement.setString(2, roomNumber);
            statement.setString(3, roomType);
            statement.setInt(4, nights);
            statement.setDouble(5, totalAmount);
            statement.setDouble(6, amountPaid);
            statement.setDouble(7, balance);
            statement.setString(8, status);

            statement.executeUpdate();

            try (Statement idStatement =
                         connection.createStatement();
                 ResultSet result = idStatement.executeQuery(
                         "SELECT last_insert_rowid()")) {

                if (result.next()) {
                    return result.getLong(1);
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Could not save guest bill: " + e.getMessage()
            );
        }

        return -1;
    }

    public static List<BillRecord> loadBills()
            throws SQLException {

        List<BillRecord> records = new ArrayList<>();

        String sql = "SELECT * FROM guest_bills ORDER BY id DESC";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {
                BillRecord record = new BillRecord(
                        result.getLong("id"),
                        result.getString("guest_name"),
                        result.getString("room_number"),
                        result.getString("room_type"),
                        result.getInt("nights"),
                        result.getDouble("total_amount"),
                        result.getDouble("amount_paid"),
                        result.getDouble("balance"),
                        result.getString("status")
                );

                records.add(record);
            }
        }

        return records;
    }

    public static boolean deleteBill(long id) {
        String sql = "DELETE FROM guest_bills WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println(
                    "Could not delete guest bill: " + e.getMessage()
            );
            return false;
        }
    }

    public static class BillRecord {
        public final long id;
        public final String guestName;
        public final String roomNumber;
        public final String roomType;
        public final int nights;
        public final double totalAmount;
        public final double amountPaid;
        public final double balance;
        public final String status;

        public BillRecord(
                long id,
                String guestName,
                String roomNumber,
                String roomType,
                int nights,
                double totalAmount,
                double amountPaid,
                double balance,
                String status) {

            this.id = id;
            this.guestName = guestName;
            this.roomNumber = roomNumber;
            this.roomType = roomType;
            this.nights = nights;
            this.totalAmount = totalAmount;
            this.amountPaid = amountPaid;
            this.balance = balance;
            this.status = status;
        }
    }
}