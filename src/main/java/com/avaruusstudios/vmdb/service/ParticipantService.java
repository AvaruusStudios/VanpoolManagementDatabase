package com.avaruusstudios.vmdb.service;

import com.avaruusstudios.vmdb.model.Participant;

import java.util.List;

/**
 * <p>
 * Defines the business rules and operations for managing {@link Participant} entities
 * within the Vanpool Management System. This service is responsible for handling all
 * participant-related business logic, including registration, category assignments,
 * and maintaining participant status within vanpool programs.
 * </p>
 *
 * <p>
 * The primary responsibilities of this service include the management of participant
 * lifecycle from registration through deactivation, as well as the critical task of
 * maintaining relationships between participants and their assigned
 * {@link com.avaruusstudios.vmdb.model.Category}s for proper billing and access control.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-08-16
 * Updated On: 2025-08-16
 *
 * @see com.avaruusstudios.vmdb.dao.ParticipantDataAccess
 * @see Participant
 */
public interface ParticipantService {

    /**
     * Creates a new {@link Participant} record in the database.
     * <p>
     * **Business Rules**:
     * <ul>
     * <li>Email addresses must be unique across all participants in the system.</li>
     * <li>The service is responsible for validating participant data, including
     * required fields and email format validation.</li>
     * <li>New participants are automatically set to active status upon creation.</li>
     * </ul>
     * </p>
     *
     * @param participant The {@link Participant} object to create.
     * @return The newly created {@link Participant} object with its database-assigned ID.
     */
    Participant create(Participant participant);

    /**
     * Retrieves a single {@link Participant} record by its unique identifier.
     *
     * @param id The unique integer ID of the participant to retrieve.
     * @return The {@link Participant} object, or {@code null} if no participant is found with the given ID.
     */
    Participant find(Integer id);

    /**
     * Retrieves all {@link Participant} records from the database.
     *
     * @return A {@link List} of all {@link Participant} objects.
     */
    List<Participant> findAllParticipants();

    /**
     * Retrieves all active {@link Participant} records from the database.
     *
     * @return A {@link List} of all active {@link Participant} objects.
     */
    List<Participant> findAllActiveParticipants();

    /**
     * Retrieves a single {@link Participant} record by their unique email address.
     *
     * @param email The email address of the participant to retrieve.
     * @return The {@link Participant} object, or {@code null} if no participant is found with the given email.
     */
    Participant findByEmail(String email);

    /**
     * Retrieves all {@link Participant} records associated with a specific program.
     *
     * @param program The program enumeration to filter by.
     * @return A {@link List} of {@link Participant} objects in the specified program.
     */
    List<Participant> findByProgram(com.avaruusstudios.vmdb.model.Program program);

    /**
     * Retrieves all {@link Participant} records with a specific role.
     *
     * @param role The role enumeration to filter by.
     * @return A {@link List} of {@link Participant} objects with the specified role.
     */
    List<Participant> findByRole(com.avaruusstudios.vmdb.model.Role role);

    /**
     * Retrieves the designated coordinator {@link Participant}.
     * <p>
     * **Business Rule**: There should typically be only one coordinator per vanpool.
     * This method finds participants with {@link com.avaruusstudios.vmdb.model.Role#COORDINATOR} and returns the
     * first one found, or {@code null} if no coordinator exists.
     * </p>
     *
     * @return The coordinator {@link Participant} object, or {@code null} if no coordinator is found.
     */
    Participant findCoordinator();

    /**
     * Gets the total count of active participants in the system.
     *
     * @return The number of active participants.
     */
    long countActiveParticipants();

    /**
     * Updates an existing {@link Participant} record.
     * <p>
     * **Business Rules**:
     * <ul>
     * <li>Email uniqueness must be maintained during updates.</li>
     * <li>Core identification fields cannot be modified once set.</li>
     * <li>Status changes should be handled through dedicated activation/deactivation methods.</li>
     * </ul>
     * </p>
     *
     * @param participant The {@link Participant} object with updated information.
     * @return The updated {@link Participant} object.
     */
    Participant update(Participant participant);

    /**
     * Performs a logical (soft) deletion of a {@link Participant} record.
     * <p>
     * **Business Rule**: Participants are never hard-deleted to maintain data integrity
     * and historical records. This method performs a soft-delete by setting the
     * `isActive` flag to false.
     * </p>
     *
     * @param id The unique integer ID of the participant to soft-delete.
     * @return {@code true} if the participant was successfully soft-deleted; {@code false} otherwise.
     */
    boolean delete(Integer id);
}