package com.avaruusstudios.vmdb.dao.impl.sqlite;

import com.avaruusstudios.vmdb.dao.CategoryDataAccess;
import com.avaruusstudios.vmdb.dao.DatabaseAccessException;
import com.avaruusstudios.vmdb.db.DatabaseManager;
import com.avaruusstudios.vmdb.db.QueryLoader;
import com.avaruusstudios.vmdb.model.Category;
import com.avaruusstudios.vmdb.model.CategoryType;
import com.avaruusstudios.vmdb.model.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * {@code CategoryDataAccessImpl} provides a concrete implementation of the {@link CategoryDataAccess}
 * interface, specifically designed for SQLite databases. This class handles all CRUD (Create, Read, Update, Delete)
 * and specialized data access operations for {@link Category} entities.
 * </p>
 *
 * <p>
 * This class implements a "soft-delete" mechanism by setting the `IsActive` flag to false (0) and
 * populating the `DeletedAt` field with a timestamp, rather than physically removing the record.
 * This is crucial for maintaining historical data integrity for audit purposes.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-25
 * Updated On: 2025-09-22
 *
 * @see CategoryDataAccess
 * @see Category
 * @see DatabaseManager
 */
public class CategoryDataAccessImpl implements CategoryDataAccess {
    /**
     * SLF4J Logger for logging informational messages, warnings, and errors within the {@code CategoryDataAccessImpl} class.
     */
    private static final Logger logger = LoggerFactory.getLogger(CategoryDataAccessImpl.class);

    /**
     * DateTimeFormatter for parsing and formatting `LocalDateTime` objects to/from database strings in "yyyy-MM-dd HH:mm:ss" format.
     */
    private static final DateTimeFormatter CUSTOM_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // SQL Query Constants
    /** SQL query to create (insert) a new category record. Loaded from `category/insertCategory.sql`. */
    private static final String SQL_CREATE_CATEGORY_RECORD;
    /** SQL query to read a single category record by its ID. Loaded from `category/selectCategoryById.sql`. */
    private static final String SQL_READ_CATEGORY_RECORD;
    /** SQL query to read all category records. Loaded from `category/selectAllCategories.sql`. */
    private static final String SQL_READ_ALL_CATEGORY_RECORD;
    /** SQL query to read all active (IsActive = 1) category records. Loaded from `category/selectActiveCategories.sql`. */
    private static final String SQL_READ_ACTIVE_CATEGORIES;
    /** SQL query to count the total number of category records. Loaded from `category/countCategories.sql`. */
    private static final String SQL_COUNT_CATEGORY_RECORD;
    /** SQL query to count the total number of active (IsActive = 1) category records. Loaded from `category/countActiveCategories.sql`. */
    private static final String SQL_COUNT_ACTIVE_CATEGORIES;
    /** SQL query to check if a category record exists by its ID. Loaded from `category/existById.sql`. */
    private static final String SQL_EXISTS_CATEGORY_BY_ID;
    /** SQL query to update an existing category record. Loaded from `category/updateCategory.sql`. */
    private static final String SQL_UPDATE_CATEGORY_RECORD;
    /** SQL query to soft-delete a category record by updating its `IsActive` and `DeletedAt` fields. Loaded from `category/deleteCategorySoft.sql`. */
    private static final String SQL_DELETE_CATEGORY_SOFT;
    /** SQL query to find categories by their type. Loaded from `category/selectCategoriesByType.sql`. */
    private static final String SQL_FIND_CATEGORIES_BY_TYPE;
    /** SQL query to find all distinct category types. Loaded from `category/selectDistinctCategoryTypes.sql`. */
    private static final String SQL_FIND_DISTINCT_CATEGORY_TYPES;

