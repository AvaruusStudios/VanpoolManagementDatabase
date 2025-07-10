package com.avaruusstudios.vmdb.view.vanpool.participant;

import com.avaruusstudios.vmdb.model.Location;
import com.avaruusstudios.vmdb.model.Participant;
import com.avaruusstudios.vmdb.model.Program;
import com.avaruusstudios.vmdb.model.Role;

import com.avaruusstudios.vmdb.view.main.MainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Import for stream operations

public class ParticipantFormController {

    @FXML
    private GridPane mainGrid;
    @FXML
    private Label lblParticipantID;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtMiddleName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPhone;
    @FXML
    private ComboBox<Location> cmbPickUpLocation;
    @FXML
    private ComboBox<Location> cmbDropOffLocation;
    @FXML
    private TextField txtDistanceMiles;
    @FXML
    private DatePicker dpJoinDate;
    @FXML
    private ComboBox<Program> cmbProgram;
    @FXML
    private TextField txtBenefitAmount;
    @FXML
    private CheckBox chkIsActive;
    @FXML
    private TextArea txtNotes;
    @FXML
    private ComboBox<Role> cmbRole; // FXML ID for Role ComboBox
    @FXML
    private Button btnSave;
    @FXML
    private Button btnClear;
    @FXML
    private Label statusBarLabel; // FXML ID for the form's OWN status bar label

    private MainController mainController; // Still keeping this if you need other communication with MainController
    private Participant currentParticipant;

    private ObservableList<Location> allLocations = FXCollections.observableArrayList();

    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     * Sets up ComboBoxes, registers event handlers, and prepares the form for a new participant.
     */
    public void initialize() {
        System.out.println("ParticipantFormController initialized.");
        populateComboBoxes();
        clearFormData();

        // Register event handlers programmatically
        btnSave.setOnAction(event -> handleSave());
        btnClear.setOnAction(event -> handleClear());

        // Add focus listeners for status bar updates
        addFocusListeners();
        setStatusBarMessage("Form ready for participant entry.");
    }

    /**
     * Sets the MainController reference. This allows communication with the main application UI.
     * While this form now manages its own status bar, you might still need this for other interactions.
     * @param mainController The MainController instance.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        System.out.println("MainController set in ParticipantFormController.");
        // Removed status bar update for main controller here
    }

    /**
     * Updates the local status bar in the participant form.
     * This method now ONLY updates the statusBarLabel within this form.
     * @param message The message to display.
     */
    private void setStatusBarMessage(String message) {
        if (statusBarLabel != null) {
            statusBarLabel.setText(message);
        }
        // Removed mainController.updateStatusBar(message) from here
    }

