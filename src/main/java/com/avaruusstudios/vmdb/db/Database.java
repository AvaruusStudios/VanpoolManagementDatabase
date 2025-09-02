package com.avaruusstudios.vmdb.db;

import com.avaruusstudios.vmdb.model.ErrorCode;
import com.avaruusstudios.vmdb.model.EventLog;
import com.avaruusstudios.vmdb.model.EventType;
import com.avaruusstudios.vmdb.model.User; // Assuming User model for EventLog
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
 * Manages the initial creation and schema application for the SQLite database file
 * for the Vanpool Management System.
 * This class is solely responsible for:
 * </p>
 * <ul>
 * <li>Verifying database file existence.</li>
 * <li>Creating the database file if it doesn't exist.</li>
 * <li>Executing the initial schema script upon database creation.</li>
 * <li>Ensuring the necessary JDBC driver is loaded.</li>
 * </ul>
 *
 * <p>
 * This class fulfills the role of the database setup handler.
 * Database connections and transaction management are now handled by {@link DatabaseManager}.
 * </p>
 *
 * <p>
 * It uses SLF4J for logging and integrates with the {@link EventLog}
 * system for recording critical database initialization errors.
 * </p>
 *
 * @see ErrorCode
 * @see EventLog
 * @see com.avaruusstudios.vmdb.db.DatabaseInitializationException
 * @see DatabaseManager
 */
public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static final String DATABASE_FILE = "vanpool.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DATABASE_FILE; // Still needed for initial connection in createDatabase
    private static final String SCHEMA_FILEPATH = "/com/avaruusstudios/vmdb/db/schema.sql";
    private static final String DATA_MANIPULATION_FILEPATH = "/com/avaruusstudios/vmdb/db/data.sql";

    /**
     * Static initializer block to ensure the SQLite JDBC driver is loaded when the class is initialized.
     * This prevents potential `ClassNotFoundException` errors during database connection attempts.
     * This block runs only once when the `Database` class is first loaded by the JVM.
     */
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            logger.info("SQLite JDBC driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            logDatabaseError(
                    null,
                    ErrorCode.DB_CONNECTION_FAILED,
                    "Failed to load SQLite JDBC driver: " + e.getMessage(),
                    "Database.static block (driver load)",
                    e
            );
            throw new DatabaseInitializationException("SQLite JDBC driver not found. Cannot initialize database.", e);
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
            // Temporarily get a connection just for the creation and schema execution
            try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
                if (connection != null) {
                    logger.info("Database '{}' created successfully.", DATABASE_FILE);
                    executeSchema(connection);
                    executeData(connection);
                }
            } catch (SQLException e) {
                logDatabaseError(
                        null,
                        ErrorCode.DB_CONNECTION_FAILED,
                        "Error creating database file and connection for '" + DATABASE_FILE + "': " + e.getMessage(),
                        "Database.createDatabase()",
                        e
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
     * @param connection The active {@link Connection} to the database for schema execution.
     * @throws DatabaseInitializationException If an error occurs while reading the schema file or executing SQL.
     */
    private static void executeSchema(Connection connection) {
        logger.info("Executing database schema from {}...", SCHEMA_FILEPATH);
        try (InputStream inputStream = Database.class.getResourceAsStream(SCHEMA_FILEPATH)) {
            if (inputStream == null) {
                String errorMsg = "Error: Could not find schema file at " + SCHEMA_FILEPATH;
                throw new DatabaseInitializationException(errorMsg);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String sqlStatements = reader.lines().collect(Collectors.joining("\n"));
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
            }
        } catch (IOException e) {
            String errorMsg = "Error reading schema file '" + SCHEMA_FILEPATH + "': " + e.getMessage();
            logDatabaseError(
                    null,
                    ErrorCode.FILE_IO_ERROR,
                    errorMsg,
                    "Database.executeSchema()",
                    e
            );
            throw new DatabaseInitializationException(errorMsg, e);
        } catch (SQLException e) {
            String errorMsg = "Error executing schema SQL (SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode() + "): " + e.getMessage();
            logDatabaseError(
                    null,
                    ErrorCode.UNKNOWN_SQL_ERROR,
                    errorMsg,
                    "Database.executeSchema()",
                    e
            );
            throw new DatabaseInitializationException(errorMsg, e);
        }
    }

    /**
     * <p>
     * Executes the SQL statements contained in the `data.sql` classpath resource.
     * This method is typically called only when the database is first created.
     * </p>
     *
     * @param connection The active {@link Connection} to the database for data execution.
     * @throws DatabaseInitializationException If an error occurs while reading the data file or executing SQL.
     */
    private static void executeData(Connection connection) {
        logger.info("Executing database data from {}...", DATA_MANIPULATION_FILEPATH);
        try (InputStream inputStream = Database.class.getResourceAsStream(DATA_MANIPULATION_FILEPATH)) {
            if (inputStream == null) {
                String errorMsg = "Error: Could not find data file at " + DATA_MANIPULATION_FILEPATH;
                throw new DatabaseInitializationException(errorMsg);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String sqlStatements = reader.lines().collect(Collectors.joining("\n"));
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
                logger.info("Database data executed successfully.");
            }
        } catch (IOException e) {
            String errorMsg = "Error reading data file '" + DATA_MANIPULATION_FILEPATH + "': " + e.getMessage();
            logDatabaseError(
                    null,
                    ErrorCode.FILE_IO_ERROR,
                    errorMsg,
                    "Database.executeData()",
                    e
            );
            throw new DatabaseInitializationException(errorMsg, e);
        } catch (SQLException e) {
            String errorMsg = "Error executing data SQL (SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode() + "): " + e.getMessage();
            logDatabaseError(
                    null,
                    ErrorCode.UNKNOWN_SQL_ERROR,
                    errorMsg,
                    "Database.executeData()",
                    e
            );
            throw new DatabaseInitializationException(errorMsg, e);
        }
    }


    /**
     * Logs database-related errors to the application's logger and creates an {@link EventLog} entry.
     * This method centralizes error logging during critical database initialization steps.
     */
    private static void logDatabaseError(User user, ErrorCode errorCode, String description, String context, Throwable cause) {
        if (cause != null) {
            logger.error("[{}]: {}", context, description, cause);
        } else {
            logger.error("[{}]: {}", context, description);
        }
        EventLog eventLog = new EventLog(
                user,
                LocalDateTime.now(),
                EventType.ERROR,
                "DatabaseInitialization",
                null,
                errorCode,
                description
        );
        logger.debug("EventLog entry created for DB error: {}", eventLog);
    }
}