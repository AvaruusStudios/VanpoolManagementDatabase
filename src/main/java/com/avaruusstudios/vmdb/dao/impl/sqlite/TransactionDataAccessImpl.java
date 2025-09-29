package com.avaruusstudios.vmdb.dao.impl.sqlite;

import com.avaruusstudios.vmdb.dao.DatabaseAccessException;
import com.avaruusstudios.vmdb.dao.TransactionDataAccess;
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
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Locale;

/**
 * <p>
 * {@code TransactionDataAccessImpl} provides a concrete implementation of the {@link TransactionDataAccess}
 * interface, specifically designed for SQLite databases. This class handles all CRUD (Create, Read, Update, Delete)
 * and specialized data access operations for {@link Transaction} entities.
 * </p>
 *
 * <p>
 * This implementation leverages {@link DatabaseManager} for connection management and {@link QueryLoader} to
 * load SQL queries from external files, promoting a clean separation of concerns and easier maintenance.
 * </p>
 *
 * <p>
 * **Key Implementation Details:**
 * <ul>
 * <li>**Data Mapping:** The {@link #mapResultSetToTransaction(ResultSet)} method handles the conversion from a database result set row to a `Transaction` object, including the correct handling of `BigDecimal`, `LocalDate`, and enum types (`Category` and `PaymentMethod`). It also reconstructs the nested `Vehicle` and `Invoice` objects using foreign key IDs.</li>
 * <li>**ID Generation:** New transaction records are inserted, and the auto-generated `TransactionID` is set back on the `Transaction` object.</li>
 * <li>**Soft Deletes:** Deletion of transaction records is implemented as a **soft-delete**, meaning the `IsActive` flag is updated to `0` and the `DeletedAt` timestamp is set, rather than physically removing the record. This preserves data for historical and auditing purposes.</li>
 * <li>**Type Safety:** The class uses `PreparedStatement` to prevent SQL injection and ensures type safety for all parameters.</li>
 * <li>**Error Handling:** All {@link SQLException}s are caught and re-thrown as a custom {@link DatabaseAccessException}, providing a consistent and clean error handling mechanism across the application's data access layer.</li>
 * </ul>
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-21
 * Updated On: 2025-07-23
 *
 * @see TransactionDataAccess
 * @see Transaction
 * @see DatabaseManager
 * @see QueryLoader
 * @see DatabaseAccessException
 */
public class TransactionDataAccessImpl implements TransactionDataAccess {

    /**
     * SLF4J logger for logging informational messages, warnings, and errors within the {@code TransactionDataAccessImpl} class.
     */
    private static final Logger logger = LoggerFactory.getLogger(TransactionDataAccessImpl.class);

