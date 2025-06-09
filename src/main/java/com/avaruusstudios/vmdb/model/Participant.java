package com.avaruusstudios.vmdb.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * <p>
 * Represents an individual participant within the Vanpool Management System.
 * This class encapsulates all personal, contact, and participation details
 * for a commuter assigned to specific pickup and drop-off locations, and potentially a vehicle.
 * </p>
 *
 * <p>
 * It serves as a core model for managing individuals involved in the vanpool program,
 * including their enrollment, activity status, and associated program benefits.
 * </p>
 *
 * @see Location
 * @see Program
 */
public class Participant {
    /**
     * Unique identifier for the participant. This serves as the primary key
     * in the database for participant records.
     */
    private int participantID;

    /**
     * The {@link Location} object representing the designated pickup point for the participant
     * in the morning. This establishes a foreign key relationship to the Locations table.
     */
    private Location pickUpLocation;

    /**
     * The {@link Location} object representing the designated drop-off point for the participant
     * for work in the morning. This establishes a foreign key relationship to the Locations table.
     */
    private Location dropOffLocation;

    /**
     * The first name of the participant.
     * This field is typically required for identification.
     */
    private String firstName;

    /**
     * The middle name of the participant. This field is optional.
     */
    private String middleName;

    /**
     * The last name or surname of the participant.
     * This field is typically required for identification.
     */
    private String lastName;

    /**
     * The primary email address for contacting the participant.
     * Used for notifications, schedules, and general communication.
     */
    private String email;

    /**
     * The primary phone number for contacting the participant.
     * May include a mobile number or office line.
     */
    private String phone;

    /**
     * The calculated distance in miles from the {@link #pickUpLocation} to the {@link #dropOffLocation}.
     * This might be used for route planning or benefit calculations.
     */
    private double distanceMiles;

    /**
     * The {@link LocalDate} when the participant officially joined or was added to the vanpool system.
     */
    private LocalDate joinDate;

    /**
     * A boolean flag indicating whether the participant is currently active in the system.
     * {@code true} if active, {@code false} if inactive (e.g., suspended, terminated, or on leave).
     */
    private boolean isActive;

    /**
     * The {@link Program} enum representing the specific program the participant is associated with
     * (e.g., "Transportation Incentive Program (TIP)", "Daily").
     */
    private Program program; // Updated type to Program

    /**
     * The monetary benefit amount, if any, that the participant receives each month.
     * This value typically corresponds to the associated {@link #program}.
     */
    private double benefitAmount;

    /**
     * Optional notes about the participant. This field can contain administrative comments,
     * special preferences, medical considerations, or any other relevant annotations.
     */
    private String notes;


    /**
     * Default constructor for creating an empty {@code Participant} object.
     * This constructor is useful for frameworks that instantiate objects via reflection
     * (e.g., Spring, JSON deserializers) before populating their fields.
     */
    public Participant() {}

