package com.avaruusstudios.vmdb.dao;

import com.avaruusstudios.vmdb.model.Vehicle;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the
 * {@link Vehicle} entity within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It aggregates the standard CRUD operations for {@link Vehicle} by extending
 * {@link ReadDataAccess}, {@link CreateDataAccess}, {@link UpdateDataAccess},
 * and {@link DeleteDataAccess}. This interface also adds specialized methods
 * relevant to vehicle data retrieval and management, such as finding by unique
 * vehicle number, retrieving the single active vehicle, or filtering by make, model, or year.
 * </p>
 *
 * <p>
 * **Business Rule**: There should ONLY ever be one (1) active vehicle assigned to a vanpool at any given time.
 * The application does not manage or "issue out" a fleet of vehicles.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.5 // Version updated for interface refactoring
 * Created On: 2025-07-14 // Assuming original creation date of the file
 * Updated On: 2025-07-20 // Current date of modification
 * @since 2025-07-05
 *
 * @see ReadDataAccess
 * @see CreateDataAccess
 * @see UpdateDataAccess
 * @see DeleteDataAccess
 * @see Vehicle
 * @see DatabaseAccessException
 */
public interface VehicleDataAccess extends ReadDataAccess<Vehicle, Integer>,
        CreateDataAccess<Vehicle>,
        UpdateDataAccess<Vehicle>,
        DeleteDataAccess<Integer> {
    /**
     * <p>
     * Finds a {@link Vehicle} record by its unique vehicle number.
     * </p>
     *
     * @param vehicleNumber The unique identifier number for the vehicle to find.
     * @return An {@link Optional} containing the {@link Vehicle} if found, or an
     * empty {@link Optional} if no vehicle exists with the given vehicle number.
     * @throws DatabaseAccessException If a database access error occurs during the lookup.
     */
    Optional<Vehicle> findByNumber(String vehicleNumber) throws DatabaseAccessException;
    /**
     * <p>
     * Finds a {@link Vehicle} record by its unique License Plate
     * </p>
     *
     * @param licensePlate The unique license plate for the vehicle
     * @return An {@link Optional} containing the {@link Vehicle} if found of an
     * empty {@link Optional} if no vehicle exists with the given license plate
     * @throws DatabaseAccessException if a database access error occurs during the lookup
     */
    Optional<Vehicle> findByPlate(String licensePlate) throws DatabaseAccessException;
    /**
     * <p>
     * Retrieves the single active {@link Vehicle} record from the database.
     * Based on business rules, there should be at most one active vehicle assigned to a vanpool.
     * </p>
     *
     * @return An {@link Optional} containing the active {@link Vehicle} object if one exists,
     * or an empty {@link Optional} if no active vehicle is found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    Optional<Vehicle> findActive() throws DatabaseAccessException;
    /**
     * <p>
     * Retrieves a list of {@link Vehicle} records that match the specified make.
     * </p>
     *
     * @param make The make of the vehicle (e.g., "Toyota").
     * @return A {@link List} of {@link Vehicle} objects matching the specified make.
     * Returns an empty list if no matching vehicles are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Vehicle> findByMake(String make) throws DatabaseAccessException;
    /**
     * <p>
     * Retrieves a list of {@link Vehicle} records that match the specified model.
     * </p>
     *
     * @param model The model of the vehicle (e.g., "Sienna").
     * @return A {@link List} of {@link Vehicle} objects matching the specified model.
     * Returns an empty list if no matching vehicles are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Vehicle> findByModel(String model) throws DatabaseAccessException;
    /**
     * <p>
     * Retrieves a list of {@link Vehicle} records that match the specified year.
     * </p>
     *
     * @param year The year of the vehicle (e.g., 2020).
     * @return A {@link List} of {@link Vehicle} objects matching the specified year.
     * Returns an empty list if no matching vehicles are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Vehicle> findByYear(int year) throws DatabaseAccessException;
}