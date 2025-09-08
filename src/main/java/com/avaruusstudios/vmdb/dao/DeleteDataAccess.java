package com.avaruusstudios.vmdb.dao;

/**
 * <p>
 * This interface defines the contract for Data Access Objects (DAOs) that provide
 * **delete operations** on entities within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * This interface specifically adds a method for deleting records by their primary key.
 * It focuses solely on the deletion aspect, allowing for flexible composition with
 * other data access capabilities (e.g., read, create, update).
 * </p>
 *
 * <p>
 * For this application, implementations of this method are expected to perform a
 * **soft-delete** rather than physically removing the record from the database.
 * This typically involves updating an `IsActive` flag to `0` and/or setting a
 * `DeletedAt` timestamp to the current time. This strategy preserves data integrity
 * for historical, auditing, and referential purposes, and allows for potential recovery.
 * </p>
 *
 * <p>
 * If no record matches the provided ID, the operation will typically complete
 * without throwing an error, but no rows will be affected. Implementations
 * might log a warning or return a boolean indicating success if desired.
 * </p>
 *
 * @param <K> The type of the primary key for the entity. Based on your project's
 * design, this is typically {@code java.lang.Integer} for all tables.
 * This type parameter ensures type safety when performing operations
 * that rely on the entity's unique identifier for deletion.
 *
 * @author AvaruusStudios
 * @version 1.1 // Incremented due to a public contract change
 * Created On: 2025-07-14 // Assuming original creation date for the content of this file
 * Updated On: 2025-08-29 // Current date of modification; return type changed from void to boolean
 */
public interface DeleteDataAccess<K> {

    /**
     * <p>
     * Deletes a record of the entity from the database based on its primary key.
     * This method logically removes an entity instance from active use in the application.
     * </p>
     *
     * <p>
     * For this application, implementations of this method are expected to perform a
     * **soft-delete** rather than physically removing the record from the database.
     * This typically involves updating an `IsActive` flag to `0` and/or setting a
     * `DeletedAt` timestamp to the current time. This strategy preserves data integrity
     * for historical, auditing, and referential purposes, and allows for potential recovery.
     * </p>
     *
     * <p>
     * If no record matches the provided ID, the operation will typically complete
     * without throwing an error, but no rows will be affected. Implementations
     * might log a warning or return a boolean indicating success if desired.
     * </p>
     *
     * @param id The primary key (of type {@code K}, typically {@code Integer}) of the
     * entity record to delete. This uniquely identifies the record to be removed.
     * @return {@code true} if the record was successfully deleted (or soft-deleted); {@code false} otherwise.
     * @throws DatabaseAccessException if a database access error occurs during the deletion process.
     * The underlying {@code SQLException} will be wrapped.
     */
    boolean delete(K id) throws DatabaseAccessException;
}