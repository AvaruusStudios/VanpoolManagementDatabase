package com.avaruusstudios.vmdb.model;

import javafx.beans.property.*; // Essential JavaFX property classes for reactive data binding

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * <p>
 * Represents a specific vehicle within the Vanpool Management System (VMDB). This model
 * encapsulates all pertinent metadata and operational characteristics necessary to track
 * and manage individual vehicles. Key information includes the vehicle's unique identification,
 * its physical specifications (make, model, year, seating capacity), lease agreement details,
 * active operational status, and any applicable financial discounts.
 * </p>
 *
 * <p>
 * The `Vehicle` class is designed to support the Vanpool Management System's graphical user
 * interface (GUI) by leveraging JavaFX Properties. These properties enable automatic and
 * reactive data binding, meaning that changes to vehicle data within the UI will
 * instantaneously update the underlying `Vehicle` object, and vice-versa. This mechanism
 * significantly reduces the boilerplate code typically required to synchronize data
 * between the application's business logic and its presentation layer.
 * </p>
 *
 * <p>
 * This object directly reflects the schema used for persistent storage of vehicle records
 * in the database. It is a fundamental component for managing the fleet, assigning vehicles
 * to {@link Vanpool}s, tracking operational status, and calculating associated costs.
 * </p>
 *
 * @see Vanpool
 * @see com.avaruusstudios.vmdb.dao.VehicleDAO // Implied for data persistence
 * @see javafx.beans.property
 */
public class Vehicle {
    /**
     * <p>
     * This {@link ReadOnlyObjectProperty} holds the unique numerical identifier for the vehicle, serving
     * as the primary key for the vehicle record in the underlying database (e.g., SQLite's
     * `VehicleID INTEGER PRIMARY KEY AUTOINCREMENT`). This property is crucial for database
     * operations: when a new `Vehicle` object is created and subsequently inserted into the database,
     * the database assigns a unique `VehicleID` which is then populated into this property.
     * This makes the `Vehicle` object fully identifiable and distinguishable from other records.
     * Similarly, when `Vehicle` records are fetched from the database, this property is populated
     * with the corresponding ID, allowing the application to reference and work with existing
     * vehicle entities. In the user interface, particularly in tabular displays (e.g., a `TableView`
     * of vehicles), this ID can be displayed to users for unique identification of each vehicle record.
     * Internally, the `equals()` and `hashCode()` methods of this class primarily rely on the value
     * of this property to determine object uniqueness and for proper functioning in collections
     * like `HashMap` or `HashSet`.
     * </p>
     * <p>
     * For a newly instantiated `Vehicle` object that has not yet been persisted to the database,
     * this property's value will be {@code null}. Once the vehicle record is successfully
     * inserted into the database and an ID is assigned, this property is set via the
     * {@link #setVehicleID(Integer)} method. As a `ReadOnlyObjectProperty`, its value
     * is intended to be immutable after its initial assignment by the database,
     * reflecting its role as a permanent identifier. Attempting to change it after
     * it has been set will result in an {@link IllegalStateException}.
     * </p>
     */
    private final ReadOnlyObjectProperty<Integer> vehicleID;

    /**
     * <p>
     * This {@link StringProperty} stores a unique alphanumeric identifier for the vehicle,
     * such as a physical license plate number, an internal fleet number, or any other unique
     * string used to distinguish one vehicle from another within the system. This property
     * is prominently displayed in vehicle lists, forms, and reports within the user interface,
     * providing users with an easily recognizable identifier for each vehicle. It is
     * actively used during data entry when users input this value while creating or editing
     * vehicle records. The {@link #setVehicleNumber(String)} method includes robust
     * validation to ensure the value is not {@code null} or an empty string after trimming
     * whitespace, which guarantees data integrity. Furthermore, this property is employed
     * by users or backend logic for searching and filtering specific vehicles and is always
     * included in various reports for clear vehicle identification.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Changes to this property in the UI, when bound using JavaFX's binding mechanisms,
     * will automatically reflect in the model, and vice-versa. The underlying string value
     * can be accessed or modified via {@link #getVehicleNumber()} and {@link #setVehicleNumber(String)},
     * or by directly interacting with the property object via {@link #vehicleNumberProperty()}.
     * </p>
     */
    private final StringProperty vehicleNumber;

    /**
     * <p>
     * This {@link StringProperty} stores the name of the vehicle's manufacturer or brand,
     * such as "Ford", "Toyota", or "Honda". This attribute is presented to users in vehicle
     * details, lists, and dropdowns within the user interface, aiding in the categorization
     * and identification of vehicles. During data entry, users either select or input the make
     * when adding or modifying vehicle records; the {@link #setMake(String)} method includes
     * validation to ensure this field is neither {@code null} nor an empty string. From a fleet
     * management perspective, this property is vital for inventory management, compiling fleet
     * statistics, and informing procurement planning, as it enables analysis based on manufacturer.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Changes made to its value through UI binding are automatically propagated to the model.
     * The underlying string value is managed by the {@link #makeProperty()} and can be accessed
     * or modified through {@link #getMake()} and {@link #setMake(String)}.
     * </p>
     */
    private final StringProperty make;

    /**
     * <p>
     * This {@link StringProperty} holds the specific model name of the vehicle, for example,
     * "Transit", "Sienna", or "Odyssey", as designated by the manufacturer. This attribute
     * is essential for distinguishing between different vehicle types from the same manufacturer
     * within vehicle lists and detail views in the user interface. When entering vehicle
     * information, users specify the model, and the {@link #setModel(String)} method performs
     * validation to ensure this field is neither {@code null} nor an empty string. For maintenance
     * and parts lookup, this model name can be used by maintenance personnel to find vehicle-specific
     * information, such as part compatibility or recommended service schedules.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #modelProperty()} and can be accessed or modified
     * via {@link #getModel()} and {@link #setModel(String)}. Changes are automatically
     * reflected in bound UI elements.
     * </p>
     */
    private final StringProperty model;

    /**
     * <p>
     * This {@link IntegerProperty} stores the four-digit year of manufacture for the vehicle,
     * such as 2020. This attribute is crucial for tracking the age of the vehicle and is used
     * for compliance assessment or valuation purposes. In the user interface, it is presented
     * in vehicle detail views and lists to provide a quick overview of the vehicle's age.
     * During data entry, users input the year when registering a vehicle; the {@link #setYear(int)}
     * method includes robust validation to ensure the year is within a reasonable historical
     * range (e.g., not before 1900) and not in the future (not greater than the current year).
     * For fleet planning, administrators utilize this property for rotation strategies, identifying
     * vehicles due for replacement based on age, and assessing depreciation. It also frequently
     * serves as a key filter or grouping criterion in various fleet reports.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #yearProperty()} and can be accessed or modified
     * via {@link #getYear()} and {@link #setYear(int)}. Changes are automatically
     * reflected in bound UI elements.
     * </p>
     */
    private final IntegerProperty year;

