package com.avaruusstudios.vmdb.service;

import com.avaruusstudios.vmdb.model.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * Defines the business rules and operations for managing {@link Vehicle} entities
 * within the Vanpool Management System. This service acts as an intermediary layer,
 * enforcing business logic and coordinating data access for vehicles.
 * </p>
 *
 * <p>
 * The primary responsibilities of this service include validating vehicle data,
 * managing the fleet (e.g., ensuring only one vehicle is active at a time),
 * and handling all CRUD (Create, Read, Update, Delete) operations in a business-rule
 * compliant manner.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-20
 * Updated On: 2025-07-20
 * @see com.avaruusstudios.vmdb.dao.VehicleDataAccess
 * @see Vehicle
 */
public interface VehicleService {

    /**
     * Creates a new {@link Vehicle} record in the database.
     * <p>
     * **Business Rules**:
     * <ul>
     * <li>Vehicles must have a unique license plate and vehicle number.</li>
     * <li>`passengerCapacity` and `discount` must be non-negative values.</li>
     * </ul>
     * </p>
     *
     * @param vehicle The {@link Vehicle} object to create.
     * @return The newly created {@link Vehicle} object with its database-assigned ID.
     */
    Vehicle create(Vehicle vehicle);

    /**
     * Retrieves a single {@link Vehicle} record by its unique identifier.
     *
     * @param id The unique integer ID of the vehicle to retrieve.
     * @return The {@link Vehicle} object, or {@code null} if no vehicle is found with the given ID.
     */
    Vehicle read(Integer id);

    /**
     * Retrieves all {@link Vehicle} records from the database.
     *
     * @return A {@link List} of all {@link Vehicle} objects.
     */
    List<Vehicle> findAllVehicles();

    /**
     * Updates an existing {@link Vehicle} record in the database.
     * <p>
     * **Business Rules**:
     * <ul>
     * <li>Vehicles must have a unique license plate and vehicle number.</li>
     * <li>`passengerCapacity` and `discount` must be non-negative values.</li>
     * </ul>
     * </p>
     *
     * @param vehicle The {@link Vehicle} object with updated information.
     * @return The updated {@link Vehicle} object.
     */
    Vehicle update(Vehicle vehicle);

    /**
     * Performs a logical (soft) deletion of a {@link Vehicle} record.
     * <p>
     * **Business Rule**: This is the final step in a vehicle's lifecycle.
     * This method must ensure the vehicle is being decommissioned and that
     * all data is recorded correctly.
     * </p>
     *
     * @param id The unique integer ID of the vehicle to soft-delete.
     * @return {@code true} if the vehicle was successfully soft-deleted; {@code false} otherwise.
     */
    boolean delete(Integer id);

    /**
     * Finds the currently active {@link Vehicle} in the system.
     * <p>
     * **Business Rule**: Only one vehicle can be active at a time.
     * </p>
     * @return The active {@link Vehicle}, or {@code null} if no vehicle is active.
     */
    Vehicle findActiveVehicle();

    /**
     * Orchestrates the process of replacing the current active vehicle with a new one.
     * This method correctly handles the lifecycle of both the old and new vehicle.
     * <p>
     * **Business Rules**:
     * <ul>
     * <li>Logs the replacement event to the EventLog.</li>
     * <li>The old vehicle's `leaseEndDate` must be a date that occurs after its `leaseStartDate`.</li>
     * <li>The old vehicle is soft-deleted by setting its `isActive` flag to `false`.</li>
     * <li>The new vehicle's `isActive` flag is set to `true`.</li>
     * </ul>
     * </p>
     *
     * @param oldVehicleId The unique ID of the vehicle to be replaced.
     * @param newVehicleId The unique ID of the vehicle to be made active.
     * @param leaseEndDate The `LocalDate` for when the lease of the old vehicle ended.
     * @return The newly activated {@link Vehicle} object.
     */
    Vehicle replaceActiveVehicle(Integer oldVehicleId, Integer newVehicleId, LocalDate leaseEndDate);
}