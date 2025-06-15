package com.avaruusstudios.vmdb.model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * <p>
 * Represents a financial invoice generated within the Vanpool Management System.
 * Each invoice details billing information for a specific period and is associated with a particular vehicle.
 * </p>
 *
 * <p>
 * A singular invoice contains metadata such as its unique identifier, the date it was prepared,
 * its due date, the billing period it covers, the associated {@link Vehicle} object,
 * and any relevant notes. This object structure directly reflects the schema used for the
 * {@code Invoices} table in the SQLite database to track billing records, where
 * {@code VehicleID_FK} is now represented by an embedded {@code Vehicle} object.
 * </p>
 *
 * @see Vehicle
 * @see InvoiceItem
 * @see Transaction
 */
public class Invoice {
    /**
     * Unique identifier for the invoice. This serves as the primary key
     * in the database for invoice records ({@code InvoiceID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created invoice not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable.
     * </p>
     */
    private final Integer invoiceID; // Changed from int to final Integer

    /**
     * The {@link LocalDate} when the invoice was officially prepared or generated.
     * This field is **required** (corresponds to {@code InvoiceDate TEXT NOT NULL} in the database).
     */
    private LocalDate invoiceDate;

    /**
     * The {@link LocalDate} by which the invoice payment is expected.
     * This field is **required** (corresponds to {@code DueDate TEXT NOT NULL} in the database).
     */
    private LocalDate dueDate;

    /**
     * The billing period of the invoice, represented as a {@link YearMonth} object.
     * This indicates the month and year for which the charges apply (e.g., "JUN-2025").
     * This field is **required** (corresponds to {@code PeriodLabel TEXT NOT NULL} in the database).
     */
    private YearMonth periodLabel;

    /**
     * The {@link Vehicle} object with which this invoice is associated.
     * This represents the foreign key relationship to the {@code Vehicles} table.
     * This field is **required** (corresponds to {@code VehicleID_FK INTEGER NOT NULL} in the database).
     */
    private Vehicle vehicle;

    /**
     * Optional notes or comments regarding the invoice.
     * This field can contain administrative remarks, specific payment instructions,
     * or a summary of the invoice's purpose (corresponds to {@code Notes TEXT} in the database).
     */
    private String notes;

    /**
     * Default constructor for creating an empty {@code Invoice} object.
     * The {@code invoiceID} is set to {@code null} to explicitly indicate that
     * this invoice has not yet been assigned a unique ID by the database.
     * This constructor is primarily used by frameworks that instantiate objects
     * via reflection (e.g., ORMs, JSON deserializers) before populating their fields.
     */
    public Invoice() {
        this.invoiceID = null; // Explicitly null for unpersisted entity
    }

    /**
     * Full constructor to initialize all fields of an {@code Invoice} instance.
     * This constructor is typically used when loading an *existing* invoice
     * record from the database, where {@code invoiceID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param invoiceID     The unique integer ID for the invoice, typically assigned by the database. Must not be {@code null}.
     * @param invoiceDate   The {@link LocalDate} when the invoice was generated (e.g., {@code 2024-05-15}). Must not be {@code null}.
     * @param dueDate       The {@link LocalDate} by which the invoice is to be paid (e.g., {@code 2024-05-31}). Must not be {@code null}.
     * @param periodLabel   The {@link YearMonth} representing the billing period of the invoice (e.g., {@code YearMonth.of(2024, 5)} for May 2024). Must not be {@code null}.
     * @param vehicle       The {@link Vehicle} object associated with this invoice. Must not be {@code null}.
     * @param notes         Any optional notes or additional information about the invoice. Can be {@code null}.
     * @throws NullPointerException if `invoiceID`, `invoiceDate`, `dueDate`, `periodLabel`, or `vehicle` are {@code null}.
     */
    public Invoice(Integer invoiceID, LocalDate invoiceDate, LocalDate dueDate, YearMonth periodLabel, Vehicle vehicle, String notes) {
        this.invoiceID = Objects.requireNonNull(invoiceID, "Invoice ID cannot be null for an existing invoice.");
        setInvoiceDate(invoiceDate);
        setDueDate(dueDate);
        setPeriodLabel(periodLabel);
        setVehicle(vehicle);
        setNotes(notes); // Notes can be null, use setter for trimming consistency
    }

