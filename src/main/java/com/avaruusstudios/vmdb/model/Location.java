package com.avaruusstudios.vmdb.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime; // New import for deletedAt
import java.util.Objects;

/**
 * <p>
 * Represents a physical location within the Vanpool Management System.
 * This class stores geographical and descriptive details for points of interest
 * such as pickup/drop-off locations, office addresses, or maintenance facilities.
 * </p>
 *
 * <p>
 * Each location is uniquely identified ({@code LocationID INTEGER PRIMARY KEY AUTOINCREMENT})
 * and its properties are designed to support data binding with JavaFX UI components, making it suitable for a responsive
 * desktop application. This class directly maps to the `Locations` table in the SQLite database,
 * including its name ({@code LocationName TEXT NOT NULL}), address ({@code Address TEXT NOT NULL}),
 * city ({@code City TEXT NOT NULL}), state ({@code State TEXT NOT NULL}), zip code ({@code ZipCode TEXT NOT NULL}),
 * optional geographic coordinates ({@code Latitude REAL}, {@code Longitude REAL}),
 * an active status flag ({@code IsActive INTEGER NOT NULL DEFAULT 1}),
 * a timestamp for logical deletion ({@code DeletedAt TEXT DEFAULT NULL}),
 * and any contextual notes ({@code Notes TEXT}).
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.1
 * Created On: 2025-07-11
 * Updated On: 2025-07-12
 * @see Vehicle
 * @see Participant
 */
public class Location {
    /**
     * The unique numerical identifier for the location. This serves as the primary key
     * in the database for location records ({@code LocationID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created location not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable.
     * </p>
     */
    private final ReadOnlyObjectProperty<Integer> locationID;
    /**
     * The human-readable and descriptive name for the location (e.g., "Main Office", "Park & Ride Lot A").
     * This field is **required** (corresponds to {@code LocationName TEXT NOT NULL} in the database).
     */
    private final StringProperty locationName;
    /**
     * The complete street address of the location (e.g., "123 Main St").
     * This field is **required** (corresponds to {@code Address TEXT NOT NULL} in the database).
     */
    private final StringProperty address;
    /**
     * The city where the location is geographically situated (e.g., "Springfield").
     * This field is **required** (corresponds to {@code City TEXT NOT NULL} in the database).
     */
    private final StringProperty city;
    /**
     * The abbreviation for the state or province where the location is situated (e.g., "CA", "NY").
     * This field is **required** (corresponds to {@code State TEXT NOT NULL} in the database).
     */
    private final StringProperty state;
    /**
     * The postal zip code for this location (e.g., "90210", "01234").
     * This field is **required** (corresponds to {@code ZipCode TEXT NOT NULL} in the database).
     */
    private final StringProperty zipCode;
    /**
     * The geographical latitude coordinate of the location as a {@link Double}.
     * This field is optional (corresponds to {@code Latitude REAL} in the database).
     * <p>
     * Latitude values range from -90.0 (South Pole) to +90.0 (North Pole).
     * </p>
     */
    private final ObjectProperty<Double> latitude;
    /**
     * The geographical longitude coordinate of the location as a {@link Double}.
     * This field is optional (corresponds to {@code Longitude REAL} in the database).
     * <p>
     * Longitude values range from -180.0 to +180.0.
     * </p>
     */
    private final ObjectProperty<Double> longitude;
    /**
     * Indicates whether the location is currently active or has been logically deleted/deactivated.
     * (corresponds to {@code IsActive INTEGER NOT NULL DEFAULT 1} in the database).
     * `true` (1) for active, `false` (0) for inactive.
     */
    private final BooleanProperty isActive;
    /**
     * The timestamp when the location was logically deleted or deactivated.
     * This field is optional and can be {@code null} if the location is active.
     * Corresponds to {@code DeletedAt TEXT DEFAULT NULL} in the database.
     */
    private final ObjectProperty<LocalDateTime> deletedAt; // New field from schema
    /**
     * Optional free-form text for additional notes or administrative comments specific to this location.
     * (corresponds to {@code Notes TEXT} in the database).
     */
    private final StringProperty notes;


