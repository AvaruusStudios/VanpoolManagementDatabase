package com.avaruusstudios.vmdb.data; // Changed package to 'data'

/**
 * <p>
 * This custom {@code RuntimeException} serves as a specialized exception type for
 * all errors originating from the Data Access Object (DAO) layer within the
 * Vanpool Management Database (VMDB) application. It provides a more
 * abstract and consistent way to handle database-related issues across
 * different layers of the application.
 * </p>
 *
 * <p>
 * Instead of propagating low-level, checked exceptions like {@code java.sql.SQLException}
 * up through the Service Layer to the User Interface, {@code DaoException}
 * wraps these underlying causes. This design choice promotes:
 * <ul>
 * <li><b>Abstraction:</b> Higher layers of the application (e.g., Service Layer)
 * do not need to be aware of the specific JDBC {@code SQLException} details.</li>
 * <li><b>Consistency:</b> All data access errors are represented by a single,
 * predictable exception type.</li>
 * <li><b>Maintainability:</b> Changes in the underlying data access technology
 * (e.g., switching from JDBC to an ORM) would ideally only require changes
 * within the DAO layer, without impacting service layer exception handling.</li>
 * <li><b>Cleaner Code:</b> Being a {@code RuntimeException}, it does not need
 * to be explicitly declared in method signatures, reducing boilerplate code.</li>
 * </ul>
 * </p>
 *
 * <p>
 * When a {@code DaoException} is caught, its message typically provides a
 * high-level description of the failure, while its cause (accessible via
 * {@link #getCause()}) can reveal the specific technical exception that occurred
 * at the database level.
 * </p>
 *
 * @author [Your Name/AvaruusStudios]
 * @version 1.0
 * @since 2025-07-04
 *
 * @see DeleteDataAccess
 * @see java.sql.SQLException
 * @see java.lang.RuntimeException
 */
public class DatabaseAccessException extends RuntimeException {
  /**
   * <p>
   * Constructs a new {@code DaoException} with the specified detail message.
   * This constructor is suitable when the underlying cause of the exception
   * is not available or not being explicitly wrapped.
   * </p>
   *
   * @param message The detailed message explaining the nature of the data access error.
   * This message is intended for logging and debugging purposes,
   * providing immediate context about the failure.
   */
  public DatabaseAccessException(String message) {
    super(message);
  }
  /**
   * <p>
   * Constructs a new {@code DaoException} with the specified detail message
   * and a wrapped cause. This is the preferred constructor when re-throwing
   * a lower-level exception (e.g., {@code SQLException}) as a {@code DaoException}.
   * </p>
   *
   * <p>
   * The {@code cause} allows for the preservation of the original exception's
   * stack trace and details, which is crucial for thorough error analysis
   * and debugging.
   * </p>
   *
   * @param message The detailed message explaining the nature of the data access error.
   * This message should provide user-friendly or layer-appropriate context.
   * @param cause The underlying {@code Throwable} (e.g., {@code SQLException}) that
   * caused this {@code DaoException}. A {@code null} value is permitted
   * if the cause is nonexistent or unknown, though providing a cause
   * is highly recommended for robust error handling.
   */
  public DatabaseAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}