package com.avaruusstudios.vmdb.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * <p>
 * Represents a single financial transaction within the Vanpool Management System.
 * Transactions record financial movements, such as expenses (fuel, maintenance),
 * income (participant payments), or credits.
 * </p>
 *
 * <p>
 * Each transaction is uniquely identified and includes details about the date,
 * the associated vehicle, the category it falls under (linking to participants if an income category),
 * the monetary amount, payment method, and an optional link to an invoice.
 * This class directly maps to the `Transactions` table in the SQLite database,
 * reflecting the updated schema where `ParticipantID_FK` is no longer directly present in this table.
 * </p>
 *
 * @see Vehicle
 * @see Category
 * @see Invoice
 * @see PaymentMethod
 */
public class Transaction {
    /**
     * Unique identifier for the transaction. This serves as the primary key
     * in the database for transaction records ({@code TransactionID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created transaction not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable.
     * </p>
     */
    private final Integer transactionID;
    /**
     * The date on which the transaction occurred.
     * This field is **required** (corresponds to {@code TransactionDate TEXT NOT NULL} in the database).
     */
    private LocalDate transactionDate;
    /**
     * The {@link Vehicle} object associated with this transaction.
     * This field is **required** (corresponds to {@code VehicleID_FK INTEGER NOT NULL} in the database).
     */
    private Vehicle vehicle;
    /**
     * The {@link Category} object that classifies this transaction.
     * This link is crucial for determining if the transaction is income, expense, or credit,
     * and indirectly linking to a participant if it's an income category.
     * This field is **required** (corresponds to {@code CategoryID_FK INTEGER NOT NULL} in the database).
     */
    private Category category;
    /**
     * The monetary amount of the transaction.
     * This field is **required** and stored as a {@link BigDecimal} for precision
     * (corresponds to {@code Amount REAL NOT NULL} in the database, but {@code REAL} is problematic for money).
     */
    private BigDecimal amount;
    /**
     * The method of payment used for this transaction (e.g., {@link PaymentMethod#VISA}).
     * This field is optional (corresponds to {@code PaymentMethod TEXT} in the database, where the enum's `dbValue` would be stored).
     */
    private PaymentMethod paymentMethod; // Changed type to PaymentMethod enum
    /**
     * The {@link Invoice} object this transaction is associated with, if any.
     * This field is optional (corresponds to {@code InvoiceID_FK INTEGER} in the database),
     * meaning not all transactions are linked to an invoice.
     */
    private Invoice invoice;
    /**
     * Optional notes or contextual information about the transaction.
     * This field is optional (corresponds to {@code Notes TEXT} in the database).
     */
    private String notes;

