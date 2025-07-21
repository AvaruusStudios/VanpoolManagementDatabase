package com.avaruusstudios.vmdb.data;

import com.avaruusstudios.vmdb.model.User;
import com.avaruusstudios.vmdb.model.Role;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the {@link User} entity
 * within the Vanpool Management Database (VMDB) application. It extends {@link DeleteDataAccess},
 * thereby inheriting fundamental Create, Read, Update, and Delete (CRUD) capabilities
 * for {@code User} objects, identified by an {@code Integer} primary key.
 * </p>
 *
 * <p>
 * In addition to the generic operations, this interface provides specialized methods tailored
 * for common user retrieval patterns unique to the {@code User} entity. These include
 * finding a user by their unique Windows username and retrieving a list of users based on
 * their assigned {@link Role}. Implementations of this interface are responsible for
 * handling the persistence and retrieval of {@code User} data from the underlying database,
 * translating between database structures and application's {@code User} model objects.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0 // Initial version for this interface
 * Created On: 2025-07-14 // Current Date
 * Updated On: 2025-07-14 // Current Date
 *
 * @see User
 * @see DeleteDataAccess
 * @see Role
 */
public interface UserDataAccess extends DeleteDataAccess<User, Integer> {

    /**
     * Retrieves a single {@link User} entity from the database based on their Windows username.
     * This method is useful given that {@code WindowsUsername} is a unique identifier in the schema.
     *
     * @param windowsUsername The unique Windows username of the user to retrieve.
     * @return An {@code Optional<User>} containing the user if found, or an empty {@code Optional} if not.
     * @throws DatabaseAccessException if a database access error occurs during the retrieval process.
     */
    Optional<User> getUserByWindowsUsername(String windowsUsername) throws DatabaseAccessException;

    /**
     * Retrieves a list of {@link User} entities from the database that belong to a specific role.
     *
     * @param role The {@link Role} enum value representing the role to filter users by.
     * @return A {@code List<User>} containing all users found for the specified role.
     * Returns an empty list if no users are found for the given role.
     * @throws DatabaseAccessException if a database access error occurs during the retrieval process.
     */
    List<User> getUsersByRole(Role role) throws DatabaseAccessException;

}