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
 * and a timestamp for its creation date ({@code DateCreated TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP}).
 * It is designed to support data binding with JavaFX UI components, making it suitable for a responsive desktop application.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.2
 * Created On: 2025-07-11
 * Updated On: 2025-09-03
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
     * The timestamp indicating when the line item record was created.
     * This field is **required** (corresponds to {@code DateCreated TEXT DEFAULT CURRENT_TIMESTAMP} in the database).
     */
    private final ObjectProperty<LocalDateTime> dateCreated;

    /**
     * Flag indicating if the line item is currently active ({@code true}) or has been soft-deleted ({@code false}).
     * Corresponds to the `IsActive` column in the database ({@code INTEGER NOT NULL DEFAULT 1}).
     */
    private final BooleanProperty isActive;

    /**
     * The date and **time** when the line item was soft-deleted.
     * This field is {@code null} if the line item is active, and uses {@link LocalDateTime} for precise deletion timestamp.
     * Corresponds to the `DeletedAt` column in the database ({@code TEXT DEFAULT NULL}).
     */
    private final ObjectProperty<LocalDateTime> deletedAt;

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
        this(null, null, null, BigDecimal.ZERO, BigDecimal.ZERO, false, null, true, null, "");
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
     * @param dateCreated The {@link LocalDateTime} when the line item was created. Can be {@code null} for new records.
     * @param notes Any optional notes or additional information about the line item. Can be {@code null}.
     * @throws NullPointerException if any required parameters are {@code null}.
     * @throws IllegalArgumentException if payments are negative.
     */
    public LineItem(Integer lineItemID, Invoice invoice, Participant participant, BigDecimal benefitPayment,
                    BigDecimal personalPayment, Boolean isPaid, LocalDateTime dateCreated, boolean isActive, LocalDateTime deletedAt, String notes) {
        // Initialize immutable ID property first
        this.lineItemID = new SimpleObjectProperty<>(this, "lineItemID", lineItemID);

        // Initialize mutable properties
        this.invoice = new SimpleObjectProperty<>(this, "invoice");
        this.participant = new SimpleObjectProperty<>(this, "participant");
        this.benefitPayment = new SimpleObjectProperty<>(this, "benefitPayment");
        this.personalPayment = new SimpleObjectProperty<>(this, "personalPayment");
        this.isPaid = new SimpleBooleanProperty(this, "isPaid");
        this.dateCreated = new SimpleObjectProperty<>(this, "dateCreated");
        this.isActive = new SimpleBooleanProperty(this, "isActive", isActive);
        this.deletedAt = new SimpleObjectProperty<>(this, "deletedAt", deletedAt);
        this.notes = new SimpleStringProperty(this, "notes");

        // Set properties with validation
        _setLineItemID(lineItemID); // Use public setter for ID to allow for nulls during creation
        setInvoice(invoice);
        setParticipant(participant);
        setBenefitPayment(benefitPayment);
        setPersonalPayment(personalPayment);
        setIsPaid(isPaid);
        _setDateCreated(dateCreated);
        setIsActive(isActive);
        setDeletedAt(deletedAt);
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
        this(null, invoice, participant, benefitPayment, personalPayment, false, null, true, null, notes);
    }

    // --- JavaFX Property Accessor Methods ---

    /**
     * Retrieves the read-only JavaFX {@link ReadOnlyObjectProperty} for the line item's unique ID.
     * This property is typically used for data binding in UI components where the ID should not be changed.
     *
     * @return The {@link ReadOnlyObjectProperty} for the line item ID.
     */
    public ReadOnlyObjectProperty<Integer> lineItemIDProperty() {
        return lineItemID;
    }

    /**
     * Retrieves the JavaFX {@link ObjectProperty} for the associated {@link Invoice}.
     * This property allows for data binding to the line item's parent invoice.
     *
     * @return The {@link ObjectProperty} for the invoice.
     */
    public ObjectProperty<Invoice> invoiceProperty() {
        return invoice;
    }

    /**
     * Retrieves the JavaFX {@link ObjectProperty} for the associated {@link Participant}.
     * This property allows for data binding to the line item's participant.
     *
     * @return The {@link ObjectProperty} for the participant.
     */
    public ObjectProperty<Participant> participantProperty() {
        return participant;
    }

    /**
     * Retrieves the JavaFX {@link ObjectProperty} for the benefit payment amount.
     * This property allows for data binding to the benefit payment.
     *
     * @return The {@link ObjectProperty} for the benefit payment amount.
     */
    public ObjectProperty<BigDecimal> benefitPaymentProperty() {
        return benefitPayment;
    }

    /**
     * Retrieves the JavaFX {@link ObjectProperty} for the personal payment amount.
     * This property allows for data binding to the personal payment.
     *
     * @return The {@link ObjectProperty} for the personal payment amount.
     */
    public ObjectProperty<BigDecimal> personalPaymentProperty() {
        return personalPayment;
    }

    /**
     * Retrieves the JavaFX {@link BooleanProperty} for the paid status.
     * This property allows for data binding to the paid status flag.
     *
     * @return The {@link BooleanProperty} for the paid status.
     */
    public BooleanProperty isPaidProperty() {
        return isPaid;
    }

    /**
     * Retrieves the JavaFX {@link ObjectProperty} for the creation timestamp.
     * This is a read-only property to ensure the creation date cannot be changed.
     *
     * @return The {@link ObjectProperty} for the creation date.
     */
    public ObjectProperty<LocalDateTime> dateCreatedProperty() {
        return dateCreated;
    }

    /**
     * Retrieves the {@link BooleanProperty} indicating if the line item is active.
     * This property supports the soft-delete mechanism.
     *
     * @return The {@link BooleanProperty} for `isActive`.
     */
    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the date and time when the line item was soft-deleted.
     *
     * @return The {@link ObjectProperty} for {@code deletedAt}, a {@link LocalDateTime}.
     */
    public ObjectProperty<LocalDateTime> deletedAtProperty() {
        return deletedAt;
    }

    /**
     * Retrieves the JavaFX {@link StringProperty} for the line item's notes.
     * This property allows for data binding to the notes field.
     *
     * @return The {@link StringProperty} for the notes.
     */
    public StringProperty notesProperty() {
        return notes;
    }

    // --- Value Getters and Setters ---

    /**
     * Retrieves the unique identifier of the line item.
     *
     * @return The integer ID of the line item, or {@code null} if not yet persisted.
     */
    public Integer getLineItemID() {
        return lineItemID.get();
    }

    /**
     * <p>
     * Sets the unique identifier for the line item. This method is public and intended for
     * use by the Data Access Layer (DAO) when an object is loaded from or saved to the database.
     * </p>
     * <p>
     * This method follows the convention of a leading underscore to indicate its special purpose:
     * it should only be called once when an object's ID is assigned, and it enforces that
     * the ID cannot be changed once set.
     * </p>
     *
     * @param id The integer ID to be set.
     * @throws IllegalStateException if the ID has already been set.
     * @throws IllegalArgumentException if the ID is null or non-positive.
     */
    public void _setLineItemID(Integer id) { // Public for DAO use
        if (this.lineItemID.get() != null) {
            throw new IllegalStateException("LineItem ID cannot be changed once set.");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("LineItem ID cannot be null or non-positive.");
        }
        ((SimpleObjectProperty<Integer>) this.lineItemID).set(id);
    }

    /**
     * Retrieves the {@link Invoice} associated with this line item.
     *
     * @return The {@link Invoice} object.
     */
    public Invoice getInvoice() {
        return invoice.get();
    }

    /**
     * Sets the associated {@link Invoice} for this line item.
     *
     * @param invoice The {@link Invoice} to set.
     * @throws NullPointerException if the invoice is {@code null}.
     */
    public void setInvoice(Invoice invoice) {
        this.invoice.set(Objects.requireNonNull(invoice, "Invoice cannot be null."));
    }

    /**
     * Retrieves the {@link Participant} associated with this line item.
     *
     * @return The {@link Participant} object.
     */
    public Participant getParticipant() {
        return participant.get();
    }

    /**
     * Sets the associated {@link Participant} for this line item.
     *
     * @param participant The {@link Participant} to set.
     * @throws NullPointerException if the participant is {@code null}.
     */
    public void setParticipant(Participant participant) {
        this.participant.set(Objects.requireNonNull(participant, "Participant cannot be null."));
    }

    /**
     * Retrieves the benefit payment amount for the line item.
     *
     * @return The {@link BigDecimal} benefit payment.
     */
    public BigDecimal getBenefitPayment() {
        return benefitPayment.get();
    }

    /**
     * Sets the benefit payment amount for this line item.
     *
     * @param benefitPayment The {@link BigDecimal} amount to set.
     * @throws NullPointerException if the payment is {@code null}.
     * @throws IllegalArgumentException if the payment amount is negative.
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
     * @return The {@link BigDecimal} personal payment.
     */
    public BigDecimal getPersonalPayment() {
        return personalPayment.get();
    }

    /**
     * Sets the personal payment amount for this line item.
     *
     * @param personalPayment The {@link BigDecimal} amount to set.
     * @throws NullPointerException if the payment is {@code null}.
     * @throws IllegalArgumentException if the payment amount is negative.
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
     * @return {@code true} if the line item is paid, {@code false} otherwise.
     */
    public boolean getIsPaid() {
        return isPaid.get();
    }

    /**
     * Sets the paid status for this line item.
     *
     * @param isPaid The boolean paid status to set.
     */
    public void setIsPaid(boolean isPaid) {
        this.isPaid.set(isPaid);
    }

    /**
     * Retrieves the creation date of the line item record.
     *
     * @return The {@link LocalDateTime} of creation.
     */
    public LocalDateTime getDateCreated() {
        return dateCreated.get();
    }

    /**
     * <p>
     * Sets the creation date for the line item. This method is public and intended for
     * use by the Data Access Layer (DAO) when an object is loaded from or saved to the database.
     * </p>
     * <p>
     * This method follows the convention of a leading underscore to indicate its special purpose:
     * it should only be called once when an object's date is assigned, and it enforces that
     * the date cannot be changed once set.
     * </p>
     *
     * @param dateCreated The {@link LocalDateTime} to be set.
     * @throws IllegalStateException if the date created has already been set.
     * @throws IllegalArgumentException if the date created is null.
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
     * Retrieves the active status of the line item.
     *
     * @return `true` if the line item is active, `false` if it has been soft-deleted.
     */
    public boolean getIsActive() {
        return isActive.get();
    }

    /**
     * Sets the active status of the line item.
     *
     * @param isActive The boolean value indicating active status.
     */
    public void setIsActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    /**
     * Retrieves the date and time when the line item was soft-deleted.
     *
     * @return The {@link LocalDateTime} when the line item was deleted, or {@code null} if it is active.
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt.get();
    }

    /**
     * Sets the date and time when the line item was soft-deleted.
     *
     * @param deletedAt The {@link LocalDateTime} to set, or {@code null}.
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt.set(deletedAt);
    }

    /**
     * Retrieves the notes associated with the line item.
     *
     * @return The notes as a {@link String}, or {@code null} if no notes exist.
     */
    public String getNotes() {
        return notes.get();
    }

    /**
     * Sets the notes for the line item.
     *
     * @param notes The notes {@link String} to set. Can be {@code null}.
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
     * benefit and personal payment amounts, paid and notes.
     * </p>
     *
     * @return A string in the format:
     * "LineItem{lineItemID=..., invoiceID=..., participantID=..., benefitPayment=..., personalPayment=..., isPaid=..., dateCreated=..., notes=...}"
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