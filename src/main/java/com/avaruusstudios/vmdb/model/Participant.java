package com.avaruusstudios.vmdb.model;

import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * <p>
 * Represents an individual participant within the Vanpool Management System.
 * This class stores personal, contact, and participation details for a commuter,
 * including their assigned pickup and drop-off locations, associated program,
 * financial benefits, and role within the vanpool.
 * </p>
 *
 * <p>
 * Each participant is uniquely identified and their properties are designed to support
 * data binding with JavaFX UI components, making it suitable for a responsive
 * desktop application. This class directly maps to the `Participants` table in the SQLite database.
 * </p>
 *
 * @see Location
 * @see Program
 * @see Role
 */
public class Participant {
    /**
     * The unique numerical identifier for the participant. This serves as the primary key
     * in the database for participant records ({@code ParticipantID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created participant not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable.
     * </p>
     */
    private final ReadOnlyObjectProperty<Integer> participantID;
    /**
     * The {@link Location} object representing the participant's designated pickup point.
     * This field is **required** (corresponds to {@code PickUpLocationID_FK INTEGER NOT NULL} in the database).
     */
    private final ObjectProperty<Location> pickUpLocation;
    /**
     * The {@link Location} object representing the participant's designated drop-off point.
     * This field is **required** (corresponds to {@code DropOffLocationID_FK INTEGER NOT NULL} in the database).
     */
    private final ObjectProperty<Location> dropOffLocation;
    /**
     * The first name of the participant.
     * This field is **required** (corresponds to {@code FirstName TEXT NOT NULL} in the database).
     */
    private final StringProperty firstName;
    /**
     * The middle name of the participant.
     * This field is optional (corresponds to {@code MiddleName TEXT} in the database).
     */
    private final StringProperty middleName;
    /**
     * The last name or surname of the participant.
     * This field is **required** (corresponds to {@code LastName TEXT NOT NULL} in the database).
     */
    private final StringProperty lastName;
    /**
     * The primary email address for contacting the participant.
     * This field is **required** and validated for a basic email format
     * (corresponds to {@code Email TEXT NOT NULL UNIQUE} in the database).
     */
    private final StringProperty email;
    /**
     * The primary phone number for contacting the participant.
     * Stored internally as digits only. This field is **required**
     * (corresponds to {@code Phone TEXT NOT NULL} in the database).
     */
    private final StringProperty phone;
    /**
     * The calculated distance in miles between the participant's pickup and drop-off locations.
     * Stored as {@link BigDecimal} for precision to avoid floating-point inaccuracies.
     * This field is **required** and must be non-negative (corresponds to {@code DistanceMiles REAL NOT NULL} in the database).
     */
    private final ObjectProperty<BigDecimal> distanceMiles;
    /**
     * The {@link LocalDate} when the participant officially joined the vanpool system.
     * This field is **required** and cannot be a future date
     * (corresponds to {@code JoinDate TEXT NOT NULL} in the database).
     */
    private final ObjectProperty<LocalDate> joinDate;
    /**
     * A boolean flag indicating whether the participant is currently active in the vanpool system.
     * An inactive participant might still exist in the system but is not currently assigned to a route.
     * (corresponds to {@code IsActive INTEGER NOT NULL} in the database, 0 for false, 1 for true).
     */
    private final BooleanProperty isActive;
    /**
     * The {@link Program} enum representing the specific vanpool program the participant is associated with.
     * This field is **required** (corresponds to {@code Program TEXT NOT NULL} in the database, storing the enum's string value).
     */
    private final ObjectProperty<Program> program;
    /**
     * The static monthly benefit amount (allowance) that the participant is allotted.
     * Stored as {@link BigDecimal} for precision in financial calculations.
     * This field is **required** and must be non-negative (corresponds to {@code BenefitAmount REAL NOT NULL} in the database).
     */
    private final ObjectProperty<BigDecimal> benefitAmount;
    /**
     * The {@link Role} assigned to the participant within the vanpool context (e.g., Driver, Commuter).
     * This field is **required** (corresponds to {@code Role TEXT NOT NULL} in the database, storing the enum's string value).
     */
    private final ObjectProperty<Role> role;
    /**
     * Optional free-form text for additional notes or administrative comments pertaining to this participant.
     * (corresponds to {@code Notes TEXT} in the database).
     */
    private final StringProperty notes;
    /**
     * A regular expression pattern used for basic validation of email addresses.
     * This pattern checks for a typical email structure (e.g., `name@domain.com`).
     * Note: Comprehensive email validation is complex and often requires external libraries or services.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
    );

    /**
     * Default constructor for creating a new, unpersisted {@code Participant} object.
     * Initializes properties with default values (null for ID, empty strings for names, current date, etc.).
     * The {@code participantID} is set to {@code null} to explicitly indicate that
     * this participant has not yet been assigned a unique ID by the database.
     * {@code Location} objects are initialized to new empty instances to avoid {@code NullPointerException} later.
     * This constructor is primarily used by frameworks that instantiate objects
     * via reflection (e.g., ORMs, JSON deserializers) before populating their fields.
     */
    public Participant() {
        this(null, new Location(), new Location(), "", "", "", "", "", BigDecimal.ZERO, LocalDate.now(), true, Program.NONE, BigDecimal.ZERO, Role.PARTICIPANT, "");
    }
    /**
     * Full constructor to initialize all fields of a {@code Participant} instance.
     * This constructor is typically used when loading an *existing* participant
     * record from the database, where {@code participantID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param participantID   The unique integer ID for this participant, typically assigned by the database. Can be {@code null} for new participants.
     * @param pickUpLocation  The participant's designated pickup {@link Location}. Must not be null.
     * @param dropOffLocation The participant's designated drop-off {@link Location}. Must not be null.
     * @param firstName       The participant's first name. Must not be null or empty.
     * @param middleName      The participant's middle name. Can be null.
     * @param lastName        The participant's last name. Must not be null or empty.
     * @param email           The participant's primary email address. Must be valid and not null/empty.
     * @param phone           The participant's primary phone number (digits only). Must be valid and not null/empty.
     * @param distanceMiles   The calculated distance in miles. Must not be null and non-negative.
     * @param joinDate        The date the participant joined the system. Must not be null and not in the future.
     * @param isActive        A boolean flag indicating if the participant is currently active.
     * @param program         The {@link Program} the participant is associated with. Must not be null.
     * @param benefitAmount   The monthly static benefit amount. Must not be null and non-negative.
     * @param role            The {@link Role} of the participant within the vanpool. Must not be null.
     * @param notes           Optional free-form notes. Can be null.
     *
     * @throws IllegalArgumentException if any mandatory argument is invalid (e.g., null, empty, bad format, out of range).
     * @throws IllegalStateException    if `participantID` is attempted to be changed once set.
     */
    public Participant(Integer participantID, Location pickUpLocation, Location dropOffLocation,
                       String firstName, String middleName, String lastName, String email, String phone,
                       BigDecimal distanceMiles, LocalDate joinDate, boolean isActive,
                       Program program, BigDecimal benefitAmount, Role role, String notes) {
        this.participantID = new SimpleObjectProperty<>(this, "participantID", participantID);
        this.pickUpLocation = new SimpleObjectProperty<>(this, "pickUpLocation");
        this.dropOffLocation = new SimpleObjectProperty<>(this, "dropOffLocation");
        this.firstName = new SimpleStringProperty(this, "firstName");
        this.middleName = new SimpleStringProperty(this, "middleName");
        this.lastName = new SimpleStringProperty(this, "lastName");
        this.email = new SimpleStringProperty(this, "email");
        this.phone = new SimpleStringProperty(this, "phone");
        this.distanceMiles = new SimpleObjectProperty<>(this, "distanceMiles");
        this.joinDate = new SimpleObjectProperty<>(this, "joinDate");
        this.isActive = new SimpleBooleanProperty(this, "isActive");
        this.program = new SimpleObjectProperty<>(this, "program");
        this.benefitAmount = new SimpleObjectProperty<>(this, "benefitAmount");
        this.role = new SimpleObjectProperty<>(this, "role");
        this.notes = new SimpleStringProperty(this, "notes");

        setPickUpLocation(pickUpLocation);
        setDropOffLocation(dropOffLocation);
        setFirstName(firstName);
        setMiddleName(middleName);
        setLastName(lastName);
        setEmail(email);
        setPhone(phone);
        setDistanceMiles(distanceMiles);
        setJoinDate(joinDate);
        setIsActive(isActive);
        setProgram(program);
        setBenefitAmount(benefitAmount);
        setRole(role);
        setNotes(notes);
    }
    /**
     * Convenience constructor for creating a new {@code Participant} object that does not yet have a database ID.
     * This constructor is ideal when preparing a new participant record for **insertion** into the database,
     * as the {@code participantID} is typically auto-generated by the database.
     * All parameters are validated via their respective setters.
     *
     * @param pickUpLocation  The participant's designated pickup {@link Location}. Must not be null.
     * @param dropOffLocation The participant's designated drop-off {@link Location}. Must not be null.
     * @param firstName       The participant's first name. Must not be null or empty.
     * @param middleName      The participant's middle name. Can be null.
     * @param lastName        The participant's last name. Must not be null or empty.
     * @param email           The participant's primary email address. Must be valid and not null/empty.
     * @param phone           The participant's primary phone number (digits only). Must be valid and not null/empty.
     * @param distanceMiles   The calculated distance in miles. Must not be null and non-negative.
     * @param joinDate        The date the participant joined the system. Must not be null and not in the future.
     * @param isActive        A boolean flag indicating if the participant is currently active.
     * @param program         The {@link Program} the participant is associated with. Must not be null.
     * @param benefitAmount   The monthly static benefit amount. Must not be null and non-negative.
     * @param role            The {@link Role} of the participant within the vanpool. Must not be null.
     * @param notes           Optional free-form notes. Can be null.
     *
     * @throws IllegalArgumentException if any mandatory argument is invalid (e.g., null, empty, bad format, out of range).
     */
    public Participant(Location pickUpLocation, Location dropOffLocation,
                       String firstName, String middleName, String lastName, String email, String phone,
                       BigDecimal distanceMiles, LocalDate joinDate, boolean isActive,
                       Program program, BigDecimal benefitAmount, Role role, String notes) {
        // Calls the full constructor with participantID as null
        this(null, pickUpLocation, dropOffLocation,
                firstName, middleName, lastName, email, phone,
                distanceMiles, joinDate, isActive,
                program, benefitAmount, role, notes);
    }

