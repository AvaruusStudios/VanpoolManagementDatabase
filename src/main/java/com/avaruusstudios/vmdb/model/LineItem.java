package com.avaruusstudios.vmdb.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
 * and a timestamp for its creation date ({@code DateCreated TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP}).
 * It is designed to support data binding with JavaFX UI components, making it suitable for a responsive desktop application.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.2
 * Created On: 2025-07-11
 * Updated On: 2025-09-03 (Added DateCreated field)
 *
 * @see Invoice
 * @see Participant
 */
public class LineItem {
    /**
     * The unique numerical identifier for the line item. This serves as the primary key
     * in the database for line item records ({@code LineItemID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created line item not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable.
     * </p>
     */
    private final ReadOnlyObjectProperty<Integer> lineItemID;

    /**
     * The {@link Invoice} object to which this line item belongs.
     * This represents the foreign key relationship to the {@code Invoices} table.
     * This field is **required** (corresponds to {@code InvoiceID_FK INTEGER NOT NULL} in the database).
     */
    private final ObjectProperty<Invoice> invoice;

    /**
     * The {@link Participant} object associated with this line item.
     * This represents the foreign key relationship to the {@code Participants} table.
     * This field is **required** (corresponds to {@code ParticipantID_FK INTEGER NOT NULL} in the database).
     */
    private final ObjectProperty<Participant> participant;

    /**
     * The benefit payment amount for the line item.
     * This field is **required** (corresponds to {@code BenefitPayment NUMERIC NOT NULL} in the database)
     * and must be non-negative.
     */
    private final ObjectProperty<BigDecimal> benefitPayment;

    /**
     * The personal payment amount for the line item.
     * This field is **required** (corresponds to {@code PersonalPayment NUMERIC NOT NULL} in the database)
     * and must be non-negative.
     */
    private final ObjectProperty<BigDecimal> personalPayment;

    /**
     * A flag indicating whether this specific line item has been paid.
     * (corresponds to {@code IsPaid INTEGER NOT NULL DEFAULT 0} in the database).
     * `true` (1) for paid, `false` (0) for unpaid.
     */
    private final BooleanProperty isPaid;

    /**
     * A flag indicating whether the line item is currently active or has been logically deleted/deactivated.
     * (corresponds to {@code IsActive INTEGER NOT NULL DEFAULT 1} in the database).
     * `true` (1) for active, `false` (0) for inactive.
     */
    private final BooleanProperty isActive;

    /**
     * The timestamp when the line item was logically deleted or deactivated.
     * This field is optional and can be {@code null} if the line item is active.
     * Corresponds to {@code DeletedAt TEXT DEFAULT NULL} in the database.
     */
    private final ObjectProperty<LocalDateTime> deletedAt;

    /**
     * The timestamp indicating when the line item record was created.
     * This field is **required** (corresponds to {@code DateCreated TEXT DEFAULT CURRENT_TIMESTAMP} in the database).
     */
    private final ObjectProperty<LocalDateTime> dateCreated;

    /**
     * Optional free-form text for additional notes or administrative comments specific to this line item.
     * (corresponds to {@code Notes TEXT} in the database).
     */
    private final StringProperty notes;

    /**
     * Default constructor for creating an empty {@code LineItem} object.
     * The {@code lineItemID} is set to {@code null} to explicitly indicate that
     * this line item has not yet been assigned a unique ID by the database.
     * Initializes other properties to default values to ensure a stable state.
     * This constructor is primarily used by frameworks that instantiate objects
     * via reflection (e.g., ORMs, JSON deserializers) before populating their fields.
     */
    public LineItem() {
        this(null, null, null, BigDecimal.ZERO, BigDecimal.ZERO, false, true, null, null, null);
    }


