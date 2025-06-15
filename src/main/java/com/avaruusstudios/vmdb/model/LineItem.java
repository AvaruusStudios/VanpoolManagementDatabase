package com.avaruusstudios.vmdb.model;

import java.math.BigDecimal; // Import BigDecimal
import java.util.Objects;

/**
 * <p>
 * Represents an individual line item within an {@link Invoice} in the Vanpool Management System.
 * Each line item details a specific charge or payment associated with a particular participant
 * for a given invoice.
 * </p>
 *
 * <p>
 * This class encapsulates the unique identifier for the item, references to its parent invoice
 * and the involved participant, the composite {@link Amount} representing benefit and personal payments,
 * a flag indicating the official payment status, and any relevant notes.
 * The payment status (`isPaid`) is determined by external transaction matching logic,
 * rather than a stored `paidAmount` field.
 * </p>
 *
 * @see Invoice
 * @see Participant
 * @see Amount
 */
public class LineItem { // Renamed from InvoiceItem to LineItem
    /**
     * Unique identifier for the invoice line item. This serves as the primary key
     * in the database for invoice item records ({@code InvoiceItemID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created invoice item not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable.
     * </p>
     */
    private final Integer invoiceItemID; // Changed from int to final Integer
    /**
     * The {@link Invoice} object to which this invoice line item belongs.
     * This represents the foreign key relationship to the {@code Invoices} table.
     * This field is **required** (corresponds to {@code InvoiceID_FK INTEGER NOT NULL} in the database).
     */
    private Invoice invoice;
    /**
     * The {@link Participant} object for whom this invoice line item is generated.
     * This represents the foreign key relationship to the {@code Participants} table.
     * This field is **required** (corresponds to {@code ParticipantID_FK INTEGER NOT NULL} in the database).
     */
    private Participant participant;
    /**
     * A composite {@link Amount} object encapsulating the required benefit payment
     * and personal payment for this invoice line item.
     * This field is **required** (corresponds to {@code BenefitPayment REAL NOT NULL} and {@code PersonalPayment REAL NOT NULL} in the database).
     */
    private Amount amountDue;
    /**
     * A boolean flag indicating whether the participant has officially paid for this invoice line item.
     * This status is typically updated by external business logic that matches transactions
     * to the required payments for this item.
     * Corresponds to {@code IsPaid INTEGER NOT NULL DEFAULT 0} in the database (where 1=true, 0=false).
     */
    private boolean isPaid;
    /**
     * Optional notes or contextual information about the invoice line item.
     * Corresponds to {@code Notes TEXT} in the database.
     */
    private String notes;

