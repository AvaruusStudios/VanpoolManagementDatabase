package com.avaruusstudios.vmdb.model;

/**
 * Defines the possible types of events that can be logged in the {@link EventLog}.
 * These types are strictly enforced as defined in the database schema's
 * `CHECK (EventType IN ('CREATE', 'READ', 'UPDATE', 'DELETE', 'ERROR'))` constraint.
 *
 * <p>
 * Using an enum provides type safety, prevents invalid event type strings,
 * and improves code readability, ensuring that only predefined and valid
 * event types are recorded.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-12
 * Updated On: 2025-07-12
 *
 * @see EventLog
 */
public enum EventType {
    /**
     * Represents a creation event (e.g., a new record was added).
     * Stored in DB as "CREATE".
     */
    CREATE("CREATE"),
    /**
     * Represents a read or view event (e.g., data was accessed).
     * Stored in DB as "READ".
     */
    READ("READ"),
    /**
     * Represents an update event (e.g., an existing record was modified).
     * Stored in DB as "UPDATE".
     */
    UPDATE("UPDATE"),
    /**
     * Represents a deletion event (e.g., a record was removed).
     * Stored in DB as "DELETE".
     */
    DELETE("DELETE"),
    /**
     * Represents an error or exception event within the system.
     * Stored in DB as "ERROR".
     */
    ERROR("ERROR");

    // --- Field ---
    /**
     * The string representation of the event type as stored in the database.
     * This value corresponds directly to the database's `EventType` column.
     */
    private final String dbValue;

    // --- Constructor ---
    /**
     * Constructor for the EventType enum.
     *
     * @param dbValue The string representation of the event type as stored in the database.
     */
    EventType(String dbValue) {
        this.dbValue = dbValue;
    }

    // --- Getters ---
    /**
     * Retrieves the exact string value that should be stored in or read from the database
     * for this event type.
     *
     * @return The database-compatible string value (e.g., "CREATE", "READ", "UPDATE").
     */
    public String getDbValue() {
        return dbValue;
    }

    // --- Utility Methods ---
    /**
     * Returns the database value of the event type when this enum constant is converted to a string.
     * This method overrides the default {@link Enum#toString()} behavior to provide
     * the direct database string value, making it suitable for logging and database interactions.
     *
     * @return The database string value of the event type.
     */
    @Override
    public String toString() {
        return dbValue;
    }

    // --- Static Factory Method ---
    /**
     * <p>
     * Converts a database string value into its corresponding {@code EventType} enum constant.
     * This static method is crucial for deserializing event type data read from the database,
     * providing a type-safe conversion.
     * </p>
     * <p>
     * The comparison is case-insensitive for robustness, and it throws an {@code IllegalArgumentException}
     * if the input is {@code null}, empty, or does not match any valid event type.
     * </p>
     *
     * @param dbValue The string value obtained from the database's `EventType` column. Must not be {@code null} or empty.
     * @return The matching {@code EventType} enum constant.
     * @throws IllegalArgumentException if the provided string does not match any valid event type's {@code dbValue}, or if it is {@code null} or empty.
     */
    public static EventType fromDbValue(String dbValue) {
        if (dbValue == null || dbValue.trim().isEmpty()) {
            throw new IllegalArgumentException("EventType database value cannot be null or empty.");
        }
        for (EventType type : EventType.values()) {
            if (type.dbValue.equalsIgnoreCase(dbValue.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown EventType database value: '" + dbValue + "'");
    }
}