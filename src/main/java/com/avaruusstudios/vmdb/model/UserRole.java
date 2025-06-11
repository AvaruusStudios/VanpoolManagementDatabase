package com.avaruusstudios.vmdb.model;

/**
 * <p>
 * Defines the possible roles a {@link User} can have within the Vanpool Management System.
 * These roles control user permissions and access levels within the application.
 * </p>
 *
 * <p>
 * This enum strictly enforces the allowed role types as defined in the database schema's
 * `UserRole VARCHAR(50) NOT NULL` field, supporting roles such as "ADMIN", "TREASURER", "USER", and "COORDINATOR".
 * </p>
 *
 * @see User
 */
public enum UserRole {
    /**
     * Represents a user with full administrative privileges.
     * Stored in DB as "ADMIN".
     */
    ADMIN("ADMIN"),
    /**
     * Represents a user responsible for financial transactions and reporting.
     * Stored in DB as "TREASURER".
     */
    TREASURER("TREASURER"),
    /**
     * Represents a standard user with basic access to the system.
     * Stored in DB as "USER".
     */
    USER("USER"),
    /**
     * Represents a user responsible for organizing and managing vanpool details.
     * Stored in DB as "COORDINATOR");
     */
    COORDINATOR("COORDINATOR");

    /**
     * The string representation of the user role as stored in the database.
     */
    private final String dbValue;

    /**
     * Constructor for the UserRole enum.
     *
     * @param dbValue The string value used for database storage.
     */
    UserRole(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Retrieves the exact string value that should be stored in or read from the database
     * for this user role.
     *
     * @return The database-compatible string value (e.g., "ADMIN", "TREASURER", "USER").
     */
    public String getDbValue() {
        return dbValue;
    }
    /**
     * Returns the database value of the user role when this enum constant is converted to a string.
     *
     * @return The database string value of the user role.
     */
    @Override
    public String toString() {
        return dbValue;
    }
    /**
     * <p>
     * Converts a database string value into its corresponding {@code UserRole} enum constant.
     * This static method is crucial for deserializing user role data read from the database.
     * </p>
     * <p>
     * The comparison is case-insensitive for robustness.
     * </p>
     *
     * @param dbValue The string value obtained from the database's `UserRole` column.
     * @return The matching {@code UserRole} enum constant.
     * @throws IllegalArgumentException if the provided string does not match any valid user role's {@code dbValue}.
     */
    public static UserRole fromDbValue(String dbValue) {
        if (dbValue == null || dbValue.trim().isEmpty()) {
            throw new IllegalArgumentException("UserRole database value cannot be null or empty.");
        }
        for (UserRole role : UserRole.values()) {
            if (role.dbValue.equalsIgnoreCase(dbValue.trim())) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown UserRole database value: '" + dbValue + "'");
    }
}