package com.avaruusstudios.vmdb.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * Represents a user within the Vanpool Management Database application.
 * This class maps directly to the `Users` table in the SQLite database,
 * storing information about individual users, their authentication details,
 * personal information, roles, and account status.
 * </p>
 *
 * <p>
 * User accounts are identified by a unique ID ({@code UserID INTEGER PRIMARY KEY AUTOINCREMENT})
 * and a unique Windows username ({@code WindowsUsername VARCHAR(255) UNIQUE NOT NULL}).
 * Each user has a defined {@link Role} ({@code UserRole VARCHAR(50) NOT NULL})
 * and an active status ({@code IsActive INTEGER NOT NULL DEFAULT 1}).
 * The class also includes fields for personal details (first, middle, last name, email),
 * a timestamp for logical deletion ({@code DeletedAt TEXT DEFAULT NULL}),
 * and the account creation timestamp ({@code DateCreated TEXT DEFAULT CURRENT_TIMESTAMP}).
 * All properties are exposed as JavaFX Properties for UI binding.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.3
 * Created On: 2025-07-11
 * Updated On: 2025-07-12
 * @see Role
 * @see EventLog
 */
public class User {
    /**
     * Unique identifier for the user. This serves as the primary key
     * in the database for user records ({@code UserID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created user not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable and is exposed as a {@link ReadOnlyObjectProperty}.
     * </p>
     */
    private final ReadOnlyObjectProperty<Integer> userId;
    /**
     * The Windows username associated with the user account.
     * This field is unique and cannot be null (corresponds to {@code WindowsUsername VARCHAR(255) UNIQUE NOT NULL} in the database).
     * It is exposed as a {@link StringProperty}.
     */
    private final StringProperty windowsUsername;
    /**
     * The first name of the user.
     * Corresponds to {@code FirstName VARCHAR(255)} in the database, which is nullable.
     * It is exposed as a {@link StringProperty}.
     */
    private final StringProperty firstName;
    /**
     * The middle name of the user.
     * Corresponds to {@code MiddleName VARCHAR(255)} in the database, which is nullable.
     * It is exposed as a {@link StringProperty}.
     */
    private final StringProperty middleName;
    /**
     * The last name of the user.
     * Corresponds to {@code LastName VARCHAR(255)} in the database, which is nullable.
     * It is exposed as a {@link StringProperty}.
     */
    private final StringProperty lastName;
    /**
     * The email address of the user.
     * Corresponds to {@code Email VARCHAR(255)} in the database, which is nullable.
     * It is exposed as a {@link StringProperty}.
     */
    private final StringProperty email;
    /**
     * The {@link Role} assigned to the user within the application (e.g., ADMIN, TREASURER, USER, COORDINATOR).
     * This field is mandatory and cannot be null (corresponds to {@code UserRole VARCHAR(50) NOT NULL} in the database).
     * It is exposed as an {@link ObjectProperty} of {@link Role}.
     */
    private final ObjectProperty<Role> role;
    /**
     * A boolean flag indicating whether the user account is currently active.
     * Corresponds to {@code IsActive INTEGER NOT NULL DEFAULT 1} in the database (where 1=true, 0=false).
     * It is exposed as a {@link BooleanProperty}.
     */
    private final BooleanProperty isActive;
    /**
     * The timestamp when the user account was logically deleted or deactivated.
     * This field is optional and can be {@code null} if the user is active (corresponds to {@code DeletedAt TEXT DEFAULT NULL} in the database).
     * It is exposed as an {@link ObjectProperty} of {@link LocalDateTime}.
     */
    private final ObjectProperty<LocalDateTime> deletedAt; // Re-introduced based on schema.sql
    /**
     * The timestamp indicating when the user account was created.
     * This field is often automatically set by the database using `CURRENT_TIMESTAMP` (corresponds to {@code DateCreated TEXT DEFAULT CURRENT_TIMESTAMP} in the database).
     * It is exposed as an {@link ObjectProperty} of {@link LocalDateTime}.
     */
    private final ObjectProperty<LocalDateTime> dateCreated;

    /**
     * Default constructor for creating an empty {@code User} object.
     * The {@code userId} is set to {@code null} to explicitly indicate that
     * this user entry has not yet been assigned a unique ID by the database.
     * Initializes other JavaFX properties to their default values (null for ObjectProperties,
     * empty string for StringProperties, true for BooleanProperties like {@code isActive}).
     */
    public User() {
        // Default values for a new, unpersisted user matching schema defaults
        this(null, "", "", "", "", "", Role.USER, true, null, null); // Default role to USER
    }

