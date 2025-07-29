package com.avaruusstudios.vmdb.implementation.sqlite;

import com.avaruusstudios.vmdb.data.DatabaseAccessException;
import com.avaruusstudios.vmdb.data.TransactionDataAccess;
import com.avaruusstudios.vmdb.db.DatabaseManager;
import com.avaruusstudios.vmdb.db.QueryLoader;
import com.avaruusstudios.vmdb.model.Category;
import com.avaruusstudios.vmdb.model.Invoice;
import com.avaruusstudios.vmdb.model.PaymentMethod;
import com.avaruusstudios.vmdb.model.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * {@code TransactionImplementation} provides a concrete implementation of the {@link TransactionDataAccess}
 * interface, specifically designed for SQLite databases. This class handles all CRUD (Create, Read, Update, Delete)
 * and specialized data access operations for {@link Transaction} entities.
 * </p>
 *
 * <p>
 * It interacts with the database using JDBC, preparing SQL statements, mapping {@link ResultSet}
 * rows to {@link Transaction} objects, and managing database connections through a {@link DatabaseManager}.
 * This implementation addresses the N+1 problem by using SQL JOINs to fetch related Category and Invoice data
 * along with Transaction data in a single query for read operations.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-26
 * Updated On: 2025-07-28
 *
 * @see TransactionDataAccess
 * @see Transaction
 * @see DatabaseManager
 */
public class TransactionImplementation implements TransactionDataAccess {

    /**
     * SLF4J Logger for logging informational messages, warnings, and errors within the {@code TransactionImplementation} class.
     */
    private static final Logger logger = LoggerFactory.getLogger(TransactionImplementation.class);

    /**
     * DateTimeFormatter for parsing and formatting `LocalDate` objects to/from database strings in "yyyy-MM-dd" format.
     */
    private static final DateTimeFormatter CUSTOM_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // SQL Query Constants
    /** SQL query to create (insert) a new transaction record. */
    private static final String SQL_CREATE_TRANSACTION_RECORD;
    /** SQL query to read a single transaction record by its ID, including joined Category and Invoice data. */
    private static final String SQL_READ_TRANSACTION_RECORD;
    /** SQL query to read all transaction records, including joined Category and Invoice data. */
    private static final String SQL_READ_ALL_TRANSACTION_RECORD;
    /** SQL query to count the total number of transaction records. */
    private static final String SQL_COUNT_TRANSACTION_RECORD;
    /** SQL query to check if a transaction record exists by its ID. */
    private static final String SQL_EXISTS_TRANSACTION_BY_ID;
    /** SQL query to update an existing transaction record. */
    private static final String SQL_UPDATE_TRANSACTION_RECORD;
    /** SQL query to perform a soft delete on a transaction record (setting IsActive to 0 and DeletedAt). */
    private static final String SQL_DELETE_TRANSACTION_SOFT;
    /** SQL query to find transactions within a specified date range, including joined Category and Invoice data. */
    private static final String SQL_FIND_BY_DATE_RANGE;
    /** SQL query to find transactions associated with a specific category, including joined Category and Invoice data. */
    private static final String SQL_FIND_BY_CATEGORY;
    /** SQL query to find transactions associated with a specific invoice, including joined Category and Invoice data. */
    private static final String SQL_FIND_BY_INVOICE;