    /**
     * Default constructor for creating a new, unpersisted {@code Location} object.
     * Initializes properties with default values (null for ID, empty strings for text, null for coordinates,
     * `isActive` as true, `deletedAt` as null, `notes` as empty string).
     * The {@code locationID} is set to {@code null} to explicitly indicate that
     * this location has not yet been assigned a unique ID by the database.
     * This constructor is primarily used by frameworks that instantiate objects
     * via reflection (e.g., ORMs, JSON deserializers) before populating their fields.
     */
    public Location() {
        // Default to active, notes as empty string, deletedAt as null
        this(null, "", "", "", "", "", null, null, true, null, "");
    }

    /**
     * Full constructor to initialize all fields of a {@code Location} instance.
     * This constructor is typically used when loading an *existing* location
     * record from the database, where {@code locationID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param locationID    Unique integer ID for this location, typically assigned by the database. Must not be {@code null} for existing locations.
     * @param locationName  A descriptive, human-readable name for the location. Must not be null or empty.
     * @param address       The complete street address. Must not be null or empty.
     * @param city          The city where the location is located. Must not be null or empty.
     * @param state         The state abbreviation where the location is located. Must not be null or empty.
     * @param zipCode       The postal zip code for the location. Must not be null or empty.
     * @param latitude      The geographical latitude coordinate. Can be {@code null}.
     * @param longitude     The geographical longitude coordinate. Can be {@code null}.
     * @param isActive      The active status of the location (true for active, false for inactive/deleted).
     * @param deletedAt     The {@link LocalDateTime} when the location was logically deleted, or {@code null} if active.
     * @param notes         Optional notes or a detailed description. Can be {@code null}.
     *
     * @throws IllegalArgumentException if any mandatory string fields are empty/null or coordinates are out of valid range.
     * @throws IllegalStateException    if `locationID` is attempted to be changed once set.
     */
    public Location(Integer locationID, String locationName, String address, String city, String state, String zipCode,
                    Double latitude, Double longitude, Boolean isActive, LocalDateTime deletedAt, String notes) {
        this.locationID = new SimpleObjectProperty<>(this, "locationID", locationID);
        this.locationName = new SimpleStringProperty(this, "locationName");
        this.address = new SimpleStringProperty(this, "address");
        this.city = new SimpleStringProperty(this, "city");
        this.state = new SimpleStringProperty(this, "state");
        this.zipCode = new SimpleStringProperty(this, "zipCode");
        this.latitude = new SimpleObjectProperty<>(this, "latitude");
        this.longitude = new SimpleObjectProperty<>(this, "longitude");
        this.isActive = new SimpleBooleanProperty(this, "isActive");
        this.deletedAt = new SimpleObjectProperty<>(this, "deletedAt"); // Initialize new property
        this.notes = new SimpleStringProperty(this, "notes");


        setLocationName(locationName);
        setAddress(address);
        setCity(city);
        setState(state);
        setZipCode(zipCode);
        setLatitude(latitude);
        setLongitude(longitude);
        setIsActive(isActive);
        setDeletedAt(deletedAt); // Set new property
        setNotes(notes);
    }

    /**
     * Convenience constructor for creating a new {@code Location} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new location record for **insertion** into the database.
     * The {@code locationID} is omitted as it is typically auto-generated by the database.
     * All parameters are validated via their respective setters.
     *
     * @param locationName  A descriptive, human-readable name for the location. Must not be null or empty.
     * @param address       The complete street address. Must not be null or empty.
     * @param city          The city where the location is located. Must not be null or empty.
     * @param state         The state abbreviation where the location is located. Must not be null or empty.
     * @param zipCode       The postal zip code for the location. Must not be null or empty.
     * @param latitude      The geographical latitude coordinate. Can be {@code null}.
     * @param longitude     The geographical longitude coordinate. Can be {@code null}.
     * @param notes         Optional notes or a detailed description. Can be {@code null}.
     *
     * @throws IllegalArgumentException if any mandatory string fields are empty/null or coordinates are out of valid range.
     */
    public Location(String locationName, String address, String city, String state, String zipCode, Double latitude, Double longitude, String notes) {
        // Default to active for new instances, and deletedAt as null
        this(null, locationName, address, city, state, zipCode, latitude, longitude, true, null, notes);
    }

    // --- JavaFX Property Accessors ---

