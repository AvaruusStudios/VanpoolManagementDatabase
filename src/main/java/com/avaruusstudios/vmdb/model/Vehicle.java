package com.avaruusstudios.vmdb.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * <p>
 * Represents a vehicle managed within the Vanpool Management System.
 * This class encapsulates all essential metadata and operational details
 * pertaining to a vehicle, such as its specifications, lease information,
 * and active status.
 * </p>
 *
 * <p>
 * This object directly reflects the schema used to track individual vehicles,
 * which are typically assigned to a {@link Vanpool}.
 * </p>
 *
 * @see Vanpool // Added reference to Vanpool as per clarification
 */
public class Vehicle {
    /**
     * Unique identifier for the vehicle. This serves as the primary key
     * in the database for vehicle records.
     */
    private int vehicleID;

    /**
     * A unique identifying number or string for the vehicle, such as a license plate
     * or an internal unit number (e.g., "VAN-001").
     */
    private String vehicleNumber;

    /**
     * The manufacturer's name of the vehicle (e.g., "Ford", "Toyota", "Honda").
     */
    private String make;

    /**
     * The specific model of the vehicle (e.g., "Transit", "Sienna", "Odyssey").
     */
    private String model;

    /**
     * The four-digit year of manufacture for the vehicle (e.g., 2020).
     */
    private int year;

    /**
     * The maximum seating capacity of the vehicle, indicating the number of
     * passengers it can legally and safely accommodate.
     */
    private int capacity;

    /**
     * The {@link LocalDate} when the vehicle's lease or service period officially began.
     */
    private LocalDate leaseStartDate;

    /**
     * The {@link LocalDate} when the vehicle's lease or service period is scheduled to end.
     * This field can be {@code null} if the lease is indefinite or not yet determined.
     */
    private LocalDate leaseEndDate;

    /**
     * A boolean flag indicating whether the vehicle is currently active and operational
     * within the vanpool system. {@code true} if active; {@code false} if inactive, retired, or unavailable.
     */
    private boolean isActive;

    /**
     * The monetary subsidy amount associated with this vehicle. This could be
     * a government subsidy, a company contribution, or any other financial aid.
     */
    private double subsidyAmount;

    /**
     * Optional notes or administrative comments about the vehicle. This field can
     * contain information such as maintenance history, custom configurations,
     * or specific operational instructions.
     */
    private String notes;

    /**
     * Default constructor for creating an empty {@code Vehicle} object.
     * This constructor is useful for frameworks that instantiate objects via reflection
     * (e.g., Spring, JSON deserializers) before populating their fields.
     */
    public Vehicle() {}

