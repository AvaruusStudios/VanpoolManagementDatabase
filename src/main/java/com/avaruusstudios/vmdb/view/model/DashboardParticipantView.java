package com.avaruusstudios.vmdb.view.model;

import com.avaruusstudios.vmdb.model.Program; // Make sure this import is correct
import com.avaruusstudios.vmdb.view.ContentParticipantController;
import javafx.scene.control.TableColumn;

/**
 * <p>
 * Represents a data transfer object (DTO) or "View Model" specifically tailored
 * for displaying participant summary information on the Dashboard.
 * </p>
 *
 * <p>
 * This class does NOT directly map to the full `Participants` database table. Instead,
 * its properties correspond exactly to the columns returned by the `selectParticipantsDashboard.sql` query.
 * This ensures type-safety and separation of concerns, preventing the core `Participant` model
 * from being burdened with view-specific derived data.
 * </p>
 *
 * <p>
 * Instances of this class are immutable once created via the constructor, providing
 * a clean and reliable data source for JavaFX {@link javafx.scene.control.TableView} bindings.
 * </p>
 *
 * @see ContentParticipantController
 * @see com.avaruusstudios.vmdb.model.Participant
 */
public class DashboardParticipantView {
    /**
     * The unique identifier for the participant, aliased as 'ID' in the dashboard SQL query.
     * This corresponds to `Participants.ParticipantID`.
     */
    private final int id;
    /**
     * The concatenated display name for the participant, aliased as 'Participant'
     * in the dashboard SQL query. This includes last name, first name, and middle initial.
     * Example: "Doe, John A"
     */
    private final String participantDisplayName;
    /**
     * The {@link Program} enum value associated with the participant, directly
     * from the `Participants.Program` column in the database.
     */
    private final Program program;
    /**
     * A boolean indicating if the participant is currently active.
     * This directly maps from the `Participants.IsActive` column (0 or 1) in the database.
     */
    private final boolean active;

    /**
     * Constructs a new {@code DashboardParticipantView} instance.
     * This constructor is used to populate the view model from the results
     * of the `selectParticipantsDashboard.sql` query.
     *
     * @param id                     The participant's unique ID.
     * @param participantDisplayName The concatenated display name of the participant
     * (LastName, FirstName, Middle Initial).
     * @param program                The {@link Program} enum associated with the participant.
     * @param active                 A boolean indicating if the participant is active.
     */
    public DashboardParticipantView(int id, String participantDisplayName, Program program, boolean active) {
        this.id = id;
        this.participantDisplayName = participantDisplayName;
        this.program = program;
        this.active = active;
    }

    /**
     * Retrieves the participant's unique identifier.
     * This property is used for binding to a {@link TableColumn} in the dashboard UI.
     *
     * @return The participant's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the concatenated display name of the participant for the dashboard.
     * This property is used for binding to a {@link TableColumn}.
     *
     * @return The formatted participant name (e.g., "Doe, John A").
     */
    public String getParticipantDisplayName() {
        return participantDisplayName;
    }

    /**
     * Retrieves the {@link Program} enum value associated with the participant.
     * This property is used for binding to a {@link TableColumn} and can be
     * further processed to display its friendly name.
     *
     * @return The participant's {@link Program}.
     */
    public Program getProgram() {
        return program;
    }

    /**
     * Retrieves the active status of the participant.
     * This boolean property is used for binding to a {@link TableColumn}.
     *
     * @return {@code true} if the participant is active, {@code false} otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Provides a string representation of the {@code DashboardParticipantView} object,
     * useful for debugging and logging.
     *
     * @return A string containing the ID, display name, program, and active status.
     */
    @Override
    public String toString() {
        return "DashboardParticipantView{" +
                "id=" + id +
                ", participantDisplayName='" + participantDisplayName + '\'' +
                ", program=" + (program != null ? program.name() : "null") +
                ", active=" + active +
                '}';
    }
}