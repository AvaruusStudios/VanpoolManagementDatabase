package com.avaruusstudios.vmdb.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//            System.out.println("Participant TitledPane expanded: " + newValue);
            if (newValue) {
                loadTableData("SELECT * FROM Participants");
            } else {
//                System.out.println("Participant TitledPane collapsed - Clearing table");
                clearTableData();
            }
        });

        // Listen for expansion changes on Transaction TitledPane
        transactionTitledPane.expandedProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("Transaction TitledPane expanded: " + newValue);
            if (newValue) {
                loadTableData("SELECT * FROM Transactions"); // Adjust query as needed
            } else {
//                System.out.println("Transaction TitledPane collapsed - Clearing table");
                clearTableData();
            }
        });
    }

    /** Loads data into the TableView based on the provided SQL query */
    private void loadTableData(String sqlQuery) {
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
        mainTableView.getColumns().clear(); // Clear any existing columns

        String url = "jdbc:sqlite:./vanpool.db";
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                columnNames.add(columnName);
                TableColumn<Map<String, Object>, Object> column = new TableColumn<>(columnName);
                final String finalColumnName = columnName; // Need a final variable for the lambda

                column.setCellValueFactory(cellData ->
                        new javafx.beans.property.ReadOnlyObjectWrapper<>(cellData.getValue().get(finalColumnName)));

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
            e.printStackTrace(); // Handle the exception properly
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