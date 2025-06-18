package com.avaruusstudios.vmdb.model;

/**
 * <p>
 * Defines the possible roles a {@link User} (and by extension, a {@link Participant})
 * can have within the Vanpool Management System. These roles control user permissions
 * and access levels within the application, as well as specific duties in the vanpool context.
 * </p>
 *
 * <p>
 * This enum strictly enforces the allowed role types as defined in the database schema's
 * `UserRole VARCHAR(50) NOT NULL` field (for general users) and `Role VARCHAR(50) NOT NULL`
 * field (for specific participant functions), supporting roles such as "ADMIN", "TREASURER",
 * "USER", "COORDINATOR", "PARTICIPANT", and "DRIVER".
 * </p>
 *
 * @see User
 * @see Participant
 */
public enum Role {
    /**
     * Represents a user with full administrative privileges across the system.
     * Stored in DB as "ADMIN". Primarily a System Role.
     */
    ADMIN("Admin", true, false),
    /**
     * Represents a user responsible for financial transactions, billing, and reporting.
     * This role may absorb rounding differences in financial calculations.
     * Stored in DB as "TREASURER". Applicable as both a System and Participant Role.
     */
    TREASURER("Treasurer", true, true),
    /**
     * Represents a standard general user with basic access to the system.
     * Stored in DB as "USER". Primarily a System Role.
     */
    USER("User", true, false),
    /**
     * Represents a user or participant responsible for organizing and managing vanpool details.
     * This role may absorb rounding differences in financial calculations.
     * Stored in DB as "COORDINATOR". Applicable as both a System and Participant Role.
     */
    COORDINATOR("Coordinator", true, true),
    /**
     * Represents a standard participant in a vanpool with no special administrative or driving duties.
     * Stored in DB as "PARTICIPANT". Primarily a Participant Role.
     */
    PARTICIPANT("Participant", false, true);
    /**
     * Represents a participant designated as a primary or secondary driver of the van.
     * Stored in DB as "DRIVER". Primarily a Participant Role.
     */
//    DRIVER("Driver", false, true);

    /**
     * The string representation of the role as stored in the database.
     */
    private final String dbValue;
    /**
     * Indicates if this role can be assigned to a system {@link User}.
     * {@code true} if applicable as a system role, {@code false} otherwise.
     */
    private final boolean isSystemApplicable;
    /**
     * Indicates if this role can be assigned to a {@link Participant} within a vanpool.
     * {@code true} if applicable as a participant role, {@code false} otherwise.
     */
    private final boolean isParticipantApplicable;

    /**
     * Constructor for the Role enum.
     *
     * @param dbValue The string value used for database storage.
     * @param isSystemApplicable A boolean indicating if this role can be assigned to a system {@link User}.
     * @param isParticipantApplicable A boolean indicating if this role can be assigned to a {@link Participant}.
     */
    Role(String dbValue, boolean isSystemApplicable, boolean isParticipantApplicable) {
        this.dbValue = dbValue;
        this.isSystemApplicable = isSystemApplicable;
        this.isParticipantApplicable = isParticipantApplicable;
    }

    /**
     * Retrieves the exact string value that should be stored in or read from the database
     * for this role.
     *
     * @return The database-compatible string value (e.g., "ADMIN", "TREASURER", "PARTICIPANT").
     */
    public String getDbValue() {
        return dbValue;
    }
    /**
     * Checks if this role is typically applicable to a system {@link User}.
     *
     * @return {@code true} if it's a system role, {@code false} otherwise.
     */
    public boolean isSystemApplicable() {
        return isSystemApplicable;
    }
    /**
     * Checks if this role is typically applicable to a {@link Participant} in a vanpool.
     *
     * @return {@code true} if it's a participant role, {@code false} otherwise.
     */
    public boolean isParticipantApplicable() {
        return isParticipantApplicable;
    }
    /**
     * Returns the database value of the role when this enum constant is converted to a string.
     *
     * @return The database string value of the role.
     */
    @Override
    public String toString() {
        return dbValue;
    }
    /**
     * <p>
     * Converts a database string value into its corresponding {@code Role} enum constant.
     * This static method is crucial for deserializing role data read from the database.
     * </p>
     * <p>
     * The comparison is case-insensitive for robustness.
     * </p>
     *
     * @param dbValue The string value obtained from a database `Role` or `UserRole` column.
     * @return The matching {@code Role} enum constant.
     * @throws IllegalArgumentException if the provided string does not match any valid role's {@code dbValue}.
     */
    public static Role fromDbValue(String dbValue) {
        if (dbValue == null || dbValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Role database value cannot be null or empty.");
        }
        for (Role role : Role.values()) {
            if (role.dbValue.equalsIgnoreCase(dbValue.trim())) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown Role database value: '" + dbValue + "'");
    }
}