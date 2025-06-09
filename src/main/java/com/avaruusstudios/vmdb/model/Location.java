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
 * full address components, and precise geographical coordinates (latitude and longitude),
 * along with any relevant notes. Locations are fundamental for routing and participant assignments.
 * </p>
 *
 * @see Participant // Link to Participant as it references Location objects
 */
public class Location {
    /**
     * Unique identifier for this location. This serves as the primary key
     * in the database for location records.
     */
    private int locationID;

    /**
     * A human-readable and descriptive name for the location,
     * such as "Main Office", "Park & Ride Lot A", or "Downtown Transfer Point".
     */
    private String locationName;

    /**
     * The street address of the location. This may be a full address,
     * or a partial one depending on the level of detail available.
     */
    private String address;

    /**
     * The city where the location is geographically situated.
     */
    private String city;

    /**
     * The state where the location is geographically situated (e.g., "CA" for California).
     */
    private String state;

    /**
     * The postal zip code for this location (e.g., "90210").
     */
    private String zipCode;

    /**
     * The geographical latitude coordinate of the location.
     * This is crucial for precise mapping and distance calculations.
     */
    private double latitude;

    /**
     * The geographical longitude coordinate of the location.
     * This is crucial for precise mapping and distance calculations.
     */
    private double longitude;

    /**
     * Optional notes or additional information about the location.
     * This could include landmarks, accessibility details, specific instructions
     * for drivers/riders, or scheduling information.
     */
    private String notes;

    /**
     * Default constructor for creating an empty {@code Location} object.
     * This constructor is primarily used by frameworks that instantiate objects
     * via reflection (e.g., ORMs, JSON deserializers) before populating their fields.
     */
    public Location() {}

    /**
     * Full constructor to initialize all fields of a {@code Location} instance.
     * This constructor provides a comprehensive way to create a location record
     * with all necessary details upon instantiation.
     *
     * @param locationID    Unique integer ID for this location, typically assigned by the database.
     * @param locationName  A descriptive, human-readable name for the location (e.g., "HQ Office").
     * @param address       The street address or physical details of the location.
     * @param city          The city where the location is located.
     * @param state         The state abbreviation where the location is located.
     * @param zipCode       The postal zip code for the location.
     * @param latitude      The geographical latitude coordinate of the location.
     * @param longitude     The geographical longitude coordinate of the location.
     * @param notes         Optional notes or a detailed description about the location. Can be {@code null}.
     */
    public Location(int locationID, String locationName, String address, String city, String state, String zipCode, double latitude, double longitude, String notes) {
        this.locationID = locationID;
        this.locationName = locationName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.notes = notes;
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Retrieves the unique identifier for this location.
     *
     * @return The integer primary key used to reference this location in the system.
     */
    public int getLocationID() {
        return locationID;
    }
    /**
     * Sets the unique identifier for this location.
     * This method is typically used when populating a location object from a persisted source.
     *
     * @param locationID The unique integer ID value, typically assigned by the database.
     */
    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }
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
     * @param locationName The descriptive label used for identification within the system.
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    /**
     * Retrieves the street address for this location.
     *
     * @return A string representing the street address, or {@code null} if not provided.
     */
    public String getAddress() {
        return address;
    }
    /**
     * Sets the street address for this location.
     *
     * @param address The full or partial street address string.
     */
    public void setAddress(String address) {
        this.address = address;
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
     * @param city The string name of the city.
     */
    public void setCity(String city) {
        this.city = city;
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
     * @param state The string abbreviation of the state.
     */
    public void setState(String state) {
        this.state = state;
    }
    /**
     * Retrieves the postal zip code associated with this location.
     *
     * @return The string representing the postal code, or an empty string if not set.
     */
    public String getZipCode() {
        return zipCode;
    }
    /**
     * Assigns a postal zip code to the location. This can be used for geographic
     * filtering, distance calculations, or mail services.
     *
     * @param zipCode The postal code in string format (e.g., "90210").
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    /**
     * Retrieves the geographical latitude coordinate of the location.
     *
     * @return The latitude as a double.
     */
    public double getLatitude() {
        return latitude;
    }
    /**
     * Assigns the geographical latitude coordinate to the location.
     * These coordinates are essential for mapping, routing, and accurate distance calculations.
     *
     * @param latitude The latitude as a double for the location.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    /**
     * Retrieves the geographical longitude coordinate of the location.
     *
     * @return The longitude as a double.
     */
    public double getLongitude() {
        return longitude;
    }
    /**
     * Assigns the geographical longitude coordinate to the location.
     * These coordinates are essential for mapping, routing, and accurate distance calculations.
     *
     * @param longitude The longitude as a double for the location.
     */
    public void setLongitude(double longitude) {
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
     *
     * @param notes A free-form text string to store supplemental information. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes = notes;
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
        // Equality is based on the primary key (locationID)
        return locationID == location.locationID;
    }
    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code locationID}, ensuring that
     * objects considered equal by {@link #equals(Object)} will have the same hash code.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(locationID); // Hash code based on the primary key
    }
}