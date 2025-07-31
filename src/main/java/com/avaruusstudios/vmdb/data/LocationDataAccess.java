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
 * It aggregates the standard CRUD operations for {@link Location} by extending
 * {@link ReadDataAccess}, {@link CreateDataAccess}, {@link UpdateDataAccess},
 * and {@link DeleteDataAccess}. This interface also adds specialized methods
 * relevant to location data retrieval and management, such as finding locations
 * by address components or by unique name.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.2 // Version updated for interface refactoring
 * Created On: 2025-07-14 // Assuming original creation date of the file
 * Updated On: 2025-07-20 // Current date of modification
 * @since 2025-07-06 // Original creation date
 *
 * @see ReadDataAccess
 * @see CreateDataAccess
 * @see UpdateDataAccess
 * @see DeleteDataAccess
 * @see Location
 * @see DatabaseAccessException
 */
public interface LocationDataAccess extends ReadDataAccess<Location, Integer>,
        CreateDataAccess<Location>,
        UpdateDataAccess<Location>,
        DeleteDataAccess<Integer> {
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
    Optional<Location> findByName(String locationName) throws DatabaseAccessException;
}