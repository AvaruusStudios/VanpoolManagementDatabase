package com.avaruusstudios.vmdb.implementation.sqlite;

import com.avaruusstudios.vmdb.data.DatabaseAccessException;
import com.avaruusstudios.vmdb.data.InvoiceDataAccess;
import com.avaruusstudios.vmdb.db.QueryLoader;
import com.avaruusstudios.vmdb.model.Invoice;
import com.avaruusstudios.vmdb.model.InvoiceType;
import com.avaruusstudios.vmdb.model.PaymentStatus;
import com.avaruusstudios.vmdb.model.Vehicle; // Assuming Vehicle is needed for mapping
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Assuming a DatabaseManager class exists for connection pooling/management
import com.avaruusstudios.vmdb.db.DatabaseManager;

/**
 * <p>
 * {@code InvoiceImplementation} provides a concrete implementation of the {@link InvoiceDataAccess}
 * interface, specifically designed for SQLite databases. This class handles all CRUD (Create, Read, Update, Delete)
 * and specialized data access operations for {@link Invoice} entities.
 * </p>
 *
 * <p>
 * It interacts with the database using JDBC, preparing SQL statements, mapping {@link ResultSet}
 * rows to {@link Invoice} objects, and managing database connections through a {@link DatabaseManager}.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-25
 * Updated On: 2025-07-25
 *
 * @see InvoiceDataAccess
 * @see Invoice
 * @see DatabaseManager
 */
public class InvoiceImplementation implements InvoiceDataAccess {
    /**
     * SLF4J Logger for logging informational messages, warnings, and errors within the {@code InvoiceImplementation} class.
     */
    private static final Logger logger = LoggerFactory.getLogger(InvoiceImplementation.class);

    /**
     * DateTimeFormatter for parsing and formatting `LocalDate` objects to/from database strings in "yyyy-MM-dd" format.
     */
    private static final DateTimeFormatter CUSTOM_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * DateTimeFormatter for parsing and formatting `LocalDateTime` objects to/from database strings in "yyyy-MM-dd HH:mm:ss" format.
     */
    private static final DateTimeFormatter CUSTOM_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * DateTimeFormatter for parsing and formatting `YearMonth` objects to/from database strings in "MMM yyyy" format (e.g., "JAN 2025").
     */
    private static final DateTimeFormatter CUSTOM_PERIOD_LABEL_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy"); // Corrected to match "MMM YYYY" format

    // SQL Query Constants
    private static final String SQL_CREATE_INVOICE_RECORD;
    private static final String SQL_READ_INVOICE_RECORD;
    private static final String SQL_READ_ALL_INVOICE_RECORD;
    private static final String SQL_COUNT_INVOICE_RECORD;
    private static final String SQL_EXISTS_INVOICE_BY_ID;
    private static final String SQL_UPDATE_INVOICE_RECORD;
    private static final String SQL_DELETE_INVOICE_SOFT;
    private static final String SQL_FIND_INVOICES_BY_DATE_RANGE;
    private static final String SQL_FIND_INVOICES_BY_INVOICE_TYPE;


