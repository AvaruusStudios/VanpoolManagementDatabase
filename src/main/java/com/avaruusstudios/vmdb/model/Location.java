package com.avaruusstudios.vmdb.model;

import java.util.Objects;

/**
 * <p>
 * Represents a physical location used within the Vanpool Management System.
 * This class encapsulates geographical and descriptive details for specific
 * pickup or drop-off points, or other points of interest.
 * </p>
 *
 * <p>
 * Each location includes essential information such as a human-readable name,
 * a complete street address, and optional precise geographical coordinates (latitude and longitude),
 * along with any relevant notes. Locations are fundamental for routing and participant assignments.
 * </p>
 *
 * <p>
 * **Note on Distance Calculations:** For accurate distance calculations, a {@code Location}
 * object typically requires its address fields (`address`, `city`, `state`, `zipCode`) to be
 * complete and valid for subsequent geocoding, or it must explicitly contain valid and precise
 * {@link #latitude} and {@link #longitude} coordinates. If coordinates are not provided,
 * they are assumed to be derivable from the complete address by an external geocoding service.
 * </p>
 *
 * @see Participant // Link to Participant as it references Location objects
 */
public class Location {
    /**
     * Unique identifier for this location. This serves as the primary key
     * in the database for location records ({@code LocationID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created location not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable.
     * </p>
     */
    private final Integer locationID;

    /**
     * A human-readable and descriptive name for the location,
     * such as "Main Office", "Park & Ride Lot A", or "Downtown Transfer Point".
     * This field is **required**.
     */
    private String locationName;

    /**
     * The complete street address of the location. This field is **required**.
     */
    private String address;

    /**
     * The city where the location is geographically situated. This field is **required**.
     */
    private String city;

    /**
     * The state where the location is geographically situated (e.g., "CA" for California).
     * This field is **required**.
     */
    private String state;

    /**
     * The postal zip code for this location (e.g., "90210"). This field is **required**.
     */
    private String zipCode;

    /**
     * The geographical latitude coordinate of the location. This field is optional.
     * If provided, it must be between -90.0 and +90.0.
     */
    private Double latitude; // Changed to Double wrapper

    /**
     * The geographical longitude coordinate of the location. This field is optional.
     * If provided, it must be between -180.0 and +180.0.
     */
    private Double longitude; // Changed to Double wrapper

    /**
     * Optional notes or additional information about the location.
     * This could include landmarks, accessibility details, specific instructions
     * for drivers/riders, or scheduling information.
     */
    private String notes;

    /**
     * Default constructor for creating a new, unpersisted {@code Location} object.
     * The {@code locationID} is set to {@code null} to explicitly indicate that
     * this location has not yet been assigned a unique ID by the database.
     * This constructor is primarily used by frameworks that instantiate objects
     * via reflection (e.g., ORMs, JSON deserializers) before populating their fields.
     */
    public Location() {
        this.locationID = null; // Explicitly null for unpersisted entity
    }

    /**
     * Full constructor to initialize all fields of a {@code Location} instance.
     * This constructor is typically used when loading an *existing* location
     * record from the database, where {@code locationID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param locationID    Unique integer ID for this location, typically assigned by the database. Must not be {@code null}.
     * @param locationName  A descriptive, human-readable name for the location (e.g., "HQ Office"). Must not be {@code null} or empty.
     * @param address       The complete street address of the location. Must not be {@code null} or empty.
     * @param city          The city where the location is located. Must not be {@code null} or empty.
     * @param state         The state abbreviation where the location is located. Must not be {@code null} or empty.
     * @param zipCode       The postal zip code for the location. Must not be {@code null} or empty.
     * @param latitude      The geographical latitude coordinate of the location. Can be {@code null}. If not null, must be between -90.0 and +90.0.
     * @param longitude     The geographical longitude coordinate of the location. Can be {@code null}. If not null, must be between -180.0 and +180.0.
     * @param notes         Optional notes or a detailed description about the location. Can be {@code null}.
     * @throws NullPointerException if `locationID`, `locationName`, `address`, `city`, `state`, or `zipCode` are {@code null}.
     * @throws IllegalArgumentException if `locationName`, `address`, `city`, `state`, or `zipCode` are empty after stripping whitespace, or if `latitude`/`longitude` are out of valid range (if provided).
     */
    public Location(Integer locationID, String locationName, String address, String city, String state, String zipCode, Double latitude, Double longitude, String notes) { // Changed latitude/longitude to Double
        this.locationID = Objects.requireNonNull(locationID, "Location ID cannot be null for an existing location.");

        // Use setters for validation
        setLocationName(locationName);
        setAddress(address);
        setCity(city);
        setState(state);
        setZipCode(zipCode);
        setLatitude(latitude);
        setLongitude(longitude);
        setNotes(notes);
    }

