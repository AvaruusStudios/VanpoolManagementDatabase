package com.avaruusstudios.vmdb.data;

import com.avaruusstudios.vmdb.model.LineItem; // Assuming your LineItem POJO is here
import java.util.List;

/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the
 * {@link LineItem} entity within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It extends {@link DeleteDataAccess} to inherit standard CRUD operations for LineItem,
 * and adds specialized methods relevant to line item data retrieval and management,
 * particularly given its strong relationship with {@link com.avaruusstudios.vmdb.model.Invoice}
 * and {@link com.avaruusstudios.vmdb.model.Participant}.
 * </p>
 *
 * @author [Your Name/AvaruusStudios]
 * @version 1.0
 * @since 2025-07-09 // Current date
 * @see DeleteDataAccess
 * @see LineItem
 * @see DatabaseAccessException
 */
public interface LineItemDataAccess extends DeleteDataAccess<LineItem, Integer> {

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
    List<LineItem> findByInvoiceId(Integer invoiceId) throws DatabaseAccessException;

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
    List<LineItem> findByParticipantId(Integer participantId) throws DatabaseAccessException;

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
    List<LineItem> findUnpaidLineItemsByParticipantId(Integer participantId) throws DatabaseAccessException;

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
    void deleteByInvoiceId(Integer invoiceId) throws DatabaseAccessException;
}