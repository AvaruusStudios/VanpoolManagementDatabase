package com.avaruusstudios.vmdb.model;

import javafx.beans.property.*; // Essential JavaFX property classes for reactive data binding

import java.util.Objects;

/**
 * <p>
 * Represents a physical location used within the Vanpool Management System. This class
 * encapsulates all necessary geographical and descriptive details for specific points
 * of interest, such as pickup or drop-off locations for vanpools, office addresses,
 * or maintenance facilities. It is a fundamental component for establishing routes,
 * assigning participants to vanpools, and managing geographical data within the system.
 * </p>
 *
 * <p>
 * The `Location` class is designed with JavaFX Properties to facilitate robust
 * data binding with the application's graphical user interface. This architectural
 * choice allows for a reactive programming model where changes in the UI are
 * automatically synchronized with the underlying `Location` object, and vice-versa,
 * thereby simplifying UI development and maintaining data consistency.
 * </p>
 *
 * <p>
 * Each location is identified by a unique numerical ID and includes essential
 * information such as a human-readable name, a complete street address, and optional
 * precise geographical coordinates (latitude and longitude), along with any relevant notes.
 * For accurate distance calculations within the system, a {@code Location} object
 * typically requires its address fields (`address`, `city`, `state`, `zipCode`) to be
 * complete and valid for subsequent geocoding by an external service, or it must
 * explicitly contain valid and precise {@link #latitudeProperty()} and
 * {@link #longitudeProperty()} coordinates. If coordinates are not provided,
 * the system assumes they can be derived from the complete address.
 * </p>
 *
 * @see Participant // Link to Participant as it references Location objects for addresses
 * @see javafx.beans.property // Referencing the JavaFX Property package for context
 * @see com.avaruusstudios.vmdb.dao.LocationDAO // Implied for data persistence operations
 */
