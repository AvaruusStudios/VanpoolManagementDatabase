package com.avaruusstudios.vmdb.service;

import com.avaruusstudios.vmdb.model.Location;

import java.util.List;

/**
 * <p>
 * Defines the business rules and operations for managing {@link Location} entities
 * within the Vanpool Management System. This service acts as an intermediary layer,
 * enforcing business logic and coordinating data access for locations.
 * </p>
 *
 * <p>
 * The primary responsibilities of this service include validating location data,
 * managing the soft-deletion process (preventing deletion if a location is in use),
 * and handling all CRUD (Create, Read, Update, Delete) operations for locations
 * in a business-rule compliant manner.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-20
 * Updated On: 2025-07-20
 *
 * @see com.avaruusstudios.vmdb.dao.LocationDataAccess
 * @see Location
 */
public interface LocationService {

    /**
     * Creates a new {@link Location} record in the database.
     * <p>
     * **Business Rule**: This method is responsible for ensuring the location's
     * geographical coordinates (`latitude` and `longitude`) are within their
     * valid ranges before persistence.
     * </p>
     *
     * @param location The {@link Location} object to create.
     * @return The newly created {@link Location} object with its database-assigned ID.
     */
    Location create(Location location);

    /**
     * Retrieves a single {@link Location} record by its unique identifier.
     *
     * @param id The unique integer ID of the location to retrieve.
     * @return The {@link Location} object, or {@code null} if no location is found with the given ID.
     */
    Location read(Integer id);

    /**
     * Retrieves all {@link Location} records from the database.
     *
     * @return A {@link List} of all {@link Location} objects.
     */
    List<Location> findAllLocations();

    /**
     * Updates an existing {@link Location} record in the database.
     * <p>
     * **Business Rule**: This method ensures the updated location data, particularly
     * the geographical coordinates, remains valid.
     * </p>
     *
     * @param location The {@link Location} object with updated information.
     * @return The updated {@link Location} object.
     */
    Location update(Location location);

    /**
     * Performs a logical (soft) deletion of a {@link Location} record.
     * <p>
     * **Business Rule**: A location cannot be deleted if it is currently in use
     * as a home or work address by any active {@link com.avaruusstudios.vmdb.model.Participant}.
     * </p>
     *
     * @param id The unique integer ID of the location to soft-delete.
     * @return {@code true} if the location was successfully soft-deleted; {@code false} otherwise.
     */
    boolean delete(Integer id);
}