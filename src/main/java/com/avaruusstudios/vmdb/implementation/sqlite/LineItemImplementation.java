package com.avaruusstudios.vmdb.implementation.sqlite;

import com.avaruusstudios.vmdb.data.DatabaseAccessException;
import com.avaruusstudios.vmdb.data.LineItemDataAccess;
import com.avaruusstudios.vmdb.db.DatabaseManager; // Assuming a DatabaseManager class exists
import com.avaruusstudios.vmdb.db.QueryLoader;
import com.avaruusstudios.vmdb.model.Amount;
import com.avaruusstudios.vmdb.model.Invoice;
import com.avaruusstudios.vmdb.model.LineItem;
import com.avaruusstudios.vmdb.model.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * {@code LineItemImplementation} provides a concrete implementation of the {@link LineItemDataAccess}
 * interface, specifically designed for SQLite databases. This class handles all CRUD (Create, Read, Update, Delete)
 * and specialized data access operations for {@link LineItem} entities.
 * </p>
 *
 * <p>
 * It interacts with the database using JDBC, preparing SQL statements, mapping {@link ResultSet}
 * rows to {@link LineItem} objects, and managing database connections through a {@link DatabaseManager}.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-25
 * Updated On: 2025-07-25
 *
 * @see LineItemDataAccess
 * @see LineItem
 * @see DatabaseManager
 */
public class LineItemImplementation implements LineItemDataAccess {
    /**
     * SLF4J Logger for logging informational messages, warnings, and errors within the {@code LineItemImplementation} class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LineItemImplementation.class);

    /**
     * DateTimeFormatter for parsing and formatting `LocalDateTime` objects to/from database strings in "yyyy-MM-dd HH:mm:ss" format.
     */
    private static final DateTimeFormatter CUSTOM_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // SQL Query Constants
    /** SQL query to insert a new line item record. */
    private static final String SQL_CREATE_LINE_ITEM_RECORD;
    /** SQL query to read a line item record by its ID. */
    private static final String SQL_READ_LINE_ITEM_RECORD;
    /** SQL query to read all line item records. */
    private static final String SQL_READ_ALL_LINE_ITEM_RECORD;
    /** SQL query to count all line item records. */
    private static final String SQL_COUNT_LINE_ITEM_RECORD;
    /** SQL query to check if a line item record exists by its ID. */
    private static final String SQL_EXISTS_LINE_ITEM_BY_ID;
    /** SQL query to update an existing line item record. */
    private static final String SQL_UPDATE_LINE_ITEM_RECORD;
    /** SQL query to soft-delete a line item record (set IsActive to 0 and DeletedAt). */
    private static final String SQL_DELETE_LINE_ITEM_SOFT;
    /** SQL query to find line items associated with a specific invoice ID. */
    private static final String SQL_FIND_LINE_ITEMS_BY_INVOICE_ID;
    /** SQL query to find line items associated with a specific participant ID. */
    private static final String SQL_FIND_LINE_ITEMS_BY_PARTICIPANT_ID;
    /** SQL query to find unpaid line items associated with a specific participant ID. */
    private static final String SQL_FIND_UNPAID_LINE_ITEMS_BY_PARTICIPANT_ID;
    /** SQL query to delete (hard delete or soft delete, depending on implementation) line items associated with a specific invoice ID. */
    private static final String SQL_DELETE_LINE_ITEMS_BY_INVOICE_ID;

