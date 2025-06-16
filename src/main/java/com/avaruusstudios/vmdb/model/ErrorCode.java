package com.avaruusstudios.vmdb.model;

/**
 * <p>
 * Defines a comprehensive set of custom error codes for the Vanpool Management System.
 * These codes are used to categorize and identify any and all errors that could be
 * encountered within the application, including system, runtime, configuration,
 * security, data handling, business logic, and external resource related issues.
 * </p>
 *
 * <p>
 * Each error code is associated with a unique integer value and a default description,
 * enhancing clarity and debugging efficiency within the {@link EventLog}.
 * The codes are categorized by thousands ranges, similar to a Standard Subject
 * Identification Code (SSIC) structure, for better organization and quick identification:
 * <ul>
 * <li>1XXX: System / Runtime Errors (e.g., unexpected states, internal processing, database connection)</li>
 * <li>2XXX: Configuration / Setup Errors</li>
 * <li>3XXX: Security / Access Control Errors (e.g., authentication, permissions)</li>
 * <li>4XXX: Data Handling / Validation Errors (e.g., missing fields, invalid input, duplicates)</li>
 * <li>5XXX: Business Logic / Process Errors (e.g., dependencies, insufficient resources, rule violations)</li>
 * <li>6XXX: External Resource / I/O Errors (e.g., file operations)</li>
 * </ul>
 * </p>
 *
 * @see EventLog
 */
public enum ErrorCode { // Renamed from AppErrorCode to ErrorCode
    // --- 1000s: System / Runtime Errors ---
    /** Indicates an unexpected or unhandled application state. */
    UNEXPECTED_APP_STATE(1000, "Unexpected application state encountered."),
    /** Generic internal processing error. */
    INTERNAL_PROCESSING_ERROR(1001, "An internal processing error occurred."),
    /** Database connection could not be established or driver failed to load. */
    DB_CONNECTION_FAILED(1002, "Failed to establish database connection or load driver."),
    /** Error occurred while executing the database schema script. */
    DB_SCHEMA_EXECUTION_ERROR(1003, "Error during database schema script execution."),
    /** Indicates an unmapped SQL error. */
    UNKNOWN_SQL_ERROR(1004, "An unknown SQL error occurred."), // Added this line


    // --- 2000s: Configuration / Setup Errors ---
    /** Application configuration setting is missing or invalid. */
    INVALID_CONFIGURATION(2000, "Invalid or missing application configuration."),
    /** Application failed to initialize a critical component. */
    COMPONENT_INITIALIZATION_FAILED(2001, "Critical component failed to initialize."),

    // --- 3000s: Security / Access Control Errors ---
    /** User authentication failed. */
    AUTHENTICATION_FAILED(3000, "User authentication failed."),
    /** User attempted an action without sufficient permissions. */
    PERMISSION_DENIED(3001, "User does not have required permissions."),
    /** Attempt to access unauthorized data. */
    UNAUTHORIZED_DATA_ACCESS(3002, "Attempt to access unauthorized data."),

    // --- 4000s: Data Handling / Validation Errors ---
    /** A required field was missing during data submission. */
    MISSING_REQUIRED_FIELD(4000, "A required data field was not provided."),
    /** Input value for a numeric field is invalid or non-numeric. */
    INVALID_NUMERIC_INPUT(4001, "Invalid numeric input for field."),
    /** Input value for a date/time field is invalid or malformed. */
    INVALID_DATE_TIME_INPUT(4002, "Invalid date/time format or value."),
    /** Input value exceeds maximum allowed length. */
    INPUT_TOO_LONG(4003, "Input value exceeds maximum allowed length."),
    /** Provided value is a duplicate where uniqueness is required. */
    DUPLICATE_UNIQUE_VALUE(4004, "A value requiring uniqueness already exists."),
    /** An invalid or unsupported enumeration value was provided. */
    INVALID_ENUM_VALUE(4005, "Invalid or unsupported enumeration value provided."),

    // --- 5000s: Business Logic / Process Errors ---
    /** Attempt to delete a record that has active dependencies. */
    DEPENDENCY_EXISTS(5000, "Cannot perform action: dependent records exist."),
    /** Insufficient funds or capacity for an operation. */
    INSUFFICIENT_RESOURCE(5001, "Insufficient funds or capacity for the operation."),
    /** Action violates a defined business rule. */
    BUSINESS_RULE_VIOLATION(5002, "Action violates a business rule."),
    /** Attempted operation is not allowed in the current record state. */
    INVALID_RECORD_STATE(5003, "Operation not allowed in current record state."),
    /** Data inconsistency detected during a business operation. */
    DATA_INCONSISTENCY(5004, "Data inconsistency detected."),

    // --- 6000s: External Resource / I/O Errors ---
    /** Required file not found on the file system. */
    FILE_NOT_FOUND(6000, "Required file not found."),
    /** Application lacks necessary permissions to read/write a file. */
    FILE_PERMISSION_DENIED(6001, "File access permission denied."),
    /** File is corrupted or in an unreadable format. */
    FILE_CORRUPTED(6002, "File is corrupted or unreadable."),
    /** Error during file read/write operation. */
    FILE_IO_ERROR(6003, "Error during file input/output operation.");

    private final int value;
    private final String defaultDescription;

    /**
     * Constructs an ErrorCode enum constant.
     *
     * @param value The unique integer value for the error code.
     * @param defaultDescription A default, human-readable description for the error.
     */
    ErrorCode(int value, String defaultDescription) {
        this.value = value;
        this.defaultDescription = defaultDescription;
    }
    /**
     * Retrieves the integer value of the error code.
     * This is the value that would be stored in the `ErrorCode` column of the `EventLog` table.
     *
     * @return The integer representation of the error code.
     */
    public int getValue() {
        return value;
    }
    /**
     * Retrieves a default description for the error code.
     * This can be used as a starting point for the `Description` field in the `EventLog`.
     *
     * @return A string describing the error.
     */
    public String getDefaultDescription() {
        return defaultDescription;
    }
    /**
     * Returns the integer value of the error code when this enum constant is converted to a string.
     *
     * @return The integer value as a string.
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    /**
     * <p>
     * Converts an integer error code value into its corresponding {@code ErrorCode} enum constant.
     * This static method can be useful when retrieving error codes from logs or external sources.
     * </p>
     *
     * @param value The integer value of the error code.
     * @return The matching {@code ErrorCode} enum constant.
     * @throws IllegalArgumentException if the provided integer value does not match any known error code.
     */
    public static ErrorCode fromValue(int value) { // Updated to ErrorCode
        for (ErrorCode code : ErrorCode.values()) { // Updated to ErrorCode
            if (code.value == value) {
                return code;
            }
        }
        throw new IllegalArgumentException("Unknown ErrorCode value: " + value); // Updated to ErrorCode
    }
}