    // --- JavaFX Property Accessors ---

    /**
     * Retrieves the read-only property for the participant's unique ID.
     * This property represents the {@code ParticipantID} column in the database.
     * <p>
     * As this property is {@code ReadOnlyObjectProperty}, its value cannot be
     * changed directly after initial assignment, enforcing the immutability
     * of the primary key for persisted entities.
     * </p>
     *
     * @return The {@link ReadOnlyObjectProperty} for {@code participantID}.
     */
    public ReadOnlyObjectProperty<Integer> participantIDProperty() {
        return participantID;
    }
    /**
     * Retrieves the {@link ObjectProperty} for the participant's pickup location.
     * This property holds a {@link Location} object and corresponds to the {@code PickUpLocationID_FK}
     * column in the database.
     *
     * @return The {@link ObjectProperty} for {@code pickUpLocation}.
     */
    public ObjectProperty<Location> pickUpLocationProperty() {
        return pickUpLocation;
    }
    /**
     * Retrieves the {@link ObjectProperty} for the participant's drop-off location.
     * This property holds a {@link Location} object and corresponds to the {@code DropOffLocationID_FK}
     * column in the database.
     *
     * @return The {@link ObjectProperty} for {@code dropOffLocation}.
     */
    public ObjectProperty<Location> dropOffLocationProperty() {
        return dropOffLocation;
    }
    /**
     * Retrieves the {@link StringProperty} for the participant's first name.
     * This property corresponds to the {@code FirstName} column in the database.
     *
     * @return The {@link StringProperty} for {@code firstName}.
     */
    public StringProperty firstNameProperty() {
        return firstName;
    }
    /**
     * Retrieves the {@link StringProperty} for the participant's middle name.
     * This property corresponds to the {@code MiddleName} column in the database.
     *
     * @return The {@link StringProperty} for {@code middleName}.
     */
    public StringProperty middleNameProperty() {
        return middleName;
    }
    /**
     * Retrieves the {@link StringProperty} for the participant's last name.
     * This property corresponds to the {@code LastName} column in the database.
     *
     * @return The {@link StringProperty} for {@code lastName}.
     */
    public StringProperty lastNameProperty() {
        return lastName;
    }
    /**
     * Retrieves the {@link StringProperty} for the participant's email address.
     * This property corresponds to the {@code Email} column in the database.
     *
     * @return The {@link StringProperty} for {@code email}.
     */
    public StringProperty emailProperty() {
        return email;
    }
    /**
     * Retrieves the {@link StringProperty} for the participant's phone number.
     * This property corresponds to the {@code Phone} column in the database.
     *
     * @return The {@link StringProperty} for {@code phone}.
     */
    public StringProperty phoneProperty() {
        return phone;
    }
    /**
     * Retrieves the {@link ObjectProperty} for the distance between pickup and drop-off locations.
     * This property holds a {@link BigDecimal} value and corresponds to the {@code DistanceMiles} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code distanceMiles}.
     */
    public ObjectProperty<BigDecimal> distanceMilesProperty() {
        return distanceMiles;
    }
    /**
     * Retrieves the {@link ObjectProperty} for the participant's join date.
     * This property holds a {@link LocalDate} value and corresponds to the {@code JoinDate} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code joinDate}.
     */
    public ObjectProperty<LocalDate> joinDateProperty() {
        return joinDate;
    }
    /**
     * Retrieves the {@link BooleanProperty} indicating if the participant is currently active.
     * This property corresponds to the {@code IsActive} column in the database.
     *
     * @return The {@link BooleanProperty} for {@code isActive}.
     */
    public BooleanProperty isActiveProperty() {
        return isActive;
    }
    /**
     * Retrieves the {@link ObjectProperty} for the participant's associated vanpool program.
     * This property holds a {@link Program} enum value and corresponds to the {@code Program} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code program}.
     */
    public ObjectProperty<Program> programProperty() {
        return program;
    }
    /**
     * Retrieves the {@link ObjectProperty} for the participant's monthly benefit amount.
     * This property holds a {@link BigDecimal} value and corresponds to the {@code BenefitAmount} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code benefitAmount}.
     */
    public ObjectProperty<BigDecimal> benefitAmountProperty() {
        return benefitAmount;
    }
    /**
     * Retrieves the {@link ObjectProperty} for the participant's role within the vanpool.
     * This property holds a {@link Role} enum value and corresponds to the {@code Role} column in the database.
     *
     * @return The {@link ObjectProperty} for {@code role}.
     */
    public ObjectProperty<Role> roleProperty() {
        return role;
    }
    /**
     * Retrieves the {@link StringProperty} for any additional notes pertaining to the participant.
     * This property corresponds to the {@code Notes} column in the database.
     *
     * @return The {@link StringProperty} for {@code notes}.
     */
    public StringProperty notesProperty() {
        return notes;
    }

