package com.avaruusstudios.vmdb.data;

/**
 * <p>
 * This interface defines the contract for Data Access Objects (DAOs) that support
 * **update operations** in addition to creation and read operations on entities
 * within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It extends {@link CreateDataAccess} to inherit methods for creating, reading,
 * and counting entities, and adds a method specifically for modifying existing entities.
 * This interface is suitable for entities that are created, read, and can have
 * their persistent state altered, but are not intended for deletion via this DAO.
 * </p>
 *
 * <p>
 * Implementations are responsible for handling the modification of existing records
 * in the underlying data storage while maintaining data integrity.
 * </p>
 *
 * @param <T> The type of the entity (e.g., {@code com.avaruusstudios.vmdb.model.Participant}).
 * This represents the domain model object that the DAO will update and interact with.
 * @param <K> The type of the primary key for the entity {@code T}. Typically {@code java.lang.Integer}.
 *
 * @author [Your Name/AvaruusStudios]
 * @version 1.0 // Initial version for this new interface
 * @since 2025-07-11
 * @see CreateDataAccess
 */
public interface UpdateDataAccess<T, K> extends CreateDataAccess<T, K> {

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
     * @throws DatabaseAccessException if a database access error occurs during the update process,
     * if no record with the given primary key is found to update,
     * or if the update operation otherwise fails (e.g., due to constraints).
     * The underlying {@code SQLException} will be wrapped.
     */
    void updateRecord(T entity) throws DatabaseAccessException;
}