package com.avaruusstudios.vmdb.model;

import java.util.Arrays;

/**
 * <p>
 * Represents the distinct types of programs a {@link Participant} can be associated with
 * within the Vanpool Management System. This enum enforces type safety for program
 * designations, ensuring that only predefined values are used.
 * </p>
 *
 * <p>
 * Each enum constant holds a corresponding string value that is used for persistence
 * in the database, aligning with the `Program` column in the `Participants` table.
 * </p>
 *
 * @see Participant
 */
public enum Program {
    /**
     * Represents the "Transportation Incentive Program (TIP)" program.
     * This is typically for participants who receive a specific incentive.
     */
    TRANSPORTATION_INCENTIVE_PROGRAM("Transportation Incentive Program (TIP)"),
    /**
     * Represents the "Daily" program.
     * This is for participants who commute on a daily basis without specific incentives.
     */
    DAILY("Daily"),
    /**
     * Represents a program type that is unknown, not applicable, or not yet defined.
     * This serves as a default or fallback when a matching program cannot be found,
     * such as during database deserialization.
     */
    NONE("None"); // Changed from UNKNOWN to NONE as per enum value

    /**
     * The string representation of the program type as stored in the database.
     */
    private final String dbValue;

    /**
     * Constructs a {@code ProgramType} enum constant with its associated database string value.
     *
     * @param dbValue The string value that corresponds to this enum constant in the database.
     */
    Program(String dbValue) {
        this.dbValue = dbValue;
    }
    /**
     * Retrieves the database string value associated with this {@code ProgramType} enum constant.
     * This value is used for persisting the enum to the database.
     *
     * @return The string value stored in the database for this program type.
     */
    public String getDbValue() {
        return dbValue;
    }
    /**
     * Converts a database string value into its corresponding {@code ProgramType} enum constant.
     * This method is essential for deserializing data from the database into Java objects.
     *
     * @param dbValue The string value retrieved from the database's `Program` column.
     * @return The {@code ProgramType} enum constant matching the provided string.
     * Returns {@link #NONE} if no matching program type is found for the given string,
     * ensuring null safety.
     */
    public static Program fromDbValue(String dbValue) {
        // Using Optional for robust handling of null or non-matching dbValue
        return Arrays.stream(Program.values())
                .filter(program -> program.getDbValue().equalsIgnoreCase(dbValue)) // Case-insensitive match
                .findFirst()
                .orElse(NONE); // Return NONE if no match
    }
    /**
     * <p>
     * Returns the user-friendly string representation of this program type.
     * This method overrides the default {@link Enum#toString()} behavior to
     * provide the more descriptive {@code dbValue} instead of the enum constant's name.
     * </p>
     * <p>
     * This is highly beneficial for logging, debugging, and user interface display.
     * </p>
     *
     * @return The string value of the program type as stored in the database and intended for display.
     */
    @Override
    public String toString() {
        return dbValue;
    }
}