package com.avaruusstudios.vmdb.model;

import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * <p>
 * Represents a single financial transaction within the Vanpool Management System.
 * Transactions record financial movements, such as expenses (fuel, maintenance),
 * income (participant payments), or credits. This entity is designed to be the core
 * record for auditing and reconciliation.
 * </p>
 *
 * <p>
 * This class directly maps to the {@code Transactions} table in the SQLite database.
 * Key properties include:
 * <ul>
 * <li>{@code TransactionID INTEGER PRIMARY KEY AUTOINCREMENT}</li>
 * <li>{@code CategoryID_FK INTEGER NOT NULL}</li>
 * <li>{@code InvoiceID_FK INTEGER} (optional link to an invoice)</li>
 * <li>{@code TransactionDate TEXT NOT NULL} (mapped to **{@link LocalDateTime}** for time precision)</li>
 * <li>{@code Amount NUMERIC NOT NULL} (uses **{@link BigDecimal}** for financial precision)</li>
 * <li>{@code PaymentMethod TEXT}</li>
 * <li>{@code IsActive INTEGER NOT NULL DEFAULT 1} (for soft deletion)</li>
 * <li>{@code DeletedAt TEXT DEFAULT NULL} (mapped to **{@link LocalDateTime}** for precise deletion timestamp)</li>
 * <li>{@code Notes TEXT} (for general contextual information)</li>
 * </ul>
 * All properties are exposed as JavaFX Properties for UI data binding.
 * </p>
 *
 * @author AvaruusStudios
 * @version 2.0
 * Created On: 2025-07-11
 * Updated On: 2025-09-20
 *
 * @see Category
 * @see Invoice
 * @see PaymentMethod
 * @see java.math.BigDecimal
 * @see java.time.LocalDateTime
 */
public class Transaction {
    // --- Model Fields ---

    /**
     * Unique identifier for the transaction. This serves as the primary key
     * in the database ({@code TransactionID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * It is exposed as a {@link ReadOnlyObjectProperty}.
     */
    private final ReadOnlyObjectProperty<Integer> transactionID;
    /**
     * The {@link Category} object that classifies this transaction.
     * This field is **required** (corresponds to {@code CategoryID_FK INTEGER NOT NULL}).
     */
    private final ObjectProperty<Category> category;
    /**
     * The {@link Invoice} object this transaction is associated with, if any.
     * This field is optional (corresponds to {@code InvoiceID_FK INTEGER}).
     */
    private final ObjectProperty<Invoice> invoice;
    /**
     * The date and **time** on which the transaction occurred.
     * This field is **required** and uses {@link LocalDateTime} for financial auditing precision.
     */
    private final ObjectProperty<LocalDateTime> transactionDate;
    /**
     * The monetary amount of the transaction.
     * This field is **required** and stored as a {@link BigDecimal} for precision.
     */
    private final ObjectProperty<BigDecimal> amount;
    /**
     * The method of payment used for this transaction.
     * This field is optional.
     */
    private final ObjectProperty<PaymentMethod> paymentMethod;
    /**
     * Flag indicating if the transaction is currently active ({@code true}) or has been soft-deleted ({@code false}).
     * Corresponds to the `IsActive` column in the database ({@code INTEGER NOT NULL DEFAULT 1}).
     */
    private final BooleanProperty isActive;
    /**
     * The date and **time** when the transaction was soft-deleted.
     * This field is {@code null} if the transaction is active, and uses {@link LocalDateTime} for full timestamp accuracy.
     */
    private final ObjectProperty<LocalDateTime> deletedAt;
    /**
     * Optional notes or contextual information about the transaction.
     * This field is optional (corresponds to {@code Notes TEXT} in the database).
     */
    private final StringProperty notes;


    // --- Constructors ---

