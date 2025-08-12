package com.avaruusstudios.vmdb.service;

import com.avaruusstudios.vmdb.model.Transaction;
import com.avaruusstudios.vmdb.model.Invoice;
import com.avaruusstudios.vmdb.model.Category;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * Defines the business rules and operations for managing {@link Transaction} entities
 * within the Vanpool Management System. This service is responsible for handling all
 * payment-related business logic, including matching payments to invoices and
 * ensuring the integrity of financial records.
 * </p>
 *
 * <p>
 * The primary responsibilities of this service include the creation and reading of
 * immutable transaction records, as well as the critical task of reconciling
 * payments with outstanding {@link com.avaruusstudios.vmdb.model.LineItem}s.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-20
 * Updated On: 2025-07-20
 *
 * @see com.avaruusstudios.vmdb.dao.TransactionDataAccess
 * @see Transaction
 */
public interface TransactionService {

    /**
     * Creates a new {@link Transaction} record in the database.
     * <p>
     * **Business Rules**:
     * <ul>
     * <li>Transactions are permanent and immutable. They cannot be updated or deleted.</li>
     * <li>The service is responsible for validating the transaction data, such as
     * ensuring the associated {@link Category} and {@link Invoice} (if provided) exist.</li>
     * </ul>
     * </p>
     *
     * @param transaction The {@link Transaction} object to create.
     * @return The newly created {@link Transaction} object with its database-assigned ID.
     */
    Transaction create(Transaction transaction);

    /**
     * Retrieves a single {@link Transaction} record by its unique identifier.
     *
     * @param id The unique integer ID of the transaction to retrieve.
     * @return The {@link Transaction} object, or {@code null} if no transaction is found with the given ID.
     */
    Transaction read(Integer id);

    /**
     * Retrieves all {@link Transaction} records from the database.
     *
     * @return A {@link List} of all {@link Transaction} objects.
     */
    List<Transaction> findAllTransactions();

    /**
     * Retrieves all {@link Transaction} records related to a specific {@link Invoice}.
     *
     * @param invoiceId The unique ID of the invoice.
     * @return A {@link List} of all {@link Transaction} objects for the given invoice.
     */
    List<Transaction> findTransactionsByInvoice(Integer invoiceId);

    /**
     * Retrieves all {@link Transaction} records related to a specific {@link Category}.
     *
     * @param categoryId The unique ID of the category.
     * @return A {@link List} of all {@link Transaction} objects for the given category.
     */
    List<Transaction> findTransactionsByCategory(Integer categoryId);

    /**
     * Retrieves all {@link Transaction} records within a specified date range.
     *
     * @param startDate The beginning of the date range (inclusive).
     * @param endDate The end of the date range (inclusive).
     * @return A {@link List} of {@link Transaction} objects within the specified date range.
     */
    List<Transaction> findTransactionsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Updates an existing {@link Transaction} record.
     * <p>
     * **Business Rule**: This method is only for correcting typos. Core financial
     * values such as `amount` and `transactionDate` are immutable and cannot be changed.
     * The service must enforce this rule by preventing such changes.
     * </p>
     *
     * @param transaction The {@link Transaction} object with updated information.
     * @return The updated {@link Transaction} object.
     */
    Transaction update(Transaction transaction);

    /**
     * Performs a logical (soft) deletion of a {@link Transaction} record.
     * <p>
     * **Business Rule**: Transactions are never hard-deleted. This method
     * performs a soft-delete by setting the `isActive` flag to false.
     * </p>
     *
     * @param id The unique integer ID of the transaction to soft-delete.
     * @return {@code true} if the transaction was successfully soft-deleted; {@code false} otherwise.
     */
    boolean delete(Integer id);
}