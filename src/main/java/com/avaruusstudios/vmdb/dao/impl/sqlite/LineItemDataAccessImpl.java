package com.avaruusstudios.vmdb.dao.impl.sqlite;

import com.avaruusstudios.vmdb.dao.DatabaseAccessException;
import com.avaruusstudios.vmdb.dao.LineItemDataAccess;
import com.avaruusstudios.vmdb.db.DatabaseManager;
import com.avaruusstudios.vmdb.db.QueryLoader;
import com.avaruusstudios.vmdb.model.LineItem;
import com.avaruusstudios.vmdb.model.Invoice;
import com.avaruusstudios.vmdb.model.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types; // Import for setNull
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * {@code LineItemDataAccessImpl} provides a concrete implementation of the {@link LineItemDataAccess}
 * interface, specifically designed for SQLite databases. This class handles all CRUD (Create, Read, Update, Delete)
 * and specialized data access operations for {@link LineItem} entities.
 * </p>
 *
 * <p>
 * This implementation is updated to support **soft deletion** of {@link LineItem} records by managing
 * the {@code IsActive} and {@code DeletedAt} columns, ensuring historical data integrity while logically
 * removing records from active views.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.2
 * Created On: 2025-07-25
 * Updated On: 2025-09-20
 *
 * @see LineItemDataAccess
 * @see LineItem
 * @see DatabaseManager
 */
public class LineItemDataAccessImpl implements LineItemDataAccess {

    /**
     * SLF4J Logger for logging informational messages, warnings, and errors within the {@code LineItemDataAccessImpl} class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LineItemDataAccessImpl.class);

    /**
     * DateTimeFormatter for parsing and formatting {@code LocalDateTime} objects to/from database strings.
     */
    private static final DateTimeFormatter CUSTOM_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // SQL Query Constants
    /** SQL query to insert a new line item record. */
    private static final String SQL_CREATE_LINE_ITEM_RECORD;
    /** SQL query to read a single line item record by its ID. */
    private static final String SQL_READ_LINE_ITEM_RECORD;
    /** SQL query to read all line item records. */
    private static final String SQL_READ_ALL_LINE_ITEM_RECORD;
    /** SQL query to select all active line item records. */
    private static final String SQL_READ_ACTIVE_LINE_ITEM_RECORD;
    /** SQL query to check if a line item record exists by its ID. */
    private static final String SQL_EXISTS_LINE_ITEM_BY_ID;
    /** SQL query to update an existing line item record. */
    private static final String SQL_UPDATE_LINE_ITEM_RECORD;
    /** SQL query to hard-delete a line item record. */
    private static final String SQL_DELETE_LINE_ITEM_HARD;
    /** SQL query to soft-delete a line item record. */
    private static final String SQL_DELETE_LINE_ITEM_SOFT;
    /** SQL query to count all line item records. */
    private static final String SQL_COUNT_LINE_ITEM_RECORD;
    /** SQL query to count all active line item records. */
    private static final String SQL_COUNT_ACTIVE_LINE_ITEM_RECORD;
    /** SQL query to find line items by invoice ID. */
    private static final String SQL_FIND_BY_INVOICE_ID;
    /** SQL query to find line items by participant ID. */
    private static final String SQL_FIND_BY_PARTICIPANT_ID;
    /** SQL query to find unpaid line items for a participant. */
    private static final String SQL_FIND_UNPAID_BY_PARTICIPANT;
    /** SQL query to soft-delete all line items for an invoice. */
    private static final String SQL_DELETE_BY_INVOICE_ID; // Assuming this query is now an UPDATE for soft-delete