    /**
     * <p>
     * This {@link IntegerProperty} specifies the maximum number of active participants that
     * the vehicle is legally and safely able to accommodate. This refers to the seating capacity
     * available for passengers (excluding the driver, if applicable, depending on how "participant"
     * is defined for capacity). In the user interface, this property is displayed in vehicle details
     * and is actively used when assigning participants to a vanpool to ensure that capacity limits
     * are respected. During data entry, users input this value, and the {@link #setCapacity(int)}
     * method rigorously enforces that the capacity must be a positive integer, preventing invalid
     * or illogical entries. From a system logic perspective, this property is utilized to determine
     * if a vehicle can accommodate additional participants in a given vanpool. For administrators,
     * it is an important metric for understanding the overall carrying capability of the fleet
     * and for making efficient assignments.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #capacityProperty()} and can be accessed or modified
     * via {@link #getCapacity()} and {@link #setCapacity(int)}. Changes are automatically
     * reflected in bound UI elements.
     * </p>
     */
    private final IntegerProperty capacity;

    /**
     * <p>
     * This {@link ObjectProperty} stores the {@link LocalDate} representing the official
     * start date of the vehicle's lease agreement or its entry into active service. This date
     * is crucial for tracking the vehicle's operational lifespan and for calculating remaining
     * lease terms. In the user interface, it is presented in vehicle detail views and forms,
     * typically bound to date pickers or similar UI controls which allow users to select or input
     * the date. The {@link #setLeaseStartDate(LocalDate)} method ensures that it is not
     * {@code null} and performs validation against the {@link #leaseEndDateProperty()} to ensure
     * chronological consistency, preventing a start date from being set after an existing end date.
     * For lease management, this property is essential for financial tracking, lease agreement renewals,
     * and calculating the duration a vehicle has been in service. It is also a common filter
     * criterion in reports to analyze fleet longevity or filter vehicles by their service start dates.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #leaseStartDateProperty()} and can be accessed or modified
     * via {@link #getLeaseStartDate()} and {@link #setLeaseStartDate(LocalDate)}. Changes are
     * automatically reflected in bound UI elements.
     * </p>
     */
    private final ObjectProperty<LocalDate> leaseStartDate;

    /**
     * <p>
     * This {@link ObjectProperty} stores the {@link LocalDate} when the vehicle's lease or
     * service period is officially scheduled to conclude. This date provides a target for
     * vehicle retirement, lease renewal, or reassignment. In the user interface, it is presented
     * in vehicle detail views and forms, typically bound to date pickers. The
     * {@link #setLeaseEndDate(LocalDate)} method allows the value to be {@code null} for
     * indefinite leases but rigorously validates it against the {@link #leaseStartDateProperty()}
     * to ensure it is not chronologically before the start date and that a start date exists
     * if an end date is set. This property is crucial for lease management, enabling the
     * planning of vehicle rotations, budgeting for new vehicle acquisitions, and managing
     * lease expiration workflows. It can also be used by the system to trigger automated
     * alerts or notifications as a lease end date approaches.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #leaseEndDateProperty()} and can be accessed or modified
     * via {@link #getLeaseEndDate()} and {@link #setLeaseEndDate(LocalDate)}. Changes are
     * automatically reflected in bound UI elements.
     * </p>
     */
    private final ObjectProperty<LocalDate> leaseEndDate;

    /**
     * <p>
     * This {@link BooleanProperty} indicates the current operational status of the vehicle.
     * A value of {@code true} signifies that the vehicle is active and currently available
     * for use in vanpools, while {@code false} indicates that the vehicle is inactive,
     * which could mean it is retired, undergoing maintenance, or otherwise unavailable for service.
     * In the user interface, this status is often represented by a checkbox, toggle switch,
     * or a status indicator in vehicle lists and detail forms, providing an immediate visual
     * cue of availability. Users can easily toggle this status. From an operational perspective,
     * this flag is used extensively in the backend and UI to filter lists of vehicles, allowing
     * the display of only active vehicles for assignment or only inactive vehicles for review.
     * This greatly assists administrators in quickly identifying and allocating available assets.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #isActiveProperty()} and can be accessed or modified
     * via {@link #isActive()} (following boolean getter conventions) and {@link #setActive(boolean)}.
     * Changes are automatically reflected in bound UI elements.
     * </p>
     */
    private final BooleanProperty isActive;

    /**
     * <p>
     * This {@link ObjectProperty} stores a {@link BigDecimal} value representing any
     * recurring monthly discount amount associated with this vehicle. Such a discount
     * might be provided by a government agency, a sponsoring organization, or a specific
     * program to reduce the overall monthly cost for the vanpool. Using {@link BigDecimal}
     * ensures accurate financial calculations, crucially avoiding floating-point inaccuracies
     * inherent with `double` or `float` for monetary values. In the user interface, this
     * property is presented in financial summaries and vehicle cost breakdowns, with input
     * fields typically bound to it. During data entry, users input this monetary value,
     * and the {@link #setDiscount(BigDecimal)} method validates that the value is not
     * {@code null} and is non-negative, preventing illogical financial entries. This property
     * is integral to financial calculations, being integrated into the logic for monthly vanpool
     * charges, participant fees, and overall program budgeting. It is also essential for financial
     * reports, demonstrating how discounts impact the economics of the vanpool program.
     * </p>
     * <p>
     * This property is mutable, allowing its value to be changed after object creation.
     * Its value is managed by the {@link #discountProperty()} and can be accessed or modified
     * via {@link #getDiscount()} and {@link #setDiscount(BigDecimal)}. Changes are
     * automatically reflected in bound UI elements.
     * </p>
     */
    private final ObjectProperty<BigDecimal> discount;

