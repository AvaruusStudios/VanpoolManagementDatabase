package com.avaruusstudios.vmdb.data;

import com.avaruusstudios.vmdb.model.Vehicle;
import java.util.Optional;

/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the
 * {@link Vehicle} entity within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It extends {@link GenericDAO} to inherit standard CRUD operations for Vehicle,
 * and adds specialized methods relevant to vehicle data retrieval and management.
 * </p>
 *
 * <p>
 * **Business Rule**: There should ONLY ever be one (1) active vehicle assigned to a vanpool at any given time.
 * The application does not manage or "issue out" a fleet of vehicles.
 * </p>
 *
 * @author [Your Name/AvaruusStudios]
 * @version 1.2 // Updated version due to method removal based on business rule clarification
 * @since 2025-07-05
 * @see GenericDAO
 * @see Vehicle
 * @see DatabaseAccessObjectException
 */
public interface VehicleDAO extends GenericDAO<Vehicle, Integer> {

    /**
     * <p>
     * Finds a {@link Vehicle} record by its unique vehicle number.
     * </p>
     *
     * @param vehicleNumber The unique identifier number for the vehicle to find.
     * @return An {@link Optional} containing the {@link Vehicle} if found, or an
     * empty {@link Optional} if no vehicle exists with the given vehicle number.
     * @throws DatabaseAccessObjectException If a database access error occurs during the lookup.
     */
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber) throws DatabaseAccessObjectException;

    /**
     * <p>
     * Retrieves the single active {@link Vehicle} record from the database.
     * Based on business rules, there should be at most one active vehicle assigned to a vanpool.
     * </p>
     *
     * @return An {@link Optional} containing the active {@link Vehicle} object if one exists,
     * or an empty {@link Optional} if no active vehicle is found.
     * @throws DatabaseAccessObjectException If a database access error occurs during retrieval.
     */
    Optional<Vehicle> findActiveVehicle() throws DatabaseAccessObjectException;
}