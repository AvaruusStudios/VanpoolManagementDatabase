package com.avaruusstudios.vmdb.db;

import com.avaruusstudios.vmdb.model.AppErrorCode;
import com.avaruusstudios.vmdb.model.EventLog;
import com.avaruusstudios.vmdb.model.EventType;
import com.avaruusstudios.vmdb.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * <p>
 * Manages the SQLite database operations for the Vanpool Management System.
 * This class is responsible for establishing database connections,
 * verifying database existence, creating the database file if it doesn't exist,
 * and executing the initial schema script.
 * </p>
 *
 * <p>
 * It serves as the primary utility for setting up the database
 * during application initialization, fulfilling the roles outlined in
 * Phase 1, Steps 1 and 2 of the project roadmap.
 * </p>
 *
 * <p>
 * This class uses SLF4J for logging and integrates with the {@link EventLog}
 * system for recording critical database initialization errors.
 * </p>
 *
 * @see AppErrorCode
 * @see EventLog
 * @see com.avaruusstudios.vmdb.db.DatabaseInitializationException
 */
public class Database {
    /**
     * <p>
     * An instance of {@link Logger} from the SLF4J API.
     * This logger is used for capturing and outputting diagnostic messages,
     * such as informational messages about database operations, debugging details,
     * warnings, and errors.
     * </p>
     *
     * <p>
     * It allows for flexible logging configuration (e.g., to console, file)
     * via an underlying logging implementation (like Logback or Log4j2),
     * independent of the application code.
     * </p>
     */
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    /**
     * <p>
     * The file name for the SQLite database.
     * This constant defines the local file where all the application's data
     * will be persistently stored. The database file will be created in the
     * application's working directory if it does not already exist.
     * </p>
     */
    private static final String DATABASE_FILE = "vanpool.db";
    /**
     * <p>
     * The full JDBC (Java Database Connectivity) URL used to establish a connection
     * to the SQLite database. This URL specifies the protocol (`jdbc:sqlite:`)
     * and the path to the database file defined by {@link #DATABASE_FILE}.
     * It's crucial for {@link DriverManager#getConnection(String)} to locate and connect
     * to the correct database instance.
     * </p>
     */
    private static final String JDBC_URL = "jdbc:sqlite:" + DATABASE_FILE;
    /**
     * <p>
     * The classpath resource path to the SQL schema definition file.
     * This file, named `schema.sql`, is expected to contain all the
     * `CREATE TABLE` and `CREATE INDEX` statements necessary to
     * initialize the database structure from scratch. It is loaded
     * as a resource from within the application's JAR file.
     * </p>
     */
    private static final String SCHEMA_FILEPATH = "/com/avaruusstudios/vmdb/db/schema.sql";

