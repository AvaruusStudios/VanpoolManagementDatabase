package com.avaruusstudios.vmdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages data manipulation operations on the database tables.
 * This class uses the Database class to obtain connections and QueryLoader to get SQL queries.
 */
public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    /**
     * Retrieves the count of active participants and the capacity of the active vehicle.
     * Assumes there is only one active vehicle at any given time.
     *
     * @return A String in the format "activeParticipants/activeVehicleCapacity".
     * @throws SQLException If a database access error occurs.
     * @throws IllegalArgumentException If an SQL query cannot be loaded.
     */
    public static String getRosterCount() throws SQLException, IllegalArgumentException {
        int activeParticipants = 0;
        int maxVehicleCapacity = 0;

        // Use Database.getConnection() to get a connection
        try (Connection conn = Database.getConnection()) { // Reverted call to getConnection()
            // Get Active Participants count
            String activeParticipantsSql = QueryLoader.getQuery("SelectParticipantsByActive.sql");
            try (PreparedStatement pstmt = conn.prepareStatement(activeParticipantsSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    activeParticipants = rs.getInt(1);
                }
            }

            // Get Capacity of the SINGLE Active Vehicle
            String capacitySql = QueryLoader.getQuery("SelectVehicleByActive.sql");
            try (PreparedStatement pstmt = conn.prepareStatement(capacitySql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    maxVehicleCapacity = rs.getInt("Capacity");
                } else {
                    logger.warn("No active vehicle found. Roster capacity might be inaccurate.");
                    maxVehicleCapacity = 0; // Default or error value
                }
            }
        }
        return activeParticipants + " / " + maxVehicleCapacity;
    }

    // You would add more methods here for other data operations, e.g.:
    // public static List<Participant> getAllActiveParticipants() throws SQLException { ... }
    // public static void addParticipant(Participant participant) throws SQLException { ... }
    // public static void updateVehicle(Vehicle vehicle) throws SQLException { ... }
}