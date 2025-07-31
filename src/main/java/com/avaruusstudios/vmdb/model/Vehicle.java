package com.avaruusstudios.vmdb.model;

import javafx.beans.property.*; // Essential JavaFX property classes for reactive data binding

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime; // Added for DeletedAt
import java.util.Objects;

/**
 * <p>
 * Represents a single vehicle within the Vanpool Management System.
 * This class captures essential operational details about a vehicle, including its identifying number,
 * physical attributes (make, model, manufactureYear, passengerCapacity), lease terms, active status, financial discount,
 * and soft deletion timestamp.
 * </p>
 *
 * <p>
 * Each vehicle is uniquely identified by its {@code VehicleID} ({@code VehicleID INTEGER PRIMARY KEY AUTOINCREMENT})
 * and {@code VehicleNumber} ({@code VehicleNumber TEXT NOT NULL}). Its properties are designed to support
 * data binding with JavaFX UI components, making it suitable for a responsive desktop application.
 * This class directly maps to the `Vehicles` table in the SQLite database as per the finalized schema.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.4
 * Created On: 2025-07-11
 * Updated On: 2025-07-12
 *
 * @see Transaction
 */
public class Vehicle {
    /**
     * The unique numerical identifier for the vehicle. This serves as the primary key
     * in the database for vehicle records ({@code VehicleID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created vehicle not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable.
     * </p>
     */
    private final ReadOnlyObjectProperty<Integer> vehicleID;
    /**
     * The unique identifier assigned by the rental agency or internal system for this specific vehicle.
     * This field is **required** (corresponds to {@code VehicleNumber TEXT NOT NULL} in the database).
     */
    private final StringProperty vehicleNumber;
    /**
     * The license plate of the vehicle.
     * This field is **required** (corresponds to {@code LicensePlate TEXT NOT NULL} in the database).
     */
    private final StringProperty licensePlate;
    /**
     * The make or manufacturer of the vehicle (e.g., "Toyota", "Ford").
     * This field is optional (corresponds to {@code Make TEXT} in the database).
     */
    private final StringProperty make;
    /**
     * The model of the vehicle (e.g., "Sienna", "Transit Connect").
     * This field is optional (corresponds to {@code Model TEXT} in the database).
     */
    private final StringProperty model;
    /**
     * The manufacturing manufactureYear of the vehicle.
     * This field is optional (corresponds to {@code ManufactureYear INTEGER} in the database).
     */
    private final IntegerProperty manufactureYear;
    /**
     * The maximum seating passengerCapacity of the vehicle, excluding the driver.
     * This field is **required** (corresponds to {@code PassengerCapacity INTEGER NOT NULL} in the database).
     */
    private final IntegerProperty passengerCapacity;
    /**
     * The start date of the vehicle's lease agreement.
     * This field is **required** (corresponds to {@code LeaseStartDate TEXT NOT NULL} in the database).
     */
    private final ObjectProperty<LocalDate> leaseStartDate;
    /**
     * The end date of the vehicle's lease agreement.
     * This field is optional (corresponds to {@code LeaseEndDate TEXT} in the database).
     */
    private final ObjectProperty<LocalDate> leaseEndDate;
    /**
     * A financial discount applied to this specific vehicle, represented as a monetary amount.
     * This field is **required** and defaults to 0 in the database schema. Stored as a {@link BigDecimal} for precision.
     * (corresponds to {@code Discount NUMERIC DEFAULT 0} in the database).
     */
    private final ObjectProperty<BigDecimal> discount;
    /**
     * A boolean flag indicating if the vehicle is currently active and in use within the vanpool.
     * This field is **required** (corresponds to {@code IsActive INTEGER NOT NULL DEFAULT 1} in the database, 0 for false, 1 for true).
     */
    private final BooleanProperty isActive;
    /**
     * The timestamp indicating when the vehicle record was logically deleted or deactivated.
     * This field is optional and can be {@code null} if the vehicle is active (corresponds to {@code DeletedAt TEXT DEFAULT NULL} in the database).
     * It is exposed as an {@link ObjectProperty} of {@link LocalDateTime}.
     */
    private final ObjectProperty<LocalDateTime> deletedAt;
    /**
     * Optional free-form text for additional notes or administrative comments specific to this vehicle.
     * (corresponds to {@code Notes TEXT} in the database).
     */
    private final StringProperty notes;