public class Location {
    /**
     * <p>
     * This {@link ReadOnlyObjectProperty} holds the unique numerical identifier for the location.
     * It serves as the primary key for the location record in the underlying database
     * (e.g., SQLite's `LocationID INTEGER PRIMARY KEY AUTOINCREMENT`). This property is
     * crucial for database operations, as it is assigned by the database upon successful
     * insertion of a new `Location` object, making the object uniquely identifiable.
     * When `Location` records are retrieved from the database, this property is populated
     * with the corresponding ID, allowing the application to reference and work with
     * existing location entities. In the user interface, particularly in tabular displays
     * (e.g., a `TableView` of locations), this ID can be displayed to users for clear
     * identification. Internally, the `equals()` and `hashCode()` methods of this class
     * primarily rely on the value of this property to determine object uniqueness and for
     * proper functioning in collections like `HashMap` or `HashSet`.
     * </p>
     * <p>
     * For a newly instantiated `Location` object that has not yet been persisted to the
     * database, this property's value will be {@code null}. Once the location record is
     * successfully inserted into the database and an ID is assigned, this property is
     * set internally via a package-private method, reflecting its role as a permanent identifier.
     * As a `ReadOnlyObjectProperty`, its value is intended to be immutable after its initial
     * assignment by the database. Attempting to change it directly after it has been set
     * will result in an {@link IllegalStateException}.
     * </p>
     */
    private final ReadOnlyObjectProperty<Integer> locationID;
    /**
     * <p>
     * This {@link StringProperty} stores a human-readable and descriptive name for the location,
     * such as "Main Office", "Park & Ride Lot A", or "Downtown Transfer Point". This name
     * is prominently displayed in location lists, forms, and reports within the user interface,
     * serving as the primary identifier for users to recognize and select locations. It is
     * actively used during data entry when users input this value while creating or editing
     * location records. The {@link #setLocationName(String)} method includes robust validation
     * to ensure the value is not {@code null} or an empty string after trimming whitespace,
     * which guarantees data integrity. Furthermore, this property is employed by users or
     * backend logic for searching and filtering specific locations, and is always included
     * in various reports for clear location identification.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Changes to this property in the UI, when bound using JavaFX's binding mechanisms,
     * will automatically reflect in the model, and vice-versa. The underlying string value
     * can be accessed or modified via {@link #getLocationName()} and {@link #setLocationName(String)},
     * or by directly interacting with the property object via {@link #locationNameProperty()}.
     * </p>
     */
    private final StringProperty locationName;
    /**
     * <p>
     * This {@link StringProperty} stores the complete street address of the location. This
     * field is essential for accurately identifying the physical whereabouts of the location
     * and is a crucial component for any geocoding or mapping services that might be integrated
     * with the system. In the user interface, it is displayed in detail forms and may be used
     * in conjunction with other address components (city, state, zipCode) to present a full
     * address. The {@link #setAddress(String)} method enforces that this value is neither
     * {@code null} nor an empty string after trimming whitespace, ensuring foundational data
     * quality. This property is used for display, data entry, and as input for external
     * geographical services.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #addressProperty()} and can be accessed or modified
     * via {@link #getAddress()} and {@link #setAddress(String)}. Changes are automatically
     * reflected in bound UI elements.
     * </p>
     */
    private final StringProperty address;
    /**
     * <p>
     * This {@link StringProperty} stores the name of the city where the location is geographically
     * situated. This component of the address is vital for precise geographical identification
     * and is used in conjunction with other address fields for routing, display, and data
     * validation. In the user interface, it is presented in location detail forms. The
     * {@link #setCity(String)} method performs validation to ensure this field is neither
     * {@code null} nor an empty string after trimming whitespace, guaranteeing data integrity.
     * This property supports geographical queries and filtering within the system.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #cityProperty()} and can be accessed or modified
     * via {@link #getCity()} and {@link #setCity(String)}. Changes are automatically
     * reflected in bound UI elements.
     * </p>
     */
    private final StringProperty city;
    /**
     * <p>
     * This {@link StringProperty} stores the abbreviation for the state or province where
     * the location is geographically situated (e.g., "CA" for California). This field is
     * essential for complete address validation and geographical categorization, particularly
     * in systems operating across multiple states or regions. In the user interface, it is
     * typically presented in location detail forms and may be populated via dropdowns. The
     * {@link #setState(String)} method ensures that this value is neither {@code null} nor
     * an empty string after trimming whitespace, maintaining data quality. This property is
     * critical for accurate geocoding and for regional data analysis.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #stateProperty()} and can be accessed or modified
     * via {@link #getState()} and {@link #setState(String)}. Changes are automatically
     * reflected in bound UI elements.
     * </p>
     */
    private final StringProperty state;
    /**
     * <p>
     * This {@link StringProperty} stores the postal zip code for this location (e.g., "90210").
     * The zip code is a critical component of the full address, used for precise geographical
     * identification, routing optimization, and delivery services. In the user interface, it
     * is presented in location detail forms and is typically validated for format and existence.
     * The {@link #setZipCode(String)} method enforces that this value is neither {@code null}
     * nor an empty string after trimming whitespace, ensuring data integrity. This property
     * supports granular geographical filtering and accurate address resolution.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #zipCodeProperty()} and can be accessed or modified
     * via {@link #getZipCode()} and {@link #setZipCode(String)}. Changes are automatically
     * reflected in bound UI elements.
     * </p>
     */
    private final StringProperty zipCode;
    /**
     * <p>
     * This {@link ObjectProperty} stores the geographical latitude coordinate of the location
     * as a {@link Double}. Latitude, along with longitude, provides precise, explicit geographical
     * positioning, which is crucial for mapping, distance calculations, and integration with
     * GIS (Geographic Information System) tools. This field is optional; if not provided,
     * the system might rely on geocoding the full address. In the user interface, it can be
     * displayed in read-only fields or editable inputs for advanced users. The {@link #setLatitude(Double)}
     * method rigorously validates that if a value is provided, it falls within the valid range
     * of -90.0 to +90.0 degrees, ensuring the integrity of the geographical data. This property
     * is particularly useful when addresses alone are insufficient for precise location (e.g.,
     * for large parking lots or specific points along a road).
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #latitudeProperty()} and can be accessed or modified
     * via {@link #getLatitude()} and {@link #setLatitude(Double)}. Changes are automatically
     * reflected in bound UI elements.
     * </p>
     */
    private final ObjectProperty<Double> latitude;
    /**
     * <p>
     * This {@link ObjectProperty} stores the geographical longitude coordinate of the location
     * as a {@link Double}. Longitude, in conjunction with latitude, provides precise, explicit
     * geographical positioning. This is indispensable for accurate mapping, calculating distances
     * between locations, and integrating with advanced routing algorithms or GIS systems. This
     * field is optional; if not explicitly set, its value might be derived from the full address
     * using a geocoding service. In the user interface, it can be displayed in read-only fields
     * or editable inputs for advanced users. The {@link #setLongitude(Double)} method rigorously
     * validates that if a value is provided, it falls within the valid range of -180.0 to +180.0
     * degrees, ensuring the integrity of the geographical data. This property enhances the
     * precision of location data beyond just a street address.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #longitudeProperty()} and can be accessed or modified
     * via {@link #getLongitude()} and {@link #setLongitude(Double)}. Changes are automatically
     * reflected in bound UI elements.
     * </p>
     */
    private final ObjectProperty<Double> longitude;
    /**
     * <p>
     * This {@link StringProperty} holds optional free-form text for additional notes or
     * administrative comments specific to this location. This field provides flexibility
     * to contain diverse unstructured information, such as accessibility details, specific
     * instructions for drivers or riders, local landmarks, or any relevant administrative remarks.
     * In the user interface, this property is typically shown in a multi-line text area or a
     * dedicated "Notes" section within the location detail view, allowing users to easily
     * review or add comments. It serves as a flexible field for administrators or staff to
     * record pertinent contextual information about the location, with the {@link #setNotes(String)}
     * method automatically stripping leading and trailing whitespace if the provided notes
     * are not {@code null}. This property acts as a quick operational reference for staff
     * when managing vanpools or preparing routes.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #notesProperty()} and can be accessed or modified
     * via {@link #getNotes()} and {@link #setNotes(String)}. Changes are automatically
     * reflected in bound UI elements.
     * </p>
     */
    private final StringProperty notes;

