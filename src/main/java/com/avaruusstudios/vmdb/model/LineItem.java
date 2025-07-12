package com.avaruusstudios.vmdb.model;

import javafx.beans.property.BooleanProperty; // Import JavaFX property classes
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime; // New import for deletedAt
import java.util.Objects;

/**
 * <p>
 * Represents an individual line item within an {@link Invoice} in the Vanpool Management System.
 * Each line item details a specific charge or payment associated with a particular participant
 * for a given invoice.
 * </p>
 *
 * <p>
 * This class encapsulates the unique identifier for the item ({@code LineItemID INTEGER PRIMARY KEY AUTOINCREMENT}),
 * references to its parent invoice ({@code InvoiceID_FK INTEGER NOT NULL})
 * and the involved participant ({@code ParticipantID_FK INTEGER NOT NULL}),
 * the composite {@link Amount} representing benefit ({@code BenefitPayment NUMERIC NOT NULL}) and personal payments ({@code PersonalPayment NUMERIC NOT NULL}),
 * a flag indicating the official payment status ({@code IsPaid INTEGER NOT NULL DEFAULT 0}),
 * an active status for soft deletion ({@code IsActive INTEGER NOT NULL DEFAULT 1}),
 * a timestamp for logical deletion ({@code DeletedAt TEXT DEFAULT NULL}),
 * and any relevant notes ({@code Notes TEXT}).
 * The payment status (`isPaid`) is determined by external transaction matching logic,
 * rather than a stored `paidAmount` field.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.1
 * Created On: 2025-07-11
 * Updated On: 2025-07-12
 *
 * @see Invoice
 * @see Participant
 * @see Amount
 */
public class LineItem {
    /**
     * Unique identifier for the invoice line item. This serves as the primary key
     * in the database for invoice item records ({@code LineItemID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created invoice item not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable.
     * </p>
     */
    private final ReadOnlyObjectProperty<Integer> invoiceItemID;
    /**
     * The {@link Invoice} object to which this invoice line item belongs.
     * This represents the foreign key relationship to the {@code Invoices} table.
     * This field is **required** (corresponds to {@code InvoiceID_FK INTEGER NOT NULL} in the database).
     */
    private final ObjectProperty<Invoice> invoice;
    /**
     * The {@link Participant} object for whom this invoice line item is generated.
     * This represents the foreign key relationship to the {@code Participants} table.
     * This field is **required** (corresponds to {@code ParticipantID_FK INTEGER NOT NULL} in the database).
     */
    private final ObjectProperty<Participant> participant;
    /**
     * A composite {@link Amount} object encapsulating the required benefit payment
     * and personal payment for this invoice line item.
     * This field is **required** (corresponds to {@code BenefitPayment NUMERIC NOT NULL} and {@code PersonalPayment NUMERIC NOT NULL} in the database).
     */
    private final ObjectProperty<Amount> amountDue;
    /**
     * A boolean flag indicating whether the participant has officially paid for this invoice line item.
     * This status is typically updated by external business logic that matches transactions
     * to the required payments for this item.
     * Corresponds to {@code IsPaid INTEGER NOT NULL DEFAULT 0} in the database (where 1=true, 0=false).
     */
    private final BooleanProperty isPaid;
    /**
     * Indicates whether the line item is currently active or has been logically deleted/deactivated.
     * (corresponds to {@code IsActive INTEGER NOT NULL DEFAULT 1} in the database).
     * `true` (1) for active, `false` (0) for inactive.
     */
    private final BooleanProperty isActive;
    /**
     * The timestamp when the line item was logically deleted or deactivated.
     * This field is optional and can be {@code null} if the line item is active.
     * Corresponds to {@code DeletedAt TEXT DEFAULT NULL} in the database.
     */
    private final ObjectProperty<LocalDateTime> deletedAt; // New field from schema
    /**
     * Optional notes or contextual information about the invoice line item.
     * Corresponds to {@code Notes TEXT} in the database.
     */
    private final StringProperty notes;