    /**
     * Default constructor for creating a new, empty {@code Transaction} object.
     * Initializes a new, unpersisted transaction with default values: ID is {@code null},
     * transaction date is now, amount is zero, and status is active.
     */
    public Transaction() {
        this(null, new Category(), null, LocalDateTime.now(), BigDecimal.ZERO, null, true, null, "");
    }

    /**
     * Full constructor to initialize all fields of a {@code Transaction} instance, typically used when loading
     * an *existing* record from the database.
     *
     * @param transactionID   The unique integer ID for the transaction, or {@code null} for new entities.
     * @param category        The classification category. Must not be {@code null}.
     * @param invoice         The linked invoice, or {@code null}.
     * @param transactionDate The date and time of the transaction. Must not be {@code null}.
     * @param amount          The monetary amount. Must not be {@code null} and must be non-negative.
     * @param paymentMethod   The method of payment, or {@code null}.
     * @param isActive        The active status.
     * @param deletedAt       The date and time of soft-deletion, or {@code null} if active.
     * @param notes           Any optional notes, or {@code null}.
     * @throws IllegalArgumentException if any mandatory argument is invalid (e.g., null category or date).
     * @throws IllegalStateException    if {@code transactionID} is attempted to be changed once set.
     */
    public Transaction(Integer transactionID, Category category, Invoice invoice,
                       LocalDateTime transactionDate, BigDecimal amount, PaymentMethod paymentMethod,
                       boolean isActive, LocalDateTime deletedAt, String notes) {
        this.transactionID = new SimpleObjectProperty<>(this, "transactionID", transactionID);
        this.category = new SimpleObjectProperty<>(this, "category");
        this.invoice = new SimpleObjectProperty<>(this, "invoice");
        this.transactionDate = new SimpleObjectProperty<>(this, "transactionDate");
        this.amount = new SimpleObjectProperty<>(this, "amount");
        this.paymentMethod = new SimpleObjectProperty<>(this, "paymentMethod");
        this.isActive = new SimpleBooleanProperty(this, "isActive", isActive);
        this.deletedAt = new SimpleObjectProperty<>(this, "deletedAt", deletedAt);
        this.notes = new SimpleStringProperty(this, "notes");

        // Setters apply validation
        setCategory(category);
        setInvoice(invoice);
        setTransactionDate(transactionDate);
        setAmount(amount);
        setPaymentMethod(paymentMethod);
        setNotes(notes);
    }

