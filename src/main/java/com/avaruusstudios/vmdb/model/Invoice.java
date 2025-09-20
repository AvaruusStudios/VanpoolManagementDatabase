package com.avaruusstudios.vmdb.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * its due date, the billing period it covers, the associated {@link Vehicle} object, its
 * {@link InvoiceType}, its {@link PaymentStatus}, any relevant notes, and an immutable timestamp for its creation date.
 * This object structure directly reflects the schema used for the {@code Invoices} table in the SQLite database to track billing records, where
 * {@code VehicleID_FK} and {@code InvoiceType} are now represented by embedded objects.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.2
 * Created On: 2025-07-11
 * Updated On: 2025-09-03
 *
 * @see Vehicle
 * @see InvoiceType
 * @see PaymentStatus
 * @see LineItem
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
    private final ReadOnlyObjectProperty<Integer> invoiceID;

    /**
     * The {@link Vehicle} object with which this invoice is associated.
     * This represents the foreign key relationship to the {@code Vehicles} table.
     * This field is **required** (corresponds to {@code VehicleID_FK INTEGER NOT NULL} in the database).
     */
    private final ObjectProperty<Vehicle> vehicle;

    /**
     * The {@link InvoiceType} of the invoice (e.g., LEASE, FUEL).
     * This influences the calculation and validation of the {@link #periodLabelProperty()}.
     * This field is **required** (corresponds to {@code InvoiceType TEXT NOT NULL} in the database).
     */
    private final ObjectProperty<InvoiceType> invoiceType;

    /**
     * The {@link LocalDate} when the invoice was officially prepared or generated.
     * This field is **required**. For new invoices, this must be the current date.
     * For existing invoices, it cannot be in the future.
     * Corresponds to {@code InvoiceDate TEXT NOT NULL} in the database.
     */
    private final ObjectProperty<LocalDate> invoiceDate;

    /**
     * The {@link LocalDate} by which the invoice payment is expected.
     * This field is **required**. It must be in the future, but not more than fourteen (14) days in the future.
     * Corresponds to {@code DueDate TEXT NOT NULL} in the database.
     */
    private final ObjectProperty<LocalDate> dueDate;

    /**
     * The billing period of the invoice, represented as a {@link YearMonth} object.
     * This indicates the month and year for which the charges apply (e.g., "JUN-2025").
     * This field is **required** and its value is validated based on the {@link #invoiceTypeProperty()} and {@link #invoiceDateProperty()}.
     * Corresponds to {@code PeriodLabel TEXT NOT NULL} in the database.
     */
    private final ObjectProperty<YearMonth> periodLabel;

    /**
     * The current payment status of the invoice (e.g., UNPAID, PAID, OVERDUE).
     * This field is **required** (corresponds to {@code PaymentStatus TEXT NOT NULL} in the database).
     */
    private final ObjectProperty<PaymentStatus> paymentStatus;

    /**
     * The timestamp indicating when the invoice record was created.
     * This field is **required** (corresponds to {@code DateCreated TEXT DEFAULT CURRENT_TIMESTAMP} in the database).
     */
    private final ObjectProperty<LocalDateTime> dateCreated;

    /**
     * Flag indicating if the invoice is currently active ({@code true}) or has been soft-deleted ({@code false}).
     * Corresponds to the `IsActive` column in the database ({@code INTEGER NOT NULL DEFAULT 1}).
     */
    private final BooleanProperty isActive;

    /**
     * The date and **time** when the invoice was soft-deleted.
     * This field is {@code null} if the invoice is active, and uses {@link LocalDateTime} for precise deletion timestamp.
     * Corresponds to the `DeletedAt` column in the database ({@code TEXT DEFAULT NULL}).
     */
    private final ObjectProperty<LocalDateTime> deletedAt;

    /**
     * Optional free-form text for additional notes or administrative comments specific to this location.
     * (corresponds to {@code Notes TEXT} in the database).
     */
    private final StringProperty notes;


    /**
     * Default constructor for creating an empty {@code Invoice} object.
     * The {@code invoiceID} is set to {@code null} to explicitly indicate that
     * this invoice has not yet been assigned a unique ID by the database.
     * Initializes other properties to default/null values to ensure a stable state.
     * This constructor is primarily used by frameworks that instantiate objects
     * via reflection (e.g., ORMs, JSON deserializers) before populating their fields.
     */
    public Invoice() {
        // Default notes as empty string, paymentStatus as UNPAID
        this(null, null, null, LocalDate.now(), LocalDate.now().plusDays(7), null, PaymentStatus.UNPAID, null, true, null, "");
    }

    /**
     * Full constructor to initialize all fields of an {@code Invoice} instance.
     * This constructor is typically used when loading an *existing* invoice
     * record from the database, where {@code invoiceID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param invoiceID     The unique integer ID for the invoice, typically assigned by the database. Must not be {@code null} for an existing invoice.
     * @param vehicle       The {@link Vehicle} object associated with this invoice. Must not be {@code null}.
     * @param invoiceType   The {@link InvoiceType} of the invoice. Must not be {@code null}.
     * @param invoiceDate   The {@link LocalDate} when the invoice was generated (e.g., {@code 2024-05-15}). Must not be {@code null} and not in the future.
     * @param dueDate       The {@link LocalDate} by which the invoice is to be paid (e.g., {@code 2024-05-31}). Must not be {@code null}, must be in the future, and within 14 days.
     * @param periodLabel   The {@link YearMonth} representing the billing period of the invoice (e.g., {@code YearMonth.of(2024, 5)} for May 2024). Must not be {@code null} and must match derived value based on type/date.
     * @param paymentStatus The {@link PaymentStatus} of the invoice. Must not be {@code null}.
     * @param dateCreated   The {@link LocalDateTime} when the invoice was created. Can be {@code null} for new records.
     * @param notes         Any optional notes or additional information about the invoice. Can be {@code null}.
     */
    public Invoice(Integer invoiceID, Vehicle vehicle, InvoiceType invoiceType, LocalDate invoiceDate, LocalDate dueDate, YearMonth periodLabel,
                   PaymentStatus paymentStatus, LocalDateTime dateCreated, boolean isActive, LocalDateTime deletedAt, String notes) {
        // Initialize immutable ID property first
        this.invoiceID = new SimpleObjectProperty<>(this, "invoiceID", invoiceID);

        // Initialize mutable properties in schema order
        this.vehicle = new SimpleObjectProperty<>(this, "vehicle");
        this.invoiceType = new SimpleObjectProperty<>(this, "invoiceType");
        this.invoiceDate = new SimpleObjectProperty<>(this, "invoiceDate");
        this.dueDate = new SimpleObjectProperty<>(this, "dueDate");
        this.periodLabel = new SimpleObjectProperty<>(this, "periodLabel");
        this.paymentStatus = new SimpleObjectProperty<>(this, "paymentStatus");
        this.dateCreated = new SimpleObjectProperty<>(this, "dateCreated");
        this.isActive = new SimpleBooleanProperty(this, "isActive", isActive);
        this.deletedAt = new SimpleObjectProperty<>(this, "deletedAt", deletedAt);
        this.notes = new SimpleStringProperty(this, "notes");

        // Set properties with validation
        _setInvoiceID(invoiceID); // Use public setter for ID to allow for nulls during creation
        setVehicle(vehicle);
        setInvoiceType(invoiceType);
        setInvoiceDate(invoiceDate);
        setDueDate(dueDate);
        setPeriodLabel(periodLabel);
        setPaymentStatus(paymentStatus);
        _setDateCreated(dateCreated);
        setIsActive(isActive);
        setDeletedAt(deletedAt);
        setNotes(notes);
    }

    /**
     * Convenience constructor for creating a new {@code Invoice} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new invoice record for **insertion** into the database.
     * The {@code invoiceID}, `invoiceDate`, and `dateCreated` fields are omitted as they are typically auto-generated by the database or handled by the DAO.
     *
     * @param vehicle       The {@link Vehicle} object with which this invoice is associated. Must not be {@code null}.
     * @param invoiceType   The {@link InvoiceType} of the invoice. Must not be {@code null}.
     * @param dueDate       The {@link LocalDate} by which the invoice is to be paid (e.g., {@code 2024-05-31}). Must not be {@code null}, must be in the future, and within 14 days.
     * @param periodLabel   The {@link YearMonth} representing the billing period of the invoice (e.g., {@code YearMonth.of(2024, 5)} for May 2024). Must not be {@code null} and must match derived value based on type/date.
     * @param notes         Any optional notes or additional information about the invoice. Can be {@code null}.
     */
    public Invoice(Vehicle vehicle, InvoiceType invoiceType, LocalDate dueDate, YearMonth periodLabel, String notes) {
        // Calls the full constructor with invoiceID and dateCreated as null, invoiceDate as now, paymentStatus as UNPAID
        this(null, vehicle, invoiceType, LocalDate.now(), dueDate, periodLabel, PaymentStatus.UNPAID, null, true, null, notes);
    }

    // --- JavaFX Property Accessor Methods ---

    /**
     * Retrieves the read-only JavaFX {@link ReadOnlyObjectProperty} for the invoice's unique ID.
     * This property is typically used for data binding in UI components where the ID should not be changed.
     *
     * @return The {@link ReadOnlyObjectProperty} for the invoice ID.
     */
    public ReadOnlyObjectProperty<Integer> invoiceIDProperty() {
        return invoiceID;
    }

    /**
     * Retrieves the JavaFX {@link ObjectProperty} for the associated {@link Vehicle}.
     * This property allows for data binding to the invoice's vehicle.
     *
     * @return The {@link ObjectProperty} for the vehicle.
     */
    public ObjectProperty<Vehicle> vehicleProperty() {
        return vehicle;
    }

    /**
     * Retrieves the JavaFX {@link ObjectProperty} for the {@link InvoiceType}.
     * This property allows for data binding to the invoice's type.
     *
     * @return The {@link ObjectProperty} for the invoice type.
     */
    public ObjectProperty<InvoiceType> invoiceTypeProperty() {
        return invoiceType;
    }

    /**
     * Retrieves the JavaFX {@link ObjectProperty} for the invoice date.
     * This property allows for data binding to the invoice's creation date.
     *
     * @return The {@link ObjectProperty} for the invoice date.
     */
    public ObjectProperty<LocalDate> invoiceDateProperty() {
        return invoiceDate;
    }

    /**
     * Retrieves the JavaFX {@link ObjectProperty} for the due date.
     * This property allows for data binding to the invoice's due date.
     *
     * @return The {@link ObjectProperty} for the due date.
     */
    public ObjectProperty<LocalDate> dueDateProperty() {
        return dueDate;
    }

    /**
     * Retrieves the JavaFX {@link ObjectProperty} for the billing period label.
     * This property allows for data binding to the invoice's billing period.
     *
     * @return The {@link ObjectProperty} for the billing period label.
     */
    public ObjectProperty<YearMonth> periodLabelProperty() {
        return periodLabel;
    }

    /**
     * Retrieves the JavaFX {@link ObjectProperty} for the {@link PaymentStatus}.
     * This property allows for data binding to the invoice's payment status.
     *
     * @return The {@link ObjectProperty} for the payment status.
     */
    public ObjectProperty<PaymentStatus> paymentStatusProperty() {
        return paymentStatus;
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
     * Retrieves the {@link BooleanProperty} indicating if the invoice is active.
     * This property supports the soft-delete mechanism.
     *
     * @return The {@link BooleanProperty} for `isActive`.
     */
    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the date and time when the invoice was soft-deleted.
     *
     * @return The {@link ObjectProperty} for {@code deletedAt}, a {@link LocalDateTime}.
     */
    public ObjectProperty<LocalDateTime> deletedAtProperty() {
        return deletedAt;
    }

    /**
     * Retrieves the JavaFX {@link StringProperty} for the invoice's notes.
     * This property allows for data binding to the notes field.
     *
     * @return The {@link StringProperty} for the notes.
     */
    public StringProperty notesProperty() {
        return notes;
    }


    // --- Value Getters and Setters ---

    /**
     * Retrieves the unique identifier of the invoice.
     *
     * @return The integer ID of the invoice, or {@code null} if not yet persisted.
     */
    public Integer getInvoiceID() {
        return invoiceID.get();
    }

    /**
     * <p>
     * Sets the unique identifier for the invoice. This method is public and intended for
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
    public void _setInvoiceID(Integer id) {
        if (this.invoiceID.get() != null) {
            throw new IllegalStateException("Invoice ID cannot be changed once set.");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invoice ID cannot be null or non-positive.");
        }
        ((SimpleObjectProperty<Integer>) this.invoiceID).set(id);
    }

    /**
     * Retrieves the {@link Vehicle} associated with this invoice.
     *
     * @return The {@link Vehicle} object.
     */
    public Vehicle getVehicle() {
        return vehicle.get();
    }

    /**
     * Sets the associated {@link Vehicle} for this invoice.
     *
     * @param vehicle The {@link Vehicle} to set.
     * @throws NullPointerException if the vehicle is {@code null}.
     */
    public void setVehicle(Vehicle vehicle) {
        this.vehicle.set(Objects.requireNonNull(vehicle, "Vehicle cannot be null."));
    }

    /**
     * Retrieves the {@link InvoiceType} of the invoice.
     *
     * @return The {@link InvoiceType} object.
     */
    public InvoiceType getInvoiceType() {
        return invoiceType.get();
    }

    /**
     * Sets the {@link InvoiceType} for this invoice.
     *
     * @param invoiceType The {@link InvoiceType} to set.
     * @throws NullPointerException if the invoice type is {@code null}.
     */
    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType.set(Objects.requireNonNull(invoiceType, "Invoice type cannot be null."));
    }

    /**
     * Retrieves the date the invoice was prepared.
     *
     * @return The {@link LocalDate} of preparation.
     */
    public LocalDate getInvoiceDate() {
        return invoiceDate.get();
    }

    /**
     * Sets the date the invoice was prepared.
     *
     * @param invoiceDate The {@link LocalDate} to set.
     * @throws NullPointerException if the invoice date is {@code null}.
     */
    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate.set(Objects.requireNonNull(invoiceDate, "Invoice date cannot be null."));
    }

    /**
     * Retrieves the date the invoice is due.
     *
     * @return The {@link LocalDate} of the due date.
     */
    public LocalDate getDueDate() {
        return dueDate.get();
    }

    /**
     * Sets the date the invoice is due.
     *
     * @param dueDate The {@link LocalDate} to set.
     * @throws NullPointerException if the due date is {@code null}.
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate.set(Objects.requireNonNull(dueDate, "Due date cannot be null."));
    }

    /**
     * Retrieves the billing period of the invoice.
     *
     * @return The {@link YearMonth} object representing the billing period.
     */
    public YearMonth getPeriodLabel() {
        return periodLabel.get();
    }

    /**
     * Sets the billing period for the invoice.
     *
     * @param periodLabel The {@link YearMonth} to set.
     * @throws NullPointerException if the period label is {@code null}.
     */
    public void setPeriodLabel(YearMonth periodLabel) {
        this.periodLabel.set(Objects.requireNonNull(periodLabel, "Period label cannot be null."));
    }

    /**
     * Retrieves the {@link PaymentStatus} of the invoice.
     *
     * @return The {@link PaymentStatus} object.
     */
    public PaymentStatus getPaymentStatus() {
        return paymentStatus.get();
    }

    /**
     * Sets the {@link PaymentStatus} for this invoice.
     *
     * @param paymentStatus The {@link PaymentStatus} to set.
     * @throws NullPointerException if the payment status is {@code null}.
     */
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus.set(Objects.requireNonNull(paymentStatus, "Payment status cannot be null."));
    }

    /**
     * Retrieves the creation date of the invoice record.
     *
     * @return The {@link LocalDateTime} of creation.
     */
    public LocalDateTime getDateCreated() {
        return dateCreated.get();
    }

    /**
     * <p>
     * Sets the creation date for the invoice. This method is public and intended for
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
     * Retrieves the active status of the invoice.
     *
     * @return `true` if the invoice is active, `false` if it has been soft-deleted.
     */
    public boolean getIsActive() {
        return isActive.get();
    }

    /**
     * Sets the active status of the invoice.
     * <p>
     * Note: While this can be set directly, for a full soft-delete operation,
     * {@link #setDeletedAt(LocalDateTime)} should be updated concurrently.
     * </p>
     *
     * @param isActive The boolean value indicating active status.
     */
    public void setIsActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    /**
     * Retrieves the date and time when the invoice was soft-deleted.
     *
     * @return The {@link LocalDateTime} when the invoice was deleted, or {@code null} if it is active.
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt.get();
    }

    /**
     * Sets the date and time when the invoice was soft-deleted.
     * <p>
     * Setting this to a non-null value should be coupled with setting {@link #setIsActive(boolean)} to {@code false}.
     * </p>
     *
     * @param deletedAt The {@link LocalDateTime} to set, or {@code null}.
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt.set(deletedAt);
    }

    /**
     * Retrieves the notes associated with the invoice.
     *
     * @return The notes as a {@link String}, or {@code null} if no notes exist.
     */
    public String getNotes() {
        return notes.get();
    }

    /**
     * Sets the notes for the invoice.
     *
     * @param notes The notes {@link String} to set. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    // --- Utility Methods ---

    /**
     * <p>
     * Returns a string representation of the {@code Invoice} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the invoice's key attributes.
     * </p>
     * <p>
     * The format includes the invoice ID, vehicle ID, invoice date, due date, period,
     * type, and payment status.
     * </p>
     *
     * @return A string in the format:
     * "Invoice{invoiceID=..., vehicleID=..., invoiceDate=..., dueDate=..., periodLabel=..., invoiceType=..., paymentStatus=...}"
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        return "Invoice{" +
                "invoiceID=" + getInvoiceID() +
                ", vehicleID=" + (getVehicle() != null ? getVehicle().getVehicleID() : "null") +
                ", invoiceType=" + getInvoiceType() +
                ", invoiceDate=" + getInvoiceDate() +
                ", dueDate=" + getDueDate() +
                ", periodLabel='" + (getPeriodLabel() != null ? getPeriodLabel().format(formatter) : "null") + '\'' +
                ", paymentStatus=" + getPaymentStatus() +
                ", dateCreated=" + getDateCreated() +
                ", notes='" + getNotes() + '\'' +
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
        return Objects.equals(getInvoiceID(), invoice.getInvoiceID());
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
        return Objects.hash(getInvoiceID());
    }
}