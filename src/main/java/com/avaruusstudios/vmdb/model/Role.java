package com.avaruusstudios.vmdb.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * <p>
 * Defines the possible roles a {@link User} (and by extension, a {@link Participant})
 * can have within the Vanpool Management System. These roles control user permissions
 * and access levels within the application, as well as specific duties in the vanpool context.
 * </p>
 *
 * <p>
 * This enum strictly enforces the allowed role types. When stored in the database,
 * the canonical name of the enum constant (e.g., "ADMIN", "PARTICIPANT") is used.
 * For display purposes in the user interface, a more user-friendly PascalCase string
 * (e.g., "Admin", "Participant") is available via {@link #getDisplayValue()} and
 * {@link #toString()}.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-12
 * Updated On: 2025-07-12
 *
 * @see User
 * @see Participant
 */
public enum Role {
    // --- Enum Constants ---
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
    PARTICIPANT("Participant", false, true),
    /**
     * Represents a participant designated as a primary or secondary driver of the van.
     * Stored in DB as "DRIVER". Primarily a Participant Role.
     */
    DRIVER("Driver", false, true);

    // --- Fields ---
    /**
     * The user-friendly string representation of the role, suitable for display in the UI.
     * This is typically PascalCase (e.g., "Admin", "Participant").
     */
    private final String displayValue;
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

    // --- Constructor ---
    /**
     * Constructs a {@code Role} enum constant.
     *
     * @param displayValue The user-friendly string value for display purposes (e.g., "Admin").
     * @param isSystemApplicable A boolean indicating if this role can be assigned to a system {@link User}.
     * @param isParticipantApplicable A boolean indicating if this role can be assigned to a {@link Participant}.
     */
    Role(String displayValue, boolean isSystemApplicable, boolean isParticipantApplicable) {
        this.displayValue = displayValue;
        this.isSystemApplicable = isSystemApplicable;
        this.isParticipantApplicable = isParticipantApplicable;
    }

    // --- Getters ---
    /**
     * Retrieves the canonical name of the enum constant (e.g., "ADMIN", "PARTICIPANT").
     * This is the recommended string value for storing in the database to ensure consistency
     * with enum constant definitions.
     *
     * @return The ALLCAPS string value used for database storage.
     */
    public String getDbValue() {
        return this.name(); // Returns "ADMIN", "PARTICIPANT", etc.
    }
    /**
     * Retrieves the user-friendly string value for this role, suitable for display in the UI.
     * This value is typically in PascalCase (e.g., "Admin", "Participant").
     *
     * @return The displayable string value of the role.
     */
    public String getDisplayValue() {
        return displayValue;
    }

    // --- Applicability Checkers ---
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

    // --- Utility Methods ---
    /**
     * Returns the user-friendly string representation of this role.
     * This method overrides the default {@link Enum#toString()} behavior to
     * provide the more descriptive {@code displayValue} instead of the enum constant's name,
     * making it suitable for logging and UI display.
     *
     * @return The displayable string value of the role.
     */
    @Override
    public String toString() {
        return displayValue;
    }

    // --- Static Factory Method ---
    /**
     * <p>
     * Converts a database string value (which should be the ALLCAPS canonical name of the enum constant)
     * into its corresponding {@code Role} enum constant.
     * This static method is crucial for deserializing role data read from the database,
     * providing a type-safe conversion.
     * </p>
     * <p>
     * The conversion is robust, trimming whitespace and converting the input to uppercase
     * to match the enum constant names.
     * </p>
     *
     * @param dbString The string value obtained from a database `Role` or `UserRole` column (e.g., "ADMIN"). Must not be {@code null}.
     * @return The matching {@code Role} enum constant.
     * @throws NullPointerException if the provided {@code dbString} is {@code null}.
     * @throws IllegalArgumentException if the provided string does not match any valid {@code Role} enum constant name,
     * or if the input string is empty after trimming.
     */
    public static Role fromDbValue(String dbString) {
        Objects.requireNonNull(dbString, "Role database string cannot be null.");
        String trimmedUpperDbString = dbString.trim().toUpperCase();
        if (trimmedUpperDbString.isEmpty()) {
            throw new IllegalArgumentException("Role database string cannot be empty after trimming.");
        }
        try {
            // Enum.valueOf expects the exact constant name (ALLCAPS)
            return Role.valueOf(trimmedUpperDbString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown Role database string: '" + dbString + "'. Valid roles are: " +
                    Arrays.toString(Arrays.stream(Role.values()).map(Role::name).toArray()), e);
        }
    }
}