    static {
        try {
            SQL_CREATE_INVOICE_RECORD = QueryLoader.getQuery("invoice/insertInvoice.sql");
            SQL_READ_INVOICE_RECORD = QueryLoader.getQuery("invoice/selectInvoiceById.sql");
            SQL_READ_ALL_INVOICE_RECORD = QueryLoader.getQuery("invoice/selectAllInvoices.sql");
            SQL_COUNT_INVOICE_RECORD = QueryLoader.getQuery("invoice/countInvoices.sql");
            SQL_EXISTS_INVOICE_BY_ID = QueryLoader.getQuery("invoice/existById.sql");
            SQL_UPDATE_INVOICE_RECORD = QueryLoader.getQuery("invoice/updateInvoice.sql");
            SQL_DELETE_INVOICE_SOFT = QueryLoader.getQuery("invoice/deleteInvoiceSoft.sql");
            SQL_FIND_INVOICES_BY_DATE_RANGE = QueryLoader.getQuery("invoice/selectInvoicesByDateRange.sql");
            SQL_FIND_INVOICES_BY_INVOICE_TYPE = QueryLoader.getQuery("invoice/selectInvoicesByInvoiceType.sql");

            logger.info("All SQL queries for InvoiceImplementation loaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load one or more SQL queries for InvoiceImplementation. Check .sql files and paths.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    // --- Private Helper Methods ---

    /**
     * <p>
     * Maps a row from a {@link ResultSet} to an {@link Invoice} object.
     * This private helper method centralizes the logic for converting raw database
     * column values into a populated {@code Invoice} model object, ensuring data consistency
     * and type safety across all read operations.
     * </p>
     * <p>
     * It handles:
     * <ul>
     * <li>Retrieval of the auto-generated `InvoiceID` and setting it via the `_setInvoiceID` method.</li>
     * <li>Mapping of foreign key IDs (`VehicleID_FK`) to {@link Vehicle} objects.</li>
     * <li>Mapping of `InvoiceType` enum using {@link InvoiceType#fromDbValue(String)}.</li>
     * <li>`LocalDate` for `InvoiceDate` and `DueDate`.</li>
     * <li>`YearMonth` for `PeriodLabel`.</li>
     * <li>Mapping of `PaymentStatus` enum using {@link PaymentStatus#fromDbValue(String)}.</li>
     * <li>Conversion of `IsActive` from `INTEGER` (0 or 1) to `boolean`.</li>
     * <li>Handling of nullable `DeletedAt` `TEXT` field, parsing it into
     * {@link LocalDateTime} objects.</li>
     * <li>String fields (`Notes`).</li>
     * </ul>
     * </p>
     *
     * @param rs The {@link ResultSet} positioned at the current row containing invoice data.
     * @return A fully populated {@link Invoice} object with data retrieved from the {@code ResultSet}.
     * @throws SQLException If a database access error occurs (e.g., a column name is not found,
     * or there is a data type mismatch during retrieval).
     */
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();

        Integer invoiceId = rs.getInt("InvoiceID");
        if (invoiceId > 0) { // SQLite getInt returns 0 for NULL for INTEGER PRIMARY KEY, check for positive valid ID
            invoice._setInvoiceID(invoiceId);
        }

        // Vehicle FKey - Map to Vehicle object (assuming a simple vehicle object with just ID)
        Integer vehicleId = rs.getObject("VehicleID_FK", Integer.class);
        if (vehicleId != null) {
            Vehicle vehicle = new Vehicle(); // Assuming Vehicle has a constructor or setter for its ID
            vehicle._setVehicleID(vehicleId); // Corrected to use _setVehicleID
            invoice.setVehicle(vehicle);
        } else {
            // This should ideally not happen if VehicleID_FK is NOT NULL in schema, but for safety
            throw new SQLException("VehicleID_FK cannot be null for InvoiceID: " + invoiceId);
        }

        // InvoiceType Enum
        String invoiceTypeStr = rs.getString("InvoiceType");
        invoice.setInvoiceType(invoiceTypeStr != null && !invoiceTypeStr.isEmpty() ? InvoiceType.fromDbValue(invoiceTypeStr) : InvoiceType.NONE);

        // InvoiceDate
        String invoiceDateStr = rs.getString("InvoiceDate");
        invoice.setInvoiceDate(invoiceDateStr != null && !invoiceDateStr.isEmpty() ?
                LocalDate.parse(invoiceDateStr, CUSTOM_DATE_FORMATTER) : null);

        // DueDate
        String dueDateStr = rs.getString("DueDate");
        invoice.setDueDate(dueDateStr != null && !dueDateStr.isEmpty() ?
                LocalDate.parse(dueDateStr, CUSTOM_DATE_FORMATTER) : null);

        // PeriodLabel
        String periodLabelStr = rs.getString("PeriodLabel");
        invoice.setPeriodLabel(periodLabelStr != null && !periodLabelStr.isEmpty() ?
                YearMonth.parse(periodLabelStr, CUSTOM_PERIOD_LABEL_FORMATTER) : null);

        // PaymentStatus Enum
        String paymentStatusStr = rs.getString("PaymentStatus");
        invoice.setPaymentStatus(paymentStatusStr != null && !paymentStatusStr.isEmpty() ? PaymentStatus.fromDbValue(paymentStatusStr) : PaymentStatus.UNPAID);


        invoice.setIsActive(rs.getInt("IsActive") == 1);

        String deletedAtStr = rs.getString("DeletedAt");
        invoice.setDeletedAt(deletedAtStr != null && !deletedAtStr.isEmpty() ?
                LocalDateTime.parse(deletedAtStr, CUSTOM_DATETIME_FORMATTER) : null);

        invoice.setNotes(rs.getString("Notes"));

        return invoice;
    }

    // --- Interface Implementations ---

    /**
     * <p>
     * Retrieves a list of {@link Invoice} records that fall within a specified date range
     * (inclusive of start and end dates). The query targets the `InvoiceDate` column.
     * </p>
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate   The end date of the range (inclusive).
     * @return A {@link List} of {@link Invoice} objects found within the date range.
     * Returns an empty list if no invoices are found in the specified range.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    @Override
    public List<Invoice> findByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseAccessException {
        List<Invoice> invoices = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_INVOICES_BY_DATE_RANGE)) {

            stmt.setString(1, startDate.format(CUSTOM_DATE_FORMATTER));
            stmt.setString(2, endDate.format(CUSTOM_DATE_FORMATTER));

            logger.debug("Executing query: {} with startDate: {} and endDate: {}", SQL_FIND_INVOICES_BY_DATE_RANGE, startDate, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
            logger.info("Found {} invoices within date range {} to {}", invoices.size(), startDate, endDate);
        } catch (SQLException e) {
            logger.error("Error finding invoices by date range {} to {}: {}", startDate, endDate, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding invoices by date range: " + e.getMessage(), e);
        }
        return invoices;
    }

    /**
     * <p>
     * Retrieves a list of {@link Invoice} records that are of a specific invoice type
     * (e.g., 'LEASE', 'FUEL', 'NONE').
     * </p>
     *
     * @param invoiceType The type of invoice to filter by (e.g., "LEASE", "FUEL").
     * @return A {@link List} of {@link Invoice} objects matching the specified type.
     * Returns an empty list if no invoices are found with that type.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    @Override
    public List<Invoice> findByInvoiceType(String invoiceType) throws DatabaseAccessException {
        List<Invoice> invoices = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_INVOICES_BY_INVOICE_TYPE)) {

            stmt.setString(1, Objects.requireNonNull(invoiceType, "Invoice type cannot be null").toUpperCase()); // Ensure it matches DB enum names

            logger.debug("Executing query: {} with invoiceType: {}", SQL_FIND_INVOICES_BY_INVOICE_TYPE, invoiceType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
            logger.info("Found {} invoices of type {}", invoices.size(), invoiceType);
        } catch (SQLException e) {
            logger.error("Error finding invoices by invoice type {}: {}", invoiceType, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding invoices by invoice type: " + e.getMessage(), e);
        }
        return invoices;
    }

    /**
     * <p>
     * Inserts a new record of the specified entity into the database.
     * This method is used to persist a new instance of an entity.
     * </p>
     *
     * <p>
     * If the primary key of the entity is auto-generated by the database (e.g.,
     * an auto-incrementing ID), the {@code invoice} object passed as a parameter
     * typically should *not* have its primary key field set beforehand. Upon successful
     * insertion, the returned entity object will include the newly generated primary key.
     * </p>
     *
     * @param invoice The invoice object (instance of type {@code Invoice}) to be created and persisted.
     * Its non-primary key fields should contain the data to be stored.
     * @return The created entity object, which may include a database-generated primary key
     * if applicable. This object represents the state of the entity as stored.
     * @throws DatabaseAccessException if a database access error occurs during the insertion process,
     * if constraints are violated (e.g., unique key violation),
     * or if the creation operation otherwise fails. The underlying
     * {@code SQLException} will be wrapped within this custom exception.
     */
    @Override
    public Invoice createRecord(Invoice invoice) throws DatabaseAccessException {
        Objects.requireNonNull(invoice, "Invoice object cannot be null for creation.");
        if (invoice.getInvoiceID() != null) {
            throw new IllegalArgumentException("Invoice ID must be null for new invoice creation (auto-generated).");
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CREATE_INVOICE_RECORD, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, invoice.getVehicle().getVehicleID()); // VehicleID_FK
            stmt.setString(2, invoice.getInvoiceType().getDbValue());
            stmt.setString(3, invoice.getInvoiceDate().format(CUSTOM_DATE_FORMATTER));
            stmt.setString(4, invoice.getDueDate().format(CUSTOM_DATE_FORMATTER));
            stmt.setString(5, invoice.getPeriodLabel().format(CUSTOM_PERIOD_LABEL_FORMATTER));
            stmt.setString(6, invoice.getPaymentStatus().getDbValue()); // New field
            stmt.setInt(7, invoice.getIsActive() ? 1 : 0);
            stmt.setString(8, invoice.getDeletedAt() != null ? invoice.getDeletedAt().format(CUSTOM_DATETIME_FORMATTER) : null);
            stmt.setString(9, invoice.getNotes());

            logger.debug("Executing insert invoice query for vehicle ID: {}", invoice.getVehicle().getVehicleID());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.error("Creating invoice failed, no rows affected for vehicle ID: {}", invoice.getVehicle().getVehicleID());
                throw new DatabaseAccessException("Creating invoice failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Integer generatedId = generatedKeys.getInt(1);
                    invoice._setInvoiceID(generatedId); // Set the auto-generated ID
                    logger.info("Successfully created invoice with ID: {}", generatedId);
                    return invoice;
                } else {
                    logger.error("Creating invoice failed, no ID obtained for vehicle ID: {}", invoice.getVehicle().getVehicleID());
                    throw new DatabaseAccessException("Creating invoice failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating invoice record: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error creating invoice record: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Deletes a record of the entity from the database based on its primary key.
     * This method logically removes an entity instance from active use in the application.
     * </p>
     *
     * <p>
     * For this application, implementations of this method are expected to perform a
     * **soft-delete** rather than physically removing the record from the database.
     * This typically involves updating an `IsActive` flag to `0` and/or setting a
     * `DeletedAt` timestamp to the current time. This strategy preserves data integrity
     * for historical, auditing, and referential purposes, and allows for potential recovery.
     * </p>
     *
     * <p>
     * If no record matches the provided ID, the operation will typically complete
     * without throwing an error, but no rows will be affected. Implementations
     * might log a warning or return a boolean indicating success if desired.
     * </p>
     *
     * @param id The primary key (of type {@code Integer}) of the
     * entity record to delete. This uniquely identifies the record to be removed.
     * @throws DatabaseAccessException if a database access error occurs during the deletion process.
     * The underlying {@code SQLException} will be wrapped.
     */
    @Override
    public void deleteRecord(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Invoice ID cannot be null for deletion.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_INVOICE_SOFT)) {

            stmt.setString(1, LocalDateTime.now().format(CUSTOM_DATETIME_FORMATTER)); // Set DeletedAt
            stmt.setInt(2, id); // Where InvoiceID = ?

            logger.debug("Executing soft-delete invoice query for ID: {}", id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No invoice found with ID: {} for soft-deletion.", id);
            } else {
                logger.info("Successfully soft-deleted invoice with ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Error soft-deleting invoice record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error soft-deleting invoice record: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Retrieves a single record of the entity from the database based on its primary key.
     * This method provides a way to fetch a specific entity instance.
     * </p>
     *
     * <p>
     * The result is wrapped in an {@code Optional<Invoice>} to clearly indicate whether
     * an entity matching the given ID was found. This prevents {@code NullPointerExceptions}
     * and encourages explicit handling of cases where the record might not exist.
     * </p>
     *
     * @param id The primary key (of type {@code Integer}) of the
     * entity record to retrieve. This uniquely identifies the record in the database.
     * @return An {@code Optional<Invoice>} containing the entity object if a record with the
     * specified ID is found in the database; otherwise, an empty {@code Optional}.
     * @throws DatabaseAccessException if a database access error occurs during the retrieval process.
     * The underlying {@code SQLException} will be wrapped.
     */
    @Override
    public Optional<Invoice> readRecord(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Invoice ID cannot be null for reading.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_INVOICE_RECORD)) {

            stmt.setInt(1, id);
            logger.debug("Executing read invoice query for ID: {}", id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Invoice invoice = mapResultSetToInvoice(rs);
                    logger.info("Found invoice with ID: {}", id);
                    return Optional.of(invoice);
                } else {
                    logger.info("No invoice found with ID: {}", id);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Error reading invoice record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error reading invoice record: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Retrieves all records of the entity from the database.
     * This method is used to fetch a complete list of all instances of a given entity type.
     * </p>
     *
     * <p>
     * The order of the returned records is dependent on the underlying database and
     * the SQL query used by the concrete implementation (e.g., if no specific
     * ORDER BY clause is applied, the order might not be guaranteed).
     * </p>
     *
     * @return A {@code List<Invoice>} containing all entity objects found in the corresponding
     * database table. Returns an empty list if no records are present.
     * The list will not be {@code null}.
     * @throws DatabaseAccessException if a database access error occurs during the retrieval process.
     * The underlying {@code SQLException} will be wrapped.
     */
    @Override
    public List<Invoice> readRecordAll() throws DatabaseAccessException {
        List<Invoice> invoices = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_ALL_INVOICE_RECORD);
             ResultSet rs = stmt.executeQuery()) {

            logger.debug("Executing read all invoices query.");
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
            logger.info("Found {} total invoices.", invoices.size());
        } catch (SQLException e) {
            logger.error("Error reading all invoice records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error reading all invoice records: " + e.getMessage(), e);
        }
        return invoices;
    }

    /**
     * <p>
     * Counts the total number of records for the specific entity type in the database.
     * This method provides a quick way to determine the size of the dataset.
     * </p>
     *
     * @return The total count of records (rows) present in the database table
     * corresponding to the entity type {@code Invoice}. Returns {@code 0} if no
     * records are found.
     * @throws DatabaseAccessException if a database access error occurs during the counting process.
     * The underlying {@code SQLException} will be wrapped.
     */
    @Override
    public long countRecord() throws DatabaseAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_INVOICE_RECORD);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.info("Total invoice record count: {}", count);
                return count;
            }
            return 0; // Should not happen if query returns 0, but as a safeguard
        } catch (SQLException e) {
            logger.error("Error counting invoice records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error counting invoice records: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Checks if a record of the entity with the specified primary key exists in the database.
     * This method is more efficient than {@link #readRecord(Integer)} when only
     * existence needs to be verified, as it avoids fetching the entire entity object.
     * </p>
     *
     * @param id The primary key (of type {@code Integer}) of the
     * entity record to check for existence. This uniquely identifies the record.
     * @return {@code true} if a record with the specified ID exists in the database;
     * {@code false} otherwise.
     * @throws DatabaseAccessException if a database access error occurs during the check.
     * The underlying {@code SQLException} will be wrapped.
     */
    @Override
    public boolean existsById(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Invoice ID cannot be null for existence check.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EXISTS_INVOICE_BY_ID)) {

            stmt.setInt(1, id);
            logger.debug("Executing existsById invoice query for ID: {}", id);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean exists = rs.next();
                logger.info("Invoice with ID {} exists: {}", id, exists);
                return exists;
            }
        } catch (SQLException e) {
            logger.error("Error checking existence of invoice record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error checking existence of invoice record: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Updates an existing record of the entity in the database.
     * This method is used to modify the persistent state of an entity.
     * </p>
     *
     * <p>
     * For the update operation to succeed, the {@code entity} object passed as a parameter
     * *must* have its primary key field set. This ID is used to identify which existing
     * record in the database should be updated. All other fields in the {@code entity}
     * object will be used to update the corresponding columns in the database.
     * </p>
     *
     * @param invoice The invoice object (instance of type {@code Invoice}) with its primary key
     * set and updated values for other fields.
     * @throws DatabaseAccessException if a database access error occurs during the update process,
     * if no record with the given primary key is found to update,
     * or if the update operation otherwise fails (e.g., due to constraints).
     * The underlying {@code SQLException} will be wrapped.
     */
    @Override
    public void updateRecord(Invoice invoice) throws DatabaseAccessException {
        Objects.requireNonNull(invoice, "Invoice object cannot be null for update.");
        Objects.requireNonNull(invoice.getInvoiceID(), "Invoice ID must not be null for update.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_INVOICE_RECORD)) {

            stmt.setInt(1, invoice.getVehicle().getVehicleID()); // VehicleID_FK
            stmt.setString(2, invoice.getInvoiceType().getDbValue());
            stmt.setString(3, invoice.getInvoiceDate().format(CUSTOM_DATE_FORMATTER));
            stmt.setString(4, invoice.getDueDate().format(CUSTOM_DATE_FORMATTER));
            stmt.setString(5, invoice.getPeriodLabel().format(CUSTOM_PERIOD_LABEL_FORMATTER));
            stmt.setString(6, invoice.getPaymentStatus().getDbValue()); // New field
            stmt.setInt(7, invoice.getIsActive() ? 1 : 0);
            stmt.setString(8, invoice.getDeletedAt() != null ? invoice.getDeletedAt().format(CUSTOM_DATETIME_FORMATTER) : null);
            stmt.setString(9, invoice.getNotes());
            stmt.setInt(10, invoice.getInvoiceID()); // WHERE clause

            logger.debug("Executing update invoice query for ID: {}", invoice.getInvoiceID());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No invoice found with ID: {} for update. Update operation resulted in 0 affected rows.", invoice.getInvoiceID());
                throw new DatabaseAccessException("Invoice with ID " + invoice.getInvoiceID() + " not found for update.");
            } else {
                logger.info("Successfully updated invoice with ID: {}", invoice.getInvoiceID());
            }
        } catch (SQLException e) {
            logger.error("Error updating invoice record with ID {}: {}", invoice.getInvoiceID(), e.getMessage(), e);
            throw new DatabaseAccessException("Error updating invoice record: " + e.getMessage(), e);
        }
    }
}