    /**
     * <p>
     * This {@link StringProperty} holds optional free-form text for additional notes or
     * administrative comments specific to this vehicle. This field provides flexibility
     * to contain diverse information such as maintenance history summaries, details on custom
     * configurations (e.g., "wheelchair accessible"), specific operational instructions, or
     * any other relevant remarks that do not fit into other structured fields. In the user
     * interface, this property is typically shown in a multi-line text area or a dedicated
     * "Notes" section within the vehicle detail view, allowing users to easily review or add
     * comments. It serves as a flexible field for administrators or staff to record unstructured
     * information about the vehicle, with the {@link #setNotes(String)} method automatically
     * stripping leading and trailing whitespace if the provided notes are not {@code null}.
     * This property acts as a quick operational reference for staff when managing or dispatching
     * vehicles, providing immediate contextual information.
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
     * This is the default constructor for creating a new, unpersisted {@code Vehicle} object.
     * Its primary use case is for frameworks or scenarios where an object needs to be
     * instantiated without initial data, with its properties being set subsequently via their
     * respective setters. Upon invocation, all internal JavaFX property fields are initialized
     * with default or `null` values: `vehicleID` is explicitly set to {@code null} to indicate
     * an unpersisted entity; `StringProperty`s (`vehicleNumber`, `make`, `model`, `notes`) are
     * initialized with empty strings (`""`); `IntegerProperty`s (`year`, `capacity`) are set to `0`;
     * and `ObjectProperty`s (`leaseStartDate`, `leaseEndDate`, `discount`) are initialized as `null`.
     * The specific value setters are then called with these default values to ensure that
     * any associated validation logic (e.g., for `year` or `capacity`) is applied, even for
     * an empty object, maintaining consistency from the moment of instantiation.
     * </p>
     */
    public Vehicle() {
        this(null, null, null, null, 0, 0, null, null, false, null, null);
    }

    /**
     * <p>
     * This comprehensive constructor initializes all fields of a {@code Vehicle} instance. It is
     * particularly useful when loading an *existing* vehicle record from a database, as it accepts
     * a pre-assigned {@code vehicleID}. Upon invocation, all internal JavaFX property fields
     * are first instantiated, establishing the reactive binding capabilities of the object.
     * Subsequently, each provided parameter is passed to its corresponding property setter method.
     * This systematic approach ensures that all field-level validation rules—such as checks for
     * non-null strings, valid numerical ranges, and chronological consistency for dates—are uniformly
     * applied. This guarantees data integrity regardless of whether the object is being created
     * from scratch (e.g., from user input) or populated from a data source. The order of setting
     * properties is carefully considered, especially for interdependent validations, such as
     * `leaseEndDate` which relies on `leaseStartDate` being set first.
     * </p>
     *
     * @param vehicleID             The unique integer ID for the vehicle. This parameter should be
     * non-{@code null} when instantiating a `Vehicle` that already exists
     * in the database. It is used to initialize the {@link #vehicleIDProperty()}.
     * @param vehicleNumber         The unique alphanumeric identifier for the vehicle (e.g., license plate).
     * This value initializes {@link #vehicleNumberProperty()}.
     * @param make                  The manufacturer's brand name (e.g., "Ford"). Initializes {@link #makeProperty()}.
     * @param model                 The specific model name of the vehicle (e.g., "Transit"). Initializes {@link #modelProperty()}.
     * @param year                  The year of manufacture (e.g., 2021). Initializes {@link #yearProperty()}.
     * @param capacity              The maximum seating capacity for active participants. Initializes {@link #capacityProperty()}.
     * @param leaseStartDate        The {@link LocalDate} when the vehicle lease or service period began.
     * Initializes {@link #leaseStartDateProperty()}.
     * @param leaseEndDate          The {@link LocalDate} when the lease is scheduled to end (can be {@code null}).
     * Initializes {@link #leaseEndDateProperty()}.
     * @param isActive              A boolean flag indicating if the vehicle is currently active and in use.
     * Initializes {@link #isActiveProperty()}.
     * @param discount              The recurring monthly discount amount associated with this vehicle.
     * Initializes {@link #discountProperty()}.
     * @param notes                 Optional notes or comments about the vehicle. Initializes {@link #notesProperty()}.
     *
     * @throws NullPointerException     If `vehicleID` (when loading an existing vehicle) or any
     * other required object-type parameters (e.g., `vehicleNumber`, `make`, `model`,
     * `leaseStartDate`, `discount`) are {@code null}.
     * @throws IllegalArgumentException If required string parameters are empty after trimming,
     * `year` is outside valid bounds, `capacity` is not positive,
     * `discount` is negative, or lease dates are logically inconsistent
     * (e.g., end date before start date).
     * @throws IllegalStateException    If `leaseEndDate` is provided as non-null but `leaseStartDate`
     * is {@code null} at the point `setLeaseEndDate` is called, violating chronological dependency.
     */
    public Vehicle(Integer vehicleID,
                   String vehicleNumber, String make, String model, int year, int capacity,
                   LocalDate leaseStartDate, LocalDate leaseEndDate, boolean isActive,
                   BigDecimal discount, String notes) {
        // Initialize JavaFX properties with default values or nulls for non-primitive types
        this.vehicleID = new SimpleObjectProperty<>(this, "vehicleID", vehicleID);
        this.vehicleNumber = new SimpleStringProperty(this, "vehicleNumber");
        this.make = new SimpleStringProperty(this, "make");
        this.model = new SimpleStringProperty(this, "model");
        this.year = new SimpleIntegerProperty(this, "year");
        this.capacity = new SimpleIntegerProperty(this, "capacity");
        this.leaseStartDate = new SimpleObjectProperty<>(this, "leaseStartDate");
        this.leaseEndDate = new SimpleObjectProperty<>(this, "leaseEndDate");
        this.isActive = new SimpleBooleanProperty(this, "isActive");
        this.discount = new SimpleObjectProperty<>(this, "discount");
        this.notes = new SimpleStringProperty(this, "notes");

        // Utilize the property setters to apply initial values and invoke validation logic.
        // The order is important for interdependent validations (e.g., leaseEndDate depends on leaseStartDate).
        setVehicleNumber(vehicleNumber);
        setMake(make);
        setModel(model);
        setYear(year);
        setCapacity(capacity);
        setLeaseStartDate(leaseStartDate);
        setLeaseEndDate(leaseEndDate);
        setActive(isActive);
        setDiscount(discount);
        setNotes(notes);
    }