    /**
     * Full constructor to initialize all fields of a {@code LineItem} instance.
     * This constructor is typically used when loading an *existing* line item
     * record from the database, where {@code lineItemID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param lineItemID The unique integer ID for the line item, typically assigned by the database. Must not be {@code null} for an existing line item.
     * @param invoice The {@link Invoice} object to which this line item belongs. Must not be {@code null}.
     * @param participant The {@link Participant} object associated with this line item. Must not be {@code null}.
     * @param benefitPayment The benefit payment amount. Must be non-negative.
     * @param personalPayment The personal payment amount. Must be non-negative.
     * @param isPaid The paid status of the line item (true for paid, false for unpaid).
     * @param isActive The active status of the line item (true for active, false for inactive/deleted).
     * @param deletedAt The {@link LocalDateTime} when the line item was logically deleted, or {@code null} if active.
     * @param dateCreated The {@link LocalDateTime} when the line item was created. Can be {@code null} for new records.
     * @param notes Any optional notes or additional information about the line item. Can be {@code null}.
     * @throws NullPointerException if any required parameters are {@code null}.
     * @throws IllegalArgumentException if payments are negative.
     */
    public LineItem(Integer lineItemID, Invoice invoice, Participant participant, BigDecimal benefitPayment,
                    BigDecimal personalPayment, Boolean isPaid, Boolean isActive, LocalDateTime deletedAt, LocalDateTime dateCreated, String notes) {
        // Initialize immutable ID property first
        this.lineItemID = new SimpleObjectProperty<>(this, "lineItemID", lineItemID);

        // Initialize mutable properties
        this.invoice = new SimpleObjectProperty<>(this, "invoice");
        this.participant = new SimpleObjectProperty<>(this, "participant");
        this.benefitPayment = new SimpleObjectProperty<>(this, "benefitPayment");
        this.personalPayment = new SimpleObjectProperty<>(this, "personalPayment");
        this.isPaid = new SimpleBooleanProperty(this, "isPaid");
        this.isActive = new SimpleBooleanProperty(this, "isActive");
        this.deletedAt = new SimpleObjectProperty<>(this, "deletedAt");
        this.dateCreated = new SimpleObjectProperty<>(this, "dateCreated");
        this.notes = new SimpleStringProperty(this, "notes");


        // Set properties with validation
        _setLineItemID(lineItemID); // Use package-private setter for ID to allow for nulls during creation
        setInvoice(invoice);
        setParticipant(participant);
        setBenefitPayment(benefitPayment);
        setPersonalPayment(personalPayment);
        setIsPaid(isPaid);
        setIsActive(isActive);
        setDeletedAt(deletedAt);
        _setDateCreated(dateCreated);
        setNotes(notes);
    }

    /**
     * Convenience constructor for creating a new {@code LineItem} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new line item record for **insertion** into the database.
     * The {@code lineItemID} is omitted as it is typically auto-generated by the database.
     * All parameters are validated via their respective setters.
     *
     * @param invoice The {@link Invoice} object to which this line item belongs. Must not be {@code null}.
     * @param participant The {@link Participant} object associated with this line item. Must not be {@code null}.
     * @param benefitPayment The benefit payment amount. Must be non-negative.
     * @param personalPayment The personal payment amount. Must be non-negative.
     * @param notes Any optional notes or additional information about the line item. Can be {@code null}.
     * @throws NullPointerException if any required parameters are {@code null}.
     * @throws IllegalArgumentException if payments are negative.
     */
    public LineItem(Invoice invoice, Participant participant, BigDecimal benefitPayment, BigDecimal personalPayment, String notes) {
        // Calls the full constructor with lineItemID as null, isPaid as false, isActive as true, deletedAt as null, and dateCreated as null
        this(null, invoice, participant, benefitPayment, personalPayment, false, true, null, null, notes);
    }


    // --- JavaFX Property Accessor Methods ---

