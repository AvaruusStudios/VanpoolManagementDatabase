package com.avaruusstudios.vmdb.views;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.InputStream;
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
    private Accordion mainAccordion;
    @FXML
    private Label txtCurrentDate;
    @FXML
    private TitledPane participantTitledPane;
    @FXML
    private TitledPane transactionTitledPane;
    @FXML
    private TitledPane locationTitledPane;
    @FXML
    private TitledPane vehicleTitledPane;
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
    @FXML
    private Hyperlink createLocation;
    @FXML
    private Hyperlink updateLocation;
    @FXML
    private Hyperlink deleteLocation;
    @FXML
    private Hyperlink createVehicle;
    @FXML
    private Hyperlink updateVehicle;
    @FXML
    private Hyperlink deleteVehicle;

    // The 'isFullyInitialized' flag is still useful for other potential deferred logic,
    // but the 'oldValue == null' check directly addresses initial listener firings.
    private boolean isFullyInitialized = false; // You can remove this line if it's not used elsewhere

    public void initialize() {
        // Set the current date in the txtCurrentDate TextField
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        txtCurrentDate.setText(currentDate.format(formatter));

        // Set onAction handlers for Participant Hyperlinks
        createParticipant.setOnAction(event -> {
            System.out.println("Create Participant clicked - Implement Add Logic");
            createParticipant.setTextFill(Color.BLACK);
            revertHyperlinkColor(createParticipant);
        });
        updateParticipant.setOnAction(event -> {
            System.out.println("Update Participant clicked - Implement Edit Logic");
            updateParticipant.setTextFill(Color.BLACK);
            revertHyperlinkColor(updateParticipant);
        });
        deleteParticipant.setOnAction(event -> {
            System.out.println("Delete Participant clicked - Implement Delete Logic");
            deleteParticipant.setTextFill(Color.BLACK);
            revertHyperlinkColor(deleteParticipant);
        });

        // Set onAction handlers for Transaction Hyperlinks
        createTransaction.setOnAction(event -> {
            System.out.println("Create Transaction clicked - Implement Add Logic");
            createTransaction.setTextFill(Color.BLACK);
            revertHyperlinkColor(createTransaction);
        });
        updateTransaction.setOnAction(event -> {
            System.out.println("Update Transaction clicked - Implement Edit Logic");
            updateTransaction.setTextFill(Color.BLACK);
            revertHyperlinkColor(updateTransaction);
        });
        deleteTransaction.setOnAction(event -> {
            System.out.println("Delete Transaction clicked - Implement Delete Logic");
            deleteTransaction.setTextFill(Color.BLACK);
            revertHyperlinkColor(deleteTransaction);
        });

        // Set onAction handlers for Location Hyperlinks
        createLocation.setOnAction(event -> {
            System.out.println("Create Location clicked - Implement Add Logic");
            createLocation.setTextFill(Color.BLACK);
            revertHyperlinkColor(createLocation);
        });
        updateLocation.setOnAction(event -> {
            System.out.println("Update Location clicked - Implement Edit Logic");
            updateLocation.setTextFill(Color.BLACK);
            revertHyperlinkColor(updateLocation);
        });
        deleteLocation.setOnAction(event -> {
            System.out.println("Delete Location clicked - Implement Delete Logic");
            deleteLocation.setTextFill(Color.BLACK);
            revertHyperlinkColor(deleteLocation);
        });

        // Set onAction handlers for Vehicle Hyperlinks
        createVehicle.setOnAction(event -> {
            System.out.println("Create Vehicle clicked - Implement Add Logic");
            createVehicle.setTextFill(Color.BLACK);
            revertHyperlinkColor(createVehicle);
        });
        updateVehicle.setOnAction(event -> {
            System.out.println("Update Vehicle clicked - Implement Edit Logic");
            updateVehicle.setTextFill(Color.BLACK);
            revertHyperlinkColor(updateVehicle);
        });
        deleteVehicle.setOnAction(event -> {
            System.out.println("Delete Vehicle clicked - Implement Delete Logic");
            deleteVehicle.setTextFill(Color.BLACK);
            revertHyperlinkColor(deleteVehicle);
        });

        // **Centralized Listener for Accordion's expanded pane**
        mainAccordion.expandedPaneProperty().addListener((observable, oldPane, newPane) -> {
            // Only proceed after initial setup is complete
            if (!isFullyInitialized) {
                System.out.println("Accordion state change during initial setup - NOT taking action.");
                return; // Exit early during initial load
            }

            if (newPane == null) {
                // This means all panes are now collapsed
                System.out.println("All TitledPanes collapsed - Clearing table.");
                clearTableData();
            } else {
                // A pane is now expanded, load its data
                // Use newPane.getId() if you've set fx:id for your TitledPanes,
                // otherwise compare the TitledPane objects directly.
                System.out.println("Pane '" + newPane.getText() + "' expanded - Loading data.");

                if (newPane == participantTitledPane) {
                    InputStream inputStream = getClass().getResourceAsStream("/com/avaruusstudios/vmdb/db/selectParticipantsAll.sql");
                    loadTableData(inputStream, "selectParticipantsAll.sql");
                } else if (newPane == transactionTitledPane) {
                    InputStream inputStream = getClass().getResourceAsStream("/com/avaruusstudios/vmdb/db/selectTransactionsAll.sql");
                    loadTableData(inputStream, "selectTransactionsAll.sql");
                } else if (newPane == locationTitledPane) {
                    InputStream inputStream = getClass().getResourceAsStream("/com/avaruusstudios/vmdb/db/selectLocationsAll.sql");
                    loadTableData(inputStream, "selectLocationsAll.sql");
                } else if (newPane == vehicleTitledPane) {
                    InputStream inputStream = getClass().getResourceAsStream("/com/avaruusstudios/vmdb/db/selectVehiclesAll.sql");
                    loadTableData(inputStream, "selectVehiclesAll.sql");
                }
            }
        });

        // Set the flag to true AFTER the current UI cycle finishes.
        // This ensures all FXML loading, property settings, and initial listener firings have occurred.
        Platform.runLater(() -> {
            isFullyInitialized = true;
            System.out.println("Application fully initialized and ready for user interaction.");

            // Optional: If you want the table to be explicitly clear on startup (even if Accordion is null),
            // you can call clearTableData() here *once* after initialization.
            // This is useful if no pane is expanded by default in your FXML.
            if (mainAccordion.getExpandedPane() == null) {
                clearTableData();
                System.out.println("Table cleared initially as no pane is expanded.");
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
        } catch (Exception e) { // Catching generic Exception for robustness in file reading
            System.err.println("Error reading SQL file '" + filename + "': " + e.getMessage());
            e.printStackTrace();
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
            System.err.println("SQL Error while loading data from " + filename + ": " + e.getMessage());
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
        mainTableView.setPlaceholder(new Label("No data to display"));
    }

    /** Sets the color of the Hyperlinks back to DEFAULT */
    private void revertHyperlinkColor(Hyperlink hyperlink) {
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        // Use a common web color for hyperlink blue, you might need to adjust based on actual default
        pause.setOnFinished(event -> hyperlink.setTextFill(Color.web("#0000EE")));
        pause.play();
    }
}