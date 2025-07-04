package com.avaruusstudios.vmdb.view.vanpool.participant;

import com.avaruusstudios.vmdb.model.Participant;
import com.avaruusstudios.vmdb.view.main.MainController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import com.avaruusstudios.vmdb.model.Program; // Still needed for Program enum conversion

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Scanner;

/**
 * <p>
 * Controls the Dashboard Participant view within the Vanpool Management System.
 * This controller is responsible for:
 * </p>
 * <ul>
 * <li>Initializing the {@link TableView} to display participant summary data.</li>
 * <li>Loading participant data from the database using a specific dashboard query.</li>
 * <li>Populating the {@link TableView} with {@link DashboardParticipantView} objects.</li>
 * <li>Updating summary labels (total and active participants).</li>
 * <li>Handling data refresh requests.</li>
 * </ul>
 *
 * <p>
 * This controller operates independently of the core {@code Participant} model for its display,
 * utilizing {@link DashboardParticipantView} to accurately reflect the data shape
 * provided by the `selectParticipantsDashboard.sql` query.
 * </p>
 *
 * @see MainController
 * @see DashboardParticipantView
 * @see Participant
 */
public class DashboardParticipantController {
    /**
     * The {@link TableView} displaying a summary list of participants.
     * It is typed to {@link DashboardParticipantView} to match the data structure
     * returned by the dashboard-specific SQL query.
     */
    @FXML
    private TableView<DashboardParticipantView> participantTableView;
    /**
     * A {@link Label} used to display the total number of participants loaded.
     */
    @FXML
    private Label lblTotalParticipants;
    /**
     * A {@link Label} used to display the count of active participants.
     */
    @FXML
    private Label lblActiveParticipants;

    /**
     * Reference to the {@link MainController} for communication,
     * primarily for updating the status bar.
     */
    private MainController mainController;

    /**
     * Sets the main controller for this dashboard view controller.
     * This method is typically called by the {@link MainController} itself during setup.
     *
     * @param mainController The {@link MainController} instance.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        System.out.println("MainController set in DashboardParticipantController.");
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method is part of the JavaFX lifecycle. It sets up the table columns
     * and triggers the initial loading of participant data.
     */
    public void initialize() {
        System.out.println("DashboardParticipantController initialized.");
        setupTableColumns(); // Define columns specifically for the dashboard query
        loadParticipantData();
    }

    /**
     * Defines and adds the columns to the {@link TableView}.
     * Columns are configured to display properties from the {@link DashboardParticipantView}
     * and align with the `selectParticipantsDashboard.sql` query's output.
     */
    private void setupTableColumns() {
        // Clear any existing columns, important for re-initialization or refresh logic
        participantTableView.getColumns().clear();

        // Column for Participant ID
        TableColumn<DashboardParticipantView, Integer> idCol = new TableColumn<>("ID");
        // Binds to the 'id' property of DashboardParticipantView via its getId() method
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(30);

        // Column for the concatenated Participant Name
        TableColumn<DashboardParticipantView, String> participantNameCol = new TableColumn<>("Participant");
        // Binds to the 'participantDisplayName' property of DashboardParticipantView
        participantNameCol.setCellValueFactory(new PropertyValueFactory<>("participantDisplayName"));
        participantNameCol.setPrefWidth(250);

        // Column for the Program
        TableColumn<DashboardParticipantView, String> programCol = new TableColumn<>("Program");
        // Uses a custom CellValueFactory to display the user-friendly value of the Program enum
        programCol.setCellValueFactory(cellData -> {
            Program program = cellData.getValue().getProgram(); // Get Program enum from DashboardParticipantView
            return new ReadOnlyStringWrapper(program != null ? program.getDisplayValue() : Program.NONE.getDisplayValue());
        });
        programCol.setPrefWidth(150);

        // Column for Active Status
        TableColumn<DashboardParticipantView, Boolean> activeCol = new TableColumn<>("Active");
        // Binds to the 'active' property of DashboardParticipantView via its isActive() method
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        activeCol.setPrefWidth(80);

        // Add all defined columns to the TableView
        participantTableView.getColumns().addAll(idCol, participantNameCol, programCol, activeCol);

        // Set column resize policy for better UI appearance
        participantTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Event handler for the refresh button. Triggers a reload of participant data.
     */
    @FXML
    private void refreshData() {
        System.out.println("Refresh button clicked in DashboardParticipantController.");
        loadParticipantData();
    }

    /**
     * Loads participant summary data from the SQLite database.
     * This method reads the SQL query from a file, executes it, and maps
     * the results into {@link DashboardParticipantView} objects to populate the {@link TableView}.
     * It also updates the total and active participant counts.
     */
    private void loadParticipantData() {
        if (mainController != null) {
            mainController.updateStatusBar("Loading dashboard participant data...");
        }

        // List to hold the view model objects
        ObservableList<DashboardParticipantView> dashboardParticipants = FXCollections.observableArrayList();

        String url = "jdbc:sqlite:./vanpool.db";
        String sqlQuery = "";
        String filename = "selectParticipantsDashboard.sql"; // The SQL query file for the dashboard

        // --- Read SQL Query from File ---
        try (InputStream inputStream = getClass().getResourceAsStream("/com/avaruusstudios/vmdb/db/" + filename)) {
            if (inputStream == null) {
                String errorMsg = "Could not find SQL file: " + filename;
                System.err.println(errorMsg);
                if (mainController != null) {
                    mainController.updateStatusBar("ERROR: " + errorMsg);
                }
                return; // Exit if SQL file not found
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
            return; // Exit on file reading error
        }

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        // --- Execute SQL Query and Map Results ---
        try {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                // Retrieve data directly from ResultSet using the SQL query's column aliases
                // These aliases must exactly match the 'AS' names in your SQL query
                int id = resultSet.getInt("ID");
                String participantDisplayName = resultSet.getString("Participant");
                // Program.fromDbValue() converts the stored string to the enum
                Program program = Program.fromDbValue(resultSet.getString("Program"));
                // resultSet.getBoolean() correctly converts 0/1 to false/true
                boolean isActive = resultSet.getBoolean("IsActive");

                // Create a new DashboardParticipantView object with the retrieved data
                DashboardParticipantView view = new DashboardParticipantView(id, participantDisplayName, program, isActive);
                dashboardParticipants.add(view);
            }

            // Set the populated list to the TableView
            participantTableView.setItems(dashboardParticipants);

            // --- Update Summary Labels ---
            // Total participants is simply the size of the list
//            lblTotalParticipants.setText(String.valueOf(dashboardParticipants.size()));
            // Count active participants using Java 8 Stream API
            long activeParticipantsCount = dashboardParticipants.stream()
                    .filter(DashboardParticipantView::isActive) // Filters where isActive() returns true
                    .count();
            lblActiveParticipants.setText(String.valueOf(activeParticipantsCount));


            // Update status bar on successful load
            if (mainController != null) {
                mainController.updateStatusBar("Dashboard data loaded successfully. " + dashboardParticipants.size() + " records found.");
            }

        } catch (SQLException e) {
            // Handle SQL-related errors
            String errorMsg = "SQL Error while loading dashboard participant data: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            if (mainController != null) {
                mainController.updateStatusBar("ERROR: " + errorMsg);
            }
        } finally {
            // Ensure JDBC resources are closed in a finally block
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}