    /**
     * Default constructor for creating an empty {@code Transaction} object.
     * The {@code transactionID} is set to {@code null} to explicitly indicate that
     * this transaction has not yet been assigned a unique ID by the database.
     * The {@code amount} is initialized to {@link BigDecimal#ZERO}.
     * The {@code paymentMethod} is initialized to {@code null}.
     * This constructor is primarily used by frameworks that instantiate objects
     * via reflection (e.g., ORMs, JSON deserializers) before populating their fields.
     */
    public Transaction() {
        this.transactionID = null;
        this.amount = BigDecimal.ZERO;
        this.paymentMethod = null; // Initialize to null for optional enum
    }
    /**
     * Full constructor to initialize all fields of a {@code Transaction} instance.
     * This constructor is typically used when loading an *existing* transaction
     * record from the database, where {@code transactionID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param transactionID   The unique integer ID for the transaction, typically assigned by the database. Must not be {@code null}.
     * @param transactionDate The date of the transaction. Must not be {@code null}.
     * @param vehicle         The {@link Vehicle} object associated with this transaction. Must not be {@code null}.
     * @param category        The {@link Category} object that classifies this transaction. Must not be {@code null}.
     * @param amount          The monetary amount of the transaction. Must not be {@code null}.
     * @param paymentMethod   The {@link PaymentMethod} used for payment, e.g., {@link PaymentMethod#VISA}. Can be {@code null}.
     * @param invoice         The {@link Invoice} object linked to this transaction, or {@code null} if none.
     * @param notes           Any optional notes or additional information about the transaction. Can be {@code null}.
     * @throws NullPointerException if `transactionID`, `transactionDate`, `vehicle`, `category`, or `amount` are {@code null}.
     */
    public Transaction(Integer transactionID, LocalDate transactionDate, Vehicle vehicle, Category category,
                       BigDecimal amount, PaymentMethod paymentMethod, Invoice invoice, String notes) { // Changed PaymentMethodType to PaymentMethod
        this.transactionID = Objects.requireNonNull(transactionID, "Transaction ID cannot be null for an existing transaction.");
        setTransactionDate(transactionDate);
        setVehicle(vehicle);
        setCategory(category);
        setAmount(amount);
        setPaymentMethod(paymentMethod);
        setInvoice(invoice);
        setNotes(notes);
    }
    /**
     * Convenience constructor for creating a new {@code Transaction} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new transaction record for **insertion** into the database.
     * The {@code transactionID} is omitted as it is typically auto-generated by the database.
     * All parameters are validated via their respective setters.
     *
     * @param transactionDate The date of the transaction. Must not be {@code null}.
     * @param vehicle         The {@link Vehicle} object associated with this transaction. Must not be {@code null}.
     * @param category        The {@link Category} object that classifies this transaction. Must not be {@code null}.
     * @param amount          The monetary amount of the transaction. Must not be {@code null}.
     * @param paymentMethod   The {@link PaymentMethod} used for payment, e.g., {@link PaymentMethod#MASTER_CARD}. Can be {@code null}.
     * @param invoice         The {@link Invoice} object linked to this transaction, or {@code null} if none.
     * @param notes           Any optional notes or additional information about the transaction. Can be {@code null}.
     * @throws NullPointerException if `transactionDate`, `vehicle`, `category`, or `amount` are {@code null}.
     */
    public Transaction(LocalDate transactionDate, Vehicle vehicle, Category category,
                       BigDecimal amount, PaymentMethod paymentMethod, Invoice invoice, String notes) { // Changed PaymentMethodType to PaymentMethod
        this.transactionID = null;
        setTransactionDate(transactionDate);
        setVehicle(vehicle);
        setCategory(category);
        setAmount(amount);
        setPaymentMethod(paymentMethod);
        setInvoice(invoice);
        setNotes(notes);
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Retrieves the unique identifier for this transaction.
     * For new, unpersisted transactions, this will be {@code null}.
     * Corresponds to the {@code TransactionID} column in the database.
     *
     * @return The {@link Integer} primary key used to identify this transaction record, or {@code null} if not yet assigned.
     */
    public Integer getTransactionID() {
        return transactionID;
    }
    /**
     * Retrieves the date on which this transaction occurred.
     * Corresponds to the {@code TransactionDate} column in the database.
     *
     * @return The transaction date.
     */
    public LocalDate getTransactionDate() {
        return transactionDate;
    }
    /**
     * Sets the date on which this transaction occurred.
     *
     * @param transactionDate The transaction date to set. Must not be {@code null}.
     * @throws NullPointerException if {@code transactionDate} is {@code null}.
     */
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = Objects.requireNonNull(transactionDate, "Transaction date cannot be null.");
    }
    /**
     * Retrieves the {@link Vehicle} object associated with this transaction.
     * Corresponds to the {@code VehicleID_FK} column in the database.
     *
     * @return The associated {@link Vehicle} object.
     */
    public Vehicle getVehicle() {
        return vehicle;
    }
    /**
     * Sets the {@link Vehicle} object associated with this transaction.
     *
     * @param vehicle The {@link Vehicle} object to set. Must not be {@code null}.
     * @throws NullPointerException if {@code vehicle} is {@code null}.
     */
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = Objects.requireNonNull(vehicle, "Vehicle cannot be null.");
    }
    /**
     * Retrieves the {@link Category} object that classifies this transaction.
     * Corresponds to the {@code CategoryID_FK} column in the database.
     *
     * @return The associated {@link Category} object.
     */
    public Category getCategory() {
        return category;
    }
    /**
     * Sets the {@link Category} object that classifies this transaction.
     *
     * @param category The {@link Category} object to set. Must not be {@code null}.
     * @throws NullPointerException if {@code category} is {@code null}.
     */
    public void setCategory(Category category) {
        this.category = Objects.requireNonNull(category, "Category cannot be null.");
    }
    /**
     * Retrieves the monetary amount of this transaction.
     * Corresponds to the {@code Amount} column in the database.
     *
     * @return The transaction amount as a {@link BigDecimal}.
     */
    public BigDecimal getAmount() {
        return amount;
    }
    /**
     * Sets the monetary amount of this transaction.
     *
     * @param amount The transaction amount to set. Must not be {@code null}.
     * @throws NullPointerException if {@code amount} is {@code null}.
     */
    public void setAmount(BigDecimal amount) {
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null.");
    }
    /**
     * Retrieves the payment method used for this transaction.
     * Corresponds to the {@code PaymentMethod} column in the database.
     *
     * @return The {@link PaymentMethod} representing the payment method, or {@code null} if not specified.
     */
    public PaymentMethod getPaymentMethod() { // Changed return type to PaymentMethod
        return paymentMethod;
    }
    /**
     * Sets the payment method used for this transaction.
     *
     * @param paymentMethod The {@link PaymentMethod} to set. Can be {@code null}.
     */
    public void setPaymentMethod(PaymentMethod paymentMethod) { // Changed parameter type to PaymentMethod
        this.paymentMethod = paymentMethod;
    }
    /**
     * Retrieves the {@link Invoice} object this transaction is associated with.
     * Corresponds to the {@code InvoiceID_FK} column in the database.
     *
     * @return The associated {@link Invoice} object, or {@code null} if no invoice is linked.
     */
    public Invoice getInvoice() {
        return invoice;
    }
    /**
     * Sets the {@link Invoice} object this transaction is associated with.
     *
     * @param invoice The {@link Invoice} object to set. Can be {@code null}.
     */
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
    /**
     * Retrieves any optional notes or contextual information about the transaction.
     * Corresponds to the {@code Notes} column in the database.
     *
     * @return A string containing the notes, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes;
    }
    /**
     * Sets additional notes or contextual information for this transaction.
     * If the provided notes are not {@code null}, leading and trailing whitespace will be stripped.
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
     * Returns a string representation of the {@code Transaction} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the transaction's key attributes.
     * </p>
     * <p>
     * The format includes the transaction ID, date, associated vehicle and category IDs,
     * amount, and invoice ID (if any).
     * </p>
     *
     * @return A string in the format:
     * "Transaction{ID=..., Date=..., VehicleID=..., CategoryID=..., Amount=..., InvoiceID=...}"
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionID=" + transactionID +
                ", transactionDate=" + transactionDate +
                ", vehicleID=" + (vehicle != null ? vehicle.getVehicleID() : "null") +
                ", categoryID=" + (category != null ? category.getCategoryID() : "null") +
                ", amount=" + amount +
                ", paymentMethod=" + (paymentMethod != null ? paymentMethod.toString() : "null") +
                ", invoiceID=" + (invoice != null ? invoice.getInvoiceID() : "null") +
                '}';
    }
    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based primarily on the unique {@code transactionID}.
     * </p>
     * <p>
     * This method adheres to the general contract of the {@link Object#equals(Object)} method,
     * ensuring consistency with hash-based collections. It correctly handles cases where
     * {@code transactionID} might be {@code null} for unpersisted entities.
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        // Equality is based on the primary key (transactionID), safely handling null Integer
        return Objects.equals(transactionID, that.transactionID);
    }
    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap} and {@link java.util.HashSet}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code transactionID}. If {@code transactionID}
     * is {@code null} (for unpersisted entities), its hash code will be 0, as per {@link Objects#hash(Object...)}.
     * This ensures that objects considered equal by {@code equals} will have the same hash code,
     * fulfilling the contract between {@code equals} and {@code hashCode}.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(transactionID);
    }
}