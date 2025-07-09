package com.avaruusstudios.vmdb.data;

import com.avaruusstudios.vmdb.model.Participant; // Assuming your Participant POJO is here
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the
 * {@link Participant} entity within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It extends {@link GenericDAO} to inherit standard CRUD operations for Participant,
 * and adds specialized methods relevant to participant data retrieval and management.
 * </p>
 *
 * @author [Your Name/AvaruusStudios]
 * @version 1.1 // Updated version due to method additions based on schema review
 * @since 2025-07-06 // Original creation date
 *
 * @see GenericDAO
 * @see Participant
 * @see DatabaseAccessObjectException
 */
public interface ParticipantDAO extends GenericDAO<Participant, Integer> {
    /**
     * <p>
     * Finds a {@link Participant} record by their unique email address.
     * </p>
     *
     * @param email The email address of the participant to find.
     * @return An {@link Optional} containing the {@link Participant} if found, or an
     * empty {@link Optional} if no participant exists with the given email.
     * @throws DatabaseAccessObjectException If a database access error occurs during the lookup.
     */
    Optional<Participant> findByEmail(String email) throws DatabaseAccessObjectException;
    /**
     * <p>
     * Counts the total number of participants who are currently marked as active.
     * </p>
     *
     * @return The count of active participants.
     * @throws DatabaseAccessObjectException If a database access error occurs during counting.
     */
    long countActiveParticipants() throws DatabaseAccessObjectException;
    /**
     * <p>
     * Retrieves a list of all participants who are currently marked as active.
     * </p>
     *
     * @return A {@link List} of active {@link Participant} objects. Returns an empty list
     * if no active participants are found.
     * @throws DatabaseAccessObjectException If a database access error occurs during retrieval.
     */
    List<Participant> findActiveParticipants() throws DatabaseAccessObjectException;
    /**
     * <p>
     * Retrieves a list of participants associated with a specific program.
     * </p>
     *
     * @param program The program name to filter by (e.g., "TRANSPORTATION_INCENTIVE_PROGRAM", "DAILY", "NONE").
     * @return A {@link List} of {@link Participant} objects in the specified program.
     * @throws DatabaseAccessObjectException If a database access error occurs during retrieval.
     */
    List<Participant> findByProgram(String program) throws DatabaseAccessObjectException;
    /**
     * <p>
     * Retrieves a list of participants based on their role within the vanpool.
     * </p>
     *
     * @param role The role to filter by (e.g., "PARTICIPANT", "COORDINATOR").
     * @return A {@link List} of {@link Participant} objects with the specified role.
     * @throws DatabaseAccessObjectException If a database access error occurs during retrieval.
     */
    List<Participant> findByRole(String role) throws DatabaseAccessObjectException;
}