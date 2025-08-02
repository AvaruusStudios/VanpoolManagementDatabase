package com.avaruusstudios.vmdb.dao.impl.sqlite;

import com.avaruusstudios.vmdb.dao.DatabaseAccessException;
import com.avaruusstudios.vmdb.dao.EventLogDataAccess;
import com.avaruusstudios.vmdb.db.DatabaseManager;
import com.avaruusstudios.vmdb.db.QueryLoader;
import com.avaruusstudios.vmdb.model.ErrorCode;
import com.avaruusstudios.vmdb.model.EventLog;
import com.avaruusstudios.vmdb.model.EventType;
import com.avaruusstudios.vmdb.model.User;

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
 * {@code EventLogImplementation} provides a concrete implementation of the {@link EventLogDataAccess}
 * interface, specifically designed for SQLite databases. This class handles all CRUD (Create, Read, Update, Delete)
 * and specialized data access operations for {@link EventLog} entities.
 * </p>
 *
 * <p>
 * It interacts with the database using JDBC, preparing SQL statements, mapping {@link ResultSet}
 * rows to {@link EventLog} objects, and managing database connections through a {@link DatabaseManager}.
 * This implementation addresses the N+1 problem by using SQL JOINs to fetch related User data
 * along with EventLog data in a single query for read operations.
 * </p>
 *
 * <p>
 * Unlike other data access implementations, {@code EventLogImplementation} performs a physical
 * (hard) delete when {@code deleteRecord} is called, as event logs are typically immutable
 * records for auditing purposes where a "soft delete" (marking as inactive) is not applicable.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-28
 * Updated On: 2025-07-28
 *
 * @see EventLogDataAccess
 * @see EventLog
 * @see DatabaseManager
 */
public class EventLogImplementation implements EventLogDataAccess {

    /**
     * SLF4J Logger for logging informational messages, warnings, and errors within the {@code EventLogImplementation} class.
     */
    private static final Logger logger = LoggerFactory.getLogger(EventLogImplementation.class);

    /**
     * DateTimeFormatter for parsing and formatting `LocalDateTime` objects to/from database strings in "yyyy-MM-dd HH:mm:ss" format.
     */
    private static final DateTimeFormatter CUSTOM_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // SQL Query Constants
    /** SQL query to create (insert) a new event log record. */
    private static final String SQL_CREATE_EVENT_LOG_RECORD;
    /** SQL query to read a single event log record by its ID, including joined User data. */
    private static final String SQL_READ_EVENT_LOG_RECORD;
    /** SQL query to read all event log records, including joined User data. */
    private static final String SQL_READ_ALL_EVENT_LOG_RECORD;
    /** SQL query to count the total number of event log records. */
    private static final String SQL_COUNT_EVENT_LOG_RECORD;
    /** SQL query to check if an event log record exists by its ID. */
    private static final String SQL_EXISTS_EVENT_LOG_BY_ID;
    /** SQL query to find event logs associated with a specific user ID, including joined User data. */
    private static final String SQL_FIND_BY_USER_ID;
    /** SQL query to find event logs by event type, including joined User data. */
    private static final String SQL_FIND_BY_EVENT_TYPE;
    /** SQL query to find event logs by table name, including joined User data. */
    private static final String SQL_FIND_BY_TABLE_NAME;


