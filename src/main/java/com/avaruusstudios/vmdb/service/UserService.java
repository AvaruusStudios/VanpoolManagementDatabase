package com.avaruusstudios.vmdb.service;

import com.avaruusstudios.vmdb.model.User;
import com.avaruusstudios.vmdb.model.Role;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Defines the business rules and operations for managing {@link User} entities
 * within the Vanpool Management System. This service acts as an intermediary layer,
 * enforcing business logic and coordinating data access for users.
 * </p>
 *
 * <p>
 * The primary responsibilities of this service include validating user data,
 * managing user roles, and handling all CRUD (Create, Read, Update, Delete)
 * operations in a business-rule compliant manner.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-08-23
 * Updated On: 2025-08-23
 *
 * @see com.avaruusstudios.vmdb.dao.UserDataAccess
 * @see User
 * @see Role
 */
public interface UserService {

    /**
     * Creates a new {@link User} record in the database.
     *
     * @param user The {@link User} object to create.
     * @return The newly created {@link User} object with its database-assigned ID.
     */
    User create(User user);

    /**
     * Retrieves a single {@link User} record by its unique identifier.
     *
     * @param id The unique integer ID of the user to retrieve.
     * @return The {@link User} object, or {@code null} if no user is found with the given ID.
     */
    User findUserById(Integer id);

    /**
     * Retrieves all {@link User} records from the database.
     *
     * @return A {@link List} of all {@link User} objects.
     */
    List<User> findAllUsers();

    /**
     * Updates an existing {@link User} record in the database.
     *
     * @param user The {@link User} object with updated information.
     * @return The updated {@link User} object.
     */
    User update(User user);

    /**
     * Performs a logical (soft) deletion of a {@link User} record.
     *
     * @param id The unique integer ID of the user to soft-delete.
     * @return {@code true} if the user was successfully soft-deleted; {@code false} otherwise.
     */
    boolean delete(Integer id);

    /**
     * Finds a user by their unique Windows username.
     *
     * @param windowsUsername The Windows username to search for.
     * @return The {@link User} object if found, or {@code null} if not.
     */
    User findUserByWindowsUsername(String windowsUsername);

    /**
     * Finds a list of users by their assigned role.
     *
     * @param role The {@link Role} to filter by.
     * @return A {@link List} of users with the specified role.
     */
    List<User> findUsersByRole(Role role);

    /**
     * Counts the total number of user records.
     *
     * @return The total count of users.
     */
    long countAll();

    /**
     * Checks if a user with the given ID exists.
     *
     * @param id The ID to check.
     * @return {@code true} if the user exists, {@code false} otherwise.
     */
    boolean existsById(Integer id);
}