    /**
     * Convenience constructor for creating a new {@code Location} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new location record for **insertion** into the database.
     * The {@code locationID} is omitted as it is typically auto-generated by the database.
     * All parameters are validated via their respective setters.
     *
     * @param locationName  A descriptive, human-readable name for the location (e.g., "HQ Office"). Must not be {@code null} or empty.
     * @param address       The complete street address of the location. Must not be {@code null} or empty.
     * @param city          The city where the location is located. Must not be {@code null} or empty.
     * @param state         The state abbreviation where the location is located. Must not be {@code null} or empty.
     * @param zipCode       The postal zip code for the location. Must not be {@code null} or empty.
     * @param latitude      The geographical latitude coordinate of the location. Can be {@code null}. If not null, must be between -90.0 and +90.0.
     * @param longitude     The geographical longitude coordinate of the location. Can be {@code null}. If not null, must be between -180.0 and +180.0.
     * @param notes         Optional notes or a detailed description about the location. Can be {@code null}.
     * @throws NullPointerException if `locationName`, `address`, `city`, `state`, or `zipCode` are {@code null}.
     * @throws IllegalArgumentException if `locationName`, `address`, `city`, `state`, or `zipCode` are empty after stripping whitespace, or if `latitude`/`longitude` are out of valid range (if provided).
     */
    public Location(String locationName, String address, String city, String state, String zipCode, Double latitude, Double longitude, String notes) { // Changed latitude/longitude to Double
        this.locationID = null; // New entity, ID will be assigned by DB

        // Use setters for validation
        setLocationName(locationName);
        setAddress(address);
        setCity(city);
        setState(state);
        setZipCode(zipCode);
        setLatitude(latitude);
        setLongitude(longitude);
        setNotes(notes);
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Retrieves the unique identifier for this location.
     * For new, unpersisted locations, this will be {@code null}.
     *
     * @return The {@link Integer} primary key used to reference this location in the system, or {@code null} if not yet assigned.
     */
    public Integer getLocationID() {
        return locationID;
    }
    // setLocationID method is removed as locationID is now final and set only via constructors

    /**
     * Retrieves the human-readable name of this location.
     * This name is frequently used in user interfaces, forms, and reports for identification.
     *
     * @return A short, descriptive string name for the location (e.g., "South Lot").
     */
    public String getLocationName() {
        return locationName;
    }
    /**
     * Sets the name of the location.
     *
     * @param locationName The descriptive label used for identification within the system. Must not be {@code null} or empty.
     * @throws NullPointerException if {@code locationName} is {@code null}.
     * @throws IllegalArgumentException if {@code locationName} is empty after stripping whitespace.
     */
    public void setLocationName(String locationName) {
        String trimmedName = Objects.requireNonNull(locationName, "Location name cannot be null.").strip();
        if (trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Location name cannot be empty.");
        }
        this.locationName = trimmedName;
    }
    /**
     * Retrieves the complete street address for this location.
     *
     * @return A string representing the complete street address.
     */
    public String getAddress() {
        return address;
    }
    /**
     * Sets the complete street address for this location.
     *
     * @param address The full street address string. Must not be {@code null} or empty.
     * @throws NullPointerException if {@code address} is {@code null}.
     * @throws IllegalArgumentException if {@code address} is empty after stripping whitespace.
     */
    public void setAddress(String address) {
        String trimmedAddress = Objects.requireNonNull(address, "Address cannot be null.").strip();
        if (trimmedAddress.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }
        this.address = trimmedAddress;
    }
    /**
     * Retrieves the city where this location is situated.
     *
     * @return The string name of the city.
     */
    public String getCity() {
        return city;
    }
    /**
     * Sets the city for this location.
     *
     * @param city The string name of the city. Must not be {@code null} or empty.
     * @throws NullPointerException if {@code city} is {@code null}.
     * @throws IllegalArgumentException if {@code city} is empty after stripping whitespace.
     */
    public void setCity(String city) {
        String trimmedCity = Objects.requireNonNull(city, "City cannot be null.").strip();
        if (trimmedCity.isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty.");
        }
        this.city = trimmedCity;
    }
    /**
     * Retrieves the state where this location is situated.
     *
     * @return The string abbreviation of the state (e.g., "CA").
     */
    public String getState() {
        return state;
    }
    /**
     * Sets the state for this location.
     *
     * @param state The string abbreviation of the state. Must not be {@code null} or empty.
     * @throws NullPointerException if {@code state} is {@code null}.
     * @throws IllegalArgumentException if {@code state} is empty after stripping whitespace.
     */
    public void setState(String state) {
        String trimmedState = Objects.requireNonNull(state, "State cannot be null.").strip();
        if (trimmedState.isEmpty()) {
            throw new IllegalArgumentException("State cannot be empty.");
        }
        this.state = trimmedState;
    }
    /**
     * Retrieves the postal zip code associated with this location.
     *
     * @return The string representing the postal code.
     */
    public String getZipCode() {
        return zipCode;
    }
    /**
     * Assigns a postal zip code to the location.
     *
     * @param zipCode The postal code in string format (e.g., "90210"). Must not be {@code null} or empty.
     * @throws NullPointerException if {@code zipCode} is {@code null}.
     * @throws IllegalArgumentException if {@code zipCode} is empty after stripping whitespace.
     */
    public void setZipCode(String zipCode) {
        String trimmedZipCode = Objects.requireNonNull(zipCode, "Zip code cannot be null.").strip();
        if (trimmedZipCode.isEmpty()) {
            throw new IllegalArgumentException("Zip code cannot be empty.");
        }
        this.zipCode = trimmedZipCode;
    }
    /**
     * Retrieves the geographical latitude coordinate of the location.
     *
     * @return The latitude as a {@link Double}, or {@code null} if not set.
     */
    public Double getLatitude() { // Changed return type
        return latitude;
    }
    /**
     * Assigns the geographical latitude coordinate to the location.
     * These coordinates are essential for mapping, routing, and accurate distance calculations.
     *
     * @param latitude The latitude as a {@link Double} for the location. Can be {@code null}. If not null, must be between -90.0 and +90.0.
     * @throws IllegalArgumentException if the {@code latitude} is outside the valid range of -90.0 to +90.0 (if provided).
     */
    public void setLatitude(Double latitude) { // Changed parameter type
        if (latitude != null) {
            if (latitude < -90.0 || latitude > 90.0) {
                throw new IllegalArgumentException("Latitude must be between -90.0 and +90.0.");
            }
        }
        this.latitude = latitude;
    }
    /**
     * Retrieves the geographical longitude coordinate of the location.
     *
     * @return The longitude as a {@link Double}, or {@code null} if not set.
     */
    public Double getLongitude() { // Changed return type
        return longitude;
    }
    /**
     * Assigns the geographical longitude coordinate to the location.
     * These coordinates are essential for mapping, routing, and accurate distance calculations.
     *
     * @param longitude The longitude as a {@link Double} for the location. Can be {@code null}. If not null, must be between -180.0 and +180.0.
     * @throws IllegalArgumentException if the {@code longitude} is outside the valid range of -180.0 to +180.0 (if provided).
     */
    public void setLongitude(Double longitude) { // Changed parameter type
        if (longitude != null) {
            if (longitude < -180.0 || longitude > 180.0) {
                throw new IllegalArgumentException("Longitude must be between -180.0 and +180.0.");
            }
        }
        this.longitude = longitude;
    }
    /**
     * Retrieves any additional notes or remarks stored for this location.
     *
     * @return A string containing notes, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes;
    }
    /**
     * Sets additional commentary or metadata for this location.
     * This field can be used for informal remarks, special instructions, or administrative memos.
     * If the provided notes are not {@code null}, leading and trailing whitespace will be stripped.
     *
     * @param notes A free-form text string to store supplemental information. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.strip() : null;
    }

    // ---------------------
    // Utilities
    // ---------------------

    /**
     * <p>
     * Returns a string representation of the {@code Location} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the location's key attributes.
     * </p>
     * <p>
     * The format includes the location ID, name, city, state, and coordinates.
     * </p>
     *
     * @return A string in the format:
     * "Location{ID=..., Name='...', City='...', State='...', Lat=..., Lng=...}"
     */
    @Override
    public String toString() {
        return "Location{" +
                "locationID=" + locationID +
                ", locationName='" + locationName + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based primarily on the unique {@code locationID}.
     * </p>
     * <p>
     * This method adheres to the general contract of the {@link Object#equals(Object)} method.
     * It correctly handles cases where {@code locationID} might be {@code null} for unpersisted entities.
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Same object reference
        if (o == null || getClass() != o.getClass()) return false; // Null or different class
        Location location = (Location) o; // Cast to Location
        // Equality is based on the primary key (locationID), safely handling null Integer
        return Objects.equals(locationID, location.locationID);
    }
    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code locationID}. If {@code locationID}
     * is {@code null} (for unpersisted entities), its hash code will be 0, as per {@link Objects#hash(Object...)}.
     * This ensures that objects considered equal by {@link #equals(Object)} will have the same hash code.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(locationID); // Hash code based on the primary key
    }
}