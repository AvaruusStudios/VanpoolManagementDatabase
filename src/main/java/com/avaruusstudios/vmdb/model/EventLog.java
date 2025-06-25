package com.avaruusstudios.vmdb.model;

import javafx.beans.property.*; // Import JavaFX property classes
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * Represents a log entry capturing significant system events within the Vanpool Management System.
 * These logs are crucial for auditing, debugging, and understanding system activity.
 * </p>
 *
 * <p>
 * Each log entry records who performed an action (via a {@link User} reference),
 * when it occurred, the type of event (e.g., CREATE, READ, UPDATE, DELETE, ERROR),
 * the affected database table and optionally a specific record ID within that table.
 * It also captures application-specific error codes and a general description of the event.
 * All properties are exposed as JavaFX Properties for UI binding.
 * </p>
 *
 * @see User
 * @see EventType
 * @see ErrorCode
 */
public class EventLog {
    /**
     * Unique identifier for the event log entry. This serves as the primary key
     * in the database ({@code EventID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created event log not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable and is exposed as a {@link ReadOnlyObjectProperty}.
     * </p>
     */
    private final ReadOnlyObjectProperty<Integer> eventID;
    /**
     * The {@link User} who initiated or is associated with this event.
     * This field is mandatory and cannot be null.
     * Corresponds to {@code UserID_FK INTEGER NOT NULL} in the database.
     * It is exposed as an {@link ObjectProperty} of {@link User}.
     */
    private final ObjectProperty<User> user;
    /**
     * The date and time when the event occurred.
     * Corresponds to {@code EventDate TEXT NOT NULL} in the database.
     * It is exposed as an {@link ObjectProperty} of {@link LocalDateTime}.
     */
    private final ObjectProperty<LocalDateTime> eventDate;
    /**
     * The {@link EventType} indicating the nature of the event (e.g., CREATE, READ, UPDATE, DELETE, ERROR).
     * This field is mandatory and cannot be null.
     * Corresponds to {@code EventType TEXT NOT NULL} in the database.
     * It is exposed as an {@link ObjectProperty} of {@link EventType}.
     */
    private final ObjectProperty<EventType> eventType;
    /**
     * The name of the database table where the event occurred or which was affected.
     * This field is mandatory and cannot be null.
     * Corresponds to {@code TableName TEXT NOT NULL} in the database.
     * It is exposed as a {@link StringProperty}.
     */
    private final StringProperty tableName;
    /**
     * The ID of the specific record within the {@code tableName} that was affected by the event.
     * This field is optional and can be null if the event doesn't pertain to a single record (e.g., an error during a batch operation).
     * Corresponds to {@code RecordID INTEGER} in the database.
     * It is exposed as an {@link ObjectProperty} of {@link Integer}.
     */
    private final ObjectProperty<Integer> recordID;
    /**
     * An optional {@link ErrorCode} associated with the event, particularly relevant for `ERROR` type events.
     * Can be null if no specific error code applies.
     * Corresponds to {@code ErrorCode INTEGER} in the database, storing {@link ErrorCode#getValue()}.
     * It is exposed as an {@link ObjectProperty} of {@link ErrorCode}.
     */
    private final ObjectProperty<ErrorCode> errorCode;
    /**
     * A detailed description of the event, providing more context or specifics.
     * This field is optional and can be null.
     * Corresponds to {@code Description TEXT} in the database.
     * It is exposed as a {@link StringProperty}.
     */
    private final StringProperty description;

