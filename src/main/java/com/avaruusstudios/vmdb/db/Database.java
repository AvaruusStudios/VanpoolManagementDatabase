package com.avaruusstudios.vmdb.db;

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
import java.util.stream.Collectors;

public class Database {
    private static final String DATABASE_FILE = "vanpool.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DATABASE_FILE;
    private static final String SCHEMA_FILEPATH = "/com/avaruusstudios/vmdb/db/schema.sql";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    private static boolean databaseExists() {
        return Files.exists(Paths.get(DATABASE_FILE));
    }

    public static void createDatabase() {
        if (!databaseExists()) {
            System.out.println("Database file not found. Creating " + DATABASE_FILE + "...");
            try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
                if (connection != null) {
                    System.out.println("Database created successfully.");
                    executeSchema(connection);
                }
            } catch (SQLException e) {
                System.err.println("Error creating database: " + e.getMessage());
            }
        } else {
            System.out.println("Database " + DATABASE_FILE + " already exists.");
        }
    }

    private static void executeSchema(Connection connection) {
        System.out.println("Executing database schema from " + SCHEMA_FILEPATH + "...");
        try (InputStream inputStream = Database.class.getResourceAsStream(SCHEMA_FILEPATH);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                System.err.println("Error: Could not find schema file at " + SCHEMA_FILEPATH);
                return;
            }

            String sqlStatements = reader.lines().collect(Collectors.joining("\n"));
            String[] individualStatements = sqlStatements.split(";\\s*\\n?"); // Split by semicolon and optional newline

            Statement statement = connection.createStatement();
            for (String sql : individualStatements) {
                if (!sql.trim().isEmpty()) {
                    statement.executeUpdate(sql);
                }
            }
            System.out.println("Database schema executed successfully.");

        } catch (IOException e) {
            System.err.println("Error reading schema file: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error executing schema: " + e.getMessage());
        }
    }
}