    /**
     * <p>
     * This convenience constructor is specifically designed for creating a new {@code Vehicle} object
     * that has not yet been saved to the database. It intentionally omits the {@code vehicleID}
     * parameter, as this identifier is typically auto-generated by the database during the
     * insertion process. This constructor efficiently delegates its initialization to the
     * {@link #Vehicle(Integer, String, String, String, int, int, LocalDate, LocalDate, boolean, BigDecimal, String)}
     * (the full constructor) by passing {@code null} for the `vehicleID`. All other parameters
     * are passed directly, ensuring that comprehensive validation logic within the property setters
     * is uniformly applied to the newly created vehicle instance, guaranteeing data quality
     * from the point of object creation.
     * </p>
     *
     * @param vehicleNumber         The unique alphanumeric identifier for the vehicle (e.g., license plate).
     * This value initializes {@link #vehicleNumberProperty()}.
     * @param make                  The manufacturer's brand name (e.g., "Ford"). Initializes {@link #makeProperty()}.
     * @param model                 The specific model name of the vehicle (e.g., "Transit"). Initializes {@link #modelProperty()}.
     * @param year                  The year of manufacture (e.g., 2021). Initializes {@link #yearProperty()}.
     * @param capacity              The maximum seating capacity for active participants. Initializes {@link #capacityProperty()}.
     * @param leaseStartDate        The {@link LocalDate} when the vehicle lease or service period began.
     * Initializes {@link #leaseStartDateProperty()}.
     * @param leaseEndDate          The {@link LocalDate} when the lease is scheduled to end (can be {@code null}).
     * Initializes {@link #leaseEndDateProperty()}.
     * @param isActive              A boolean flag indicating if the vehicle is currently active and in use.
     * Initializes {@link #isActiveProperty()}.
     * @param discount              The recurring monthly discount amount associated with this vehicle.
     * Initializes {@link #discountProperty()}.
     * @param notes                 Optional notes or comments about the vehicle. Initializes {@link #notesProperty()}.
     *
     * @throws NullPointerException     If any required object-type parameters (e.g., `vehicleNumber`, `make`, `model`,
     * `leaseStartDate`, `discount`) are {@code null}.
     * @throws IllegalArgumentException If required string parameters are empty after trimming,
     * `year` is outside valid bounds, `capacity` is not positive,
     * `discount` is negative, or lease dates are logically inconsistent.
     * @throws IllegalStateException    If `leaseEndDate` is provided as non-null but `leaseStartDate`
     * is {@code null} at the point `setLeaseEndDate` is called, violating chronological dependency.
     */
    public Vehicle(String vehicleNumber, String make, String model,
                   int year, int capacity, LocalDate leaseStartDate, LocalDate leaseEndDate,
                   boolean isActive, BigDecimal discount, String notes) {
        this(null, vehicleNumber, make, model, year, capacity, leaseStartDate, leaseEndDate, isActive, discount, notes);
    }

    // --- JavaFX Property Accessors ---

