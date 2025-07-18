package com.avaruusstudios.vmdb.implementation.sqlite; // Corrected package

import com.avaruusstudios.vmdb.db.DatabaseManager;
import com.avaruusstudios.vmdb.db.QueryLoader;
import com.avaruusstudios.vmdb.model.User;
import com.avaruusstudios.vmdb.model.Role;
import com.avaruusstudios.vmdb.data.UserDataAccess;         // Added import for UserDataAccess
import com.avaruusstudios.vmdb.data.GenericDataAccess;     // Added import for GenericDataAccess
import com.avaruusstudios.vmdb.data.UpdateDataAccess;     // Added import for UpdateDataAccess
import com.avaruusstudios.vmdb.data.CreateDataAccess;     // Added import for CreateDataAccess
import com.avaruusstudios.vmdb.data.ReadDataAccess;       // Added import for ReadDataAccess
import com.avaruusstudios.vmdb.data.DatabaseAccessException; // Added import for DatabaseAccessException

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * Concrete implementation of the {@link com.avaruusstudios.vmdb.data.UserDataAccess} interface for managing
 * {@link com.avaruusstudios.vmdb.model.User} data in an SQLite database. This class fully implements all generic
 * CRUD and read operations inherited via {@link com.avaruusstudios.vmdb.data.GenericDataAccess}, along with
 * specific queries for {@code User} entities.
 * </p>
 *
 * <p>
 * It uses {@link com.avaruusstudios.vmdb.db.DatabaseManager} to obtain database connections and {@link com.avaruusstudios.vmdb.db.QueryLoader}
 * to externalize SQL queries from `.sql` files. Any {@link java.sql.SQLException}s are
 * wrapped into {@link com.avaruusstudios.vmdb.data.DatabaseAccessException} for consistent error handling
 * across the DAO layer. {@code LocalDateTime} objects are persisted as `TEXT`
 * using a custom format ("dd-MMM-yyyy HH:mm"), and `boolean` values as `INTEGER` (1 for true, 0 for false).
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-14
 * Updated On: 2025-07-14
 *
 * @see com.avaruusstudios.vmdb.data.UserDataAccess
 * @see com.avaruusstudios.vmdb.model.User
 * @see com.avaruusstudios.vmdb.db.DatabaseManager
 * @see com.avaruusstudios.vmdb.data.DatabaseAccessException
 * @see com.avaruusstudios.vmdb.db.QueryLoader
 * @see com.avaruusstudios.vmdb.data.GenericDataAccess
 * @see com.avaruusstudios.vmdb.data.UpdateDataAccess
 * @see com.avaruusstudios.vmdb.data.CreateDataAccess
 * @see com.avaruusstudios.vmdb.data.ReadDataAccess
 */
