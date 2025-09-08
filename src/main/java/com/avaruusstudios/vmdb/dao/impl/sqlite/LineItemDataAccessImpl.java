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
 * It interacts with the database using JDBC, preparing SQL statements, mapping {@link ResultSet}
 * rows to {@link LineItem} objects, and managing database connections through a {@link DatabaseManager}.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.1
 * Created On: 2025-07-25
 * Updated On: 2025-07-25
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
    /** SQL query to find line items by invoice ID. */
    private static final String SQL_FIND_BY_INVOICE_ID;
    /** SQL query to find line items by participant ID. */
    private static final String SQL_FIND_BY_PARTICIPANT_ID;
    /** SQL query to find unpaid line items for a participant. */
    private static final String SQL_FIND_UNPAID_BY_PARTICIPANT;
    /** SQL query to delete all line items for an invoice. */
    private static final String SQL_DELETE_BY_INVOICE_ID;

    static {
        try {
            SQL_CREATE_LINE_ITEM_RECORD = QueryLoader.getQuery("line_item/insertLineItem.sql");
            SQL_READ_LINE_ITEM_RECORD = QueryLoader.getQuery("line_item/selectLineItemById.sql");
            SQL_READ_ALL_LINE_ITEM_RECORD = QueryLoader.getQuery("line_item/selectAllLineItems.sql");
            SQL_EXISTS_LINE_ITEM_BY_ID = QueryLoader.getQuery("line_item/existById.sql");
            SQL_UPDATE_LINE_ITEM_RECORD = QueryLoader.getQuery("line_item/updateLineItem.sql");
            SQL_DELETE_LINE_ITEM_HARD = QueryLoader.getQuery("line_item/deleteLineItemHard.sql");
            SQL_DELETE_LINE_ITEM_SOFT = QueryLoader.getQuery("line_item/deleteLineItemSoft.sql");
            SQL_COUNT_LINE_ITEM_RECORD = QueryLoader.getQuery("line_item/countLineItems.sql");
            SQL_FIND_BY_INVOICE_ID = QueryLoader.getQuery("line_item/selectLineItemsByInvoiceId.sql");
            SQL_FIND_BY_PARTICIPANT_ID = QueryLoader.getQuery("line_item/selectLineItemsByParticipantId.sql");
            SQL_FIND_UNPAID_BY_PARTICIPANT = QueryLoader.getQuery("line_item/selectUnpaidLineItemsByParticipantId.sql");
            SQL_DELETE_BY_INVOICE_ID = QueryLoader.getQuery("line_item/deleteLineItemsByInvoiceId.sql");
            logger.info("All SQL queries for LineItemDataAccessImpl loaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load one or more SQL queries for LineItemDataAccessImpl. Check .sql files and paths.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    // --- Private Helper Methods ---
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

            logger.debug("Successfully mapped ResultSet to LineItem object with ID: {}", lineItem.getLineItemID());
        } catch (Exception e) {
            logger.error("Error mapping ResultSet to LineItem: {}", e.getMessage(), e);
            throw new SQLException("Failed to map ResultSet to LineItem: " + e.getMessage(), e);
        }
        return lineItem;
    }

    // --- Public Interface Methods (From LineItemDataAccess) ---

    @Override
    public Optional<LineItem> read(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Line Item ID cannot be null for read operation.");

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

    @Override
    public List<LineItem> readAll() throws DatabaseAccessException {
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
            stmt.setString(6, lineItem.getNotes());

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
            stmt.setString(6, lineItem.getNotes());
            stmt.setInt(7, lineItem.getLineItemID()); // WHERE clause

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

    @Override
    public boolean delete(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Line Item ID cannot be null for delete operation.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_LINE_ITEM_HARD)) {
            stmt.setInt(1, id);

            logger.debug("Executing hard delete line item query for ID: {}", id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No line item found with ID: {} for deletion. Delete operation resulted in 0 affected rows.", id);
                return false;
            } else {
                logger.info("Successfully hard-deleted line item with ID: {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error hard-deleting line item record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error hard-deleting line item record: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(Integer id) throws DatabaseAccessException {
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

    @Override
    public long count() throws DatabaseAccessException {
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

    @Override
    public List<LineItem> findByInvoice(Integer invoiceId) throws DatabaseAccessException {
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

    @Override
    public List<LineItem> findByParticipant(Integer participantId) throws DatabaseAccessException {
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

    @Override
    public List<LineItem> findUnpaidByParticipant(Integer participantId) throws DatabaseAccessException {
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

    @Override
    public void deleteByInvoice(Integer invoiceId) throws DatabaseAccessException {
        Objects.requireNonNull(invoiceId, "Invoice ID cannot be null for deleteByInvoice operation.");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_BY_INVOICE_ID)) {
            stmt.setInt(1, invoiceId);

            logger.debug("Executing delete by invoice ID query for ID: {}", invoiceId);
            int affectedRows = stmt.executeUpdate();

            logger.info("Successfully deleted {} line items for invoice ID: {}", affectedRows, invoiceId);

        } catch (SQLException e) {
            logger.error("Error deleting line items by invoice ID {}: {}", invoiceId, e.getMessage(), e);
            throw new DatabaseAccessException("Error deleting line items by invoice ID: " + e.getMessage(), e);
        }
    }
}