    /**
     * Static initializer block to ensure the SQLite JDBC driver is loaded when the class is initialized.
     * This prevents potential `ClassNotFoundException` errors during database connection attempts.
     */
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            logger.info("SQLite JDBC driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            // Log to EventLog and throw a critical error as database operations are impossible without the driver
            logDatabaseError(
                    null, // No user context during driver load
                    AppErrorCode.DB_CONNECTION_FAILED.getValue(),
                    "Failed to load SQLite JDBC driver: " + e.getMessage(),
                    "Database.static block" // Context
            );
            // Re-throw as a runtime exception since the application cannot proceed without the driver
            throw new DatabaseInitializationException("SQLite JDBC driver not found.", e);
        }
    }
    /**
     * Establishes and returns a connection to the SQLite database.
     * It is the caller's responsibility to close this connection properly
     * using a try-with-resources statement or a finally block.
     *
     * @return An active {@link Connection} to the database.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL);
            logger.debug("Database connection established.");
            return connection;
        } catch (SQLException e) {
            logDatabaseError(
                    null, // No specific user context for connection failure
                    AppErrorCode.DB_CONNECTION_FAILED.getValue(),
                    "Failed to establish database connection to " + JDBC_URL + ": " + e.getMessage(),
                    "Database.getConnection()"
            );
            throw e; // Re-throw the original SQLException
        }
    }
    /**
     * Checks if the SQLite database file already exists on the file system.
     *
     * @return {@code true} if the database file exists, {@code false} otherwise.
     */
    private static boolean databaseExists() {
        return Files.exists(Paths.get(DATABASE_FILE));
    }
    /**
     * <p>
     * Initializes the database. If the database file does not exist, it creates the file
     * and executes the schema from the `schema.sql` classpath resource.
     * </p>
     *
     * <p>
     * If the database file already exists, it does nothing and logs a message.
     * This method ensures the database is ready for use upon application startup.
     * </p>
     *
     * @throws DatabaseInitializationException If an error occurs during database creation or schema execution.
     */
    public static void createDatabase() {
        if (!databaseExists()) {
            logger.info("Database file '{}' not found. Attempting to create...", DATABASE_FILE);
            try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
                if (connection != null) {
                    logger.info("Database '{}' created successfully.", DATABASE_FILE);
                    executeSchema(connection);
                }
            } catch (SQLException e) {
                logDatabaseError(
                        null, // User context might be null during app init
                        AppErrorCode.DB_CONNECTION_FAILED.getValue(),
                        "Error creating database connection for '" + DATABASE_FILE + "': " + e.getMessage(),
                        "Database.createDatabase()"
                );
                throw new DatabaseInitializationException("Failed to create database: " + e.getMessage(), e);
            }
        } else {
            logger.info("Database '{}' already exists. Skipping creation.", DATABASE_FILE);
        }
    }
    /**
     * <p>
     * Executes the SQL statements contained in the `schema.sql` classpath resource.
     * This method is typically called only when the database is first created.
     * </p>
     *
     * <p>
     * The SQL script is split by semicolons. While generally effective for DDL statements,
     * this simple parsing mechanism may be insufficient for complex SQL scripts containing
     * semicolons within string literals, comments, or procedural blocks.
     * </p>
     *
     * @param connection The active {@link Connection} to the database.
     * @throws DatabaseInitializationException If an error occurs while reading the schema file or executing SQL.
     */
    private static void executeSchema(Connection connection) {
        logger.info("Executing database schema from {}...", SCHEMA_FILEPATH);
        try (InputStream inputStream = Database.class.getResourceAsStream(SCHEMA_FILEPATH);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                String errorMsg = "Error: Could not find schema file at " + SCHEMA_FILEPATH;
                logDatabaseError(
                        null,
                        AppErrorCode.DB_SCHEMA_EXECUTION_ERROR.getValue(),
                        errorMsg,
                        "Database.executeSchema()"
                );
                throw new DatabaseInitializationException(errorMsg);
            }

            String sqlStatements = reader.lines().collect(Collectors.joining("\n"));
            // Split by semicolon, optionally followed by whitespace and a newline,
            // to handle statements separated by just ';' or ';<newline>'
            String[] individualStatements = sqlStatements.split(";\\s*\\n?");

            try (Statement statement = connection.createStatement()) {
                for (String sql : individualStatements) {
                    String trimmedSql = sql.trim();
                    if (!trimmedSql.isEmpty()) {
                        logger.debug("Executing SQL: {}", trimmedSql);
                        statement.executeUpdate(trimmedSql);
                    }
                }
            }
            logger.info("Database schema executed successfully.");

        } catch (IOException e) {
            String errorMsg = "Error reading schema file '" + SCHEMA_FILEPATH + "': " + e.getMessage();
            logDatabaseError(
                    null,
                    AppErrorCode.DB_SCHEMA_EXECUTION_ERROR.getValue(),
                    errorMsg,
                    "Database.executeSchema()"
            );
            throw new DatabaseInitializationException(errorMsg, e);
        } catch (SQLException e) {
            String errorMsg = "Error executing schema SQL: " + e.getMessage();
            logDatabaseError(
                    null,
                    e.getErrorCode(), // Use specific DB error code
                    errorMsg,
                    "Database.executeSchema()"
            );
            throw new DatabaseInitializationException(errorMsg, e);
        }
    }
    /**
     * <p>
     * Logs database-related errors to the application's logger and creates an {@link EventLog} entry.
     * This method centralizes error logging during critical database initialization steps.
     * </p>
     *
     * <p>
     * Note: For simplicity in a static `Database` utility, a `null` {@link User} is passed
     * if no user context is available (e.g., during application startup). In other parts of the application,
     * a specific {@link User} object would be provided.
     * </p>
     *
     * @param user        The {@link User} associated with the error, or {@code null} if no specific user context.
     * @param errorCode   The numeric error code (either from {@link AppErrorCode} or a raw SQL error code).
     * @param description A descriptive message for the error.
     * @param context     A string indicating where the error occurred (e.g., "Database.getConnection()").
     */
    private static void logDatabaseError(User user, Integer errorCode, String description, String context) {
        // Log to standard logger (e.g., console/file via Logback/Log4j2)
        logger.error("[{}]: {}", context, description);

        // Create and log to EventLog (assuming a mechanism to save EventLog exists,
        // or a simple static method to directly create an EventLog instance).
        // For actual persistence, this would ideally go through an EventLogDAO.
        // For now, we'll just instantiate it; actual saving would happen later.
        EventLog eventLog = new EventLog(
                0, // EventID will be auto-generated by DB
                user,
                LocalDateTime.now(),
                EventType.ERROR,
                "DatabaseInitialization", // TableName can be a descriptive string for system errors
                null, // RecordID not applicable for system errors
                errorCode,
                description
        );
        // In a full application, you'd have an EventLogDAO here:
        // try { eventLogDAO.save(eventLog); } catch (SQLException logEx) { logger.error("Failed to save EventLog: " + logEx.getMessage()); }
        logger.debug("EventLog entry created for DB error: {}", eventLog);
    }
}