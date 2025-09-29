package com.avaruusstudios.vmdb.service.impl;

import com.avaruusstudios.vmdb.dao.UserDataAccess;
import com.avaruusstudios.vmdb.model.User;
import com.avaruusstudios.vmdb.model.Role;
import com.avaruusstudios.vmdb.service.UserService;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * This class serves as the concrete implementation of the {@link UserService} interface,
 * defining the business logic and operations for managing {@link User} entities within the
 * Vanpool Management Database (VMDB) application. It acts as an intermediary,
 * enforcing business rules and coordinating with the underlying data access layer.
 * </p>
 *
 * <p>
 * This implementation adheres to the principle of a "thin" service layer, where
 * methods primarily delegate calls to the corresponding {@link UserDataAccess} methods,
 * but only after ensuring business rules are met. This design promotes a clear
 * separation of concerns, keeping business logic centralized in this layer and
 * database-specific operations confined to the DAO layer.
 * </p>
 *
 * <p>
 * **Dependency Management**: This class is designed to receive its dependencies via
 * constructor injection. It is not managed by a framework like Spring, so its
 * dependencies, specifically an instance of {@link UserDataAccess}, must be
 * manually provided at object creation time.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-08-27
 * Updated On: 2025-08-29
 *
 * @see UserService
 * @see UserDataAccess
 * @see User
 */
public class UserServiceImpl implements UserService {

    private final UserDataAccess userDataAccess;

    /**
     * Constructs a new UserServiceImpl.
     * <p>
     * This constructor is crucial for the proper functioning of the service layer.
     * It manually injects the required {@link UserDataAccess} dependency,
     * a design pattern known as **Constructor Injection**. This guarantees that a
     * {@code UserServiceImpl} instance is always initialized with a valid
     * data access object, which is essential for all its operations.
     * </p>
     * @param userDataAccess The data access object for user operations.
     */
    public UserServiceImpl(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    /**
     * Creates a new {@link User} record in the database.
     * <p>
     * **Business Rules**:
     * <ul>
     * <li>The user object and its Windows username cannot be null.</li>
     * <li>The Windows username must be unique. The service will check for an existing
     * user with the same username before attempting to create the new record.</li>
     * </ul>
     * </p>
     * @param user The {@link User} object to create.
     * @return The newly created {@link User} object with its database-assigned ID, or {@code null}
     * if a user with the same Windows username already exists.
     */
    @Override
    public User create(User user) {
        if (user == null || user.getWindowsUsername() == null) {
            return null;
        }

        Optional<User> existingUser = userDataAccess.findUserByWindowsUsername(user.getWindowsUsername());
        if (existingUser.isPresent()) {
            return null;
        }
        return userDataAccess.create(user);
    }

    /**
     * Reads a single {@link User} record by its unique identifier.
     * <p>
     * This method retrieves the user and handles the {@link Optional} return type
     * from the data access layer by returning {@code null} if the user is not found,
     * as per the service interface contract.
     * </p>
     * @param id The unique integer ID of the user to retrieve.
     * @return The {@link User} object, or {@code null} if no user is found with the given ID.
     */
    @Override
    public User findUserById(Integer id) {
        Optional<User> userOptional = userDataAccess.find(id);
        return userOptional.orElse(null);
    }

    /**
     * Retrieves all active {@link User} records from the database.
     * <p>
     * This method is a direct pass-through to the data access layer to retrieve all
     * active user records without any additional business logic or filtering.
     * </p>
     * @return A {@link List} of all {@link User} objects.
     */
    @Override
    public List<User> findAllUsers() {
        return userDataAccess.findAll(); // FIX: Changed method name
    }

    /**
     * Updates an existing {@link User} record in the database.
     * <p>
     * **Business Rules**:
     * <ul>
     * <li>The user object must be non-null and must contain a valid ID.</li>
     * <li>The service will first check if the user exists before attempting to update.
     * This prevents unnecessary database operations and potential errors.</li>
     * <li>The Windows username must be unique. This check is performed before the
     * update to ensure no other existing user has the same username.</li>
     * </ul>
     * </p>
     * @param user The {@link User} object with updated information.
     * @return The updated {@link User} object, or {@code null} if the user does not exist or if
     * a duplicate Windows username is detected.
     */
    @Override
    public User update(User user) {
        if (user == null || user.getUserId() == null || user.getWindowsUsername() == null) {
            return null;
        }

        if (!userDataAccess.existsById(user.getUserId())) {
            return null;
        }

        Optional<User> existingUser = userDataAccess.findUserByWindowsUsername(user.getWindowsUsername());
        if (existingUser.isPresent() && !existingUser.get().getUserId().equals(user.getUserId())) {
            return null;
        }

        return userDataAccess.update(user); // FIX: Now returns a User object
    }

    /**
     * Performs a logical (soft) deletion of a {@link User} record.
     * <p>
     * **Business Rule**: This method marks the user as inactive rather than
     * permanently removing them from the database. This preserves the record for
     * historical and auditing purposes. The corresponding `DeletedAt` timestamp
     * is also updated. The service will first check if the user exists before
     * attempting the soft-delete to prevent unnecessary database operations.
     * </p>
     * @param id The unique integer ID of the user to soft-delete.
     * @return {@code true} if the user was successfully soft-deleted; {@code false} otherwise.
     */
    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            return false;
        }

        if (!userDataAccess.existsById(id)) {
            return false;
        }
        return userDataAccess.delete(id); // FIX: Now returns a boolean
    }

    /**
     * Finds a user by their unique Windows username.
     * <p>
     * This is a specialized finder method that leverages a unique attribute.
     * It handles the {@link Optional} return type from the DAO by returning
     * {@code null} if no user is found.
     * </p>
     * @param windowsUsername The Windows username to search for.
     * @return The {@link User} object if found, or {@code null} if not.
     */
    @Override
    public User findUserByWindowsUsername(String windowsUsername) {
        Optional<User> userOptional = userDataAccess.findUserByWindowsUsername(windowsUsername);
        return userOptional.orElse(null);
    }

    /**
     * Finds a list of users by their assigned role.
     * <p>
     * This method is a direct pass-through to the data access layer to retrieve
     * a list of users based on a specific role filter.
     * </p>
     * @param role The {@link Role} to filter by.
     * @return A {@link List} of users with the specified role.
     */
    @Override
    public List<User> findUsersByRole(Role role) {
        return userDataAccess.findUsersByRole(role);
    }

    /**
     * Counts the total number of user records in the database.
     * <p>
     * This is a direct pass-through method to the data access layer, providing a
     * quick way to get a total count without retrieving all records.
     * </p>
     * @return The total count of users.
     */
    @Override
    public long countAll() {
        return userDataAccess.count(); // FIX: Changed method name
    }

    /**
     * Checks if a user with the given ID exists in the database.
     * <p>
     * This is a utility method that is more efficient than retrieving the entire
     * record and checking for null. It's often used for pre-validation before
     * performing other operations.
     * </p>
     * @param id The ID to check.
     * @return {@code true} if the user exists, {@code false} otherwise.
     */
    @Override
    public boolean existsById(Integer id) {
        return userDataAccess.existsById(id);
    }
}