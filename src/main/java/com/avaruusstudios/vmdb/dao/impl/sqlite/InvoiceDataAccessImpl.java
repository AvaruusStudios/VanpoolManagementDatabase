package com.avaruusstudios.vmdb.dao.impl.sqlite;

import com.avaruusstudios.vmdb.dao.DatabaseAccessException;
import com.avaruusstudios.vmdb.dao.InvoiceDataAccess;
import com.avaruusstudios.vmdb.db.DatabaseManager;
import com.avaruusstudios.vmdb.db.QueryLoader;
import com.avaruusstudios.vmdb.model.Invoice;
import com.avaruusstudios.vmdb.model.InvoiceType;
import com.avaruusstudios.vmdb.model.PaymentStatus;
import com.avaruusstudios.vmdb.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * {@code InvoiceDataAccessImpl} provides a concrete implementation of the {@link InvoiceDataAccess}
 * interface, specifically designed for SQLite databases. This class handles all CRUD (Create, Read, Update, Delete)
 * and specialized data access operations for {@link Invoice} entities.
 * </p>
 *
 * <p>
 * This implementation is updated to support **soft deletion** of {@link Invoice} records by managing
 * the {@code IsActive} and {@code DeletedAt} columns, ensuring historical data integrity while logically
 * removing records from active views.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.3
 * Created On: 2025-07-25
 * Updated On: 2025-09-20
 *
 * @see InvoiceDataAccess
 * @see Invoice
 * @see DatabaseManager
 */
public class InvoiceDataAccessImpl implements InvoiceDataAccess {
    /**
     * SLF4J Logger for logging informational messages, warnings, and errors within the {@code InvoiceDataAccessImpl} class.
     */
    private static final Logger logger = LoggerFactory.getLogger(InvoiceDataAccessImpl.class);

    /**
     * DateTimeFormatter for parsing and formatting {@code LocalDate} objects to/from database strings in "yyyy-MM-dd" format.
     */
    private static final DateTimeFormatter CUSTOM_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * DateTimeFormatter for parsing and formatting {@code YearMonth} objects to/from database strings in "yyyy-MM" format.
     */
    private static final DateTimeFormatter CUSTOM_PERIOD_LABEL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * DateTimeFormatter for parsing and formatting {@code LocalDateTime} objects to/from database strings in "yyyy-MM-dd HH:mm:ss" format.
     */
    private static final DateTimeFormatter CUSTOM_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // SQL Query Constants
    private static final String SQL_CREATE_INVOICE_RECORD;
    private static final String SQL_READ_INVOICE_RECORD;
    private static final String SQL_READ_ALL_INVOICE_RECORD;
    private static final String SQL_EXISTS_INVOICE_BY_ID;
    private static final String SQL_UPDATE_INVOICE_RECORD;
    private static final String SQL_DELETE_INVOICE_HARD;
    /** SQL query for soft-deleting an invoice (sets IsActive=0, DeletedAt=NOW). */
    private static final String SQL_DELETE_INVOICE_SOFT;
    private static final String SQL_COUNT_INVOICE_RECORD;
    private static final String SQL_FIND_INVOICES_BY_DATE_RANGE;
    private static final String SQL_FIND_INVOICES_BY_INVOICE_TYPE;
    /** SQL query to find only active invoices by date range (includes WHERE IsActive = 1). */
    private static final String SQL_FIND_ACTIVE_INVOICES_BY_DATE_RANGE;

