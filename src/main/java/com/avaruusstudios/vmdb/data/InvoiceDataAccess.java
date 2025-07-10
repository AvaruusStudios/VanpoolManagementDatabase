package com.avaruusstudios.vmdb.data;

import com.avaruusstudios.vmdb.model.Invoice;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the
 * {@link Invoice} entity within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It extends {@link GenericDataAccess} to inherit standard CRUD operations for Invoice,
 * and adds specialized methods relevant to invoice data retrieval and management.
 * </p>
 *
 * @author [Your Name/AvaruusStudios]
 * @version 1.2 // Updated version due to method renaming
 * @since 2025-07-08
 *
 * @see GenericDataAccess
 * @see Invoice
 * @see DatabaseAccessException
 */
public interface InvoiceDataAccess extends GenericDataAccess<Invoice, Integer> {
    /**
     * <p>
     * Retrieves a list of {@link Invoice} records that fall within a specified date range
     * (inclusive of start and end dates).
     * </p>
     *
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @return A {@link List} of {@link Invoice} objects found within the date range.
     * Returns an empty list if no invoices are found in the specified range.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Invoice> findByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseAccessException;
    /**
     * <p>
     * Retrieves a list of {@link Invoice} records that are of a specific invoice type
     * (e.g., 'LEASE', 'FUEL', 'NONE').
     * </p>
     *
     * @param invoiceType The type of invoice to filter by (e.g., "LEASE", "FUEL").
     * @return A {@link List} of {@link Invoice} objects matching the specified type.
     * Returns an empty list if no invoices are found with that type.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Invoice> findByInvoiceType(String invoiceType) throws DatabaseAccessException;

    // Removed: List<Invoice> findByStatus(String status) - renamed to findByInvoiceType
    // Removed previously: List<Invoice> findByParticipantId(Integer participantId)
}