    /**
     * Adds focus listeners to key input fields to provide status bar messages.
     */
    private void addFocusListeners() {
        txtFirstName.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Enter the participant's first name.");
        });
        txtMiddleName.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Enter the participant's middle name (optional).");
        });
        txtLastName.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Enter the participant's last name.");
        });
        txtEmail.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Enter the participant's email address.");
        });
        txtPhone.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Enter the participant's phone number.");
        });
        cmbPickUpLocation.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Select the participant's primary pickup location.");
        });
        cmbDropOffLocation.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Select the participant's primary drop-off location.");
        });
        txtDistanceMiles.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Enter the estimated distance in miles for their commute.");
        });
        dpJoinDate.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Select the date the participant joined the program.");
        });
        cmbProgram.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Select the program the participant belongs to.");
        });
        txtBenefitAmount.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Enter the monthly benefit amount for the participant.");
        });
        chkIsActive.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Check if the participant is currently active in the program.");
        });
        cmbRole.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Select the participant's role in the system.");
        });
        txtNotes.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Add any additional notes about the participant.");
        });
        btnSave.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Click to save participant data.");
        });
        btnClear.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) setStatusBarMessage("Click to clear the form for new entry.");
        });
    }

    /**
     * Populates the ComboBoxes with dummy data and filters roles to only display participant-applicable roles.
     * In a real application, this would involve fetching data from your database via a DAO.
     */
    private void populateComboBoxes() {
        // --- Dummy Location Data ---
        allLocations.addAll(
                new Location(1, "Main Office", "123 Main St, Suite 100", "Anytown", "CA", "90210", 12.345, -12.345, true, "Headquarters"),
                new Location(2, "North Depot", "456 Oak Ave, Warehouse A", "Northville", "CA", "90211", 13.456, -13.456, true, "North side depot"),
                new Location(3, "South Hub", "789 Pine Ln, Unit B", "South City", "CA", "90212", 14.567, -14.567, true, "South region hub")
        );
        cmbPickUpLocation.setItems(allLocations);
        cmbDropOffLocation.setItems(allLocations);

        // --- Populate Program ComboBox with enum values ---
        cmbProgram.setItems(FXCollections.observableArrayList(Program.values()));
        cmbProgram.getSelectionModel().select(Program.NONE);

        // --- Populate Role ComboBox with ONLY participant-applicable enum values ---
        ObservableList<Role> participantRoles = FXCollections.observableArrayList(
                // Filter Role.values() to include only those where isParticipantApplicable() is true
                java.util.Arrays.stream(Role.values())
                        .filter(Role::isParticipantApplicable)
                        .collect(Collectors.toList())
        );
        cmbRole.setItems(participantRoles);
        // Set a default selection for Role (e.g., PARTICIPANT)
        cmbRole.getSelectionModel().select(Role.PARTICIPANT);
    }

    /**
     * Loads an existing Participant's data into the form for editing.
     * @param participant The Participant object to load.
     */
    public void setParticipant(Participant participant) {
        this.currentParticipant = participant;
        if (participant.getParticipantID() != null) {
            lblParticipantID.setText(String.valueOf(participant.getParticipantID()));
        } else {
            lblParticipantID.setText("[New Participant]");
        }

        txtFirstName.setText(participant.getFirstName());
        txtMiddleName.setText(participant.getMiddleName());
        txtLastName.setText(participant.getLastName());
        txtEmail.setText(participant.getEmail());
        txtPhone.setText(participant.getPhone());

        cmbPickUpLocation.getSelectionModel().select(participant.getPickUpLocation());
        cmbDropOffLocation.getSelectionModel().select(participant.getDropOffLocation());

        txtDistanceMiles.setText(participant.getDistanceMiles() != null ? participant.getDistanceMiles().toPlainString() : "");
        dpJoinDate.setValue(participant.getJoinDate());
        cmbProgram.getSelectionModel().select(participant.getProgram());
        txtBenefitAmount.setText(participant.getBenefitAmount() != null ? participant.getBenefitAmount().toPlainString() : "");
        chkIsActive.setSelected(participant.isActive());
        txtNotes.setText(participant.getNotes());
        cmbRole.getSelectionModel().select(participant.getRole()); // Select the Role enum

        setStatusBarMessage("Participant data loaded for editing: " + participant.getFirstName() + " " + participant.getLastName());
    }

    /**
     * Clears all input fields and resets the form to create a new Participant.
     * This method is called internally by initialize() and handleClear().
     */
    private void clearFormData() {
        this.currentParticipant = null; // Mark as new participant
        lblParticipantID.setText("[Auto-Generated]");
        txtFirstName.clear();
        txtMiddleName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPhone.clear();
        cmbPickUpLocation.getSelectionModel().clearSelection();
        cmbDropOffLocation.getSelectionModel().clearSelection();
        txtDistanceMiles.clear();
        dpJoinDate.setValue(null);
        cmbProgram.getSelectionModel().select(Program.NONE); // Default to NONE
        txtBenefitAmount.clear();
        chkIsActive.setSelected(true); // Default to active for new
        txtNotes.clear();
        cmbRole.getSelectionModel().select(Role.PARTICIPANT); // Default to PARTICIPANT

        // Removed mainController.updateStatusBar calls
    }

    /**
     * Handles the clear button action. Clears the form and updates the status bar.
     */
    private void handleClear() {
        System.out.println("Clear button clicked.");
        clearFormData();
        setStatusBarMessage("Form cleared for new participant entry.");
    }

    /**
     * Handles the save action. Gathers data from the form, validates it,
     * creates or updates a Participant object, and provides feedback.
     */
    private void handleSave() {
        System.out.println("Save button clicked.");
        try {
            // --- Validation ---
            List<String> errors = new ArrayList<>();

            if (txtFirstName.getText().trim().isEmpty()) errors.add("First Name is required.");
            if (txtLastName.getText().trim().isEmpty()) errors.add("Last Name is required.");
            if (txtEmail.getText().trim().isEmpty()) errors.add("Email is required.");
            if (txtPhone.getText().trim().isEmpty()) errors.add("Phone is required.");
            if (cmbPickUpLocation.getSelectionModel().getSelectedItem() == null) errors.add("Pickup Location is required.");
            if (cmbDropOffLocation.getSelectionModel().getSelectedItem() == null) errors.add("Drop-off Location is required.");
            if (dpJoinDate.getValue() == null) errors.add("Join Date is required.");
            if (cmbProgram.getSelectionModel().getSelectedItem() == null || cmbProgram.getSelectionModel().getSelectedItem() == Program.NONE) {
                errors.add("Please select a valid Program.");
            }
            if (cmbRole.getSelectionModel().getSelectedItem() == null) {
                errors.add("Please select a valid Role."); // Role.GUEST is not an option here due to filtering
            }


            BigDecimal distanceMiles = null;
            try {
                String distanceText = txtDistanceMiles.getText().trim();
                if (distanceText.isEmpty()) {
                    errors.add("Distance Miles is required.");
                } else {
                    distanceMiles = new BigDecimal(distanceText);
                    if (distanceMiles.compareTo(BigDecimal.ZERO) < 0) {
                        errors.add("Distance Miles cannot be negative.");
                    }
                }
            } catch (NumberFormatException e) {
                errors.add("Invalid number format for Distance Miles.");
            }

            BigDecimal benefitAmount = null;
            try {
                String benefitText = txtBenefitAmount.getText().trim();
                if (benefitText.isEmpty()) {
                    errors.add("Benefit Amount is required.");
                } else {
                    benefitAmount = new BigDecimal(benefitText);
                    if (benefitAmount.compareTo(BigDecimal.ZERO) < 0) {
                        errors.add("Benefit Amount cannot be negative.");
                    }
                }
            } catch (NumberFormatException e) {
                errors.add("Invalid number format for Benefit Amount.");
            }

            if (!errors.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", String.join("\n", errors));
                setStatusBarMessage("Validation failed: " + errors.get(0));
                return;
            }

            // Get selected enum values and objects
            Location pickUpLocation = cmbPickUpLocation.getSelectionModel().getSelectedItem();
            Location dropOffLocation = cmbDropOffLocation.getSelectionModel().getSelectedItem();
            Program selectedProgram = cmbProgram.getSelectionModel().getSelectedItem();
            Role selectedRole = cmbRole.getSelectionModel().getSelectedItem();


            // Determine if creating new or updating existing
            if (currentParticipant == null) {
                currentParticipant = new Participant(
                        pickUpLocation,
                        dropOffLocation,
                        txtFirstName.getText().trim(),
                        txtMiddleName.getText().trim(),
                        txtLastName.getText().trim(),
                        txtEmail.getText().trim(),
                        txtPhone.getText().trim(),
                        distanceMiles,
                        dpJoinDate.getValue(),
                        chkIsActive.isSelected(),
                        selectedProgram,
                        benefitAmount,
                        selectedRole,
                        txtNotes.getText().trim()
                );
                showAlert(Alert.AlertType.INFORMATION, "Save Successful", "New Participant created!");
                setStatusBarMessage("New participant saved: " + currentParticipant.getFirstName() + " " + currentParticipant.getLastName());
            } else {
                currentParticipant.setPickUpLocation(pickUpLocation);
                currentParticipant.setDropOffLocation(dropOffLocation);
                currentParticipant.setFirstName(txtFirstName.getText().trim());
                currentParticipant.setMiddleName(txtMiddleName.getText().trim());
                currentParticipant.setLastName(txtLastName.getText().trim());
                currentParticipant.setEmail(txtEmail.getText().trim());
                currentParticipant.setPhone(txtPhone.getText().trim());
                currentParticipant.setDistanceMiles(distanceMiles);
                currentParticipant.setJoinDate(dpJoinDate.getValue());
                currentParticipant.setProgram(selectedProgram);
                currentParticipant.setBenefitAmount(benefitAmount);
                currentParticipant.setIsActive(chkIsActive.isSelected());
                currentParticipant.setNotes(txtNotes.getText().trim());
                currentParticipant.setRole(selectedRole);

                showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Participant updated!");
                setStatusBarMessage("Participant updated: " + currentParticipant.getFirstName() + " " + currentParticipant.getLastName());
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error Saving Participant", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            setStatusBarMessage("Error: " + e.getMessage());
        }
    }

    /**
     * Helper method to display an alert dialog.
     * @param alertType The type of alert (e.g., ERROR, INFORMATION).
     * @param title The title of the alert window.
     * @param message The message content of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}