package com.avaruusstudios.vmdb.data;

import com.avaruusstudios.vmdb.model.Transaction;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the
 * {@link Transaction} entity within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It extends {@link GenericDataAccess} to inherit standard CRUD operations for Transaction,
 * and includes specific methods for filtering transactions by date range, category, and invoice.
 * This refined interface reflects a streamlined approach, moving complex aggregations and
 * calculations (like running balances) to higher service or presentation layers, and
 * removing dependencies on vehicle-specific data based on recent schema adjustments.
 * </p>
 *
 * @author [Your Name/AvaruusStudios]
 * @version 1.1 // Updated version to reflect removals and refinements
 * @since 2025-07-09 // Original creation date
 *
 * @see GenericDataAccess
 * @see Transaction
 * @see DatabaseAccessException
 */
public interface TransactionDataAccess extends UpdateDataAccess<Transaction, Integer> {

    /**
     * <p>
     * Retrieves a list of {@link Transaction} records that fall within a specified date range.
     * </p>
     *
     * @param startDate The start date of the range (inclusive). Must not be {@code null}.
     * @param endDate   The end date of the range (inclusive). Must not be {@code null}.
     * @return A {@link List} of transactions within the given date range. Returns an empty list if none are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseAccessException;

    /**
     * <p>
     * Retrieves a list of {@link Transaction} records associated with a specific category.
     * </p>
     *
     * @param categoryId The ID of the {@link com.avaruusstudios.vmdb.model.Category}. Must not be {@code null}.
     * @return A {@link List} of transactions for the specified category. Returns an empty list if none are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Transaction> findByCategoryId(Integer categoryId) throws DatabaseAccessException;

    /**
     * <p>
     * Retrieves a list of {@link Transaction} records associated with a specific invoice.
     * </p>
     * <p>
     * Note: The `InvoiceID_FK` in the database schema is nullable, so transactions may or may not be linked to an invoice.
     * This method will only return transactions that *are* linked to the specified `invoiceId`.
     * </p>
     *
     * @param invoiceId The ID of the {@link com.avaruusstudios.vmdb.model.Invoice}. Must not be {@code null}.
     * @return A {@link List} of transactions for the specified invoice. Returns an empty list if none are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Transaction> findByInvoiceId(Integer invoiceId) throws DatabaseAccessException;

    /**
     * <p>
     * Retrieves a list of {@link Transaction} records filtered by both category and date range.
     * </p>
     *
     * @param categoryId The ID of the {@link com.avaruusstudios.vmdb.model.Category}. Must not be {@code null}.
     * @param startDate  The start date of the range (inclusive). Must not be {@code null}.
     * @param endDate    The end date of the range (inclusive). Must not be {@code null}.
     * @return A {@link List} of transactions matching the criteria. Returns an empty list if none are found.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Transaction> findByCategoryIdAndDateRange(Integer categoryId, LocalDate startDate, LocalDate endDate) throws DatabaseAccessException;
}