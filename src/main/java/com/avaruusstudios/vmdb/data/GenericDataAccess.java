package com.avaruusstudios.vmdb.data;

/**
 * <p>
 * This interface defines the contract for a Generic Data Access Object (DAO) within the
 * Vanpool Management Database (VMDB) application. It provides a standardized set of
 * **fundamental Create, Read, Update, Delete (CRUD) operations**, along with a record
 * counting mechanism, for any given entity type.
 * </p>
 *
 * <p>
 * By extending {@link UpdateDataAccess}, this interface inherits methods for creating,
 * reading, counting, and updating entities. It then specifically adds the method for
 * deleting records by their primary key. This aggregate of methods represents the
 * complete lifecycle of data manipulation and serves as the standard interface for
 * entities that require all fundamental data modification operations.
 * </p>
 *
 * <p>
 * Implementations of this interface are responsible for interacting directly with the
 * underlying data storage (e.g., a relational database via JDBC) and translating
 * between the application's entity objects and the database's data structures,
 * ensuring data integrity throughout the process.
 * </p>
 *
 * @param <T> The type of the entity (e.g., {@code com.avaruusstudios.vmdb.model.Participant},
 * {@code com.avaruusstudios.vmdb.model.Vehicle},
 * {@code com.avaruusstudios.vmdb.model.Transaction}). This represents the
 * domain model object that the DAO will persist and retrieve.
 * @param <K> The type of the primary key for the entity {@code T}. Based on your project's
 * design, this is typically {@code java.lang.Integer} for all tables.
 * This type parameter ensures type safety when performing operations
 * that rely on the entity's unique identifier.
 *
 * @author [Your Name/AvaruusStudios]
 * @version 1.3 // Incremented version due to Javadoc clarification
 * @since 2025-07-11
 * @see UpdateDataAccess
 */
public interface GenericDataAccess<T, K> extends UpdateDataAccess<T, K> {

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
     * @throws DatabaseAccessException if a database access error occurs during the deletion process.
     * The underlying {@code SQLException} will be wrapped.
     */
    void deleteRecord(K id) throws DatabaseAccessException;
}