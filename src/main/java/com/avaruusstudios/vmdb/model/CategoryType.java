package com.avaruusstudios.vmdb.model;

/**
 * <p>
 * Defines the possible types for a financial category.
 * These types are strictly enforced as defined in the database schema's
 * `CHECK (CategoryType IN ('Income', 'Expense', 'Credit'))` constraint.
 * </p>
 *
 * <p>
 * Using an enum provides type safety, prevents invalid category type strings,
 * and improves code readability.
 * </p>
 */
public enum CategoryType {
    /**
     * Represents categories related to money coming into the system.
     */
    INCOME("Income"),
    /**
     * Represents categories related to money flowing out of the system.
     */
    EXPENSE("Expense"),
    /**
     * Represents categories related to credit adjustments or refunds.
     */
    CREDIT("Credit");

    private final String dbValue;

    /**
     * Constructor for the CategoryType enum.
     *
     * @param dbValue The string representation of the category type as stored in the database.
     */
    CategoryType(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Returns the string representation of the category type used in the database.
     *
     * @return The database string value (e.g., "Income", "Expense", "Credit").
     */
    public String getDbValue() {
        return dbValue;
    }
    /**
     * Converts a database string value into its corresponding `CategoryType` enum constant.
     * This method is useful when loading data from the database.
     *
     * @param dbValue The string value from the database (case-insensitive match).
     * @return The matching `CategoryType` enum constant.
     * @throws IllegalArgumentException if the provided string does not match any valid category type.
     */
    public static CategoryType fromDbValue(String dbValue) {
        for (CategoryType type : CategoryType.values()) {
            if (type.dbValue.equalsIgnoreCase(dbValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown CategoryType database value: " + dbValue);
    }
}
