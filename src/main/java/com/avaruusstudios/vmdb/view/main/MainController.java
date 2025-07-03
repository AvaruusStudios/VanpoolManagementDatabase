package com.avaruusstudios.vmdb.view.main;

import com.avaruusstudios.vmdb.db.DatabaseManager;
import com.avaruusstudios.vmdb.view.vanpool.participant.DashboardParticipantController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects; // Import Objects for Objects.requireNonNull for getResource

/**
 * <p>
 * The main controller for the Vanpool Management System's primary application window.
 * This class manages the overall UI layout, handles navigation between different
 * dashboard views, and orchestrates interactions with backend services like
 * {@link DatabaseManager} for displaying summary data.
 * </p>
 *
 * <p>
 * It is responsible for initializing the dashboard summary labels, setting up
 * navigation button actions, and dynamically loading FXML content into the
 * central {@link #contentStackPane}. It also provides a status bar for
 * application feedback and a utility for displaying user-facing alerts.
 * </p>
 *
 * @see DatabaseManager
 * @see com.avaruusstudios.vmdb.db.Database
 * @see com.avaruusstudios.vmdb.db.QueryLoader
 */
public class MainController {

    /**
     * An instance of {@link Logger} from the SLF4J API.
     * Used for capturing and outputting diagnostic messages, warnings, and errors
     * related to UI operations and interactions within the main controller.
     */
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    /**
     * The central {@link StackPane} where different FXML views (dashboards, forms)
     * are loaded and displayed. This acts as the main content area of the application.
     */
    @FXML
    private StackPane contentStackPane;

    /**
     * The main {@link Accordion} that organizes the navigation sections (e.g., Vanpool, Billing).
     */
    @FXML
    private Accordion mainAccordion;

    /**
     * A {@link Label} displaying the current date.
     */
    @FXML
    private Label txtCurrentDate;

    /**
     * A {@link Label} displaying information about the most recent transaction. (Placeholder for future implementation).
     */
    @FXML
    private Label txtRecentTransaction;

    /**
     * A {@link Label} displaying the current financial balance. (Placeholder for future implementation).
     */
    @FXML
    private Label txtBalance;

    /**
     * A {@link Label} used as a status bar at the bottom of the main window to provide user feedback.
     */
    @FXML
    private Label statusBarLabel;

    /**
     * The {@link TitledPane} for the Vanpool section within the main accordion.
     */
    @FXML
    private TitledPane vanpoolTitledPane;

    /**
     * The {@link TitledPane} for the Billing section within the main accordion.
     */
    @FXML
    private TitledPane billingTitledPane;

    /**
     * Button to load the Vanpool Dashboard view.
     */
    @FXML
    private Button vanpoolDashboardButton;

    /**
     * Button to load the Vehicle Dashboard view. (Placeholder for future implementation).
     */
    @FXML
    private Button vehicleDashboardButton;

    /**
     * Button to load the Routes Dashboard view.
     */
    @FXML
    private Button routesDashboardButton;

    /**
     * Label displaying a count or summary related to routes. (Placeholder for future implementation).
     */
    @FXML
    private Label routesLabel;

    /**
     * Button to load the Roster/Participants Dashboard view.
     */
    @FXML
    private Button rosterDashboardButton;

    /**
     * Label displaying the roster count (active participants / vehicle capacity).
     */
    @FXML
    private Label rosterLabel;

    /**
     * Button to load the Billing Dashboard view.
     */
    @FXML
    private Button billingDashboardButton;

    /**
     * Button to load the Transaction Log Dashboard view.
     */
    @FXML
    private Button transactionLogDashboardButton;

    /**
     * Label displaying a count or summary related to transactions. (Placeholder for future implementation).
     */
    @FXML
    private Label transactionLogLabel;

    /**
     * Button to load the Invoices Dashboard view.
     */
    @FXML
    private Button invoicesDashboardButton;

    /**
     * Label displaying a count or summary related to invoices. (Placeholder for future implementation).
     */
    @FXML
    private Label invoicesLabel;


