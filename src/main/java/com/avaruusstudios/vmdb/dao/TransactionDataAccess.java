package com.avaruusstudios.vmdb.dao;

import com.avaruusstudios.vmdb.model.Category;
import com.avaruusstudios.vmdb.model.Invoice;
import com.avaruusstudios.vmdb.model.Transaction;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * {@code TransactionDataAccess} defines the contract for data access operations pertaining to
 * {@link Transaction} entities within the Vanpool Management Database. This interface
 * specifies the methods for interacting with the persistent storage layer for transactions.
 * </p>
 *
 * <p>
 * It extends {@link CreateDataAccess}, {@link ReadDataAccess}, {@link UpdateDataAccess},
 * and {@link DeleteDataAccess} to provide standard CRUD operations (Create, Read, Update, Delete).
 * </p>
 *
 * <p>
 * Additionally, it includes specialized methods for finding transactions by date range,
 * by category, and by invoice, catering to common business requirements for transaction reporting
 * and reconciliation.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-25
 * Updated On: 2025-07-26
 *
 * @see Transaction
 * @see CreateDataAccess
 * @see ReadDataAccess
 * @see UpdateDataAccess
 * @see DeleteDataAccess
 */
public interface TransactionDataAccess extends CreateDataAccess<Transaction>,
        ReadDataAccess<Transaction, Integer>,
        UpdateDataAccess<Transaction>,
        DeleteDataAccess<Integer> { // Added DeleteDataAccess

    /**
     * <p>
     * Retrieves a list of {@link Transaction} records that fall within a specified date range.
     * The range is inclusive of both the start and end dates.
     * </p>
     *
     * @param startDate The {@link LocalDate} representing the beginning of the date range (inclusive).
     * @param endDate   The {@link LocalDate} representing the end of the date range (inclusive).
     * @return A {@link List} of {@link Transaction} objects whose `transactionDate` falls within
     * the specified range. Returns an empty list if no transactions are found in the range.
     * @throws DatabaseAccessException if a database access error occurs during retrieval.
     */
    List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseAccessException;

    /**
     * <p>
     * Retrieves a list of {@link Transaction} records associated with a specific {@link Category}.
     * </p>
     *
     * @param category The {@link Category} object whose transactions are to be retrieved.
     * The `CategoryID` of this object is used for the lookup.
     * @return A {@link List} of {@link Transaction} objects linked to the given category.
     * Returns an empty list if no transactions are found for the category.
     * @throws DatabaseAccessException if a database access error occurs during retrieval.
     */
    List<Transaction> findByCategory(Category category) throws DatabaseAccessException;

    /**
     * <p>
     * Retrieves a list of {@link Transaction} records associated with a specific {@link Invoice}.
     * </p>
     *
     * @param invoice The {@link Invoice} object whose transactions are to be retrieved.
     * The `InvoiceID` of this object is used for the lookup.
     * @return A {@link List} of {@link Transaction} objects linked to the given invoice.
     * Returns an empty list if no transactions are found for the invoice.
     * @throws DatabaseAccessException if a database access error occurs during retrieval.
     */
    List<Transaction> findByInvoice(Invoice invoice) throws DatabaseAccessException;
}