    /**
     * Full constructor to initialize all fields of a {@code Vehicle} instance.
     * This constructor allows for the complete creation of a vehicle record
     * with all necessary details upon instantiation.
     *
     * @param vehicleID      The unique integer ID for the vehicle.
     * @param vehicleNumber  The unique alphanumeric identifier for the vehicle (e.g., license plate).
     * @param make           The manufacturer's brand name (e.g., "Ford").
     * @param model          The specific model name of the vehicle (e.g., "Transit").
     * @param year           The year of manufacture (e.g., 2021).
     * @param capacity       The maximum seating capacity for passengers.
     * @param leaseStartDate The {@link LocalDate} when the vehicle lease or service period began.
     * @param leaseEndDate   The {@link LocalDate} when the lease is scheduled to end (can be {@code null}).
     * @param isActive       A boolean indicating if the vehicle is currently active and in use.
     * @param subsidyAmount  The monetary subsidy associated with this vehicle.
     * @param notes          Optional notes or comments about the vehicle.
     */
    public Vehicle(int vehicleID, String vehicleNumber, String make, String model,
                   int year, int capacity, LocalDate leaseStartDate, LocalDate leaseEndDate,
                   boolean isActive, double subsidyAmount, String notes) {
        this.vehicleID = vehicleID;
        this.vehicleNumber = vehicleNumber;
        this.make = make;
        this.model = model;
        this.year = year;
        this.capacity = capacity;
        this.leaseStartDate = leaseStartDate;
        this.leaseEndDate = leaseEndDate;
        this.isActive = isActive;
        this.subsidyAmount = subsidyAmount;
        this.notes = notes;
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Retrieves the unique ID assigned to this vehicle.
     *
     * @return The integer primary key used to identify this vehicle record.
     */
    public int getVehicleID() { // Changed method name from getVehicleId()
        return vehicleID;
    }
    /**
     * Sets the unique identifier for this vehicle.
     * This method is typically used when populating a vehicle object from the database.
     *
     * @param vehicleID The unique integer ID for the vehicle.
     */
    public void setVehicleID(int vehicleID) { // Changed parameter name and method name from setVehicleId()
        this.vehicleID = vehicleID;
    }
    /**
     * Retrieves the unique vehicle number used for internal tracking or as a license plate.
     *
     * @return The alphanumeric string representing the vehicle's unique number.
     */
    public String getVehicleNumber() {
        return vehicleNumber;
    }
    /**
     * Sets the unique vehicle number that identifies it within the organization or as a license plate.
     *
     * @param vehicleNumber The alphanumeric tracking code for internal reference or license plate.
     */
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    /**
     * Retrieves the manufacturer's name of this vehicle.
     *
     * @return The string representing the vehicle's manufacturer brand name (e.g., "Ford").
     */
    public String getMake() {
        return make;
    }
    /**
     * Sets the make or brand of the vehicle.
     *
     * @param make The manufacturer's brand name (e.g., "Chevrolet").
     */
    public void setMake(String make) {
        this.make = make;
    }
    /**
     * Retrieves the specific model of the vehicle.
     *
     * @return The string representing the manufacturer's model name (e.g., "Transit").
     */
    public String getModel() {
        return model;
    }
    /**
     * Sets the specific model for this vehicle.
     *
     * @param model The manufacturer's model name.
     */
    public void setModel(String model) {
        this.model = model;
    }
    /**
     * Retrieves the manufacturing year of the vehicle.
     *
     * @return The four-digit year of production (e.g., 2021).
     */
    public int getYear() {
        return year;
    }
    /**
     * Sets the vehicle's year of manufacture.
     *
     * @param year The year the vehicle was built, used for age, compliance, or valuation.
     */
    public void setYear(int year) {
        this.year = year;
    }
    /**
     * Returns the maximum passenger seating capacity of this vehicle.
     *
     * @return The integer number of riders this vehicle can accommodate.
     */
    public int getCapacity() {
        return capacity;
    }
    /**
     * Sets the number of passengers the vehicle can hold.
     *
     * @param capacity The integer number of passenger seats available.
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    /**
     * Retrieves the {@link LocalDate} when the vehicle's lease or service period started.
     *
     * @return The {@link LocalDate} representing the start of the lease.
     */
    public LocalDate getLeaseStartDate() {
        return leaseStartDate;
    }
    /**
     * Sets the {@link LocalDate} for the vehicle's lease start date.
     *
     * @param leaseStartDate The {@link LocalDate} to set as the lease start date.
     */
    public void setLeaseStartDate(LocalDate leaseStartDate) {
        this.leaseStartDate = leaseStartDate;
    }
    /**
     * Retrieves the {@link LocalDate} when the vehicle's lease is scheduled to end.
     *
     * @return The {@link LocalDate} representing the scheduled lease termination date,
     * or {@code null} if the lease is indefinite or not yet set.
     */
    public LocalDate getLeaseEndDate() {
        return leaseEndDate;
    }
    /**
     * Sets the {@link LocalDate} for the vehicle's lease end date.
     *
     * @param leaseEndDate The {@link LocalDate} to set as the lease end date. Can be {@code null}.
     */
    public void setLeaseEndDate(LocalDate leaseEndDate) {
        this.leaseEndDate = leaseEndDate;
    }
    /**
     * Indicates whether the vehicle is currently available and in active use within the system.
     *
     * @return {@code true} if the vehicle is active; {@code false} if retired, undergoing maintenance, or unavailable.
     */
    public boolean isActive() {
        return isActive;
    }
    /**
     * Toggles or sets the vehicle's operational status.
     * This flag helps in filtering active inventory or deactivating vehicles from service.
     *
     * @param active {@code true} to mark the vehicle as active; {@code false} to deactivate it.
     */
    public void setActive(boolean active) {
        this.isActive = active;
    }
    /**
     * Retrieves the monetary subsidy amount associated with this vehicle.
     *
     * @return The subsidy amount as a double (e.g., 300.00).
     */
    public double getSubsidyAmount() {
        return subsidyAmount;
    }
    /**
     * Sets the monetary subsidy provided for this vehicle.
     *
     * @param subsidyAmount The monetary amount of the subsidy to set.
     */
    public void setSubsidyAmount(double subsidyAmount) {
        this.subsidyAmount = subsidyAmount;
    }
    /**
     * Retrieves any supplemental notes or comments recorded for the vehicle.
     *
     * @return A string containing notes, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes;
    }
    /**
     * Assigns additional notes or commentary to this vehicle record.
     *
     * @param notes The string containing additional comments or remarks about this vehicle. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    // ---------------------
    // Utilities
    // ---------------------

    /**
     * <p>
     * Returns a string representation of the {@code Vehicle} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the vehicle's key identifying attributes.
     * </p>
     * <p>
     * The format includes the vehicle ID, number, make, model, and year.
     * </p>
     *
     * @return A string in the format:
     * "Vehicle{ID=..., Number='...', Make='...', Model='...', Year=...}"
     */
    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleID=" + vehicleID +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                '}';
    }
    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based primarily on the unique {@code vehicleID}.
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
        Vehicle vehicle = (Vehicle) o; // Cast to Vehicle
        // Equality is based on the primary key (vehicleID)
        return vehicleID == vehicle.vehicleID;
    }
    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code vehicleID}, ensuring that
     * objects considered equal by {@link #equals(Object)} will have the same hash code.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(vehicleID); // Hash code based on the primary key
    }
}