    /**
     * Default constructor for creating an empty {@code EventLog} object.
     * The {@code eventID} is set to {@code null} to explicitly indicate that
     * this log entry has not yet been assigned a unique ID by the database.
     * Initializes other JavaFX properties to their default values (null for ObjectProperties, empty string for StringProperties).
     * This constructor is primarily used by frameworks that instantiate objects
     * via reflection (e.g., ORMs, JSON deserializers) before populating their fields.
     */
    public EventLog() {
        this.eventID = new SimpleObjectProperty<>(this, "eventID", null);
        this.user = new SimpleObjectProperty<>(this, "user");
        this.eventDate = new SimpleObjectProperty<>(this, "eventDate");
        this.eventType = new SimpleObjectProperty<>(this, "eventType");
        this.tableName = new SimpleStringProperty(this, "tableName");
        this.recordID = new SimpleObjectProperty<>(this, "recordID");
        this.errorCode = new SimpleObjectProperty<>(this, "errorCode");
        this.description = new SimpleStringProperty(this, "description");
    }
    /**
     * Full constructor to initialize all fields of an {@code EventLog} instance.
     * This constructor allows for the comprehensive creation of an event log record,
     * aligning with the database schema and established object relationships.
     * This constructor is typically used when loading an *existing* event log
     * record from the database, where {@code eventID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param eventID     The unique integer ID for the event, typically assigned by the database. Must not be {@code null}.
     * @param user        The {@link User} associated with this event. Must not be {@code null}.
     * @param eventDate   The {@link LocalDateTime} when the event occurred. Must not be {@code null}.
     * @param eventType   The {@link EventType} of the event. Must not be {@code null}.
     * @param tableName   The name of the affected database table. Must not be {@code null}.
     * @param recordID    The ID of the affected record, or {@code null} if not applicable.
     * @param errorCode   An optional {@link ErrorCode}, or {@code null}.
     * @param description A detailed description of the event. Can be {@code null}.
     * @throws NullPointerException if `eventID`, `user`, `eventDate`, `eventType`, or `tableName` are {@code null}.
     */
    public EventLog(Integer eventID, User user, LocalDateTime eventDate, EventType eventType,
                    String tableName, Integer recordID, ErrorCode errorCode, String description) {
        this.eventID = new SimpleObjectProperty<>(this, "eventID", Objects.requireNonNull(eventID, "Event ID cannot be null for an existing log entry."));
        this.user = new SimpleObjectProperty<>(this, "user");
        this.eventDate = new SimpleObjectProperty<>(this, "eventDate");
        this.eventType = new SimpleObjectProperty<>(this, "eventType");
        this.tableName = new SimpleStringProperty(this, "tableName");
        this.recordID = new SimpleObjectProperty<>(this, "recordID");
        this.errorCode = new SimpleObjectProperty<>(this, "errorCode");
        this.description = new SimpleStringProperty(this, "description");

        // Use setters to apply validation and business logic
        setUser(user);
        setEventDate(eventDate);
        setEventType(eventType);
        setTableName(tableName);
        setRecordID(recordID);
        setErrorCode(errorCode);
        setDescription(description);
    }
    /**
     * Convenience constructor for creating a new {@code EventLog} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new event log record for **insertion** into the database.
     * The {@code eventID} is omitted as it is typically auto-generated by the database.
     * All parameters are validated via their respective setters.
     *
     * @param user        The {@link User} associated with this event. Must not be {@code null}.
     * @param eventDate   The {@link LocalDateTime} when the event occurred. Must not be {@code null}.
     * @param eventType   The {@link EventType} of the event. Must not be {@code null}.
     * @param tableName   The name of the affected database table. Must not be {@code null}.
     * @param recordID    The ID of the affected record, or {@code null} if not applicable.
     * @param errorCode   An optional {@link ErrorCode}, or {@code null}.
     * @param description A detailed description of the event. Can be {@code null}.
     * @throws NullPointerException if `user`, `eventDate`, `eventType`, or `tableName` are {@code null}.
     */
    public EventLog(User user, LocalDateTime eventDate, EventType eventType,
                    String tableName, Integer recordID, ErrorCode errorCode, String description) {
        // Delegate to the full constructor with null for eventID for a new entity
        this(null, user, eventDate, eventType, tableName, recordID, errorCode, description);
    }

    // ------------------------------------
    // JavaFX Property Accessor Methods
    // ------------------------------------