    /**
     * Full constructor to initialize all fields of a {@code Participant} instance.
     * This constructor allows for the complete creation of a participant record
     * with all necessary details upon instantiation.
     *
     * @param participantID     Unique system-generated identifier for the participant.
     * @param pickUpLocation    The {@link Location} object representing the participant's pickup point.
     * @param dropOffLocation   The {@link Location} object representing the participant's drop-off point.
     * @param firstName         The first name of the participant.
     * @param middleName        The middle name of the participant (can be null or empty).
     * @param lastName          The last name of the participant.
     * @param email             The contact email address of the participant.
     * @param phone             The contact phone number of the participant.
     * @param distanceMiles     The distance in miles between the pickup and drop-off locations.
     * @param joinDate          The {@link LocalDate} when the participant joined the program.
     * @param isActive          A boolean indicating the participant's active status.
     * @param program           The {@link Program} enum indicating the program the participant is in. // Updated type
     * @param benefitAmount     The monthly benefit amount the participant receives.
     * @param notes             Any optional notes or administrative comments about the participant.
     */
    public Participant(int participantID, Location pickUpLocation, Location dropOffLocation,
                       String firstName, String middleName, String lastName, String email,
                       String phone, double distanceMiles, LocalDate joinDate,
                       boolean isActive, Program program, double benefitAmount, String notes) { // Updated type
        this.participantID = participantID;
        this.pickUpLocation = pickUpLocation;
        this.dropOffLocation = dropOffLocation;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.distanceMiles = distanceMiles;
        this.joinDate = joinDate;
        this.isActive = isActive;
        this.program = program;
        this.benefitAmount = benefitAmount;
        this.notes = notes;
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Retrieves the unique identifier for this participant.
     *
     * @return The integer primary key used to identify this participant in the system.
     */
    public int getParticipantID() {
        return participantID;
    }

    /**
     * Sets the unique identifier for this participant.
     * This method is typically used when populating a participant object from the database.
     *
     * @param participantID The unique integer ID for the participant.
     */
    public void setParticipantID(int participantID) {
        this.participantID = participantID;
    }

    /**
     * Retrieves the {@link Location} object representing the participant's pickup point.
     *
     * @return The {@link Location} object, or {@code null} if not set.
     */
    public Location getPickUpLocation() {
        return pickUpLocation;
    }

    /**
     * Sets the {@link Location} object for the participant's pickup point.
     *
     * @param pickUpLocation The {@link Location} object to associate with this participant's pickup.
     */
    public void setPickUpLocation(Location pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    /**
     * Retrieves the {@link Location} object representing the participant's drop-off point.
     *
     * @return The {@link Location} object, or {@code null} if not set.
     */
    public Location getDropOffLocation() {
        return dropOffLocation;
    }

    /**
     * Sets the {@link Location} object for the participant's drop-off point.
     *
     * @param dropOffLocation The {@link Location} object to associate with this participant's drop-off.
     */
    public void setDropOffLocation(Location dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    /**
     * Retrieves the first name of the participant.
     *
     * @return The participant's given name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the participant.
     *
     * @param firstName The desired first name for the participant.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Retrieves the middle name of the participant.
     *
     * @return The participant's middle name, or {@code null} if not provided.
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the middle name of the participant.
     *
     * @param middleName The desired middle name for the participant. Can be {@code null}.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Retrieves the last name or surname of the participant.
     *
     * @return The participant's family name or surname.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name or surname of the participant.
     *
     * @param lastName The desired last name for the participant.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Retrieves the primary email address for the participant.
     *
     * @return The participant's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the primary email address for the participant.
     *
     * @param email The desired email address for communication.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the primary phone number for the participant.
     *
     * @return The participant's contact phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the primary phone number for the participant.
     *
     * @param phone The desired contact phone number.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Retrieves the calculated distance in miles between the participant's pickup and drop-off locations.
     *
     * @return The distance in miles as a double.
     */
    public double getDistanceMiles() {
        return distanceMiles;
    }

    /**
     * Sets the calculated distance in miles between the participant's pickup and drop-off locations.
     *
     * @param distanceMiles The distance in miles to set.
     */
    public void setDistanceMiles(double distanceMiles) {
        this.distanceMiles = distanceMiles;
    }

    /**
     * Retrieves the {@link LocalDate} when the participant joined the vanpool program.
     *
     * @return The {@link LocalDate} representing the join date.
     */
    public LocalDate getJoinDate() {
        return joinDate;
    }

    /**
     * Sets the {@link LocalDate} when the participant joined the vanpool program.
     *
     * @param joinDate The {@link LocalDate} to set as the join date.
     */
    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    /**
     * Checks if the participant is currently marked as active in the system.
     *
     * @return {@code true} if the participant is active; {@code false} otherwise.
     */
    public boolean getIsActive() {
        return isActive;
    }

    /**
     * Sets the active status of the participant.
     *
     * @param isActive {@code true} to mark the participant as active; {@code false} to mark as inactive.
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Retrieves the {@link Program} enum associated with the participant.
     *
     * @return The {@link Program} enum constant for the participant's program.
     */
    public Program getProgram() { // Updated return type
        return program;
    }

    /**
     * Sets the {@link Program} for the participant.
     *
     * @param program The {@link Program} enum constant to set. // Updated parameter type
     */
    public void setProgram(Program program) { // Updated parameter type
        this.program = program;
    }

    /**
     * Retrieves the monthly benefit amount the participant receives.
     *
     * @return The monetary benefit amount as a double.
     */
    public double getBenefitAmount() {
        return benefitAmount;
    }

    /**
     * Sets the monthly benefit amount the participant receives.
     *
     * @param benefitAmount The monetary benefit amount to set.
     */
    public void setBenefitAmount(double benefitAmount) {
        this.benefitAmount = benefitAmount;
    }

    /**
     * Retrieves any additional notes or remarks associated with this participant.
     *
     * @return A string containing notes, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets additional notes or administrative comments for this participant.
     *
     * @param notes The string containing notes to set. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    // ---------------------
    // Utilities
    // ---------------------

    /**
     * <p>
     * Returns a string representation of the {@code Participant} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the participant's key identifying attributes.
     * </p>
     * <p>
     * The format includes the participant ID, first name, last name, and email.
     * </p>
     *
     * @return A string in the format:
     * "Participant{ID=..., Name='...', Email='...'}"
     */
    @Override
    public String toString() {
        return "Participant{" +
                "participantID=" + participantID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based primarily on the unique {@code participantID}.
     * </p>
     * <p>
     * This method adheres to the general contract of the {@link Object#equals(Object)} method.
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Same object reference
        if (o == null || getClass() != o.getClass()) return false; // Null or different class
        Participant that = (Participant) o; // Cast to Participant
        // Equality is based on the primary key (participantID)
        return participantID == that.participantID;
    }
    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code participantID}, ensuring that
     * objects considered equal by {@link #equals(Object)} will have the same hash code.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(participantID); // Hash code based on the primary key
    }
}