package com.avaruusstudios.vmdb.model;

import java.util.Arrays;
import java.util.Objects; // Added for Objects.requireNonNull in updated fromDbValue

/**
 * Defines the types of financial categories used in the Vanpool Management System.
 * These types classify money flowing into (INCOME), money flowing out of (EXPENSE),
 * or general adjustments (CREDIT) to the vanpool accounts.
 *
 * <p>
 * This enum's constant names (e.g., "INCOME", "EXPENSE") serve as the canonical
 * string representation for database storage. It also provides a separate
 * user-friendly string for display purposes. Every transaction *must* be
 * classified as one of these three distinct types.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.1
 * Created On: 2025-07-12
 * Updated On: 2025-07-12
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
    CREDIT("Credit");

    // --- Fields ---
    /**
     * The user-friendly string representation of the category type for display purposes.
     */
    private final String displayValue;

    // --- Constructor ---
    /**
     * Constructor for the CategoryType enum.
     *
     * @param displayValue The user-friendly string value for display purposes.
     */
    CategoryType(String displayValue) {
        this.displayValue = displayValue;
    }

    // --- Getters ---
    /**
     * Retrieves the exact string value that should be stored in or read from the database
     * for this category type. This value is the canonical name of the enum constant itself.
     *
     * @return The database-compatible string value (e.g., "INCOME", "EXPENSE", "CREDIT").
     */
    public String getDbValue() {
        return this.name();
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

    // --- Static Factory Method ---
    /**
     * <p>
     * Converts a database string value into its corresponding {@code CategoryType} enum constant.
     * This static method is crucial for deserializing category type data read from the database,
     * providing a type-safe conversion.
     * </p>
     * <p>
     * The conversion is robust, trimming whitespace and comparing case-insensitively
     * against the {@code name()} (canonical DB value) of each enum constant.
     * </p>
     *
     * @param dbValue The string value obtained from a database `CategoryType` column. Must not be {@code null} or empty.
     * @return The matching {@code CategoryType} enum constant.
     * @throws NullPointerException if {@code dbValue} is {@code null}.
     * @throws IllegalArgumentException if {@code dbValue} is empty, or if no matching {@code CategoryType} is found.
     */
    public static CategoryType fromDbValue(String dbValue) {
        Objects.requireNonNull(dbValue, "Category type database value cannot be null.");
        String trimmedDbValue = dbValue.trim();
        if (trimmedDbValue.isEmpty()) {
            throw new IllegalArgumentException("Category type database value cannot be empty.");
        }

        return Arrays.stream(CategoryType.values())
                .filter(type -> type.name().equalsIgnoreCase(trimmedDbValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CategoryType database value: " + dbValue));
    }
}