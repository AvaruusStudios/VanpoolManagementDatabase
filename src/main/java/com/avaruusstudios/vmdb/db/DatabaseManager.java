package com.avaruusstudios.vmdb.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <p>
 * Manages database connections and transactions for the Vanpool Management System.
 * This class is the primary entry point for obtaining active {@link Connection}
 * objects and controlling the transactional behavior of database operations.
 * </p>
 * <ul>
 * <li>Provides {@link Connection} objects to other parts of the application,
 * particularly to DAO implementations.</li>
 * <li>Manages transaction lifecycle (begin, commit, rollback) for database operations.</li>
 * </ul>
 *
 * <p>
 * It uses SLF4J for logging.
 * </p>
 *
 * @see Database
 * @see com.avaruusstudios.vmdb.dao.GenericDao
 */
public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DATABASE_FILE = "vanpool.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DATABASE_FILE;

    // No longer needs a static block to load driver, as Database.java already does that.
    // Also, Database.createDatabase() ensures the file exists and schema is run.

    /**
     * Establishes a new connection to the SQLite database and returns the active {@link Connection} object.
     * This is the primary method for DAOs and other data access components to obtain a database connection.
     * <p>
     * It is the caller's responsibility to close this connection properly using a try-with-resources statement
     * or a finally block to prevent resource leaks.
     * </p>
     *
     * @return An active {@link Connection} to the database.
     * @throws SQLException If a database access error occurs during connection establishment.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL);
            logger.debug("Database connection established by DatabaseManager.");
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to establish database connection to {}: {}", JDBC_URL, e.getMessage(), e);
            // In a real application, you might wrap this in a custom runtime exception
            // for higher layers, or use EventLog here.
            throw e; // Re-throw the original SQLException
        }
    }

    /**
     * Begins a transaction on the provided database connection.
     * Sets the auto-commit mode to false.
     *
     * @param connection The JDBC Connection object.
     * @throws SQLException If a database access error occurs.
     */
    public static void beginTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(false);
            logger.debug("Transaction started on connection.");
        }
    }

    /**
     * Commits the current transaction on the provided database connection.
     * Resets the auto-commit mode to true after committing.
     *
     * @param connection The JDBC Connection object.
     * @throws SQLException If a database access error occurs.
     */
    public static void commitTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.commit();
            connection.setAutoCommit(true); // Reset to true after commit
            logger.debug("Transaction committed on connection.");
        }
    }

    /**
     * Rolls back the current transaction on the provided database connection.
     * Resets the auto-commit mode to true after rolling back.
     *
     * @param connection The JDBC Connection object.
     * @throws SQLException If a database access error occurs.
     */
    public static void rollbackTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.rollback();
            connection.setAutoCommit(true); // Reset to true after rollback
            logger.warn("Transaction rolled back on connection.");
        }
    }

    // Placeholder for schema update/migration (if you decide to implement it here later)
    // public static void runSchemaUpdateScript(String scriptFileName) throws SQLException {
    //     // Implementation would involve reading and executing SQL from a file,
    //     // similar to Database.executeSchema, but for updates/migrations.
    // }

    // Removed getRosterCount() as it belongs in the Service Layer.
}