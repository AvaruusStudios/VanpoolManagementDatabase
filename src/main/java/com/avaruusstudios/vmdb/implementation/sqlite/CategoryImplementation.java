package com.avaruusstudios.vmdb.implementation.sqlite;

import com.avaruusstudios.vmdb.data.CategoryDataAccess;
import com.avaruusstudios.vmdb.data.DatabaseAccessException;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * {@code CategoryImplementation} provides a concrete implementation of the {@link CategoryDataAccess}
 * interface, specifically designed for SQLite databases. This class handles all CRUD (Create, Read, Update, Delete)
 * and specialized data access operations for {@link Category} entities.
 * </p>
 *
 * <p>
 * It interacts with the database using JDBC, preparing SQL statements, mapping {@link ResultSet}
 * rows to {@link Category} objects, and managing database connections through a {@link DatabaseManager}.
 * </p>
 *
 * <p>
 * This class also enforces the business rule regarding {@link CategoryType} and {@link Participant}
 * association, as defined in {@link Category} model's Javadoc.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-25
 * Updated On: 2025-07-25
 *
 * @see CategoryDataAccess
 * @see Category
 * @see DatabaseManager
 */
public class CategoryImplementation implements CategoryDataAccess {
    /**
     * SLF4J Logger for logging informational messages, warnings, and errors within the {@code CategoryImplementation} class.
     */
    private static final Logger logger = LoggerFactory.getLogger(CategoryImplementation.class);

    /**
     * DateTimeFormatter for parsing and formatting `LocalDate` objects to/from database strings in "yyyy-MM-dd" format.
     * Note: Category uses LocalDate for `deletedAt`.
     */
    private static final DateTimeFormatter CUSTOM_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // SQL Query Constants
    private static final String SQL_CREATE_CATEGORY_RECORD;
    private static final String SQL_READ_CATEGORY_RECORD;
    private static final String SQL_READ_ALL_CATEGORY_RECORD;
    private static final String SQL_COUNT_CATEGORY_RECORD;
    private static final String SQL_EXISTS_CATEGORY_BY_ID;
    private static final String SQL_UPDATE_CATEGORY_RECORD;
    private static final String SQL_DELETE_CATEGORY_SOFT;
    private static final String SQL_FIND_CATEGORIES_BY_TYPE; // Renamed SQL constant
    private static final String SQL_FIND_DISTINCT_CATEGORY_TYPES; // Renamed SQL constant