    /**
     * Retrieves the read-only property for the location's unique ID.
     * This property represents the {@code LocationID} column in the database.
     * <p>
     * As this property is {@code ReadOnlyObjectProperty}, its value cannot be
     * changed directly after initial assignment, enforcing the immutability
     * of the primary key for persisted entities.
     * </p>
     *
     * @return The {@link ReadOnlyObjectProperty} for {@code locationID}.
     */
    public ReadOnlyObjectProperty<Integer> locationIDProperty() {
        return locationID;
    }

    /**
     * Retrieves the {@link StringProperty} for the location's human-readable name.
     * This property corresponds to the {@code LocationName} column in the database.
     *
     * @return The {@link StringProperty} for {@code locationName}.
     */
    public StringProperty locationNameProperty() {
        return locationName;
    }

    /**
     * Retrieves the {@link StringProperty} for the location's street address.
     * This property corresponds to the {@code Address} column in the database.
     *
     * @return The {@link StringProperty} for {@code address}.
     */
    public StringProperty addressProperty() {
        return address;
    }

    /**
     * Retrieves the {@link StringProperty} for the location's city.
     * This property corresponds to the {@code City} column in the database.
     *
     * @return The {@link StringProperty} for {@code city}.
     */
    public StringProperty cityProperty() {
        return city;
    }

    /**
     * Retrieves the {@link StringProperty} for the location's state or province abbreviation.
     * This property corresponds to the {@code State} column in the database.
     *
     * @return The {@link StringProperty} for {@code state}.
     */
    public StringProperty stateProperty() {
        return state;
    }

    /**
     * Retrieves the {@link StringProperty} for the location's postal zip code.
     * This property corresponds to the {@code ZipCode} column in the database.
     *
     * @return The {@link StringProperty} for {@code zipCode}.
     */
    public StringProperty zipCodeProperty() {
        return zipCode;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the location's geographical latitude.
     * This property holds a {@link Double} value and corresponds to the {@code Latitude} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code latitude}.
     */
    public ObjectProperty<Double> latitudeProperty() {
        return latitude;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the location's geographical longitude.
     * This property holds a {@link Double} value and corresponds to the {@code Longitude} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code longitude}.
     */
    public ObjectProperty<Double> longitudeProperty() {
        return longitude;
    }

    /**
     * Retrieves the {@link BooleanProperty} for the active status of the location.
     * This property corresponds to the {@code IsActive} column in the database.
     *
     * @return The {@link BooleanProperty} for {@code isActive}.
     */
    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    /**
     * Retrieves the {@link ObjectProperty} for the deletion timestamp of the location.
     * This property corresponds to the {@code DeletedAt} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code deletedAt}.
     */
    public ObjectProperty<LocalDateTime> deletedAtProperty() { // New property accessor
        return deletedAt;
    }

    /**
     * Retrieves the {@link StringProperty} for any additional notes pertaining to the location.
     * This property corresponds to the {@code Notes} column in the database.
     *
     * @return The {@link StringProperty} for {@code notes}.
     */
    public StringProperty notesProperty() {
        return notes;
    }

    // --- Value Getters and Setters ---

    /**
     * Retrieves the unique ID of the location.
     * <p>
     * For new, unpersisted locations, this will be {@code null}.
     * Once assigned by the database, this ID should not be changed.
     * </p>
     *
     * @return The {@link Integer} primary key of the location, or {@code null} if not yet persisted.
     */
    public Integer getLocationID() {
        return locationID.get();
    }

    /**
     * Sets the unique ID for this location. This method is designed to be package-private
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
    void _setLocationID(Integer id) { // Package-private for DAO use only
        if (this.locationID.get() != null) {
            throw new IllegalStateException("Location ID cannot be changed once set.");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Location ID cannot be null or non-positive.");
        }
        ((SimpleObjectProperty<Integer>) this.locationID).set(id);
    }

    /**
     * Retrieves the human-readable name of the location.
     * Corresponds to the {@code LocationName} column in the database.
     *
     * @return The location name as a {@link String}.
     */
    public String getLocationName() {
        return locationName.get();
    }