    /**
     * Default constructor for creating an empty {@code LineItem} object.
     * The {@code invoiceItemID} is set to {@code null} to explicitly indicate that
     * this invoice line item has not yet been assigned a unique ID by the database.
     * This constructor initializes the {@code amountDue} field with a default {@link Amount} object.
     * This constructor is primarily used by frameworks that instantiate objects
     * via reflection (e.g., ORMs, JSON deserializers) before populating their fields.
     */
    public LineItem() { // Renamed from InvoiceItem
        this.invoiceItemID = null; // Explicitly null for unpersisted entity
        this.amountDue = new Amount(); // Ensure Amount is always initialized
    }
    /**
     * Full constructor to initialize all fields of an {@code LineItem} instance.
     * This constructor is typically used when loading an *existing* invoice line item
     * record from the database, where {@code invoiceItemID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param invoiceItemID     The unique integer ID for the invoice line item, typically assigned by the database. Must not be {@code null}.
     * @param invoice           The {@link Invoice} object to which this item belongs. Must not be {@code null}.
     * @param participant       The {@link Participant} object for whom this item is generated. Must not be {@code null}.
     * @param amountDue         The {@link Amount} object representing the required benefit and personal payments. Must not be {@code null}.
     * @param isPaid            A boolean indicating if the full amount due for this item has been officially paid.
     * @param notes             Any optional notes or additional information about the invoice item. Can be {@code null}.
     * @throws NullPointerException if `invoiceItemID`, `invoice`, `participant`, or `amountDue` are {@code null}.
     */
    public LineItem(Integer invoiceItemID, Invoice invoice, Participant participant, // Renamed from InvoiceItem
                    Amount amountDue, boolean isPaid, String notes) {
        this.invoiceItemID = Objects.requireNonNull(invoiceItemID, "Invoice item ID cannot be null for an existing item.");
        setInvoice(invoice);
        setParticipant(participant);
        setAmountDue(amountDue);
        this.isPaid = isPaid; // boolean, no setter validation needed
        setNotes(notes); // Notes can be null, use setter for trimming consistency
    }
    /**
     * Convenience constructor for creating a new {@code LineItem} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new invoice line item record for **insertion** into the database.
     * The {@code invoiceItemID} is omitted as it is typically auto-generated by the database.
     * All parameters are validated via their respective setters.
     *
     * @param invoice           The {@link Invoice} object to which this item belongs. Must not be {@code null}.
     * @param participant       The {@link Participant} object for whom this item is generated. Must not be {@code null}.
     * @param amountDue         The {@link Amount} object representing the required benefit and personal payments. Must not be {@code null}.
     * @param isPaid            A boolean indicating if the full amount due for this item has been officially paid.
     * @param notes             Any optional notes or additional information about the invoice item. Can be {@code null}.
     * @throws NullPointerException if `invoice`, `participant`, or `amountDue` are {@code null}.
     */
    public LineItem(Invoice invoice, Participant participant, // Renamed from InvoiceItem
                    Amount amountDue, boolean isPaid, String notes) {
        this.invoiceItemID = null; // New entity, ID will be assigned by DB
        setInvoice(invoice);
        setParticipant(participant);
        setAmountDue(amountDue);
        this.isPaid = isPaid;
        setNotes(notes);
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Retrieves the unique identifier for this invoice line item.
     * For new, unpersisted invoice items, this will be {@code null}.
     * Corresponds to the {@code InvoiceItemID} column in the database.
     *
     * @return The {@link Integer} primary key used to identify this invoice line item record, or {@code null} if not yet assigned.
     */
    public Integer getInvoiceItemID() {
        return invoiceItemID;
    }

    // setInvoiceItemID method is removed as invoiceItemID is now final and set only via constructors

    /**
     * Retrieves the {@link Invoice} object to which this invoice line item belongs.
     * Corresponds to the {@code InvoiceID_FK} column in the database.
     *
     * @return The associated {@link Invoice} object.
     */
    public Invoice getInvoice() {
        return invoice;
    }
    /**
     * Sets the {@link Invoice} object to which this invoice line item belongs.
     *
     * @param invoice The {@link Invoice} object to set. Must not be {@code null}.
     * @throws NullPointerException if {@code invoice} is {@code null}.
     */
    public void setInvoice(Invoice invoice) {
        this.invoice = Objects.requireNonNull(invoice, "Invoice cannot be null.");
    }
    /**
     * Retrieves the {@link Participant} object for whom this invoice line item is generated.
     * Corresponds to the {@code ParticipantID_FK} column in the database.
     *
     * @return The associated {@link Participant} object.
     */
    public Participant getParticipant() {
        return participant;
    }
    /**
     * Sets the {@link Participant} object for whom this invoice line item is generated.
     *
     * @param participant The {@link Participant} object to set. Must not be {@code null}.
     * @throws NullPointerException if {@code participant} is {@code null}.
     */
    public void setParticipant(Participant participant) {
        this.participant = Objects.requireNonNull(participant, "Participant cannot be null.");
    }
    /**
     * Retrieves the {@link Amount} object representing the required benefit and personal payments.
     * Corresponds to {@code BenefitPayment} and {@code PersonalPayment} columns in the database.
     *
     * @return The {@link Amount} object containing the due payment components.
     */
    public Amount getAmountDue() {
        return amountDue;
    }
    /**
     * Sets the {@link Amount} object representing the required benefit and personal payments.
     *
     * @param amountDue The {@link Amount} object to set. Must not be {@code null}.
     * @throws NullPointerException if {@code amountDue} is {@code null}.
     */
    public void setAmountDue(Amount amountDue) {
        this.amountDue = Objects.requireNonNull(amountDue, "Amount due cannot be null.");
    }
    /**
     * Checks if the participant has officially paid for this invoice line item based on the stored flag.
     * Corresponds to the {@code IsPaid} column in the database.
     *
     * @return {@code true} if the item is marked as paid; {@code false} otherwise.
     */
    public boolean getIsPaid() {
        return isPaid;
    }
    /**
     * Sets whether the participant has officially paid for this invoice line item.
     * Corresponds to the {@code IsPaid} column in the database.
     *
     * @param paid {@code true} to mark as paid; {@code false} to mark as unpaid.
     */
    public void setIsPaid(boolean paid) {
        isPaid = paid;
    }
    /**
     * Retrieves any optional notes or contextual information about the invoice line item.
     * Corresponds to the {@code Notes} column in the database.
     *
     * @return A string containing the notes, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes;
    }
    /**
     * Sets additional notes or contextual information for this invoice line item.
     * If the provided notes are not {@code null}, leading and trailing whitespace will be stripped.
     *
     * @param notes The string containing notes to set. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.strip() : null;
    }

    // ---------------------
    // Computed Properties
    // ---------------------

    /**
     * Calculates the total amount that is currently due for this invoice line item,
     * which is the sum of the benefit payment and personal payment components
     * encapsulated within the {@link Amount} object.
     *
     * @return The combined total amount due for this invoice line item as a {@link BigDecimal},
     * or {@link BigDecimal#ZERO} if {@code amountDue} is null.
     */
    public BigDecimal getTotalDue() { // Changed return type to BigDecimal
        return amountDue != null ? amountDue.getTotal() : BigDecimal.ZERO; // Returns BigDecimal
    }

    // ---------------------
    // Utility Methods
    // ---------------------

    /**
     * <p>
     * Returns a string representation of the {@code LineItem} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the invoice line item's key attributes.
     * </p>
     * <p>
     * The format includes the invoice item ID, the parent invoice's ID,
     * the associated participant's ID, the total amount due, and the official paid status.
     * </p>
     *
     * @return A string in the format:
     * "LineItem{ID=..., InvoiceID=..., ParticipantID=..., AmountDue=..., IsPaid=...}"
     */
    @Override
    public String toString() {
        return "LineItem{" + // Renamed from InvoiceItem
                "invoiceItemID=" + invoiceItemID +
                ", invoiceID=" + (invoice != null ? invoice.getInvoiceID() : "null") +
                ", participantID=" + (participant != null ? participant.getParticipantID() : "null") +
                ", amountDue=" + (amountDue != null ? amountDue.toString() : "null") +
                ", isPaid=" + isPaid +
                '}';
    }
    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based primarily on the unique {@code invoiceItemID}.
     * </p>
     * <p>
     * This method adheres to the general contract of the {@link Object#equals(Object)} method,
     * ensuring consistency with hash-based collections. It correctly handles cases where
     * {@code invoiceItemID} might be {@code null} for unpersisted entities.
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineItem that = (LineItem) o; // Renamed from InvoiceItem
        // Equality is based on the primary key (invoiceItemID), safely handling null Integer
        return Objects.equals(invoiceItemID, that.invoiceItemID); // Updated to handle Integer nulls
    }
    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap} and {@link java.util.HashSet}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code invoiceItemID}. If {@code invoiceItemID}
     * is {@code null} (for unpersisted entities), its hash code will be 0, as per {@link Objects#hash(Object...)}.
     * This ensures that objects considered equal by {@link #equals(Object)} will have the same hash code.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(invoiceItemID);
    }
}