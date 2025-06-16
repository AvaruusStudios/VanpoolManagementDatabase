package com.avaruusstudios.vmdb.model;

/**
 * <p>
 * Defines the specific types of payment methods accepted for financial transactions
 * within the Vanpool Management System. This enum ensures data integrity by
 * strictly limiting payment method inputs to a predefined set, aligning with
 * common financial processing categories.
 * </p>
 *
 * <p>
 * Each enum constant includes a string representation (`dbValue`) that is
 * intended for storage in the database's `PaymentMethod VARCHAR(50)` field.
 * This approach provides a clear mapping between the Java enum and its
 * persisted form, facilitating robust serialization and deserialization.
 * </p>
 *
 * @see Transaction
 */
public enum PaymentMethod { // Renamed from PaymentMethodType to PaymentMethod
    /**
     * Represents payments made using a dedicated benefit card (e.g., pre-tax commuter benefits).
     * Stored in DB as "Benefit Card".
     */
    BENEFIT_CARD("Benefit Card"),
    /**
     * Represents payments made using a Visa credit or debit card.
     * Stored in DB as "Visa".
     */
    VISA("Visa"),
    /**
     * Represents payments made using an American Express credit card.
     * Stored in DB as "Amex".
     */
    AMEX("Amex"),
    /**
     * Represents payments made using a MasterCard credit or debit card.
     * Stored in DB as "Master Card".
     */
    MASTER_CARD("Master Card");

    /**
     * The string representation of the payment method as stored in the database.
     */
    private final String dbValue;

    /**
     * Constructor for the PaymentMethod enum.
     *
     * @param dbValue The string value used for database storage.
     */
    PaymentMethod(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Retrieves the exact string value that should be stored in or read from the database
     * for this payment method type.
     *
     * @return The database-compatible string value (e.g., "Visa", "Benefit Card").
     */
    public String getDbValue() {
        return dbValue;
    }
    /**
     * Returns the database value of the payment method type when this enum constant is converted to a string.
     *
     * @return The database string value of the payment method type.
     */
    @Override
    public String toString() {
        return dbValue;
    }
    /**
     * <p>
     * Converts a database string value into its corresponding {@code PaymentMethod} enum constant.
     * This static method is crucial for deserializing payment method data read from the database.
     * </p>
     * <p>
     * The comparison is case-insensitive for robustness.
     * </p>
     *
     * @param dbValue The string value obtained from a database `PaymentMethod` column.
     * @return The matching {@code PaymentMethod} enum constant.
     * @throws IllegalArgumentException if the provided string does not match any valid payment method's {@code dbValue}.
     */
    public static PaymentMethod fromDbValue(String dbValue) {
        if (dbValue == null || dbValue.trim().isEmpty()) {
            return null; // Return null if the database value is null or empty, as per Transaction's allowance.
        }
        for (PaymentMethod type : PaymentMethod.values()) {
            if (type.dbValue.equalsIgnoreCase(dbValue.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PaymentMethod database value: '" + dbValue + "'");
    }
}