    /**
     * Sets the human-readable name of the location.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is mandatory and cannot be set to {@code null} or an empty string.
     * </p>
     *
     * @param locationName The name to set.
     * @throws IllegalArgumentException if the provided name is {@code null} or empty after trimming.
     */
    public void setLocationName(String locationName) {
        String trimmedName = (locationName == null) ? null : locationName.trim();
        if (trimmedName == null || trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Location name cannot be null or empty.");
        }
        this.locationName.set(trimmedName);
    }

    /**
     * Retrieves the street address of the location.
     * Corresponds to the {@code Address} column in the database.
     *
     * @return The street address as a {@link String}.
     */
    public String getAddress() {
        return address.get();
    }

    /**
     * Sets the street address of the location.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is mandatory and cannot be set to {@code null} or an empty string.
     * </p>
     *
     * @param address The address to set.
     * @throws IllegalArgumentException if the provided address is {@code null} or empty after trimming.
     */
    public void setAddress(String address) {
        String trimmedAddress = (address == null) ? null : address.trim();
        if (trimmedAddress == null || trimmedAddress.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        this.address.set(trimmedAddress);
    }

    /**
     * Retrieves the city of the location.
     * Corresponds to the {@code City} column in the database.
     *
     * @return The city as a {@link String}.
     */
    public String getCity() {
        return city.get();
    }

    /**
     * Sets the city of the location.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is mandatory and cannot be set to {@code null} or an empty string.
     * </p>
     *
     * @param city The city to set.
     * @throws IllegalArgumentException if the provided city is {@code null} or empty after trimming.
     */
    public void setCity(String city) {
        String trimmedCity = (city == null) ? null : city.trim();
        if (trimmedCity == null || trimmedCity.isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty.");
        }
        this.city.set(trimmedCity);
    }

    /**
     * Retrieves the state or province abbreviation of the location.
     * Corresponds to the {@code State} column in the database.
     *
     * @return The state abbreviation as a {@link String}.
     */
    public String getState() {
        return state.get();
    }

    /**
     * Sets the state or province abbreviation of the location.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is mandatory and cannot be set to {@code null} or an empty string.
     * </p>
     *
     * @param state The state abbreviation to set.
     * @throws IllegalArgumentException if the provided state is {@code null} or empty after trimming.
     */
    public void setState(String state) {
        String trimmedState = (state == null) ? null : state.trim();
        if (trimmedState == null || trimmedState.isEmpty()) {
            throw new IllegalArgumentException("State cannot be null or empty.");
        }
        this.state.set(trimmedState);
    }

    /**
     * Retrieves the postal zip code of the location.
     * Corresponds to the {@code ZipCode} column in the database.
     *
     * @return The zip code as a {@link String}.
     */
    public String getZipCode() {
        return zipCode.get();
    }

    /**
     * Sets the postal zip code of the location.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is mandatory and cannot be set to {@code null} or an empty string.
     * Basic validation ensures it's not empty, but more complex regex validation
     * (e.g., for specific country formats) could be added here.
     * </p>
     *
     * @param zipCode The zip code to set.
     * @throws IllegalArgumentException if the provided zip code is {@code null} or empty after trimming.
     */
    public void setZipCode(String zipCode) {
        String trimmedZipCode = (zipCode == null) ? null : zipCode.trim();
        if (trimmedZipCode == null || trimmedZipCode.isEmpty()) {
            throw new IllegalArgumentException("Zip Code cannot be null or empty.");
        }
        this.zipCode.set(trimmedZipCode);
    }

    /**
     * Retrieves the geographical latitude of the location.
     * Corresponds to the {@code Latitude} column in the database.
     *
     * @return The latitude as a {@link Double}, or {@code null} if not set.
     */
    public Double getLatitude() {
        return latitude.get();
    }

    /**
     * Sets the geographical latitude of the location.
     * <p>
     * The latitude value must be between -90.0 and +90.0 degrees (inclusive),
     * representing the valid range for geographical latitudes. This field is optional.
     * </p>
     *
     * @param latitude The latitude to set. Can be {@code null}.
     * @throws IllegalArgumentException if the latitude is out of the valid range [-90.0, 90.0].
     */
    public void setLatitude(Double latitude) {
        if (latitude != null && (latitude < -90.0 || latitude > 90.0)) {
            throw new IllegalArgumentException("Latitude must be between -90 and +90 degrees.");
        }
        this.latitude.set(latitude);
    }

    /**
     * Retrieves the geographical longitude of the location.
     * Corresponds to the {@code Longitude} column in the database.
     *
     * @return The longitude as a {@link Double}, or {@code null} if not set.
     */
    public Double getLongitude() {
        return longitude.get();
    }

    /**
     * Sets the geographical longitude of the location.
     * <p>
     * The longitude value must be between -180.0 and +180.0 degrees (inclusive),
     * representing the valid range for geographical longitudes. This field is optional.
     * </p>
     *
     * @param longitude The longitude to set. Can be {@code null}.
     * @throws IllegalArgumentException if the longitude is out of the valid range [-180.0, 180.0].
     */
    public void setLongitude(Double longitude) {
        if (longitude != null && (longitude < -180.0 || longitude > 180.0)) {
            throw new IllegalArgumentException("Longitude must be between -180 and +180 degrees.");
        }
        this.longitude.set(longitude);
    }

    /**
     * Retrieves the active status of the location.
     * Corresponds to the {@code IsActive} column in the database.
     *
     * @return {@code true} if the location is active, {@code false} if it's inactive/logically deleted.
     */
    public boolean getIsActive() {
        return isActive.get();
    }

    /**
     * Sets the active status of the location.
     *
     * @param isActive {@code true} to mark the location as active, {@code false} for inactive/logically deleted.
     */
    public void setIsActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    /**
     * Retrieves the timestamp when the location was logically deleted or deactivated.
     * Corresponds to the {@code DeletedAt} column in the database.
     *
     * @return The {@link LocalDateTime} of deletion, or {@code null} if the location is active.
     */
    public LocalDateTime getDeletedAt() { // New getter
        return deletedAt.get();
    }

    /**
     * Sets the timestamp when the location was logically deleted or deactivated.
     *
     * @param deletedAt The {@link LocalDateTime} to set as the deletion timestamp. Can be {@code null}.
     */
    public void setDeletedAt(LocalDateTime deletedAt) { // New setter
        this.deletedAt.set(deletedAt);
    }

    /**
     * Retrieves any additional notes or administrative comments for the location.
     * Corresponds to the {@code Notes} column in the database.
     *
     * @return The notes string, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes.get();
    }

    /**
     * Sets additional notes or administrative comments for the location.
     * <p>
     * If the provided notes are not {@code null}, leading and trailing whitespace will be stripped.
     * </p>
     *
     * @param notes The string containing notes to set. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes.set((notes == null) ? null : notes.trim());
    }

    // --- Utility Methods ---

    /**
     * <p>
     * Returns a string representation of the {@code Location} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the location's key attributes.
     * </p>
     * <p>
     * The format includes the location ID, name, city, state, active status, and deleted timestamp.
     * </p>
     *
     * @return A string in the format:
     * "Location{ID=..., Name=..., City=..., State=..., isActive=..., deletedAt=...}"
     */
    @Override
    public String toString() {
        return "Location{" +
                "locationID=" + getLocationID() +
                ", locationName='" + getLocationName() + '\'' +
                ", city='" + getCity() + '\'' +
                ", state='" + getState() + '\'' +
                ", isActive=" + getIsActive() +
                ", deletedAt=" + getDeletedAt() + // Added new field
                '}';
    }

    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based primarily on the unique {@code locationID}.
     * </p>
     * <p>
     * This method adheres to the general contract of the {@link Object#equals(Object)} method,
     * ensuring consistency with hash-based collections. It correctly handles cases where
     * {@code locationID} might be {@code null} for unpersisted entities, in which case
     * it falls back to object identity comparison ({@code super.equals(o)}).
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location that = (Location) o;
        // Equality is based on the primary key (locationID), safely handling null Integer
        if (getLocationID() == null || that.getLocationID() == null) {
            return super.equals(o); // If IDs are null, fall back to object identity
        }
        return Objects.equals(getLocationID(), that.getLocationID());
    }

    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap} and {@link java.util.HashSet}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code locationID}. If {@code locationID}
     * is {@code null} (for unpersisted entities), it falls back to the default hash code
     * provided by {@code super.hashCode()}, ensuring consistency with {@code equals()}
     * for unpersisted objects as well.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return (getLocationID() == null) ? super.hashCode() : Objects.hash(getLocationID());
    }
}