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
 * {@link InvoiceType}, its {@link PaymentStatus}, any relevant notes, its active status for soft deletion,
 * and an immutable timestamp for its creation date.
 * This object structure directly reflects the schema used for the {@code Invoices} table in the SQLite database to track billing records, where
 * {@code VehicleID_FK} and {@code InvoiceType} are now represented by embedded objects.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.2
 * Created On: 2025-07-11
 * Updated On: 2025-09-02 (Added DateCreated field)
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
    private final ObjectProperty<Vehicle> vehicle; // Reordered to match schema
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
     * Indicates whether the invoice is currently active or has been logically deleted/deactivated.
     * (corresponds to {@code IsActive INTEGER NOT NULL DEFAULT 1} in the database).
     * `true` (1) for active, `false` (0) for inactive.
     */
    private final BooleanProperty isActive;
    /**
     * The timestamp when the invoice was logically deleted or deactivated.
     * This field is optional and can be {@code null} if the invoice is active.
     * Corresponds to {@code DeletedAt TEXT DEFAULT NULL} in the database.
     */
    private final ObjectProperty<LocalDateTime> deletedAt;
    /**
     * The timestamp indicating when the invoice record was created.
     * This field is **required** (corresponds to {@code DateCreated TEXT DEFAULT CURRENT_TIMESTAMP} in the database).
     */
    private final ObjectProperty<LocalDateTime> dateCreated;
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
        // Default to active, notes as empty string, deletedAt as null, paymentStatus as UNPAID
        this(null, null, null, LocalDate.now(), LocalDate.now().plusDays(7), null, PaymentStatus.UNPAID, true, null, null, "");
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
     * @param isActive      The active status of the invoice (true for active, false for inactive/deleted).
     * @param deletedAt     The {@link LocalDateTime} when the invoice was logically deleted, or {@code null} if active.
     * @param dateCreated   The {@link LocalDateTime} when the invoice was created. Can be {@code null} for new records.
     * @param notes         Any optional notes or additional information about the invoice. Can be {@code null}.
     * @throws NullPointerException     if `invoiceID` (for existing invoices), `vehicle`, `invoiceType`, `invoiceDate`, `dueDate`, `periodLabel`, or `paymentStatus` are {@code null}.
     * @throws IllegalArgumentException if date or period label validations fail (e.g., invoice date in future, due date out of range, period label mismatch).
     * @throws IllegalStateException    if dependencies for `periodLabel` validation (like `invoiceType` or `invoiceDate`) are not set when `setPeriodLabel` is called.
     */
    public Invoice(Integer invoiceID, Vehicle vehicle, InvoiceType invoiceType, LocalDate invoiceDate,
                   LocalDate dueDate, YearMonth periodLabel, PaymentStatus paymentStatus, Boolean isActive, LocalDateTime deletedAt, LocalDateTime dateCreated, String notes) {
        // Initialize immutable ID property first
        this.invoiceID = new SimpleObjectProperty<>(this, "invoiceID", invoiceID);

        // Initialize mutable properties in schema order
        this.vehicle = new SimpleObjectProperty<>(this, "vehicle");
        this.invoiceType = new SimpleObjectProperty<>(this, "invoiceType");
        this.invoiceDate = new SimpleObjectProperty<>(this, "invoiceDate");
        this.dueDate = new SimpleObjectProperty<>(this, "dueDate");
        this.periodLabel = new SimpleObjectProperty<>(this, "periodLabel");
        this.paymentStatus = new SimpleObjectProperty<>(this, "paymentStatus"); // Initialize new property
        this.isActive = new SimpleBooleanProperty(this, "isActive");
        this.deletedAt = new SimpleObjectProperty<>(this, "deletedAt");
        this.dateCreated = new SimpleObjectProperty<>(this, "dateCreated");
        this.notes = new SimpleStringProperty(this, "notes");


        // Set dependencies first using their setters with validation
        _setInvoiceID(invoiceID); // Use package-private setter for ID to allow for nulls during creation
        setVehicle(vehicle);
        setInvoiceType(invoiceType);
        setInvoiceDate(invoiceDate);
        setDueDate(dueDate);
        // PeriodLabel depends on invoiceType and invoiceDate, so set after them
        setPeriodLabel(periodLabel);
        setPaymentStatus(paymentStatus); // Set new property
        setIsActive(isActive);
        setDeletedAt(deletedAt);
        _setDateCreated(dateCreated);
        setNotes(notes);
    }

    /**
     * Convenience constructor for creating a new {@code Invoice} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new invoice record for **insertion** into the database.
     * The {@code invoiceID} is omitted as it is typically auto-generated by the database.
     * All parameters are validated via their respective setters.
     *
     * @param vehicle       The {@link Vehicle} object associated with this invoice. Must not be {@code null}.
     * @param invoiceType   The {@link InvoiceType} of the invoice. Must not be {@code null}.
     * @param invoiceDate   The {@link LocalDate} when the invoice was generated (e.g., {@code 2024-05-15}). Must not be {@code null} and MUST BE THE CURRENT DATE for new invoices.
     * @param dueDate       The {@link LocalDate} by which the invoice is to be paid (e.g., {@code 2024-05-31}). Must not be {@code null}, must be in the future, and within 14 days.
     * @param periodLabel   The {@link YearMonth} representing the billing period of the invoice (e.g., {@code YearMonth.of(2024, 5)} for May 2024). Must not be {@code null} and must match derived value based on type/date.
     * @param notes         Any optional notes or additional information about the invoice. Can be {@code null}.
     * @throws NullPointerException     if `vehicle`, `invoiceType`, `invoiceDate`, `dueDate`, `periodLabel` are {@code null}.
     * @throws IllegalArgumentException if date or period label validations fail, or if `invoiceDate` is not the current date for new invoices.
     * @throws IllegalStateException    if dependencies for `periodLabel` validation (like `invoiceType` or `invoiceDate`) are not set when `setPeriodLabel` is called.
     */
    public Invoice(Vehicle vehicle, InvoiceType invoiceType, LocalDate invoiceDate,
                   LocalDate dueDate, YearMonth periodLabel, String notes) {
        // Calls the full constructor with invoiceID as null, isActive as true, deletedAt as null, dateCreated as null, and paymentStatus as UNPAID (default for new entities)
        this(null, vehicle, invoiceType, invoiceDate, dueDate, periodLabel, PaymentStatus.UNPAID, true, null, null, notes);

        // Additional Rule for convenience constructor: InvoiceDate MUST BE == CURRENT DATE at time of creation for new invoices.
        Objects.requireNonNull(invoiceDate, "Invoice date cannot be null for new invoice."); // Redundant but for clarity
        if (!getInvoiceDate().equals(LocalDate.now())) { // Access via getter as setter already applied
            throw new IllegalArgumentException("For new invoices, invoice date must be the current date (" + LocalDate.now() + ").");
        }
    }

    // --- JavaFX Property Accessor Methods ---

    /**
     * Retrieves the read-only property for the invoice's unique ID.
     * This property represents the {@code InvoiceID} column in the database.
     * <p>
     * As this property is {@code ReadOnlyObjectProperty}, its value cannot be
     * changed directly after initial assignment, enforcing the immutability
     * of the primary key for persisted entities.
     * </p>
     *
     * @return The {@link ReadOnlyObjectProperty} for {@code invoiceID}.
     */
    public ReadOnlyObjectProperty<Integer> invoiceIDProperty() {
        return invoiceID;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the {@link Vehicle} associated with this invoice.
     * This property represents the foreign key relationship to the {@code Vehicles} table.
     *
     * @return The {@link ObjectProperty} for {@code vehicle}.
     */
    public ObjectProperty<Vehicle> vehicleProperty() {
        return vehicle;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the invoice's type.
     * This property represents the {@code InvoiceType} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code invoiceType}.
     */
    public ObjectProperty<InvoiceType> invoiceTypeProperty() {
        return invoiceType;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the invoice's generation date.
     * This property holds a {@link LocalDate} value and corresponds to the {@code InvoiceDate} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code invoiceDate}.
     */
    public ObjectProperty<LocalDate> invoiceDateProperty() {
        return invoiceDate;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the invoice's due date.
     * This property holds a {@link LocalDate} value and corresponds to the {@code DueDate} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code dueDate}.
     */
    public ObjectProperty<LocalDate> dueDateProperty() {
        return dueDate;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the invoice's billing period.
     * This property holds a {@link YearMonth} value and corresponds to the {@code PeriodLabel} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code periodLabel}.
     */
    public ObjectProperty<YearMonth> periodLabelProperty() {
        return periodLabel;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the invoice's payment status.
     * This property holds a {@link PaymentStatus} value and corresponds to the {@code PaymentStatus} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code paymentStatus}.
     */
    public ObjectProperty<PaymentStatus> paymentStatusProperty() {
        return paymentStatus;
    }

    /**
     * Retrieves the {@link BooleanProperty} for the active status of the invoice.
     * This property corresponds to the {@code IsActive} column in the database.
     *
     * @return The {@link BooleanProperty} for {@code isActive}.
     */
    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the deletion timestamp of the invoice.
     * This property corresponds to the {@code DeletedAt} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code deletedAt}.
     */
    public ObjectProperty<LocalDateTime> deletedAtProperty() {
        return deletedAt;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the creation timestamp of the invoice.
     * This property corresponds to the {@code DateCreated} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code dateCreated}.
     */
    public ObjectProperty<LocalDateTime> dateCreatedProperty() {
        return dateCreated;
    }

    /**
     * Retrieves the {@link StringProperty} for any optional notes associated with this invoice.
     * This property corresponds to the {@code Notes} column in the database.
     *
     * @return The {@link StringProperty} for {@code notes}.
     */
    public StringProperty notesProperty() {
        return notes;
    }


    // --- Value Getters and Setters ---

    /**
     * Retrieves the unique identifier for this invoice.
     * For new, unpersisted invoices, this will be {@code null}.
     * Corresponds to the {@code InvoiceID} column in the database.
     *
     * @return The {@link Integer} primary key used to identify this invoice record, or {@code null} if not yet assigned.
     */
    public Integer getInvoiceID() {
        return invoiceID.get();
    }

    /**
     * Sets the unique ID for this invoice. This method is designed to be package-private
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
    public void _setInvoiceID(Integer id) { // Package-private for DAO use only
        if (this.invoiceID.get() != null) {
            throw new IllegalStateException("Invoice ID cannot be changed once set.");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invoice ID cannot be null or non-positive.");
        }
        ((SimpleObjectProperty<Integer>) this.invoiceID).set(id);
    }

    /**
     * Retrieves the {@link Vehicle} object associated with this invoice.
     * This represents the foreign key relationship to the {@code Vehicles} table.
     *
     * @return The associated {@link Vehicle} object.
     */
    public Vehicle getVehicle() {
        return vehicle.get();
    }

    /**
     * Sets the {@link Vehicle} object associated with this invoice.
     * This links the invoice to a specific vehicle.
     *
     * @param vehicle The {@link Vehicle} object to associate with this invoice. Must not be {@code null}.
     * @throws NullPointerException if {@code vehicle} is {@code null}.
     */
    public void setVehicle(Vehicle vehicle) {
        this.vehicle.set(Objects.requireNonNull(vehicle, "Vehicle cannot be null."));
    }

    /**
     * Retrieves the type of the invoice.
     * Corresponds to the {@code InvoiceType} column in the database.
     *
     * @return The {@link InvoiceType} of the invoice.
     */
    public InvoiceType getInvoiceType() {
        return invoiceType.get();
    }

    /**
     * Sets the type of the invoice. This field is required and influences the
     * calculation/validation of the {@link #getPeriodLabel()}.
     *
     * @param invoiceType The {@link InvoiceType} to set. Must not be {@code null}.
     * @throws NullPointerException if {@code invoiceType} is {@code null}.
     */
    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType.set(Objects.requireNonNull(invoiceType, "Invoice type cannot be null."));
    }

    /**
     * Retrieves the {@link LocalDate} when the invoice was generated.
     * Corresponds to the {@code InvoiceDate} column in the database.
     *
     * @return The {@link LocalDate} representing the invoice generation date.
     */
    public LocalDate getInvoiceDate() {
        return invoiceDate.get();
    }

    /**
     * Sets the {@link LocalDate} when the invoice was generated.
     * This date CANNOT be in the FUTURE.
     * For new invoices created via the convenience constructor, it must explicitly be the current date.
     *
     * @param invoiceDate The {@link LocalDate} to set as the invoice generation date. Must not be {@code null} and must not be in the future.
     * @throws NullPointerException     if {@code invoiceDate} is {@code null}.
     * @throws IllegalArgumentException if {@code invoiceDate} is in the future.
     */
    public void setInvoiceDate(LocalDate invoiceDate) {
        Objects.requireNonNull(invoiceDate, "Invoice date cannot be null.");
        // Rule: Date CANNOT be in the FUTURE.
        if (invoiceDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invoice date cannot be in the future. Provided: " + invoiceDate + ", Current Date: " + LocalDate.now());
        }
        this.invoiceDate.set(invoiceDate);
    }

    /**
     * Retrieves the {@link LocalDate} representing the due date of the invoice.
     * Corresponds to the {@code DueDate} column in the database.
     *
     * @return The {@link LocalDate} by which the invoice payment is expected.
     */
    public LocalDate getDueDate() {
        return dueDate.get();
    }

    /**
     * Sets the {@link LocalDate} for the invoice's due date.
     * This indicates the date by which payment is expected.
     * <p>
     * Rule: DueDate MUST be in the FUTURE but not more than fourteen (14) days in the future.
     * </p>
     * Corresponds to the {@code DueDate} column in the database.
     *
     * @param dueDate The {@link LocalDate} to set as the invoice due date. Must not be {@code null}.
     * @throws NullPointerException     if {@code dueDate} is {@code null}.
     * @throws IllegalArgumentException if {@code dueDate} is not in the future, or is more than 14 days in the future.
     */
    public void setDueDate(LocalDate dueDate) {
        Objects.requireNonNull(dueDate, "Due date cannot be null.");
        LocalDate today = LocalDate.now();
        LocalDate maxDueDate = today.plusDays(14);

        // Rule: DueDate MUST be in the FUTURE
        if (!dueDate.isAfter(today)) {
            throw new IllegalArgumentException("Due date must be in the future. Provided: " + dueDate + ", Current Date: " + today);
        }
        // Rule: DueDate not more than fourteen (14) days in the future.
        if (dueDate.isAfter(maxDueDate)) {
            throw new IllegalArgumentException("Due date cannot be more than 14 days in the future. Max allowed: " + maxDueDate + ", Provided: " + dueDate);
        }
        this.dueDate.set(dueDate);
    }

    /**
     * Retrieves the billing period of the invoice as a {@link YearMonth} object.
     * Corresponds to the {@code PeriodLabel} column in the database.
     *
     * @return The {@link YearMonth} representing the billing period (e.g., June 2025).
     */
    public YearMonth getPeriodLabel() {
        return periodLabel.get();
    }

    /**
     * Sets the billing period of the invoice.
     * This {@link YearMonth} must conform to the rules based on {@link #getInvoiceType()} and {@link #getInvoiceDate()}:
     * <ul>
     * <li>If {@link #getInvoiceType()} is {@code LEASE}, {@code periodLabel} must be `invoiceDate`'s month + 1.</li>
     * <li>If {@link #getInvoiceType()} is {@code FUEL}, {@code periodLabel} must be `invoiceDate`'s month.</li>
     * </ul>
     *
     * @param periodLabel The {@link YearMonth} to set as the billing period. Must not be {@code null}.
     * @throws NullPointerException     if {@code periodLabel} is {@code null}.
     * @throws IllegalStateException    if {@code invoiceType} or {@code invoiceDate} are not set (i.e., are {@code null})
     * before calling this setter, as they are required for validation.
     * @throws IllegalArgumentException if {@code periodLabel} does not match the expected period based on
     * the current {@code invoiceType} and {@code invoiceDate}.
     */
    public void setPeriodLabel(YearMonth periodLabel) {
        Objects.requireNonNull(periodLabel, "Period label cannot be null.");

        // Validation for periodLabel depends on invoiceType and invoiceDate.
        // Ensure they are set before validating periodLabel.
        if (getInvoiceType() == null) {
            throw new IllegalStateException("InvoiceType must be set before validating PeriodLabel.");
        }
        if (getInvoiceDate() == null) {
            throw new IllegalStateException("InvoiceDate must be set before validating PeriodLabel.");
        }

        YearMonth expectedPeriod;
        switch (getInvoiceType()) {
            case LEASE:
                expectedPeriod = YearMonth.from(getInvoiceDate()).plusMonths(1);
                break;
            case FUEL:
                expectedPeriod = YearMonth.from(getInvoiceDate());
                break;
            case NONE: // Handle cases where invoiceType might be NONE
            default:
                throw new IllegalArgumentException("Cannot determine expected period: Invalid or 'NONE' InvoiceType provided: " + getInvoiceType());
        }

        if (!periodLabel.equals(expectedPeriod)) {
            throw new IllegalArgumentException("Period label " + periodLabel.format(DateTimeFormatter.ofPattern("MMM-yyyy")) +
                    " does not match expected period " + expectedPeriod.format(DateTimeFormatter.ofPattern("MMM-yyyy")) +
                    " for InvoiceType " + getInvoiceType() + " and InvoiceDate " + getInvoiceDate());
        }
        this.periodLabel.set(periodLabel);
    }

    /**
     * Retrieves the current payment status of the invoice.
     * Corresponds to the {@code PaymentStatus} column in the database.
     *
     * @return The {@link PaymentStatus} of the invoice.
     */
    public PaymentStatus getPaymentStatus() {
        return paymentStatus.get();
    }

    /**
     * Sets the payment status of the invoice. This field is required.
     *
     * @param paymentStatus The {@link PaymentStatus} to set. Must not be {@code null}.
     * @throws NullPointerException if {@code paymentStatus} is {@code null}.
     */
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus.set(Objects.requireNonNull(paymentStatus, "Payment status cannot be null."));
    }

    /**
     * Retrieves the active status of the invoice.
     *
     * @return {@code true} if the invoice is active, {@code false} if it's inactive/logically deleted.
     */
    public boolean getIsActive() {
        return isActive.get();
    }

    /**
     * Sets the active status of the invoice.
     *
     * @param isActive {@code true} to mark the invoice as active, {@code false} for inactive/logically deleted.
     */
    public void setIsActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    /**
     * Retrieves the timestamp when the invoice was logically deleted or deactivated.
     * Corresponds to the {@code DeletedAt} column in the database.
     *
     * @return The {@link LocalDateTime} of deletion, or {@code null} if the invoice is active.
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt.get();
    }

    /**
     * Sets the timestamp when the invoice was logically deleted or deactivated.
     *
     * @param deletedAt The {@link LocalDateTime} to set as the deletion timestamp. Can be {@code null}.
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt.set(deletedAt);
    }

    /**
     * Retrieves any additional notes or administrative comments for the participant.
     * Corresponds to the {@code Notes} column in the database.
     *
     * @return The notes string, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes.get();
    }

    /**
     * Sets additional notes or internal comments for this invoice.
     * If the provided notes are not {@code null}, leading and trailing whitespace will be stripped.
     * Corresponds to the {@code Notes} column in the database.
     *
     * @param notes The string containing notes to set. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes.set((notes != null) ? notes.strip() : null);
    }

    /**
     * Retrieves the timestamp when the invoice record was created.
     *
     * @return The {@link LocalDateTime} of creation.
     */
    public LocalDateTime getDateCreated() {
        return dateCreated.get();
    }

    /**
     * Sets the timestamp when the invoice record was created.
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

    // --- Utility Methods ---

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
        YearMonth currentPeriodLabel = getPeriodLabel();
        if (currentPeriodLabel == null) {
            return null;
        }
        return currentPeriodLabel.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
    }

    /**
     * <p>
     * Returns a string representation of the {@code Invoice} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the invoice's key attributes.
     * </p>
     * <p>
     * The format includes the invoice ID, vehicle ID, invoice date, due date,
     * formatted billing period, invoice type, payment status, active status, deleted timestamp, and notes.
     * </p>
     *
     * @return A string in the format:
     * "Invoice{invoiceID=..., vehicleID=..., invoiceType=..., invoiceDate=..., dueDate=..., periodLabel=..., paymentStatus=..., isActive=..., deletedAt=..., dateCreated=..., notes=...}"
     */
    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceID=" + getInvoiceID() +
                ", vehicleID=" + (getVehicle() != null ? getVehicle().getVehicleID() : "null") +
                ", invoiceType=" + getInvoiceType() +
                ", invoiceDate=" + getInvoiceDate() +
                ", dueDate=" + getDueDate() +
                ", periodLabel=" + getFormattedPeriodLabel() +
                ", paymentStatus=" + getPaymentStatus() + // Added new field
                ", isActive=" + getIsActive() +
                ", deletedAt=" + getDeletedAt() +
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