    /**
     * Convenience constructor for creating a new {@code Invoice} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new invoice record for **insertion** into the database.
     * The {@code invoiceID} is omitted as it is typically auto-generated by the database.
     * All parameters are validated via their respective setters.
     *
     * @param invoiceDate   The {@link LocalDate} when the invoice was generated (e.g., {@code 2024-05-15}). Must not be {@code null}.
     * @param dueDate       The {@link LocalDate} by which the invoice is to be paid (e.g., {@code 2024-05-31}). Must not be {@code null}.
     * @param periodLabel   The {@link YearMonth} representing the billing period of the invoice (e.g., {@code YearMonth.of(2024, 5)} for May 2024). Must not be {@code null}.
     * @param vehicle       The {@link Vehicle} object associated with this invoice. Must not be {@code null}.
     * @param notes         Any optional notes or additional information about the invoice. Can be {@code null}.
     * @throws NullPointerException if `invoiceDate`, `dueDate`, `periodLabel`, or `vehicle` are {@code null}.
     */
    public Invoice(LocalDate invoiceDate, LocalDate dueDate, YearMonth periodLabel, Vehicle vehicle, String notes) {
        this.invoiceID = null; // New entity, ID will be assigned by DB
        setInvoiceDate(invoiceDate);
        setDueDate(dueDate);
        setPeriodLabel(periodLabel);
        setVehicle(vehicle);
        setNotes(notes);
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Retrieves the unique identifier for this invoice.
     * For new, unpersisted invoices, this will be {@code null}.
     * Corresponds to the {@code InvoiceID} column in the database.
     *
     * @return The {@link Integer} primary key used to identify this invoice record, or {@code null} if not yet assigned.
     */
    public Integer getInvoiceID() { // Changed return type to Integer
        return invoiceID;
    }
    // setInvoiceID method is removed as invoiceID is now final and set only via constructors

    /**
     * Retrieves the {@link LocalDate} when the invoice was generated.
     * Corresponds to the {@code InvoiceDate} column in the database.
     *
     * @return The {@link LocalDate} representing the invoice generation date.
     */
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }
    /**
     * Sets the {@link LocalDate} when the invoice was generated.
     *
     * @param invoiceDate The {@link LocalDate} to set as the invoice generation date. Must not be {@code null}.
     * @throws NullPointerException if {@code invoiceDate} is {@code null}.
     */
    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = Objects.requireNonNull(invoiceDate, "Invoice date cannot be null.");
    }
    /**
     * Retrieves the {@link LocalDate} representing the due date of the invoice.
     * Corresponds to the {@code DueDate} column in the database.
     *
     * @return The {@link LocalDate} by which the invoice payment is expected.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }
    /**
     * Sets the {@link LocalDate} for the invoice's due date.
     * This indicates the date by which participants are required to pay their share.
     * Corresponds to the {@code DueDate} column in the database.
     *
     * @param dueDate The {@link LocalDate} to set as the invoice due date. Must not be {@code null}.
     * @throws NullPointerException if {@code dueDate} is {@code null}.
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = Objects.requireNonNull(dueDate, "Due date cannot be null.");
    }
    /**
     * Retrieves the billing period of the invoice as a {@link YearMonth} object.
     * Corresponds to the {@code PeriodLabel} column in the database.
     *
     * @return The {@link YearMonth} representing the billing period (e.g., June 2025).
     */
    public YearMonth getPeriodLabel() {
        return periodLabel;
    }
    /**
     * Sets the billing period of the invoice.
     * This {@link YearMonth} indicates the specific month and year for which the invoice's charges apply.
     * Corresponds to the {@code PeriodLabel} column in the database.
     *
     * @param periodLabel The {@link YearMonth} to set as the billing period. Must not be {@code null}.
     * @throws NullPointerException if {@code periodLabel} is {@code null}.
     */
    public void setPeriodLabel(YearMonth periodLabel) {
        this.periodLabel = Objects.requireNonNull(periodLabel, "Period label cannot be null.");
    }
    /**
     * Retrieves the {@link Vehicle} object associated with this invoice.
     * This represents the foreign key relationship to the {@code Vehicles} table.
     *
     * @return The associated {@link Vehicle} object.
     */
    public Vehicle getVehicle() {
        return vehicle;
    }
    /**
     * Sets the {@link Vehicle} object associated with this invoice.
     * This links the invoice to a specific vehicle.
     *
     * @param vehicle The {@link Vehicle} object to associate with this invoice. Must not be {@code null}.
     * @throws NullPointerException if {@code vehicle} is {@code null}.
     */
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = Objects.requireNonNull(vehicle, "Vehicle cannot be null.");
    }
    /**
     * Retrieves any optional notes or remarks associated with this invoice.
     * This might include details about special charges, payment instructions, or administrative comments.
     * Corresponds to the {@code Notes} column in the database.
     *
     * @return A string containing the notes, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes;
    }
    /**
     * Sets additional notes or internal comments for this invoice.
     * If the provided notes are not {@code null}, leading and trailing whitespace will be stripped.
     * Corresponds to the {@code Notes} column in the database.
     *
     * @param notes The string containing notes to set. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.strip() : null;
    }

    // ---------------------
    // Utility Methods
    // ---------------------

    /**
     * <p>
     * Returns a formatted string representation of the invoice's billing period.
     * This method uses a specific date format ("MMM-yyyy") to display the period
     * in a user-friendly manner (e.g., "Jun-2025").
     * </p>
     * <p>
     * If the {@code periodLabel} is {@code null}, this method will return {@code null}.
     * </p>
     *
     * @return A formatted string representing the invoice's billing period, or {@code null} if {@code periodLabel} is {@code null}.
     */
    public String getFormattedPeriodLabel() {
        if (periodLabel == null) {
            return null;
        }
        return periodLabel.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
    }
    /**
     * <p>
     * Returns a string representation of the {@code Invoice} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the invoice's key attributes.
     * </p>
     * <p>
     * The format includes the invoice ID, invoice date, due date,
     * formatted billing period, and the associated vehicle's ID (if available).
     * </p>
     *
     * @return A string in the format:
     * "Invoice{invoiceID=..., invoiceDate=..., dueDate=..., periodLabel=..., vehicleID=...}"
     */
    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceID=" + invoiceID +
                ", invoiceDate=" + invoiceDate +
                ", dueDate=" + dueDate +
                ", periodLabel=" + getFormattedPeriodLabel() +
                ", vehicleID=" + (vehicle != null ? vehicle.getVehicleID() : "null") + // Safely get vehicle ID
                '}';
    }
    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based primarily on the unique {@code invoiceID}.
     * </p>
     * <p>
     * This method adheres to the general contract of the {@link Object#equals(Object)} method,
     * ensuring consistency with hash-based collections. It correctly handles cases where
     * {@code invoiceID} might be {@code null} for unpersisted entities.
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        // Equality is based on the primary key (invoiceID), safely handling null Integer
        return Objects.equals(invoiceID, invoice.invoiceID); // Updated to handle Integer nulls
    }
    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap} and {@link java.util.HashSet}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code invoiceID}. If {@code invoiceID}
     * is {@code null} (for unpersisted entities), its hash code will be 0, as per {@link Objects#hash(Object...)}.
     * This ensures that objects considered equal by {@link #equals(Object)} will have the same hash code.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(invoiceID);
    }
}