    /**
     * <p>
     * Date/time formatter for converting between {@link LocalDateTime} objects in Java
     * and `TEXT` representation in the SQLite database for the {@code DeletedAt} field.
     * </p>
     * <p>
     * The pattern "dd-MMM-yyyy HH:mm" ensures a consistent format (e.g., "14-Jul-2025 10:30").
     * {@link Locale#ENGLISH} is used to guarantee consistent month abbreviations (e.g., "Jan", "Feb")
     * regardless of the default locale of the JVM.
     * </p>
     */
    private static final DateTimeFormatter CUSTOM_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.ENGLISH);

    // SQL Query Constants
    /** SQL query to insert a new transaction record. Loaded from `transaction/insertTransaction.sql`. */
    private static final String SQL_CREATE_TRANSACTION_RECORD;
    /** SQL query to select a transaction record by its unique ID. Loaded from `transaction/selectTransactionById.sql`. */
    private static final String SQL_READ_TRANSACTION_RECORD;
    /** SQL query to select all transaction records. Loaded from `transaction/selectAllTransactions.sql`. */
    private static final String SQL_READ_ALL_TRANSACTION_RECORDS;
    /** SQL query to count the total number of transaction records. Loaded from `transaction/countTransactions.sql`. */
    private static final String SQL_COUNT_TRANSACTION_RECORDS;
    /** SQL query to count the total number of active transaction records. Loaded from `transaction/countActiveTransactions.sql`. */
    private static final String SQL_COUNT_ACTIVE_TRANSACTION_RECORDS;
    /** SQL query to check if a transaction record with a given ID exists. Loaded from `transaction/existsTransactionById.sql`. */
    private static final String SQL_EXISTS_TRANSACTION_BY_ID;
    /** SQL query to update an existing transaction record. Loaded from `transaction/updateTransaction.sql`. */
    private static final String SQL_UPDATE_TRANSACTION_RECORD;
    /** SQL query to soft-delete a transaction record. Loaded from `transaction/deleteTransactionSoft.sql`. */
    private static final String SQL_DELETE_TRANSACTION_SOFT;
    /** SQL query to select a list of transaction records by their vehicle ID. Loaded from `transaction/selectTransactionsByVehicle.sql`. */
    private static final String SQL_FIND_BY_VEHICLE;
    /** SQL query to select a list of transaction records by their payment method. Loaded from `transaction/selectTransactionsByPaymentMethod.sql`. */
    private static final String SQL_FIND_BY_PAYMENT_METHOD;
    /** SQL query to select a list of transaction records by their category. Loaded from `transaction/selectTransactionsByCategory.sql`. */
    private static final String SQL_FIND_BY_CATEGORY;
    /** SQL query to select a list of transaction records by their invoice ID. Loaded from `transaction/selectTransactionsByInvoice.sql`. */
    private static final String SQL_FIND_BY_INVOICE;
    /** SQL query to select a list of all active transaction records. */
    private static final String SQL_READ_ACTIVE_TRANSACTION_RECORDS;
    /** SQL query to select a list of transaction records within a specified date range. */
    private static final String SQL_FIND_BY_DATE_RANGE;
    /** SQL query to select a list of active transaction records within a specified date range. */
    private static final String SQL_FIND_ACTIVE_BY_DATE_RANGE;

    /**
     * Static initializer block to load all SQL query strings from external `.sql` files
     * using the {@link QueryLoader}. This block executes only once when the class is first loaded,
     * ensuring that all necessary SQL queries are available before any database operations are attempted.
     * <p>
     * If any query file cannot be found or loaded, an {@link IllegalArgumentException} is caught,
     * logged as an error, and re-thrown as an {@link ExceptionInInitializerError} to indicate
     * a critical application setup failure that prevents the DAO from functioning correctly.
     * </p>
     */
    static {
        try {
            SQL_CREATE_TRANSACTION_RECORD = QueryLoader.getQuery("transaction/insertTransaction.sql");
            SQL_READ_TRANSACTION_RECORD = QueryLoader.getQuery("transaction/selectTransactionById.sql");
            SQL_READ_ALL_TRANSACTION_RECORDS = QueryLoader.getQuery("transaction/selectAllTransactions.sql");
            SQL_COUNT_TRANSACTION_RECORDS = QueryLoader.getQuery("transaction/countTransactions.sql");
            SQL_COUNT_ACTIVE_TRANSACTION_RECORDS = QueryLoader.getQuery("transaction/countActiveTransactions.sql");
            SQL_EXISTS_TRANSACTION_BY_ID = QueryLoader.getQuery("transaction/existsTransactionById.sql");
            SQL_UPDATE_TRANSACTION_RECORD = QueryLoader.getQuery("transaction/updateTransaction.sql");
            SQL_DELETE_TRANSACTION_SOFT = QueryLoader.getQuery("transaction/deleteTransactionSoft.sql");
            SQL_FIND_BY_VEHICLE = QueryLoader.getQuery("transaction/selectTransactionsByVehicle.sql");
            SQL_FIND_BY_PAYMENT_METHOD = QueryLoader.getQuery("transaction/selectTransactionsByPaymentMethod.sql");
            SQL_FIND_BY_CATEGORY = QueryLoader.getQuery("transaction/selectTransactionsByCategory.sql");
            SQL_FIND_BY_INVOICE = QueryLoader.getQuery("transaction/selectTransactionsByInvoice.sql");
            SQL_READ_ACTIVE_TRANSACTION_RECORDS = "SELECT * FROM Transactions WHERE IsActive = 1";
            SQL_FIND_BY_DATE_RANGE = "SELECT * FROM Transactions WHERE TransactionDate BETWEEN ? AND ?";
            SQL_FIND_ACTIVE_BY_DATE_RANGE = "SELECT * FROM Transactions WHERE IsActive = 1 AND TransactionDate BETWEEN ? AND ?";
            logger.info("All SQL queries for TransactionDataAccessImpl loaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load one or more SQL queries for TransactionDataAccessImpl. Check .sql files and paths.", e);
            throw new ExceptionInInitializerError(e); // Indicate a critical initialization failure
        }
    }

    /**
     * <p>
     * Maps a row from a {@link ResultSet} to a {@link Transaction} object.
     * This private helper method centralizes the logic for converting raw database
     * column values into a populated {@code Transaction} model object, ensuring data consistency
     * and type safety across all read operations.
     * </p>
     *
     * @param rs The {@link ResultSet} positioned at the current row containing transaction data.
     * @return A fully populated {@link Transaction} object.
     * @throws SQLException If a database access error occurs.
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        Integer transactionId = rs.getInt("TransactionID");
        if (transactionId > 0) {
            transaction._setTransactionID(transactionId);
        }
        transaction.setTransactionDate(LocalDateTime.parse(rs.getString("TransactionDate"), CUSTOM_DATETIME_FORMATTER));

        Integer invoiceId = rs.getInt("InvoiceID_FK");
        if (!rs.wasNull()) {
            Invoice invoice = new Invoice();
            invoice._setInvoiceID(invoiceId);
            transaction.setInvoice(invoice);
        }

        String categoryNameStr = rs.getString("Category");
        Category category = new Category();
        category.setCategoryName(categoryNameStr);
        transaction.setCategory(category);

        transaction.setPaymentMethod(PaymentMethod.fromDbValue(rs.getString("PaymentMethod")));
        transaction.setAmount(BigDecimal.valueOf(rs.getDouble("TransactionAmount")));

        transaction.setIsActive(rs.getInt("IsActive") == 1);
        String deletedAtStr = rs.getString("DeletedAt");
        transaction.setDeletedAt(deletedAtStr != null && !deletedAtStr.isEmpty() ?
                LocalDateTime.parse(deletedAtStr, CUSTOM_DATETIME_FORMATTER) : null);
        transaction.setNotes(rs.getString("Notes"));

        return transaction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction create(Transaction transaction) throws DatabaseAccessException {
        Objects.requireNonNull(transaction, "Transaction object cannot be null for creation.");
        if (transaction.getTransactionID() != null) {
            throw new IllegalArgumentException("Transaction ID must be null for new transaction creation (auto-generated).");
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CREATE_TRANSACTION_RECORD, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, transaction.getTransactionDate().format(CUSTOM_DATETIME_FORMATTER));

            if (transaction.getInvoice() != null) {
                stmt.setInt(2, transaction.getInvoice().getInvoiceID());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }


            stmt.setString(3, transaction.getNotes());
            stmt.setString(4, transaction.getCategory().getCategoryName());
            stmt.setString(5, transaction.getPaymentMethod().getDbValue());
            stmt.setBigDecimal(6, transaction.getAmount());
            stmt.setInt(7, transaction.getIsActive() ? 1 : 0);
            stmt.setString(8, transaction.getDeletedAt() != null ? transaction.getDeletedAt().format(CUSTOM_DATETIME_FORMATTER) : null);
            stmt.setString(9, transaction.getNotes());

            logger.debug("Executing insert transaction query for transaction on date: {}", transaction.getTransactionDate());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.error("Creating transaction failed, no rows affected for transaction on date: {}", transaction.getTransactionDate());
                throw new DatabaseAccessException("Creating transaction failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Integer generatedId = generatedKeys.getInt(1);
                    transaction._setTransactionID(generatedId);
                    logger.info("Successfully created transaction with ID: {}", generatedId);
                    return transaction;
                } else {
                    logger.error("Creating transaction failed, no ID obtained for transaction on date: {}", transaction.getTransactionDate());
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
     */
    @Override
    public boolean delete(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Transaction ID cannot be null for deletion.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_TRANSACTION_SOFT)) {

            stmt.setString(1, LocalDateTime.now().format(CUSTOM_DATETIME_FORMATTER)); // Set DeletedAt
            stmt.setInt(2, id); // Where TransactionID = ?

            logger.debug("Executing soft-delete transaction query for ID: {}", id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No transaction found with ID: {} for soft-deletion.", id);
                return false;
            } else {
                logger.info("Successfully soft-deleted transaction with ID: {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error soft-deleting transaction record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error soft-deleting transaction record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Transaction> find(Integer id) throws DatabaseAccessException {
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
     */
    @Override
    public List<Transaction> findAll() throws DatabaseAccessException {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_ALL_TRANSACTION_RECORDS);
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
     */
    @Override
    public List<Transaction> findActive() throws DatabaseAccessException {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_ACTIVE_TRANSACTION_RECORDS);
             ResultSet rs = stmt.executeQuery()) {

            logger.debug("Executing find active transactions query.");
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            logger.info("Found {} active transactions.", transactions.size());
        } catch (SQLException e) {
            logger.error("Error reading active transaction records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error reading active transaction records: " + e.getMessage(), e);
        }
        return transactions;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public long count() throws DatabaseAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_TRANSACTION_RECORDS);
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
     */
    @Override
    public long countActive() throws DatabaseAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_ACTIVE_TRANSACTION_RECORDS);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.info("Total active transaction record count: {}", count);
                return count;
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting active transaction records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error counting active transaction records: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
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
     */
    @Override
    public Transaction update(Transaction transaction) throws DatabaseAccessException {
        Objects.requireNonNull(transaction, "Transaction object cannot be null for update.");
        Objects.requireNonNull(transaction.getTransactionID(), "Transaction ID must not be null for update.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_TRANSACTION_RECORD)) {

            stmt.setString(1, transaction.getTransactionDate().format(CUSTOM_DATETIME_FORMATTER));

            if (transaction.getInvoice() != null) {
                stmt.setInt(2, transaction.getInvoice().getInvoiceID());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, transaction.getNotes());
            stmt.setString(4, transaction.getCategory().getCategoryName());
            stmt.setString(5, transaction.getPaymentMethod().getDbValue());
            stmt.setBigDecimal(6, transaction.getAmount());
            stmt.setInt(7, transaction.getIsActive() ? 1 : 0);
            stmt.setString(8, transaction.getDeletedAt() != null ? transaction.getDeletedAt().format(CUSTOM_DATETIME_FORMATTER) : null);
            stmt.setString(9, transaction.getNotes());
            stmt.setInt(10, transaction.getTransactionID());

            logger.debug("Executing update transaction query for ID: {}", transaction.getTransactionID());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No transaction found with ID: {} for update. Update operation resulted in 0 affected rows.", transaction.getTransactionID());
                throw new DatabaseAccessException("Transaction with ID " + transaction.getTransactionID() + " not found for update.");
            } else {
                logger.info("Successfully updated transaction with ID: {}", transaction.getTransactionID());
                return transaction;
            }
        } catch (SQLException e) {
            logger.error("Error updating transaction record with ID {}: {}", transaction.getTransactionID(), e.getMessage(), e);
            throw new DatabaseAccessException("Error updating transaction record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> findByPaymentMethod(PaymentMethod paymentMethod) throws DatabaseAccessException {
        Objects.requireNonNull(paymentMethod, "PaymentMethod cannot be null for search.");
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_PAYMENT_METHOD)) {

            stmt.setString(1, paymentMethod.getDbValue());
            logger.debug("Executing find transactions by payment method query for method: {}", paymentMethod.getDbValue());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            logger.info("Found {} transactions for payment method: {}", transactions.size(), paymentMethod.getDbValue());
        } catch (SQLException e) {
            logger.error("Error finding transactions by payment method {}: {}", paymentMethod.getDbValue(), e.getMessage(), e);
            throw new DatabaseAccessException("Error finding transactions by payment method: " + e.getMessage(), e);
        }
        return transactions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> findByCategory(Category category) throws DatabaseAccessException {
        Objects.requireNonNull(category, "Category cannot be null for search.");
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_CATEGORY)) {

            stmt.setString(1, category.getCategoryName());
            logger.debug("Executing find transactions by category query for category: {}", category.getCategoryName());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            logger.info("Found {} transactions for category: {}", transactions.size(), category.getCategoryName());
        } catch (SQLException e) {
            logger.error("Error finding transactions by category {}: {}", category.getCategoryName(), e.getMessage(), e);
            throw new DatabaseAccessException("Error finding transactions by category: " + e.getMessage(), e);
        }
        return transactions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> findByInvoice(Invoice invoice) throws DatabaseAccessException {
        Objects.requireNonNull(invoice, "Invoice cannot be null for invoice search.");
        Objects.requireNonNull(invoice.getInvoiceID(), "Invoice ID cannot be null for invoice search.");

        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_INVOICE)) {

            stmt.setInt(1, invoice.getInvoiceID());

            logger.debug("Executing query: {} with invoice ID: {}", SQL_FIND_BY_INVOICE, invoice.getInvoiceID());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            logger.info("Found {} transactions for invoice ID: {}", transactions.size(), invoice.getInvoiceID());
        } catch (SQLException e) {
            logger.error("Error finding transactions by invoice {}: {}", invoice.getInvoiceID(), e.getMessage(), e);
            throw new DatabaseAccessException("Error finding transactions by invoice: " + e.getMessage(), e);
        }
        return transactions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseAccessException {
        Objects.requireNonNull(startDate, "Start date cannot be null for search.");
        Objects.requireNonNull(endDate, "End date cannot be null for search.");

        List<Transaction> transactions = new ArrayList<>();
        // Now using the class-level static constant SQL_FIND_BY_DATE_RANGE
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_DATE_RANGE)) {

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59);

            stmt.setString(1, startDateTime.format(CUSTOM_DATETIME_FORMATTER));
            stmt.setString(2, endDateTime.format(CUSTOM_DATETIME_FORMATTER));

            logger.debug("Executing find transactions by date range query from {} ({}) to {} ({})",
                    startDate, startDateTime.format(CUSTOM_DATETIME_FORMATTER),
                    endDate, endDateTime.format(CUSTOM_DATETIME_FORMATTER));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            logger.info("Found {} transactions in date range.", transactions.size());
        } catch (SQLException e) {
            logger.error("Error finding transactions by date range: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error finding transactions by date range: " + e.getMessage(), e);
        }
        return transactions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> findActiveByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseAccessException {
        Objects.requireNonNull(startDate, "Start date cannot be null for search.");
        Objects.requireNonNull(endDate, "End date cannot be null for search.");

        List<Transaction> transactions = new ArrayList<>();
        // Using the new SQL constant for active transactions within a date range
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ACTIVE_BY_DATE_RANGE)) {

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59);

            stmt.setString(1, startDateTime.format(CUSTOM_DATETIME_FORMATTER));
            stmt.setString(2, endDateTime.format(CUSTOM_DATETIME_FORMATTER));

            logger.debug("Executing find ACTIVE transactions by date range query from {} to {}", startDate, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            logger.info("Found {} active transactions in date range.", transactions.size());
        } catch (SQLException e) {
            logger.error("Error finding active transactions by date range: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error finding active transactions by date range: " + e.getMessage(), e);
        }
        return transactions;
    }
}