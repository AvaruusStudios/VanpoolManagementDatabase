package com.avaruusstudios.vmdb.data;

import com.avaruusstudios.vmdb.model.Location; // Assuming your Location POJO is here
import java.util.Optional;

/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the
 * {@link Location} entity within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It extends {@link GenericDAO} to inherit standard CRUD operations for Location,
 * and adds specialized methods relevant to location data retrieval and management.
 * </p>
 *
 * @author [Your Name/AvaruusStudios]
 * @version 1.1 // Updated version due to method removal
 * @since 2025-07-06
 *
 * @see GenericDAO
 * @see Location
 * @see DatabaseAccessObjectException
 */
public interface LocationDAO extends GenericDAO<Location, Integer> {
    /**
     * <p>
     * Finds a {@link Location} record by a unique combination of its address components.
     * This could be used for verifying if a specific address already exists in the system.
     * </p>
     *
     * @param address The street address (e.g., "123 Main St").
     * @param city The city (e.g., "Anytown").
     * @param state The state (e.g., "CA").
     * @param zipCode The zip code (e.g., "90210").
     *
     * @return An {@link Optional} containing the {@link Location} if a match is found,
     * or an empty {@link Optional} if no location exists with the given address details.
     *
     * @throws DatabaseAccessObjectException If a database access error occurs during the lookup.
     */
    Optional<Location> findByAddress(String address, String city, String state, String zipCode) throws DatabaseAccessObjectException;
}