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
 * It aggregates the standard CRUD operations for {@link Invoice} by extending
 * {@link ReadDataAccess}, {@link CreateDataAccess}, {@link UpdateDataAccess},
 * and {@link DeleteDataAccess}. This interface also adds specialized methods
 * relevant to invoice data retrieval and management, supporting various filtering
 * and reporting needs.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.2 // Version updated for interface refactoring
 * Created On: 2025-07-14 // Assuming original creation date of the file
 * Updated On: 2025-07-20 // Current date of modification
 * @since 2025-07-08
 *
 * @see ReadDataAccess
 * @see CreateDataAccess
 * @see UpdateDataAccess
 * @see DeleteDataAccess
 * @see Invoice
 * @see DatabaseAccessException
 */
public interface InvoiceDataAccess extends ReadDataAccess<Invoice, Integer>,
        CreateDataAccess<Invoice>,
        UpdateDataAccess<Invoice>,
        DeleteDataAccess<Integer> {
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