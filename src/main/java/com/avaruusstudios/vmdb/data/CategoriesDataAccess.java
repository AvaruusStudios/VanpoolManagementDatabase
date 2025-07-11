package com.avaruusstudios.vmdb.data;

import com.avaruusstudios.vmdb.model.Category;
import com.avaruusstudios.vmdb.model.CategoryType;
import java.util.List;


/**
 * <p>
 * This interface defines the contract for Data Access Operations specific to the
 * {@link Category} entity within the Vanpool Management Database (VMDB) application.
 * </p>
 *
 * <p>
 * It extends {@link GenericDataAccess} to inherit standard CRUD operations for Category.
 * This DAO is designed to support UI elements like cascading dropdowns by providing
 * access to distinct category types and all categories filtered by a specific type,
 * aligning with the Coordinator's tracking and data presentation needs.
 * </p>
 *
 * @author [Your Name/AvaruusStudios]
 * @version 1.4 // Incremented version for new method additions based on UI requirements
 * @since 2025-07-09
 *
 * @see GenericDataAccess
 * @see Category
 * @see DatabaseAccessException
 */
public interface CategoriesDataAccess extends GenericDataAccess<Category, Integer> {

    /**
     * <p>
     * Retrieves a list of all unique {@link CategoryType} values currently present
     * in the `Categories` table.
     * </p>
     * <p>
     * This method is ideal for populating the first dropdown in a cascading category
     * selection mechanism, ensuring that only category types for which records exist
     * are presented to the user.
     * </p>
     *
     * @return A {@link List} of unique {@link CategoryType} enums. Returns an empty list if no categories are present.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<CategoryType> findUniqueCategoryTypes() throws DatabaseAccessException;

    /**
     * <p>
     * Retrieves a list of all {@link Category} records that belong to a specified {@link CategoryType}.
     * </p>
     * <p>
     * This method is designed to populate the second dropdown in a cascading category
     * selection, providing all category names associated with the chosen type.
     * This includes both participant-linked income categories (where {@code CategoryName}
     * is a participant's name) and general vanpool expense/credit categories
     * (where {@code ParticipantID_FK} is {@code NULL}). Further filtering by participant
     * or specific names will occur outside this DAO layer.
     * </p>
     *
     * @param categoryType The {@link CategoryType} to filter categories by (e.g., {@link CategoryType#INCOME}, {@link CategoryType#EXPENSE}, {@link CategoryType#CREDIT}). Must not be {@code null}.
     * @return A {@link List} of all {@link Category} objects matching the specified type.
     * Returns an empty list if no categories are found for the given type.
     * @throws DatabaseAccessException If a database access error occurs during retrieval.
     */
    List<Category> findCategoriesByType(CategoryType categoryType) throws DatabaseAccessException;
}