    /**
     * Retrieves the {@link ReadOnlyObjectProperty} for the unique identifier of this event log entry.
     * <p>
     * This property represents the {@code EventID} column in the database.
     * Its value is immutable once set (typically by the database).
     * </p>
     *
     * @return The {@link ReadOnlyObjectProperty} for {@code eventID}.
     */
    public ReadOnlyObjectProperty<Integer> eventIDProperty() {
        return eventID;
    }
    /**
     * Retrieves the {@link ObjectProperty} for the {@link User} who initiated or is associated with this event.
     *
     * @return The {@link ObjectProperty} for {@code user}.
     */
    public ObjectProperty<User> userProperty() {
        return user;
    }
    /**
     * Retrieves the {@link ObjectProperty} for the date and time when the event occurred.
     *
     * @return The {@link ObjectProperty} for {@code eventDate}.
     */
    public ObjectProperty<LocalDateTime> eventDateProperty() {
        return eventDate;
    }
    /**
     * Retrieves the {@link ObjectProperty} for the {@link EventType} indicating the nature of the event.
     *
     * @return The {@link ObjectProperty} for {@code eventType}.
     */
    public ObjectProperty<EventType> eventTypeProperty() {
        return eventType;
    }
    /**
     * Retrieves the {@link StringProperty} for the name of the database table affected by the event.
     *
     * @return The {@link StringProperty} for {@code tableName}.
     */
    public StringProperty tableNameProperty() {
        return tableName;
    }
    /**
     * Retrieves the {@link ObjectProperty} for the ID of the specific record within the affected table.
     *
     * @return The {@link ObjectProperty} for {@code recordID}.
     */
    public ObjectProperty<Integer> recordIDProperty() {
        return recordID;
    }
    /**
     * Retrieves the {@link ObjectProperty} for an optional {@link ErrorCode} associated with the event.
     *
     * @return The {@link ObjectProperty} for {@code errorCode}.
     */
    public ObjectProperty<ErrorCode> errorCodeProperty() {
        return errorCode;
    }
    /**
     * Retrieves the {@link StringProperty} for a detailed description of the event.
     *
     * @return The {@link StringProperty} for {@code description}.
     */
    public StringProperty descriptionProperty() {
        return description;
    }

    // ---------------------
    // Value Getters and Setters
    // ---------------------