    static {
        try {
            SQL_CREATE_LINE_ITEM_RECORD = QueryLoader.getQuery("line_item/insertLineItem.sql");
            SQL_READ_LINE_ITEM_RECORD = QueryLoader.getQuery("line_item/selectLineItemById.sql");
            SQL_READ_ALL_LINE_ITEM_RECORD = QueryLoader.getQuery("line_item/selectAllLineItems.sql");
            SQL_READ_ACTIVE_LINE_ITEM_RECORD = "SELECT * FROM LineItems WHERE IsActive = 1";
            SQL_EXISTS_LINE_ITEM_BY_ID = QueryLoader.getQuery("line_item/existById.sql");
            SQL_UPDATE_LINE_ITEM_RECORD = QueryLoader.getQuery("line_item/updateLineItem.sql");
            SQL_DELETE_LINE_ITEM_HARD = QueryLoader.getQuery("line_item/deleteLineItemHard.sql");
            SQL_DELETE_LINE_ITEM_SOFT = QueryLoader.getQuery("line_item/deleteLineItemSoft.sql");
            SQL_COUNT_LINE_ITEM_RECORD = QueryLoader.getQuery("line_item/countLineItems.sql");
            SQL_COUNT_ACTIVE_LINE_ITEM_RECORD = "SELECT COUNT(*) FROM LineItems WHERE IsActive = 1";
            SQL_FIND_BY_INVOICE_ID = QueryLoader.getQuery("line_item/selectLineItemsByInvoiceId.sql");
            SQL_FIND_BY_PARTICIPANT_ID = QueryLoader.getQuery("line_item/selectLineItemsByParticipantId.sql");
            SQL_FIND_UNPAID_BY_PARTICIPANT = QueryLoader.getQuery("line_item/selectUnpaidLineItemsByParticipantId.sql");
            SQL_DELETE_BY_INVOICE_ID = QueryLoader.getQuery("line_item/softDeleteLineItemsByInvoiceId.sql"); // Assume file renamed/logic changed to soft-delete
            logger.info("All SQL queries for LineItemDataAccessImpl loaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load one or more SQL queries for LineItemDataAccessImpl. Check .sql files and paths.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    // --- Private Helper Methods ---

    /**
     * <p>
     * Maps a single row from a JDBC {@link ResultSet} to a {@link LineItem} model object.
     * </p>
     * <p>
     * This method is responsible for parsing all database column values, including the
     * soft-delete fields ({@code IsActive} and {@code DeletedAt}), and correctly
     * setting foreign key objects ({@code Invoice}, {@code Participant}).
     * </p>
     *
     * @param rs The {@link ResultSet} pointing to the current row to be mapped.
     * @return A fully populated {@link LineItem} object.
     * @throws SQLException If a column access error or data parsing error occurs.
     */
    private LineItem mapResultSetToLineItem(ResultSet rs) throws SQLException {
        LineItem lineItem = new LineItem();
        try {
            lineItem._setLineItemID(rs.getInt("LineItemID"));

            Invoice invoice = new Invoice();
            invoice._setInvoiceID(rs.getInt("InvoiceID_FK"));
            lineItem.setInvoice(invoice);

            Participant participant = new Participant();
            participant._setParticipantID(rs.getInt("ParticipantID_FK"));
            lineItem.setParticipant(participant);

            lineItem.setBenefitPayment(rs.getBigDecimal("BenefitPayment"));
            lineItem.setPersonalPayment(rs.getBigDecimal("PersonalPayment"));
            lineItem.setIsPaid(rs.getInt("IsPaid") == 1);
            lineItem.setNotes(rs.getString("Notes"));
            lineItem._setDateCreated(LocalDateTime.parse(rs.getString("DateCreated"), CUSTOM_DATETIME_FORMATTER));

            // Map Soft-Delete Fields
            lineItem.setIsActive(rs.getInt("IsActive") == 1);
            String deletedAtString = rs.getString("DeletedAt");
            if (deletedAtString != null) {
                lineItem.setDeletedAt(LocalDateTime.parse(deletedAtString, CUSTOM_DATETIME_FORMATTER));
            } else {
                lineItem.setDeletedAt(null);
            }

            logger.debug("Successfully mapped ResultSet to LineItem object with ID: {}", lineItem.getLineItemID());
        } catch (Exception e) {
            logger.error("Error mapping ResultSet to LineItem: {}", e.getMessage(), e);
            throw new SQLException("Failed to map ResultSet to LineItem: " + e.getMessage(), e);
        }
        return lineItem;
    }

    // --- Public Interface Methods (From LineItemDataAccess) ---

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LineItem> find(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Line Item ID cannot be null for read operation.");
        // Implementation remains unchanged
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_LINE_ITEM_RECORD)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLineItem(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error reading line item record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error reading line item record: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LineItem> findAll() throws DatabaseAccessException {
        List<LineItem> lineItems = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_READ_ALL_LINE_ITEM_RECORD)) {
            while (rs.next()) {
                lineItems.add(mapResultSetToLineItem(rs));
            }
            logger.info("Successfully read all {} line item records.", lineItems.size());
        } catch (SQLException e) {
            logger.error("Error reading all line item records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error reading all line item records: " + e.getMessage(), e);
        }
        return lineItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LineItem> findActive() throws DatabaseAccessException {
        List<LineItem> activeLineItems = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_ACTIVE_LINE_ITEM_RECORD);
             ResultSet rs = stmt.executeQuery()) {

            logger.debug("Executing read active line items query.");
            while (rs.next()) {
                activeLineItems.add(mapResultSetToLineItem(rs));
            }
            logger.info("Found {} active line items.", activeLineItems.size());
        } catch (SQLException e) {
            logger.error("Error reading active line item records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error reading active line item records: " + e.getMessage(), e);
        }
        return activeLineItems;
    }

    /**
     * <p>
     * Persists a new {@link LineItem} record to the database.
     * </p>
     * <p>
     * This method binds all properties of the {@link LineItem} object, including the soft-delete
     * fields ({@code IsActive} and {@code DeletedAt}), to the INSERT query. The auto-generated
     * {@code LineItemID} is then retrieved and set back onto the model object before returning.
     * </p>
     *
     * @param lineItem The {@link LineItem} entity to be created. Its ID must be {@code null}.
     * @return The created {@link LineItem} entity with its auto-generated ID populated.
     * @throws DatabaseAccessException If a database access error occurs during creation, or if no ID is returned.
     */
    @Override
    public LineItem create(LineItem lineItem) throws DatabaseAccessException {
        Objects.requireNonNull(lineItem, "Line Item object cannot be null for create operation.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CREATE_LINE_ITEM_RECORD, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, lineItem.getInvoice().getInvoiceID());
            stmt.setInt(2, lineItem.getParticipant().getParticipantID());
            stmt.setBigDecimal(3, lineItem.getBenefitPayment());
            stmt.setBigDecimal(4, lineItem.getPersonalPayment());
            stmt.setInt(5, lineItem.getIsPaid() ? 1 : 0);

            // Bind IsActive
            stmt.setInt(6, lineItem.getIsActive() ? 1 : 0);

            // Bind DeletedAt - Can be null
            if (lineItem.getDeletedAt() != null) {
                stmt.setString(7, lineItem.getDeletedAt().format(CUSTOM_DATETIME_FORMATTER));
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }

            stmt.setString(8, lineItem.getNotes());

            logger.debug("Executing create line item query...");
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("Create line item operation resulted in 0 affected rows.");
                throw new DatabaseAccessException("Creating line item failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    lineItem._setLineItemID(id);
                    logger.info("Successfully created new line item with ID: {}", id);
                    return lineItem;
                } else {
                    throw new DatabaseAccessException("Creating line item failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating new line item record: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error creating new line item record: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Updates an existing {@link LineItem} record in the database.
     * </p>
     * <p>
     * All non-ID fields of the line item are updated, including the soft-delete fields
     * ({@code IsActive} and {@code DeletedAt}), allowing for reactivation or permanent deletion tracking.
     * </p>
     *
     * @param lineItem The {@link LineItem} entity to be updated. Its ID must be non-null.
     * @return The updated {@link LineItem} entity.
     * @throws DatabaseAccessException If a database access error occurs, or if no record is found with the given ID.
     */
    @Override
    public LineItem update(LineItem lineItem) throws DatabaseAccessException {
        Objects.requireNonNull(lineItem, "Line Item object cannot be null for update operation.");
        Objects.requireNonNull(lineItem.getLineItemID(), "Line Item ID cannot be null for update operation.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_LINE_ITEM_RECORD)) {

            stmt.setInt(1, lineItem.getInvoice().getInvoiceID());
            stmt.setInt(2, lineItem.getParticipant().getParticipantID());
            stmt.setBigDecimal(3, lineItem.getBenefitPayment());
            stmt.setBigDecimal(4, lineItem.getPersonalPayment());
            stmt.setInt(5, lineItem.getIsPaid() ? 1 : 0);

            // Bind IsActive
            stmt.setInt(6, lineItem.getIsActive() ? 1 : 0);

            // Bind DeletedAt - Can be null
            if (lineItem.getDeletedAt() != null) {
                stmt.setString(7, lineItem.getDeletedAt().format(CUSTOM_DATETIME_FORMATTER));
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }

            stmt.setString(8, lineItem.getNotes());

            // WHERE clause
            stmt.setInt(9, lineItem.getLineItemID());

            logger.debug("Executing update line item query for ID: {}", lineItem.getLineItemID());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No line item found with ID: {} for update. Update operation resulted in 0 affected rows.", lineItem.getLineItemID());
                throw new DatabaseAccessException("LineItem with ID " + lineItem.getLineItemID() + " not found for update.");
            } else {
                logger.info("Successfully updated line item with ID: {}", lineItem.getLineItemID());
                return lineItem;
            }
        } catch (SQLException e) {
            logger.error("Error updating line item record with ID {}: {}", lineItem.getLineItemID(), e.getMessage(), e);
            throw new DatabaseAccessException("Error updating line item record: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * **Performs a soft-delete operation** on the {@link LineItem} record identified by the given ID.
     * </p>
     * <p>
     * The record is **not removed** from the database. Instead, this method executes an UPDATE query
     * that sets the {@code IsActive} column to **0** (false) and the {@code DeletedAt} column to the
     * **current system timestamp**. This preserves data for auditing and historical reports.
     * </p>
     *
     * @param id The unique integer ID of the line item to be soft-deleted.
     * @return {@code true} if the soft-delete update was successful and one row was affected; {@code false} otherwise.
     * @throws DatabaseAccessException If a database access error occurs during the operation.
     */
    @Override
    public boolean delete(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Line Item ID cannot be null for soft-delete operation.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_LINE_ITEM_SOFT)) {

            // 1. Bind DeletedAt (sets it to the current timestamp)
            String deletedAtString = LocalDateTime.now().format(CUSTOM_DATETIME_FORMATTER);
            stmt.setString(1, deletedAtString);

            // 2. Bind WHERE clause ID
            stmt.setInt(2, id);

            logger.debug("Executing soft delete line item query for ID: {}", id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No line item found with ID: {} for soft deletion. Operation resulted in 0 affected rows.", id);
                return false;
            } else {
                logger.info("Successfully soft-deleted line item with ID: {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error soft-deleting line item record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error soft-deleting line item record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(Integer id) throws DatabaseAccessException {
        // Implementation remains unchanged
        Objects.requireNonNull(id, "Line Item ID cannot be null for existsById operation.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EXISTS_LINE_ITEM_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking existence of line item with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error checking line item existence: " + e.getMessage(), e);
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
             ResultSet rs = stmt.executeQuery(SQL_COUNT_LINE_ITEM_RECORD)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting line item records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error counting line item records: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countActive() throws DatabaseAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_ACTIVE_LINE_ITEM_RECORD);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.info("Total active line item record count: {}", count);
                return count;
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting active line item records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error counting active line item records: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LineItem> findByInvoice(Integer invoiceId) throws DatabaseAccessException {
        // Implementation remains unchanged
        Objects.requireNonNull(invoiceId, "Invoice ID cannot be null for findByInvoice operation.");
        List<LineItem> lineItems = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_INVOICE_ID)) {
            stmt.setInt(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lineItems.add(mapResultSetToLineItem(rs));
                }
            }
            logger.info("Successfully retrieved {} line items for invoice ID: {}", lineItems.size(), invoiceId);
        } catch (SQLException e) {
            logger.error("Error finding line items by invoice ID {}: {}", invoiceId, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding line items by invoice ID: " + e.getMessage(), e);
        }
        return lineItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LineItem> findByParticipant(Integer participantId) throws DatabaseAccessException {
        // Implementation remains unchanged
        Objects.requireNonNull(participantId, "Participant ID cannot be null for findByParticipant operation.");
        List<LineItem> lineItems = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_PARTICIPANT_ID)) {
            stmt.setInt(1, participantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lineItems.add(mapResultSetToLineItem(rs));
                }
            }
            logger.info("Successfully retrieved {} line items for participant ID: {}", lineItems.size(), participantId);
        } catch (SQLException e) {
            logger.error("Error finding line items by participant ID {}: {}", participantId, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding line items by participant ID: " + e.getMessage(), e);
        }
        return lineItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LineItem> findUnpaidByParticipant(Integer participantId) throws DatabaseAccessException {
        // Implementation remains unchanged
        Objects.requireNonNull(participantId, "Participant ID cannot be null for findUnpaidByParticipant operation.");
        List<LineItem> lineItems = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_UNPAID_BY_PARTICIPANT)) {
            stmt.setInt(1, participantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lineItems.add(mapResultSetToLineItem(rs));
                }
            }
            logger.info("Successfully retrieved {} unpaid line items for participant ID: {}", lineItems.size(), participantId);
        } catch (SQLException e) {
            logger.error("Error finding unpaid line items by participant ID {}: {}", participantId, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding unpaid line items by participant ID: " + e.getMessage(), e);
        }
        return lineItems;
    }

    /**
     * <p>
     * **Performs a cascading soft-delete operation** on all {@link LineItem} records associated with the given {@code invoiceId}.
     * </p>
     * <p>
     * This method executes an UPDATE query that sets the {@code IsActive} column to **0** (false) and the
     * {@code DeletedAt} column to the **current system timestamp** for all line items belonging to the specified invoice.
     * This operation is essential for maintaining data integrity when the parent {@link Invoice} is soft-deleted.
     * </p>
     *
     * @param invoiceId The unique integer ID of the parent invoice whose line items should be soft-deleted.
     * @throws DatabaseAccessException If a database access error occurs during the operation.
     */
    @Override
    public void deleteByInvoice(Integer invoiceId) throws DatabaseAccessException {
        Objects.requireNonNull(invoiceId, "Invoice ID cannot be null for deleteByInvoice operation.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_BY_INVOICE_ID)) {

            // 1. Bind DeletedAt
            String deletedAtString = LocalDateTime.now().format(CUSTOM_DATETIME_FORMATTER);
            stmt.setString(1, deletedAtString);

            // 2. Bind WHERE clause ID
            stmt.setInt(2, invoiceId);

            logger.debug("Executing soft delete by invoice ID query for ID: {}", invoiceId);
            int affectedRows = stmt.executeUpdate();

            logger.info("Successfully soft-deleted {} line items for invoice ID: {}", affectedRows, invoiceId);

        } catch (SQLException e) {
            logger.error("Error soft-deleting line items by invoice ID {}: {}", invoiceId, e.getMessage(), e);
            throw new DatabaseAccessException("Error soft-deleting line items by invoice ID: " + e.getMessage(), e);
        }
    }
}