    /**
     * Initializes the controller after its root element has been completely processed.
     * This method is automatically called by the FXMLLoader. It sets up initial UI states,
     * populates summary labels, and assigns event handlers to navigation buttons.
     */
    public void initialize() {
        logger.info("MainController initializing...");

        // Set the current date in the txtCurrentDate TextField
        txtCurrentDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));

        // Initialize header summary labels (optional, can be done by a dashboard view later)
        txtRecentTransaction.setText("N/A");
        txtBalance.setText("N/A");
        statusBarLabel.setText("Status: Ready");

        // Expand the Vanpool TitledPane by default
        mainAccordion.setExpandedPane(vanpoolTitledPane);

        // Update Counts for all filter labels
        updateAllCounts();

        // Set onAction Events for navigation buttons
        vanpoolDashboardButton.setOnAction(event -> {
            loadView("/com/avaruusstudios/vmdb/view/contentVanpoolDashboard.fxml"); // Placeholder FXML
        });

        routesDashboardButton.setOnAction(event -> {
            loadView("/com/avaruusstudios/vmdb/view/contentRoutes.fxml"); // Placeholder FXML
        });

        rosterDashboardButton.setOnAction(event -> {
            loadView("/com/avaruusstudios/vmdb/view/vanpool/participant/dashboardParticipants.fxml"); // Load Participants view
        });

        billingDashboardButton.setOnAction(event -> {
            loadView("/com/avaruusstudios/vmdb/view/contentBillingDashboard.fxml"); // Placeholder FXML
        });

        transactionLogDashboardButton.setOnAction(event -> {
            loadView("/com/avaruusstudios/vmdb/view/contentTransactions.fxml"); // Placeholder FXML
        });

        invoicesDashboardButton.setOnAction(event -> {
            loadView("/com/avaruusstudios/vmdb/view/contentInvoices.fxml"); // Placeholder FXML
        });

        logger.info("MainController initialization complete.");
    }

    /**
     * Updates all dynamic count labels displayed within the accordion's navigation section.
     * This method orchestrates calls to specific update methods for each count.
     */
    private void updateAllCounts() {
        logger.debug("Updating all dashboard counts.");
        // Routes Count (Placeholder for now)
        routesLabel.setText("N/A");

        // Roster Count: Active Participants / Max Active Vehicle Capacity
        updateRosterCount();

        // Transaction Log Count (Placeholder for now)
        transactionLogLabel.setText("N/A");

        // Invoices Count (Placeholder for now)
        invoicesLabel.setText("N/A");
    }

    /**
     * Updates the Roster button's label with the format "Active Participants / Vehicle Capacity".
     * This data is retrieved from the {@link DatabaseManager}. In case of a database error
     * or an issue loading the necessary SQL queries, the label will display "N/A" and an
     * error message will be logged and shown to the user via an alert.
     */
    private void updateRosterCount() {
        try {
            logger.debug("Attempting to retrieve roster count from DatabaseManager.");
            String rosterCount = DatabaseManager.getRosterCount();
            rosterLabel.setText(rosterCount);
            logger.info("Roster count updated to: {}", rosterCount);
            updateStatusBar("Roster count updated.");
        } catch (SQLException e) {
            logger.error("Database error updating roster count: {}", e.getMessage(), e);
            rosterLabel.setText("N/A");
            updateStatusBar("ERROR: Database error updating roster.");
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Load Roster Count",
                    "A database error occurred: " + e.getMessage() + "\nPlease check logs for details.");
            // TODO: Integrate EventLogger.log() here for critical DB error
        } catch (IllegalArgumentException e) {
            logger.error("Configuration error loading roster count query: {}", e.getMessage(), e);
            rosterLabel.setText("N/A");
            updateStatusBar("ERROR: Configuration error updating roster.");
            showAlert(Alert.AlertType.ERROR, "Configuration Error", "Failed to Load Roster Query",
                    "An internal configuration error occurred: " + e.getMessage() + "\nContact support.");
            // TODO: Integrate EventLogger.log() here for critical config error
        }
    }

    /**
     * Updates the text in the main application's status bar.
     * This method can be called by child controllers or other parts of the application
     * to provide real-time status feedback to the user.
     *
     * @param message The message {@link String} to display in the status bar.
     */
    public void updateStatusBar(String message) {
        if (statusBarLabel != null) {
            statusBarLabel.setText("Status: " + message);
            logger.debug("Status bar updated: {}", message);
        }
    }

    /**
     * Loads an FXML view into the {@link #contentStackPane}, replacing any existing content.
     * Each loaded FXML view is expected to have its own controller responsible for its data and logic.
     * The loaded view's controller will be passed a reference to this MainController if it
     * implements a specific interface (e.g., {@code ContentParticipantController}).
     *
     * @param fxmlPath The classpath path to the FXML file to load (e.g., "/com/avaruusstudios/vmdb/view/dashboardParticipants.fxml").
     */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath), "FXML resource not found: " + fxmlPath));
            Parent view = loader.load(); // Load the FXML

            // Get the controller of the loaded view
            Object controller = loader.getController();

            // Pass the MainController instance to the child controller if it needs it.
            // This is a common pattern for child controllers to communicate back to the parent.
            if (controller instanceof DashboardParticipantController) {
                ((DashboardParticipantController) controller).setMainController(this);
                logger.debug("Passed MainController to ContentParticipantController.");
            }
            // Add similar 'if' blocks for other specific content controllers as you create them:
            // else if (controller instanceof TransactionContentController) {
            //     ((TransactionContentController) controller).setMainController(this);
            //     logger.debug("Passed MainController to TransactionContentController.");
            // }

            contentStackPane.getChildren().clear(); // Clear existing content
            contentStackPane.getChildren().add(view); // Add the new view
            logger.info("Loaded view: {}", fxmlPath);
            updateStatusBar("Loaded view: " + fxmlPath); // Update status bar
        } catch (IOException e) {
            logger.error("Failed to load FXML view from {}: {}", fxmlPath, e.getMessage(), e);
            updateStatusBar("ERROR loading view: " + fxmlPath); // Update status bar on error
            showAlert(Alert.AlertType.ERROR, "UI Load Error", "Failed to Load View",
                    "Could not load the requested view: " + fxmlPath + "\nPlease check logs for details.");
        } catch (NullPointerException e) { // Catch NullPointerException specifically for Objects.requireNonNull
            logger.error("FXML resource not found for path {}: {}", fxmlPath, e.getMessage(), e);
            updateStatusBar("ERROR: Missing UI file for " + fxmlPath);
            showAlert(Alert.AlertType.ERROR, "UI Resource Missing", "View File Not Found",
                    "The application could not find a necessary UI file: " + fxmlPath + "\nPlease ensure all application resources are intact.");
        }
    }

    /**
     * Clears the current content from the {@link #contentStackPane}.
     * Optionally, displays a default placeholder message.
     */
    private void clearContentPane() {
        contentStackPane.getChildren().clear();
        // Optionally, display a default message or placeholder
        Label placeholder = new Label("Select a category from the left to view data.");
        placeholder.getStyleClass().add("placeholder-text"); // Apply styling if needed
        contentStackPane.getChildren().add(placeholder);
        logger.info("Content pane cleared.");
        updateStatusBar("Content pane cleared. Ready for selection.");
    }

    /**
     * Helper method to show an alert dialog to the user.
     * This method is purely for UI display and does not perform any logging itself.
     * Logging for the underlying event that triggered the alert should be done
     * by the calling method.
     *
     * @param type    The {@link Alert.AlertType} (e.g., INFORMATION, WARNING, ERROR).
     * @param title   The title text for the alert dialog window.
     * @param header  The header text displayed prominently within the alert dialog.
     * @param content The main content text of the alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        // Platform.runLater ensures that UI updates are done on the JavaFX Application Thread
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    // You can remove this line if 'isFullyInitialized' is not used elsewhere.
    // private boolean isFullyInitialized = false;
}