package com.avaruusstudios.vmdb.db;

/**
 * <p>
 * A custom runtime exception indicating a critical failure during database initialization
 * or schema execution. This exception is thrown by the {@link Database} utility
 * to signal that the application cannot proceed without a properly configured and
 * accessible database.
 * </p>
 *
 * <p>
 * Using a specific exception type allows higher layers of the application to
 * catch and handle database setup failures distinctly from other runtime errors.
 * </p>
 *
 * @see Database
 */
public class DatabaseInitializationException extends RuntimeException {

    /**
     * Constructs a new {@code DatabaseInitializationException} with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link Throwable#getMessage()} method).
     */
    public DatabaseInitializationException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code DatabaseInitializationException} with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
     * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public DatabaseInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}