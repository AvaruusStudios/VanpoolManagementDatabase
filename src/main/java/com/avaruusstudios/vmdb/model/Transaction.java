package com.avaruusstudios.vmdb.model;

import javafx.beans.property.*;
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
 * Each transaction is uniquely identified ({@code TransactionID INTEGER PRIMARY KEY AUTOINCREMENT})
 * and includes details about the associated category ({@code CategoryID_FK INTEGER NOT NULL}),
 * an optional link to an invoice ({@code InvoiceID_FK INTEGER}), the date ({@code TransactionDate TEXT NOT NULL}),
 * the monetary amount ({@code Amount NUMERIC NOT NULL}), the payment method ({@code PaymentMethod TEXT}),
 * and any contextual notes ({@code Notes TEXT}).
 * This class directly maps to the `Transactions` table in the SQLite database.
 * All properties are exposed as JavaFX Properties for UI binding.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.2
 * Created On: 2025-07-11
 * Updated On: 2025-07-12
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
     * Once assigned by the database, it becomes immutable and is exposed as a {@link ReadOnlyObjectProperty}.
     * </p>
     */
    private final ReadOnlyObjectProperty<Integer> transactionID;
    /**
     * The {@link Category} object that classifies this transaction.
     * This link is crucial for determining if the transaction is income, expense, or credit.
     * This field is **required** (corresponds to {@code CategoryID_FK INTEGER NOT NULL} in the database)
     * and is exposed as an {@link ObjectProperty} of {@link Category}.
     */
    private final ObjectProperty<Category> category;
    /**
     * The {@link Invoice} object this transaction is associated with, if any.
     * This field is optional (corresponds to {@code InvoiceID_FK INTEGER} in the database),
     * meaning not all transactions are linked to an invoice.
     * It is exposed as an {@link ObjectProperty} of {@link Invoice}.
     */
    private final ObjectProperty<Invoice> invoice;
    /**
     * The date on which the transaction occurred.
     * This field is **required** (corresponds to {@code TransactionDate TEXT NOT NULL} in the database)
     * and is exposed as an {@link ObjectProperty} of {@link LocalDate}.
     */
    private final ObjectProperty<LocalDate> transactionDate;
    /**
     * The monetary amount of the transaction.
     * This field is **required** and stored as a {@link BigDecimal} for precision
     * (corresponds to {@code Amount NUMERIC NOT NULL} in the database).
     * It is exposed as an {@link ObjectProperty} of {@link BigDecimal}.
     */
    private final ObjectProperty<BigDecimal> amount;
    /**
     * The method of payment used for this transaction (e.g., {@link PaymentMethod#VISA}).
     * This field is optional (corresponds to {@code PaymentMethod TEXT} in the database, where the enum's `dbValue` would be stored).
     * It is exposed as an {@link ObjectProperty} of {@link PaymentMethod}.
     */
    private final ObjectProperty<PaymentMethod> paymentMethod;
    /**
     * Optional notes or contextual information about the transaction.
     * This field is optional (corresponds to {@code Notes TEXT} in the database)
     * and is exposed as a {@link StringProperty}.
     */
    private final StringProperty notes;

    /**
     * Default constructor for creating an empty {@code Transaction} object.
     * The {@code transactionID} is set to {@code null} to explicitly indicate that
     * this transaction has not yet been assigned a unique ID by the database.
     * The {@code amount} is initialized to {@link BigDecimal#ZERO}.
     * Other properties are initialized to their default JavaFX Property values.
     * This constructor is primarily used by frameworks that instantiate objects
     * via reflection (e.g., ORMs, JSON deserializers) before populating their fields.
     */
    public Transaction() {
        // Initialize with default values for a new, unpersisted transaction
        this(null, new Category(), null, LocalDate.now(), BigDecimal.ZERO, null, "");
    }

    /**
     * Full constructor to initialize all fields of a {@code Transaction} instance.
     * This constructor is typically used when loading an *existing* transaction
     * record from the database, where {@code transactionID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param transactionID   The unique integer ID for the transaction, typically assigned by the database. Can be {@code null} for new transactions.
     * @param category        The {@link Category} object that classifies this transaction. Must not be {@code null}.
     * @param invoice         The {@link Invoice} object linked to this transaction, or {@code null} if none.
     * @param transactionDate The date of the transaction. Must not be {@code null}.
     * @param amount          The monetary amount of the transaction. Must not be {@code null}.
     * @param paymentMethod   The {@link PaymentMethod} used for payment. Can be {@code null}.
     * @param notes           Any optional notes or additional information about the transaction. Can be {@code null}.
     * @throws IllegalArgumentException if any mandatory argument is invalid (e.g., null).
     * @throws IllegalStateException    if `transactionID` is attempted to be changed once set.
     */
    public Transaction(Integer transactionID, Category category, Invoice invoice,
                       LocalDate transactionDate, BigDecimal amount, PaymentMethod paymentMethod, String notes) {
        this.transactionID = new SimpleObjectProperty<>(this, "transactionID", transactionID);
        this.category = new SimpleObjectProperty<>(this, "category");
        this.invoice = new SimpleObjectProperty<>(this, "invoice");
        this.transactionDate = new SimpleObjectProperty<>(this, "transactionDate");
        this.amount = new SimpleObjectProperty<>(this, "amount");
        this.paymentMethod = new SimpleObjectProperty<>(this, "paymentMethod");
        this.notes = new SimpleStringProperty(this, "notes");

        // Setters will apply validation
        setCategory(category);
        setInvoice(invoice);
        setTransactionDate(transactionDate);
        setAmount(amount);
        setPaymentMethod(paymentMethod);
        setNotes(notes);
    }

    /**
     * Convenience constructor for creating a new {@code Transaction} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new transaction record for **insertion** into the database.
     * The {@code transactionID} is omitted as it is typically auto-generated by the database.
     * All parameters are validated via their respective setters.
     *
     * @param category        The {@link Category} object that classifies this transaction. Must not be {@code null}.
     * @param invoice         The {@link Invoice} object linked to this transaction, or {@code null} if none.
     * @param transactionDate The date of the transaction. Must not be {@code null}.
     * @param amount          The monetary amount of the transaction. Must not be {@code null}.
     * @param paymentMethod   The {@link PaymentMethod} used for payment. Can be {@code null}.
     * @param notes           Any optional notes or additional information about the transaction. Can be {@code null}.
     * @throws IllegalArgumentException if any mandatory argument is invalid (e.g., null).
     */
    public Transaction(Category category, Invoice invoice,
                       LocalDate transactionDate, BigDecimal amount, PaymentMethod paymentMethod, String notes) {
        // Delegate to the full constructor with null for transactionID for a new entity
        this(null, category, invoice, transactionDate, amount, paymentMethod, notes);
    }

    // --- JavaFX Property Accessor Methods ---

    /**
     * Retrieves the {@link ReadOnlyObjectProperty} for the unique identifier of this transaction.
     * <p>
     * This property represents the {@code TransactionID} column in the database.
     * Its value is immutable once set (typically by the database).
     * </p>
     *
     * @return The {@link ReadOnlyObjectProperty} for {@code transactionID}.
     */
    public ReadOnlyObjectProperty<Integer> transactionIDProperty() {
        return transactionID;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the {@link Category} object that classifies this transaction.
     * This property corresponds to the {@code CategoryID_FK} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code category}.
     */
    public ObjectProperty<Category> categoryProperty() {
        return category;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the {@link Invoice} object this transaction is associated with.
     * This property corresponds to the {@code InvoiceID_FK} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code invoice}.
     */
    public ObjectProperty<Invoice> invoiceProperty() {
        return invoice;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the date on which this transaction occurred.
     * This property corresponds to the {@code TransactionDate} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code transactionDate}.
     */
    public ObjectProperty<LocalDate> transactionDateProperty() {
        return transactionDate;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the monetary amount of this transaction.
     * This property corresponds to the {@code Amount} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code amount}.
     */
    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the payment method used for this transaction.
     * This property corresponds to the {@code PaymentMethod} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code paymentMethod}.
     */
    public ObjectProperty<PaymentMethod> paymentMethodProperty() {
        return paymentMethod;
    }

    /**
     * Retrieves the {@link StringProperty} for any optional notes about the transaction.
     * This property corresponds to the {@code Notes} column in the database.
     *
     * @return The {@link StringProperty} for {@code notes}.
     */
    public StringProperty notesProperty() {
        return notes;
    }

    // --- Value Getters and Setters ---

    /**
     * Retrieves the unique identifier for this transaction.
     * For new, unpersisted transactions, this will be {@code null}.
     * Corresponds to the {@code TransactionID} column in the database.
     *
     * @return The {@link Integer} primary key used to identify this transaction record, or {@code null} if not yet assigned.
     */
    public Integer getTransactionID() {
        return transactionID.get();
    }

    /**
     * Sets the unique ID for this transaction. This method is designed to be package-private
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
    void _setTransactionID(Integer id) { // Package-private for DAO use only
        if (this.transactionID.get() != null) {
            throw new IllegalStateException("Transaction ID cannot be changed once set.");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Transaction ID cannot be null or non-positive.");
        }
        ((SimpleObjectProperty<Integer>) this.transactionID).set(id);
    }

    /**
     * Retrieves the {@link Category} object that classifies this transaction.
     * Corresponds to the {@code CategoryID_FK} column in the database.
     *
     * @return The associated {@link Category} object.
     */
    public Category getCategory() {
        return category.get();
    }

    /**
     * Sets the {@link Category} object that classifies this transaction.
     *
     * @param category The {@link Category} object to set. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code category} is {@code null}.
     */
    public void setCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null for a transaction.");
        }
        this.category.set(category);
    }

    /**
     * Retrieves the {@link Invoice} object this transaction is associated with.
     * Corresponds to the {@code InvoiceID_FK} column in the database.
     *
     * @return The associated {@link Invoice} object, or {@code null} if no invoice is linked.
     */
    public Invoice getInvoice() {
        return invoice.get();
    }

    /**
     * Sets the {@link Invoice} object this transaction is associated with.
     *
     * @param invoice The {@link Invoice} object to set. Can be {@code null}.
     */
    public void setInvoice(Invoice invoice) {
        this.invoice.set(invoice);
    }

    /**
     * Retrieves the date on which this transaction occurred.
     * Corresponds to the {@code TransactionDate} column in the database.
     *
     * @return The transaction date.
     */
    public LocalDate getTransactionDate() {
        return transactionDate.get();
    }

    /**
     * Sets the date on which this transaction occurred.
     *
     * @param transactionDate The transaction date to set. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code transactionDate} is {@code null}.
     */
    public void setTransactionDate(LocalDate transactionDate) {
        if (transactionDate == null) {
            throw new IllegalArgumentException("Transaction date cannot be null.");
        }
        this.transactionDate.set(transactionDate);
    }

    /**
     * Retrieves the monetary amount of this transaction.
     * Corresponds to the {@code Amount} column in the database.
     *
     * @return The transaction amount as a {@link BigDecimal}.
     */
    public BigDecimal getAmount() {
        return amount.get();
    }

    /**
     * Sets the monetary amount of this transaction.
     *
     * @param amount The transaction amount to set. Must not be {@code null} and must be non-negative.
     * @throws IllegalArgumentException if {@code amount} is {@code null} or negative.
     */
    public void setAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null for a transaction.");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Transaction amount cannot be negative.");
        }
        this.amount.set(amount);
    }

    /**
     * Retrieves the payment method used for this transaction.
     * Corresponds to the {@code PaymentMethod} column in the database.
     *
     * @return The {@link PaymentMethod} representing the payment method, or {@code null} if not specified.
     */
    public PaymentMethod getPaymentMethod() {
        return paymentMethod.get();
    }

    /**
     * Sets the payment method used for this transaction.
     *
     * @param paymentMethod The {@link PaymentMethod} to set. Can be {@code null}.
     */
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    /**
     * Retrieves any optional notes or contextual information about the transaction.
     * Corresponds to the {@code Notes} column in the database.
     *
     * @return A string containing the notes, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes.get();
    }

    /**
     * Sets additional notes or contextual information for this transaction.
     * If the provided notes are not {@code null}, leading and trailing whitespace will be stripped.
     *
     * @param notes The string containing notes to set. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes.set((notes != null) ? notes.trim() : null);
    }

    // --- Utility Methods ---

    /**
     * <p>
     * Returns a string representation of the {@code Transaction} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the transaction's key attributes.
     * </p>
     * <p>
     * The format includes the transaction ID, date, associated category ID,
     * amount, payment method, and invoice ID (if any).
     * </p>
     *
     * @return A string in the format:
     * "Transaction{ID=..., CategoryID=..., InvoiceID=..., Date=..., Amount=..., PaymentMethod=..., Notes=...}"
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionID=" + getTransactionID() +
                ", categoryID=" + (getCategory() != null ? getCategory().getCategoryID() : "null") +
                ", invoiceID=" + (getInvoice() != null ? getInvoice().getInvoiceID() : "null") +
                ", transactionDate=" + getTransactionDate() +
                ", amount=" + getAmount() +
                ", paymentMethod=" + (getPaymentMethod() != null ? getPaymentMethod().toString() : "null") +
                ", notes='" + getNotes() + '\'' +
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
        return Objects.equals(getTransactionID(), that.getTransactionID());
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
        return Objects.hash(getTransactionID());
    }
}