    static {
        try {
            SQL_CREATE_INVOICE_RECORD = QueryLoader.getQuery("invoice/insertInvoice.sql");
            SQL_READ_INVOICE_RECORD = QueryLoader.getQuery("invoice/selectInvoiceById.sql");
            SQL_READ_ALL_INVOICE_RECORD = QueryLoader.getQuery("invoice/selectAllInvoices.sql");
            SQL_EXISTS_INVOICE_BY_ID = QueryLoader.getQuery("invoice/existById.sql");
            SQL_UPDATE_INVOICE_RECORD = QueryLoader.getQuery("invoice/updateInvoice.sql");
            SQL_DELETE_INVOICE_HARD = QueryLoader.getQuery("invoice/deleteInvoiceHard.sql");
            SQL_DELETE_INVOICE_SOFT = QueryLoader.getQuery("invoice/deleteInvoiceSoft.sql");
            SQL_COUNT_INVOICE_RECORD = QueryLoader.getQuery("invoice/countInvoices.sql");
            SQL_FIND_INVOICES_BY_DATE_RANGE = QueryLoader.getQuery("invoice/selectInvoicesByDateRange.sql");
            SQL_FIND_INVOICES_BY_INVOICE_TYPE = QueryLoader.getQuery("invoice/selectInvoicesByType.sql");
            SQL_FIND_ACTIVE_INVOICES_BY_DATE_RANGE = QueryLoader.getQuery("invoice/selectActiveInvoicesByDateRange.sql");
            logger.info("All SQL queries for InvoiceDataAccessImpl loaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load one or more SQL queries for InvoiceDataAccessImpl. Check .sql files and paths.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    // --- Private Helper Methods ---

    /**
     * <p>
     * Maps a single row from a JDBC {@link ResultSet} to an {@link Invoice} model object.
     * </p>
     * <p>
     * This method is responsible for parsing all database column values, converting date/time strings
     * to Java {@link LocalDate}, {@link YearMonth}, {@link LocalDateTime} objects, mapping
     * database values to model enums ({@link InvoiceType}, {@link PaymentStatus}), and correctly
     * setting the soft-delete fields ({@code IsActive} and {@code DeletedAt}).
     * </p>
     *
     * @param rs The {@link ResultSet} pointing to the current row to be mapped.
     * @return A fully populated {@link Invoice} object.
     * @throws SQLException If a column access error or data parsing error occurs.
     */
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        try {
            invoice._setInvoiceID(rs.getInt("InvoiceID"));
            invoice._setDateCreated(LocalDateTime.parse(rs.getString("DateCreated"), CUSTOM_DATETIME_FORMATTER));

            // Map Foreign Key Objects (assuming minimal object instantiation here)
            Vehicle vehicle = new Vehicle();
            vehicle._setVehicleID(rs.getInt("VehicleID_FK"));
            invoice.setVehicle(vehicle);

            // Map Enums
            invoice.setInvoiceType(InvoiceType.fromDbValue(rs.getString("InvoiceType")));
            invoice.setPaymentStatus(PaymentStatus.fromDbValue(rs.getString("PaymentStatus")));

            // Map Date/Period
            invoice.setInvoiceDate(LocalDate.parse(rs.getString("InvoiceDate"), CUSTOM_DATE_FORMATTER));
            invoice.setDueDate(LocalDate.parse(rs.getString("DueDate"), CUSTOM_DATE_FORMATTER));
            invoice.setPeriodLabel(YearMonth.parse(rs.getString("PeriodLabel"), CUSTOM_PERIOD_LABEL_FORMATTER));

            // Map Soft-Delete Fields
            invoice.setIsActive(rs.getInt("IsActive") == 1);
            String deletedAtString = rs.getString("DeletedAt");
            if (deletedAtString != null) {
                invoice.setDeletedAt(LocalDateTime.parse(deletedAtString, CUSTOM_DATETIME_FORMATTER));
            } else {
                invoice.setDeletedAt(null);
            }

            // Map Notes
            invoice.setNotes(rs.getString("Notes"));

            logger.debug("Successfully mapped ResultSet to Invoice object with ID: {}", invoice.getInvoiceID());
        } catch (Exception e) {
            logger.error("Error mapping ResultSet to Invoice: {}", e.getMessage(), e);
            throw new SQLException("Failed to map ResultSet to Invoice: " + e.getMessage(), e);
        }
        return invoice;
    }

    // --- Public Interface Methods (From InvoiceDataAccess) ---

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Invoice> find(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Invoice ID cannot be null for read operation.");
        // Implementation remains unchanged, relies on updated mapResultSetToInvoice
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_INVOICE_RECORD)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error reading invoice record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error reading invoice record: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Invoice> findAll() throws DatabaseAccessException {
        // Implementation remains unchanged, relies on updated mapResultSetToInvoice
        List<Invoice> invoices = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_READ_ALL_INVOICE_RECORD)) {
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
            logger.info("Successfully read all {} invoice records.", invoices.size());
        } catch (SQLException e) {
            logger.error("Error reading all invoice records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error reading all invoice records: " + e.getMessage(), e);
        }
        return invoices;
    }