    // --- Value Getters and Setters ---

    /**
     * Retrieves the unique ID of the participant.
     * <p>
     * For new, unpersisted participants, this will be {@code null}.
     * Once assigned by the database, this ID should not be changed.
     * </p>
     *
     * @return The {@link Integer} primary key of the participant, or {@code null} if not yet persisted.
     */
    public Integer getParticipantID() {
        return participantID.get();
    }
    /**
     * Sets the unique ID for this participant. This method is designed to be package-private
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
    void _setParticipantID(Integer id) { // Package-private for DAO use only
        if (this.participantID.get() != null) {
            throw new IllegalStateException("Participant ID cannot be changed once set.");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Participant ID cannot be null or non-positive.");
        }
        ((SimpleObjectProperty<Integer>)this.participantID).set(id);
    }
    /**
     * Retrieves the participant's pickup {@link Location}.
     *
     * @return The {@link Location} object representing the pickup point.
     */
    public Location getPickUpLocation() {
        return pickUpLocation.get();
    }
    /**
     * Sets the participant's pickup {@link Location}.
     * <p>
     * This field is mandatory and must not be {@code null}. It links the participant
     * to a specific geographical pickup point defined by a {@link Location} object.
     * </p>
     *
     * @param pickUpLocation The {@link Location} object to set. Must not be {@code null}.
     * @throws IllegalArgumentException if the provided {@code pickUpLocation} is {@code null}.
     */
    public void setPickUpLocation(Location pickUpLocation) {
        if (pickUpLocation == null) {
            throw new IllegalArgumentException("Participant pickup location cannot be null.");
        }
        this.pickUpLocation.set(pickUpLocation);
    }
    /**
     * Retrieves the participant's drop-off {@link Location}.
     *
     * @return The {@link Location} object representing the drop-off point.
     */
    public Location getDropOffLocation() {
        return dropOffLocation.get();
    }
    /**
     * Sets the participant's drop-off {@link Location}.
     * <p>
     * This field is mandatory and must not be {@code null}. It links the participant
     * to a specific geographical drop-off point defined by a {@link Location} object.
     * </p>
     *
     * @param dropOffLocation The {@link Location} object to set. Must not be {@code null}.
     * @throws IllegalArgumentException if the provided {@code dropOffLocation} is {@code null}.
     */
    public void setDropOffLocation(Location dropOffLocation) {
        if (dropOffLocation == null) {
            throw new IllegalArgumentException("Participant drop-off location cannot be null.");
        }
        this.dropOffLocation.set(dropOffLocation);
    }
    /**
     * Retrieves the participant's first name.
     *
     * @return The first name as a {@link String}.
     */
    public String getFirstName() {
        return firstName.get();
    }
    /**
     * Sets the participant's first name.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is mandatory and cannot be set to {@code null} or an empty string.
     * </p>
     *
     * @param firstName The first name to set.
     * @throws IllegalArgumentException if the provided first name is {@code null} or empty after trimming.
     */
    public void setFirstName(String firstName) {
        String trimmedFirstName = (firstName == null) ? null : firstName.trim();
        if (trimmedFirstName == null || trimmedFirstName.isEmpty()) {
            throw new IllegalArgumentException("Participant first name cannot be null or empty.");
        }
        this.firstName.set(trimmedFirstName);
    }
    /**
     * Retrieves the participant's middle name.
     *
     * @return The middle name as a {@link String}, or {@code null} if not set.
     */
    public String getMiddleName() {
        return middleName.get();
    }
    /**
     * Sets the participant's middle name.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is optional and can be set to {@code null} or an empty string (which will be converted to {@code null} after trimming).
     * </p>
     *
     * @param middleName The middle name to set. Can be {@code null}.
     */
    public void setMiddleName(String middleName) {
        this.middleName.set((middleName == null || middleName.trim().isEmpty()) ? null : middleName.trim());
    }
    /**
     * Retrieves the participant's last name.
     *
     * @return The last name as a {@link String}.
     */
    public String getLastName() {
        return lastName.get();
    }
    /**
     * Sets the participant's last name.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is mandatory and cannot be set to {@code null} or an empty string.
     * </p>
     *
     * @param lastName The last name to set.
     * @throws IllegalArgumentException if the provided last name is {@code null} or empty after trimming.
     */
    public void setLastName(String lastName) {
        String trimmedLastName = (lastName == null) ? null : lastName.trim();
        if (trimmedLastName == null || trimmedLastName.isEmpty()) {
            throw new IllegalArgumentException("Participant last name cannot be null or empty.");
        }
        this.lastName.set(trimmedLastName);
    }
    /**
     * Retrieves the participant's email address.
     *
     * @return The email address as a {@link String}.
     */
    public String getEmail() {
        return email.get();
    }
    /**
     * Sets the participant's email address.
     * <p>
     * The input string will be trimmed of leading/trailing whitespace.
     * This field is mandatory and cannot be set to {@code null} or an empty string.
     * It is also validated against a basic email format pattern.
     * </p>
     *
     * @param email The email address to set.
     * @throws IllegalArgumentException if the email is {@code null}, empty after trimming, or does not match the basic email format.
     */
    public void setEmail(String email) {
        String trimmedEmail = (email == null) ? null : email.trim();
        if (trimmedEmail == null || trimmedEmail.isEmpty()) {
            throw new IllegalArgumentException("Participant email cannot be null or empty.");
        }
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new IllegalArgumentException("Invalid email format for participant.");
        }
        this.email.set(trimmedEmail);
    }
    /**
     * Retrieves the participant's phone number (digits only).
     *
     * @return The phone number as a {@link String}.
     */
    public String getPhone() {
        return phone.get();
    }
    /**
     * Sets the participant's phone number.
     * <p>
     * All non-digit characters will be removed from the input string.
     * The resulting cleaned phone number must be between 7 and 15 digits long
     * to be considered valid. This field is mandatory.
     * </p>
     *
     * @param phone The phone number to set.
     * @throws IllegalArgumentException if the cleaned phone number is {@code null}, empty, or falls outside the valid length range.
     */
    public void setPhone(String phone) {
        String cleanedPhone = (phone == null) ? null : phone.replaceAll("[^0-9]", "");
        if (cleanedPhone == null || cleanedPhone.isEmpty()) {
            throw new IllegalArgumentException("Participant phone number cannot be null or empty after cleaning.");
        }
        if (cleanedPhone.length() < 7 || cleanedPhone.length() > 15) {
            throw new IllegalArgumentException("Participant phone number must be between 7 and 15 digits long after cleaning.");
        }
        this.phone.set(cleanedPhone);
    }
    /**
     * Retrieves the calculated distance in miles between pickup and drop-off locations.
     *
     * @return The distance in miles as a {@link BigDecimal}.
     */
    public BigDecimal getDistanceMiles() {
        return distanceMiles.get();
    }
    /**
     * Sets the calculated distance in miles.
     * <p>
     * This field is mandatory and must not be {@code null}. The distance must also be
     * a non-negative value, as negative distances are physically impossible.
     * Stored as {@link BigDecimal} for precision.
     * </p>
     *
     * @param distanceMiles The distance to set. Must not be {@code null} and must be non-negative.
     * @throws IllegalArgumentException if {@code distanceMiles} is {@code null} or negative.
     */
    public void setDistanceMiles(BigDecimal distanceMiles) {
        if (distanceMiles == null) {
            throw new IllegalArgumentException("Participant distance (miles) cannot be null.");
        }
        if (distanceMiles.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Participant distance (miles) cannot be negative.");
        }
        this.distanceMiles.set(distanceMiles);
    }
    /**
     * Retrieves the participant's join date.
     *
     * @return The join date as a {@link LocalDate}.
     */
    public LocalDate getJoinDate() {
        return joinDate.get();
    }
    /**
     * Sets the participant's join date.
     * <p>
     * This field is mandatory and must not be {@code null}. The join date also
     * cannot be a date in the future, ensuring that only valid historical or
     * current join dates are recorded.
     * </p>
     *
     * @param joinDate The join date to set. Must not be {@code null} and must not be a future date.
     * @throws IllegalArgumentException if {@code joinDate} is {@code null} or in the future.
     */
    public void setJoinDate(LocalDate joinDate) {
        if (joinDate == null) {
            throw new IllegalArgumentException("Participant join date cannot be null.");
        }
        if (joinDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Participant join date cannot be in the future.");
        }
        this.joinDate.set(joinDate);
    }
    /**
     * Retrieves the participant's active status.
     *
     * @return {@code true} if the participant is currently active, {@code false} otherwise.
     */
    public boolean isActive() {
        return isActive.get();
    }
    /**
     * Sets the participant's active status.
     * <p>
     * This boolean flag indicates whether the participant is currently active
     * in the vanpool system (e.g., actively commuting).
     * </p>
     *
     * @param active {@code true} to set the participant as active, {@code false} to set as inactive.
     */
    public void setIsActive(boolean active) {
        this.isActive.set(active);
    }
    /**
     * Retrieves the {@link Program} the participant is associated with.
     *
     * @return The {@link Program} enum value.
     */
    public Program getProgram() {
        return program.get();
    }
    /**
     * Sets the participant's associated program.
     * <p>
     * This field is mandatory and must not be {@code null}. It categorizes the participant
     * into a specific vanpool program.
     * </p>
     *
     * @param program The {@link Program} to set. Must not be {@code null}.
     * @throws IllegalArgumentException if the provided {@code program} is {@code null}.
     */
    public void setProgram(Program program) {
        if (program == null) {
            throw new IllegalArgumentException("Participant program cannot be null.");
        }
        this.program.set(program);
    }
    /**
     * Retrieves the monthly benefit amount allotted to the participant.
     *
     * @return The monthly benefit amount as a {@link BigDecimal}.
     */
    public BigDecimal getBenefitAmount() {
        return benefitAmount.get();
    }
    /**
     * Sets the monthly benefit amount.
     * <p>
     * This field is mandatory and must not be {@code null}. The benefit amount must also be
     * a non-negative value, as negative benefits are not logical in this context.
     * Stored as {@link BigDecimal} for precise financial representation.
     * </p>
     *
     * @param benefitAmount The benefit amount to set. Must not be {@code null} and must be non-negative.
     * @throws IllegalArgumentException if {@code benefitAmount} is {@code null} or negative.
     */
    public void setBenefitAmount(BigDecimal benefitAmount) {
        if (benefitAmount == null) {
            throw new IllegalArgumentException("Participant benefit amount cannot be null.");
        }
        if (benefitAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Participant benefit amount cannot be negative.");
        }
        this.benefitAmount.set(benefitAmount);
    }
    /**
     * Retrieves the {@link Role} assigned to the participant.
     *
     * @return The {@link Role} enum value.
     */
    public Role getRole() {
        return role.get();
    }
    /**
     * Sets the participant's role.
     * <p>
     * This field is mandatory and must not be {@code null}. It defines the participant's
     * functional role within the vanpool system (e.g., "DRIVER", "COMMUTER").
     * </p>
     *
     * @param role The {@link Role} to set. Must not be {@code null}.
     * @throws IllegalArgumentException if the provided {@code role} is {@code null}.
     */
    public void setRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Participant role cannot be null.");
        }
        this.role.set(role);
    }
    /**
     * Retrieves any additional notes or administrative comments for the participant.
     *
     * @return The notes string, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes.get();
    }
    /**
     * Sets additional notes or administrative comments for the participant.
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
     * Returns a string representation of the {@code Participant} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the participant's key attributes.
     * </p>
     * <p>
     * The format includes the participant ID, full name, email, active status,
     * associated program, and role.
     * </p>
     *
     * @return A string in the format:
     * "Participant{ID=..., FirstName='...', LastName='...', Email='...', IsActive=..., Program=..., Role=...}"
     */
    @Override
    public String toString() {
        return "Participant{" +
                "participantID=" + getParticipantID() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", isActive=" + isActive() +
                ", program=" + getProgram() +
                ", role=" + getRole() +
                '}';
    }
    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based primarily on the unique {@code participantID}.
     * </p>
     * <p>
     * This method adheres to the general contract of the {@link Object#equals(Object)} method,
     * ensuring consistency with hash-based collections. It correctly handles cases where
     * {@code participantID} might be {@code null} for unpersisted entities, in which case
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
        Participant that = (Participant) o;
        // Equality is based on the primary key (participantID), safely handling null Integer
        if (getParticipantID() == null || that.getParticipantID() == null) {
            return super.equals(o); // If IDs are null, fall back to object identity
        }
        return Objects.equals(getParticipantID(), that.getParticipantID());
    }
    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap} and {@link java.util.HashSet}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code participantID}. If {@code participantID}
     * is {@code null} (for unpersisted entities), it falls back to the default hash code
     * provided by {@code super.hashCode()}, ensuring consistency with {@code equals()}
     * for unpersisted objects as well.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return (getParticipantID() == null) ? super.hashCode() : Objects.hash(getParticipantID());
    }
}