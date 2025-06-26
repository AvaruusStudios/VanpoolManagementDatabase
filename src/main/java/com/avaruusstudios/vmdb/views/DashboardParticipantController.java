package com.avaruusstudios.vmdb.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.ReadOnlyObjectWrapper; // Correct import for ReadOnlyObjectWrapper

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DashboardParticipantController {

    @FXML
    private TableView<Map<String, Object>> participantTableView;
    @FXML
    private Label lblTotalParticipants; // For summary detail
    @FXML
    private Label lblActiveParticipants; // For summary detail

    private MainController mainController; // Reference to the main controller

    /**
     * Called by FXMLLoader to give a reference to the main application controller.
     * This allows child controllers to update the main UI, like the status bar.
     * @param mainController The instance of the MainController.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        System.out.println("MainController set in ParticipantContentController.");
    }

    public void initialize() {
        System.out.println("ParticipantContentController initialized.");
        // Load data when this view is initialized
        loadParticipantData();
    }

    @FXML
    private void refreshData() {
        System.out.println("Refresh button clicked in ParticipantContentController.");
        loadParticipantData();
    }

    /**
     * Loads participant data from the SQLite database and populates the TableView.
     * This method reads an SQL query from a file, executes it, and dynamically
     * creates TableColumns based on the ResultSet metadata. It also calculates
     * total and active participants for summary labels.
     * <p>
     * Column widths are carefully set with min, pref, and max values to balance
     * fitting content (e.g., full names) with preventing excessively large
     * columns (e.g., for IDs), while ensuring the table fills its width without
     * horizontal scrolling via `CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN`.
     * </p>
     */
    private void loadParticipantData() {
        if (mainController != null) {
            mainController.updateStatusBar("Loading participant data...");
        }

        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
        participantTableView.getColumns().clear(); // Clear existing columns

        String url = "jdbc:sqlite:./vanpool.db"; // Your database connection URL
        String sqlQuery = "";
        String filename = "selectParticipantsDashboard.sql"; // The specific query for this view

        try (InputStream inputStream = getClass().getResourceAsStream("/com/avaruusstudios/vmdb/db/" + filename)) {
            if (inputStream == null) {
                String errorMsg = "Could not find SQL file: " + filename;
                System.err.println(errorMsg);
                if (mainController != null) {
                    mainController.updateStatusBar("ERROR: " + errorMsg);
                }
                return;
            }
            try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
                StringBuilder sb = new StringBuilder();
                while (scanner.hasNextLine()) {
                    sb.append(scanner.nextLine()).append("\n");
                }
                sqlQuery = sb.toString();
            }
        } catch (Exception e) {
            String errorMsg = "Error reading SQL file '" + filename + "': " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            if (mainController != null) {
                mainController.updateStatusBar("ERROR: " + errorMsg);
            }
            return;
        }

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<String> columnNames = new ArrayList<>();

            // 1. Create columns and set their factories with refined sizing rules
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String columnLabel = metaData.getColumnLabel(i);
                columnNames.add(columnName);

                TableColumn<Map<String, Object>, Object> column = new TableColumn<>(columnLabel);
                final String finalColumnName = columnName;
                column.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(finalColumnName)));

                // --- REFINED COLUMN SIZING LOGIC ---
                if (columnName.toLowerCase().endsWith("id") || columnName.toLowerCase().endsWith("_fk")) {
                    column.setMinWidth(50);
                    column.setPrefWidth(80); // Good for typical ID numbers
                    column.setMaxWidth(120); // Prevent IDs from taking too much space
                } else if (columnName.toLowerCase().equals("participant")) { // Specific for the combined name field
                    column.setMinWidth(180); // Minimum for a short name to be readable
                    column.setPrefWidth(280); // Aim for a typical full name display
                    column.setMaxWidth(Double.MAX_VALUE); // Allow it to expand significantly if needed
                } else if (columnName.toLowerCase().contains("name") || // General name fields
                        columnName.toLowerCase().contains("address") ||
                        columnName.toLowerCase().contains("notes") ||
                        columnName.toLowerCase().contains("email") ||
                        columnName.toLowerCase().contains("method") ||
                        columnName.toLowerCase().contains("program") ||
                        columnName.toLowerCase().contains("description") ||
                        columnName.toLowerCase().contains("type")) {
                    column.setMinWidth(100);
                    column.setPrefWidth(160); // Good for average text fields
                    column.setMaxWidth(300); // Cap general text fields
                } else if (columnName.toLowerCase().equals("isactive")) { // Specific for boolean 'IsActive'
                    column.setMinWidth(60);
                    column.setPrefWidth(80); // Just enough for "true" or "false"
                    column.setMaxWidth(100);
                } else { // Default for other columns (e.g., numbers, dates, smaller strings)
                    column.setMinWidth(75);
                    column.setPrefWidth(120);
                    column.setMaxWidth(200);
                }

                participantTableView.getColumns().add(column);
            }

            // Keep CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN to ensure the table fills its width
            // and avoids horizontal scrolling. This policy will use your defined min/pref/max widths
            // to distribute space, with the last column being flexible.
            participantTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);


            // 2. Populate data
            int totalParticipants = 0;
            int activeParticipants = 0; // Assuming 'IsActive' column exists
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (String colName : columnNames) {
                    row.put(colName, resultSet.getObject(colName));
                }
                data.add(row);
                totalParticipants++;
                if (row.containsKey("IsActive") && (Boolean) row.get("IsActive")) {
                    activeParticipants++;
                }
            }

            participantTableView.setItems(data);

            // Update summary labels
            lblTotalParticipants.setText(String.valueOf(totalParticipants));
            lblActiveParticipants.setText(String.valueOf(activeParticipants));


            if (mainController != null) {
                mainController.updateStatusBar("Participant data loaded successfully. " + totalParticipants + " records found.");
            }

        } catch (SQLException e) {
            String errorMsg = "SQL Error while loading participant data: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            if (mainController != null) {
                mainController.updateStatusBar("ERROR: " + errorMsg);
            }
        } finally {
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}