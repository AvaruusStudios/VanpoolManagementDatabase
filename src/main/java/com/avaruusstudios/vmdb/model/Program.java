package com.avaruusstudios.vmdb.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * <p>
 * Represents the distinct types of programs a {@link Participant} can be associated with
 * within the Vanpool Management System. This enum enforces type safety for program
 * designations, ensuring that only predefined values are used for every participant.
 * </p>
 *
 * <p>
 * When stored in the database, the canonical name of the enum constant (e.g., "DAILY",
 * "TRANSPORTATION_INCENTIVE_PROGRAM") is used. For user-facing display, a more
 * descriptive string (e.g., "Daily", "Transportation Incentive Program (TIP)") is
 * available via {@link #getDisplayValue()} and {@link #toString()}.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.1
 * Created On: 2025-07-12
 * Updated On: 2025-07-12
 *
 * @see Participant
 */
public enum Program {
    /**
     * Represents the "Transportation Incentive Program (TIP)" program.
     * This is typically for participants who receive a specific incentive.
     * Stored in DB as "TRANSPORTATION_INCENTIVE_PROGRAM".
     */
    TRANSPORTATION_INCENTIVE_PROGRAM("Transportation Incentive Program (TIP)"),
    /**
     * Represents the "Daily" program.
     * This is for participants who commute on a daily basis without specific incentives.
     * Stored in DB as "DAILY".
     */
    DAILY("Daily"),
    /**
     * Represents the "None" program.
     * This is the default choice for a new Participant.
     * Stored in the DB as "NONE".
     */
    NONE("None");

    // --- Field ---
    /**
     * The user-friendly string representation of the program type, suitable for display in the UI.
     */
    private final String displayValue;

    // --- Constructor ---
    /**
     * Constructs a {@code Program} enum constant with its associated display string value.
     *
     * @param displayValue The user-friendly string value that corresponds to this enum constant,
     * intended for display.
     */
    Program(String displayValue) {
        this.displayValue = displayValue;
    }

    // --- Getters ---
    /**
     * Retrieves the canonical name of the enum constant (e.g., "DAILY", "TRANSPORTATION_INCENTIVE_PROGRAM").
     * This is the recommended string value for storing in the database to ensure consistency
     * with enum constant definitions.
     *
     * @return The ALLCAPS string value used for database storage.
     */
    public String getDbValue() {
        return this.name(); // Returns "TRANSPORTATION_INCENTIVE_PROGRAM", "DAILY", etc.
    }

    /**
     * Retrieves the user-friendly string value for this program type, suitable for display in the UI.
     *
     * @return The displayable string value of the program type.
     */
    public String getDisplayValue() {
        return displayValue;
    }

    // --- Utility Methods ---
    /**
     * Returns the user-friendly string representation of this program type.
     * This method overrides the default {@link Enum#toString()} behavior to
     * provide the more descriptive {@code displayValue} instead of the enum constant's name,
     * making it suitable for logging and UI display.
     *
     * @return The displayable string value of the program type.
     */
    @Override
    public String toString() {
        return displayValue;
    }

    // --- Static Factory Method ---
    /**
     * <p>
     * Converts a database string value (which should be the ALLCAPS canonical name of the enum constant)
     * into its corresponding {@code Program} enum constant.
     * This method is essential for deserializing data from the database into Java objects,
     * providing a type-safe conversion.
     * </p>
     * <p>
     * The conversion is robust, trimming whitespace and converting the input to uppercase
     * to match the enum constant names.
     * </p>
     *
     * @param dbString The string value retrieved from the database's `Program` column (e.g., "DAILY"). Must not be {@code null} or empty.
     * @return The {@code Program} enum constant matching the provided string.
     * @throws NullPointerException if {@code dbString} is {@code null}.
     * @throws IllegalArgumentException if {@code dbString} is empty, or if no matching program type is found for the given string.
     */
    public static Program fromDbValue(String dbString) {
        Objects.requireNonNull(dbString, "Program database value cannot be null.");
        String trimmedUpperDbString = dbString.trim().toUpperCase();
        if (trimmedUpperDbString.isEmpty()) {
            throw new IllegalArgumentException("Program database value cannot be empty.");
        }

        return Arrays.stream(Program.values())
                .filter(program -> program.name().equalsIgnoreCase(trimmedUpperDbString)) // Compare with canonical name
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Program database value: '" + dbString + "'"));
    }
}