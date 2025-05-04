package com.avaruusstudios.vmdb.model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Represents an invoice in the Vanpool Management System
 * A singular invoice contains metadata about the invoice itself such as Due Date, Date Prepared.
 * This object reflect the schema used for the SQLite Database
 */
public class Invoice {
    /** Unique identifier for the invoice */
    private int invoiceID;
    /** Date the invoice was prepared */
    private LocalDate invoiceDate;
    /** Date the invoice is to be paid */
    private LocalDate dueDate;
    /** Billing period of the invoice */
    private YearMonth periodLabel;
    /** VehicleID the invoice is associated with */
    private int vehicleID;
    /** Invoice notes */
    private String notes;

    /**
     * Default Constructor
     */
    public Invoice() {}
    /**
     * Full constructor to initialize all fields.
     *
     * @param invoiceID     the unique invoice ID
     * @param invoiceDate   the date the invoice was generated
     * @param dueDate       the due date of the invoice
     * @param periodLabel   the period of invoice (ie: MMM-YYYY)
     * @param vehicleID  the unique vanpool ID which ties the invoice to the vehicle
     * @param notes         any extra information
     */
    public Invoice(int invoiceID, LocalDate invoiceDate, LocalDate dueDate, YearMonth periodLabel, int vehicleID, String notes) {
        this.invoiceID = invoiceID;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.periodLabel = periodLabel;
        this.vehicleID = vehicleID;
        this.notes = notes;
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Gets the unique invoice ID.
     *
     * @return the invoice id
     */
    public int getInvoiceID() {
        return invoiceID;
    }
    /**
     * Sets the unique invoice ID.
     * This method is typically used when initializing an invoice object from a persisted source.
     *
     * @param invoiceID the invoice id
     */
    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }
    /**
     * Gets the date the invoice was generated.
     *
     * @return the invoice date
     */
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }
    /**
     * Sets the date the invoice was generated.
     *
     * @param invoiceDate the invoice date
     */
    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * Gets due date of the invoice.
     *
     * @return the due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }
    /**
     * Sets due date of the invoice.
     * Indicates the date in which participants are required to pay their share.
     *
     * @param dueDate the due date
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    /**
     * Gets period label of the invoice in the format (MMM-YYYY).
     *
     * @return the period label
     */
    public YearMonth getPeriodLabel() {
        return periodLabel;
    }
    /**
     * Sets period label of the invoice.
     * Indicates the billing period of the invoice.
     *
     * @param periodLabel the period label
     */
    public void setPeriodLabel(YearMonth periodLabel) {
        this.periodLabel = periodLabel;
    }
    /**
     * Gets the ID of the vehicle assigned to this invoice.
     *
     * @return the vehicle id fk
     */
    public int getVehicleID() {
        return vehicleID;
    }
    /**
     * Sets the ID of the vehicle associated with this invoice.
     *
     * @param vehicleID the vehicle ID
     */
    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }
    /**
     * Gets any notes or remarks associated with this invoice.
     * May include purpose of the invoice.
     *
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }
    /**
     * Sets additional notes or internal comments for this invoice.
     *
     * @param notes the notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    // ---------------------
    // Utilities
    // ---------------------

    /**
     * Gets formatted period label.
     *
     * @return the formatted period label
     */
    public String getFormattedPeriodLabel() {
        return periodLabel.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
    }

}