    /**
     * Default constructor for creating a new, unpersisted {@code Vehicle} object.
     * Initializes properties with default values (null for ID, empty strings, current date for lease start, etc.).
     * The {@code vehicleID} is set to {@code null} to explicitly indicate that
     * this vehicle has not yet been assigned a unique ID by the database.
     * Mandatory fields like {@code vehicleNumber}, {@code passengerCapacity}, {@code licensePlate}, {@code leaseStartDate}, {@code isActive}, and {@code discount}
     * are initialized to sensible defaults to ensure valid state as per schema.
     */
    public Vehicle() {
        // Updated constructor call to include licensePlate
        this(null, "", null, null, 0, 1, "UNKNOWN", LocalDate.now(), null, BigDecimal.ZERO, true, null, null);
    }

    /**
     * Full constructor to initialize all fields of a {@code Vehicle} instance.
     * This constructor is typically used when loading an *existing* vehicle
     * record from the database, where {@code vehicleID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param vehicleID     The unique integer ID for the vehicle, typically assigned by the database. Can be {@code null} for new vehicles.
     * @param vehicleNumber The unique vehicle identifier issued by the rental agency or internal system. Must not be null or empty.
     * @param make          The make or manufacturer of the vehicle. Can be null or empty.
     * @param model         The model of the vehicle. Can be null or empty.
     * @param manufactureYear The manufacturing year of the vehicle. Can be 0 or null if unknown, but typically validated for reasonable range.
     * @param passengerCapacity The seating capacity of the vehicle. Must be a positive integer.
     * @param licensePlate  The license plate of the vehicle. Must not be null or empty.
     * @param leaseStartDate The start date of the lease. Must not be null.
     * @param leaseEndDate  The end date of the lease. Can be null, but if present, must be after {@code leaseStartDate}.
     * @param discount      The discount amount for the vehicle. Must not be null and non-negative.
     * @param isActive      Boolean flag indicating active status.
     * @param deletedAt     The {@link LocalDateTime} timestamp when the vehicle was logically deleted, or {@code null} if active.
     * @param notes         Optional notes about the vehicle. Can be {@code null}.
     * @throws IllegalArgumentException if any mandatory field is invalid (e.g., null, empty, bad format, out of range).
     * @throws IllegalStateException    if `vehicleID` is attempted to be changed once set.
     */
    public Vehicle(Integer vehicleID, String vehicleNumber, String make, String model, Integer manufactureYear, int passengerCapacity,
                   String licensePlate, LocalDate leaseStartDate, LocalDate leaseEndDate, BigDecimal discount, boolean isActive,
                   LocalDateTime deletedAt, String notes) {
        this.vehicleID = new SimpleObjectProperty<>(this, "vehicleID", vehicleID);
        this.vehicleNumber = new SimpleStringProperty(this, "vehicleNumber");
        this.make = new SimpleStringProperty(this, "make");
        this.model = new SimpleStringProperty(this, "model");
        this.manufactureYear = new SimpleIntegerProperty(this, "manufactureYear");
        this.passengerCapacity = new SimpleIntegerProperty(this, "passengerCapacity");
        this.licensePlate = new SimpleStringProperty(this, "licensePlate"); // Initialize new property
        this.leaseStartDate = new SimpleObjectProperty<>(this, "leaseStartDate");
        this.leaseEndDate = new SimpleObjectProperty<>(this, "leaseEndDate");
        this.discount = new SimpleObjectProperty<>(this, "discount");
        this.isActive = new SimpleBooleanProperty(this, "isActive");
        this.deletedAt = new SimpleObjectProperty<>(this, "deletedAt");
        this.notes = new SimpleStringProperty(this, "notes");

        // Setters will apply validation
        setVehicleNumber(vehicleNumber);
        setMake(make);
        setModel(model);
        setManufactureYear(manufactureYear);
        setPassengerCapacity(passengerCapacity);
        setLicensePlate(licensePlate); // Set the new property
        setLeaseStartDate(leaseStartDate);
        setLeaseEndDate(leaseEndDate);
        setDiscount(discount);
        setIsActive(isActive);
        setDeletedAt(deletedAt);
        setNotes(notes);
    }

