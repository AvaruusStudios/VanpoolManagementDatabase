package com.avaruusstudios.vmdb.model;

import java.time.LocalDate;

/**
 * Represents a vehicle used in the Vanpool Management System.
 * A vehicle contains metadata such as make, model, capacity, lease period, and subsidy details.
 * This object reflects the schema used to track active/inactive vanpool vehicles.
 */
public class Vehicle {
    /** Unique identifier for the vehicle */
    private int vehicleID;
    /** Vehicle number, e.g., license plate or unit number */
    private String vehicleNumber;
    /** Vehicle make (e.g., Ford, Toyota) */
    private String make;
    /** Vehicle model (e.g., Transit, Sienna) */
    private String model;
    /** Year of manufacture */
    private int year;
    /** Seating capacity of the vehicle */
    private int capacity;
    /** Lease start date in ISO format (yyyy-MM-dd HH:MM) */
    private LocalDate leaseStartDate;
    /** Lease end date, nullable */
    private LocalDate leaseEndDate;
    /** Indicates if vehicle is active (1=true, 0=false) */
    private boolean active;
    /** Subsidy amount associated with the vehicle */
    private double subsidyAmount;
    /** Notes or comments about the vehicle */
    private String notes;

    /**
     * Default constructor
     */
    public Vehicle() {}
    /**
     * Full constructor to initialize all fields.
     *
     * @param vehicleID      the unique vehicle ID
     * @param vehicleNumber  the identifying vehicle number
     * @param make           the make of the vehicle
     * @param model          the model of the vehicle
     * @param year           the manufacture year
     * @param capacity       the number of seats
     * @param leaseStartDate lease starting date
     * @param leaseEndDate   lease ending date
     * @param active         whether the vehicle is active
     * @param subsidyAmount  subsidy amount associated
     * @param notes          any extra information
     */
    public Vehicle(int vehicleID, String vehicleNumber, String make, String model,
                   int year, int capacity, LocalDate leaseStartDate, LocalDate leaseEndDate,
                   boolean active, double subsidyAmount, String notes) {
        this.vehicleID = vehicleID;
        this.vehicleNumber = vehicleNumber;
        this.make = make;
        this.model = model;
        this.year = year;
        this.capacity = capacity;
        this.leaseStartDate = leaseStartDate;
        this.leaseEndDate = leaseEndDate;
        this.active = active;
        this.subsidyAmount = subsidyAmount;
        this.notes = notes;
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Gets the unique ID assigned to this vehicle.
     *
     * @return vehicleID A numeric primary key used to identify this record.
     */
    public int getVehicleId() {
        return vehicleID;
    }
    /**
     * Sets the unique vehicle ID.
     * This method is typically used when initializing a vehicle object from a persisted source.
     *
     * @param vehicleId Numeric primary key from the database.
     */
    public void setVehicleId(int vehicleId) {
        this.vehicleID = vehicleId;
    }
    /**
     * Retrieves the vehicle number used for internal tracking.
     * This is often a human-readable alphanumeric ID assigned during fleet onboarding.
     *
     * @return vehicleNumber Fleet tracking number such as "VAN-1024".
     */
    public String getVehicleNumber() {
        return vehicleNumber;
    }
    /**
     * Sets the vehicle number that uniquely identifies it within the organization.
     *
     * @param vehicleNumber Alphanumeric tracking code for internal reference.
     */
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    /**
     * Gets the manufacturer name of this vehicle.
     * Common examples include Ford, Toyota, Honda.
     *
     * @return make Vehicle's manufacturer brand name.
     */
    public String getMake() {
        return make;
    }
    /**
     * Sets the make or brand of the vehicle.
     * Used to distinguish between manufacturers in reports or filters.
     *
     * @param make Brand name such as "Ford" or "Chevrolet".
     */
    public void setMake(String make) {
        this.make = make;
    }
    /**
     * Gets the specific model of the vehicle (e.g., Transit, Odyssey).
     *
     * @return model Model name assigned by the manufacturer.
     */
    public String getModel() {
        return model;
    }
    /**
     * Sets the specific model for this vehicle.
     * Helps distinguish between different configurations or types in the same make.
     *
     * @param model Manufacturer model name.
     */
    public void setModel(String model) {
        this.model = model;
    }
    /**
     * Retrieves the manufacturing year of the vehicle.
     *
     * @return year 4-digit year of production (e.g., 2021).
     */
    public int getYear() {
        return year;
    }
    /**
     * Sets the vehicle's year of manufacture.
     *
     * @param year Year vehicle was built, used for age, compliance, or valuation.
     */
    public void setYear(int year) {
        this.year = year;
    }
    /**
     * Returns the maximum passenger capacity of this vehicle.
     *
     * @return capacity Number of riders this vehicle can accommodate.
     */
    public int getCapacity() {
        return capacity;
    }
    /**
     * Defines the number of passengers the vehicle can hold.
     * May be used for route planning and eligibility validation.
     *
     * @param capacity Number of passenger seats available.
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    /**
     * Retrieves the date the vehicle lease started.
     *
     * @return leaseStartDate Start of lease in ISO format (YYYY-MM-DD).
     */
    public LocalDate getLeaseStartDate() {
        return leaseStartDate;
    }
    /**
     * Sets the lease start date for this vehicle.
     * Indicates when the leasing agreement or service period began.
     *
     * @param leaseStartDate ISO 8601-formatted start date.
     */
    public void setLeaseStartDate(LocalDate leaseStartDate) {
        this.leaseStartDate = leaseStartDate;
    }
    /**
     * Retrieves the date the lease ends, if one is set.
     *
     * @return leaseEndDate The scheduled lease termination date or null if indefinite.
     */
    public LocalDate getLeaseEndDate() {
        return leaseEndDate;
    }
    /**
     * Sets the lease end date for this vehicle.
     * Can be null if the lease is open-ended or not yet terminated.
     *
     * @param leaseEndDate ISO-formatted end date or null.
     */
    public void setLeaseEndDate(LocalDate leaseEndDate) {
        this.leaseEndDate = leaseEndDate;
    }
    /**
     * Indicates whether the vehicle is currently available and in use.
     *
     * @return true if the vehicle is active; false if retired or unavailable.
     */
    public boolean isActive() {
        return active;
    }
    /**
     * Toggles the vehicle's operational status.
     * This flag helps in filtering active inventory or deactivating vehicles.
     *
     * @param active Set to true for active vehicles; false to deactivate.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * Gets the subsidy amount tied to this vehicle.
     * This may be used in cost calculations, reports, or eligibility audits.
     *
     * @return subsidyAmount Dollar amount of external or internal funding.
     */
    public double getSubsidyAmount() {
        return subsidyAmount;
    }
    /**
     * Sets the monetary subsidy provided for this vehicle.
     * May be used to offset leasing costs or participant dues.
     *
     * @param subsidyAmount Amount in USD (e.g., 300.00).
     */
    public void setSubsidyAmount(double subsidyAmount) {
        this.subsidyAmount = subsidyAmount;
    }
    /**
     * Retrieves any supplemental notes or comments for the vehicle.
     * Could include maintenance details, custom configurations, or usage instructions.
     *
     * @return notes Optional string of notes; may be null or empty.
     */
    public String getNotes() {
        return notes;
    }
    /**
     * Assigns additional notes or commentary to this vehicle record.
     * This can be used for informal remarks, scheduling quirks, or internal memos.
     *
     * @param notes Additional comments or remarks about this vehicle.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
