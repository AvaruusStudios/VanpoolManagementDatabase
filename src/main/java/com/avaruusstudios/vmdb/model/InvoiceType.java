package com.avaruusstudios.vmdb.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Defines the distinct types of invoices that can be generated within the Vanpool Management System.
 * These types significantly influence business logic, particularly the calculation and validation
 * of the invoice's billing period (accessed via {@link Invoice#getPeriodLabel()}).
 *
 * <p>
 * When stored in the database, the canonical name of the enum constant (e.g., "LEASE", "FUEL") is used.
 * For user-facing display in the UI, a more user-friendly PascalCase string (e.g., "Lease", "Fuel")
 * is available via {@link #getDisplayValue()} and {@link #toString()}.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-12
 * Updated On: 2025-07-12
 *
 * @see Invoice
 */
public enum InvoiceType {
    // --- Enum Constants ---
    /**
     * Represents an invoice primarily for vehicle lease charges.
     * When associated with an invoice, the {@code periodLabel} for a LEASE invoice
     * is typically one month *after* the {@code invoiceDate}.
     * Stored in DB as "LEASE".
     */
    LEASE("Lease"),
    /**
     * Represents an invoice primarily for fuel charges.
     * When associated with an invoice, the {@code periodLabel} for a FUEL invoice
     * is typically the *same month* as the {@code invoiceDate}.
     * Stored in DB as "FUEL".
     */
    FUEL("Fuel"),
    /**
     * Represents an invoice type that is unknown, not applicable, or not yet defined.
     * This serves as a default or fallback when a matching type cannot be found,
     * such as during database deserialization, ensuring null safety.
     * Stored in DB as "NONE".
     */
    NONE("None");

    // --- Field ---
    /**
     * The user-friendly string representation of the invoice type, suitable for display in the UI.
     */
    private final String displayValue;

    // --- Constructor ---
    /**
     * Constructs an {@code InvoiceType} enum constant.
     *
     * @param displayValue The user-friendly string value for display purposes (e.g., "Lease").
     */
    InvoiceType(String displayValue) {
        this.displayValue = displayValue;
    }

    // --- Getters ---
    /**
     * Retrieves the canonical name of the enum constant (e.g., "LEASE", "FUEL").
     * This is the recommended string value for storing in the database to ensure consistency
     * with enum constant definitions.
     *
     * @return The ALLCAPS string value used for database storage.
     */
    public String getDbValue() {
        return this.name(); // Returns "LEASE", "FUEL", etc.
    }

    /**
     * Retrieves the user-friendly string value for this invoice type, suitable for display in the UI.
     *
     * @return The displayable string value.
     */
    public String getDisplayValue() {
        return displayValue;
    }

    // --- Utility Methods ---
    /**
     * Returns the user-friendly string representation of this invoice type.
     * This method overrides the default {@link Enum#toString()} behavior to
     * provide the more descriptive {@code displayValue} instead of the enum constant's name,
     * making it suitable for logging and UI display.
     *
     * @return The displayable string value of the invoice type.
     */
    @Override
    public String toString() {
        return displayValue;
    }

    // --- Static Factory Method ---
    /**
     * <p>
     * Converts a database string value (which should be the ALLCAPS canonical name of the enum constant)
     * into its corresponding {@code InvoiceType} enum constant.
     * This static method is crucial for deserializing invoice type data read from the database,
     * providing a type-safe conversion.
     * </p>
     * <p>
     * The conversion is robust, trimming whitespace and converting the input to uppercase
     * to match the enum constant names. If no matching type is found, it gracefully
     * returns {@link #NONE}.
     * </p>
     *
     * @param dbString The string value obtained from a database `InvoiceType` column (e.g., "LEASE").
     * @return The matching {@code InvoiceType} enum constant, or {@link #NONE} if no match is found or the input is {@code null} or empty.
     */
    public static InvoiceType fromDbValue(String dbString) {
        if (dbString == null || dbString.trim().isEmpty()) {
            return NONE; // Return NONE for null or empty strings
        }
        String trimmedUpperDbString = dbString.trim().toUpperCase();
        return Arrays.stream(InvoiceType.values())
                .filter(type -> type.name().equalsIgnoreCase(trimmedUpperDbString))
                .findFirst()
                .orElse(NONE); // Return NONE if no match
    }
}