    /**
     * Convenience constructor for creating a new {@code Vehicle} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new vehicle record for **insertion** into the database.
     * The {@code vehicleID} is omitted as it is typically auto-generated by the database.
     * The {@code deletedAt} field is omitted as it is typically managed by database defaults or set upon persistence.
     * All parameters are validated via their respective setters.
     *
     * @param vehicleNumber The unique vehicle identifier issued by the rental agency or internal system. Must not be null or empty.
     * @param make          The make or manufacturer of the vehicle. Can be null or empty.
     * @param model         The model of the vehicle. Can be null or empty.
     * @param manufactureYear The manufacturing year of the vehicle. Can be 0 or null if unknown, but typically validated for reasonable range.
     * @param passengerCapacity The seating capacity of the vehicle. Must be a positive integer.
     * @param licensePlate  The license plate of the vehicle. Must not be null or empty.
     * @param leaseStartDate The start date of the lease. Must not be null.
     * @param leaseEndDate  The end date of the lease. Can be null, but if present, must be after {@code leaseStartDate}.
     * @param discount      The discount amount for the vehicle. Must not be null and non-negative.
     * @param isActive      Boolean flag indicating active status.
     * @param notes         Optional notes about the vehicle. Can be {@code null}.
     * @throws IllegalArgumentException if any mandatory field is invalid.
     */
    public Vehicle(String vehicleNumber, String make, String model, Integer manufactureYear, int passengerCapacity,
                   String licensePlate, LocalDate leaseStartDate, LocalDate leaseEndDate, BigDecimal discount, boolean isActive, String notes) {
        // Delegate to the full constructor with null for vehicleID and deletedAt for a new entity
        // Updated constructor call to include licensePlate
        this(null, vehicleNumber, make, model, manufactureYear, passengerCapacity, licensePlate, leaseStartDate, leaseEndDate, discount, isActive, null, notes);
    }

    // --- JavaFX Property Accessors ---

    /**
     * Retrieves the read-only property for the vehicle's unique ID.
     * This property represents the {@code VehicleID} column in the database.
     * <p>
     * As this property is {@code ReadOnlyObjectProperty}, its value cannot be
     * changed directly after initial assignment, enforcing the immutability
     * of the primary key for persisted entities.
     * </p>
     *
     * @return The {@link ReadOnlyObjectProperty} for {@code vehicleID}.
     */
    public ReadOnlyObjectProperty<Integer> vehicleIDProperty() {
        return vehicleID;
    }

    /**
     * Retrieves the {@link StringProperty} for the vehicle's unique identification number.
     * This property corresponds to the {@code VehicleNumber} column in the database.
     *
     * @return The {@link StringProperty} for {@code vehicleNumber}.
     */
    public StringProperty vehicleNumberProperty() {
        return vehicleNumber;
    }

    /**
     * Retrieves the {@link StringProperty} for the vehicle's license plate.
     * This property corresponds to the {@code LicensePlate} column in the database.
     *
     * @return The {@link StringProperty} for {@code licensePlate}.
     */
    public StringProperty licensePlateProperty() {
        return licensePlate;
    }

    /**
     * Retrieves the {@link StringProperty} for the vehicle's make or manufacturer.
     * This property corresponds to the {@code Make} column in the database.
     *
     * @return The {@link StringProperty} for {@code make}.
     */
    public StringProperty makeProperty() {
        return make;
    }

    /**
     * Retrieves the {@link StringProperty} for the vehicle's model.
     * This property corresponds to the {@code Model} column in the database.
     *
     * @return The {@link StringProperty} for {@code model}.
     */
    public StringProperty modelProperty() {
        return model;
    }