    /**
     * Retrieves the unique identifier for this event log entry.
     * For new, unpersisted entries, this will be {@code null}.
     * Corresponds to the {@code EventID} column in the database.
     *
     * @return The {@link Integer} primary key used to identify this event log record, or {@code null} if not yet assigned.
     */
    public Integer getEventID() {
        return eventID.get();
    }
    /**
     * Sets the unique ID for this event log entry. This method is designed to be package-private
     * and is primarily for use by data access objects (DAOs) when an ID is generated
     * by the database upon insertion.
     * <p>
     * It includes a check to prevent the ID from being modified once it has been set,
     * ensuring the immutability of the primary key.
     * </p>
     *
     * @param id The unique integer ID assigned by the database.
     * @throws IllegalStateException if the ID has already been assigned to this object.
     * @throws IllegalArgumentException if the provided ID is {@code null} or non-positive.
     */
    void _setEventID(Integer id) { // Package-private for DAO use only
        if (this.eventID.get() != null) {
            throw new IllegalStateException("Event ID cannot be changed once set.");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Event ID cannot be null or non-positive.");
        }
        ((SimpleObjectProperty<Integer>) this.eventID).set(id);
    }
    /**
     * Retrieves the {@link User} who initiated or is associated with this event.
     * Corresponds to the {@code UserID_FK} column in the database.
     *
     * @return The associated {@link User} object.
     */
    public User getUser() {
        return user.get();
    }
    /**
     * Sets the {@link User} who initiated or is associated with this event.
     *
     * @param user The {@link User} object to set. Must not be {@code null}.
     * @throws NullPointerException if {@code user} is {@code null}.
     */
    public void setUser(User user) {
        this.user.set(Objects.requireNonNull(user, "User cannot be null for an event log entry."));
    }
    /**
     * Retrieves the date and time when this event occurred.
     * Corresponds to the {@code EventDate} column in the database.
     *
     * @return The event date and time.
     */
    public LocalDateTime getEventDate() {
        return eventDate.get();
    }
    /**
     * Sets the date and time when this event occurred.
     *
     * @param eventDate The event date and time to set. Must not be {@code null}.
     * @throws NullPointerException if {@code eventDate} is {@code null}.
     */
    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate.set(Objects.requireNonNull(eventDate, "Event date cannot be null."));
    }
    /**
     * Retrieves the {@link EventType} of this event.
     * Corresponds to the {@code EventType} column in the database.
     *
     * @return The {@link EventType} enum constant (CREATE, READ, UPDATE, DELETE, or ERROR).
     */
    public EventType getEventType() {
        return eventType.get();
    }
    /**
     * Sets the {@link EventType} of this event.
     *
     * @param eventType The {@link EventType} to set. Must not be {@code null}.
     * @throws NullPointerException if {@code eventType} is {@code null}.
     */
    public void setEventType(EventType eventType) {
        this.eventType.set(Objects.requireNonNull(eventType, "Event type cannot be null."));
    }
    /**
     * Retrieves the name of the database table affected by this event.
     * Corresponds to the {@code TableName} column in the database.
     *
     * @return The name of the affected table.
     */
    public String getTableName() {
        return tableName.get();
    }
    /**
     * Sets the name of the database table affected by this event.
     * <p>
     * Leading and trailing whitespace will be trimmed.
     * </p>
     *
     * @param tableName The table name to set. Must not be {@code null}.
     * @throws NullPointerException if {@code tableName} is {@code null}.
     * @throws IllegalArgumentException if the trimmed table name is empty.
     */
    public void setTableName(String tableName) {
        String trimmedName = Objects.requireNonNull(tableName, "Table name cannot be null.").strip();
        if (trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be empty after trimming.");
        }
        this.tableName.set(trimmedName);
    }
    /**
     * Retrieves the ID of the specific record within the affected table.
     * Corresponds to the {@code RecordID} column in the database.
     *
     * @return The record ID, or {@code null} if not applicable.
     */
    public Integer getRecordID() {
        return recordID.get();
    }
    /**
     * Sets the ID of the specific record within the affected table.
     *
     * @param recordID The record ID to set. Can be {@code null}.
     */
    public void setRecordID(Integer recordID) {
        this.recordID.set(recordID);
    }
    /**
     * Retrieves an optional application error code associated with the event.
     * Corresponds to the {@code ErrorCode} column in the database.
     *
     * @return The {@link ErrorCode}, or {@code null} if no specific error code applies.
     */
    public ErrorCode getErrorCode() {
        return errorCode.get();
    }
    /**
     * Sets an optional application error code associated with the event.
     *
     * @param errorCode The {@link ErrorCode} to set. Can be {@code null}.
     */
    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode.set(errorCode);
    }
    /**
     * Retrieves the detailed description of the event.
     * Corresponds to the {@code Description} column in the database.
     *
     * @return The event description, or {@code null} if no description is present.
     */
    public String getDescription() {
        return description.get();
    }
    /**
     * Sets a detailed description for this event.
     * If the provided description is not {@code null}, leading and trailing whitespace will be stripped.
     *
     * @param description The description to set. Can be {@code null}.
     */
    public void setDescription(String description) {
        this.description.set((description != null) ? description.strip() : null);
    }

    // ---------------------
    // Utility Methods
    // ---------------------

    /**
     * <p>
     * Returns a string representation of the {@code EventLog} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the event's key attributes.
     * </p>
     * <p>
     * The format includes the event ID, associated user's ID, event date,
     * type, affected table, and optionally record ID and error code.
     * </p>
     *
     * @return A string in the format:
     * "EventLog{ID=..., UserID=..., Date=..., Type=..., Table='...', RecordID=..., ErrorCode=...}"
     */
    @Override
    public String toString() {
        return "EventLog{" +
                "eventID=" + getEventID() +
                ", userID=" + (getUser() != null ? getUser().getUserId() : "null") +
                ", eventDate=" + getEventDate() +
                ", eventType=" + getEventType() +
                ", tableName='" + getTableName() + '\'' +
                ", recordID=" + getRecordID() +
                ", errorCode=" + (getErrorCode() != null ? getErrorCode().getValue() : "null") + // Uses ErrorCode's integer value via getter
                '}';
    }
    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based primarily on the unique {@code eventID}.
     * </p>
     * <p>
     * This method adheres to the general contract of the {@link Object#equals(Object)} method,
     * ensuring consistency with hash-based collections. It correctly handles cases where
     * {@code eventID} might be {@code null} for unpersisted entities.
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventLog eventLog = (EventLog) o;
        // Equality is based on the primary key (eventID), safely handling null Integer via getter
        return Objects.equals(getEventID(), eventLog.getEventID());
    }
    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap} and {@link java.util.HashSet}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code eventID}. If {@code eventID}
     * is {@code null} (for unpersisted entities), its hash code will be 0, as per {@link Objects#hash(Object...)}.
     * This ensures that objects considered equal by {@code equals} will have the same hash code,
     * fulfilling the contract between {@code equals} and {@code hashCode}.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getEventID());
    }
}