public class UserImplementation implements UserDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(UserImplementation.class);

    // Date/Time formatter for converting between LocalDateTime and TEXT in SQLite
    // Using Locale.ENGLISH for consistent month abbreviations (e.g., "Jan", "Feb")
    private static final DateTimeFormatter CUSTOM_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.ENGLISH);

    // SQL Queries (loaded from external .sql files with lowercase first word + CamelCase filenames)
    private static final String SQL_CREATE_USER_RECORD;
    private static final String SQL_READ_USER_RECORD;
    private static final String SQL_READ_ALL_USER_RECORDS;
    private static final String SQL_COUNT_USER_RECORDS;
    private static final String SQL_EXISTS_USER_BY_ID;
    private static final String SQL_UPDATE_USER_RECORD;
    private static final String SQL_DELETE_USER_SOFT;
    private static final String SQL_SELECT_USER_BY_WINDOWS_USERNAME;
    private static final String SQL_SELECT_USERS_BY_ROLE;

    static {
        try {
            // Updated SQL filenames to match 'lowercaseFirstWordCamelCase.sql' convention
            // Assumed mapping: createRecord -> insertUser.sql, readRecord -> selectUserById.sql etc.
            SQL_CREATE_USER_RECORD = QueryLoader.getQuery("user/insertUser.sql");
            SQL_READ_USER_RECORD = QueryLoader.getQuery("user/selectUserById.sql");
            SQL_READ_ALL_USER_RECORDS = QueryLoader.getQuery("user/selectAllUsers.sql");
            SQL_COUNT_USER_RECORDS = QueryLoader.getQuery("user/countUsers.sql");
            SQL_EXISTS_USER_BY_ID = QueryLoader.getQuery("user/existsUserById.sql"); // New SQL file needed
            SQL_UPDATE_USER_RECORD = QueryLoader.getQuery("user/updateUser.sql");
            SQL_DELETE_USER_SOFT = QueryLoader.getQuery("user/deleteUserSoft.sql");
            SQL_SELECT_USER_BY_WINDOWS_USERNAME = QueryLoader.getQuery("user/selectUserByWindowsUsername.sql");
            SQL_SELECT_USERS_BY_ROLE = QueryLoader.getQuery("user/selectUsersByRole.sql");
            logger.info("All SQL queries for UserImplementation loaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load one or more SQL queries for UserImplementation. Check .sql files and paths.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Maps a {@link ResultSet} row to a {@link User} object.
     * This method handles the conversion of database column values to the appropriate
     * Java types for the User POJO.
     *
     * @param rs The {@link ResultSet} from which to read user data.
     * @return A fully populated {@link User} object.
     * @throws SQLException If a database access error occurs or column not found.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        Integer userId = rs.getInt("UserID");
        User user = new User();
        user._setUserId(userId);

        user.setWindowsUsername(rs.getString("WindowsUsername"));
        user.setFirstName(rs.getString("FirstName"));
        user.setMiddleName(rs.getString("MiddleName"));
        user.setLastName(rs.getString("LastName"));
        user.setEmail(rs.getString("Email"));
        user.setRole(Role.fromDbValue(rs.getString("UserRole")));
        user.setActive(rs.getInt("IsActive") == 1);

        String deletedAtStr = rs.getString("DeletedAt");
        user.setDeletedAt(deletedAtStr != null && !deletedAtStr.isEmpty() ? LocalDateTime.parse(deletedAtStr, CUSTOM_DATETIME_FORMATTER) : null);

        String dateCreatedStr = rs.getString("DateCreated");
        user.setDateCreated(dateCreatedStr != null && !dateCreatedStr.isEmpty() ? LocalDateTime.parse(dateCreatedStr, CUSTOM_DATETIME_FORMATTER) : null);

        return user;
    }

    @Override
    public User createRecord(User user) throws DatabaseAccessException {
        Objects.requireNonNull(user, "User object cannot be null for creation.");
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_CREATE_USER_RECORD, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getWindowsUsername());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getMiddleName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getRole().getDbValue());
            pstmt.setInt(7, user.isActive() ? 1 : 0);

            if (user.getDateCreated() != null) {
                pstmt.setString(8, user.getDateCreated().format(CUSTOM_DATETIME_FORMATTER));
            } else {
                pstmt.setNull(8, Types.VARCHAR);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseAccessException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user._setUserId(generatedKeys.getInt(1));
                    logger.info("User created successfully with ID: {}", user.getUserId());
                } else {
                    throw new DatabaseAccessException("Creating user failed, no ID obtained.");
                }
            }
            return user;

        } catch (SQLException e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            if (e.getErrorCode() == 19 && e.getMessage().contains("UNIQUE constraint failed: Users.WindowsUsername")) {
                throw new DatabaseAccessException("User with Windows username '" + user.getWindowsUsername() + "' already exists.", e);
            }
            throw new DatabaseAccessException("Failed to create user: " + e.getMessage(), e);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.error("User validation/state error during creation: {}", e.getMessage(), e);
            throw new DatabaseAccessException("User data is invalid or in an illegal state for creation: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> readRecord(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "User ID cannot be null for retrieval.");
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be positive for retrieval.");
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_READ_USER_RECORD)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user by ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Failed to retrieve user by ID " + id + ": " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> readRecordAll() throws DatabaseAccessException {
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_READ_ALL_USER_RECORDS)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all users: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Failed to retrieve all users: " + e.getMessage(), e);
        }
        return users;
    }

    @Override
    public long countRecord() throws DatabaseAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_COUNT_USER_RECORDS)) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting users: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Failed to count users: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean existsById(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "User ID cannot be null for existence check.");
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be positive for existence check.");
        }
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_EXISTS_USER_BY_ID)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                // If any row is returned, the record exists
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking existence of user by ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Failed to check existence of user by ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void updateRecord(User user) throws DatabaseAccessException {
        Objects.requireNonNull(user, "User object cannot be null for update.");
        Objects.requireNonNull(user.getUserId(), "User ID cannot be null for update.");
        if (user.getUserId() <= 0) {
            throw new IllegalArgumentException("User ID must be positive for update.");
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_UPDATE_USER_RECORD)) {

            pstmt.setString(1, user.getWindowsUsername());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getMiddleName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getRole().getDbValue());
            pstmt.setInt(7, user.isActive() ? 1 : 0);

            if (user.getDeletedAt() != null) {
                pstmt.setString(8, user.getDeletedAt().format(CUSTOM_DATETIME_FORMATTER));
            } else {
                pstmt.setNull(8, Types.VARCHAR);
            }

            pstmt.setInt(9, user.getUserId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Update user with ID {} affected 0 rows. User might not exist.", user.getUserId());
            } else {
                logger.info("User with ID {} updated successfully. Affected rows: {}", user.getUserId(), affectedRows);
            }

        } catch (SQLException e) {
            logger.error("Error updating user with ID {}: {}", user.getUserId(), e.getMessage(), e);
            if (e.getErrorCode() == 19 && e.getMessage().contains("UNIQUE constraint failed: Users.WindowsUsername")) {
                throw new DatabaseAccessException("Cannot update user: Windows username '" + user.getWindowsUsername() + "' is already in use by another user.", e);
            }
            throw new DatabaseAccessException("Failed to update user with ID " + user.getUserId() + ": " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error("User validation error during update: {}", e.getMessage(), e);
            throw new DatabaseAccessException("User data is invalid for update: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteRecord(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "User ID cannot be null for deletion.");
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be positive for deletion.");
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_DELETE_USER_SOFT)) {

            pstmt.setString(1, LocalDateTime.now().format(CUSTOM_DATETIME_FORMATTER));
            pstmt.setInt(2, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Soft-delete user with ID {} affected 0 rows. User might not exist or already deleted.", id);
            } else {
                logger.info("User with ID {} soft-deleted successfully (IsActive=0, DeletedAt={}).", id, LocalDateTime.now().format(CUSTOM_DATETIME_FORMATTER));
            }
        } catch (SQLException e) {
            logger.error("Error soft-deleting user with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Failed to soft-delete user with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> getUserByWindowsUsername(String windowsUsername) throws DatabaseAccessException {
        Objects.requireNonNull(windowsUsername, "Windows username cannot be null for retrieval.");
        String trimmedUsername = windowsUsername.trim();
        if (trimmedUsername.isEmpty()) {
            throw new IllegalArgumentException("Windows username cannot be empty for retrieval.");
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_SELECT_USER_BY_WINDOWS_USERNAME)) {

            pstmt.setString(1, trimmedUsername);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user by Windows username '{}': {}", trimmedUsername, e.getMessage(), e);
            throw new DatabaseAccessException("Failed to retrieve user by Windows username '" + trimmedUsername + "': " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> getUsersByRole(Role role) throws DatabaseAccessException {
        Objects.requireNonNull(role, "Role cannot be null for retrieval.");
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_SELECT_USERS_BY_ROLE)) {

            pstmt.setString(1, role.getDbValue());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving users by role '{}': {}", role.name(), e.getMessage(), e);
            throw new DatabaseAccessException("Failed to retrieve users by role '" + role.name() + "': " + e.getMessage(), e);
        }
        return users;
    }
}