    static {
        try {
            // You will need to create corresponding .sql files in your `queries/category/` directory.
            SQL_CREATE_CATEGORY_RECORD = QueryLoader.getQuery("category/insertCategory.sql");
            SQL_READ_CATEGORY_RECORD = QueryLoader.getQuery("category/selectCategoryById.sql");
            SQL_READ_ALL_CATEGORY_RECORD = QueryLoader.getQuery("category/selectAllCategories.sql");
            SQL_COUNT_CATEGORY_RECORD = QueryLoader.getQuery("category/countCategories.sql");
            SQL_EXISTS_CATEGORY_BY_ID = QueryLoader.getQuery("category/existById.sql");
            SQL_UPDATE_CATEGORY_RECORD = QueryLoader.getQuery("category/updateCategory.sql");
            SQL_DELETE_CATEGORY_SOFT = QueryLoader.getQuery("category/deleteCategorySoft.sql");
            SQL_FIND_CATEGORIES_BY_TYPE = QueryLoader.getQuery("category/selectCategoriesByType.sql");
            SQL_FIND_DISTINCT_CATEGORY_TYPES = QueryLoader.getQuery("category/selectDistinctCategoryTypes.sql");

            logger.info("All SQL queries for CategoryImplementation loaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load one or more SQL queries for CategoryImplementation. Check .sql files and paths.", e);
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
     * <p>
     * It handles:
     * <ul>
     * <li>Retrieval of the auto-generated `CategoryID` and setting it via the `_setCategoryID` method.</li>
     * <li>Mapping of foreign key IDs (`ParticipantID_FK`) to {@link Participant} objects (nullable).</li>
     * <li>Conversion of `CategoryType` from `TEXT` to {@link CategoryType} enum.</li>
     * <li>Conversion of `IsActive` from `INTEGER` (0 or 1) to `boolean`.</li>
     * <li>Handling of nullable `DeletedAt` `TEXT` field, parsing it into
     * {@link LocalDate} objects.</li>
     * <li>String fields (`CategoryName`, `Description`).</li>
     * </ul>
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
        if (categoryId > 0) { // SQLite getInt returns 0 for NULL for INTEGER PRIMARY KEY, check for positive valid ID
            category._setCategoryID(categoryId); // Use the public setter
        }

        // Participant FKey - Map to Participant object (nullable)
        Integer participantId = rs.getObject("ParticipantID_FK", Integer.class);
        if (participantId != null) {
            // Assuming Participant has a constructor or setter for its ID
            // or you might fetch the full Participant object here if needed,
            // but for now, just setting the ID is sufficient for the Category model.
            Participant participant = new Participant();
            participant._setParticipantID(participantId);
            category.setParticipant(participant);
        } else {
            category.setParticipant(null); // Explicitly set to null if no participant ID
        }

        // CategoryType
        String categoryTypeStr = rs.getString("CategoryType");
        if (categoryTypeStr != null) {
            try {
                category.setCategoryType(CategoryType.fromDbValue(categoryTypeStr));
            } catch (IllegalArgumentException e) {
                logger.error("Invalid CategoryType value '{}' in database for CategoryID {}. {}", categoryTypeStr, categoryId, e.getMessage());
                throw new DatabaseAccessException("Invalid CategoryType value from database: " + categoryTypeStr, e);
            }
        } else {
            // This should ideally not happen if CategoryType is NOT NULL in DB, but handle defensively
            logger.warn("CategoryType is NULL for CategoryID {}. This should not occur.", categoryId);
            // Decide how to handle: throw exception or set a default/null. Throwing an exception is safer.
            throw new DatabaseAccessException("CategoryType cannot be null for CategoryID: " + categoryId);
        }


        category.setCategoryName(rs.getString("CategoryName"));
        category.setDescription(rs.getString("Description"));
        category.setIsActive(rs.getInt("IsActive")); // This is already 0 or 1, and setIsActive expects int

        String deletedAtStr = rs.getString("DeletedAt");
        category.setDeletedAt(deletedAtStr != null && !deletedAtStr.isEmpty() ?
                LocalDate.parse(deletedAtStr, CUSTOM_DATE_FORMATTER) : null);

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
                // We should also check if the participant ID is set, if the participant object exists
                if (category.getParticipant().getParticipantID() != null) {
                    throw new IllegalArgumentException("For EXPENSE or CREDIT category types, a Participant must NOT be specified.");
                }
            }
        }
    }

    /**
     * <p>
     * Helper method to set common parameters for {@link PreparedStatement} when
     * creating or updating a {@link Category} record.
     * </p>
     *
     * @param stmt     The {@link PreparedStatement} to which parameters will be set.
     * @param category The {@link Category} object containing the data.
     * @param startIndex The starting index for setting parameters. Useful if the statement has
     * other parameters before the common ones (e.g., for update statements).
     * @throws SQLException If a database access error occurs during parameter setting.
     */
    private void setCategoryStatementParameters(PreparedStatement stmt, Category category, int startIndex) throws SQLException {
        // ParticipantID_FK can be NULL, so handle it
        if (category.getParticipant() != null && category.getParticipant().getParticipantID() != null) {
            stmt.setInt(startIndex, category.getParticipant().getParticipantID());
        } else {
            stmt.setNull(startIndex, java.sql.Types.INTEGER);
        }
        stmt.setString(startIndex + 1, category.getCategoryType().getDbValue());
        stmt.setString(startIndex + 2, category.getCategoryName());
        stmt.setString(startIndex + 3, category.getDescription());
        stmt.setInt(startIndex + 4, category.getIsActive()); // IsActive is 0 or 1
        stmt.setString(startIndex + 5, category.getDeletedAt() != null ? category.getDeletedAt().format(CUSTOM_DATE_FORMATTER) : null);
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
    public Category createRecord(Category category) throws DatabaseAccessException {
        Objects.requireNonNull(category, "Category object cannot be null for creation.");
        Objects.requireNonNull(category.getCategoryType(), "Category type cannot be null for creation.");
        Objects.requireNonNull(category.getCategoryName(), "Category name cannot be null for creation.");

        if (category.getCategoryID() != null) {
            throw new IllegalArgumentException("Category ID must be null for new record creation (auto-generated).");
        }

        // Enforce business rule before database interaction
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
                    category._setCategoryID(generatedId); // Set the auto-generated ID
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
     * <p>
     * This method performs a soft-delete by updating `IsActive` and `DeletedAt` fields.
     * </p>
     */
    @Override
    public void deleteRecord(Integer id) throws DatabaseAccessException {
        Objects.requireNonNull(id, "Category ID cannot be null for deletion.");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_CATEGORY_SOFT)) {

            stmt.setString(1, LocalDate.now().format(CUSTOM_DATE_FORMATTER)); // Set DeletedAt
            stmt.setInt(2, id); // Where CategoryID = ?

            logger.debug("Executing soft-delete category query for ID: {}", id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No category found with ID: {} for soft-deletion.", id);
            } else {
                logger.info("Successfully soft-deleted category with ID: {}", id);
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
    public Optional<Category> readRecord(Integer id) throws DatabaseAccessException {
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
    public List<Category> readRecordAll() throws DatabaseAccessException {
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
     */
    @Override
    public long countRecord() throws DatabaseAccessException {
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
     * <li>If {@link CategoryType#INCOME}, {@link Participant} must be specified.</li>
     * <li>If {@link CategoryType#EXPENSE} or {@link CategoryType#CREDIT}, {@link Participant} must NOT be specified.</li>
     * </ul>
     * </p>
     */
    @Override
    public void updateRecord(Category category) throws DatabaseAccessException {
        Objects.requireNonNull(category, "Category object cannot be null for update.");
        Objects.requireNonNull(category.getCategoryID(), "Category ID must not be null for update.");
        Objects.requireNonNull(category.getCategoryType(), "Category type cannot be null for update.");
        Objects.requireNonNull(category.getCategoryName(), "Category name cannot be null for update.");

        // Enforce business rule before database interaction
        enforceParticipantCategoryTypeBusinessRule(category);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_CATEGORY_RECORD)) {

            setCategoryStatementParameters(stmt, category, 1);
            stmt.setInt(7, category.getCategoryID()); // WHERE CategoryID = ?

            logger.debug("Executing update category query for ID: {}", category.getCategoryID());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No category found with ID: {} for update. Update operation resulted in 0 affected rows.", category.getCategoryID());
                throw new DatabaseAccessException("Category with ID " + category.getCategoryID() + " not found for update.");
            } else {
                logger.info("Successfully updated category with ID: {}", category.getCategoryID());
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
     */
    @Override
    public List<Category> findCategoriesByType(CategoryType type) throws DatabaseAccessException { // Renamed method
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
     */
    @Override
    public List<CategoryType> findUniqueCategoryTypes() throws DatabaseAccessException { // Renamed method
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