    /**
     * Retrieves the {@link IntegerProperty} for the vehicle's manufacturing manufactureYear.
     * This property corresponds to the {@code ManufactureYear} column in the database.
     *
     * @return The {@link IntegerProperty} for {@code manufactureYear}.
     */
    public IntegerProperty manufactureYearProperty() {
        return manufactureYear;
    }

    /**
     * Retrieves the {@link IntegerProperty} for the vehicle's seating passengerCapacity.
     * This property corresponds to the {@code PassengerCapacity} column in the database.
     *
     * @return The {@link IntegerProperty} for {@code passengerCapacity}.
     */
    public IntegerProperty passengerCapacityProperty() {
        return passengerCapacity;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the vehicle's lease start date.
     * This property holds a {@link LocalDate} value and corresponds to the {@code LeaseStartDate} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code leaseStartDate}.
     */
    public ObjectProperty<LocalDate> leaseStartDateProperty() {
        return leaseStartDate;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the vehicle's lease end date.
     * This property holds a {@link LocalDate} value and corresponds to the {@code LeaseEndDate} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code leaseEndDate}.
     */
    public ObjectProperty<LocalDate> leaseEndDateProperty() {
        return leaseEndDate;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the vehicle's discount amount.
     * This property holds a {@link BigDecimal} value and corresponds to the {@code Discount} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code discount}.
     */
    public ObjectProperty<BigDecimal> discountProperty() {
        return discount;
    }

