package com.avaruusstudios.vmdb.model;

import java.time.LocalDate;

/**
 * Represents an individual participant in the vanpool system.
 * A participant is typically a commuter assigned to a specific vehicle and location,
 * and contains personal, contact, and participation details.
 */
public class Participant {
    /** Unique identifier for the participant (Primary Key) */
    private int participantID;
    /** First name of the participant */
    private String firstName;
    /** Middle name of the Participant */
    private String middleName;
    /** Last name or surname of the participant */
    private String lastName;
    /** Email address for contacting the participant */
    private String email;
    /** Phone number associated with the participant (may include mobile or office) */
    private String phone;
    /** ID of the vehicle the participant is assigned to (foreign key) */
    private int pickUpLocation_FK;
    /** ID of the location the participant is associated with (foreign key) */
    private int dropOffLocation_FK;
    /** Distance in miles from PickUpLocation_FK to DropOffLocation_FK */
    private double distance;
    /** Date when the participant was added to the system */
    private LocalDate joinDate;
    /** Indicates whether the participant is currently active in the system */
    private boolean active;
    /** Optional notes for administrative comments, preferences, or warnings */
    private String notes;


    /**
     * Default constructor for a blank participant object.
     * Typically used when populating data via deserialization or UI binding.
     */
    public Participant() {}

    /**
     * Full constructor to initialize all fields of the Participant object.
     *
     * @param participantID         Unique system-generated ID
     * @param firstName             Participant's first name
     * @param middleName            Participant's middle name
     * @param lastName              Participant's last name
     * @param email                 Contact email
     * @param phone                 Contact phone number
     * @param PickUpLocation_FK     Associated location ID
     * @param DropOffLocation_FK    Associated location ID
     * @param distance              Distance in miles from PickUpLocation_FK to DropOffLocation_FK
     * @param joinDate              Date of enrollment in the vanpool program
     * @param active                True if actively commuting
     * @param notes                 Freeform notes or annotations
     */
    public Participant(int participantID, String firstName, String middleName, String lastName, String email,
                       String phone, int PickUpLocation_FK, int DropOffLocation_FK, double distance, LocalDate joinDate,
                       boolean active, String notes) {
        this.participantID = participantID;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.pickUpLocation_FK = PickUpLocation_FK;
        this.dropOffLocation_FK = DropOffLocation_FK;
        this.distance = distance;
        this.joinDate = joinDate;
        this.active = active;
        this.notes = notes;
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Gets the participant's system-assigned ID.
     *
     * @return participantID Integer primary key for participant records.
     */
    public int getParticipantID() {
        return participantID;
    }
    /**
     * Sets the unique identifier for this participant.
     *
     * @param participantID Integer ID, typically managed by the database.
     */
    public void setParticipantID(int participantID) {
        this.participantID = participantID;
    }
    /**
     * Gets the first name of the participant.
     *
     * @return firstName Given name or preferred first name.
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * Sets the participant's first name.
     *
     * @param firstName Participant's given name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * Gets the middle name of the participant.
     *
     * @return middleName Given name or preferred first name.
     */
    public String getMiddleName() {
        return middleName;
    }
    /**
     * Sets the participant's middle name.
     *
     * @param middleName Participant's given name.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    /**
     * Retrieves the last name or surname of the participant.
     *
     * @return lastName Legal or preferred last name.
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * Sets the last name or surname of the participant.
     *
     * @param lastName Family or surname of the participant.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * Retrieves the email address associated with the participant.
     *
     * @return email Email used for communication or alerts.
     */
    public String getEmail() {
        return email;
    }
    /**
     * Sets the email address for this participant.
     *
     * @param email Contact email (e.g., user@example.com).
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Retrieves the contact phone number for the participant.
     *
     * @return phone Participant's mobile or preferred contact number.
     */
    public String getPhone() {
        return phone;
    }
    /**
     * Sets the participant's phone number.
     *
     * @param phone Contact number, may include mobile or landline.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    /**
     * Gets the ID of the vehicle assigned to this participant.
     *
     * @return pickUpLocation_FK Foreign key referencing a Vehicle object.
     */
    public int getPickUpLocation_FK() {
        return pickUpLocation_FK;
    }
    /**
     * Sets the ID of the vehicle associated with this participant.
     *
     * @param PickUpLocation_FK Vehicle reference ID for routing and matching.
     */
    public void setPickUpLocation_FK(int PickUpLocation_FK) {
        this.pickUpLocation_FK = PickUpLocation_FK;
    }
    /**
     * Retrieves the location ID where this participant is based.
     *
     * @return dropOffLocation_FK Foreign key to a Location entity.
     */
    public int getDropOffLocation_FK() {
        return dropOffLocation_FK;
    }
    /**
     * Sets the participant's associated location ID.
     *
     * @param dropOffLocation_FK ID of pickup/drop-off location.
     */
    public void setDropOffLocation_FK(int dropOffLocation_FK) {
        this.dropOffLocation_FK = dropOffLocation_FK;
    }
    /**
     * Gets the distance in miles from PickUpLocation_FK to DropOffLocation_FK.
     *
     * @return joinDate LocalDate representing the participant’s enrollment date.
     */
    public double getDistance() {
        return distance;
    }
    /**
     * Sets the distance in miles from PickUpLocation_FK to DropOffLocation_FK.
     *
     * @param distance distance in miles from PickUpLocation_FK to DropOffLocation_FK.
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }
    /**
     * Gets the date when this participant joined the program.
     *
     * @return joinDate LocalDate representing the participant’s enrollment date.
     */
    public LocalDate getJoinDate() {
        return joinDate;
    }
    /**
     * Sets the date the participant was added to the system.
     *
     * @param joinDate ISO date of joining (e.g., 2022-10-15).
     */
    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }
    /**
     * Checks if the participant is currently marked as active.
     *
     * @return true if participant is active; false otherwise.
     */
    public boolean isActive() {
        return active;
    }
    /**
     * Updates the participant's active status.
     * Can be toggled based on enrollment, suspension, or termination.
     *
     * @param active Set to true to activate; false to deactivate.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * Retrieves any notes or remarks associated with this participant.
     * May include behavioral comments, schedule preferences, or restrictions.
     *
     * @return notes Optional freeform text; may be null or empty.
     */
    public String getNotes() {
        return notes;
    }
    /**
     * Sets additional notes or internal comments for this participant.
     *
     * @param notes Administrative comments or rider details.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
