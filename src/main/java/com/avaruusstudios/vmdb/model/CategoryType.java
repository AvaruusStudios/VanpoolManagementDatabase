package com.avaruusstudios.vmdb.model;

import java.util.Arrays;

/**
 * <p>
 * Defines the types of financial categories used in the Vanpool Management System.
 * These types classify money flowing into (INCOME), money flowing out of (EXPENSE),
 * or general adjustments (CREDIT) to the vanpool accounts.
 * </p>
 *
 * <p>
 * This enum's constant names (e.g., "INCOME", "EXPENSE") serve as the canonical
 * string representation for database storage. It also provides a separate
 * user-friendly string for display purposes. It includes a {@link #NONE} type
 * for robust handling of unknown or unspecified categories, aligning with the
 * design of {@link Role}, {@link Program}, and {@link InvoiceType} enums.
 * </p>
 *
 * @see Category
 * @see Role
 * @see Program
 * @see InvoiceType
 */
public enum CategoryType {
    /**
     * Represents a category for money coming into the vanpool (e.g., participant payments, external funding).
     * Stored in DB as "INCOME" (which is its enum name). Displayed as "Income".
     */
    INCOME("Income"),
    /**
     * Represents a category for money going out of the vanpool (e.g., fuel, maintenance, tolls, van rental).
     * Stored in DB as "EXPENSE" (which is its enum name). Displayed as "Expense".
     */
    EXPENSE("Expense"),
    /**
     * Represents a category for general credits applied to a participant's account or the vanpool.
     * This could include overpayments, refunds, or other positive adjustments that are not standard income.
     * Stored in DB as "CREDIT" (which is its enum name). Displayed as "Credit".
     */
    CREDIT("Credit"),
    /**
     * Represents an unknown or unspecified category type.
     * Stored in DB as "NONE" (which is its enum name). Displayed as "None".
     */
    NONE("None");

    /**
     * The user-friendly string representation of the category type for display purposes.
     */
    private final String displayValue;

    /**
     * Constructor for the CategoryType enum.
     *
     * @param displayValue The user-friendly string value for display purposes.
     */
    CategoryType(String displayValue) {
        this.displayValue = displayValue;
    }

    /**
     * Retrieves the exact string value that should be stored in or read from the database
     * for this category type. This value is the canonical name of the enum constant itself.
     *
     * @return The database-compatible string value (e.g., "INCOME", "EXPENSE", "CREDIT", "NONE").
     */
    public String getDbValue() {
        return this.name(); // Consistent with Role.java, Program.java, InvoiceType.java
    }
    /**
     * Retrieves the user-friendly string value for this category type, suitable for display in the UI.
     *
     * @return The displayable string value.
     */
    public String getDisplayValue() {
        return displayValue;
    }
    /**
     * Returns the user-friendly display value of the category type when this enum constant is converted to a string.
     * This method overrides the default {@link Enum#toString()} behavior to
     * provide the more descriptive {@code displayValue} instead of the enum constant's name,
     * making it suitable for logging and UI display.
     *
     * @return The displayable string value of the category type.
     */
    @Override
    public String toString() {
        return displayValue;
    }
    /**
     * <p>
     * Converts a database string value into its corresponding {@code CategoryType} enum constant.
     * This static method is crucial for deserializing category type data read from the database,
     * providing a type-safe conversion, and handles unknown values gracefully by returning {@link #NONE}.
     * </p>
     * <p>
     * The conversion is robust, trimming whitespace and comparing case-insensitively
     * against the {@code name()} (canonical DB value) of each enum constant. If the provided string is
     * {@code null}, empty, or does not match any valid category type, {@link #NONE} is returned.
     * </p>
     *
     * @param dbValue The string value obtained from a database `CategoryType` column.
     * @return The matching {@code CategoryType} enum constant, or {@link #NONE} if no match is found or input is invalid.
     */
    public static CategoryType fromDbValue(String dbValue) {
        if (dbValue == null || dbValue.trim().isEmpty()) {
            return NONE; // Return NONE for null or empty strings
        }
        String trimmedDbValue = dbValue.trim();
        // Use Arrays.stream for conciseness and robustness
        return Arrays.stream(CategoryType.values())
                .filter(type -> type.name().equalsIgnoreCase(trimmedDbValue)) // Compare against this.name()
                .findFirst()
                .orElse(NONE); // Return NONE if no match is found
    }
}