    /**
     * Retrieves the {@link BooleanProperty} indicating if the vehicle is currently active.
     * This property corresponds to the {@code IsActive} column in the database.
     *
     * @return The {@link BooleanProperty} for {@code isActive}.
     */
    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the deletion timestamp of the vehicle.
     * This property corresponds to the {@code DeletedAt} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code deletedAt}.
     */
    public ObjectProperty<LocalDateTime> deletedAtProperty() {
        return deletedAt;
    }

    /**
     * Retrieves the {@link StringProperty} for any additional notes pertaining to the vehicle.
     * This property corresponds to the {@code Notes} column in the database.
     *
     * @return The {@link StringProperty} for {@code notes}.
     */
    public StringProperty notesProperty() {
        return notes;
    }

    // --- Value Getters and Setters ---

    /**
     * Retrieves the unique ID of the vehicle.
     * <p>
     * For new, unpersisted vehicles, this will be {@code null}.
     * Once assigned by the database, this ID should not be changed.
     * </p>
     *
     * @return The {@link Integer} primary key of the vehicle, or {@code null} if not yet persisted.
     */
    public Integer getVehicleID() {
        return vehicleID.get();
    }

    /**
     * Sets the unique ID for this vehicle. This method is primarily for use by data access objects (DAOs)
     * when an ID is generated by the database upon insertion.
     * <p>
     * It includes a check to prevent the ID from being modified once it has been set,
     * ensuring the logical immutability of the primary key.
     * </p>
     *
     * @param id The unique integer ID assigned by the database.
     * @throws IllegalStateException    if the ID has already been assigned to this object.
     * @throws IllegalArgumentException if the provided ID is {@code null} or non-positive.
     */
    public void _setVehicleID(Integer id) {
        if (this.vehicleID.get() != null) {
            throw new IllegalStateException("Vehicle ID cannot be changed once set.");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Vehicle ID cannot be null or non-positive.");
        }
        ((SimpleObjectProperty<Integer>) this.vehicleID).set(id);
    }

    /**
     * Retrieves the unique vehicle identification number issued by the rental agency or internal system.
     * Corresponds to the {@code VehicleNumber} column in the database.
     *
     * @return The vehicle number as a {@link String}. Will not be {@code null} or empty after trimming, as per schema.
     */
    public String getVehicleNumber() {
        return vehicleNumber.get();
    }

    /**
     * Sets the unique vehicle identification number.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is mandatory and cannot be set to {@code null} or an empty string.
     * </p>
     *
     * @param vehicleNumber The vehicle number to set.
     * @throws IllegalArgumentException if the provided vehicle number is {@code null} or empty after trimming.
     */
    public void setVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle number cannot be null or empty.");
        }
        this.vehicleNumber.set(vehicleNumber.trim());
    }

    /**
     * Retrieves the license plate of the vehicle.
     * Corresponds to the {@code LicensePlate} column in the database.
     *
     * @return The license plate as a {@link String}. Will not be {@code null} or empty after trimming.
     */
    public String getLicensePlate() {
        return licensePlate.get();
    }

    /**
     * Sets the license plate of the vehicle.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is mandatory and cannot be set to {@code null} or an empty string.
     * </p>
     *
     * @param licensePlate The license plate to set.
     * @throws IllegalArgumentException if the provided license plate is {@code null} or empty after trimming.
     */
    public void setLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be null or empty.");
        }
        this.licensePlate.set(licensePlate.trim());
    }

    /**
     * Retrieves the make or manufacturer of the vehicle.
     * Corresponds to the {@code Make} column in the database.
     *
     * @return The vehicle make as a {@link String}, or {@code null} if not set.
     */
    public String getMake() {
        return make.get();
    }

    /**
     * Sets the make or manufacturer of the vehicle.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is optional and can be set to {@code null} or an empty string (which will be converted to {@code null} after trimming).
     * </p>
     *
     * @param make The name of the make to set. Can be {@code null}.
     */
    public void setMake(String make) {
        this.make.set((make == null || make.trim().isEmpty()) ? null : make.trim());
    }

    /**
     * Retrieves the model of the vehicle.
     * Corresponds to the {@code Model} column in the database.
     *
     * @return The vehicle model as a {@link String}, or {@code null} if not set.
     */
    public String getModel() {
        return model.get();
    }

    /**
     * Sets the model of the vehicle.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is optional and can be set to {@code null} or an empty string (which will be converted to {@code null} after trimming).
     * </p>
     *
     * @param model The name of the model to set. Can be {@code null}.
     */
    public void setModel(String model) {
        this.model.set((model == null || model.trim().isEmpty()) ? null : model.trim());
    }

    /**
     * Retrieves the manufacturing manufactureYear of the vehicle.
     * Corresponds to the {@code ManufactureYear} column in the database.
     *
     * @return The manufacturing manufactureYear as an {@link Integer}, or {@code 0} if not set.
     */
    public Integer getManufactureYear() {
        return manufactureYear.get();
    }

    /**
     * Sets the manufacturing manufactureYear of the vehicle.
     * <p>
     * The input manufactureYear must be a positive value if specified. It cannot be before the year 2000 and cannot be in the future
     * relative to the current year (allowing current year + 1 for new models).
     * This field is optional. If setting to {@code null} or 0, it will be stored as 0.
     * </p>
     *
     * @param manufactureYear The manufacturing manufactureYear to set. Can be {@code null} or 0. If a positive value,
     * must not be before 2000 and not in the far future.
     * @throws IllegalArgumentException if the manufactureYear is before 2000 or in the future.
     */
    public void setManufactureYear(Integer manufactureYear) {
        if (manufactureYear != null && manufactureYear > 0) { // Only validate if a positive manufactureYear is provided
            final int MIN_ALLOWED_YEAR = 2000;
            int currentYear = LocalDate.now().getYear();

            if (manufactureYear < MIN_ALLOWED_YEAR) {
                throw new IllegalArgumentException("Vehicle manufactureYear cannot be before " + MIN_ALLOWED_YEAR + ".");
            }

            if (manufactureYear > currentYear + 1) { // Allowing next year for new models
                throw new IllegalArgumentException("Vehicle manufactureYear cannot be in the far future. Max allowed: " + (currentYear + 1));
            }
        }
        // If manufactureYear is null or <= 0, it will be stored as 0 (for IntegerProperty simplicity)
        this.manufactureYear.set(manufactureYear == null ? 0 : manufactureYear);
    }

    /**
     * Retrieves the maximum seating passengerCapacity of the vehicle.
     * Corresponds to the {@code PassengerCapacity} column in the database.
     *
     * @return The passengerCapacity as an {@code int}.
     */
    public int getPassengerCapacity() {
        return passengerCapacity.get();
    }

    /**
     * Sets the maximum seating passengerCapacity of the vehicle.
     * <p>
     * The passengerCapacity must be a positive integer (e.g., a vehicle must be able to seat at least one person beyond the driver).
     * This field is mandatory.
     * </p>
     *
     * @param passengerCapacity The seating passengerCapacity to set. Must be a positive value.
     * @throws IllegalArgumentException if the passengerCapacity is less than or equal to 0.
     */
    public void setPassengerCapacity(int passengerCapacity) {
        if (passengerCapacity <= 0) {
            throw new IllegalArgumentException("Vehicle passengerCapacity must be a positive value.");
        }
        this.passengerCapacity.set(passengerCapacity);
    }

    /**
     * Retrieves the start date of the vehicle's lease agreement.
     * Corresponds to the {@code LeaseStartDate} column in the database.
     *
     * @return The lease start date as a {@link LocalDate}.
     */
    public LocalDate getLeaseStartDate() {
        return leaseStartDate.get();
    }

    /**
     * Sets the start date of the vehicle's lease agreement.
     * <p>
     * This field is mandatory and must not be {@code null}. The lease start date
     * cannot be a date in the future, ensuring valid chronological data.
     * </p>
     *
     * @param leaseStartDate The lease start date to set. Must not be {@code null} and must not be a future date.
     * @throws IllegalArgumentException if the lease start date is {@code null} or in the future.
     */
    public void setLeaseStartDate(LocalDate leaseStartDate) {
        if (leaseStartDate == null) {
            throw new IllegalArgumentException("Vehicle lease start date cannot be null.");
        }
        if (leaseStartDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Vehicle lease start date cannot be in the future.");
        }
        this.leaseStartDate.set(leaseStartDate);
    }

    /**
     * Retrieves the end date of the vehicle's lease agreement.
     * Corresponds to the {@code LeaseEndDate} column in the database.
     *
     * @return The lease end date as a {@link LocalDate}, or {@code null} if not set.
     */
    public LocalDate getLeaseEndDate() {
        return leaseEndDate.get();
    }

    /**
     * Sets the end date of the vehicle's lease agreement.
     * <p>
     * This field is optional. If provided, the lease end date must be on or after the {@link #getLeaseStartDate()}.
     * This ensures the lease period is logically consistent.
     * </p>
     *
     * @param leaseEndDate The lease end date to set. Can be {@code null}.
     * @throws IllegalArgumentException if {@code leaseEndDate} is provided and is before the {@code leaseStartDate}.
     */
    public void setLeaseEndDate(LocalDate leaseEndDate) {
        if (leaseEndDate != null && getLeaseStartDate() != null && leaseEndDate.isBefore(getLeaseStartDate())) {
            throw new IllegalArgumentException("Vehicle lease end date cannot be before the lease start date.");
        }
        this.leaseEndDate.set(leaseEndDate);
    }

    /**
     * Retrieves the discount amount applied to this vehicle.
     * Corresponds to the {@code Discount} column in the database.
     *
     * @return The discount amount as a {@link BigDecimal}.
     */
    public BigDecimal getDiscount() {
        return discount.get();
    }

    /**
     * Sets the discount amount for the vehicle.
     * <p>
     * This field is mandatory and must not be {@code null}. The discount must also be
     * a non-negative value. Stored as {@link BigDecimal} for accurate financial calculations.
     * </p>
     *
     * @param discount The discount amount to set. Must not be {@code null} and must be non-negative.
     * @throws IllegalArgumentException if the discount amount is {@code null} or negative.
     */
    public void setDiscount(BigDecimal discount) {
        if (discount == null) {
            throw new IllegalArgumentException("Vehicle discount cannot be null.");
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Vehicle discount cannot be negative.");
        }
        this.discount.set(discount);
    }

    /**
     * Retrieves the active status of the vehicle.
     * Corresponds to the {@code IsActive} column in the database.
     *
     * @return {@code true} if the vehicle is currently active, {@code false} otherwise.
     */
    public boolean isActive() {
        return isActive.get();
    }

    /**
     * Sets the active status of the vehicle.
     * <p>
     * This boolean flag indicates whether the vehicle is currently operational and in use
     * within the vanpool system. This field is mandatory.
     * </p>
     *
     * @param active {@code true} to mark the vehicle as active, {@code false} to mark it as inactive.
     */
    public void setIsActive(boolean active) {
        this.isActive.set(active);
    }

    /**
     * Retrieves the timestamp when the vehicle record was logically deleted or deactivated.
     * Corresponds to the {@code DeletedAt} column in the database.
     *
     * @return The {@link LocalDateTime} of deletion, or {@code null} if the vehicle is active.
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt.get();
    }

    /**
     * Sets the timestamp when the vehicle record was logically deleted or deactivated.
     *
     * @param deletedAt The {@link LocalDateTime} to set as the deletion timestamp. Can be {@code null}.
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt.set(deletedAt);
    }

    /**
     * Retrieves any additional notes or administrative comments for the vehicle.
     * Corresponds to the {@code Notes} column in the database.
     *
     * @return The notes string, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes.get();
    }

    /**
     * Sets additional notes or administrative comments for the vehicle.
     * <p>
     * If the provided notes are not {@code null}, leading and trailing whitespace will be stripped.
     * </p>
     *
     * @param notes The string containing notes to set. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes.set((notes == null) ? null : notes.trim());
    }

    // --- Utility Methods ---

    /**
     * <p>
     * Returns a string representation of the {@code Vehicle} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the vehicle's key attributes.
     * </p>
     * <p>
     * The format includes the vehicle ID, vehicle number, make, model, manufactureYear,
     * passengerCapacity, licensePlate, lease dates, discount, active status, deletion timestamp, and notes.
     * </p>
     *
     * @return A string in the format:
     * "Vehicle{ID=..., VehicleNumber='...', Make='...', Model='...', ManufactureYear=..., PassengerCapacity=..., LicensePlate='...', LeaseStartDate=..., LeaseEndDate=..., Discount=..., IsActive=..., DeletedAt=..., Notes='...'}"
     */
    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleID=" + getVehicleID() +
                ", vehicleNumber='" + getVehicleNumber() + '\'' +
                ", make='" + getMake() + '\'' +
                ", model='" + getModel() + '\'' +
                ", manufactureYear=" + getManufactureYear() +
                ", passengerCapacity=" + getPassengerCapacity() +
                ", licensePlate='" + getLicensePlate() + '\'' + // Added licensePlate to toString
                ", leaseStartDate=" + getLeaseStartDate() +
                ", leaseEndDate=" + getLeaseEndDate() +
                ", discount=" + getDiscount() +
                ", isActive=" + isActive() +
                ", deletedAt=" + getDeletedAt() +
                ", notes='" + getNotes() + '\'' +
                '}';
    }

    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based primarily on the unique {@code vehicleID}.
     * If {@code vehicleID} is null for both, it falls back to comparing `vehicleNumber` and `licensePlate`
     * to identify unpersisted entities before database assignment.
     * </p>
     * <p>
     * This method adheres to the general contract of the {@link Object#equals(Object)} method,
     * ensuring consistency with hash-based collections.
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle that = (Vehicle) o;

        // If both vehicles have an ID, compare by ID
        if (getVehicleID() != null && that.getVehicleID() != null) {
            return Objects.equals(getVehicleID(), that.getVehicleID());
        }

        // If IDs are not present for both (i.e., new/unpersisted objects),
        // compare by unique business keys: vehicleNumber and licensePlate
        return Objects.equals(getVehicleNumber(), that.getVehicleNumber()) &&
                Objects.equals(getLicensePlate(), that.getLicensePlate());
    }

    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap} and {@link java.util.HashSet}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code vehicleID}. If {@code vehicleID}
     * is {@code null} (for unpersisted entities), it uses the hash of `vehicleNumber` and `licensePlate`
     * to maintain consistency with the {@code equals()} method.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return (getVehicleID() == null) ? Objects.hash(getVehicleNumber(), getLicensePlate()) : Objects.hash(getVehicleID());
    }
}