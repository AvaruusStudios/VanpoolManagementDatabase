package com.avaruusstudios.vmdb.service;

/**
 * <p>
 * Custom exception class for the service layer of the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * This exception serves as a generic wrapper for errors that occur within the service layer,
 * such as business rule violations or issues with the underlying data access objects.
 * It provides a clean, abstract way to handle exceptions without exposing the low-level
 * details of the database or other dependencies to the controller or presentation layers.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-08-29
 */
public class ServiceException extends RuntimeException {

    /**
     * Constructs a new ServiceException with the specified detail message.
     *
     * @param message The detail message. The detail message is saved for
     * later retrieval by the {@link #getMessage()} method.
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new ServiceException with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is *not*
     * automatically incorporated in this exception's detail message.
     *
     * @param message The detail message.
     * @param cause   The cause (which is saved for later retrieval by the
     * {@link #getCause()} method).
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}