    /**
     * <p>
     * Persists a new {@link Invoice} record to the database.
     * </p>
     * <p>
     * This method binds all properties of the {@link Invoice} object, including the soft-delete
     * fields ({@code IsActive} and {@code DeletedAt}), to the INSERT query. The auto-generated
     * {@code InvoiceID} is then retrieved and set back onto the model object before returning.
     * </p>
     *
     * @param invoice The {@link Invoice} entity to be created. Its ID must be {@code null}.
     * @return The created {@link Invoice} entity with its auto-generated ID populated.
     * @throws DatabaseAccessException If a database access error occurs during creation, or if no ID is returned.
     */
    @Override
    public Invoice create(Invoice invoice) throws DatabaseAccessException {
        Objects.requireNonNull(invoice, "Invoice object cannot be null for create operation.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CREATE_INVOICE_RECORD, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, invoice.getVehicle().getVehicleID());
            stmt.setString(2, invoice.getInvoiceType().getDbValue());
            stmt.setString(3, invoice.getInvoiceDate().format(CUSTOM_DATE_FORMATTER));
            stmt.setString(4, invoice.getDueDate().format(CUSTOM_DATE_FORMATTER));
            stmt.setString(5, invoice.getPeriodLabel().format(CUSTOM_PERIOD_LABEL_FORMATTER));
            stmt.setString(6, invoice.getPaymentStatus().getDbValue());

            // Bind IsActive
            stmt.setInt(7, invoice.getIsActive() ? 1 : 0);

            // Bind DeletedAt - Can be null
            if (invoice.getDeletedAt() != null) {
                stmt.setString(8, invoice.getDeletedAt().format(CUSTOM_DATETIME_FORMATTER));
            } else {
                stmt.setNull(8, Types.VARCHAR);
            }

            stmt.setString(9, invoice.getNotes());

            logger.debug("Executing create invoice query...");
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("Create invoice operation resulted in 0 affected rows.");
                throw new DatabaseAccessException("Creating invoice failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    invoice._setInvoiceID(id);
                    logger.info("Successfully created new invoice with ID: {}", id);
                    return invoice;
                } else {
                    throw new DatabaseAccessException("Creating invoice failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating new invoice record: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error creating new invoice record: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Updates an existing {@link Invoice} record in the database.
     * </p>
     * <p>
     * All non-ID fields of the invoice are updated, including the soft-delete fields
     * ({@code IsActive} and {@code DeletedAt}), allowing for reactivation or permanent deletion tracking.
     * </p>
     *
     * @param invoice The {@link Invoice} entity to be updated. Its ID must be non-null.
     * @return The updated {@link Invoice} entity.
     * @throws DatabaseAccessException If a database access error occurs, or if no record is found with the given ID.
     */
    @Override
    public Invoice update(Invoice invoice) throws DatabaseAccessException {
        Objects.requireNonNull(invoice, "Invoice object cannot be null for update operation.");
        Objects.requireNonNull(invoice.getInvoiceID(), "Invoice ID cannot be null for update operation.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_INVOICE_RECORD)) {

            stmt.setInt(1, invoice.getVehicle().getVehicleID());
            stmt.setString(2, invoice.getInvoiceType().getDbValue());
            stmt.setString(3, invoice.getInvoiceDate().format(CUSTOM_DATE_FORMATTER));
            stmt.setString(4, invoice.getDueDate().format(CUSTOM_DATE_FORMATTER));
            stmt.setString(5, invoice.getPeriodLabel().format(CUSTOM_PERIOD_LABEL_FORMATTER));
            stmt.setString(6, invoice.getPaymentStatus().getDbValue());

            // Bind IsActive
            stmt.setInt(7, invoice.getIsActive() ? 1 : 0);

            // Bind DeletedAt - Can be null
            if (invoice.getDeletedAt() != null) {
                stmt.setString(8, invoice.getDeletedAt().format(CUSTOM_DATETIME_FORMATTER));
            } else {
                stmt.setNull(8, Types.VARCHAR);
            }

            stmt.setString(9, invoice.getNotes());

            // WHERE clause
            stmt.setInt(10, invoice.getInvoiceID());

            logger.debug("Executing update invoice query for ID: {}", invoice.getInvoiceID());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No invoice found with ID: {} for update. Update operation resulted in 0 affected rows.", invoice.getInvoiceID());
                throw new DatabaseAccessException("Invoice with ID " + invoice.getInvoiceID() + " not found for update.");
            } else {
                logger.info("Successfully updated invoice with ID: {}", invoice.getInvoiceID());
                return invoice;
            }
        } catch (SQLException e) {
            logger.error("Error updating invoice record with ID {}: {}", invoice.getInvoiceID(), e.getMessage(), e);
            throw new DatabaseAccessException("Error updating invoice record: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * **Performs a soft-delete operation** on the {@link Invoice} record identified by the given ID.
     * </p>
     * <p>
     * The record is **not removed** from the database. Instead, this method executes an UPDATE query
     * that sets the {@code IsActive} column to **0** (false) and the {@code DeletedAt} column to the
     * **current system timestamp** ({@link LocalDateTime#now()}). This preserves data for auditing and historical reports.
     * </p>
     *
     * @param id The unique integer ID of the invoice to be soft-deleted.
     * @return {@code true} if the soft-delete update was successful and one row was affected; {@code false} otherwise.
     * @throws DatabaseAccessException If a database access error occurs during the operation, or if the ID is not found.
     */
    @Override
    public boolean delete(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Invoice ID cannot be null for delete operation (soft-delete).");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_INVOICE_SOFT)) {

            // 1. Bind DeletedAt (sets it to the current timestamp)
            String deletedAtString = LocalDateTime.now().format(CUSTOM_DATETIME_FORMATTER);
            stmt.setString(1, deletedAtString);

            // 2. Bind WHERE clause ID
            stmt.setInt(2, id);

            logger.debug("Executing soft delete invoice query for ID: {}", id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No invoice found with ID: {} for soft deletion. Operation resulted in 0 affected rows.", id);
                return false;
            } else {
                logger.info("Successfully soft-deleted invoice with ID: {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error soft-deleting invoice record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error soft-deleting invoice record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(Integer id) throws DatabaseAccessException {
        // Implementation remains unchanged
        Objects.requireNonNull(id, "Invoice ID cannot be null for existsById operation.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EXISTS_INVOICE_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking existence of invoice with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error checking invoice existence: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() throws DatabaseAccessException {
        // Implementation remains unchanged
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_COUNT_INVOICE_RECORD)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting invoice records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error counting invoice records: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Invoice> findByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseAccessException {
        // Implementation remains unchanged, fetches ALL (active and soft-deleted) records
        Objects.requireNonNull(startDate, "Start date cannot be null.");
        Objects.requireNonNull(endDate, "End date cannot be null.");

        List<Invoice> invoices = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_INVOICES_BY_DATE_RANGE)) {
            stmt.setString(1, startDate.format(CUSTOM_DATE_FORMATTER));
            stmt.setString(2, endDate.format(CUSTOM_DATE_FORMATTER));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding invoices by date range ({} to {}): {}", startDate, endDate, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding invoices by date range: " + e.getMessage(), e);
        }
        return invoices;
    }

    /**
     * <p>
     * Retrieves a list of {@link Invoice} records that fall within a specified date range
     * (inclusive of start and end dates) and are currently marked as **active** (not soft-deleted).
     * </p>
     * <p>
     * This method executes a database query that includes a `WHERE IsActive = 1` clause for optimal performance.
     * </p>
     *
     * @param startDate The {@link LocalDate} representing the beginning of the date range (inclusive).
     * @param endDate   The {@link LocalDate} representing the end of the date range (inclusive).
     * @return A {@link List} of only **active** {@link Invoice} objects found within the date range.
     * Returns an empty list if no active invoices are found in the specified range.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    @Override
    public List<Invoice> findActiveByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseAccessException {
        Objects.requireNonNull(startDate, "Start date cannot be null.");
        Objects.requireNonNull(endDate, "End date cannot be null.");

        List<Invoice> invoices = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ACTIVE_INVOICES_BY_DATE_RANGE)) {
            stmt.setString(1, startDate.format(CUSTOM_DATE_FORMATTER));
            stmt.setString(2, endDate.format(CUSTOM_DATE_FORMATTER));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
            logger.info("Found {} ACTIVE invoices in date range ({} to {}).", invoices.size(), startDate, endDate);
        } catch (SQLException e) {
            logger.error("Error finding ACTIVE invoices by date range ({} to {}): {}", startDate, endDate, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding active invoices by date range: " + e.getMessage(), e);
        }
        return invoices;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Invoice> findByType(String invoiceType) throws DatabaseAccessException {
        // Implementation remains unchanged
        Objects.requireNonNull(invoiceType, "Invoice type cannot be null.");

        List<Invoice> invoices = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_INVOICES_BY_INVOICE_TYPE)) {
            stmt.setString(1, invoiceType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding invoices by type '{}': {}", invoiceType, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding invoices by type: " + e.getMessage(), e);
        }
        return invoices;
    }
}