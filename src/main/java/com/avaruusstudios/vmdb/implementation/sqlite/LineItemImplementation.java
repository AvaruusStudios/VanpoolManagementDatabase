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
    private static final String SQL_CREATE_LINE_ITEM_RECORD;
    private static final String SQL_READ_LINE_ITEM_RECORD;
    private static final String SQL_READ_ALL_LINE_ITEM_RECORD;
    private static final String SQL_COUNT_LINE_ITEM_RECORD;
    private static final String SQL_EXISTS_LINE_ITEM_BY_ID;
    private static final String SQL_UPDATE_LINE_ITEM_RECORD;
    private static final String SQL_DELETE_LINE_ITEM_SOFT;
    private static final String SQL_FIND_LINE_ITEMS_BY_INVOICE_ID;
    private static final String SQL_FIND_LINE_ITEMS_BY_PARTICIPANT_ID;
    private static final String SQL_FIND_UNPAID_LINE_ITEMS_BY_PARTICIPANT_ID;
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
     * <p>
     * Inserts a new record of the specified entity into the database.
     * This method is used to persist a new instance of an entity.
     * </p>
     *
     * <p>
     * If the primary key of the entity is auto-generated by the database (e.g.,
     * an auto-incrementing ID), the {@code lineItem} object passed as a parameter
     * typically should *not* have its primary key field set beforehand. Upon successful
     * insertion, the returned entity object will include the newly generated primary key.
     * </p>
     *
     * @param lineItem The line item object (instance of type {@code LineItem}) to be created and persisted.
     * Its non-primary key fields should contain the data to be stored.
     * @return The created entity object, which may include a database-generated primary key
     * if applicable. This object represents the state of the entity as stored.
     * @throws DatabaseAccessException if a database access error occurs during the insertion process,
     * if constraints are violated (e.g., unique key violation),
     * or if the creation operation otherwise fails. The underlying
     * {@code SQLException} will be wrapped within this custom exception.
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
     * <p>
     * Retrieves a single record of the entity from the database based on its primary key.
     * This method provides a way to fetch a specific entity instance.
     * </p>
     *
     * <p>
     * The result is wrapped in an {@code Optional<LineItem>} to clearly indicate whether
     * an entity matching the given ID was found. This prevents {@code NullPointerExceptions}
     * and encourages explicit handling of cases where the record might not exist.
     * </p>
     *
     * @param id The primary key (of type {@code Integer}) of the
     * entity record to retrieve. This uniquely identifies the record in the database.
     * @return An {@code Optional<LineItem>} containing the entity object if a record with the
     * specified ID is found in the database; otherwise, an empty {@code Optional}.
     * @throws DatabaseAccessException if a database access error occurs during the retrieval process.
     * The underlying {@code SQLException} will be wrapped.
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
     * @return A {@code List<LineItem>} containing all entity objects found in the corresponding
     * database table. Returns an empty list if no records are present.
     * The list will not be {@code null}.
     * @throws DatabaseAccessException if a database access error occurs during the retrieval process.
     * The underlying {@code SQLException} will be wrapped.
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
     * <p>
     * Counts the total number of records for the specific entity type in the database.
     * This method provides a quick way to determine the size of the dataset.
     * </p>
     *
     * @return The total count of records (rows) present in the database table
     * corresponding to the entity type {@code LineItem}. Returns {@code 0} if no
     * records are found.
     * @throws DatabaseAccessException if a database access error occurs during the counting process.
     * The underlying {@code SQLException} will be wrapped.
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
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting line item records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error counting line item records: " + e.getMessage(), e);
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
        Objects.requireNonNull(id, "LineItem ID cannot be null for existence check.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EXISTS_LINE_ITEM_BY_ID)) {

            stmt.setInt(1, id);
            logger.debug("Executing existsById line item query for ID: {}", id);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean exists = rs.next();
                logger.info("Line item with ID {} exists: {}", id, exists);
                return exists;
            }
        } catch (SQLException e) {
            logger.error("Error checking existence of line item record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error checking existence of line item record: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Updates an existing record of the entity in the database.
     * This method is used to modify the persistent state of an entity.
     * </p>
     *
     * <p>
     * For the update operation to succeed, the {@code lineItem} object passed as a parameter
     * *must* have its primary key field set. This ID is used to identify which existing
     * record in the database should be updated. All other fields in the {@code lineItem}
     * object will be used to update the corresponding columns in the database.
     * </p>
     *
     * @param lineItem The line item object (instance of type {@code LineItem}) with its primary key
     * set and updated values for other fields.
     * @throws DatabaseAccessException if a database access error occurs during the update process,
     * if no record with the given primary key is found to update,
     * or if the update operation otherwise fails (e.g., due to constraints).
     * The underlying {@code SQLException} will be wrapped.
     */
    @Override
    public void updateRecord(LineItem lineItem) throws DatabaseAccessException {
        Objects.requireNonNull(lineItem, "LineItem object cannot be null for update.");
        Objects.requireNonNull(lineItem.getInvoiceItemID(), "LineItem ID must not be null for update.");
        Objects.requireNonNull(lineItem.getInvoice(), "Invoice cannot be null for LineItem update.");
        Objects.requireNonNull(lineItem.getParticipant(), "Participant cannot be null for LineItem update.");
        Objects.requireNonNull(lineItem.getAmountDue(), "AmountDue cannot be null for LineItem update.");

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

    /**
     * <p>
     * Retrieves a list of {@link LineItem} records that belong to a specific invoice.
     * </p>
     *
     * @param invoiceId The ID of the invoice whose line items are to be retrieved.
     * @return A {@link List} of {@link LineItem} objects associated with the given invoice ID.
     * Returns an empty list if no line items are found for the invoice.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    @Override
    public List<LineItem> findByInvoiceId(Integer invoiceId) throws DatabaseAccessException {
        Objects.requireNonNull(invoiceId, "Invoice ID cannot be null for finding line items by invoice ID.");
        List<LineItem> lineItems = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_LINE_ITEMS_BY_INVOICE_ID)) {

            stmt.setInt(1, invoiceId);

            logger.debug("Executing findByInvoiceId query for InvoiceID: {}", invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lineItems.add(mapResultSetToLineItem(rs));
                }
            }
            logger.info("Found {} line items for InvoiceID: {}", lineItems.size(), invoiceId);
        } catch (SQLException e) {
            logger.error("Error finding line items by InvoiceID {}: {}", invoiceId, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding line items by InvoiceID: " + e.getMessage(), e);
        }
        return lineItems;
    }

    /**
     * <p>
     * Retrieves a list of {@link LineItem} records that are associated with a specific participant.
     * </p>
     *
     * @param participantId The ID of the participant whose line items are to be retrieved.
     * @return A {@link List} of {@link LineItem} objects associated with the given participant ID.
     * Returns an empty list if no line items are found for the participant.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    @Override
    public List<LineItem> findByParticipantId(Integer participantId) throws DatabaseAccessException {
        Objects.requireNonNull(participantId, "Participant ID cannot be null for finding line items by participant ID.");
        List<LineItem> lineItems = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_LINE_ITEMS_BY_PARTICIPANT_ID)) {

            stmt.setInt(1, participantId);

            logger.debug("Executing findByParticipantId query for ParticipantID: {}", participantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lineItems.add(mapResultSetToLineItem(rs));
                }
            }
            logger.info("Found {} line items for ParticipantID: {}", lineItems.size(), participantId);
        } catch (SQLException e) {
            logger.error("Error finding line items by ParticipantID {}: {}", participantId, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding line items by ParticipantID: " + e.getMessage(), e);
        }
        return lineItems;
    }

    /**
     * <p>
     * Retrieves a list of {@link LineItem} records for a specific participant that are marked as unpaid.
     * </p>
     *
     * @param participantId The ID of the participant.
     * @return A {@link List} of unpaid {@link LineItem} objects for the specified participant.
     * Returns an empty list if no unpaid line items are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    @Override
    public List<LineItem> findUnpaidLineItemsByParticipantId(Integer participantId) throws DatabaseAccessException {
        Objects.requireNonNull(participantId, "Participant ID cannot be null for finding unpaid line items.");
        List<LineItem> lineItems = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_UNPAID_LINE_ITEMS_BY_PARTICIPANT_ID)) {

            stmt.setInt(1, participantId);

            logger.debug("Executing findUnpaidLineItemsByParticipantId query for ParticipantID: {}", participantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lineItems.add(mapResultSetToLineItem(rs));
                }
            }
            logger.info("Found {} unpaid line items for ParticipantID: {}", lineItems.size(), participantId);
        } catch (SQLException e) {
            logger.error("Error finding unpaid line items by ParticipantID {}: {}", participantId, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding unpaid line items by ParticipantID: " + e.getMessage(), e);
        }
        return lineItems;
    }

    /**
     * <p>
     * Deletes all {@link LineItem} records that belong to a specific invoice.
     * This method is useful when managing invoice updates or deletions that
     * require cascading changes to associated line items. This is a hard delete
     * operation from the database.
     * </p>
     *
     * @param invoiceId The ID of the invoice whose line items are to be deleted.
     * @throws DatabaseAccessException If a database access error occurs during deletion.
     */
    @Override
    public void deleteByInvoiceId(Integer invoiceId) throws DatabaseAccessException {
        Objects.requireNonNull(invoiceId, "Invoice ID cannot be null for deleting line items by invoice ID.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_LINE_ITEMS_BY_INVOICE_ID)) {

            stmt.setInt(1, invoiceId);

            logger.debug("Executing deleteByInvoiceId query for InvoiceID: {}", invoiceId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No line items found for InvoiceID: {} to delete.", invoiceId);
            } else {
                logger.info("Successfully deleted {} line items for InvoiceID: {}", affectedRows, invoiceId);
            }
        } catch (SQLException e) {
            logger.error("Error deleting line items by InvoiceID {}: {}", invoiceId, e.getMessage(), e);
            throw new DatabaseAccessException("Error deleting line items by InvoiceID: " + e.getMessage(), e);
        }
    }
}