    static {
        try {
            SQL_CREATE_CATEGORY_RECORD = QueryLoader.getQuery("category/insertCategory.sql");
            SQL_READ_CATEGORY_RECORD = QueryLoader.getQuery("category/selectCategoryById.sql");
            SQL_READ_ALL_CATEGORY_RECORD = QueryLoader.getQuery("category/selectAllCategories.sql");
            SQL_READ_ACTIVE_CATEGORIES = QueryLoader.getQuery("category/selectActiveCategories.sql");
            SQL_COUNT_CATEGORY_RECORD = QueryLoader.getQuery("category/countCategories.sql");
            SQL_COUNT_ACTIVE_CATEGORIES = QueryLoader.getQuery("category/countActiveCategories.sql");
            SQL_EXISTS_CATEGORY_BY_ID = QueryLoader.getQuery("category/existById.sql");
            SQL_UPDATE_CATEGORY_RECORD = QueryLoader.getQuery("category/updateCategory.sql");
            SQL_DELETE_CATEGORY_SOFT = QueryLoader.getQuery("category/deleteCategorySoft.sql");
            SQL_FIND_CATEGORIES_BY_TYPE = QueryLoader.getQuery("category/selectCategoriesByType.sql");
            SQL_FIND_DISTINCT_CATEGORY_TYPES = QueryLoader.getQuery("category/selectDistinctCategoryTypes.sql");

            logger.info("All SQL queries for CategoryDataAccessImpl loaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load one or more SQL queries for CategoryDataAccessImpl. Check .sql files and paths.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    // --- Private Helper Methods ---

    /**
     * <p>
     * Maps a row from a {@link ResultSet} to a {@link Category} object.
     * This private helper method centralizes the logic for converting raw database
     * column values into a populated {@code Category} model object, ensuring data consistency
     * and type safety across all read operations.
     * </p>
     *
     * @param rs The {@link ResultSet} positioned at the current row containing category data.
     * @return A fully populated {@link Category} object with data retrieved from the {@code ResultSet}.
     * @throws SQLException If a database access error occurs (e.g., a column name is not found,
     * or there is a data type mismatch during retrieval).
     * @throws DatabaseAccessException If there's an issue converting database values to model objects,
     * specifically for {@link CategoryType}.
     */
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException, DatabaseAccessException {
        Category category = new Category();

        Integer categoryId = rs.getInt("CategoryID");
        if (categoryId > 0) {
            category._setCategoryID(categoryId);
        }

        Integer participantId = rs.getObject("ParticipantID_FK", Integer.class);
        if (participantId != null) {
            Participant participant = new Participant();
            participant._setParticipantID(participantId);
            category.setParticipant(participant);
        } else {
            category.setParticipant(null);
        }

        String categoryTypeStr = rs.getString("CategoryType");
        if (categoryTypeStr != null) {
            try {
                category.setCategoryType(CategoryType.fromDbValue(categoryTypeStr));
            } catch (IllegalArgumentException e) {
                logger.error("Invalid CategoryType value '{}' in database for CategoryID {}. {}", categoryTypeStr, categoryId, e.getMessage());
                throw new DatabaseAccessException("Invalid CategoryType value from database: " + categoryTypeStr, e);
            }
        } else {
            logger.warn("CategoryType is NULL for CategoryID {}. This should not occur.", categoryId);
            throw new DatabaseAccessException("CategoryType cannot be null for CategoryID: " + categoryId);
        }

        category.setCategoryName(rs.getString("CategoryName"));
        category.setDescription(rs.getString("Description"));
        category.setIsActive(rs.getInt("IsActive"));

        String deletedAtStr = rs.getString("DeletedAt");
        category.setDeletedAt(deletedAtStr != null && !deletedAtStr.isEmpty() ?
                LocalDateTime.parse(deletedAtStr, CUSTOM_DATETIME_FORMATTER) : null);

        return category;
    }

    /**
     * <p>
     * Helper method to enforce the business rule regarding {@link CategoryType} and
     * {@link Participant} association before persisting a {@link Category} object.
     * </p>
     *
     * @param category The {@link Category} object to validate.
     * @throws IllegalArgumentException If the business rule is violated.
     */
    private void enforceParticipantCategoryTypeBusinessRule(Category category) {
        if (category.getCategoryType() == CategoryType.INCOME) {
            if (category.getParticipant() == null || category.getParticipant().getParticipantID() == null) {
                throw new IllegalArgumentException("For INCOME category type, a Participant must be specified.");
            }
        } else if (category.getCategoryType() == CategoryType.EXPENSE || category.getCategoryType() == CategoryType.CREDIT) {
            if (category.getParticipant() != null) {
                if (category.getParticipant().getParticipantID() != null) {
                    throw new IllegalArgumentException("For EXPENSE or CREDIT category types, a Participant must NOT be specified.");
                }
            }
        }
    }

    /**
     * <p>
     * Helper method to set common parameters for a {@link PreparedStatement} when
     * creating or updating a {@link Category} record.
     * </p>
     *
     * @param stmt     The {@link PreparedStatement} to which parameters will be set.
     * @param category The {@link Category} object containing the data.
     * @param startIndex The starting index for setting parameters.
     * @throws SQLException If a database access error occurs during parameter setting.
     */
    private void setCategoryStatementParameters(PreparedStatement stmt, Category category, int startIndex) throws SQLException {
        if (category.getParticipant() != null && category.getParticipant().getParticipantID() != null) {
            stmt.setInt(startIndex, category.getParticipant().getParticipantID());
        } else {
            stmt.setNull(startIndex, java.sql.Types.INTEGER);
        }
        stmt.setString(startIndex + 1, category.getCategoryType().getDbValue());
        stmt.setString(startIndex + 2, category.getCategoryName());
        stmt.setString(startIndex + 3, category.getDescription());
        stmt.setInt(startIndex + 4, category.getIsActive());
        stmt.setString(startIndex + 5, category.getDeletedAt() != null ? category.getDeletedAt().format(CUSTOM_DATETIME_FORMATTER) : null);
    }

    // --- Interface Implementations ---

    /**
     * {@inheritDoc}
     *
     * <p>
     * This method enforces the business rule:
     * <ul>
     * <li>If {@link CategoryType#INCOME}, {@link Participant} must be specified.</li>
     * <li>If {@link CategoryType#EXPENSE} or {@link CategoryType#CREDIT}, {@link Participant} must NOT be specified.</li>
     * </ul>
     * </p>
     */
    @Override
    public Category create(Category category) throws DatabaseAccessException {
        Objects.requireNonNull(category, "Category object cannot be null for creation.");
        Objects.requireNonNull(category.getCategoryType(), "Category type cannot be null for creation.");
        Objects.requireNonNull(category.getCategoryName(), "Category name cannot be null for creation.");

        if (category.getCategoryID() != null) {
            throw new IllegalArgumentException("Category ID must be null for new record creation (auto-generated).");
        }

        enforceParticipantCategoryTypeBusinessRule(category);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CREATE_CATEGORY_RECORD, Statement.RETURN_GENERATED_KEYS)) {

            setCategoryStatementParameters(stmt, category, 1);

            logger.debug("Executing insert category query for name: {}, type: {}",
                    category.getCategoryName(), category.getCategoryType().getDbValue());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.error("Creating category failed, no rows affected for name: {}, type: {}",
                        category.getCategoryName(), category.getCategoryType().getDbValue());
                throw new DatabaseAccessException("Creating category failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Integer generatedId = generatedKeys.getInt(1);
                    category._setCategoryID(generatedId);
                    logger.info("Successfully created category with ID: {}", generatedId);
                    return category;
                } else {
                    logger.error("Creating category failed, no ID obtained for name: {}, type: {}",
                            category.getCategoryName(), category.getCategoryType().getDbValue());
                    throw new DatabaseAccessException("Creating category failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating category record: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error creating category record: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error("Business rule violation during category creation: {}", e.getMessage());
            throw new DatabaseAccessException("Business rule violation: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This method performs a soft-delete by updating the `IsActive` flag to `0` and
     * the `DeletedAt` timestamp to the current time.
     * </p>
     */
    @Override
    public boolean delete(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Category ID cannot be null for deletion.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_CATEGORY_SOFT)) {

            stmt.setString(1, LocalDateTime.now().format(CUSTOM_DATETIME_FORMATTER));
            stmt.setInt(2, id);

            logger.debug("Executing soft-delete category query for ID: {}", id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No category found with ID: {} for soft-deletion.", id);
                return false;
            } else {
                logger.info("Successfully soft-deleted category with ID: {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error soft-deleting category record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error soft-deleting category record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Category> find(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Category ID cannot be null for reading.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_CATEGORY_RECORD)) {

            stmt.setInt(1, id);
            logger.debug("Executing read category query for ID: {}", id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Category category = mapResultSetToCategory(rs);
                    logger.info("Found category with ID: {}", id);
                    return Optional.of(category);
                } else {
                    logger.info("No category found with ID: {}", id);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Error reading category record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error reading category record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> findAll() throws DatabaseAccessException {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_ALL_CATEGORY_RECORD);
             ResultSet rs = stmt.executeQuery()) {

            logger.debug("Executing read all categories query.");
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
            logger.info("Found {} total categories.", categories.size());
        } catch (SQLException e) {
            logger.error("Error reading all category records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error reading all category records: " + e.getMessage(), e);
        }
        return categories;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This method retrieves all active category records (where `IsActive = 1`).
     * </p>
     */
    @Override
    public List<Category> findActive() throws DatabaseAccessException {
        List<Category> activeCategories = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_READ_ACTIVE_CATEGORIES);
             ResultSet rs = stmt.executeQuery()) {

            logger.debug("Executing read active categories query.");
            while (rs.next()) {
                activeCategories.add(mapResultSetToCategory(rs));
            }
            logger.info("Found {} active categories.", activeCategories.size());
        } catch (SQLException e) {
            logger.error("Error reading active category records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error reading active category records: " + e.getMessage(), e);
        }
        return activeCategories;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() throws DatabaseAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_CATEGORY_RECORD);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.info("Total category record count: {}", count);
                return count;
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting category records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error counting category records: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This method counts all active category records (where `IsActive = 1`).
     * </p>
     */
    @Override
    public long countActive() throws DatabaseAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_ACTIVE_CATEGORIES);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.info("Total active category record count: {}", count);
                return count;
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting active category records: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error counting active category records: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Category ID cannot be null for existence check.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EXISTS_CATEGORY_BY_ID)) {

            stmt.setInt(1, id);
            logger.debug("Executing existsById category query for ID: {}", id);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean exists = rs.next();
                logger.info("Category with ID {} exists: {}", id, exists);
                return exists;
            }
        } catch (SQLException e) {
            logger.error("Error checking existence of category record with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseAccessException("Error checking existence of category record: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This method enforces the business rule:
     * <ul>
     * <li>If {@link CategoryType#INCOME}, a {@link Participant} must be specified.</li>
     * <li>If {@link CategoryType#EXPENSE} or {@link CategoryType#CREDIT}, a {@link Participant} must NOT be specified.</li>
     * </ul>
     * </p>
     */
    @Override
    public Category update(Category category) throws DatabaseAccessException {
        Objects.requireNonNull(category, "Category object cannot be null for update.");
        Objects.requireNonNull(category.getCategoryID(), "Category ID must not be null for update.");
        Objects.requireNonNull(category.getCategoryType(), "Category type cannot be null for update.");
        Objects.requireNonNull(category.getCategoryName(), "Category name cannot be null for update.");

        enforceParticipantCategoryTypeBusinessRule(category);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_CATEGORY_RECORD)) {

            setCategoryStatementParameters(stmt, category, 1);
            stmt.setInt(7, category.getCategoryID());

            logger.debug("Executing update category query for ID: {}", category.getCategoryID());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No category found with ID: {} for update. Update operation resulted in 0 affected rows.", category.getCategoryID());
                throw new DatabaseAccessException("Category with ID " + category.getCategoryID() + " not found for update.");
            } else {
                logger.info("Successfully updated category with ID: {}", category.getCategoryID());
                return category;
            }
        } catch (SQLException e) {
            logger.error("Error updating category record with ID {}: {}", category.getCategoryID(), e.getMessage(), e);
            throw new DatabaseAccessException("Error updating category record: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error("Business rule violation during category update: {}", e.getMessage());
            throw new DatabaseAccessException("Business rule violation: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Retrieves a list of all {@link Category} records that match a specified {@link CategoryType}.
     * </p>
     */
    @Override
    public List<Category> findByType(CategoryType type) throws DatabaseAccessException {
        Objects.requireNonNull(type, "CategoryType cannot be null for finding categories by type.");
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_CATEGORIES_BY_TYPE)) {

            stmt.setString(1, type.getDbValue());

            logger.debug("Executing findCategoriesByType query for CategoryType: {}", type.getDbValue());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
            logger.info("Found {} categories for CategoryType: {}", categories.size(), type.getDbValue());
        } catch (SQLException e) {
            logger.error("Error finding categories by CategoryType {}: {}", type.getDbValue(), e.getMessage(), e);
            throw new DatabaseAccessException("Error finding categories by CategoryType: " + e.getMessage(), e);
        }
        return categories;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Retrieves a list of all distinct {@link CategoryType} values currently present in the database.
     * </p>
     */
    @Override
    public List<CategoryType> findDistinctTypes() throws DatabaseAccessException {
        List<CategoryType> distinctTypes = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_DISTINCT_CATEGORY_TYPES);
             ResultSet rs = stmt.executeQuery()) {

            logger.debug("Executing findUniqueCategoryTypes query.");
            while (rs.next()) {
                String typeStr = rs.getString(1);
                if (typeStr != null) {
                    try {
                        distinctTypes.add(CategoryType.fromDbValue(typeStr));
                    } catch (IllegalArgumentException e) {
                        logger.warn("Unknown CategoryType value '{}' found in database, skipping. {}", typeStr, e.getMessage());
                    }
                }
            }
            logger.info("Found {} distinct category types.", distinctTypes.size());
        } catch (SQLException e) {
            logger.error("Error finding distinct category types: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error finding distinct category types: " + e.getMessage(), e);
        }
        return distinctTypes;
    }
}