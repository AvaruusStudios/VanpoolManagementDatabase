package com.avaruusstudios.vmdb.views;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class MainController {
    @FXML
    private StackPane contentStackPane;
    @FXML
    private Accordion mainAccordion;
    @FXML
    private Label txtCurrentDate;
    @FXML
    private Label txtRecentTransaction;
    @FXML
    private Label txtBalance;
    @FXML
    private Label statusBarLabel;
    @FXML
    private TitledPane participantTitledPane;
    @FXML
    private TitledPane transactionTitledPane;
    @FXML
    private TitledPane locationTitledPane;
    @FXML
    private TitledPane vehicleTitledPane;
    @FXML
    private TitledPane invoiceTitledPane;
    @FXML
    private TitledPane categoryTitledPane;
    @FXML
    private Button createParticipant;
    @FXML
    private Button updateParticipant;
    @FXML
    private Button deleteParticipant;
    @FXML
    private Button createTransaction;
    @FXML
    private Button updateTransaction;
    @FXML
    private Button deleteTransaction;
    @FXML
    private Button createLocation;
    @FXML
    private Button updateLocation;
    @FXML
    private Button deleteLocation;
    @FXML
    private Button createVehicle;
    @FXML
    private Button updateVehicle;
    @FXML
    private Button deleteVehicle;
    @FXML
    private Button createInvoice;
    @FXML
    private Button updateInvoice;
    @FXML
    private Button deleteInvoice;
    @FXML
    private Button createCategory;
    @FXML
    private Button updateCategory;
    @FXML
    private Button deleteCategory;

    // The 'isFullyInitialized' flag is still useful for other potential deferred logic,
    // but the 'oldValue == null' check directly addresses initial listener firings.
    private boolean isFullyInitialized = false; // You can remove this line if it's not used elsewhere

    public void initialize() {
        // Set the current date in the txtCurrentDate TextField
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        txtCurrentDate.setText(currentDate.format(formatter));

        // Initialize header summary labels (optional, can be done by a dashboard view later)
        txtRecentTransaction.setText("N/A");
        txtBalance.setText("N/A");

        // Set onAction handlers for Participant Buttons
        createParticipant.setOnAction(event -> {
            System.out.println("Create Participant clicked - Implement Add Logic");
        });
        updateParticipant.setOnAction(event -> {
            System.out.println("Update Participant clicked - Implement Edit Logic");
        });
        deleteParticipant.setOnAction(event -> {
            System.out.println("Delete Participant clicked - Implement Delete Logic");
        });

        // Set onAction handlers for Transaction Buttons
        createTransaction.setOnAction(event -> {
            System.out.println("Create Transaction clicked - Implement Add Logic");
        });
        updateTransaction.setOnAction(event -> {
            System.out.println("Update Transaction clicked - Implement Edit Logic");
        });
        deleteTransaction.setOnAction(event -> {
            System.out.println("Delete Transaction clicked - Implement Delete Logic");
        });

        // Set onAction handlers for Location Buttons
        createLocation.setOnAction(event -> {
            System.out.println("Create Location clicked - Implement Add Logic");
        });
        updateLocation.setOnAction(event -> {
            System.out.println("Update Location clicked - Implement Edit Logic");
        });
        deleteLocation.setOnAction(event -> {
            System.out.println("Delete Location clicked - Implement Delete Logic");
        });

        // Set onAction handlers for Vehicle Buttons
        createVehicle.setOnAction(event -> {
            System.out.println("Create Vehicle clicked - Implement Add Logic");
        });
        updateVehicle.setOnAction(event -> {
            System.out.println("Update Vehicle clicked - Implement Edit Logic");
        });
        deleteVehicle.setOnAction(event -> {
            System.out.println("Delete Vehicle clicked - Implement Delete Logic");
        });

        // Set onAction handlers for Invoice Buttons
        createInvoice.setOnAction(event -> {
            System.out.println("Create Invoice clicked - Implement Add Logic");
        });
        updateInvoice.setOnAction(event -> {
            System.out.println("Update Invoice clicked - Implement Edit Logic");
        });
        deleteInvoice.setOnAction(event -> {
            System.out.println("Delete Invoice clicked - Implement Delete Logic");
        });

        // Set onAction handlers for Category Buttons
        createCategory.setOnAction(event -> {
            System.out.println("Create Category clicked - Implement Add Logic");
        });
        updateCategory.setOnAction(event -> {
            System.out.println("Update Category clicked - Implement Edit Logic");
        });
        deleteCategory.setOnAction(event -> {
            System.out.println("Delete Category clicked - Implement Delete Logic");
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
                clearContentPane();
            } else {
                // A pane is now expanded, load its data
                // Use newPane.getId() if you've set fx:id for your TitledPanes,
                // otherwise compare the TitledPane objects directly.
                System.out.println("Pane '" + newPane.getText() + "' expanded - Loading data.");

                // Use the new camelCase filenames
                if (newPane == participantTitledPane) {
                    loadView("/com/avaruusstudios/vmdb/views/ParticipantContent.fxml");
                } else if (newPane == transactionTitledPane) {
                    loadView("/com/avaruusstudios/vmdb/views/TransactionContent.fxml");
                } else if (newPane == locationTitledPane) {
                    loadView("/com/avaruusstudios/vmdb/views/LocationContent.fxml");
                } else if (newPane == vehicleTitledPane) {
                    loadView("/com/avaruusstudios/vmdb/views/VehicleContent.fxml");
                } else if (newPane == invoiceTitledPane) {
                    loadView("/com/avaruusstudios/vmdb/views/InvoiceContent.fxml");
                } else if (newPane == categoryTitledPane) {
                    loadView("/com/avaruusstudios/vmdb/views/CategoryContent.fxml");
                }
            }
        });

        // Set the flag to true AFTER the current UI cycle finishes.
        // This ensures all FXML loading, property settings, and initial listener firings have occurred.
        Platform.runLater(() -> {
            isFullyInitialized = true;
            System.out.println("Application fully initialized and ready for user interaction.");

            // Optional: If you want the table to be explicitly clear on startup (even if Accordion is null),
            // you can call clearContentPane() here *once* after initialization.
            if (mainAccordion.getExpandedPane() == null) {
                clearContentPane(); // NEW: Clear content pane initially
                System.out.println("Content pane cleared initially as no pane is expanded.");
            }
            // Optional: Load a default "Home" or "Dashboard" view on startup
            // loadViewIntoContentPane("/com/avaruusstudios/vmdb/views/DashboardContent.fxml");
        });
    }

    /**
     * Updates the text in the main application's status bar.
     * This method is called by child controllers to provide status feedback.
     * @param message The message to display in the status bar.
     */
    public void updateStatusBar(String message) {
        if (statusBarLabel != null) {
            statusBarLabel.setText("Status: " + message);
        }
    }

    /**
     * Loads an FXML view into the contentStackPane.
     * Each loaded FXML view is expected to have its own controller responsible for its data and logic.
     *
     * @param fxmlPath The path to the FXML file to load.
     */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load(); // Load the FXML

            // Get the controller of the loaded view
            Object controller = loader.getController();

            // Pass the MainController instance to the child controller if it needs it.
            // This is a common pattern for child controllers to communicate back to the parent.
            if (controller instanceof ParticipantContentController) {
                ((ParticipantContentController) controller).setMainController(this);
            }
            // Add similar 'if' blocks for other specific content controllers as you create them:
            // else if (controller instanceof TransactionContentController) {
            //     ((TransactionContentController) controller).setMainController(this);
            // }

            contentStackPane.getChildren().clear(); // Clear existing content
            contentStackPane.getChildren().add(view); // Add the new view
            System.out.println("Loaded view: " + fxmlPath);
            updateStatusBar("Loaded view: " + fxmlPath); // Update status bar
        } catch (IOException e) {
            System.err.println("Failed to load FXML view from " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            updateStatusBar("ERROR loading view: " + fxmlPath); // Update status bar on error
        }
    }

    /** Clears the current view from the contentStackPane */
    private void clearContentPane() {
        contentStackPane.getChildren().clear();
        // Optionally, display a default message or placeholder
        Label placeholder = new Label("Select a category from the left to view data.");
        placeholder.getStyleClass().add("placeholder-text"); // Apply styling if needed
        contentStackPane.getChildren().add(placeholder);
        updateStatusBar("Content pane cleared. Ready for selection.");
    }
}