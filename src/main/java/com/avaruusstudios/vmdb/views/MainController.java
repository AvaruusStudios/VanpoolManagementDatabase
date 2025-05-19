package com.avaruusstudios.vmdb.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController {
    @FXML
    private TableView<Map<String, Object>> mainTableView;
    @FXML
    private Label txtCurrentDate;
    @FXML
    private TitledPane participantTitledPane;
    @FXML
    private TitledPane transactionTitledPane;
    @FXML
    private TitledPane locationTitledPane;
    @FXML
    private Hyperlink createParticipant;
    @FXML
    private Hyperlink updateParticipant;
    @FXML
    private Hyperlink deleteParticipant;
    @FXML
    private Hyperlink createTransaction;
    @FXML
    private Hyperlink updateTransaction;
    @FXML
    private Hyperlink deleteTransaction;

    public void initialize() {
        // Set the current date in the txtCurrentDate TextField
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy"); // You can adjust the format
        txtCurrentDate.setText(currentDate.format(formatter));

        // Set onAction handlers for Participant Hyperlinks
        createParticipant.setOnAction(event -> System.out.println("Create Participant clicked - Implement Add Logic"));
        updateParticipant.setOnAction(event -> System.out.println("Update Participant clicked - Implement Edit Logic"));
        deleteParticipant.setOnAction(event -> System.out.println("Delete Participant clicked - Implement Delete Logic"));

        // Set onAction handlers for Transaction Hyperlinks
        createTransaction.setOnAction(event -> System.out.println("Create Transaction clicked - Implement Add Logic"));
        updateTransaction.setOnAction(event -> System.out.println("Update Transaction clicked - Implement Edit Logic"));
        deleteTransaction.setOnAction(event -> System.out.println("Delete Transaction clicked - Implement Delete Logic"));

// Listen for expansion changes on Participant TitledPane
        participantTitledPane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                InputStream inputStream = getClass().getResourceAsStream("/com/avaruusstudios/vmdb/db/qryParticipants.sql");
                loadTableData(inputStream, "qryParticipants.sql");
            } else {
                // Only clear if NEITHER of the other panes is expanded
                if (!transactionTitledPane.isExpanded() && !locationTitledPane.isExpanded()) {
                    System.out.println("Participant TitledPane collapsed - Clearing table");
                    clearTableData();
                } else {
                    System.out.println("Participant TitledPane collapsed, but another pane is expanded - NOT clearing table");
                }
            }
        });

        // Listen for expansion changes on Transaction TitledPane
        transactionTitledPane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                InputStream inputStream = getClass().getResourceAsStream("/com/avaruusstudios/vmdb/db/qryTransactions.sql");
                loadTableData(inputStream, "qryTransactions.sql");
            } else {
                // Only clear if NEITHER of the other panes is expanded
                if (!participantTitledPane.isExpanded() && !locationTitledPane.isExpanded()) {
                    System.out.println("Transaction TitledPane collapsed - Clearing table");
                    clearTableData();
                } else {
                    System.out.println("Transaction TitledPane collapsed, but another pane is expanded - NOT clearing table");
                }
            }
        });

        // Listen for expansion changes on Locations TitledPane
        locationTitledPane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                InputStream inputStream = getClass().getResourceAsStream("/com/avaruusstudios/vmdb/db/qryLocations.sql");
                loadTableData(inputStream, "qryLocations.sql");
            } else {
                // Only clear if NEITHER of the other panes is expanded
                if (!participantTitledPane.isExpanded() && !transactionTitledPane.isExpanded()) {
                    System.out.println("Locations TitledPane collapsed - Clearing table");
                    clearTableData();
                } else {
                    System.out.println("Locations TitledPane collapsed, but another pane is expanded - NOT clearing table");
                }
            }
        });
    }

    /** Loads data into the TableView based on the provided SQL query */
    private void loadTableData(InputStream inputStream, String filename) {
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
        mainTableView.getColumns().clear();

        String url = "jdbc:sqlite:./vanpool.db";
        String sqlQuery = "";

        if (inputStream == null) {
            System.err.println("Could not find SQL file: " + filename);
            return;
        }

        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine()).append("\n");
            }
            sqlQuery = sb.toString();
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
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                columnNames.add(columnName);
                TableColumn<Map<String, Object>, Object> column = new TableColumn<>(columnName);
                final String finalColumnName = columnName;
                column.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyObjectWrapper<>(cellData.getValue().get(finalColumnName)));
                mainTableView.getColumns().add(column);
            }

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (String columnName : columnNames) {
                    row.put(columnName, resultSet.getObject(columnName));
                }
                data.add(row);
            }

            mainTableView.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Ensure resources are closed in the finally block
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /** Clears the data from the TableView */
    private void clearTableData() {
        mainTableView.getItems().clear();
        mainTableView.getColumns().clear();
        // Optionally, you could set a placeholder text if the table is empty
         mainTableView.setPlaceholder(new Label("No data to display"));
    }
}