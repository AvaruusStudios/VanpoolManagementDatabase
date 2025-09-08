package com.avaruusstudios.vmdb.dao;

/**
 * <p>
 * This interface defines the contract for Data Access Objects (DAOs) that provide
 * **update operations** on entities within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * This interface specifically adds a method for modifying existing entities. It focuses
 * solely on the update aspect, allowing for flexible composition with other data access
 * capabilities (e.g., read, create, delete).
 * </p>
 *
 * <p>
 * Implementations are responsible for handling the modification of existing records
 * in the underlying data storage while maintaining data integrity.
 * </p>
 *
 * @param <T> The type of the entity (e.g., {@code com.avaruusstudios.vmdb.model.Participant}).
 * This represents the domain model object that the DAO will update and interact with.
 *
 * @author AvaruusStudios
 * @version 1.1 // Incremented due to a public contract change
 * Created On: 2025-07-14 // Assuming original creation date for the content of this file
 * Updated On: 2025-08-29 // Current date of modification; return type changed from void to T
 */
public interface UpdateDataAccess<T> {

    /**
     * <p>
     * Updates an existing record of the entity in the database.
     * This method is used to modify the persistent state of an entity.
     * </p>
     *
     * <p>
     * For the update operation to succeed, the {@code entity} object passed as a parameter
     * *must* have its primary key field set. This ID is used to identify which existing
     * record in the database should be updated. All other fields in the {@code entity}
     * object will be used to update the corresponding columns in the database.
     * </p>
     *
     * @param entity The entity object (instance of type {@code T}) with its primary key
     * set and updated values for other fields.
     * @return The updated entity object after the operation is complete.
     * @throws DatabaseAccessException if a database access error occurs during the update process,
     * if no record with the given primary key is found to update,
     * or if the update operation otherwise fails (e.g., due to constraints).
     * The underlying {@code SQLException} will be wrapped.
     */
    T update(T entity) throws DatabaseAccessException;
}