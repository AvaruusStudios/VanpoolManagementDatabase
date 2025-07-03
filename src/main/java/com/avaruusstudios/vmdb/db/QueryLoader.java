package com.avaruusstudios.vmdb.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to load SQL queries by reading the content of individual .sql files
 * from the classpath resources. This class specifically serves the database layer
 * by providing externalized SQL query strings.
 */
public class QueryLoader {

    private static final Logger logger = LoggerFactory.getLogger(QueryLoader.class);
    // Base path for SQL files
    private static final String SQL_BASE_PATH = "/com/avaruusstudios/vmdb/db/";

    /**
     * Retrieves an SQL query by reading the content of a .sql file from the classpath.
     * The file is expected to be in the `SQL_BASE_PATH`.
     *
     * @param fileName The name of the SQL file (e.g., "SelectParticipantsByActive.sql").
     * @return The content of the SQL file as a single string.
     * @throws IllegalArgumentException if the file is not found or cannot be read, or its content is empty.
     */
    public static String getQuery(String fileName) {
        String fullPath = SQL_BASE_PATH + fileName;
        try (InputStream inputStream = QueryLoader.class.getResourceAsStream(fullPath)) {
            if (inputStream == null) {
                String errorMsg = "SQL query file not found: " + fullPath;
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String query = reader.lines().collect(Collectors.joining("\n")).trim();
                if (query.isEmpty()) {
                    String errorMsg = "SQL query file is empty: " + fullPath;
                    logger.error(errorMsg);
                    throw new IllegalArgumentException(errorMsg);
                }
                logger.debug("Loaded SQL query from: {}", fullPath);
                return query;
            }
        } catch (IOException ex) {
            String errorMsg = "Error reading SQL query file: " + fullPath + ": " + ex.getMessage();
            logger.error(errorMsg, ex);
            throw new IllegalArgumentException(errorMsg, ex);
        }
    }
}