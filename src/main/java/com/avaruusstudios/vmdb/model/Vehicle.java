package com.avaruusstudios.vmdb.model;

import java.math.BigDecimal;
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
 * @see Vanpool
 */
public class Vehicle {
    /**
     * Unique identifier for the vehicle. This serves as the primary key
     * in the database for vehicle records ({@code VehicleID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created vehicle not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable.
     * </p>
     */
    private final Integer vehicleID;

    /**
     * A unique identifying number or string for the vehicle, such as a license plate
     * or an internal unit number (e.g., "VAN-001"). This field is **required**.
     */
    private String vehicleNumber;

    /**
     * The manufacturer's name of the vehicle (e.g., "Ford", "Toyota", "Honda").
     * This field is **required**.
     */
    private String make;

    /**
     * The specific model of the vehicle (e.g., "Transit", "Sienna", "Odyssey").
     * This field is **required**.
     */
    private String model;

    /**
     * The four-digit year of manufacture for the vehicle (e.g., 2020).
     * This field is **required** and must be a valid past or current year.
     */
    private int year;

    /**
     * The maximum number of **active participants** the vehicle can support.
     * This field is **required** and must be positive.
     */
    private int capacity;

    /**
     * The {@link LocalDate} when the vehicle's lease or service period officially began.
     * This field is **required**.
     */
    private LocalDate leaseStartDate;

    /**
     * The {@link LocalDate} when the vehicle's lease or service period is scheduled to end.
     * This field can be {@code null} if the lease is indefinite or not yet determined.
     * If not {@code null}, it must be on or after the {@link #leaseStartDate}.
     */
    private LocalDate leaseEndDate;

    /**
     * A boolean flag indicating whether the vehicle is currently active and operational
     * within the vanpool system. {@code true} if active; {@code false} if inactive, retired, or unavailable.
     */
    private boolean isActive;

    /**
     * The recurring **monthly** discount amount applied to this vehicle, typically provided by a
     * government or external sponsor to reduce the overall cost for the vanpool.
     * This field uses {@link BigDecimal} for precision and must be non-negative.
     */
    private BigDecimal discount; // Renamed from monthlyDiscountAmount

    /**
     * Optional notes or administrative comments about the vehicle. This field can
     * contain information such as maintenance history, custom configurations,
     * or specific operational instructions.
     */
    private String notes;

    /**
     * Default constructor for creating a new, unpersisted {@code Vehicle} object.
     * The {@code vehicleID} is set to {@code null} to explicitly indicate that
     * this vehicle has not yet been assigned a unique ID by the database.
     * This constructor is useful for frameworks that instantiate objects via reflection
     * before populating their fields via setters.
     */
    public Vehicle() {
        this.vehicleID = null; // Explicitly null for unpersisted entity
    }

    /**
     * Full constructor to initialize all fields of a {@code Vehicle} instance.
     * This constructor is typically used when loading an *existing* vehicle
     * record from the database, where {@code vehicleID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param vehicleID             The unique integer ID for the vehicle. Must not be {@code null}.
     * @param vehicleNumber         The unique alphanumeric identifier for the vehicle (e.g., license plate). Must not be {@code null} or empty.
     * @param make                  The manufacturer's brand name (e.g., "Ford"). Must not be {@code null} or empty.
     * @param model                 The specific model name of the vehicle (e.g., "Transit"). Must not be {@code null} or empty.
     * @param year                  The year of manufacture (e.g., 2021). Must be a valid past or current year.
     * @param capacity              The maximum seating capacity for active participants. Must be positive.
     * @param leaseStartDate        The {@link LocalDate} when the vehicle lease or service period began. Must not be {@code null}.
     * @param leaseEndDate          The {@link LocalDate} when the lease is scheduled to end (can be {@code null}). If not null, must be on or after leaseStartDate.
     * @param isActive              A boolean indicating if the vehicle is currently active and in use.
     * @param discount              The recurring monthly discount amount associated with this vehicle. Must not be {@code null} and non-negative.
     * @param notes                 Optional notes or comments about the vehicle. Can be {@code null}.
     * @throws NullPointerException     if `vehicleID` or any other required object-type parameters (e.g., `vehicleNumber`, `make`, `model`, `leaseStartDate`, `discount`) are {@code null}.
     * @throws IllegalArgumentException if required string parameters are empty, `year` is invalid, `capacity` is not positive, `discount` is negative, or lease dates are inconsistent.
     * @throws IllegalStateException    if `leaseEndDate` is non-null but `leaseStartDate` is null when `setLeaseEndDate` is called.
     */
    public Vehicle(Integer vehicleID,
                   String vehicleNumber, String make, String model, int year, int capacity,
                   LocalDate leaseStartDate, LocalDate leaseEndDate, boolean isActive,
                   BigDecimal discount, String notes) { // Changed parameter name
        this.vehicleID = Objects.requireNonNull(vehicleID, "Vehicle ID cannot be null for an existing vehicle.");

        // Use setters for validation and consistency
        setVehicleNumber(vehicleNumber);
        setMake(make);
        setModel(model);
        setYear(year);
        setCapacity(capacity);
        setLeaseStartDate(leaseStartDate);
        setLeaseEndDate(leaseEndDate); // This setter will validate against leaseStartDate
        setActive(isActive);
        setDiscount(discount); // Changed setter call
        setNotes(notes);
    }