    /**
     * Full constructor to initialize all fields of a {@code User} instance.
     * This constructor allows for the comprehensive creation of a user record,
     * aligning with the database schema. It is typically used when loading an
     * *existing* user record from the database, where {@code userId} has already been assigned.
     * All mandatory parameters are validated via their respective setters.
     *
     * @param userId          The unique integer ID for the user, typically auto-generated by the database. Can be {@code null} for new users.
     * @param windowsUsername The unique Windows username of the user. Must not be {@code null} or empty.
     * @param firstName       The first name of the user. Can be {@code null}.
     * @param middleName      The middle name of the user. Can be {@code null}.
     * @param lastName        The last name of the user. Can be {@code null}.
     * @param email           The email address of the user. Can be {@code null}.
     * @param role            The {@link Role} of the user. Must not be {@code null}.
     * @param isActive        A boolean indicating whether the user account is active.
     * @param deletedAt       The {@link LocalDateTime} timestamp when the user was logically deleted, or {@code null} if active.
     * @param dateCreated     The {@link LocalDateTime} timestamp indicating when the user account was created. Can be {@code null} if database handles default.
     * @throws IllegalArgumentException if any mandatory argument is invalid (e.g., null, empty, non-positive ID).
     * @throws IllegalStateException    if `userId` is attempted to be changed once set.
     */
    public User(Integer userId, String windowsUsername, String firstName, String middleName, String lastName,
                String email, Role role, boolean isActive, LocalDateTime deletedAt, LocalDateTime dateCreated) {
        this.userId = new SimpleObjectProperty<>(this, "userId", userId);
        this.windowsUsername = new SimpleStringProperty(this, "windowsUsername");
        this.firstName = new SimpleStringProperty(this, "firstName");
        this.middleName = new SimpleStringProperty(this, "middleName");
        this.lastName = new SimpleStringProperty(this, "lastName");
        this.email = new SimpleStringProperty(this, "email");
        this.role = new SimpleObjectProperty<>(this, "role");
        this.isActive = new SimpleBooleanProperty(this, "isActive");
        this.deletedAt = new SimpleObjectProperty<>(this, "deletedAt"); // Initialize new property
        this.dateCreated = new SimpleObjectProperty<>(this, "dateCreated");

        // Use setters to apply validation and business logic
        setWindowsUsername(windowsUsername);
        setFirstName(firstName);
        setMiddleName(middleName);
        setLastName(lastName);
        setEmail(email);
        setRole(role);
        setActive(isActive);
        setDeletedAt(deletedAt); // Set new property
        setDateCreated(dateCreated);
    }

    /**
     * Convenience constructor for creating a new {@code User} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new user record for **insertion** into the database.
     * The {@code userId} is omitted as it is typically auto-generated by the database.
     * The {@code deletedAt} and {@code dateCreated} fields are omitted as they are often
     * managed by database defaults or set upon persistence.
     * All mandatory parameters are validated via their respective setters.
     *
     * @param windowsUsername The unique Windows username of the user. Must not be {@code null} or empty.
     * @param firstName       The first name of the user. Can be {@code null}.
     * @param middleName      The middle name of the user. Can be {@code null}.
     * @param lastName        The last name of the user. Can be {@code null}.
     * @param email           The email address of the user. Can be {@code null}.
     * @param role            The {@link Role} of the user. Must not be {@code null}.
     * @param isActive        A boolean indicating whether the user account is active.
     * @throws IllegalArgumentException if any mandatory argument is invalid (e.g., null, empty).
     */
    public User(String windowsUsername, String firstName, String middleName, String lastName,
                String email, Role role, boolean isActive) {
        // Delegate to the full constructor with null for userId, deletedAt, and dateCreated for a new entity
        this(null, windowsUsername, firstName, middleName, lastName, email, role, isActive, null, null);
    }

    // --- JavaFX Property Accessor Methods ---

    /**
     * Retrieves the {@link ReadOnlyObjectProperty} for the unique identifier of this user.
     * <p>
     * This property represents the {@code UserID} column in the database.
     * Its value is immutable once set (typically by the database).
     * </p>
     *
     * @return The {@link ReadOnlyObjectProperty} for {@code userId}.
     */
    public ReadOnlyObjectProperty<Integer> userIdProperty() {
        return userId;
    }

    /**
     * Retrieves the {@link StringProperty} for the Windows username of the user.
     * This property corresponds to the {@code WindowsUsername} column in the database.
     *
     * @return The {@link StringProperty} for {@code windowsUsername}.
     */
    public StringProperty windowsUsernameProperty() {
        return windowsUsername;
    }

    /**
     * Retrieves the {@link StringProperty} for the first name of the user.
     * This property corresponds to the {@code FirstName} column in the database.
     *
     * @return The {@link StringProperty} for {@code firstName}.
     */
    public StringProperty firstNameProperty() {
        return firstName;
    }