    /**
     * Convenience constructor for creating a new {@code Transaction} object, omitting the auto-generated ID.
     * This constructor is ideal when preparing a new transaction record for **insertion** into the database.
     * New transactions are initialized as active with a null deletion time.
     *
     * @param category        The classification category. Must not be {@code null}.
     * @param invoice         The linked invoice, or {@code null}.
     * @param transactionDate The date and time of the transaction. Must not be {@code null}.
     * @param amount          The monetary amount. Must not be {@code null} and must be non-negative.
     * @param paymentMethod   The method of payment, or {@code null}.
     * @param notes           Any optional notes, or {@code null}.
     * @throws IllegalArgumentException if any mandatory argument is invalid.
     */
    public Transaction(Category category, Invoice invoice,
                       LocalDateTime transactionDate, BigDecimal amount, PaymentMethod paymentMethod, String notes) {
        // Delegate to the full constructor with null for ID, and default isActive=true, deletedAt=null
        this(null, category, invoice, transactionDate, amount, paymentMethod, true, null, notes);
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
     * Retrieves the {@link ObjectProperty} for the date and time on which this transaction occurred.
     * This property corresponds to the {@code TransactionDate} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code transactionDate}, a {@link LocalDateTime}.
     */
    public ObjectProperty<LocalDateTime> transactionDateProperty() {
        return transactionDate;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the monetary amount of this transaction.
     * This property corresponds to the {@code Amount} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code amount}, a {@link BigDecimal}.
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
     * Retrieves the {@link BooleanProperty} indicating if the transaction is active.
     * This property corresponds to the `IsActive` column in the database, used for soft-deletion.
     *
     * @return The {@link BooleanProperty} for `isActive`.
     */
    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the date and time when the transaction was soft-deleted.
     * This property corresponds to the `DeletedAt` column in the database.
     *
     * @return The {@link ObjectProperty} for {@code deletedAt}, a {@link LocalDateTime}.
     */
    public ObjectProperty<LocalDateTime> deletedAtProperty() {
        return deletedAt;
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
     * @return The {@link Integer} primary key, or {@code null} if not yet assigned.
     */
    public Integer getTransactionID() {
        return transactionID.get();
    }

    /**
     * Sets the unique ID for this transaction. This method is primarily for use by data access objects (DAOs)
     * when an ID is generated by the database upon insertion.
     *
     * @param id The unique integer ID assigned by the database.
     * @throws IllegalStateException    if the ID has already been assigned to this object.
     * @throws IllegalArgumentException if the provided ID is {@code null} or non-positive.
     */
    public void _setTransactionID(Integer id) {
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
     * Retrieves the date and time on which this transaction occurred.
     * Corresponds to the {@code TransactionDate} column in the database.
     *
     * @return The transaction date and time as a {@link LocalDateTime}.
     */
    public LocalDateTime getTransactionDate() {
        return transactionDate.get();
    }

    /**
     * Sets the date and time on which this transaction occurred.
     *
     * @param transactionDate The transaction date and time to set. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code transactionDate} is {@code null}.
     */
    public void setTransactionDate(LocalDateTime transactionDate) {
        if (transactionDate == null) {
            throw new IllegalArgumentException("Transaction date and time cannot be null.");
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
     * Retrieves the active status of the transaction.
     * Corresponds to the `IsActive` column in the database.
     *
     * @return `true` if the transaction is active, `false` if it has been soft-deleted.
     */
    public boolean getIsActive() {
        return isActive.get();
    }

    /**
     * Sets the active status of the transaction.
     *
     * @param isActive The boolean value indicating active status.
     */
    public void setIsActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    /**
     * Retrieves the date and time when the transaction was soft-deleted.
     * Corresponds to the `DeletedAt` column in the database.
     *
     * @return The {@link LocalDateTime} when the transaction was deleted, or {@code null} if it is active.
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt.get();
    }

    /**
     * Sets the date and time when the transaction was soft-deleted.
     * This should be set to a non-null value when soft-deleting a transaction,
     * and to {@code null} if reactivating it.
     *
     * @param deletedAt The {@link LocalDateTime} to set, or {@code null}.
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt.set(deletedAt);
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
     * Returns a string representation of the {@code Transaction} object for debugging and logging.
     * </p>
     * <p>
     * The format includes all key properties and foreign key IDs.
     * </p>
     *
     * @return A string in the format:
     * "Transaction{ID=..., CategoryID=..., InvoiceID=..., Date=..., Amount=..., PaymentMethod=..., IsActive=..., DeletedAt=..., Notes=...}"
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Transaction{" +
                "transactionID=" + getTransactionID() +
                ", categoryID=" + (getCategory() != null ? getCategory().getCategoryID() : "null") +
                ", invoiceID=" + (getInvoice() != null ? getInvoice().getInvoiceID() : "null") +
                ", transactionDate=" + (getTransactionDate() != null ? getTransactionDate().format(formatter) : "null") +
                ", amount=" + getAmount() +
                ", paymentMethod=" + (getPaymentMethod() != null ? getPaymentMethod().toString() : "null") +
                ", isActive=" + getIsActive() +
                ", deletedAt=" + (getDeletedAt() != null ? getDeletedAt().format(formatter) : "null") +
                ", notes='" + getNotes() + '\'' +
                '}';
    }

    /**
     * <p>
     * Indicates whether some other object is "equal to" this one, primarily based on the unique {@code transactionID}.
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if the primary keys match; {@code false} otherwise.
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
     * Returns a hash code value for the object based on the unique {@code transactionID}.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTransactionID());
    }
}