    /**
     * Convenience constructor for creating a new {@code Vehicle} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new vehicle record for **insertion** into the database.
     * The {@code vehicleID} is omitted as it is typically auto-generated by the database.
     * All parameters are validated via their respective setters.
     *
     * @param vehicleNumber         The unique alphanumeric identifier for the vehicle (e.g., license plate). Must not be {@code null} or empty.
     * @param make                  The manufacturer's brand name (e.g., "Ford"). Must not be {@code null} or empty.
     * @param model                 The specific model name of the vehicle (e.g., "Transit"). Must not be {@code null} or empty.
     * @param year                  The year of manufacture (e.g., 2021). Must be a valid past or current year.
     * @param capacity              The maximum seating capacity for active participants. Must be positive.
     * @param leaseStartDate        The {@link LocalDate} when the vehicle lease or service period began. Must not be {@code null}.
     * @param leaseEndDate          The {@link LocalDate} when the lease is scheduled to end (can be {@code null}). If not null, must be on or after leaseStartDate.
     * @param isActive              A boolean indicating if the vehicle is currently active and in use.
     * @param discount              The recurring monthly discount amount associated with this vehicle. Must not be {@code null} and non-negative.
     * @param notes                 Optional notes or comments about the vehicle. Can be {@code null}.
     * @throws NullPointerException     if any required object-type parameters (e.g., `vehicleNumber`, `make`, `model`, `leaseStartDate`, `discount`) are {@code null}.
     * @throws IllegalArgumentException if required string parameters are empty, `year` is invalid, `capacity` is not positive, `discount` is negative, or lease dates are inconsistent.
     * @throws IllegalStateException    if `leaseEndDate` is non-null but `leaseStartDate` is null when `setLeaseEndDate` is called.
     */
    public Vehicle(String vehicleNumber, String make, String model,
                   int year, int capacity, LocalDate leaseStartDate, LocalDate leaseEndDate,
                   boolean isActive, BigDecimal discount, String notes) { // Changed parameter name
        this.vehicleID = null; // New entity, ID will be assigned by DB
        // Use setters for validation and consistency
        setVehicleNumber(vehicleNumber);
        setMake(make);
        setModel(model);
        setYear(year);
        setCapacity(capacity);
        setLeaseStartDate(leaseStartDate);
        setLeaseEndDate(leaseEndDate); // This setter will validate against leaseStartDate
        setActive(isActive);
        setDiscount(discount); // Changed setter call
        setNotes(notes);
    }


    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Retrieves the unique ID assigned to this vehicle.
     * For new, unpersisted vehicles, this will be {@code null}.
     *
     * @return The {@link Integer} primary key used to identify this vehicle record, or {@code null} if not yet assigned.
     */
    public Integer getVehicleID() {
        return vehicleID;
    }
    // setVehicleID method is removed as vehicleID is now final and set only via constructors

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
     * @param vehicleNumber The alphanumeric tracking code for internal reference or license plate. Must not be {@code null} or empty.
     * @throws NullPointerException if {@code vehicleNumber} is {@code null}.
     * @throws IllegalArgumentException if {@code vehicleNumber} is empty after stripping whitespace.
     */
    public void setVehicleNumber(String vehicleNumber) {
        String trimmedNumber = Objects.requireNonNull(vehicleNumber, "Vehicle number cannot be null.").strip();
        if (trimmedNumber.isEmpty()) {
            throw new IllegalArgumentException("Vehicle number cannot be empty.");
        }
        this.vehicleNumber = trimmedNumber;
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
     * @param make The manufacturer's brand name (e.g., "Chevrolet"). Must not be {@code null} or empty.
     * @throws NullPointerException if {@code make} is {@code null}.
     * @throws IllegalArgumentException if {@code make} is empty after stripping whitespace.
     */
    public void setMake(String make) {
        String trimmedMake = Objects.requireNonNull(make, "Make cannot be null.").strip();
        if (trimmedMake.isEmpty()) {
            throw new IllegalArgumentException("Make cannot be empty.");
        }
        this.make = trimmedMake;
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
     * @param model The manufacturer's model name. Must not be {@code null} or empty.
     * @throws NullPointerException if {@code model} is {@code null}.
     * @throws IllegalArgumentException if {@code model} is empty after stripping whitespace.
     */
    public void setModel(String model) {
        String trimmedModel = Objects.requireNonNull(model, "Model cannot be null.").strip();
        if (trimmedModel.isEmpty()) {
            throw new IllegalArgumentException("Model cannot be empty.");
        }
        this.model = trimmedModel;
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
     * Must be a valid year (e.g., not in the future and within a reasonable historical range).
     * @throws IllegalArgumentException if the {@code year} is less than 1900 or greater than the current year.
     */
    public void setYear(int year) {
        int currentYear = LocalDate.now().getYear();
        if (year < 1900 || year > currentYear) { // Arbitrary lower bound, adjust as needed
            throw new IllegalArgumentException("Year must be between 1900 and " + currentYear + ".");
        }
        this.year = year;
    }
    /**
     * Returns the maximum number of active participants this vehicle can support.
     *
     * @return The integer number of active participants this vehicle can accommodate.
     */
    public int getCapacity() {
        return capacity;
    }
    /**
     * Sets the maximum number of active participants the vehicle can hold.
     *
     * @param capacity The integer number of active participant seats available. Must be a positive value.
     * @throws IllegalArgumentException if {@code capacity} is less than or equal to zero.
     */
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be a positive number.");
        }
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
     * @param leaseStartDate The {@link LocalDate} to set as the lease start date. Must not be {@code null}.
     * @throws NullPointerException if {@code leaseStartDate} is {@code null}.
     * @throws IllegalArgumentException if the {@code leaseEndDate} is already set and `leaseStartDate` is after it.
     */
    public void setLeaseStartDate(LocalDate leaseStartDate) {
        Objects.requireNonNull(leaseStartDate, "Lease start date cannot be null.");
        // If leaseEndDate is already set, validate consistency
        if (this.leaseEndDate != null && leaseStartDate.isAfter(this.leaseEndDate)) {
            throw new IllegalArgumentException("Lease start date cannot be after lease end date.");
        }
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
     * @throws IllegalArgumentException if {@code leaseEndDate} is not {@code null} and is before the {@link #leaseStartDate}.
     * @throws IllegalStateException    if `leaseEndDate` is non-null but `leaseStartDate` is null when this method is called.
     */
    public void setLeaseEndDate(LocalDate leaseEndDate) {
        if (leaseEndDate != null) {
            // Cannot validate end date without a start date. Force start date first or ensure order.
            if (this.leaseStartDate == null) {
                throw new IllegalStateException("Lease start date must be set before setting a non-null lease end date.");
            }
            if (leaseEndDate.isBefore(this.leaseStartDate)) {
                throw new IllegalArgumentException("Lease end date cannot be before lease start date.");
            }
        }
        this.leaseEndDate = leaseEndDate;
    }
    /**
     * Indicates whether the vehicle is currently available and in active use within the system.
     *
     * @return {@code true} if the vehicle is active; {@code false} if retired, undergoing maintenance, or unavailable.
     */
    public boolean isActive() { // Getter name changed to `isActive()` for boolean convention
        return isActive;
    }
    /**
     * Toggles or sets the vehicle's operational status.
     * This flag helps in filtering active inventory or deactivating vehicles from service.
     *
     * @param active {@code true} to mark the vehicle as active; {@code false} to deactivate it.
     */
    public void setActive(boolean active) { // Setter name remains `setActive()`
        this.isActive = active;
    }
    /**
     * Retrieves the recurring **monthly** discount amount associated with this vehicle.
     *
     * @return The monthly discount amount as a {@link BigDecimal}.
     */
    public BigDecimal getDiscount() { // Renamed getter
        return discount;
    }
    /**
     * Sets the recurring **monthly** discount amount provided for this vehicle.
     *
     * @param discount The monetary amount of the discount to set. Must not be {@code null} and must be non-negative.
     * @throws NullPointerException if {@code discount} is {@code null}.
     * @throws IllegalArgumentException if {@code discount} is negative.
     */
    public void setDiscount(BigDecimal discount) { // Renamed setter and changed parameter type
        this.discount = Objects.requireNonNull(discount, "Discount amount cannot be null.");
        if (this.discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount amount cannot be negative.");
        }
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
     * If the provided notes are not {@code null}, leading and trailing whitespace will be stripped.
     *
     * @param notes The string containing additional comments or remarks about this vehicle. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.strip() : null;
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
     * It correctly handles cases where {@code vehicleID} might be {@code null} for unpersisted entities.
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
        // Equality is based on the primary key (vehicleID), safely handling null Integer
        return Objects.equals(vehicleID, vehicle.vehicleID);
    }
    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code vehicleID}. If {@code vehicleID}
     * is {@code null} (for unpersisted entities), its hash code will be 0, as per {@link Objects#hash(Object...)}.
     * This ensures that objects considered equal by {@link #equals(Object)} will have the same hash code.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(vehicleID); // Hash code based on the primary key
    }
}