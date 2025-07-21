package com.avaruusstudios.vmdb.data;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * This interface defines the contract for Data Access Objects (DAOs) that provide
 * **read-only operations** on entities within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It establishes a standardized set of methods for retrieving single entities by their
 * primary key, fetching all entities of a specific type, counting the total number
 * of records, and checking for the existence of a record by its primary key. This interface
 * forms the most fundamental layer for interacting with persistent data, suitable for
 * entities where modifications are not permitted via this DAO.
 * </p>
 *
 * <p>
 * Implementations of this interface are responsible for querying the underlying data
 * storage (e.g., a relational database via JDBC) and translating between the application's
 * entity objects and the database's data structures.
 * </p>
 *
 * @param <T> The type of the entity (e.g., {@code com.avaruusstudios.vmdb.model.Participant}).
 * This represents the domain model object that the DAO will retrieve.
 * @param <K> The type of the primary key for the entity {@code T}. Based on your project's
 * design, this is typically {@code java.lang.Integer} for all tables.
 * This type parameter ensures type safety when performing operations
 * that rely on the entity's unique identifier.
 *
 * @author AvaruusStudios
 * @version 1.0 // This version refers to the state of this specific interface's public contract
 * Created On: 2025-07-14 // Assuming original creation date for the content of this file
 * Updated On: 2025-07-20 // Current date of modification
 */
public interface ReadDataAccess<T, K> {

    /**
     * <p>
     * Retrieves a single record of the entity from the database based on its primary key.
     * This method provides a way to fetch a specific entity instance.
     * </p>
     *
     * <p>
     * The result is wrapped in an {@code Optional<T>} to clearly indicate whether
     * an entity matching the given ID was found. This prevents {@code NullPointerExceptions}
     * and encourages explicit handling of cases where the record might not exist.
     * </p>
     *
     * @param id The primary key (of type {@code K}, typically {@code Integer}) of the
     * entity record to retrieve. This uniquely identifies the record in the database.
     * @return An {@code Optional<T>} containing the entity object if a record with the
     * specified ID is found in the database; otherwise, an empty {@code Optional}.
     * @throws DatabaseAccessException if a database access error occurs during the retrieval process.
     * The underlying {@code SQLException} will be wrapped.
     */
    Optional<T> readRecord(K id) throws DatabaseAccessException;

    /**
     * <p>
     * Retrieves all records of the entity from the database.
     * This method is used to fetch a complete list of all instances of a given entity type.
     * </p>
     *
     * <p>
     * The order of the returned records is dependent on the underlying database and
     * the SQL query used by the concrete implementation (e.g., if no specific
     * ORDER BY clause is applied, the order might not be guaranteed).
     * </p>
     *
     * @return A {@code List<T>} containing all entity objects found in the corresponding
     * database table. Returns an empty list if no records are present.
     * The list will not be {@code null}.
     * @throws DatabaseAccessException if a database access error occurs during the retrieval process.
     * The underlying {@code SQLException} will be wrapped.
     */
    List<T> readRecordAll() throws DatabaseAccessException;

    /**
     * <p>
     * Counts the total number of records for the specific entity type in the database.
     * This method provides a quick way to determine the size of the dataset.
     * </p>
     *
     * @return The total count of records (rows) present in the database table
     * corresponding to the entity type {@code T}. Returns {@code 0} if no
     * records are found.
     * @throws DatabaseAccessException if a database access error occurs during the counting process.
     * The underlying {@code SQLException} will be wrapped.
     */
    long countRecord() throws DatabaseAccessException;

    /**
     * <p>
     * Checks if a record of the entity with the specified primary key exists in the database.
     * This method is more efficient than {@link #readRecord(Object)} when only
     * existence needs to be verified, as it avoids fetching the entire entity object.
     * </p>
     *
     * @param id The primary key (of type {@code K}, typically {@code Integer}) of the
     * entity record to check for existence. This uniquely identifies the record.
     * @return {@code true} if a record with the specified ID exists in the database;
     * {@code false} otherwise.
     * @throws DatabaseAccessException if a database access error occurs during the check.
     * The underlying {@code SQLException} will be wrapped.
     * @since 2025-07-13 (or current date if this is being added now)
     */
    boolean existsById(K id) throws DatabaseAccessException;
}