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
 * It interacts with the database using JDBC, preparing SQL statements, mapping {@link ResultSet}
 * rows to {@link Invoice} objects, and managing database connections through a {@link DatabaseManager}.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.2
 * Created On: 2025-07-25
 * Updated On: 2025-07-25
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
     * DateTimeFormatter for parsing and formatting `LocalDate` objects to/from database strings in "yyyy-MM-dd" format.
     */
    private static final DateTimeFormatter CUSTOM_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * DateTimeFormatter for parsing and formatting `YearMonth` objects to/from database strings in "yyyy-MM" format.
     */
    private static final DateTimeFormatter CUSTOM_PERIOD_LABEL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * DateTimeFormatter for parsing and formatting `LocalDateTime` objects to/from database strings.
     */
    private static final DateTimeFormatter CUSTOM_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // SQL Query Constants
    /** SQL query to insert a new invoice record. */
    private static final String SQL_CREATE_INVOICE_RECORD;
    /** SQL query to read a single invoice record by its ID. */
    private static final String SQL_READ_INVOICE_RECORD;
    /** SQL query to read all invoice records. */
    private static final String SQL_READ_ALL_INVOICE_RECORD;
    /** SQL query to check if an invoice record exists by its ID. */
    private static final String SQL_EXISTS_INVOICE_BY_ID;
    /** SQL query to update an existing invoice record. */
    private static final String SQL_UPDATE_INVOICE_RECORD;
    /** SQL query to hard-delete an invoice record. */
    private static final String SQL_DELETE_INVOICE_HARD;
    /** SQL query to count all invoice records. */
    private static final String SQL_COUNT_INVOICE_RECORD;
    /** SQL query to find invoices by date range. */
    private static final String SQL_FIND_INVOICES_BY_DATE_RANGE;
    /** SQL query to find invoices by invoice type. */
    private static final String SQL_FIND_INVOICES_BY_INVOICE_TYPE;

    static {
        try {
            SQL_CREATE_INVOICE_RECORD = QueryLoader.getQuery("invoice/insertInvoice.sql");
            SQL_READ_INVOICE_RECORD = QueryLoader.getQuery("invoice/selectInvoiceById.sql");
            SQL_READ_ALL_INVOICE_RECORD = QueryLoader.getQuery("invoice/selectAllInvoices.sql");
            SQL_EXISTS_INVOICE_BY_ID = QueryLoader.getQuery("invoice/existById.sql");
            SQL_UPDATE_INVOICE_RECORD = QueryLoader.getQuery("invoice/updateInvoice.sql");
            SQL_DELETE_INVOICE_HARD = QueryLoader.getQuery("invoice/deleteInvoiceHard.sql");
            SQL_COUNT_INVOICE_RECORD = QueryLoader.getQuery("invoice/countInvoices.sql");
            SQL_FIND_INVOICES_BY_DATE_RANGE = QueryLoader.getQuery("invoice/selectInvoicesByDateRange.sql");
            SQL_FIND_INVOICES_BY_INVOICE_TYPE = QueryLoader.getQuery("invoice/selectInvoicesByType.sql");
            logger.info("All SQL queries for InvoiceDataAccessImpl loaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load one or more SQL queries for InvoiceDataAccessImpl. Check .sql files and paths.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    // --- Private Helper Methods ---
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        try {
            invoice._setInvoiceID(rs.getInt("InvoiceID"));
            invoice._setDateCreated(LocalDateTime.parse(rs.getString("DateCreated"), CUSTOM_DATETIME_FORMATTER));

            Vehicle vehicle = new Vehicle();
            vehicle._setVehicleID(rs.getInt("VehicleID_FK"));
            invoice.setVehicle(vehicle);

            invoice.setInvoiceType(InvoiceType.fromDbValue(rs.getString("InvoiceType")));
            invoice.setPaymentStatus(PaymentStatus.fromDbValue(rs.getString("PaymentStatus")));
            invoice.setInvoiceDate(LocalDate.parse(rs.getString("InvoiceDate"), CUSTOM_DATE_FORMATTER));
            invoice.setDueDate(LocalDate.parse(rs.getString("DueDate"), CUSTOM_DATE_FORMATTER));
            invoice.setPeriodLabel(YearMonth.parse(rs.getString("PeriodLabel"), CUSTOM_PERIOD_LABEL_FORMATTER));
            invoice.setNotes(rs.getString("Notes"));

            logger.debug("Successfully mapped ResultSet to Invoice object with ID: {}", invoice.getInvoiceID());
        } catch (Exception e) {
            logger.error("Error mapping ResultSet to Invoice: {}", e.getMessage(), e);
            throw new SQLException("Failed to map ResultSet to Invoice: " + e.getMessage(), e);
        }
        return invoice;
    }

    // --- Public Interface Methods (From InvoiceDataAccess) ---

    @Override
    public Optional<Invoice> read(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Invoice ID cannot be null for read operation.");

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

    @Override
    public List<Invoice> readAll() throws DatabaseAccessException {
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
            stmt.setString(7, invoice.getNotes());

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
            stmt.setString(7, invoice.getNotes());
            stmt.setInt(8, invoice.getInvoiceID()); // WHERE clause

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

    @Override
    public boolean delete(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Invoice ID cannot be null for delete operation.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_INVOICE_HARD)) {
            stmt.setInt(1, id);

            logger.debug("Executing hard delete invoice query for ID: {}", id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No invoice found with ID: {} for deletion. Delete operation resulted in 0 affected rows.", id);
                throw new DatabaseAccessException("Invoice with ID " + id + " not found for deletion.");
            } else {
                logger.info("Successfully hard-deleted invoice with ID: {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error hard-deleting invoice record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error hard-deleting invoice record: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(Integer id) throws DatabaseAccessException {
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

    @Override
    public long count() throws DatabaseAccessException {
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

    @Override
    public List<Invoice> findByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseAccessException {
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

    @Override
    public List<Invoice> findByType(String invoiceType) throws DatabaseAccessException {
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