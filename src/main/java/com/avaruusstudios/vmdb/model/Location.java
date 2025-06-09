package com.avaruusstudios.vmdb.model;

/**
 * Represents a physical pickup or drop-off location used within the Vanpool Management System.
 * Each location includes descriptive details such as the name, address, city, zip code, and any relevant notes.
 */
public class Location {
    /** Unique identifier for this location (Primary Key) */
    private int locationID;
    /** Human-readable name of the location, e.g., "Main Office", "Park & Ride Lot" */
    private String locationName;
    /** Street address of the location (may be partial or null) */
    private String address;
    /** City where the location is situated */
    private String city;
    /** State where the location is situated */
    private String state;
    /** Postal zip code for this location */
    private String zipCode;
    /** Location Latitude */
    private double latitude;
    /** Location Longitude */
    private double longitude;
    /** Optional notes about the location, such as landmarks, accessibility, or scheduling info */
    private String notes;

    /**
     * Default constructor for creating a blank Location object.
     * Useful for frameworks that instantiate objects via reflection.
     */
    public Location() {}

    /**
     * Full constructor to initialize all fields of a Location instance.
     *
     * @param locationID    Unique ID for this location (typically assigned by DB)
     * @param locationName  Descriptive name of the location
     * @param address       Street address or physical details
     * @param city          City where the location resides
     * @param state         State where the location is situated
     * @param zipCode       Postal zip code
     * @param latitude      Geographical latitude
     * @param longitude     Geographical longitude
     * @param notes         Optional notes or description
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
     * Gets the unique identifier for this location.
     *
     * @return locationId Integer key used to reference this location in the system.
     */
    public int getLocationID() {
        return locationID;
    }
    /**
     * Sets the unique identifier for this location.
     *
     * @param locationID ID value, typically from the database.
     */
    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }
    /**
     * Retrieves the human-readable name of this location.
     * This name is used in UI displays, forms, and reports.
     *
     * @return locationName A short, descriptive name like "HQ Office" or "South Lot".
     */
    public String getLocationName() {
        return locationName;
    }
    /**
     * Sets the name of the location.
     *
     * @param locationName Descriptive label used for identification in the system.
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    /**
     * Retrieves the street address for this location.
     *
     * @return address A string representing the address or null if not provided.
     */
    public String getAddress() {
        return address;
    }
    /**
     * Sets the street address for this location.
     *
     * @param address Full or partial street address.
     */
    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * Gets the city where this location is situated.
     *
     * @return city Name of the city as a string.
     */
    public String getCity() {
        return city;
    }
    /**
     * Sets the city for this location.
     *
     * @param city City name.
     */
    public void setCity(String city) {
        this.city = city;
    }
    /**
     * Gets the city where this location is situated.
     *
     * @return state Name of the State as a string.
     */
    public String getState() {
        return state;
    }
    /**
     * Sets the city for this location.
     *
     * @param state City name.
     */
    public void setState(String state) {
        this.state = state;
    }
    /**
     * Gets the zip code associated with this location.
     *
     * @return zipCode Postal code or empty string if not set.
     */
    public String getZipCode() {
        return zipCode;
    }
    /**
     * Assigns a postal zip code to the location.
     * May be used for distance calculations or geolocation.
     *
     * @param zipCode Postal code in string format (e.g., "90210").
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    /**
     * Gets the Latitude as a double
     *
     * @return latitude The latitude as a double
     */
    public double getLatitude() {
        return latitude;
    }
    /**
     * Assigns the Latitude of the location
     * May be used for distance calculations
     *
     * @param latitude Latitude as a double for the location
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    /**
     * Gets the Longitude of the location
     * @return longitude The longitude of the Location
     */
    public double getLongitude() {
        return longitude;
    }
    /**
     * Assigns the Longitude of the Location
     * May be used for distance calculations
     *
     * @param longitude Longitude as a double for the location
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    /**
     * Retrieves any additional notes about this location.
     * This might include access instructions, alternate names, or usage restrictions.
     *
     * @return notes Optional string of remarks; can be null.
     */
    public String getNotes() {
        return notes;
    }
    /**
     * Sets additional commentary or metadata for this location.
     * Used for informal notes, staff memos, or scheduling instructions.
     *
     * @param notes Free-form text used to store supplemental info.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
