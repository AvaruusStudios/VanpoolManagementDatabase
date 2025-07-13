package com.avaruusstudios.vmdb.model;

import java.util.Arrays;
import java.util.Objects; // Although not strictly needed for this specific `fromDbValue`, good to include if other methods might use it.

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
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-12
 * Updated On: 2025-07-12
 *
 * @see Transaction
 */
public enum PaymentMethod { // Renamed from PaymentMethodType to PaymentMethod
    // --- Enum Constants ---
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

    // --- Field ---
    /**
     * The string representation of the payment method as stored in the database.
     */
    private final String dbValue;

    // --- Constructor ---
    /**
     * Constructor for the PaymentMethod enum.
     *
     * @param dbValue The string value used for database storage.
     */
    PaymentMethod(String dbValue) {
        this.dbValue = dbValue;
    }

    // --- Getters ---
    /**
     * Retrieves the exact string value that should be stored in or read from the database
     * for this payment method type.
     *
     * @return The database-compatible string value (e.g., "Visa", "Benefit Card").
     */
    public String getDbValue() {
        return dbValue;
    }

    // --- Utility Methods ---
    /**
     * Returns the database value of the payment method type when this enum constant is converted to a string.
     * This method overrides the default {@link Enum#toString()} behavior to provide
     * the direct database string value, making it suitable for logging and database interactions.
     *
     * @return The database string value of the payment method type.
     */
    @Override
    public String toString() {
        return dbValue;
    }

    // --- Static Factory Method ---
    /**
     * <p>
     * Converts a database string value into its corresponding {@code PaymentMethod} enum constant.
     * This static method is crucial for deserializing payment method data read from the database.
     * </p>
     * <p>
     * The comparison is case-insensitive for robustness. If the provided string is {@code null} or
     * empty after trimming, the method returns {@code null}, aligning with cases where a payment method
     * might not be explicitly set in the database (e.g., for certain transaction types or initial states).
     * </p>
     *
     * @param dbValue The string value obtained from a database `PaymentMethod` column.
     * @return The matching {@code PaymentMethod} enum constant, or {@code null} if the input is {@code null} or empty.
     * @throws IllegalArgumentException if the provided non-empty string does not match any valid payment method's {@code dbValue}.
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