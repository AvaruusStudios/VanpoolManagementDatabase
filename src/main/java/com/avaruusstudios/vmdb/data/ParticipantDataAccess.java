package com.avaruusstudios.vmdb.data;

import com.avaruusstudios.vmdb.model.Participant;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the
 * {@link Participant} entity within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It aggregates the standard CRUD operations for {@link Participant} by extending
 * {@link ReadDataAccess}, {@link CreateDataAccess}, {@link UpdateDataAccess},
 * and {@link DeleteDataAccess}. This interface also adds specialized methods
 * relevant to participant data retrieval and management, such as finding by email,
 * counting/retrieving active participants, or filtering by program/role.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.1 // Version updated for interface refactoring
 * Created On: 2025-07-14 // Assuming original creation date of the file
 * Updated On: 2025-07-20 // Current date of modification
 * @since 2025-07-06 // Original creation date
 *
 * @see ReadDataAccess
 * @see CreateDataAccess
 * @see UpdateDataAccess
 *
 * @see DeleteDataAccess
 * @see Participant
 * @see DatabaseAccessException
 */
public interface ParticipantDataAccess extends ReadDataAccess<Participant, Integer>,
        CreateDataAccess<Participant>,
        UpdateDataAccess<Participant>,
        DeleteDataAccess<Integer> {
    /**
     * <p>
     * Finds a {@link Participant} record by their unique email address.
     * </p>
     *
     * @param email The email address of the participant to find.
     * @return An {@link Optional} containing the {@link Participant} if found, or an
     * empty {@link Optional} if no participant exists with the given email.
     * @throws DatabaseAccessException If a database access error occurs during the lookup.
     */
    Optional<Participant> findByEmail(String email) throws DatabaseAccessException;
    /**
     * <p>
     * Counts the total number of participants who are currently marked as active.
     * </p>
     *
     * @return The count of active participants.
     * @throws DatabaseAccessException If a database access error occurs during counting.
     */
    long countActiveParticipants() throws DatabaseAccessException;
    /**
     * <p>
     * Retrieves a list of all participants who are currently marked as active.
     * </p>
     *
     * @return A {@link List} of active {@link Participant} objects. Returns an empty list
     * if no active participants are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Participant> findActiveParticipants() throws DatabaseAccessException;
    /**
     * <p>
     * Retrieves a list of participants associated with a specific program.
     * </p>
     *
     * @param program The program name to filter by (e.g., "TRANSPORTATION_INCENTIVE_PROGRAM", "DAILY", "NONE").
     * @return A {@link List} of {@link Participant} objects in the specified program.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Participant> findByProgram(String program) throws DatabaseAccessException;
    /**
     * <p>
     * Retrieves a list of participants based on their role within the vanpool.
     * </p>
     *
     * @param role The role to filter by (e.g., "PARTICIPANT", "COORDINATOR").
     * @return A {@link List} of {@link Participant} objects with the specified role.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Participant> findByRole(String role) throws DatabaseAccessException;
}