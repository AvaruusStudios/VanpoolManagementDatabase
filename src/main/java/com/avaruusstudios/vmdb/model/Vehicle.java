package com.avaruusstudios.vmdb.model;

import java.time.LocalDate;

/**
 * Represents a vehicle in the vanpool system.
 * Each vehicle may be assigned to invoices and participants.
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
     * Gets the VehicleID from the datasource
     *
     * @return the vehicle ID
     */
    public int getVehicleID() {
        return vehicleID;
    }
    /**
     * Sets the VehicleID in the datasource
     *
     * @param vehicleID sets the vehicle ID
     */
    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }
    /**
     * Gets the Vehicle Number from the datasource
     *
     * @return the vehicle number
     */
    public String getVehicleNumber() {
        return vehicleNumber;
    }
    /**
     * Sets the Vehicle Number in the datasource
     *
     * @param vehicleNumber sets the vehicle number
     */
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    /**
     * Gets the Vehicle Make from the datasource
     *
     * @return the vehicle make
     */
    public String getMake() {
        return make;
    }
    /**
     * Sets the Vehicle Make in the datasource
     *
     * @param make sets the vehicle make
     */
    public void setMake(String make) {
        this.make = make;
    }
    /**
     * Gets the Vehicle Model from the datasource
     *
     * @return the vehicle model
     */
    public String getModel() {
        return model;
    }
    /**
     * Sets the Vehicle Model in the datasource
     *
     * @param model sets the vehicle model
     */
    public void setModel(String model) {
        this.model = model;
    }
    /**
     * Gets the Vehicle Year from the datasource
     *
     * @return the manufacture year
     */
    public int getYear() {
        return year;
    }
    /**
     * Sets the Vehicle Year in the datasource
     *
     * @param year sets the manufacture year
     */
    public void setYear(int year) {
        this.year = year;
    }
    /**
     * Gets the Vehicle Seating Capacity from the datasource
     *
     * @return the seating capacity
     */
    public int getCapacity() {
        return capacity;
    }
    /**
     * Sets the Vehicle Seating Capacity in the datasource
     *
     * @param capacity sets the seating capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    /**
     * Gets the Vehicle Lease Start Date from the datasource
     *
     * @return the lease start date
     */
    public LocalDate getLeaseStartDate() {
        return leaseStartDate;
    }
    /**
     * Sets the Vehicle Lease Start Date in the datasource
     *
     * @param leaseStartDate sets the lease start date
     */
    public void setLeaseStartDate(LocalDate leaseStartDate) {
        this.leaseStartDate = leaseStartDate;
    }
    /**
     * Gets the Vehicle Lease End Date from the datasource
     *
     * @return the lease end date
     */
    public LocalDate getLeaseEndDate() {
        return leaseEndDate;
    }
    /**
     * Sets the Vehicle Lease End Date in the datasource
     *
     * @param leaseEndDate sets the lease end date
     */
    public void setLeaseEndDate(LocalDate leaseEndDate) {
        this.leaseEndDate = leaseEndDate;
    }
    /**
     * Gets the Vehicle State from the datasource.  This is a boolean value; 0 = Inactive, 1 = Active
     *
     * @return whether the vehicle is active
     */
    public boolean isActive() {
        return active;
    }
    /**
     * Sets the Vehicle State in the datasource.  This is a boolean value; 0 = Inactive, 1 = Active
     *
     * @param active sets whether the vehicle is active
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * Gets the Subsidy Amount for the Vehicle from the datasource
     *
     * @return the subsidy amount
     */
    public double getSubsidyAmount() {
        return subsidyAmount;
    }
    /**
     * Sets the Subsidy Amount for the Vehicle in the datasource
     *
     * @param subsidyAmount sets the subsidy amount
     */
    public void setSubsidyAmount(double subsidyAmount) {
        this.subsidyAmount = subsidyAmount;
    }
    /**
     * Gets the Notes for the Vehicle from the datasource
     *
     * @return any additional notes
     */
    public String getNotes() {
        return notes;
    }
    /**
     * Sets the Notes for the Vehicle in the datasource
     *
     * @param notes sets any additional notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
