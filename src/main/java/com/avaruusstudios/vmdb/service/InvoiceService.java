package com.avaruusstudios.vmdb.service;

import com.avaruusstudios.vmdb.model.Invoice;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * Defines the business rules and operations for managing {@link Invoice} entities
 * within the Vanpool Management System. This service acts as an intermediary layer,
 * enforcing business logic and coordinating data access for invoices.
 * </p>
 *
 * <p>
 * The primary responsibilities of this service include validating invoice data
 * and handling all CRUD (Create, Read, Update, Delete) operations in a business-rule
 * compliant manner.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-08-23
 * Updated On: 2025-08-23
 *
 * @see com.avaruusstudios.vmdb.dao.InvoiceDataAccess
 * @see Invoice
 */
public interface InvoiceService {

    /**
     * Creates a new {@link Invoice} record in the database.
     *
     * @param invoice The {@link Invoice} object to create.
     * @return The newly created {@link Invoice} object with its database-assigned ID.
     */
    Invoice create(Invoice invoice);

    /**
     * Retrieves a single {@link Invoice} record by its unique identifier.
     *
     * @param id The unique integer ID of the invoice to retrieve.
     * @return The {@link Invoice} object, or {@code null} if no invoice is found with the given ID.
     */
    Invoice findInvoiceById(Integer id);

    /**
     * Retrieves all {@link Invoice} records from the database.
     *
     * @return A {@link List} of all {@link Invoice} objects.
     */
    List<Invoice> findAllInvoices();

    /**
     * Updates an existing {@link Invoice} record in the database.
     *
     * @param invoice The {@link Invoice} object with updated information.
     * @return The updated {@link Invoice} object.
     */
    Invoice update(Invoice invoice);

    /**
     * Performs a logical (soft) deletion of an {@link Invoice} record.
     *
     * @param id The unique integer ID of the invoice to soft-delete.
     * @return {@code true} if the invoice was successfully soft-deleted; {@code false} otherwise.
     */
    boolean delete(Integer id);

    /**
     * Finds a list of invoices that fall within a specified date range.
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @return A {@link List} of invoices within the date range.
     */
    List<Invoice> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Finds a list of invoices by their type.
     *
     * @param invoiceType The type of invoice to filter by.
     * @return A {@link List} of invoices with the specified type.
     */
    List<Invoice> findByType(String invoiceType);

    /**
     * Checks if an invoice with the given ID exists.
     *
     * @param id The ID to check.
     * @return {@code true} if the invoice exists, {@code false} otherwise.
     */
    boolean existsById(Integer id);

    /**
     * Counts the total number of invoice records.
     *
     * @return The total count of invoices.
     */
    long countAll();
}