    static {
        try {
            SQL_CREATE_EVENT_LOG_RECORD = QueryLoader.getQuery("eventlog/insertEventLog.sql");
            SQL_READ_EVENT_LOG_RECORD = QueryLoader.getQuery("eventlog/selectEventLogByIdJoined.sql");
            SQL_READ_ALL_EVENT_LOG_RECORD = QueryLoader.getQuery("eventlog/selectAllEventLogsJoined.sql");
            SQL_COUNT_EVENT_LOG_RECORD = QueryLoader.getQuery("eventlog/countEventLogs.sql");
            SQL_EXISTS_EVENT_LOG_BY_ID = QueryLoader.getQuery("eventlog/existByIdEventLog.sql");
            SQL_FIND_BY_USER_ID = QueryLoader.getQuery("eventlog/selectEventLogsByUserId.sql");
            SQL_FIND_BY_EVENT_TYPE = QueryLoader.getQuery("eventlog/selectEventLogsByEventType.sql");
            SQL_FIND_BY_TABLE_NAME = QueryLoader.getQuery("eventlog/selectEventLogsByTableName.sql"); // Added for new method

            logger.info("All SQL queries for EventLogImplementation loaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load one or more SQL queries for EventLogImplementation. Check .sql files and paths.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * <p>
     * Maps a row from a {@link ResultSet} to a fully populated {@link EventLog} object.
     * This private helper method centralizes the logic for converting raw database
     * column values into a populated {@code EventLog} model object, ensuring data consistency
     * and type safety across all read operations.
     * </p>
     * <p>
     * This method expects a {@link ResultSet} that contains not only `EventLogs` table columns
     * but also joined columns from the `Users` table (if applicable), allowing for
     * the direct hydration of associated {@link User} objects without incurring an N+1 query problem.
     * </p>
     * <p>
     * It handles:
     * <ul>
     * <li>Retrieval of the `EventID` and setting it via the `_setEventID` method.</li>
     * <li>Parsing `LocalDateTime` for `EventDate` using {@link #CUSTOM_DATETIME_FORMATTER}.</li>
     * <li>Mapping of {@link EventType} enum using {@link EventType#fromDbValue(String)}.</li>
     * <li>Mapping of full {@link User} details from joined columns (`UserID_FK`, `WindowsUsername`, `FirstName`, `LastName`).</li>
     * <li>Retrieval of `TableName` and `RecordID` fields.</li>
     * <li>Mapping of {@link ErrorCode} enum using {@link ErrorCode#fromValue(int)}, handling nullable error codes.</li>
     * <li>Retrieval of `Description` string field.</li>
     * </ul>
     * </p>
     *
     * @param rs The {@link ResultSet} positioned at the current row containing event log and user data.
     * @return A fully populated {@link EventLog} object with data retrieved from the {@code ResultSet}.
     * @throws SQLException If a database access error occurs (e.g., a column name is not found,
     * or there is a data type mismatch during retrieval from the {@code ResultSet}).
     */
    private EventLog mapResultSetToEventLog(ResultSet rs) throws SQLException {
        EventLog eventLog = new EventLog();

        // EventLog fields - using EventLog model's property names and setters
        Integer eventID = rs.getInt("EventID");
        if (eventID > 0) {
            eventLog._setEventID(eventID);
        }
        LocalDateTime eventDate = LocalDateTime.parse(rs.getString("EventDate"), CUSTOM_DATETIME_FORMATTER);
        String eventTypeStr = rs.getString("EventType");
        String tableName = rs.getString("TableName");
        Integer recordID = rs.getObject("RecordID", Integer.class);
        Integer errorCodeInt = rs.getObject("ErrorCode", Integer.class); // ErrorCode is stored as INTEGER in DB

        String description = rs.getString("Description");

        // User fields from JOIN (nullable if the FK allows it or is not always present)
        User user = null;
        Integer userID_FK = rs.getObject("UserID_FK", Integer.class);
        if (userID_FK != null) {
            String windowsUsername = rs.getString("WindowsUsername");
            if (windowsUsername != null && !rs.wasNull()) {
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");

                user = new User();
                user._setUserId(userID_FK);
                user.setWindowsUsername(windowsUsername);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                // Set other user fields if necessary (e.g., email, role, isActive, deletedAt, dateCreated)
            } else if (!rs.wasNull()) { // UserID_FK is present but windowsUsername is null, implies partial user or inactive
                user = new User();
                user._setUserId(userID_FK);
                logger.warn("User with ID {} referenced by event log {} not fully found in join. Partial User object created.", userID_FK, eventID);
            }
        }
        eventLog.setUser(user);

        EventType eventType = (eventTypeStr != null) ? EventType.fromDbValue(eventTypeStr) : null;
        ErrorCode errorCode = (errorCodeInt != null) ? ErrorCode.fromValue(errorCodeInt) : null; // Use fromValue for int

        // Using setters from EventLog model
        eventLog.setEventDate(eventDate);
        eventLog.setEventType(eventType);
        eventLog.setTableName(tableName);
        eventLog.setRecordID(recordID);
        eventLog.setErrorCode(errorCode);
        eventLog.setDescription(description);

        return eventLog;
    }

    // --- Interface Implementations ---

    /**
     * {@inheritDoc}
     * <p>
     * Creates a new event log record in the database.
     * The `EventID` of the provided {@code EventLog} object must be {@code null}
     * as it will be auto-generated by the database.
     * </p>
     *
     * @param eventLog The {@link EventLog} object to be created. Must not be {@code null}.
     * @return The created {@link EventLog} object with its auto-generated `EventID` set.
     * @throws DatabaseAccessException If a database access error occurs, or if the event log creation fails
     * (e.g., no rows affected, no ID obtained).
     * @throws NullPointerException    If the provided `eventLog` object or its required fields (EventDate, EventType, User, UserId, TableName) are {@code null}.
     * @throws IllegalArgumentException If the `eventLog` object already has a non-null `EventID`.
     */
    @Override
    public EventLog createRecord(EventLog eventLog) throws DatabaseAccessException {
        Objects.requireNonNull(eventLog, "EventLog object cannot be null for creation.");
        if (eventLog.getEventID() != null) {
            throw new IllegalArgumentException("EventLog ID must be null for new event log creation (auto-generated).");
        }
        Objects.requireNonNull(eventLog.getEventDate(), "Event Date cannot be null for event log creation.");
        Objects.requireNonNull(eventLog.getEventType(), "Event Type cannot be null for event log creation.");
        Objects.requireNonNull(eventLog.getUser(), "User object cannot be null for event log creation.");
        Objects.requireNonNull(eventLog.getUser().getUserId(), "User ID cannot be null for event log creation.");
        Objects.requireNonNull(eventLog.getTableName(), "Table Name cannot be null for event log creation.");


        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CREATE_EVENT_LOG_RECORD, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, eventLog.getEventDate().format(CUSTOM_DATETIME_FORMATTER));
            stmt.setString(2, eventLog.getEventType().getDbValue());
            stmt.setInt(3, eventLog.getUser().getUserId());
            stmt.setString(4, eventLog.getTableName());
            // RecordID is nullable, so handle it
            if (eventLog.getRecordID() != null) {
                stmt.setInt(5, eventLog.getRecordID());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            // ErrorCode is nullable, store its integer value
            if (eventLog.getErrorCode() != null) {
                stmt.setInt(6, eventLog.getErrorCode().getValue()); // Use getValue() for ErrorCode
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            stmt.setString(7, eventLog.getDescription());


            logger.debug("Executing insert event log query for user ID: {}", eventLog.getUser().getUserId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.error("Creating event log failed, no rows affected for user ID: {}", eventLog.getUser().getUserId());
                throw new DatabaseAccessException("Creating event log failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Integer generatedId = generatedKeys.getInt(1);
                    eventLog._setEventID(generatedId);
                    logger.info("Successfully created event log with ID: {}", generatedId);
                    return eventLog;
                } else {
                    logger.error("Creating event log failed, no ID obtained for user ID: {}", eventLog.getUser().getUserId());
                    throw new DatabaseAccessException("Creating event log failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating event log record: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error creating event log record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Reads a single event log record from the database by its ID.
     * This method fetches the event log along with its associated User details
     * using SQL JOINs to ensure a fully hydrated object and avoid the N+1 problem.
     * </p>
     *
     * @param id The ID of the event log record to be read. Must not be {@code null}.
     * @return An {@link Optional} containing the {@link EventLog} object if found,
     * or an empty {@link Optional} if no record matches the given ID.
     * @throws DatabaseAccessException If a database access error occurs during the read operation.
     * @throws NullPointerException    If the provided `id` is {@code null}.
     */
    @Override
    public Optional<EventLog> readRecord(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "EventLog ID cannot be null for reading.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_EVENT_LOG_RECORD)) {

            stmt.setInt(1, id);
            logger.debug("Executing read event log query for ID: {}", id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    EventLog eventLog = mapResultSetToEventLog(rs);
                    logger.info("Found event log with ID: {}", id);
                    return Optional.of(eventLog);
                } else {
                    logger.info("No event log found with ID: {}", id);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Error reading event log record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error reading event log record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Reads all event log records from the database.
     * This method fetches all event logs along with their associated User details
     * using SQL JOINs to ensure fully hydrated objects and avoid the N+1 problem.
     * </p>
     *
     * @return A {@link List} of all {@link EventLog} objects found in the database.
     * The list will be empty if no records are found.
     * @throws DatabaseAccessException If a database access error occurs during the read operation.
     */
    @Override
    public List<EventLog> readRecordAll() throws DatabaseAccessException {
        List<EventLog> eventLogs = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_ALL_EVENT_LOG_RECORD);
             ResultSet rs = stmt.executeQuery()) {

            logger.debug("Executing read all event logs query.");
            while (rs.next()) {
                eventLogs.add(mapResultSetToEventLog(rs));
            }
            logger.info("Found {} total event logs.", eventLogs.size());
        } catch (SQLException e) {
            logger.error("Error reading all event log records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error reading all event log records: " + e.getMessage(), e);
        }
        return eventLogs;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Counts the total number of event log records in the database.
     * </p>
     *
     * @return The total count of event log records as a {@code long}.
     * @throws DatabaseAccessException If a database access error occurs during the count operation.
     */
    @Override
    public long countRecord() throws DatabaseAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_EVENT_LOG_RECORD);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.info("Total event log record count: {}", count);
                return count;
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting event log records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error counting event log records: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Checks if an event log record with the given ID exists in the database.
     * </p>
     *
     * @param id The ID of the event log record to check for existence. Must not be {@code null}.
     * @return {@code true} if an event log with the given ID exists, {@code false} otherwise.
     * @throws DatabaseAccessException If a database access error occurs during the existence check.
     * @throws NullPointerException    If the provided `id` is {@code null}.
     */
    @Override
    public boolean existsById(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "EventLog ID cannot be null for existence check.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EXISTS_EVENT_LOG_BY_ID)) {

            stmt.setInt(1, id);
            logger.debug("Executing existsById event log query for ID: {}", id);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean exists = rs.next();
                logger.info("Event log with ID {} exists: {}", id, exists);
                return exists;
            }
        } catch (SQLException e) {
            logger.error("Error checking existence of event log record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error checking existence of event log record: " + e.getMessage(), e);
        }
    }

    /**
     * Implementation for finding event logs by User ID.
     * {@inheritDoc}
     * <p>
     * Finds all event log records associated with a specific user.
     * This method fetches event logs along with their associated User details
     * using SQL JOINs to ensure fully hydrated objects and avoid the N+1 problem.
     * </p>
     *
     * @param userId The ID of the user to search by. Must not be {@code null}.
     * @return A {@link List} of {@link EventLog} objects linked to the specified user.
     * The list will be empty if no records are found.
     * @throws DatabaseAccessException If a database access error occurs during the search.
     * @throws NullPointerException    If the provided `userId` is {@code null}.
     */
    @Override
    public List<EventLog> findByUser(Integer userId) throws DatabaseAccessException {
        Objects.requireNonNull(userId, "User ID cannot be null for event log search by user.");
        List<EventLog> eventLogs = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {

            stmt.setInt(1, userId);

            logger.debug("Executing findByUserId query for user ID: {}", userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventLogs.add(mapResultSetToEventLog(rs));
                }
            }
            logger.info("Found {} event logs for user ID: {}", eventLogs.size(), userId);
        } catch (SQLException e) {
            logger.error("Error finding event logs by user ID {}: {}", userId, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding event logs by user: " + e.getMessage(), e);
        }
        return eventLogs;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Finds all event log records of a specific event type.
     * This method fetches event logs along with their associated User details
     * using SQL JOINs to ensure fully hydrated objects and avoid the N+1 problem.
     * </p>
     *
     * @param eventType The {@link EventType} enum representing the event type to search by.
     * Must not be {@code null}.
     * @return A {@link List} of {@link EventLog} objects matching the specified event type.
     * The list will be empty if no records are found.
     * @throws DatabaseAccessException If a database access error occurs during the search.
     * @throws NullPointerException    If the provided `eventType` is {@code null}.
     */
    @Override
    public List<EventLog> findByType(EventType eventType) throws DatabaseAccessException {
        Objects.requireNonNull(eventType, "Event type cannot be null for event log search by event type.");
        List<EventLog> eventLogs = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_EVENT_TYPE)) {

            stmt.setString(1, eventType.getDbValue());

            logger.debug("Executing findByEventType query for event type: {}", eventType.getDbValue());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventLogs.add(mapResultSetToEventLog(rs));
                }
            }
            logger.info("Found {} event logs for event type: {}", eventLogs.size(), eventType.getDbValue());
        } catch (SQLException e) {
            logger.error("Error finding event logs by event type {}: {}", eventType.getDbValue(), e.getMessage(), e);
            throw new DatabaseAccessException("Error finding event logs by event type: " + e.getMessage(), e);
        }
        return eventLogs;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves a list of all {@link EventLog} records where the event occurred in a specific table.
     * </p>
     * <p>
     * This method is useful for targeted auditing or diagnostics, allowing you to view all
     * log entries related to a particular database table (e.g., "Users", "Transactions").
     * </p>
     *
     * @param tableName The name of the table (e.g., "Users", "Vehicles", "Transactions") to filter events by. Must not be {@code null}.
     * @return A {@link List} of {@link EventLog} objects where the event's table name matches. Returns an empty list if no logs are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     * @throws NullPointerException    If the provided `tableName` is {@code null}.
     */
    @Override // This method is now correctly overriding the interface method
    public List<EventLog> findByTable(String tableName) throws DatabaseAccessException {
        Objects.requireNonNull(tableName, "Table name cannot be null for event log search by table name.");
        List<EventLog> eventLogs = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_TABLE_NAME)) {

            stmt.setString(1, tableName);

            logger.debug("Executing findByTableName query for table name: {}", tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventLogs.add(mapResultSetToEventLog(rs));
                }
            }
            logger.info("Found {} event logs for table name: {}", eventLogs.size(), tableName);
        } catch (SQLException e) {
            logger.error("Error finding event logs by table name {}: {}", tableName, e.getMessage(), e);
            throw new DatabaseAccessException("Error finding event logs by table name: " + e.getMessage(), e);
        }
        return eventLogs;
    }
}