    /**
     * <p>
     * This method provides direct access to the {@link ReadOnlyObjectProperty} that encapsulates
     * the unique identifier for this vehicle. Its primary use is by JavaFX UI components,
     * such as `TableView` columns configured with a `PropertyValueFactory`, to directly bind
     * to and display the vehicle's ID. Since the `vehicleID` is typically assigned by the
     * database and should not be changed by the UI after creation, this property is designed
     * as `ReadOnly`. Data Access Objects (DAOs) will utilize a specific setter
     * ({@link #setVehicleID(Integer)}) for initial assignment after a database insert operation,
     * reflecting the ID's immutable nature once established.
     * </p>
     *
     * @return The {@link ReadOnlyObjectProperty} for {@code vehicleID}, allowing UI elements
     * to observe changes to this read-only identifier.
     */
    public ReadOnlyObjectProperty<Integer> vehicleIDProperty() {
        return vehicleID;
    }
    /**
     * <p>
     * This method provides direct access to the {@link StringProperty} that encapsulates
     * the vehicle's unique identification number (e.g., license plate or internal fleet number).
     * This is crucial for data binding in JavaFX applications. UI controls such as `TextField`s
     * for data input, `Label`s for display, or `TableView` columns for list presentation would
     * typically use `bindBidirectional()` or `PropertyValueFactory` with this property. This
     * creates a reactive link where any change in the UI field automatically updates the model,
     * and conversely, any programmatic change to the property in the model updates the UI.
     * </p>
     *
     * @return The {@link StringProperty} for {@code vehicleNumber}, enabling bidirectional data binding.
     */
    public StringProperty vehicleNumberProperty() {
        return vehicleNumber;
    }
    /**
     * <p>
     * This method provides direct access to the {@link StringProperty} that encapsulates
     * the vehicle's manufacturer's name (make). It is used by JavaFX UI components to establish
     * robust data binding connections. Similar to `vehicleNumberProperty()`, UI elements like
     * `TextField`s, `Label`s, or `ComboBox`es (if using a predefined list of makes) would
     * bind to this property to display and allow modification of the vehicle's make. This
     * ensures that changes in the UI are immediately reflected in the model and vice-versa.
     * </p>
     *
     * @return The {@link StringProperty} for {@code make}, facilitating reactive UI updates.
     */
    public StringProperty makeProperty() {
        return make;
    }
    /**
     * <p>
     * This method provides direct access to the {@link StringProperty} that encapsulates
     * the vehicle's specific model name. It is primarily used by JavaFX UI components for
     * data binding purposes. UI controls such as `TextField`s or `Label`s would bind to
     * this property to both display and allow modification of the vehicle's model. This
     * binding establishes a reactive link ensuring that any user input or programmatic
     * change is synchronized between the view and the model.
     * </p>
     *
     * @return The {@link StringProperty} for {@code model}, supporting bidirectional data binding.
     */
    public StringProperty modelProperty() {
        return model;
    }
    /**
     * <p>
     * This method provides direct access to the {@link IntegerProperty} that encapsulates
     * the vehicle's manufacturing year. It is primarily used by JavaFX UI components for
     * data binding. UI elements like `TextField`s (often with input filters for numerical
     * values), `Label`s for display, or `Spinner`s for incremental adjustments would bind
     * to this property. This enables the UI to display the current year and allows users
     * to modify it, with changes automatically reflected in the model. Input validation,
     * such as ensuring a valid year range, is handled by the associated {@link #setYear(int)} method.
     * </p>
     *
     * @return The {@link IntegerProperty} for {@code year}, enabling reactive updates in the UI.
     */
    public IntegerProperty yearProperty() {
        return year;
    }
    /**
     * <p>
     * This method provides direct access to the {@link IntegerProperty} that encapsulates
     * the vehicle's maximum participant capacity. It is primarily used by JavaFX UI components
     * for data binding. UI elements such as `TextField`s (which might have input filters
     * for numbers) or `Spinner`s would bind to this property to display and allow modification
     * of the vehicle's capacity. Validation, specifically ensuring a positive capacity value,
     * is enforced by the associated {@link #setCapacity(int)} method, ensuring data integrity.
     * </p>
     *
     * @return The {@link IntegerProperty} for {@code capacity}, facilitating numerical data binding.
     */
    public IntegerProperty capacityProperty() {
        return capacity;
    }
    /**
     * <p>
     * This method provides direct access to the {@link ObjectProperty} that encapsulates
     * the vehicle's lease start date as a {@link LocalDate}. This method is specifically
     * designed for robust data binding with JavaFX UI date controls. `DatePicker` controls
     * in the user interface, for instance, would bind directly to this property using
     * `bindBidirectional()`. This allows users to conveniently select or input the lease
     * start date, with changes instantly reflected in the model. Display-only elements
     * like `Label`s could also bind to this property to show the date. Crucially, any
     * validation logic for this date is contained within the associated
     * {@link #setLeaseStartDate(LocalDate)} method.
     * </p>
     *
     * @return The {@link ObjectProperty} for {@code leaseStartDate}, supporting date field binding.
     */
    public ObjectProperty<LocalDate> leaseStartDateProperty() {
        return leaseStartDate;
    }
    /**
     * <p>
     * This method provides direct access to the {@link ObjectProperty} that encapsulates
     * the vehicle's lease end date as a {@link LocalDate}. This method facilitates robust
     * data binding with JavaFX date selection UI components. Similar to
     * `leaseStartDateProperty()`, `DatePicker` controls in the UI would typically bind
     * to this property. It is designed to allow for `null` values if the lease is indefinite
     * or not yet determined. Its associated {@link #setLeaseEndDate(LocalDate)} method
     * includes crucial validation against the start date, ensuring chronological consistency
     * when an end date is present.
     * </p>
     *
     * @return The {@link ObjectProperty} for {@code leaseEndDate}, enabling date field binding.
     */
    public ObjectProperty<LocalDate> leaseEndDateProperty() {
        return leaseEndDate;
    }
    /**
     * <p>
     * This method provides direct access to the {@link BooleanProperty} that indicates
     * whether the vehicle is currently active and operational within the system. It is
     * specifically designed for binding with JavaFX UI controls that represent binary
     * (true/false) states. UI elements such as `CheckBox`es, `ToggleButton`s, or custom
     * switches would typically bind bidirectionally to this property. This allows users
     * to easily toggle the vehicle's active status, with the change instantly updating
     * the model. Display-only indicators (e.g., status lights or text labels) can also
     * observe this property to reflect the vehicle's current availability.
     * </p>
     *
     * @return The {@link BooleanProperty} for {@code isActive}, supporting boolean data binding.
     */
    public BooleanProperty isActiveProperty() {
        return isActive;
    }
    /**
     * <p>
     * This method provides direct access to the {@link ObjectProperty} that encapsulates
     * the vehicle's monthly discount amount, represented as a {@link BigDecimal}. It is
     * primarily used for binding with JavaFX UI components that handle monetary values.
     * `TextField`s designed for currency input (often integrated with formatters), or
     * `Label`s displaying financial figures, would bind to this property. Utilizing
     * `BigDecimal` and its corresponding `ObjectProperty` is critical for ensuring that
     * precise monetary values are handled without the floating-point inaccuracies
     * often associated with `double` or `float` types. Validation against negative
     * values is performed by the associated {@link #setDiscount(BigDecimal)} method
     * to maintain financial integrity.
     * </p>
     *
     * @return The {@link ObjectProperty} for {@code discount}, facilitating accurate monetary data binding.
     */
    public ObjectProperty<BigDecimal> discountProperty() {
        return discount;
    }
    /**
     * <p>
     * This method provides direct access to the {@link StringProperty} that encapsulates
     * any supplementary notes or comments recorded for the vehicle. It is primarily used
     * for binding with JavaFX UI components that handle free-form text. A `TextArea` or
     * a multi-line `TextField` would typically bind to this property, allowing users to
     * enter or review detailed textual notes about the vehicle. This enables flexible
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
     * This method retrieves the current integer value of the vehicle's unique identifier.
     * It directly accesses the value held by the {@link #vehicleIDProperty()}. This getter
     * is principally used by Data Access Objects (`VehicleDAO`) to retrieve the ID for
     * database queries (e.g., `SELECT * FROM Vehicles WHERE VehicleID = ?`) or for establishing
     * foreign key relationships in other tables. Furthermore, internal object comparison
     * methods like `equals()` and `hashCode()` rely on this getter to obtain the value used
     * for identity checks. Any business logic that needs to refer to the vehicle's primary
     * key without direct UI binding would also utilize this method.
     * </p>
     *
     * @return The {@link Integer} primary key of the vehicle, or {@code null} if the vehicle
     * object has not yet been persisted and assigned an ID by the database.
     */
    public Integer getVehicleID() {
        return vehicleID.get();
    }
    /**
     * <p>
     * This method sets the unique ID for this vehicle. Its specific design targets use by
     * the Data Access Object (DAO) layer. When a new {@code Vehicle} object is inserted
     * into a database that employs auto-incrementing primary keys, this method is invoked
     * by the DAO to populate the `vehicleID` property with the newly generated ID retrieved
     * from the database. A crucial constraint governs its use: this method can only be called
     * once per `Vehicle` object if the `vehicleID` is currently {@code null}. This restriction
     * reinforces the immutability of the primary key once it has been assigned, diligently
     * preventing any accidental alteration of a persistent entity's fundamental identity.
     * </p>
     *
     * @param id The unique integer ID assigned by the database after a successful insert operation.
     * @throws IllegalStateException if the {@code vehicleID} has already been assigned (i.e., is not {@code null})
     * when this method is called, reinforcing its read-once-after-creation nature.
     */
    public void setVehicleID(Integer id) {
        if (this.vehicleID.get() != null) {
            throw new IllegalStateException("Vehicle ID cannot be changed once set.");
        }
        // Safely cast to SimpleObjectProperty for modification, as it's the mutable implementation
        ((SimpleObjectProperty<Integer>) this.vehicleID).set(id);
    }
    /**
     * <p>
     * This method retrieves the current string value of the vehicle's unique identification number.
     * It directly accesses the value encapsulated by the {@link #vehicleNumberProperty()}.
     * This getter is typically used by Data Access Objects (DAOs) for reading data from the model
     * before persisting it, or by any backend logic that requires the raw string value of the
     * vehicle number for processing or display in non-UI contexts.
     * </p>
     *
     * @return The alphanumeric string representing the vehicle's unique number.
     */
    public String getVehicleNumber() {
        return vehicleNumber.get();
    }
    /**
     * <p>
     * This method sets the unique vehicle number that identifies it within the organization or
     * serves as its license plate. It updates the underlying {@link #vehicleNumberProperty()}
     * and incorporates critical validation logic. This setter strictly ensures that the
     * `vehicleNumber` provided is not {@code null} and is not an empty string after any leading
     * or trailing whitespace has been removed using `strip()`. If these constraints are violated,
     * an appropriate {@link NullPointerException} or {@link IllegalArgumentException} is thrown,
     * thereby guaranteeing data integrity. This method is invoked when creating a new `Vehicle`
     * object, when a user updates a vehicle record from a form in the UI (with changes flowing
     * through the property binding to this setter), or by DAOs when populating an object from
     * a database record.
     * </p>
     *
     * @param vehicleNumber The alphanumeric tracking code for internal reference or license plate.
     * Must not be {@code null} or empty.
     * @throws NullPointerException if {@code vehicleNumber} is {@code null}.
     * @throws IllegalArgumentException if {@code vehicleNumber} is empty after stripping whitespace.
     */
    public void setVehicleNumber(String vehicleNumber) {
        String trimmedNumber = Objects.requireNonNull(vehicleNumber, "Vehicle number cannot be null.").strip();
        if (trimmedNumber.isEmpty()) {
            throw new IllegalArgumentException("Vehicle number cannot be empty.");
        }
        this.vehicleNumber.set(trimmedNumber);
    }
    /**
     * <p>
     * This method retrieves the current string value representing the vehicle's manufacturer's
     * name (make). It directly accesses the value encapsulated by the {@link #makeProperty()}.
     * This getter is primarily utilized by Data Access Objects (DAOs) for reading data
     * from the model for persistence operations, or by any backend component that requires the
     * raw string value of the vehicle's make for internal processing or non-UI display.
     * </p>
     *
     * @return The string representing the vehicle's manufacturer brand name (e.g., "Ford").
     */
    public String getMake() {
        return make.get();
    }
    /**
     * <p>
     * This method sets the make or brand of the vehicle. It updates the underlying
     * {@link #makeProperty()} and includes important validation logic. This setter strictly
     * ensures that the `make` value provided is not {@code null} and is not an empty string
     * after any leading or trailing whitespace has been removed using `strip()`. If these
     * constraints are not met, appropriate exceptions are thrown, safeguarding data quality.
     * This method is typically invoked during object creation, when a user updates the make
     * in a vehicle form within the UI (via property binding), or by a DAO when populating
     * an object from a database record.
     * </p>
     *
     * @param make The manufacturer's brand name (e.g., "Chevrolet"). Must not be {@code null} or empty.
     * @throws NullPointerException if {@code make} is {@code null}.
     * @throws IllegalArgumentException if {@code make} is empty after stripping whitespace.
     */
    public void setMake(String make) {
        String trimmedMake = Objects.requireNonNull(make, "Make cannot be null.").strip();
        if (trimmedMake.isEmpty()) {
            throw new IllegalArgumentException("Make cannot be empty.");
        }
        this.make.set(trimmedMake);
    }
    /**
     * <p>
     * This method retrieves the current string value of the vehicle's specific model.
     * It directly accesses the value encapsulated by the {@link #modelProperty()}.
     * This getter is used by Data Access Objects (DAOs) for data retrieval operations,
     * or by any application logic that needs the raw string value of the vehicle's model
     * for internal processing or reporting outside of UI binding contexts.
     * </p>
     *
     * @return The string representing the manufacturer's model name (e.g., "Transit").
     */
    public String getModel() {
        return model.get();
    }
    /**
     * <p>
     * This method sets the specific model name for this vehicle. It updates the underlying
     * {@link #modelProperty()} and incorporates essential validation. This setter ensures
     * that the `model` provided is not {@code null} and is not an empty string after
     * trimming any leading or trailing whitespace. Failure to meet these criteria will
     * result in an appropriate exception. This method is invoked when a `Vehicle` object
     * is instantiated, when a user modifies the model in the UI (through property binding),
     * or by a DAO during the data loading process from persistent storage.
     * </p>
     *
     * @param model The manufacturer's model name. Must not be {@code null} or empty.
     * @throws NullPointerException if {@code model} is {@code null}.
     * @throws IllegalArgumentException if {@code model} is empty after stripping whitespace.
     */
    public void setModel(String model) {
        String trimmedModel = Objects.requireNonNull(model, "Model cannot be null.").strip();
        if (trimmedModel.isEmpty()) {
            throw new IllegalArgumentException("Model cannot be empty.");
        }
        this.model.set(trimmedModel);
    }
    /**
     * <p>
     * This method retrieves the current integer value of the vehicle's manufacturing year.
     * It directly accesses the value held by the {@link #yearProperty()}. This getter
     * is primarily used by Data Access Objects (DAOs) for data retrieval from the model,
     * or by any backend logic requiring the raw integer value of the year for calculations
     * (e.g., age of the vehicle) or non-UI display.
     * </p>
     *
     * @return The four-digit year of production (e.g., 2021).
     */
    public int getYear() {
        return year.get();
    }
    /**
     * <p>
     * This method sets the vehicle's year of manufacture. It updates the underlying
     * {@link #yearProperty()} and includes robust validation to ensure data integrity.
     * This setter verifies that the `year` falls within a plausible historical range
     * (currently defined as not before 1900) and is not in the future (not greater
     * than the current calendar year, determined dynamically). This strict validation
     * helps maintain data quality for age-related calculations, compliance checks,
     * and accurate reporting. This method is invoked during object construction, when
     * a user inputs or modifies the year in a vehicle detail form (with changes propagated
     * via property binding), or by a DAO when loading data from persistent storage.
     * </p>
     *
     * @param year The year the vehicle was built, used for age, compliance, or valuation.
     * Must be a valid year (e.g., not in the future and within a reasonable historical range).
     * @throws IllegalArgumentException if the {@code year} is less than 1900 or greater than the current year.
     */
    public void setYear(int year) {
        int currentYear = LocalDate.now().getYear();
        if (year < 1900 || year > currentYear) { // Arbitrary lower bound, adjust as needed
            throw new IllegalArgumentException("Year must be between 1900 and " + currentYear + ".");
        }
        this.year.set(year);
    }
    /**
     * <p>
     * This method retrieves the current integer value of the vehicle's maximum participant capacity.
     * It directly accesses the value held by the {@link #capacityProperty()}. This getter is used
     * by Data Access Objects (DAOs) for data retrieval, or by any backend logic that needs the raw
     * numerical value of the vehicle's seating capacity, for instance, when performing checks
     * for vanpool assignments.
     * </p>
     *
     * @return The integer number of active participants this vehicle can accommodate.
     */
    public int getCapacity() {
        return capacity.get();
    }
    /**
     * <p>
     * This method sets the maximum number of active participants the vehicle can hold.
     * It updates the underlying {@link #capacityProperty()} and includes essential validation.
     * This setter enforces that the `capacity` must be a positive integer, meaning a vehicle
     * must be able to hold at least one participant (excluding the driver, if applicable,
     * as per system definition). If the provided capacity is less than or equal to zero, an
     * {@link IllegalArgumentException} is thrown to prevent illogical entries and maintain
     * data integrity. This method is invoked during object initialization, when a user updates
     * the capacity in the UI (through property binding), or by a DAO when populating a `Vehicle`
     * object from the database.
     * </p>
     *
     * @param capacity The integer number of active participant seats available. Must be a positive value.
     * @throws IllegalArgumentException if {@code capacity} is less than or equal to zero.
     */
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be a positive number.");
        }
        this.capacity.set(capacity);
    }
    /**
     * <p>
     * This method retrieves the current {@link LocalDate} value representing the vehicle's
     * lease start date. It directly accesses the value held by the {@link #leaseStartDateProperty()}.
     * This getter is primarily used by Data Access Objects (DAOs) for data retrieval, or by
     * any backend logic that needs the raw `LocalDate` value of the lease commencement for
     * calculations or internal processing.
     * </p>
     *
     * @return The {@link LocalDate} representing the start of the lease.
     */
    public LocalDate getLeaseStartDate() {
        return leaseStartDate.get();
    }
    /**
     * <p>
     * This method sets the {@link LocalDate} for the vehicle's lease start date. It updates
     * the underlying {@link #leaseStartDateProperty()} and includes important validation logic.
     * This setter first ensures that the `leaseStartDate` provided is not {@code null}.
     * Subsequently, it performs a crucial logical consistency check: if a
     * {@link #leaseEndDateProperty()} is already set (i.e., not null), it validates that the
     * new `leaseStartDate` is not chronologically after the existing end date. This strict
     * order maintenance prevents illogical lease periods. If any of these conditions are violated,
     * an appropriate exception is thrown. This method is invoked during vehicle object creation,
     * when a user selects or modifies the lease start date in a UI form (via property binding),
     * or by a DAO when hydrating an object from a database record.
     * </p>
     *
     * @param leaseStartDate The {@link LocalDate} to set as the lease start date. Must not be {@code null}.
     * @throws NullPointerException if {@code leaseStartDate} is {@code null}.
     * @throws IllegalArgumentException if the {@code leaseEndDate} is already set and `leaseStartDate` is after it.
     */
    public void setLeaseStartDate(LocalDate leaseStartDate) {
        Objects.requireNonNull(leaseStartDate, "Lease start date cannot be null.");
        // If leaseEndDate is already set, validate consistency
        if (getLeaseEndDate() != null && leaseStartDate.isAfter(getLeaseEndDate())) {
            throw new IllegalArgumentException("Lease start date cannot be after lease end date.");
        }
        this.leaseStartDate.set(leaseStartDate);
    }
    /**
     * <p>
     * This method retrieves the current {@link LocalDate} value representing the vehicle's
     * scheduled lease end date. It directly accesses the value held by the
     * {@link #leaseEndDateProperty()}. This getter is used by Data Access Objects (DAOs)
     * for data retrieval, or by any backend logic that needs the raw `LocalDate` value of
     * the lease termination for calculations or internal processing. This property can be
     * {@code null} if the lease is indefinite or not yet determined, providing flexibility
     * for various lease arrangements.
     * </p>
     *
     * @return The {@link LocalDate} representing the scheduled lease termination date,
     * or {@code null} if the lease is indefinite or not yet set.
     */
    public LocalDate getLeaseEndDate() {
        return leaseEndDate.get();
    }
    /**
     * <p>
     * This method sets the {@link LocalDate} for the vehicle's lease end date. It updates the
     * underlying {@link #leaseEndDateProperty()} and enforces critical chronological validation.
     * If `leaseEndDate` is provided as a non-{@code null} value, the method first rigorously
     * checks if the {@link #leaseStartDateProperty()} is already set. An
     * {@link IllegalStateException} is thrown if a non-null `leaseEndDate` is provided before
     * a `leaseStartDate` has been established, ensuring a logical order for date entry. Following
     * this, it validates that the provided `leaseEndDate` is not chronologically before the
     * `leaseStartDate`. An {@link IllegalArgumentException} is thrown if this consistency rule
     * is violated, safeguarding the integrity of the lease period. This design allows for
     * indefinite leases (by setting `leaseEndDate` to {@code null}) while maintaining strict
     * date order when an end date is explicitly specified. This method is invoked during vehicle
     * object construction, when a user selects or modifies the lease end date in a UI form
     * (through property binding), or by a DAO when populating an object.
     * </p>
     *
     * @param leaseEndDate The {@link LocalDate} to set as the lease end date. Can be {@code null}.
     * @throws IllegalArgumentException if {@code leaseEndDate} is not {@code null} and is
     * chronologically before the {@link #getLeaseStartDate()}.
     * @throws IllegalStateException    if `leaseEndDate` is non-null but `leaseStartDate`
     * is {@code null} at the point this method is called.
     */
    public void setLeaseEndDate(LocalDate leaseEndDate) {
        if (leaseEndDate != null) {
            // Cannot validate end date without a start date. Force start date first or ensure order.
            if (getLeaseStartDate() == null) {
                throw new IllegalStateException("Lease start date must be set before setting a non-null lease end date.");
            }
            if (leaseEndDate.isBefore(getLeaseStartDate())) {
                throw new IllegalArgumentException("Lease end date cannot be before lease start date.");
            }
        }
        this.leaseEndDate.set(leaseEndDate);
    }
    /**
     * <p>
     * This method retrieves the current boolean value indicating whether the vehicle is active.
     * It directly accesses the value held by the {@link #isActiveProperty()}. This getter
     * is used by Data Access Objects (DAOs) for data retrieval, or by any backend logic that
     * needs to check the vehicle's operational status (e.g., when filtering available vehicles
     * for assignment to vanpools). It adheres to the standard JavaBeans naming convention for
     * boolean getters (`isActive()` instead of `getIsActive()`).
     * </p>
     *
     * @return {@code true} if the vehicle is active and available for use; {@code false} if it's
     * inactive (e.g., retired, undergoing maintenance, or otherwise unavailable).
     */
    public boolean isActive() {
        return isActive.get();
    }
    /**
     * <p>
     * This method sets the operational status of the vehicle. It updates the underlying
     * {@link #isActiveProperty()}. This setter is typically invoked during object construction,
     * when a user toggles the active status in the UI (e.g., via a checkbox, with changes
     * propagated through property binding), or by a DAO when loading data from persistent storage.
     * This flag is crucial for filtering vehicles to show only those currently in service,
     * aiding in efficient fleet management and operational planning by quickly identifying
     * and allocating available assets.
     * </p>
     *
     * @param active {@code true} to mark the vehicle as active; {@code false} to deactivate it.
     */
    public void setActive(boolean active) {
        this.isActive.set(active);
    }
    /**
     * <p>
     * This method retrieves the current {@link BigDecimal} value representing the vehicle's
     * monthly discount amount. It directly accesses the value held by the {@link #discountProperty()}.
     * This getter is used by Data Access Objects (DAOs) for data retrieval, or by financial
     * calculation logic within the application that needs the raw monetary value of the discount
     * for accurate computations related to vanpool charges or budgeting.
     * </p>
     *
     * @return The monthly discount amount as a {@link BigDecimal}.
     */
    public BigDecimal getDiscount() {
        return discount.get();
    }
    /**
     * <p>
     * This method sets the recurring monthly discount amount for the vehicle. It updates the
     * underlying {@link #discountProperty()} and incorporates essential financial validation.
     * This setter strictly ensures that the `discount` provided is not {@code null} and is
     * a non-negative value (i.e., greater than or equal to zero). This prevents illogical
     * negative discounts from being applied, which is critical for maintaining financial
     * integrity within the system. If the `discount` is less than zero, an
     * {@link IllegalArgumentException} is thrown. This method is invoked during object creation,
     * when a user inputs or modifies the discount amount in a UI form (through property binding),
     * or by a DAO when populating an object from the database. This value directly impacts
     * the financial model of the vanpool and its associated calculations.
     * </p>
     *
     * @param discount The monetary amount of the discount to set. Must not be {@code null} and must be non-negative.
     * @throws NullPointerException if {@code discount} is {@code null}.
     * @throws IllegalArgumentException if {@code discount} is negative.
     */
    public void setDiscount(BigDecimal discount) {
        Objects.requireNonNull(discount, "Discount amount cannot be null.");
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount amount cannot be negative.");
        }
        this.discount.set(discount);
    }
    /**
     * <p>
     * This method retrieves the current string value of any supplemental notes or comments
     * recorded for the vehicle. It directly accesses the value held by the
     * {@link #notesProperty()}. This getter is used by Data Access Objects (DAOs) for data
     * retrieval, or by any backend logic that needs the raw textual notes for internal
     * processing or display outside of UI binding contexts. It can return {@code null}
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
     * This method assigns additional notes or free-form commentary to this vehicle record.
     * It updates the underlying {@link #notesProperty()}. If the provided `notes` string
     * is not {@code null}, leading and trailing whitespace will be automatically stripped
     * using the `strip()` method before the value is set. This helps in maintaining cleaner
     * data and preventing extraneous whitespace from being stored. The field can also be
     * explicitly set to {@code null} if no notes are required for the vehicle. This method
     * is invoked during object creation, when a user adds or modifies notes in a UI form
     * (e.g., a `TextArea`, with changes propagated via property binding), or by a DAO
     * during data loading from persistent storage. This field provides valuable flexibility
     * for recording unstructured information critical for comprehensive operational context.
     * </p>
     *
     * @param notes The string containing additional comments or remarks about this vehicle. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes.set((notes != null) ? notes.strip() : null);
    }

    // --- Utilities ---

    /**
     * <p>
     * This method returns a concise string representation of the {@code Vehicle} object.
     * It is primarily intended for debugging, logging, and quick display in console output,
     * providing a human-readable summary of the vehicle's key identifying attributes.
     * The format includes the vehicle's ID, unique number, make, model, and year. These
     * values are obtained by calling their respective getter methods (e.g., `getVehicleID()`,
     * `getVehicleNumber()`), which in turn query the underlying JavaFX properties. This
     * ensures that the `toString()` output consistently reflects the current state of the
     * object's properties, making it useful for verifying object state at any given time.
     * </p>
     *
     * @return A string in the format:
     * "Vehicle{ID=..., Number='...', Make='...', Model='...', Year=...}"
     */
    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleID=" + getVehicleID() +
                ", vehicleNumber='" + getVehicleNumber() + '\'' +
                ", make='" + getMake() + '\'' +
                ", model='" + getModel() + '\'' +
                ", year=" + getYear() +
                '}';
    }
    /**
     * <p>
     * This method determines whether this {@code Vehicle} object is "equal to" another object.
     * The comparison for equality is primarily based on the unique {@code vehicleID}. This
     * approach ensures that two `Vehicle` objects are considered equal if they represent
     * the same entity in the underlying database, regardless of whether other attributes
     * (like `notes` or `isActive`) might differ in memory. This method adheres strictly
     * to the general contract of the {@link Object#equals(Object)} method, guaranteeing
     * properties such as reflexivity, symmetry, transitivity, consistency, and correct
     * handling of null comparisons. It correctly handles scenarios where the `vehicleID`
     * might be {@code null} for unpersisted entities, relying on
     * {@link Objects#equals(Object, Object)} for a null-safe comparison of the ID values.
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument (based on `vehicleID`);
     * {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Optimization: Same object reference indicates equality
        if (o == null || getClass() != o.getClass()) return false; // Null object or different class type
        Vehicle vehicle = (Vehicle) o; // Cast to Vehicle for property access
        // Equality is determined by the unique primary key, safely handling null IDs for new objects
        return Objects.equals(getVehicleID(), vehicle.getVehicleID());
    }
    /**
     * <p>
     * This method computes and returns a hash code value for this {@code Vehicle} object.
     * It is provided to support the efficient operation of hash tables, such as those
     * implemented by {@link java.util.HashMap} and {@link java.util.HashSet}. The hash
     * code generation is based solely on the unique {@code vehicleID}. This design choice
     * is crucial for consistency with the `equals()` method, ensuring that any two `Vehicle`
     * objects considered equal (i.e., having the same `vehicleID`) will invariably produce
     * the same hash code. If the `vehicleID` is {@code null} (typically for an unpersisted
     * entity), its hash code will gracefully default to 0, as per the behavior of
     * {@link Objects#hash(Object...)}, maintaining proper collection behavior.
     * </p>
     *
     * @return A hash code value for this object, derived from its {@code vehicleID}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getVehicleID()); // Hash code based on the primary key
    }
}