    /**
     * Retrieves the {@link StringProperty} for the middle name of the user.
     * This property corresponds to the {@code MiddleName} column in the database.
     *
     * @return The {@link StringProperty} for {@code middleName}.
     */
    public StringProperty middleNameProperty() {
        return middleName;
    }

    /**
     * Retrieves the {@link StringProperty} for the last name of the user.
     * This property corresponds to the {@code LastName} column in the database.
     *
     * @return The {@link StringProperty} for {@code lastName}.
     */
    public StringProperty lastNameProperty() {
        return lastName;
    }

    /**
     * Retrieves the {@link StringProperty} for the email address of the user.
     * This property corresponds to the {@code Email} column in the database.
     *
     * @return The {@link StringProperty} for {@code email}.
     */
    public StringProperty emailProperty() {
        return email;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the {@link Role} assigned to the user.
     * This property corresponds to the {@code UserRole} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code role}.
     */
    public ObjectProperty<Role> roleProperty() {
        return role;
    }

    /**
     * Retrieves the {@link BooleanProperty} indicating whether the user account is active.
     * This property corresponds to the {@code IsActive} column in the database.
     *
     * @return The {@link BooleanProperty} for {@code isActive}.
     */
    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the deletion timestamp of the user.
     * This property corresponds to the {@code DeletedAt} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code deletedAt}.
     */
    public ObjectProperty<LocalDateTime> deletedAtProperty() {
        return deletedAt;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the timestamp indicating when the user account was created.
     * This property corresponds to the {@code DateCreated} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code dateCreated}.
     */
    public ObjectProperty<LocalDateTime> dateCreatedProperty() {
        return dateCreated;
    }

    // --- Value Getters and Setters ---

    /**
     * Retrieves the unique identifier for the user.
     * For new, unpersisted entries, this will be {@code null}.
     * Corresponds to the {@code UserID} column in the database.
     *
     * @return The {@link Integer} primary key used to identify this user record, or {@code null} if not yet assigned.
     */
    public Integer getUserId() {
        return userId.get();
    }

    /**
     * Sets the unique ID for this user. This method is designed to be package-private
     * and is primarily for use by data access objects (DAOs) when an ID is generated
     * by the database upon insertion.
     * <p>
     * It includes a check to prevent the ID from being modified once it has been set,
     * ensuring the immutability of the primary key.
     * </p>
     *
     * @param id The unique integer ID assigned by the database.
     * @throws IllegalStateException    if the ID has already been assigned to this object.
     * @throws IllegalArgumentException if the provided ID is {@code null} or non-positive.
     */
    void _setUserId(Integer id) { // Package-private for DAO use only
        if (this.userId.get() != null) {
            throw new IllegalStateException("User ID cannot be changed once set.");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID cannot be null or non-positive.");
        }
        ((SimpleObjectProperty<Integer>) this.userId).set(id);
    }

    /**
     * Retrieves the Windows username of the user.
     * Corresponds to the {@code WindowsUsername} column in the database.
     *
     * @return The Windows username. Will not be {@code null} or empty after trimming, as per schema.
     */
    public String getWindowsUsername() {
        return windowsUsername.get();
    }

    /**
     * Sets the Windows username of the user.
     * <p>
     * Leading and trailing whitespace will be trimmed.
     * </p>
     *
     * @param windowsUsername The Windows username to set. Must not be {@code null} or empty after trimming.
     * @throws IllegalArgumentException if {@code windowsUsername} is {@code null} or empty after trimming.
     */
    public void setWindowsUsername(String windowsUsername) {
        if (windowsUsername == null || windowsUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Windows username cannot be null or empty.");
        }
        this.windowsUsername.set(windowsUsername.trim());
    }

    /**
     * Retrieves the first name of the user.
     * Corresponds to the {@code FirstName} column in the database.
     *
     * @return The first name, or {@code null} if not specified.
     */
    public String getFirstName() {
        return firstName.get();
    }

    /**
     * Sets the first name of the user.
     * If the provided first name is not {@code null}, leading and trailing whitespace will be stripped.
     *
     * @param firstName The first name to set. Can be {@code null}.
     */
    public void setFirstName(String firstName) {
        this.firstName.set((firstName != null) ? firstName.trim() : null);
    }

    /**
     * Retrieves the middle name of the user.
     * Corresponds to the {@code MiddleName} column in the database.
     *
     * @return The middle name, or {@code null} if not present.
     */
    public String getMiddleName() {
        return middleName.get();
    }

    /**
     * Sets the middle name of the user.
     * If the provided middle name is not {@code null}, leading and trailing whitespace will be stripped.
     *
     * @param middleName The middle name to set. Can be {@code null}.
     */
    public void setMiddleName(String middleName) {
        this.middleName.set((middleName != null) ? middleName.trim() : null);
    }

    /**
     * Retrieves the last name of the user.
     * Corresponds to the {@code LastName} column in the database.
     *
     * @return The last name, or {@code null} if not specified.
     */
    public String getLastName() {
        return lastName.get();
    }

    /**
     * Sets the last name of the user.
     * If the provided last name is not {@code null}, leading and trailing whitespace will be stripped.
     *
     * @param lastName The last name to set. Can be {@code null}.
     */
    public void setLastName(String lastName) {
        this.lastName.set((lastName != null) ? lastName.trim() : null);
    }

    /**
     * Retrieves the email address of the user.
     * Corresponds to the {@code Email} column in the database.
     *
     * @return The email address, or {@code null} if not specified.
     */
    public String getEmail() {
        return email.get();
    }

    /**
     * Sets the email address of the user.
     * If the provided email is not {@code null}, leading and trailing whitespace will be stripped.
     *
     * @param email The email address to set. Can be {@code null}.
     */
    public void setEmail(String email) {
        this.email.set((email != null) ? email.trim() : null);
    }

    /**
     * Retrieves the {@link Role} of the user within the application.
     * Corresponds to the {@code UserRole} column in the database.
     *
     * @return The {@link Role} enum constant. Will not be {@code null} as per schema.
     */
    public Role getRole() {
        return role.get();
    }

    /**
     * Sets the {@link Role} of the user within the application.
     *
     * @param role The {@link Role} to set. Must not be {@code null} as per schema.
     * @throws IllegalArgumentException if {@code role} is {@code null}.
     */
    public void setRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null.");
        }
        this.role.set(role);
    }

    /**
     * Checks if the user account is currently active.
     * Corresponds to the {@code IsActive} column in the database.
     *
     * @return {@code true} if the account is active, {@code false} otherwise.
     */
    public boolean isActive() {
        return isActive.get();
    }

    /**
     * Sets the active status of the user account.
     *
     * @param active {@code true} to activate the account, {@code false} to deactivate.
     */
    public void setActive(boolean active) {
        this.isActive.set(active);
    }

    /**
     * Retrieves the timestamp when the user account was logically deleted or deactivated.
     * Corresponds to the {@code DeletedAt} column in the database.
     *
     * @return The {@link LocalDateTime} of deletion, or {@code null} if the user is active.
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt.get();
    }

    /**
     * Sets the timestamp when the user account was logically deleted or deactivated.
     *
     * @param deletedAt The {@link LocalDateTime} to set as the deletion timestamp. Can be {@code null}.
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt.set(deletedAt);
    }

    /**
     * Retrieves the timestamp indicating when the user account was created.
     * Corresponds to the {@code DateCreated} column in the database.
     *
     * @return The creation date and time, or {@code null} if not set (e.g., by DB default).
     */
    public LocalDateTime getDateCreated() {
        return dateCreated.get();
    }

    /**
     * Sets the timestamp indicating when the user account was created.
     *
     * @param dateCreated The creation date and time to set. Can be {@code null}.
     */
    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated.set(dateCreated);
    }

    // --- Utility Methods ---

    /**
     * <p>
     * Returns a string representation of the {@code User} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the user's key attributes.
     * </p>
     * <p>
     * The format includes the user ID, Windows username, full name, email,
     * user role, active status, deletion timestamp, and creation timestamp.
     * </p>
     *
     * @return A string in the format:
     * "User{ID=..., Username='...', FirstName='...', MiddleName='...', LastName='...', Email='...', Role=..., Active=..., DeletedAt=..., DateCreated=...}"
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + getUserId() +
                ", windowsUsername='" + getWindowsUsername() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", middleName='" + (getMiddleName() != null ? getMiddleName() : "null") + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + (getEmail() != null ? getEmail() : "null") + '\'' +
                ", role=" + getRole() +
                ", isActive=" + isActive() +
                ", deletedAt=" + getDeletedAt() + // Added back
                ", dateCreated=" + (getDateCreated() != null ? getDateCreated() : "null") +
                '}';
    }

    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is primarily based on the unique {@code userId}.
     * </p>
     * <p>
     * This method adheres to the general contract of the {@link Object#equals(Object)} method,
     * ensuring consistency with hash-based collections. It correctly handles cases where
     * {@code userId} might be {@code null} for unpersisted entities.
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // Equality is based on the primary key (userId), safely handling null Integer via getter
        return Objects.equals(getUserId(), user.getUserId());
    }

    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap} and {@link java.util.HashSet}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code userId}. If {@code userId}
     * is {@code null} (for unpersisted entities), its hash code will be 0, as per {@link Objects#hash(Object...)}.
     * This ensures that objects considered equal by {@code equals} will have the same hash code,
     * fulfilling the contract between {@code equals} and {@code hashCode}.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }
}