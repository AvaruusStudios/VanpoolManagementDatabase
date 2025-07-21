package com.avaruusstudios.vmdb.data;

import com.avaruusstudios.vmdb.model.EventLog;
import com.avaruusstudios.vmdb.model.EventType;
import java.util.List;

/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the
 * {@link EventLog} entity within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It extends {@link DeleteDataAccess} to inherit standard CRUD operations for EventLog records.
 * This interface provides specific query methods for retrieving event logs based on
 * the associated user, the type of event, or the table affected, supporting key auditing
 * and diagnostic functionalities.
 * </p>
 *
 * @author [Your Name/AvaruusStudios]
 * @version 1.1 // Incremented version due to method additions
 * @since 2025-07-11
 *
 * @see DeleteDataAccess
 * @see EventLog
 * @see DatabaseAccessException
 */
public interface EventLogDataAccess extends CreateDataAccess<EventLog, Integer> {

    /**
     * <p>
     * Retrieves a list of all {@link EventLog} records associated with a specific user.
     * </p>
     * <p>
     * This method is intended for auditing and tracking all events performed by or related
     * to a particular user in the system.
     * </p>
     *
     * @param userId The ID of the user whose event logs are to be retrieved. Must not be {@code null}.
     * @return A {@link List} of {@link EventLog} objects for the specified user. Returns an empty list if no logs are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<EventLog> findByUserId(Integer userId) throws DatabaseAccessException;

    /**
     * <p>
     * Retrieves a list of all {@link EventLog} records that match a specified {@link EventType}.
     * </p>
     * <p>
     * This method is useful for filtering logs to see specific categories of events,
     * such as all 'ERROR' logs for debugging, or all 'CREATE' logs for auditing new entries.
     * </p>
     *
     * @param eventType The {@link EventType} to filter events by (e.g., {@link EventType#ERROR}, {@link EventType#CREATE}). Must not be {@code null}.
     * @return A {@link List} of {@link EventLog} objects matching the specified type. Returns an empty list if no logs are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<EventLog> findByEventType(EventType eventType) throws DatabaseAccessException;

    /**
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
     */
    List<EventLog> findByTableName(String tableName) throws DatabaseAccessException;
}