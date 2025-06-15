package com.avaruusstudios.vmdb.model;

/**
 * <p>
 * Defines the types of financial categories used in the Vanpool Management System.
 * These types classify money flowing into (INCOME), money flowing out of (EXPENSE),
 * or general adjustments (CREDIT) to the vanpool accounts.
 * </p>
 *
 * <p>
 * This enum maps to a string representation for database storage.
 * </p>
 *
 * @see Category
 */
public enum CategoryType {
    /**
     * Represents a category for money coming into the vanpool (e.g., participant payments, external funding).
     * Stored in DB as "INCOME".
     */
    INCOME("INCOME"),
    /**
     * Represents a category for money going out of the vanpool (e.g., fuel, maintenance, tolls, van rental).
     * Stored in DB as "EXPENSE".
     */
    EXPENSE("EXPENSE"),
    /**
     * Represents a category for general credits applied to a participant's account or the vanpool.
     * This could include overpayments, refunds, or other positive adjustments that are not standard income.
     * Stored in DB as "CREDIT".
     */
    CREDIT("CREDIT"); // Only INCOME, EXPENSE, CREDIT

    /**
     * The string representation of the category type as stored in the database.
     */
    private final String dbValue;

    /**
     * Constructor for the CategoryType enum.
     *
     * @param dbValue The string value used for database storage.
     */
    CategoryType(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Retrieves the exact string value that should be stored in or read from the database
     * for this category type.
     *
     * @return The database-compatible string value (e.g., "INCOME", "EXPENSE", "CREDIT").
     */
    public String getDbValue() {
        return dbValue;
    }
    /**
     * Returns the database value of the category type when this enum constant is converted to a string.
     *
     * @return The database string value of the category type.
     */
    @Override
    public String toString() {
        return dbValue;
    }
    /**
     * <p>
     * Converts a database string value into its corresponding {@code CategoryType} enum constant.
     * This static method is crucial for deserializing category type data read from the database.
     * </p>
     * <p>
     * The comparison is case-insensitive for robustness.
     * </p>
     *
     * @param dbValue The string value obtained from a database `CategoryType` column.
     * @return The matching {@code CategoryType} enum constant.
     * @throws IllegalArgumentException if the provided string does not match any valid category type's {@code dbValue}.
     */
    public static CategoryType fromDbValue(String dbValue) {
        if (dbValue == null || dbValue.trim().isEmpty()) {
            throw new IllegalArgumentException("CategoryType database value cannot be null or empty.");
        }
        for (CategoryType type : CategoryType.values()) {
            if (type.dbValue.equalsIgnoreCase(dbValue.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown CategoryType database value: '" + dbValue + "'");
    }
}