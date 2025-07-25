package com.avaruusstudios.vmdb.model;

import java.util.Arrays;

/**
 * <p>
 * Defines the possible payment statuses for an {@link Invoice} within the Vanpool Management System.
 * These statuses are crucial for tracking the financial lifecycle of an invoice, from creation
 * through various stages of payment.
 * </p>
 *
 * <p>
 * When stored in the database, the canonical name of the enum constant (e.g., "UNPAID", "PAID") is used.
 * For user-facing display in the UI, a more user-friendly PascalCase string (e.g., "Unpaid", "Paid")
 * is available via {@link #getDisplayValue()} and {@link #toString()}.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-25
 * Updated On: 2025-07-25
 *
 * @see Invoice
 */
public enum PaymentStatus {
    /**
     * Represents an invoice that has been issued but no payment has been received yet.
     * Stored in DB as "UNPAID".
     */
    UNPAID("Unpaid"),
    /**
     * Represents an invoice for which some, but not all, of the total amount has been paid.
     * Stored in DB as "PARTIALLY_PAID".
     */
    PARTIALLY_PAID("Partially Paid"),
    /**
     * Represents an invoice for which the full amount due has been received.
     * Stored in DB as "PAID".
     */
    PAID("Paid"),
    /**
     * Represents an invoice whose due date has passed without full payment being received.
     * Stored in DB as "OVERDUE".
     */
    OVERDUE("Overdue"),
    /**
     * Represents an invoice that has been cancelled and is no longer expected to be paid.
     * Stored in DB as "CANCELLED".
     */
    CANCELLED("Cancelled");

    // --- Field ---
    /**
     * The user-friendly string representation of the payment status, suitable for display in the UI.
     */
    private final String displayValue;

    // --- Constructor ---
    /**
     * Constructs a {@code PaymentStatus} enum constant.
     *
     * @param displayValue The user-friendly string value for display purposes (e.g., "Unpaid").
     */
    PaymentStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    // --- Getters ---
    /**
     * Retrieves the canonical name of the enum constant (e.g., "UNPAID", "PAID").
     * This is the recommended string value for storing in the database to ensure consistency
     * with enum constant definitions.
     *
     * @return The ALLCAPS string value used for database storage.
     */
    public String getDbValue() {
        return this.name(); // Returns "UNPAID", "PAID", etc.
    }

    /**
     * Retrieves the user-friendly string value for this payment status, suitable for display in the UI.
     *
     * @return The displayable string value.
     */
    public String getDisplayValue() {
        return displayValue;
    }

    // --- Utility Methods ---
    /**
     * Returns the user-friendly string representation of this payment status.
     * This method overrides the default {@link Enum#toString()} behavior to
     * provide the more descriptive {@code displayValue} instead of the enum constant's name,
     * making it suitable for logging and UI display.
     *
     * @return The displayable string value of the payment status.
     */
    @Override
    public String toString() {
        return displayValue;
    }

    // --- Static Factory Method ---
    /**
     * <p>
     * Converts a database string value (which should be the ALLCAPS canonical name of the enum constant)
     * into its corresponding {@code PaymentStatus} enum constant.
     * This static method is crucial for deserializing payment status data read from the database,
     * providing a type-safe conversion.
     * </p>
     * <p>
     * The conversion is robust, trimming whitespace and converting the input to uppercase
     * to match the enum constant names. If no matching status is found, it gracefully
     * returns {@link #UNPAID} as a reasonable default for an unknown or uninitialized state.
     * </p>
     *
     * @param dbString The string value obtained from a database `PaymentStatus` column (e.g., "PAID").
     * @return The matching {@code PaymentStatus} enum constant, or {@link #UNPAID} if no match is found or the input is {@code null} or empty.
     */
    public static PaymentStatus fromDbValue(String dbString) {
        if (dbString == null || dbString.trim().isEmpty()) {
            return UNPAID; // Default to UNPAID for null or empty strings
        }
        String trimmedUpperDbString = dbString.trim().toUpperCase();
        return Arrays.stream(PaymentStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(trimmedUpperDbString))
                .findFirst()
                .orElse(UNPAID); // Default to UNPAID if no match
    }
}