    static {
        try {
            // Placeholder SQL queries. These would typically be loaded from .sql files.
            // You will need to create corresponding .sql files in your `queries/lineitem/` directory.
            SQL_CREATE_LINE_ITEM_RECORD = QueryLoader.getQuery("lineitem/insertLineItem.sql");
            SQL_READ_LINE_ITEM_RECORD = QueryLoader.getQuery("lineitem/selectLineItemById.sql");
            SQL_READ_ALL_LINE_ITEM_RECORD = QueryLoader.getQuery("lineitem/selectAllLineItems.sql");
            SQL_COUNT_LINE_ITEM_RECORD = QueryLoader.getQuery("lineitem/countLineItems.sql");
            SQL_EXISTS_LINE_ITEM_BY_ID = QueryLoader.getQuery("lineitem/existById.sql");
            SQL_UPDATE_LINE_ITEM_RECORD = QueryLoader.getQuery("lineitem/updateLineItem.sql");
            SQL_DELETE_LINE_ITEM_SOFT = QueryLoader.getQuery("lineitem/deleteLineItemSoft.sql");
            SQL_FIND_LINE_ITEMS_BY_INVOICE_ID = QueryLoader.getQuery("lineitem/selectLineItemsByInvoiceId.sql");
            SQL_FIND_LINE_ITEMS_BY_PARTICIPANT_ID = QueryLoader.getQuery("lineitem/selectLineItemsByParticipantId.sql");
            SQL_FIND_UNPAID_LINE_ITEMS_BY_PARTICIPANT_ID = QueryLoader.getQuery("lineitem/selectUnpaidLineItemsByParticipantId.sql");
            SQL_DELETE_LINE_ITEMS_BY_INVOICE_ID = QueryLoader.getQuery("lineitem/deleteLineItemsByInvoiceId.sql");

            logger.info("All SQL queries for LineItemImplementation loaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load one or more SQL queries for LineItemImplementation. Check .sql files and paths.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    // --- Private Helper Methods ---

    /**
     * <p>
     * Maps a row from a {@link ResultSet} to a {@link LineItem} object.
     * This private helper method centralizes the logic for converting raw database
     * column values into a populated {@code LineItem} model object, ensuring data consistency
     * and type safety across all read operations.
     * </p>
     * <p>
     * It handles:
     * <ul>
     * <li>Retrieval of the auto-generated `LineItemID` and setting it via the `_setInvoiceItemID` method.</li>
     * <li>Mapping of foreign key IDs (`InvoiceID_FK`, `ParticipantID_FK`) to {@link Invoice} and {@link Participant} objects.</li>
     * <li>Mapping of `BenefitPayment` and `PersonalPayment` to an {@link Amount} object.</li>
     * <li>Conversion of `IsPaid` and `IsActive` from `INTEGER` (0 or 1) to `boolean`.</li>
     * <li>Handling of nullable `DeletedAt` `TEXT` field, parsing it into
     * {@link LocalDateTime} objects.</li>
     * <li>String fields (`Notes`).</li>
     * </ul>
     * </p>
     *
     * @param rs The {@link ResultSet} positioned at the current row containing line item data.
     * @return A fully populated {@link LineItem} object with data retrieved from the {@code ResultSet}.
     * @throws SQLException If a database access error occurs (e.g., a column name is not found,
     * or there is a data type mismatch during retrieval).
     */
    private LineItem mapResultSetToLineItem(ResultSet rs) throws SQLException {
        LineItem lineItem = new LineItem();

        Integer lineItemId = rs.getInt("LineItemID");
        if (lineItemId > 0) { // SQLite getInt returns 0 for NULL for INTEGER PRIMARY KEY, check for positive valid ID
            lineItem._setInvoiceItemID(lineItemId); // Use the package-private setter
        }

        // Invoice FKey - Map to Invoice object (assuming a simple invoice object with just ID)
        Integer invoiceId = rs.getObject("InvoiceID_FK", Integer.class);
        if (invoiceId != null) {
            Invoice invoice = new Invoice(); // Assuming Invoice has a constructor or setter for its ID
            invoice._setInvoiceID(invoiceId);
            lineItem.setInvoice(invoice);
        } else {
            throw new SQLException("InvoiceID_FK cannot be null for LineItemID: " + lineItemId);
        }

        // Participant FKey - Map to Participant object (assuming a simple participant object with just ID)
        Integer participantId = rs.getObject("ParticipantID_FK", Integer.class);
        if (participantId != null) {
            Participant participant = new Participant(); // Assuming Participant has a constructor or setter for its ID
            // For Participant, you might have _setParticipantID if it follows similar convention
            // Or a constructor like new Participant(participantId);
            participant._setParticipantID(participantId); // Assuming similar naming convention as _setInvoiceID
            lineItem.setParticipant(participant);
        } else {
            throw new SQLException("ParticipantID_FK cannot be null for LineItemID: " + lineItemId);
        }

        // AmountDue (BenefitPayment, PersonalPayment)
        // Ensure Amount class has a constructor that accepts BigDecimal for benefit and personal payments.
        Amount amountDue = new Amount(
                rs.getBigDecimal("BenefitPayment"),
                rs.getBigDecimal("PersonalPayment")
        );
        lineItem.setAmountDue(amountDue);

        lineItem.setIsPaid(rs.getInt("IsPaid") == 1);
        lineItem.setIsActive(rs.getInt("IsActive") == 1);

        String deletedAtStr = rs.getString("DeletedAt");
        lineItem.setDeletedAt(deletedAtStr != null && !deletedAtStr.isEmpty() ?
                LocalDateTime.parse(deletedAtStr, CUSTOM_DATETIME_FORMATTER) : null);

        lineItem.setNotes(rs.getString("Notes"));

        return lineItem;
    }

    // --- Interface Implementations ---

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LineItem> findByInvoiceId(Integer invoiceId) throws DatabaseAccessException {
        List<LineItem> lineItems = new ArrayList<>();
        Objects.requireNonNull(invoiceId, "Invoice ID cannot be null for finding line items.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_LINE_ITEMS_BY_INVOICE_ID)) {

            stmt.setInt(1, invoiceId);
            logger.debug("Executing query: {} with invoice ID: {}", SQL_FIND_LINE_ITEMS_BY_INVOICE_ID, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lineItems.add(mapResultSetToLineItem(rs));
                }
            }
            logger.info("Found {} line items for invoice ID: {}", lineItems.size(), invoiceId);
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
    public List<LineItem> findByParticipantId(Integer participantId) throws DatabaseAccessException {
        List<LineItem> lineItems = new ArrayList<>();
        Objects.requireNonNull(participantId, "Participant ID cannot be null for finding line items.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_LINE_ITEMS_BY_PARTICIPANT_ID)) {

            stmt.setInt(1, participantId);
            logger.debug("Executing query: {} with participant ID: {}", SQL_FIND_LINE_ITEMS_BY_PARTICIPANT_ID, participantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lineItems.add(mapResultSetToLineItem(rs));
                }
            }
            logger.info("Found {} line items for participant ID: {}", lineItems.size(), participantId);
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
    public List<LineItem> findUnpaidByParticipantId(Integer participantId) throws DatabaseAccessException {
        List<LineItem> lineItems = new ArrayList<>();
        Objects.requireNonNull(participantId, "Participant ID cannot be null for finding unpaid line items.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_UNPAID_LINE_ITEMS_BY_PARTICIPANT_ID)) {

            stmt.setInt(1, participantId);
            logger.debug("Executing query: {} with participant ID: {}", SQL_FIND_UNPAID_LINE_ITEMS_BY_PARTICIPANT_ID, participantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lineItems.add(mapResultSetToLineItem(rs));
                }
            }
            logger.info("Found {} unpaid line items for participant ID: {}", lineItems.size(), participantId);
        } catch (SQLException e) {
            logger.error("Error finding unpaid line items by participant ID {}: {}", participantId, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding unpaid line items by participant ID: " + e.getMessage(), e);
        }
        return lineItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByInvoiceId(Integer invoiceId) throws DatabaseAccessException {
        Objects.requireNonNull(invoiceId, "Invoice ID cannot be null for deleting line items.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_LINE_ITEMS_BY_INVOICE_ID)) {

            stmt.setInt(1, invoiceId);
            logger.debug("Executing delete line items by invoice ID query for InvoiceID: {}", invoiceId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No line items found for Invoice ID: {} for deletion.", invoiceId);
            } else {
                logger.info("Successfully deleted {} line items for Invoice ID: {}", affectedRows, invoiceId);
            }
        } catch (SQLException e) {
            logger.error("Error deleting line items by Invoice ID {}: {}", invoiceId, e.getMessage(), e);
            throw new DatabaseAccessException("Error deleting line items by Invoice ID: " + e.getMessage(), e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public LineItem createRecord(LineItem lineItem) throws DatabaseAccessException {
        Objects.requireNonNull(lineItem, "LineItem object cannot be null for creation.");
        Objects.requireNonNull(lineItem.getInvoice(), "Invoice cannot be null for LineItem creation.");
        Objects.requireNonNull(lineItem.getParticipant(), "Participant cannot be null for LineItem creation.");
        Objects.requireNonNull(lineItem.getAmountDue(), "AmountDue cannot be null for LineItem creation.");
        if (lineItem.getInvoiceItemID() != null) {
            throw new IllegalArgumentException("LineItem ID must be null for new record creation (auto-generated).");
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CREATE_LINE_ITEM_RECORD, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, lineItem.getInvoice().getInvoiceID()); // InvoiceID_FK
            stmt.setInt(2, lineItem.getParticipant().getParticipantID()); // ParticipantID_FK
            stmt.setBigDecimal(3, lineItem.getAmountDue().getBenefitAmountDue()); // BenefitPayment
            stmt.setBigDecimal(4, lineItem.getAmountDue().getPersonalAmountDue()); // PersonalPayment
            stmt.setInt(5, lineItem.getIsPaid() ? 1 : 0);
            stmt.setInt(6, lineItem.getIsActive() ? 1 : 0);
            stmt.setString(7, lineItem.getDeletedAt() != null ? lineItem.getDeletedAt().format(CUSTOM_DATETIME_FORMATTER) : null);
            stmt.setString(8, lineItem.getNotes());

            logger.debug("Executing insert line item query for InvoiceID: {}, ParticipantID: {}",
                    lineItem.getInvoice().getInvoiceID(), lineItem.getParticipant().getParticipantID());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.error("Creating line item failed, no rows affected for InvoiceID: {}, ParticipantID: {}",
                        lineItem.getInvoice().getInvoiceID(), lineItem.getParticipant().getParticipantID());
                throw new DatabaseAccessException("Creating line item failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Integer generatedId = generatedKeys.getInt(1);
                    lineItem._setInvoiceItemID(generatedId); // Set the auto-generated ID
                    logger.info("Successfully created line item with ID: {}", generatedId);
                    return lineItem;
                } else {
                    logger.error("Creating line item failed, no ID obtained for InvoiceID: {}, ParticipantID: {}",
                            lineItem.getInvoice().getInvoiceID(), lineItem.getParticipant().getParticipantID());
                    throw new DatabaseAccessException("Creating line item failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating line item record: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error creating line item record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
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
     */
    @Override
    public void deleteRecord(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "LineItem ID cannot be null for deletion.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_LINE_ITEM_SOFT)) {

            stmt.setString(1, LocalDateTime.now().format(CUSTOM_DATETIME_FORMATTER)); // Set DeletedAt
            stmt.setInt(2, id); // Where LineItemID = ?

            logger.debug("Executing soft-delete line item query for ID: {}", id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No line item found with ID: {} for soft-deletion.", id);
            } else {
                logger.info("Successfully soft-deleted line item with ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Error soft-deleting line item record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error soft-deleting line item record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The result is wrapped in an {@code Optional<LineItem>} to clearly indicate whether
     * an entity matching the given ID was found. This prevents {@code NullPointerExceptions}
     * and encourages explicit handling of cases where the record might not exist.
     * </p>
     */
    @Override
    public Optional<LineItem> readRecord(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "LineItem ID cannot be null for reading.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_LINE_ITEM_RECORD)) {

            stmt.setInt(1, id);
            logger.debug("Executing read line item query for ID: {}", id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LineItem lineItem = mapResultSetToLineItem(rs);
                    logger.info("Found line item with ID: {}", id);
                    return Optional.of(lineItem);
                } else {
                    logger.info("No line item found with ID: {}", id);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Error reading line item record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error reading line item record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The order of the returned records is dependent on the underlying database and
     * the SQL query used by the concrete implementation (e.g., if no specific
     * ORDER BY clause is applied, the order might not be guaranteed).
     * </p>
     */
    @Override
    public List<LineItem> readRecordAll() throws DatabaseAccessException {
        List<LineItem> lineItems = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_ALL_LINE_ITEM_RECORD);
             ResultSet rs = stmt.executeQuery()) {

            logger.debug("Executing read all line items query.");
            while (rs.next()) {
                lineItems.add(mapResultSetToLineItem(rs));
            }
            logger.info("Found {} total line items.", lineItems.size());
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
    public long countRecord() throws DatabaseAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_LINE_ITEM_RECORD);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.info("Total line item record count: {}", count);
                return count;
            }
            return 0; // Should not happen if query returns 0, but as a safeguard
        } catch (SQLException e) {
            logger.error("Error counting line item records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error counting line item records: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "LineItem ID cannot be null for existence check.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EXISTS_LINE_ITEM_BY_ID)) {

            stmt.setInt(1, id);
            logger.debug("Executing existsById line item query for ID: {}", id);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean exists = rs.next();
                logger.info("LineItem with ID {} exists: {}", id, exists);
                return exists;
            }
        } catch (SQLException e) {
            logger.error("Error checking existence of line item record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error checking existence of line item record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * For the update operation to succeed, the {@code lineItem} object passed as a parameter
     * *must* have its primary key field set. This ID is used to identify which existing
     * record in the database should be updated. All other fields in the {@code lineItem}
     * object will be used to update the corresponding columns in the database.
     * </p>
     */
    @Override
    public void updateRecord(LineItem lineItem) throws DatabaseAccessException {
        Objects.requireNonNull(lineItem, "LineItem object cannot be null for update.");
        Objects.requireNonNull(lineItem.getInvoiceItemID(), "LineItem ID must not be null for update.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_LINE_ITEM_RECORD)) {

            stmt.setInt(1, lineItem.getInvoice().getInvoiceID()); // InvoiceID_FK
            stmt.setInt(2, lineItem.getParticipant().getParticipantID()); // ParticipantID_FK
            stmt.setBigDecimal(3, lineItem.getAmountDue().getBenefitAmountDue()); // BenefitPayment
            stmt.setBigDecimal(4, lineItem.getAmountDue().getPersonalAmountDue()); // PersonalPayment
            stmt.setInt(5, lineItem.getIsPaid() ? 1 : 0);
            stmt.setInt(6, lineItem.getIsActive() ? 1 : 0);
            stmt.setString(7, lineItem.getDeletedAt() != null ? lineItem.getDeletedAt().format(CUSTOM_DATETIME_FORMATTER) : null);
            stmt.setString(8, lineItem.getNotes());
            stmt.setInt(9, lineItem.getInvoiceItemID()); // WHERE clause

            logger.debug("Executing update line item query for ID: {}", lineItem.getInvoiceItemID());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No line item found with ID: {} for update. Update operation resulted in 0 affected rows.", lineItem.getInvoiceItemID());
                throw new DatabaseAccessException("LineItem with ID " + lineItem.getInvoiceItemID() + " not found for update.");
            } else {
                logger.info("Successfully updated line item with ID: {}", lineItem.getInvoiceItemID());
            }
        } catch (SQLException e) {
            logger.error("Error updating line item record with ID {}: {}", lineItem.getInvoiceItemID(), e.getMessage(), e);
            throw new DatabaseAccessException("Error updating line item record: " + e.getMessage(), e);
        }
    }
}