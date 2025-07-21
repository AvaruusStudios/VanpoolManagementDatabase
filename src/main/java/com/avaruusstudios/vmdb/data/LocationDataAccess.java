package com.avaruusstudios.vmdb.data;

import com.avaruusstudios.vmdb.model.Location;
import java.util.Optional;


/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the
 * {@link Location} entity within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It extends {@link DeleteDataAccess} to inherit standard CRUD operations for Location,
 * and adds specialized methods relevant to location data retrieval and management.
 * </p>
 *
 * @author [Your Name/AvaruusStudios]
 * @version 1.2 // Updated version due to method addition based on schema review
 * @since 2025-07-06 // Original creation date
 *
 * @see DeleteDataAccess
 * @see Location
 * @see DatabaseAccessException
 */
public interface LocationDataAccess extends DeleteDataAccess<Location, Integer> {
    /**
     * <p>
     * Finds a {@link Location} record by a unique combination of its address components.
     * This could be used for verifying if a specific address already exists in the system.
     * </p>
     *
     * @param streetAddress The street address (e.g., "123 Main St").
     * @param city The city (e.g., "Anytown").
     * @param state The state (e.g., "CA").
     * @param zipCode The zip code (e.g., "90210").
     * @return An {@link Optional} containing the {@link Location} if a match is found,
     * or an empty {@link Optional} if no location exists with the given address details.
     * @throws DatabaseAccessException If a database access error occurs during the lookup.
     */
    Optional<Location> findByAddress(String streetAddress, String city, String state, String zipCode) throws DatabaseAccessException;
    /**
     * <p>
     * Finds a {@link Location} record by its unique name.
     * </p>
     *
     * @param locationName The name of the location to find (e.g., "Main Office", "South Parking Lot").
     * @return An {@link Optional} containing the {@link Location} if found, or an
     * empty {@link Optional} if no location exists with the given name.
     * @throws DatabaseAccessException If a database access error occurs during the lookup.
     */
    Optional<Location> findByLocationName(String locationName) throws DatabaseAccessException;
}