    /**
     * Retrieves the read-only property for the line item's unique ID.
     * This property represents the {@code LineItemID} column in the database.
     * <p>
     * As this property is {@code ReadOnlyObjectProperty}, its value cannot be
     * changed directly after initial assignment, enforcing the immutability
     * of the primary key for persisted entities.
     * </p>
     *
     * @return The {@link ReadOnlyObjectProperty} for {@code lineItemID}.
     */
    public ReadOnlyObjectProperty<Integer> lineItemIDProperty() {
        return lineItemID;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the associated {@link Invoice} object.
     * This property represents the foreign key relationship to the {@code Invoices} table.
     *
     * @return The {@link ObjectProperty} for {@code invoice}.
     */
    public ObjectProperty<Invoice> invoiceProperty() {
        return invoice;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the associated {@link Participant} object.
     * This property represents the foreign key relationship to the {@code Participants} table.
     *
     * @return The {@link ObjectProperty} for {@code participant}.
     */
    public ObjectProperty<Participant> participantProperty() {
        return participant;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the benefit payment amount.
     * This property represents the {@code BenefitPayment} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code benefitPayment}.
     */
    public ObjectProperty<BigDecimal> benefitPaymentProperty() {
        return benefitPayment;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the personal payment amount.
     * This property represents the {@code PersonalPayment} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code personalPayment}.
     */
    public ObjectProperty<BigDecimal> personalPaymentProperty() {
        return personalPayment;
    }

    /**
     * Retrieves the {@link BooleanProperty} for the paid status of the line item.
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
    public ObjectProperty<LocalDateTime> deletedAtProperty() {
        return deletedAt;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the creation timestamp of the line item.
     * This property corresponds to the {@code DateCreated} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code dateCreated}.
     */
    public ObjectProperty<LocalDateTime> dateCreatedProperty() {
        return dateCreated;
    }

    /**
     * Retrieves the {@link StringProperty} for any optional notes associated with this line item.
     * This property corresponds to the {@code Notes} column in the database.
     *
     * @return The {@link StringProperty} for {@code notes}.
     */
    public StringProperty notesProperty() {
        return notes;
    }


    // --- Value Getters and Setters ---

    /**
     * Retrieves the unique identifier for this line item.
     * For new, unpersisted line items, this will be {@code null}.
     * Corresponds to the {@code LineItemID} column in the database.
     *
     * @return The {@link Integer} primary key used to identify this line item record, or {@code null} if not yet assigned.
     */
    public Integer getLineItemID() {
        return lineItemID.get();
    }

    /**
     * Sets the unique ID for this line item. This method is designed to be package-private
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
    public void _setLineItemID(Integer id) { // Package-private for DAO use only
        if (this.lineItemID.get() != null) {
            throw new IllegalStateException("LineItem ID cannot be changed once set.");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("LineItem ID cannot be null or non-positive.");
        }
        ((SimpleObjectProperty<Integer>) this.lineItemID).set(id);
    }

    /**
     * Retrieves the {@link Invoice} object to which this line item belongs.
     *
     * @return The associated {@link Invoice} object.
     */
    public Invoice getInvoice() {
        return invoice.get();
    }

    /**
     * Sets the {@link Invoice} object to which this line item belongs.
     *
     * @param invoice The {@link Invoice} object to associate with this line item. Must not be {@code null}.
     * @throws NullPointerException if {@code invoice} is {@code null}.
     */
    public void setInvoice(Invoice invoice) {
        this.invoice.set(Objects.requireNonNull(invoice, "Invoice cannot be null."));
    }

    /**
     * Retrieves the {@link Participant} object associated with this line item.
     *
     * @return The associated {@link Participant} object.
     */
    public Participant getParticipant() {
        return participant.get();
    }

    /**
     * Sets the {@link Participant} object associated with this line item.
     *
     * @param participant The {@link Participant} object to associate with this line item. Must not be {@code null}.
     * @throws NullPointerException if {@code participant} is {@code null}.
     */
    public void setParticipant(Participant participant) {
        this.participant.set(Objects.requireNonNull(participant, "Participant cannot be null."));
    }

    /**
     * Retrieves the benefit payment amount for the line item.
     *
     * @return The {@link BigDecimal} representing the benefit payment amount.
     */
    public BigDecimal getBenefitPayment() {
        return benefitPayment.get();
    }

    /**
     * Sets the benefit payment amount for the line item.
     *
     * @param benefitPayment The {@link BigDecimal} amount to set. Must be non-negative.
     * @throws NullPointerException     if {@code benefitPayment} is {@code null}.
     * @throws IllegalArgumentException if {@code benefitPayment} is negative.
     */
    public void setBenefitPayment(BigDecimal benefitPayment) {
        Objects.requireNonNull(benefitPayment, "Benefit payment cannot be null.");
        if (benefitPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Benefit payment cannot be negative. Provided: " + benefitPayment);
        }
        this.benefitPayment.set(benefitPayment);
    }

    /**
     * Retrieves the personal payment amount for the line item.
     *
     * @return The {@link BigDecimal} representing the personal payment amount.
     */
    public BigDecimal getPersonalPayment() {
        return personalPayment.get();
    }

    /**
     * Sets the personal payment amount for the line item.
     *
     * @param personalPayment The {@link BigDecimal} amount to set. Must be non-negative.
     * @throws NullPointerException     if {@code personalPayment} is {@code null}.
     * @throws IllegalArgumentException if {@code personalPayment} is negative.
     */
    public void setPersonalPayment(BigDecimal personalPayment) {
        Objects.requireNonNull(personalPayment, "Personal payment cannot be null.");
        if (personalPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Personal payment cannot be negative. Provided: " + personalPayment);
        }
        this.personalPayment.set(personalPayment);
    }

    /**
     * Retrieves the paid status of the line item.
     *
     * @return {@code true} if the line item has been paid, {@code false} otherwise.
     */
    public boolean getIsPaid() {
        return isPaid.get();
    }

    /**
     * Sets the paid status of the line item.
     *
     * @param isPaid {@code true} to mark the line item as paid, {@code false} otherwise.
     */
    public void setIsPaid(boolean isPaid) {
        this.isPaid.set(isPaid);
    }

    /**
     * Retrieves the active status of the line item.
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
    public LocalDateTime getDeletedAt() {
        return deletedAt.get();
    }

    /**
     * Sets the timestamp when the line item was logically deleted or deactivated.
     *
     * @param deletedAt The {@link LocalDateTime} to set as the deletion timestamp. Can be {@code null}.
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt.set(deletedAt);
    }

    /**
     * Retrieves the timestamp when the line item record was created.
     *
     * @return The {@link LocalDateTime} of creation.
     */
    public LocalDateTime getDateCreated() {
        return dateCreated.get();
    }

    /**
     * Sets the timestamp when the line item record was created.
     * <p>
     * This method is designed for use by the DAO when reading a record from the database.
     * The value is immutable once set.
     * </p>
     *
     * @param dateCreated The {@link LocalDateTime} to set as the creation timestamp.
     */
    public void _setDateCreated(LocalDateTime dateCreated) {
        if (this.dateCreated.get() != null) {
            throw new IllegalStateException("Date created cannot be changed once set.");
        }
        if (dateCreated == null) {
            throw new IllegalArgumentException("Date created cannot be null.");
        }
        ((SimpleObjectProperty<LocalDateTime>) this.dateCreated).set(dateCreated);
    }

    /**
     * Retrieves any additional notes or administrative comments for the line item.
     * Corresponds to the {@code Notes} column in the database.
     *
     * @return The notes string, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes.get();
    }

    /**
     * Sets additional notes or internal comments for this line item.
     * If the provided notes are not {@code null}, leading and trailing whitespace will be stripped.
     * Corresponds to the {@code Notes} column in the database.
     *
     * @param notes The string containing notes to set. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes.set((notes != null) ? notes.strip() : null);
    }

    // --- Utility Methods ---

    /**
     * <p>
     * Returns a string representation of the {@code LineItem} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the line item's key attributes.
     * </p>
     * <p>
     * The format includes the line item ID, invoice ID, participant ID,
     * benefit and personal payment amounts, paid and active status, and notes.
     * </p>
     *
     * @return A string in the format:
     * "LineItem{lineItemID=..., invoiceID=..., participantID=..., benefitPayment=..., personalPayment=..., isPaid=..., isActive=..., deletedAt=..., dateCreated=..., notes=...}"
     */
    @Override
    public String toString() {
        return "LineItem{" +
                "lineItemID=" + getLineItemID() +
                ", invoiceID=" + (getInvoice() != null ? getInvoice().getInvoiceID() : "null") +
                ", participantID=" + (getParticipant() != null ? getParticipant().getParticipantID() : "null") +
                ", benefitPayment=" + getBenefitPayment() +
                ", personalPayment=" + getPersonalPayment() +
                ", isPaid=" + getIsPaid() +
                ", isActive=" + getIsActive() +
                ", deletedAt=" + getDeletedAt() +
                ", dateCreated=" + getDateCreated() +
                ", notes='" + getNotes() + '\'' +
                '}';
    }

    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based primarily on the unique {@code lineItemID}.
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
        return Objects.equals(getLineItemID(), that.getLineItemID());
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
        return Objects.hash(getLineItemID());
    }
}