    /**
     * Default constructor for creating an empty {@code LineItem} object.
     * The {@code invoiceItemID} is set to {@code null} to explicitly indicate that
     * this invoice line item has not yet been assigned a unique ID by the database.
     * This constructor initializes the {@code amountDue} field with a default {@link Amount} object.
     * Initializes other properties to default/null values to ensure a stable state,
     * including setting `isPaid` to `false` and `isActive` to `true` by default,
     * and `deletedAt` to `null`.
     * This constructor is primarily used by frameworks that instantiate objects
     * via reflection (e.g., ORMs, JSON deserializers) before populating their fields.
     */
    public LineItem() {
        this.invoiceItemID = new SimpleObjectProperty<>(this, "invoiceItemID", null);
        this.invoice = new SimpleObjectProperty<>(this, "invoice");
        this.participant = new SimpleObjectProperty<>(this, "participant");
        this.amountDue = new SimpleObjectProperty<>(this, "amountDue", new Amount()); // Ensure Amount is always initialized
        this.isPaid = new SimpleBooleanProperty(this, "isPaid", false); // Default to unpaid
        this.isActive = new SimpleBooleanProperty(this, "isActive", true); // Default to active
        this.deletedAt = new SimpleObjectProperty<>(this, "deletedAt", null); // Initialize new property to null
        this.notes = new SimpleStringProperty(this, "notes");
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
     * @param isActive          The active status of the line item (true for active, false for inactive/deleted).
     * @param deletedAt         The {@link LocalDateTime} when the line item was logically deleted, or {@code null} if active.
     * @param notes             Any optional notes or additional information about the invoice item. Can be {@code null}.
     * @throws NullPointerException if `invoiceItemID`, `invoice`, `participant`, or `amountDue` are {@code null}.
     */
    public LineItem(Integer invoiceItemID, Invoice invoice, Participant participant,
                    Amount amountDue, boolean isPaid, boolean isActive, LocalDateTime deletedAt, String notes) {
        this.invoiceItemID = new SimpleObjectProperty<>(this, "invoiceItemID", Objects.requireNonNull(invoiceItemID, "Invoice item ID cannot be null for an existing item."));
        this.invoice = new SimpleObjectProperty<>(this, "invoice");
        this.participant = new SimpleObjectProperty<>(this, "participant");
        this.amountDue = new SimpleObjectProperty<>(this, "amountDue");
        this.isPaid = new SimpleBooleanProperty(this, "isPaid");
        this.isActive = new SimpleBooleanProperty(this, "isActive");
        this.deletedAt = new SimpleObjectProperty<>(this, "deletedAt"); // Initialize new property
        this.notes = new SimpleStringProperty(this, "notes");

        setInvoice(invoice);
        setParticipant(participant);
        setAmountDue(amountDue);
        setIsPaid(isPaid);
        setIsActive(isActive);
        setDeletedAt(deletedAt); // Set new property
        setNotes(notes);
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
    public LineItem(Invoice invoice, Participant participant,
                    Amount amountDue, boolean isPaid, String notes) {
        // Call the full constructor with null for invoiceItemID, isActive defaulting to true, and deletedAt as null
        this(null, invoice, participant, amountDue, isPaid, true, null, notes);
    }

    // --- JavaFX Property Accessors ---

    /**
     * Retrieves the read-only property for the invoice line item's unique ID.
     * This property represents the {@code LineItemID} column in the database.
     * <p>
     * As this property is {@code ReadOnlyObjectProperty}, its value cannot be
     * changed directly after initial assignment, enforcing the immutability
     * of the primary key for persisted entities.
     * </p>
     *
     * @return The {@link ReadOnlyObjectProperty} for {@code invoiceItemID}.
     */
    public ReadOnlyObjectProperty<Integer> invoiceItemIDProperty() {
        return invoiceItemID;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the {@link Invoice} object
     * to which this line item belongs.
     * This property represents the foreign key relationship to the {@code Invoices} table ({@code InvoiceID_FK}).
     *
     * @return The {@link ObjectProperty} for {@code invoice}.
     */
    public ObjectProperty<Invoice> invoiceProperty() {
        return invoice;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the {@link Participant} object
     * for whom this line item is generated.
     * This property represents the foreign key relationship to the {@code Participants} table ({@code ParticipantID_FK}).
     *
     * @return The {@link ObjectProperty} for {@code participant}.
     */
    public ObjectProperty<Participant> participantProperty() {
        return participant;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the {@link Amount} object
     * representing the required benefit and personal payments.
     * This property corresponds to the {@code BenefitPayment} and {@code PersonalPayment} columns in the database.
     *
     * @return The {@link ObjectProperty} for {@code amountDue}.
     */
    public ObjectProperty<Amount> amountDueProperty() {
        return amountDue;
    }

    /**
     * Retrieves the {@link BooleanProperty} indicating whether the line item is paid.
     * This property corresponds to the {@code IsPaid} column in the database.
     *
     * @return The {@link BooleanProperty} for {@code isPaid}.
     */
    public BooleanProperty isPaidProperty() {
        return isPaid;
    }

    /**
     * Retrieves the {@link BooleanProperty} for the active status of the line item.
     * This property corresponds to the {@code IsActive} column in the database.
     *
     * @return The {@link BooleanProperty} for {@code isActive}.
     */
    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the deletion timestamp of the line item.
     * This property corresponds to the {@code DeletedAt} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code deletedAt}.
     */
    public ObjectProperty<LocalDateTime> deletedAtProperty() { // New property accessor
        return deletedAt;
    }

    /**
     * Retrieves the {@link StringProperty} for any optional notes.
     * This property corresponds to the {@code Notes} column in the database.
     *
     * @return The {@link StringProperty} for {@code notes}.
     */
    public StringProperty notesProperty() {
        return notes;
    }

    // --- Value Getters and Setters ---

    /**
     * Retrieves the unique identifier for this invoice line item.
     * For new, unpersisted invoice items, this will be {@code null}.
     * Corresponds to the {@code LineItemID} column in the database.
     *
     * @return The {@link Integer} primary key used to identify this invoice line item record, or {@code null} if not yet assigned.
     */
    public Integer getInvoiceItemID() {
        return invoiceItemID.get();
    }

    /**
     * Sets the unique ID for this invoice line item. This method is designed to be package-private
     * and is primarily for use by data access objects (DAOs) when an ID is generated
     * by the database upon insertion.
     * <p>
     * It includes a check to prevent the ID from being modified once it has been set,
     * ensuring the immutability of the primary key.
     * </p>
     *
     * @param id The unique integer ID assigned by the database.
     * @throws IllegalStateException    if the ID has already been assigned to this object.
     * @throws IllegalArgumentException if the provided ID is {@code null} or non-positive.
     */
    void _setInvoiceItemID(Integer id) { // Package-private for DAO use only
        if (this.invoiceItemID.get() != null) {
            throw new IllegalStateException("Invoice Item ID cannot be changed once set.");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invoice Item ID cannot be null or non-positive.");
        }
        ((SimpleObjectProperty<Integer>) this.invoiceItemID).set(id);
    }

    /**
     * Retrieves the {@link Invoice} object to which this invoice line item belongs.
     * Corresponds to the {@code InvoiceID_FK} column in the database.
     *
     * @return The associated {@link Invoice} object.
     */
    public Invoice getInvoice() {
        return invoice.get();
    }

    /**
     * Sets the {@link Invoice} object to which this invoice line item belongs.
     *
     * @param invoice The {@link Invoice} object to set. Must not be {@code null}.
     * @throws NullPointerException if {@code invoice} is {@code null}.
     */
    public void setInvoice(Invoice invoice) {
        this.invoice.set(Objects.requireNonNull(invoice, "Invoice cannot be null."));
    }

    /**
     * Retrieves the {@link Participant} object for whom this invoice line item is generated.
     * Corresponds to the {@code ParticipantID_FK} column in the database.
     *
     * @return The associated {@link Participant} object.
     */
    public Participant getParticipant() {
        return participant.get();
    }

    /**
     * Sets the {@link Participant} object for whom this invoice line item is generated.
     *
     * @param participant The {@link Participant} object to set. Must not be {@code null}.
     * @throws NullPointerException if {@code participant} is {@code null}.
     */
    public void setParticipant(Participant participant) {
        this.participant.set(Objects.requireNonNull(participant, "Participant cannot be null."));
    }

    /**
     * Retrieves the {@link Amount} object representing the required benefit and personal payments.
     * Corresponds to {@code BenefitPayment} and {@code PersonalPayment} columns in the database.
     *
     * @return The {@link Amount} object containing the due payment components.
     */
    public Amount getAmountDue() {
        return amountDue.get();
    }

    /**
     * Sets the {@link Amount} object representing the required benefit and personal payments.
     *
     * @param amountDue The {@link Amount} object to set. Must not be {@code null}.
     * @throws NullPointerException if {@code amountDue} is {@code null}.
     */
    public void setAmountDue(Amount amountDue) {
        this.amountDue.set(Objects.requireNonNull(amountDue, "Amount due cannot be null."));
    }

    /**
     * Checks if the participant has officially paid for this invoice line item based on the stored flag.
     * Corresponds to the {@code IsPaid} column in the database.
     *
     * @return {@code true} if the item is marked as paid; {@code false} otherwise.
     */
    public boolean getIsPaid() {
        return isPaid.get();
    }

    /**
     * Sets whether the participant has officially paid for this invoice line item.
     * Corresponds to the {@code IsPaid} column in the database.
     *
     * @param paid {@code true} to mark as paid; {@code false} to mark as unpaid.
     */
    public void setIsPaid(boolean paid) {
        this.isPaid.set(paid);
    }

    /**
     * Retrieves the active status of the line item.
     * Corresponds to the {@code IsActive} column in the database.
     *
     * @return {@code true} if the line item is active, {@code false} if it's inactive/logically deleted.
     */
    public boolean getIsActive() {
        return isActive.get();
    }

    /**
     * Sets the active status of the line item.
     *
     * @param isActive {@code true} to mark the line item as active, {@code false} for inactive/logically deleted.
     */
    public void setIsActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    /**
     * Retrieves the timestamp when the line item was logically deleted or deactivated.
     * Corresponds to the {@code DeletedAt} column in the database.
     *
     * @return The {@link LocalDateTime} of deletion, or {@code null} if the line item is active.
     */
    public LocalDateTime getDeletedAt() { // New getter
        return deletedAt.get();
    }

    /**
     * Sets the timestamp when the line item was logically deleted or deactivated.
     *
     * @param deletedAt The {@link LocalDateTime} to set as the deletion timestamp. Can be {@code null}.
     */
    public void setDeletedAt(LocalDateTime deletedAt) { // New setter
        this.deletedAt.set(deletedAt);
    }

    /**
     * Retrieves any optional notes or contextual information about the invoice line item.
     * Corresponds to the {@code Notes} column in the database.
     *
     * @return A string containing the notes, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes.get();
    }

    /**
     * Sets additional notes or contextual information for this invoice line item.
     * If the provided notes are not {@code null}, leading and trailing whitespace will be stripped.
     *
     * @param notes The string containing notes to set. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes.set((notes != null) ? notes.strip() : null);
    }

    // --- Computed Properties ---

    /**
     * Calculates the total amount that is currently due for this invoice line item,
     * which is the sum of the benefit payment and personal payment components
     * encapsulated within the {@link Amount} object.
     *
     * @return The combined total amount due for this invoice line item as a {@link BigDecimal},
     * or {@link BigDecimal#ZERO} if {@code amountDue} is null.
     */
    public BigDecimal getTotalDue() {
        // Access amountDue via its getter, as it's now a property
        Amount currentAmountDue = getAmountDue();
        return currentAmountDue != null ? currentAmountDue.getTotal() : BigDecimal.ZERO;
    }

    // --- Utility Methods ---

    /**
     * <p>
     * Returns a string representation of the {@code LineItem} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the invoice line item's key attributes.
     * </p>
     * <p>
     * The format includes the invoice item ID, the parent invoice's ID,
     * the associated participant's ID, the total amount due, the official paid status,
     * the active status, the deleted timestamp, and notes.
     * </p>
     *
     * @return A string in the format:
     * "LineItem{ID=..., InvoiceID=..., ParticipantID=..., AmountDue=..., IsPaid=..., IsActive=..., DeletedAt=..., Notes=...}"
     */
    @Override
    public String toString() {
        return "LineItem{" +
                "invoiceItemID=" + getInvoiceItemID() +
                ", invoiceID=" + (getInvoice() != null ? getInvoice().getInvoiceID() : "null") +
                ", participantID=" + (getParticipant() != null ? getParticipant().getParticipantID() : "null") +
                ", amountDue=" + (getAmountDue() != null ? getAmountDue().toString() : "null") +
                ", isPaid=" + getIsPaid() +
                ", isActive=" + getIsActive() +
                ", deletedAt=" + getDeletedAt() + // Added new field
                ", notes='" + getNotes() + '\'' +
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
        LineItem that = (LineItem) o;
        // Equality is based on the primary key (invoiceItemID), safely handling null Integer
        return Objects.equals(getInvoiceItemID(), that.getInvoiceItemID());
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
        return Objects.hash(getInvoiceItemID());
    }
}