    static {
        try {
            SQL_CREATE_TRANSACTION_RECORD = QueryLoader.getQuery("transaction/insertTransaction.sql");
            SQL_READ_TRANSACTION_RECORD = QueryLoader.getQuery("transaction/selectTransactionByIdJoined.sql");
            SQL_READ_ALL_TRANSACTION_RECORD = QueryLoader.getQuery("transaction/selectAllTransactionsJoined.sql");
            SQL_COUNT_TRANSACTION_RECORD = QueryLoader.getQuery("transaction/countTransactions.sql");
            SQL_EXISTS_TRANSACTION_BY_ID = QueryLoader.getQuery("transaction/existByIdTransaction.sql");
            SQL_UPDATE_TRANSACTION_RECORD = QueryLoader.getQuery("transaction/updateTransaction.sql");
            SQL_DELETE_TRANSACTION_SOFT = QueryLoader.getQuery("transaction/deleteTransactionSoft.sql");

            SQL_FIND_BY_DATE_RANGE = QueryLoader.getQuery("transaction/selectTransactionsByDateRange.sql");
            SQL_FIND_BY_CATEGORY = QueryLoader.getQuery("transaction/selectTransactionsByCategory.sql");
            SQL_FIND_BY_INVOICE = QueryLoader.getQuery("transaction/selectTransactionsByInvoice.sql");

            logger.info("All SQL queries for TransactionImplementation loaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load one or more SQL queries for TransactionImplementation. Check .sql files and paths.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Constructs a new {@code TransactionImplementation}.
     * This class adheres to the convention of not having an explicit constructor for dependency injection.
     * Related objects (Category, Invoice) are fully populated directly from the joined ResultSet in read operations.
     */
    public TransactionImplementation() {
        // Empty constructor as per convention.
    }

    /**
     * <p>
     * Maps a row from a {@link ResultSet} to a fully populated {@link Transaction} object.
     * This private helper method centralizes the logic for converting raw database
     * column values into a populated {@code Transaction} model object, ensuring data consistency
     * and type safety across all read operations.
     * </p>
     * <p>
     * This method expects a {@link ResultSet} that contains not only `Transaction` table columns
     * but also joined columns from the `Categories` and `Invoices` tables, allowing for
     * the direct hydration of associated {@link Category} and {@link Invoice} objects
     * without incurring an N+1 query problem.
     * </p>
     * <p>
     * It handles:
     * <ul>
     * <li>Retrieval of the `TransactionID` and setting it via the `_setTransactionID` method.</li>
     * <li>Mapping of full {@link Category} details from joined columns (`CategoryID_FK`, `CategoryName`).</li>
     * <li>Mapping of full {@link Invoice} details from joined columns (`InvoiceID_FK`, `InvoiceDate`), handling nulls
     * and creating a partial Invoice object if the foreign key exists but joined data does not.</li>
     * <li>Parsing `LocalDate` for `TransactionDate` and `DeletedAt` using {@link #CUSTOM_DATE_FORMATTER}.</li>
     * <li>Mapping of `PaymentMethod` enum using {@link PaymentMethod#fromDbValue(String)}.</li>
     * <li>Conversion of `IsActive` from `INTEGER` (0 or 1) to `boolean`.</li>
     * <li>Retrieval of `Notes` string field.</li>
     * </ul>
     * </p>
     *
     * @param rs The {@link ResultSet} positioned at the current row containing transaction, category, and invoice data.
     * @return A fully populated {@link Transaction} object with data retrieved from the {@code ResultSet}.
     * @throws SQLException If a database access error occurs (e.g., a column name is not found,
     * or there is a data type mismatch during retrieval from the {@code ResultSet}).
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();

        // Transaction fields
        Integer transactionID = rs.getInt("TransactionID");
        if (transactionID > 0) {
            transaction._setTransactionID(transactionID);
        }
        LocalDate transactionDate = LocalDate.parse(rs.getString("TransactionDate"), CUSTOM_DATE_FORMATTER);
        BigDecimal amount = rs.getBigDecimal("Amount");
        String paymentMethodStr = rs.getString("PaymentMethod");
        boolean isActive = rs.getInt("IsActive") == 1;
        String deletedAtStr = rs.getString("DeletedAt");
        LocalDate deletedAt = (deletedAtStr != null && !deletedAtStr.isEmpty()) ?
                LocalDate.parse(deletedAtStr, CUSTOM_DATE_FORMATTER) : null;
        String notes = rs.getString("Notes");

        // Category fields from JOIN
        Integer categoryID = rs.getInt("CategoryID_FK");
        String categoryName = rs.getString("CategoryName");

        Category category = new Category();
        category._setCategoryID(categoryID);
        category.setCategoryName(categoryName);
        transaction.setCategory(category);

        // Invoice fields from JOIN (nullable)
        Invoice invoice = null;
        Integer invoiceID_FK = rs.getObject("InvoiceID_FK", Integer.class);
        if (invoiceID_FK != null) {
            String invoiceDateStr = rs.getString("InvoiceDate");
            if (invoiceDateStr != null && !rs.wasNull()) { // Check if invoiceDateStr is genuinely present from join
                invoice = new Invoice();
                invoice._setInvoiceID(invoiceID_FK);
                invoice.setInvoiceDate(LocalDate.parse(invoiceDateStr, CUSTOM_DATE_FORMATTER));
            } else if (!rs.wasNull()) { // FK is present, but joined data for InvoiceDate is null. Could be soft deleted or non-existent.
                invoice = new Invoice();
                invoice._setInvoiceID(invoiceID_FK);
                logger.warn("Invoice with ID {} referenced by transaction {} found as FK but joined data is null. Partial Invoice object created.", invoiceID_FK, transactionID);
            }
        }
        transaction.setInvoice(invoice);

        PaymentMethod paymentMethod = (paymentMethodStr != null) ? PaymentMethod.fromDbValue(paymentMethodStr) : null;
        transaction.setPaymentMethod(paymentMethod);

        transaction.setTransactionDate(transactionDate);
        transaction.setAmount(amount);
        transaction.setIsActive(isActive);
        transaction.setDeletedAt(deletedAt);
        transaction.setNotes(notes);

        return transaction;
    }

    // --- Interface Implementations ---

    /**
     * {@inheritDoc}
     * <p>
     * Creates a new transaction record in the database.
     * The `TransactionID` of the provided {@code Transaction} object must be {@code null}
     * as it will be auto-generated by the database.
     * </p>
     *
     * @param transaction The {@link Transaction} object to be created. Must not be {@code null}.
     * @return The created {@link Transaction} object with its auto-generated `TransactionID` set.
     * @throws DatabaseAccessException If a database access error occurs, or if the transaction creation fails
     * (e.g., no rows affected, no ID obtained).
     * @throws NullPointerException    If the provided `transaction` object is {@code null}.
     * @throws IllegalArgumentException If the `transaction` object already has a non-null `TransactionID`.
     */
    @Override
    public Transaction createRecord(Transaction transaction) throws DatabaseAccessException {
        Objects.requireNonNull(transaction, "Transaction object cannot be null for creation.");
        if (transaction.getTransactionID() != null) {
            throw new IllegalArgumentException("Transaction ID must be null for new transaction creation (auto-generated).");
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CREATE_TRANSACTION_RECORD, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, transaction.getCategory().getCategoryID());
            stmt.setObject(2, transaction.getInvoice() != null ? transaction.getInvoice().getInvoiceID() : null);
            stmt.setString(3, transaction.getTransactionDate().format(CUSTOM_DATE_FORMATTER));
            stmt.setBigDecimal(4, transaction.getAmount());
            stmt.setString(5, transaction.getPaymentMethod() != null ? transaction.getPaymentMethod().getDbValue() : null);
            stmt.setInt(6, transaction.getIsActive() ? 1 : 0);
            stmt.setString(7, transaction.getDeletedAt() != null ? transaction.getDeletedAt().format(CUSTOM_DATE_FORMATTER) : null);
            stmt.setString(8, transaction.getNotes());

            logger.debug("Executing insert transaction query for category ID: {}", transaction.getCategory().getCategoryID());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.error("Creating transaction failed, no rows affected for category ID: {}", transaction.getCategory().getCategoryID());
                throw new DatabaseAccessException("Creating transaction failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Integer generatedId = generatedKeys.getInt(1);
                    transaction._setTransactionID(generatedId);
                    logger.info("Successfully created transaction with ID: {}", generatedId);
                    return transaction;
                } else {
                    logger.error("Creating transaction failed, no ID obtained for category ID: {}", transaction.getCategory().getCategoryID());
                    throw new DatabaseAccessException("Creating transaction failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating transaction record: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error creating transaction record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Performs a soft delete operation on a {@link Transaction} record identified by its ID.
     * This method does not physically remove the record from the database. Instead, it
     * updates the `IsActive` flag to `0` (false) and sets the `DeletedAt` column
     * to the current date, preserving the record for historical or auditing purposes.
     * </p>
     *
     * @param id The ID of the transaction record to be soft-deleted. Must not be {@code null}.
     * @throws DatabaseAccessException If a database access error occurs during the soft deletion.
     * @throws NullPointerException    If the provided `id` is {@code null}.
     */
    @Override
    public void deleteRecord(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Transaction ID cannot be null for deletion.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_TRANSACTION_SOFT)) {

            stmt.setInt(1, 0); // IsActive = 0
            stmt.setString(2, LocalDate.now().format(CUSTOM_DATE_FORMATTER)); // Set DeletedAt
            stmt.setInt(3, id); // WHERE TransactionID = ?

            logger.debug("Executing soft-delete transaction query for ID: {}", id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No transaction found with ID: {} for soft-deletion. It might already be deleted or does not exist.", id);
            } else {
                logger.info("Successfully soft-deleted transaction with ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Error soft-deleting transaction record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error soft-deleting transaction record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Reads a single transaction record from the database by its ID.
     * This method fetches the transaction along with its associated Category and Invoice details
     * using SQL JOINs to ensure a fully hydrated object and avoid the N+1 problem.
     * </p>
     *
     * @param id The ID of the transaction record to be read. Must not be {@code null}.
     * @return An {@link Optional} containing the {@link Transaction} object if found,
     * or an empty {@link Optional} if no record matches the given ID.
     * @throws DatabaseAccessException If a database access error occurs during the read operation.
     * @throws NullPointerException    If the provided `id` is {@code null}.
     */
    @Override
    public Optional<Transaction> readRecord(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Transaction ID cannot be null for reading.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_TRANSACTION_RECORD)) {

            stmt.setInt(1, id);
            logger.debug("Executing read transaction query for ID: {}", id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Transaction transaction = mapResultSetToTransaction(rs);
                    logger.info("Found transaction with ID: {}", id);
                    return Optional.of(transaction);
                } else {
                    logger.info("No transaction found with ID: {}", id);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Error reading transaction record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error reading transaction record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Reads all transaction records from the database.
     * This method fetches all transactions along with their associated Category and Invoice details
     * using SQL JOINs to ensure fully hydrated objects and avoid the N+1 problem.
     * </p>
     *
     * @return A {@link List} of all {@link Transaction} objects found in the database.
     * The list will be empty if no records are found.
     * @throws DatabaseAccessException If a database access error occurs during the read operation.
     */
    @Override
    public List<Transaction> readRecordAll() throws DatabaseAccessException {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_ALL_TRANSACTION_RECORD);
             ResultSet rs = stmt.executeQuery()) {

            logger.debug("Executing read all transactions query.");
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            logger.info("Found {} total transactions.", transactions.size());
        } catch (SQLException e) {
            logger.error("Error reading all transaction records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error reading all transaction records: " + e.getMessage(), e);
        }
        return transactions;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Counts the total number of transaction records in the database.
     * </p>
     *
     * @return The total count of transaction records as a {@code long}.
     * @throws DatabaseAccessException If a database access error occurs during the count operation.
     */
    @Override
    public long countRecord() throws DatabaseAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_TRANSACTION_RECORD);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.info("Total transaction record count: {}", count);
                return count;
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting transaction records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error counting transaction records: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Checks if a transaction record with the given ID exists in the database.
     * </p>
     *
     * @param id The ID of the transaction record to check for existence. Must not be {@code null}.
     * @return {@code true} if a transaction with the given ID exists, {@code false} otherwise.
     * @throws DatabaseAccessException If a database access error occurs during the existence check.
     * @throws NullPointerException    If the provided `id` is {@code null}.
     */
    @Override
    public boolean existsById(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Transaction ID cannot be null for existence check.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EXISTS_TRANSACTION_BY_ID)) {

            stmt.setInt(1, id);
            logger.debug("Executing existsById transaction query for ID: {}", id);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean exists = rs.next();
                logger.info("Transaction with ID {} exists: {}", id, exists);
                return exists;
            }
        } catch (SQLException e) {
            logger.error("Error checking existence of transaction record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error checking existence of transaction record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates an existing transaction record in the database.
     * The `TransactionID` of the provided {@code Transaction} object must be non-{@code null}
     * as it identifies the record to be updated.
     * </p>
     *
     * @param transaction The {@link Transaction} object containing the updated data.
     * Must not be {@code null} and must have a non-{@code null} `TransactionID`.
     * @throws DatabaseAccessException If a database access error occurs or if the update operation
     * affects no rows (meaning the transaction with the given ID was not found).
     * @throws NullPointerException    If the provided `transaction` object or its `TransactionID` is {@code null}.
     */
    @Override
    public void updateRecord(Transaction transaction) throws DatabaseAccessException {
        Objects.requireNonNull(transaction, "Transaction object cannot be null for update.");
        Objects.requireNonNull(transaction.getTransactionID(), "Transaction ID must not be null for update.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_TRANSACTION_RECORD)) {

            stmt.setInt(1, transaction.getCategory().getCategoryID());
            stmt.setObject(2, transaction.getInvoice() != null ? transaction.getInvoice().getInvoiceID() : null);
            stmt.setString(3, transaction.getTransactionDate().format(CUSTOM_DATE_FORMATTER));
            stmt.setBigDecimal(4, transaction.getAmount());
            stmt.setString(5, transaction.getPaymentMethod() != null ? transaction.getPaymentMethod().getDbValue() : null);
            stmt.setInt(6, transaction.getIsActive() ? 1 : 0);
            stmt.setString(7, transaction.getDeletedAt() != null ? transaction.getDeletedAt().format(CUSTOM_DATE_FORMATTER) : null);
            stmt.setString(8, transaction.getNotes());
            stmt.setInt(9, transaction.getTransactionID()); // WHERE clause

            logger.debug("Executing update transaction query for ID: {}", transaction.getTransactionID());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No transaction found with ID: {} for update. Update operation resulted in 0 affected rows.", transaction.getTransactionID());
                throw new DatabaseAccessException("Transaction with ID " + transaction.getTransactionID() + " not found for update.");
            } else {
                logger.info("Successfully updated transaction with ID: {}", transaction.getTransactionID());
            }
        } catch (SQLException e) {
            logger.error("Error updating transaction record with ID {}: {}", transaction.getTransactionID(), e.getMessage(), e);
            throw new DatabaseAccessException("Error updating transaction record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Finds all transaction records within a specified date range (inclusive of start and end dates).
     * This method fetches transactions along with their associated Category and Invoice details
     * using SQL JOINs to ensure fully hydrated objects and avoid the N+1 problem.
     * </p>
     *
     * @param startDate The start date of the range (inclusive). Must not be {@code null}.
     * @param endDate   The end date of the range (inclusive). Must not be {@code null}.
     * @return A {@link List} of {@link Transaction} objects that fall within the specified date range.
     * The list will be empty if no records are found.
     * @throws DatabaseAccessException If a database access error occurs during the search.
     * @throws NullPointerException    If either `startDate` or `endDate` is {@code null}.
     */
    @Override
    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseAccessException {
        Objects.requireNonNull(startDate, "Start date cannot be null for date range search.");
        Objects.requireNonNull(endDate, "End date cannot be null for date range search.");
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_DATE_RANGE)) {

            stmt.setString(1, startDate.format(CUSTOM_DATE_FORMATTER));
            stmt.setString(2, endDate.format(CUSTOM_DATE_FORMATTER));

            logger.debug("Executing findByDateRange query for transactions between {} and {}", startDate, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            logger.info("Found {} transactions within date range from {} to {}.", transactions.size(), startDate, endDate);
        } catch (SQLException e) {
            logger.error("Error finding transactions by date range ({} to {}): {}", startDate, endDate, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding transactions by date range: " + e.getMessage(), e);
        }
        return transactions;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Finds all transaction records associated with a specific category.
     * This method fetches transactions along with their associated Category and Invoice details
     * using SQL JOINs to ensure fully hydrated objects and avoid the N+1 problem.
     * </p>
     *
     * @param category The {@link Category} object representing the category to search by.
     * Must not be {@code null} and must have a non-{@code null} `CategoryID`.
     * @return A {@link List} of {@link Transaction} objects linked to the specified category.
     * The list will be empty if no records are found.
     * @throws DatabaseAccessException If a database access error occurs during the search.
     * @throws NullPointerException    If the provided `category` object or its `CategoryID` is {@code null}.
     */
    @Override
    public List<Transaction> findByCategory(Category category) throws DatabaseAccessException {
        Objects.requireNonNull(category, "Category cannot be null for category search.");
        Objects.requireNonNull(category.getCategoryID(), "Category ID cannot be null for category search.");
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_CATEGORY)) {

            stmt.setInt(1, category.getCategoryID());

            logger.debug("Executing findByCategory query for category ID: {}", category.getCategoryID());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            logger.info("Found {} transactions for category ID: {}", transactions.size(), category.getCategoryID());
        } catch (SQLException e) {
            logger.error("Error finding transactions by category ID {}: {}", category.getCategoryID(), e.getMessage(), e);
            throw new DatabaseAccessException("Error finding transactions by category: " + e.getMessage(), e);
        }
        return transactions;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Finds all transaction records associated with a specific invoice.
     * This method fetches transactions along with their associated Category and Invoice details
     * using SQL JOINs to ensure fully hydrated objects and avoid the N+1 problem.
     * </p>
     *
     * @param invoice The {@link Invoice} object representing the invoice to search by.
     * Must not be {@code null} and must have a non-{@code null} `InvoiceID`.
     * @return A {@link List} of {@link Transaction} objects linked to the specified invoice.
     * The list will be empty if no records are found.
     * @throws DatabaseAccessException If a database access error occurs during the search.
     * @throws NullPointerException    If the provided `invoice` object or its `InvoiceID` is {@code null}.
     */
    @Override
    public List<Transaction> findByInvoice(Invoice invoice) throws DatabaseAccessException {
        Objects.requireNonNull(invoice, "Invoice cannot be null for invoice search.");
        Objects.requireNonNull(invoice.getInvoiceID(), "Invoice ID cannot be null for invoice search.");
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_INVOICE)) {

            stmt.setInt(1, invoice.getInvoiceID());

            logger.debug("Executing findByInvoice query for invoice ID: {}", invoice.getInvoiceID());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            logger.info("Found {} transactions for invoice ID: {}", transactions.size(), invoice.getInvoiceID());
        } catch (SQLException e) {
            logger.error("Error finding transactions by invoice ID {}: {}", invoice.getInvoiceID(), e.getMessage(), e);
            throw new DatabaseAccessException("Error finding transactions by invoice: " + e.getMessage(), e);
        }
        return transactions;
    }
}