package com.avaruusstudios.vmdb.model;

import java.time.LocalDateTime;

/**
 * Represents a user within the Vanpool Management Database application.
 * This class maps to the {@code tblUsers} table in the SQLite database,
 * storing information about individual users and their roles within the system.
 */
public class User {
    private int userId;
    private String windowsUsername;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String userRole;
    private boolean isActive;
    private LocalDateTime dateCreated;

    /**
     * Default constructor for the {@code User} class.
     * Initializes a new, empty {@code User} object.
     */
    public User() {
        // Default constructor
    }

    /**
     * Constructs a new {@code User} object with the specified attributes.
     *
     * @param userId          The unique identifier for the user.
     * @param windowsUsername The Windows username of the user (must be unique).
     * @param firstName       The first name of the user.
     * @param middleName      The middle name of the user (optional).
     * @param lastName        The last name of the user.
     * @param email           The email address of the user.
     * @param userRole        The role of the user within the application (e.g., "ADMIN", "TREASURER", "USER").
     * @param isActive        A boolean indicating whether the user account is active.
     * @param dateCreated     The timestamp indicating when the user account was created.
     */
    public User(int userId, String windowsUsername, String firstName, String middleName, String lastName, String email, String userRole, boolean isActive, LocalDateTime dateCreated) {
        this.userId = userId;
        this.windowsUsername = windowsUsername;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.userRole = userRole;
        this.isActive = isActive;
        this.dateCreated = dateCreated;
    }

    /**
     * Gets the unique identifier for the user.
     *
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the unique identifier for the user.
     *
     * @param userId The user ID to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the Windows username of the user. This field is unique and non-null.
     *
     * @return The Windows username.
     */
    public String getWindowsUsername() {
        return windowsUsername;
    }

    /**
     * Sets the Windows username of the user.
     *
     * @param windowsUsername The Windows username to set.
     */
    public void setWindowsUsername(String windowsUsername) {
        this.windowsUsername = windowsUsername;
    }

    /**
     * Gets the first name of the user.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the middle name of the user. This field is optional and can be null.
     *
     * @return The middle name, or null if not present.
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the middle name of the user.
     *
     * @param middleName The middle name to set.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Gets the last name of the user.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email address of the user.
     *
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the role of the user within the application.
     * Examples include "ADMIN", "TREASURER", and "USER".
     *
     * @return The user role.
     */
    public String getUserRole() {
        return userRole;
    }

    /**
     * Sets the role of the user within the application.
     *
     * @param userRole The user role to set.
     */
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    /**
     * Indicates whether the user account is currently active.
     *
     * @return {@code true} if the account is active, {@code false} otherwise.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Sets the active status of the user account.
     *
     * @param active {@code true} to activate the account, {@code false} to deactivate.
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Gets the timestamp indicating when the user account was created.
     *
     * @return The date and time of creation.
     */
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets the timestamp indicating when the user account was created.
     *
     * @param dateCreated The date and time of creation to set.
     */
    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * Returns a string representation of the {@code User} object.
     * This is useful for debugging and logging purposes.
     *
     * @return A string containing the attributes of the user.
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", windowsUsername='" + windowsUsername + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", userRole='" + userRole + '\'' +
                ", isActive=" + isActive +
                ", dateCreated=" + dateCreated +
                '}';
    }
}