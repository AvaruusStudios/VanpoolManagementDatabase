package com.avaruusstudios.vmdb.data;

import com.avaruusstudios.vmdb.model.LineItem; // Assuming your LineItem POJO is here
import com.avaruusstudios.vmdb.model.Invoice; // Added import for Invoice, as it's @see'd now
import com.avaruusstudios.vmdb.model.Participant; // Added import for Participant, as it's @see'd now
import java.util.List;

/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the
 * {@link LineItem} entity within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It aggregates the standard CRUD operations for {@link LineItem} by extending
 * {@link ReadDataAccess}, {@link CreateDataAccess}, {@link UpdateDataAccess},
 * and {@link DeleteDataAccess}. This interface also adds specialized methods
 * relevant to line item data retrieval and management, particularly given its
 * strong relationship with {@link Invoice} and {@link Participant}.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0 // This version refers to the state of this specific interface's public contract
 * Created On: 2025-07-14 // Assuming original creation date of the file
 * Updated On: 2025-07-20 // Current date of modification
 * @since 2025-07-09
 *
 * @see ReadDataAccess
 * @see CreateDataAccess
 * @see UpdateDataAccess
 * @see DeleteDataAccess
 * @see LineItem
 * @see Invoice
 * @see Participant
 * @see DatabaseAccessException
 */
public interface LineItemDataAccess extends ReadDataAccess<LineItem, Integer>,
        CreateDataAccess<LineItem>,
        UpdateDataAccess<LineItem>,
        DeleteDataAccess<Integer> {

    /**
     * <p>
     * Retrieves a list of {@link LineItem} records that belong to a specific invoice.
     * </p>
     *
     * @param invoiceId The ID of the invoice whose line items are to be retrieved.
     * @return A {@link List} of {@link LineItem} objects associated with the given invoice ID.
     * Returns an empty list if no line items are found for the invoice.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<LineItem> findByInvoice(Integer invoiceId) throws DatabaseAccessException;

    /**
     * <p>
     * Retrieves a list of {@link LineItem} records that are associated with a specific participant.
     * </p>
     *
     * @param participantId The ID of the participant whose line items are to be retrieved.
     * @return A {@link List} of {@link LineItem} objects associated with the given participant ID.
     * Returns an empty list if no line items are found for the participant.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<LineItem> findByParticipant(Integer participantId) throws DatabaseAccessException;

    /**
     * <p>
     * Retrieves a list of {@link LineItem} records for a specific participant that are marked as unpaid.
     * </p>
     *
     * @param participantId The ID of the participant.
     * @return A {@link List} of unpaid {@link LineItem} objects for the specified participant.
     * Returns an empty list if no unpaid line items are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<LineItem> findUnpaidByParticipant(Integer participantId) throws DatabaseAccessException;

    /**
     * <p>
     * Deletes all {@link LineItem} records that belong to a specific invoice.
     * This method is useful when managing invoice updates or deletions that
     * require cascading changes to associated line items.
     * </p>
     *
     * @param invoiceId The ID of the invoice whose line items are to be deleted.
     * @throws DatabaseAccessException If a database access error occurs during deletion.
     */
    void deleteByInvoice(Integer invoiceId) throws DatabaseAccessException;
}