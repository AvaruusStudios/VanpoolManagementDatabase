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
 * @version 1.0
 * @since 2025-07-05
 *
 * @see GenericDAO
 * @see Participant
 * @see DAOException
 */
public interface ParticipantDAO extends GenericDAO<Participant, Integer> {

    /**
     * <p>
     * Finds a {@link Participant} record by their unique email address.
     * This is a common lookup operation not covered by generic ID-based retrieval.
     * </p>
     *
     * @param email The email address of the participant to find.
     * @return An {@link Optional} containing the {@link Participant} if found, or an
     * empty {@link Optional} if no participant exists with the given email.
     * @throws DAOException If a database access error occurs during the lookup.
     */
    Optional<Participant> findByEmail(String email) throws DAOException;

    /**
     * <p>
     * Retrieves a list of all participants who are currently marked as active.
     * </p>
     *
     * @return A {@link List} of active {@link Participant} objects. Returns an empty list
     * if no active participants are found.
     * @throws DAOException If a database access error occurs during retrieval.
     */
    List<Participant> findActiveParticipants() throws DAOException;

    /**
     * <p>
     * Counts the total number of participants who are currently marked as active.
     * </p>
     *
     * @return The count of active participants.
     * @throws DAOException If a database access error occurs during counting.
     */
    long countActiveParticipants() throws DAOException;

    // You would add any other Participant-specific queries here, for example:
    // List<Participant> findParticipantsByLastName(String lastName) throws DAOException;
    // Optional<Participant> findByPhoneNumber(String phoneNumber) throws DAOException;
}