    /**
     * <p>
     * This is the default constructor for creating a new, unpersisted {@code Location} object.
     * Its primary use case is for frameworks or scenarios where an object needs to be
     * instantiated without initial data, with its properties being set subsequently via their
     * respective setters. Upon invocation, all internal JavaFX property fields are initialized
     * with default or `null` values: `locationID` is explicitly set to {@code null} to indicate
     * an unpersisted entity; `StringProperty`s (`locationName`, `address`, `city`, `state`,
     * `zipCode`, `notes`) are initialized with empty strings (`""`); and `ObjectProperty`s
     * (`latitude`, `longitude`) are initialized as `null`. The specific value setters are then
     * called with these default values to ensure that any associated validation logic (e.g.,
     * for required string fields) is applied, even for an empty object, maintaining consistency
     * from the moment of instantiation.
     * </p>
     */
    public Location() {
        this(null, "", "", "", "", "", null, null, ""); // Default String values to empty string, Double values to null
    }
    /**
     * <p>
     * This comprehensive constructor initializes all fields of a {@code Location} instance. It is
     * particularly useful when loading an *existing* location record from a database, as it accepts
     * a pre-assigned {@code locationID}. Upon invocation, all internal JavaFX property fields
     * are first instantiated, establishing the reactive binding capabilities of the object.
     * Subsequently, each provided parameter is passed to its corresponding property setter method.
     * This systematic approach ensures that all field-level validation rules—such as checks for
     * non-null and non-empty strings, and valid ranges for geographical coordinates—are uniformly
     * applied. This guarantees data integrity regardless of whether the object is being created
     * from scratch (e.g., from user input) or populated from a data source. The order of setting
     * properties is carefully considered where dependencies might exist, though for `Location`
     * properties are largely independent.
     * </p>
     *
     * @param locationID    Unique integer ID for this location, typically assigned by the database.
     * This parameter initializes the {@link #locationIDProperty()}.
     * @param locationName  A descriptive, human-readable name for the location (e.g., "HQ Office").
     * This value initializes {@link #locationNameProperty()}.
     * @param address       The complete street address of the location. This value initializes {@link #addressProperty()}.
     * @param city          The city where the location is located. This value initializes {@link #cityProperty()}.
     * @param state         The state abbreviation where the location is located. This value initializes {@link #stateProperty()}.
     * @param zipCode       The postal zip code for the location. This value initializes {@link #zipCodeProperty()}.
     * @param latitude      The geographical latitude coordinate of the location. This value initializes {@link #latitudeProperty()}.
     * @param longitude     The geographical longitude coordinate of the location. This value initializes {@link #longitudeProperty()}.
     * @param notes         Optional notes or a detailed description about the location. This value initializes {@link #notesProperty()}.
     * @throws NullPointerException if `locationID` (when loading an existing location), or any
     * other required string parameters (`locationName`, `address`, `city`, `state`, `zipCode`) are {@code null}.
     * @throws IllegalArgumentException if `locationName`, `address`, `city`, `state`, or `zipCode` are empty after
     * stripping whitespace, or if `latitude`/`longitude` are out of valid range (if provided).
     */
    public Location(Integer locationID, String locationName, String address, String city, String state, String zipCode, Double latitude, Double longitude, String notes) {
        // Initialize JavaFX properties with their respective types
        this.locationID = new SimpleObjectProperty<>(this, "locationID", locationID);
        this.locationName = new SimpleStringProperty(this, "locationName");
        this.address = new SimpleStringProperty(this, "address");
        this.city = new SimpleStringProperty(this, "city");
        this.state = new SimpleStringProperty(this, "state");
        this.zipCode = new SimpleStringProperty(this, "zipCode");
        this.latitude = new SimpleObjectProperty<>(this, "latitude");
        this.longitude = new SimpleObjectProperty<>(this, "longitude");
        this.notes = new SimpleStringProperty(this, "notes");

        // Use setters for validation and assignment
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
     * <p>
     * This convenience constructor is specifically designed for creating a new {@code Location} object
     * that has not yet been saved to the database. It intentionally omits the {@code locationID}
     * parameter, as this identifier is typically auto-generated by the database during the
     * insertion process. This constructor efficiently delegates its initialization to the
     * {@link #Location(Integer, String, String, String, String, String, Double, Double, String)}
     * (the full constructor) by passing {@code null} for the `locationID`. All other parameters
     * are passed directly, ensuring that comprehensive validation logic within the property setters
     * is uniformly applied to the newly created location instance, guaranteeing data quality
     * from the point of object creation.
     * </p>
     *
     * @param locationName  A descriptive, human-readable name for the location (e.g., "HQ Office").
     * This value initializes {@link #locationNameProperty()}.
     * @param address       The complete street address of the location. This value initializes {@link #addressProperty()}.
     * @param city          The city where the location is located. This value initializes {@link #cityProperty()}.
     * @param state         The state abbreviation where the location is located. This value initializes {@link #stateProperty()}.
     * @param zipCode       The postal zip code for the location. This value initializes {@link #zipCodeProperty()}.
     * @param latitude      The geographical latitude coordinate of the location. This value initializes {@link #latitudeProperty()}.
     * @param longitude     The geographical longitude coordinate of the location. This value initializes {@link #longitudeProperty()}.
     * @param notes         Optional notes or a detailed description about the location. This value initializes {@link #notesProperty()}.
     * @throws NullPointerException if any required string parameters (`locationName`, `address`, `city`, `state`, `zipCode`) are {@code null}.
     * @throws IllegalArgumentException if `locationName`, `address`, `city`, `state`, or `zipCode` are empty after
     * stripping whitespace, or if `latitude`/`longitude` are out of valid range (if provided).
     */
    public Location(String locationName, String address, String city, String state, String zipCode, Double latitude, Double longitude, String notes) {
        this(null, locationName, address, city, state, zipCode, latitude, longitude, notes);
    }

    // --- JavaFX Property Accessors ---

    /**
     * <p>
     * This method provides direct access to the {@link ReadOnlyObjectProperty} that encapsulates
     * the unique identifier for this location. Its primary use is by JavaFX UI components,
     * such as `TableView` columns configured with a `PropertyValueFactory`, to directly bind
     * to and display the location's ID. Since the `locationID` is typically assigned by the
     * database and should not be changed by the UI after creation, this property is designed
     * as `ReadOnly`. Data Access Objects (DAOs) will utilize a specific internal setter
     * ({@link #_setLocationID(Integer)}) for initial assignment after a database insert operation,
     * reflecting the ID's immutable nature once established.
     * </p>
     *
     * @return The {@link ReadOnlyObjectProperty} for {@code locationID}, allowing UI elements
     * to observe changes to this read-only identifier.
     */
    public ReadOnlyObjectProperty<Integer> locationIDProperty() {
        return locationID;
    }
    /**
     * <p>
     * This method provides direct access to the {@link StringProperty} that encapsulates
     * the location's human-readable name. This is crucial for data binding in JavaFX applications.
     * UI controls such as `TextField`s for data input, `Label`s for display, or `TableView`
     * columns for list presentation would typically use `bindBidirectional()` or `PropertyValueFactory`
     * with this property. This creates a reactive link where any change in the UI field
     * automatically updates the model, and conversely, any programmatic change to the property
     * in the model updates the UI.
     * </p>
     *
     * @return The {@link StringProperty} for {@code locationName}, enabling bidirectional data binding.
     */
    public StringProperty locationNameProperty() {
        return locationName;
    }
    /**
     * <p>
     * This method provides direct access to the {@link StringProperty} that encapsulates
     * the complete street address of the location. It is used by JavaFX UI components to
     * establish robust data binding connections. UI elements like `TextField`s for input
     * or `Label`s for display would bind to this property to present and allow modification
     * of the location's address. This ensures that changes in the UI are immediately
     * reflected in the model and vice-versa, making it easy to manage location address data.
     * </p>
     *
     * @return The {@link StringProperty} for {@code address}, facilitating reactive UI updates.
     */
    public StringProperty addressProperty() {
        return address;
    }
    /**
     * <p>
     * This method provides direct access to the {@link StringProperty} that encapsulates
     * the city where the location is situated. It is primarily used by JavaFX UI components
     * for data binding purposes. UI controls such as `TextField`s or `Label`s would bind
     * to this property to both display and allow modification of the location's city. This
     * binding establishes a reactive link ensuring that any user input or programmatic
     * change is synchronized between the view and the model.
     * </p>
     *
     * @return The {@link StringProperty} for {@code city}, supporting bidirectional data binding.
     */
    public StringProperty cityProperty() {
        return city;
    }
    /**
     * <p>
     * This method provides direct access to the {@link StringProperty} that encapsulates
     * the state or province abbreviation where the location is situated. It is primarily
     * used by JavaFX UI components for data binding. UI elements like `TextField`s (or
     * `ComboBox`es if using a predefined list of states) or `Label`s for display would
     * bind to this property. This enables the UI to display the current state and allows
     * users to modify it, with changes automatically reflected in the model.
     * </p>
     *
     * @return The {@link StringProperty} for {@code state}, enabling reactive updates in the UI.
     */
    public StringProperty stateProperty() {
        return state;
    }
    /**
     * <p>
     * This method provides direct access to the {@link StringProperty} that encapsulates
     * the postal zip code for the location. It is primarily used by JavaFX UI components
     * for data binding. UI elements such as `TextField`s (often with input filters for
     * postal code formats) or `Label`s for display would bind to this property. This allows
     * the UI to display the current zip code and enables users to modify it, with changes
     * automatically reflected in the model.
     * </p>
     *
     * @return The {@link StringProperty} for {@code zipCode}, facilitating postal code data binding.
     */
    public StringProperty zipCodeProperty() {
        return zipCode;
    }
    /**
     * <p>
     * This method provides direct access to the {@link ObjectProperty} that encapsulates
     * the geographical latitude coordinate of the location as a {@link Double}. This method
     * is specifically designed for robust data binding with JavaFX UI controls that handle
     * numerical coordinates. `TextField`s or `Spinner`s for numerical input would typically
     * bind bidirectionally to this property. This allows users to conveniently input or adjust
     * the latitude, with changes instantly reflected in the model. Crucially, any validation
     * logic for this coordinate is contained within the associated {@link #setLatitude(Double)} method.
     * </p>
     *
     * @return The {@link ObjectProperty} for {@code latitude}, supporting double-precision coordinate binding.
     */
    public ObjectProperty<Double> latitudeProperty() {
        return latitude;
    }
    /**
     * <p>
     * This method provides direct access to the {@link ObjectProperty} that encapsulates
     * the geographical longitude coordinate of the location as a {@link Double}. This method
     * facilitates robust data binding with JavaFX UI controls designed for numerical coordinates.
     * Similar to `latitudeProperty()`, `TextField`s or `Spinner`s would typically bind
     * to this property. It is designed to allow for `null` values if the coordinates are
     * not explicitly provided. Its associated {@link #setLongitude(Double)} method includes
     * crucial validation to ensure the value falls within the correct geographical range.
     * </p>
     *
     * @return The {@link ObjectProperty} for {@code longitude}, enabling double-precision coordinate binding.
     */
    public ObjectProperty<Double> longitudeProperty() {
        return longitude;
    }
    /**
     * <p>
     * This method provides direct access to the {@link StringProperty} that encapsulates
     * any supplementary notes or comments recorded for the location. It is primarily used
     * for binding with JavaFX UI components that handle free-form text. A `TextArea` or
     * a multi-line `TextField` would typically bind to this property, allowing users to
     * enter or review detailed textual notes about the location. This enables flexible
     * input and display of unstructured information vital for operational context.
     * </p>
     *
     * @return The {@link StringProperty} for {@code notes}, supporting flexible text input and display.
     */
    public StringProperty notesProperty() {
        return notes;
    }

    // --- Value Getters and Setters ---

    /**
     * <p>
     * This method retrieves the current integer value of the location's unique identifier.
     * It directly accesses the value held by the {@link #locationIDProperty()}. This getter
     * is principally used by Data Access Objects (`LocationDAO`) to retrieve the ID for
     * database queries (e.g., `SELECT * FROM Locations WHERE LocationID = ?`) or for
     * establishing foreign key relationships in other tables. Furthermore, internal object
     * comparison methods like `equals()` and `hashCode()` rely on this getter to obtain
     * the value used for identity checks. Any business logic that needs to refer to the
     * location's primary key without direct UI binding would also utilize this method.
     * </p>
     *
     * @return The {@link Integer} primary key of the location, or {@code null} if the location
     * object has not yet been persisted and assigned an ID by the database.
     */
    public Integer getLocationID() {
        return locationID.get();
    }
    /**
     * <p>
     * This package-private method sets the unique ID for this location. Its specific design
     * targets use exclusively by the Data Access Object (DAO) layer. When a new {@code Location}
     * object is inserted into a database that employs auto-incrementing primary keys, this
     * method is invoked by the DAO to populate the `locationID` property with the newly generated
     * ID retrieved from the database. A crucial constraint governs its use: this method can
     * only be called once per `Location` object if the `locationID` is currently {@code null}.
     * This restriction reinforces the immutability of the primary key once it has been assigned,
     * diligently preventing any accidental alteration of a persistent entity's fundamental identity.
     * </p>
     *
     * @param id The unique integer ID assigned by the database after a successful insert operation.
     * @throws IllegalStateException if the {@code locationID} has already been assigned (i.e., is not {@code null})
     * when this method is called, reinforcing its read-once-after-creation nature.
     */
    void _setLocationID(Integer id) { // Package-private for DAO use only
        if (this.locationID.get() != null) {
            throw new IllegalStateException("Location ID cannot be changed once set.");
        }
        ((SimpleObjectProperty<Integer>) this.locationID).set(id);
    }
    /**
     * <p>
     * This method retrieves the current string value of the location's human-readable name.
     * It directly accesses the value encapsulated by the {@link #locationNameProperty()}.
     * This getter is typically used by Data Access Objects (DAOs) for reading data from the model
     * before persisting it, or by any backend logic that requires the raw string value of the
     * location name for processing or display in non-UI contexts.
     * </p>
     *
     * @return A short, descriptive string name for the location (e.g., "South Lot").
     */
    public String getLocationName() {
        return locationName.get();
    }
    /**
     * <p>
     * This method sets the human-readable name for the location. It updates the underlying
     * {@link #locationNameProperty()} and incorporates critical validation logic. This setter
     * strictly ensures that the `locationName` provided is not {@code null} and is not an
     * empty string after any leading or trailing whitespace has been removed using `strip()`.
     * If these constraints are violated, an appropriate {@link NullPointerException} or
     * {@link IllegalArgumentException} is thrown, thereby guaranteeing data integrity. This
     * method is invoked when creating a new `Location` object, when a user updates a location
     * record from a form in the UI (with changes flowing through the property binding to this setter),
     * or by DAOs when populating an object from a database record.
     * </p>
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
        this.locationName.set(trimmedName);
    }
    /**
     * <p>
     * This method retrieves the current string value representing the complete street address
     * for this location. It directly accesses the value encapsulated by the {@link #addressProperty()}.
     * This getter is primarily utilized by Data Access Objects (DAOs) for reading data from the model
     * for persistence operations, or by any backend component that requires the raw string value of
     * the location's address for internal processing or non-UI display, such as geocoding.
     * </p>
     *
     * @return A string representing the complete street address.
     */
    public String getAddress() {
        return address.get();
    }
    /**
     * <p>
     * This method sets the complete street address for this location. It updates the underlying
     * {@link #addressProperty()} and includes important validation logic. This setter strictly
     * ensures that the `address` value provided is not {@code null} and is not an empty string
     * after any leading or trailing whitespace has been removed using `strip()`. If these
     * constraints are not met, appropriate exceptions are thrown, safeguarding data quality.
     * This method is typically invoked during object creation, when a user updates the address
     * in a location form within the UI (via property binding), or by a DAO when populating
     * an object from a database record.
     * </p>
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
        this.address.set(trimmedAddress);
    }
    /**
     * <p>
     * This method retrieves the current string value of the city where this location is situated.
     * It directly accesses the value encapsulated by the {@link #cityProperty()}. This getter
     * is used by Data Access Objects (DAOs) for data retrieval operations, or by any application
     * logic that needs the raw string value of the location's city for internal processing
     * or reporting outside of UI binding contexts.
     * </p>
     *
     * @return The string name of the city.
     */
    public String getCity() {
        return city.get();
    }
    /**
     * <p>
     * This method sets the city name for this location. It updates the underlying
     * {@link #cityProperty()} and incorporates essential validation. This setter ensures
     * that the `city` provided is not {@code null} and is not an empty string after
     * trimming any leading or trailing whitespace. Failure to meet these criteria will
     * result in an appropriate exception. This method is invoked when a `Location` object
     * is instantiated, when a user modifies the city in the UI (through property binding),
     * or by a DAO during the data loading process from persistent storage.
     * </p>
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
        this.city.set(trimmedCity);
    }
    /**
     * <p>
     * This method retrieves the current string value of the state abbreviation where this
     * location is situated. It directly accesses the value encapsulated by the
     * {@link #stateProperty()}. This getter is used by Data Access Objects (DAOs) for
     * data retrieval, or by any backend logic requiring the raw string value of the state
     * for calculations or non-UI display.
     * </p>
     *
     * @return The string abbreviation of the state (e.g., "CA").
     */
    public String getState() {
        return state.get();
    }
    /**
     * <p>
     * This method sets the state or province abbreviation for this location. It updates the
     * underlying {@link #stateProperty()} and includes robust validation to ensure data integrity.
     * This setter verifies that the `state` is not {@code null} and is not an empty string
     * after trimming whitespace. This strict validation helps maintain data quality for
     * geographical categorization and accurate reporting. This method is invoked during
     * object construction, when a user inputs or modifies the state in a location form
     * within the UI (with changes propagated via property binding), or by a DAO when
     * loading data from persistent storage.
     * </p>
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
        this.state.set(trimmedState);
    }
    /**
     * <p>
     * This method retrieves the current string value of the postal zip code associated with
     * this location. It directly accesses the value held by the {@link #zipCodeProperty()}.
     * This getter is used by Data Access Objects (DAOs) for data retrieval, or by any backend
     * logic that needs the raw string value of the zip code for calculations or internal processing.
     * </p>
     *
     * @return The string representing the postal code.
     */
    public String getZipCode() {
        return zipCode.get();
    }
    /**
     * <p>
     * This method assigns a postal zip code to the location. It updates the underlying
     * {@link #zipCodeProperty()} and includes essential validation. This setter enforces
     * that the `zipCode` provided is not {@code null} and is not an empty string after
     * trimming any leading or trailing whitespace. If these conditions are violated, an
     * appropriate exception is thrown to prevent invalid entries and maintain data integrity.
     * This method is invoked during object initialization, when a user updates the zip code
     * in the UI (through property binding), or by a DAO when populating a `Location` object
     * from the database.
     * </p>
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
        this.zipCode.set(trimmedZipCode);
    }
    /**
     * <p>
     * This method retrieves the current {@link Double} value representing the geographical
     * latitude coordinate of the location. It directly accesses the value held by the
     * {@link #latitudeProperty()}. This getter is used by Data Access Objects (DAOs) for
     * data retrieval, or by any backend logic that needs the raw `Double` value of the
     * latitude for calculations, mapping, or internal processing. This property can be
     * {@code null} if the coordinates are not explicitly set, in which case geocoding
     * from the address may be required.
     * </p>
     *
     * @return The latitude as a {@link Double}, or {@code null} if not set.
     */
    public Double getLatitude() {
        return latitude.get();
    }
    /**
     * <p>
     * This method assigns the geographical latitude coordinate to the location. It updates the
     * underlying {@link #latitudeProperty()} and includes robust validation. This setter verifies
     * that if a `latitude` value is provided (i.e., not {@code null}), it falls within the
     * valid geographical range of -90.0 to +90.0 degrees. If the provided latitude is outside
     * this range, an {@link IllegalArgumentException} is thrown, preventing the storage of
     * invalid geographical data. This field is optional, allowing it to be {@code null} if
     * precise coordinates are not available or are to be derived from the address. This method
     * is invoked during object initialization, when a user inputs or modifies the latitude in
     * a UI form (through property binding), or by a DAO when populating an object from the database.
     * </p>
     *
     * @param latitude The latitude as a {@link Double} for the location. Can be {@code null}.
     * If not null, must be between -90.0 and +90.0.
     * @throws IllegalArgumentException if the {@code latitude} is outside the valid range of -90.0 to +90.0 (if provided).
     */
    public void setLatitude(Double latitude) {
        if (latitude != null) {
            if (latitude < -90.0 || latitude > 90.0) {
                throw new IllegalArgumentException("Latitude must be between -90.0 and +90.0.");
            }
        }
        this.latitude.set(latitude);
    }
    /**
     * <p>
     * This method retrieves the current {@link Double} value representing the geographical
     * longitude coordinate of the location. It directly accesses the value held by the
     * {@link #longitudeProperty()}. This getter is used by Data Access Objects (DAOs) for
     * data retrieval, or by any backend logic that needs the raw `Double` value of the
     * longitude for calculations, mapping, or internal processing. This property can be
     * {@code null} if the coordinates are not explicitly set, indicating a reliance on
     * geocoding from the address.
     * </p>
     *
     * @return The longitude as a {@link Double}, or {@code null} if not set.
     */
    public Double getLongitude() {
        return longitude.get();
    }
    /**
     * <p>
     * This method assigns the geographical longitude coordinate to the location. It updates the
     * underlying {@link #longitudeProperty()} and includes robust validation. This setter verifies
     * that if a `longitude` value is provided (i.e., not {@code null}), it falls within the
     * valid geographical range of -180.0 to +180.0 degrees. If the provided longitude is outside
     * this range, an {@link IllegalArgumentException} is thrown, preventing the storage of
     * invalid geographical data. This field is optional, allowing it to be {@code null} if
     * precise coordinates are not available or are to be derived from the address. This method
     * is invoked during object initialization, when a user inputs or modifies the longitude in
     * a UI form (through property binding), or by a DAO when populating an object from the database.
     * </p>
     *
     * @param longitude The longitude as a {@link Double} for the location. Can be {@code null}.
     * If not null, must be between -180.0 and +180.0.
     * @throws IllegalArgumentException if the {@code longitude} is outside the valid range of -180.0 to +180.0 (if provided).
     */
    public void setLongitude(Double longitude) {
        if (longitude != null) {
            if (longitude < -180.0 || longitude > 180.0) {
                throw new IllegalArgumentException("Longitude must be between -180.0 and +180.0.");
            }
        }
        this.longitude.set(longitude);
    }
    /**
     * <p>
     * This method retrieves any additional notes or remarks stored for this location. It directly
     * accesses the value held by the {@link #notesProperty()}. This getter is used by Data Access
     * Objects (DAOs) for data retrieval, or by any backend logic that needs the raw textual notes
     * for internal processing or display outside of UI binding contexts. It can return {@code null}
     * if no notes are currently present, indicating an empty notes field.
     * </p>
     *
     * @return A string containing notes, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes.get();
    }
    /**
     * <p>
     * This method assigns additional notes or free-form commentary to this location record.
     * It updates the underlying {@link #notesProperty()}. If the provided `notes` string
     * is not {@code null}, leading and trailing whitespace will be automatically stripped
     * using the `strip()` method before the value is set. This helps in maintaining cleaner
     * data and preventing extraneous whitespace from being stored. The field can also be
     * explicitly set to {@code null} if no notes are required for the location. This method
     * is invoked during object creation, when a user adds or modifies notes in a UI form
     * (e.g., a `TextArea`, with changes propagated via property binding), or by a DAO
     * during data loading from persistent storage. This field provides valuable flexibility
     * for recording unstructured information critical for comprehensive operational context.
     * </p>
     *
     * @param notes A free-form text string to store supplemental information. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes.set((notes != null) ? notes.strip() : null);
    }

    // --- Utilities ---

    /**
     * <p>
     * This method returns a concise string representation of the {@code Location} object.
     * It is primarily intended for debugging, logging, and quick display in console output,
     * providing a human-readable summary of the location's key identifying attributes.
     * The format includes the location's ID, name, city, state, and geographical coordinates.
     * These values are obtained by calling their respective getter methods (e.g., `getLocationID()`,
     * `getLocationName()`), which in turn query the underlying JavaFX properties. This
     * ensures that the `toString()` output consistently reflects the current state of the
     * object's properties, making it useful for verifying object state at any given time.
     * </p>
     *
     * @return A string in the format:
     * "Location{ID=..., Name='...', City='...', State='...', Lat=..., Lng=...}"
     */
    @Override
    public String toString() {
        return "Location{" +
                "locationID=" + getLocationID() +
                ", locationName='" + getLocationName() + '\'' +
                ", city='" + getCity() + '\'' +
                ", state='" + getState() + '\'' +
                ", latitude=" + getLatitude() +
                ", longitude=" + getLongitude() +
                '}';
    }
    /**
     * <p>
     * This method determines whether this {@code Location} object is "equal to" another object.
     * The comparison for equality is primarily based on the unique {@code locationID}. This
     * approach ensures that two `Location` objects are considered equal if they represent
     * the same entity in the underlying database, regardless of whether other attributes
     * (like `notes` or `address`) might differ in memory. This method adheres strictly
     * to the general contract of the {@link Object#equals(Object)} method, guaranteeing
     * properties such as reflexivity, symmetry, transitivity, consistency, and correct
     * handling of null comparisons. It correctly handles scenarios where the `locationID`
     * might be {@code null} for unpersisted entities, relying on
     * {@link Objects#equals(Object, Object)} for a null-safe comparison of the ID values.
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument (based on `locationID`);
     * {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Optimization: Same object reference indicates equality
        if (o == null || getClass() != o.getClass()) return false; // Null object or different class type
        Location location = (Location) o; // Cast to Location for property access
        // Equality is determined by the unique primary key, safely handling null IDs for new objects
        return Objects.equals(getLocationID(), location.getLocationID());
    }
    /**
     * <p>
     * This method computes and returns a hash code value for this {@code Location} object.
     * It is provided to support the efficient operation of hash tables, such as those
     * implemented by {@link java.util.HashMap} and {@link java.util.HashSet}. The hash
     * code generation is based solely on the unique {@code locationID}. This design choice
     * is crucial for consistency with the `equals()` method, ensuring that any two `Location`
     * objects considered equal (i.e., having the same `locationID`) will invariably produce
     * the same hash code. If the `locationID` is {@code null} (typically for an unpersisted
     * entity), its hash code will gracefully default to 0, as per the behavior of
     * {@link Objects#hash(Object...)}, maintaining proper collection behavior.
     * </p>
     *
     * @return A hash code value for this object, derived from its {@code locationID}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getLocationID()); // Hash code based on the primary key
    }
}