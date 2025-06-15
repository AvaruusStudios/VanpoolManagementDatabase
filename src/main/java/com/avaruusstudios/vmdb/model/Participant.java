package com.avaruusstudios.vmdb.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

// Import the consolidated Role enum

/**
 * <p>
 * Represents an individual participant within the Vanpool Management System.
 * This class encapsulates all personal, contact, and participation details
 * for a commuter assigned to specific pickup and drop-off locations.
 * </p>
 *
 * <p>
 * This class directly maps to the `Participants` table in the database,
 * which is understood to *not* have a direct foreign key to the `Users` table
 * nor a direct foreign key to the `Vehicles` table.
 * If a participant is also a system user, the linkage is implicitly made
 * through common fields like the email address, managed at the application layer.
 * Participants are assigned to a Vanpool, and Vanpools have assigned Vehicles,
 * making the link between Participant and Vehicle indirect.
 * </p>
 *
 * <p>
 * Each participant has a specific {@link Role} within the vanpool context
 * (e.g., Coordinator, Treasurer, Participant).
 * </p>
 *
 * @see Location
 * @see Program
 * @see Role
 */
public class Participant {
    /**
     * Unique identifier for the participant. This serves as the primary key
     * in the database for participant records ({@code ParticipantID INTEGER PRIMARY KEY AUTOINCREMENT}).
     * <p>
     * For a newly created participant not yet persisted to the database, this value will be {@code null}.
     * Once assigned by the database, it becomes immutable.
     * </p>
     */
    private final Integer participantID;
    /**
     * The {@link Location} object representing the designated pickup point for the participant
     * in the morning. This establishes a foreign key relationship to the Locations table.
     * It is a **required** field for active participants.
     */
    private Location pickUpLocation;
    /**
     * The {@link Location} object representing the designated drop-off point for the participant
     * for work in the morning. This establishes a foreign key relationship to the Locations table.
     * It is a **required** field for active participants.
     */
    private Location dropOffLocation;
    /**
     * The first name of the participant.
     * This field is **required** for identification and communication.
     * It should not be null or empty.
     */
    private String firstName;
    /**
     * The middle name of the participant. This field is **optional** and may be null or empty.
     */
    private String middleName;
    /**
     * The last name or surname of the participant.
     * This field is **required** for identification and communication.
     * It should not be null or empty.
     */
    private String lastName;
    /**
     * The primary email address for contacting the participant.
     * Used for notifications, schedules, and general communication.
     * This field is **required** and should be unique. Validation ensures it's a valid email format.
     * This may also serve as an implicit link to the `Users` table if a participant is also a system user.
     */
    private String email;
    /**
     * The primary phone number for contacting the participant.
     * <p>
     * This field stores only **numerical digits**. All special characters (spaces, hyphens,
     * parentheses) are removed upon setting. The UI is responsible for displaying this
     * number in a formatted way (e.g., `+# (###) ###-####`).
     * </p>
     * This field is **required**.
     */
    private String phone;
    /**
     * The calculated distance in miles from the {@link #pickUpLocation} to the {@link #dropOffLocation}.
     * This value is used for route planning or potential benefit calculations.
     * It is represented as a {@link BigDecimal} for **precision**, and must be non-negative.
     */
    private BigDecimal distanceMiles;
    /**
     * The {@link LocalDate} when the participant officially joined or was added to the vanpool system.
     * This field is **required** and represents the start date of their participation.
     * Validation ensures this date is not in the future.
     */
    private LocalDate joinDate;
    /**
     * A boolean flag indicating whether the participant is currently active in the system.
     * {@code true} if active (currently participating in vanpool activities, incurring charges);
     * {@code false} if inactive (e.g., suspended, terminated, or on leave, not incurring regular charges).
     */
    private boolean isActive;
    /**
     * The {@link Program} enum representing the specific program the participant is associated with
     * (e.g., "Transportation Incentive Program (TIP)", "Daily"). This field is **required**.
     */
    private Program program;
    /**
     * The **static monthly benefit amount** (allowance) that the participant is allotted.
     * This value is typically tied to their associated {@link #program} and represents the
     * *maximum* benefit they can utilize in a given billing period.
     * <p>
     * **Important:** As clarified in business logic discussions, this field stores the
     * **fixed monthly allowance**. It is **not** a running balance that is decremented
     * by benefit usage. The actual consumption of benefit is tracked by summing
     * {@code InvoiceItem.Amount.benefitDue} entries for the participant within the current period,
     * which is managed externally to this field. This value is managed outside this application
     * (e.g., via a manual update or external system integration). It is a non-negative {@link BigDecimal}.
     * </p>
     */
    private BigDecimal benefitAmount;
    /**
     * The {@link Role} assigned to the participant, indicating their specific
     * responsibilities or administrative privileges within the vanpool (e.g., Coordinator, Treasurer, Driver).
     * This is the vanpool-specific role, distinct from a user's general system role.
     * This field is **required** and directly influences logic like remainder distribution.
     */
    private Role role;
    /**
     * Optional notes about the participant. This field can contain administrative comments,
     * special preferences, medical considerations, or any other relevant annotations.
     * May be null or empty.
     */
    private String notes;

    /**
     * Default constructor for creating a new, unpersisted {@code Participant} object.
     * The {@code participantID} is set to {@code null} to explicitly indicate that
     * this participant has not yet been assigned a unique ID by the database.
     * This constructor is useful for frameworks that instantiate objects via reflection.
     */
    public Participant() {
        this.participantID = null; // Explicitly null for unpersisted entity
        this.role = Role.PARTICIPANT; // Default vanpool role for a new participant (rider)
    }
    /**
     * Full constructor to initialize all fields of a {@code Participant} instance.
     * This constructor is typically used when loading an *existing* participant
     * record from the database, where {@code participantID} has already been assigned.
     * All parameters are validated via their respective setters.
     *
     * @param participantID     The unique system-generated identifier for the participant. Must not be {@code null} for existing.
     * @param firstName         The first name of the participant. Must not be {@code null} or empty.
     * @param middleName        The middle name of the participant (can be null or empty).
     * @param lastName          The last name of the participant. Must not be {@code null} or empty.
     * @param email             The contact email address of the participant. Must not be {@code null} or empty, and must be a valid format.
     * @param phone             The contact phone number of the participant. Must not be {@code null} or empty.
     * @param pickUpLocation    The {@link Location} object representing the participant's pickup point. Must not be {@code null}.
     * @param dropOffLocation   The {@link Location} object representing the participant's drop-off point. Must not be {@code null}.
     * @param distanceMiles     The distance in miles between the pickup and drop-off locations. Must be non-negative.
     * @param joinDate          The {@link LocalDate} when the participant joined the program. Must not be {@code null} and must not be in the future.
     * @param isActive          A boolean indicating the participant's active status.
     * @param program           The {@link Program} enum indicating the program the participant is in. Must not be {@code null}.
     * @param benefitAmount     The monthly benefit amount the participant receives (static allowance). Must not be {@code null} and non-negative.
     * @param role              The {@link Role} of the participant within the vanpool context. Must not be {@code null}.
     * @param notes             Any optional notes or administrative comments about the participant. Can be {@code null}.
     * @throws NullPointerException     if `participantID` or any other required object-type parameters (e.g., locations, names, email, date, program, benefitAmount, role) are {@code null}.
     * @throws IllegalArgumentException if any required string parameters are empty, or if `distanceMiles` or `benefitAmount` are negative, or `joinDate` is in the future.
     */
    public Participant(Integer participantID,
                       String firstName, String middleName, String lastName,
                       String email, String phone,
                       Location pickUpLocation, Location dropOffLocation, BigDecimal distanceMiles,
                       LocalDate joinDate, boolean isActive, Program program, BigDecimal benefitAmount, Role role,
                       String notes) {
        this.participantID = Objects.requireNonNull(participantID, "Participant ID cannot be null for an existing participant.");

        // Personal / Contact Info
        this.setFirstName(firstName);
        this.setMiddleName(middleName); // middleName can be null
        this.setLastName(lastName);
        this.setEmail(email);
        this.setPhone(phone); // Will now validate and strip non-digits

        // Location Info
        this.setPickUpLocation(pickUpLocation);
        this.setDropOffLocation(dropOffLocation);
        this.setDistanceMiles(distanceMiles);

        // Participation / Program Info
        this.setJoinDate(joinDate);
        this.isActive = isActive; // boolean, no setter validation needed
        this.setProgram(program);
        this.setBenefitAmount(benefitAmount);
        this.setRole(role);

        // Notes
        this.setNotes(notes); // Notes can be null/empty, use setter for potential trimming consistency
    }
    /**
     * Convenience constructor for creating a new {@code Participant} object that doesn't yet have a database ID.
     * This constructor is ideal when preparing a new participant record for **insertion** into the database.
     * The {@code participantID} is omitted as it is typically auto-generated by the database.
     * The default {@link Role} is set to {@code Role.PARTICIPANT}.
     * All parameters are validated via their respective setters.
     *
     * @param firstName         The first name of the participant. Must not be {@code null} or empty.
     * @param middleName        The middle name of the participant (can be null or empty).
     * @param lastName          The last name of the participant. Must not be {@code null} or empty.
     * @param email             The contact email address of the participant. Must not be {@code null} or empty, and must be a valid format.
     * @param phone             The contact phone number of the participant. Must not be {@code null} or empty.
     * @param pickUpLocation    The {@link Location} object representing the participant's pickup point. Must not be {@code null}.
     * @param dropOffLocation   The {@link Location} object representing the participant's drop-off point. Must not be {@code null}.
     * @param distanceMiles     The distance in miles between the pickup and drop-off locations. Must be non-negative.
     * @param joinDate          The {@link LocalDate} when the participant joined the program. Must not be {@code null} and must not be in the future.
     * @param isActive          A boolean indicating the participant's active status.
     * @param program           The {@link Program} enum indicating the program the participant is in. Must not be {@code null}.
     * @param benefitAmount     The monthly benefit amount the participant receives (static allowance). Must not be {@code null} and non-negative.
     * @param notes             Any optional notes or administrative comments about the participant. Can be {@code null}.
     * @throws NullPointerException     if any required object-type parameters (e.g., locations, names, email, date, program, benefitAmount) are {@code null}.
     * @throws IllegalArgumentException if any required string parameters are empty, or if `distanceMiles` or `benefitAmount` are negative, or `joinDate` is in the future.
     */
    public Participant(String firstName, String middleName, String lastName,
                       String email, String phone,
                       Location pickUpLocation, Location dropOffLocation, BigDecimal distanceMiles,
                       LocalDate joinDate, boolean isActive, Program program, BigDecimal benefitAmount,
                       String notes) {
        this.participantID = null; // New entity, ID will be assigned by DB
        this.role = Role.PARTICIPANT; // Default role for new participants

        // Personal / Contact Info
        this.setFirstName(firstName);
        this.setMiddleName(middleName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setPhone(phone);

        // Location Info
        this.setPickUpLocation(pickUpLocation);
        this.setDropOffLocation(dropOffLocation);
        this.setDistanceMiles(distanceMiles);

        // Participation / Program Info
        this.setJoinDate(joinDate);
        this.isActive = isActive;
        this.setProgram(program);
        this.setBenefitAmount(benefitAmount);

        // Notes
        this.setNotes(notes);
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Retrieves the unique identifier for this participant.
     * For new, unpersisted participants, this will be {@code null}.
     *
     * @return The {@link Integer} primary key used to identify this participant in the system, or {@code null} if not yet assigned.
     */
    public Integer getParticipantID() {
        return participantID;
    }
    /**
     * Retrieves the {@link Location} object representing the participant's pickup point.
     *
     * @return The {@link Location} object, or {@code null} if not set (though typically required for active participants).
     */
    public Location getPickUpLocation() {
        return pickUpLocation;
    }
    /**
     * Sets the {@link Location} object for the participant's pickup point.
     *
     * @param pickUpLocation The {@link Location} object to associate with this participant's pickup. Must not be {@code null}.
     * @throws NullPointerException if {@code pickUpLocation} is {@code null}.
     */
    public void setPickUpLocation(Location pickUpLocation) {
        this.pickUpLocation = Objects.requireNonNull(pickUpLocation, "Pickup location cannot be null.");
    }
    /**
     * Retrieves the {@link Location} object representing the participant's drop-off point.
     *
     * @return The {@link Location} object, or {@code null} if not set (though typically required for active participants).
     */
    public Location getDropOffLocation() {
        return dropOffLocation;
    }
    /**
     * Sets the {@link Location} object for the participant's drop-off point.
     *
     * @param dropOffLocation The {@link Location} object to associate with this participant's drop-off. Must not be {@code null}.
     * @throws NullPointerException if {@code dropOffLocation} is {@code null}.
     */
    public void setDropOffLocation(Location dropOffLocation) {
        this.dropOffLocation = Objects.requireNonNull(dropOffLocation, "Drop-off location cannot be null.");
    }
    /**
     * Retrieves the first name of the participant.
     *
     * @return The participant's given name.
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * Sets the first name of the participant.
     *
     * @param firstName The desired first name for the participant. Must not be {@code null} or empty.
     * @throws NullPointerException if {@code firstName} is {@code null}.
     * @throws IllegalArgumentException if {@code firstName} is empty after stripping whitespace.
     */
    public void setFirstName(String firstName) {
        this.firstName = Objects.requireNonNull(firstName, "First name cannot be null.").strip();
        if (this.firstName.isEmpty()) throw new IllegalArgumentException("First name cannot be empty.");
    }
    /**
     * Retrieves the middle name of the participant.
     *
     * @return The participant's middle name, or {@code null} if not provided.
     */
    public String getMiddleName() {
        return middleName;
    }
    /**
     * Sets the middle name of the participant.
     * If the provided middle name is not {@code null}, leading and trailing whitespace will be stripped.
     *
     * @param middleName The desired middle name for the participant. Can be {@code null}.
     */
    public void setMiddleName(String middleName) {
        this.middleName = (middleName != null) ? middleName.strip() : null;
    }
    /**
     * Retrieves the last name or surname of the participant.
     *
     * @return The participant's family name or surname.
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * Sets the last name or surname of the participant.
     *
     * @param lastName The desired last name for the participant. Must not be {@code null} or empty.
     * @throws NullPointerException if {@code lastName} is {@code null}.
     * @throws IllegalArgumentException if {@code lastName} is empty after stripping whitespace.
     */
    public void setLastName(String lastName) {
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null.").strip();
        if (this.lastName.isEmpty()) throw new IllegalArgumentException("Last name cannot be empty.");
    }
    /**
     * Retrieves the primary email address for the participant.
     *
     * @return The participant's email address.
     */
    public String getEmail() {
        return email;
    }
    /**
     * Sets the primary email address for the participant.
     *
     * @param email The desired email address for communication. Must not be {@code null} or empty, and must be a valid format.
     * @throws NullPointerException if {@code email} is {@code null}.
     * @throws IllegalArgumentException if {@code email} is empty after stripping whitespace, or if it's not a valid email format.
     */
    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email cannot be null.").strip();
        if (this.email.isEmpty()) throw new IllegalArgumentException("Email cannot be empty.");
        // Basic email format validation (more robust validation might be in a dedicated validator)
        if (!this.email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }
    /**
     * Retrieves the primary phone number for the participant.
     * <p>
     * Note: This method returns the phone number as stored (numerical digits only).
     * The UI is responsible for formatting this number for display (e.g., `+# (###) ###-####`).
     * </p>
     * @return The participant's contact phone number as a string of digits.
     */
    public String getPhone() {
        return phone;
    }
    /**
     * Sets the primary phone number for the participant.
     * <p>
     * This method processes the input to store only numerical digits.
     * Any non-digit characters (spaces, hyphens, parentheses, etc.) will be removed.
     * It also validates that the resulting numeric string is not empty and has a reasonable length.
     * </p>
     * @param phone The desired contact phone number. Must not be {@code null}.
     * @throws NullPointerException if {@code phone} is {@code null}.
     * @throws IllegalArgumentException if the phone number, after stripping non-digits, is empty
     * or shorter than 7 digits (a common minimum for local phone numbers).
     */
    public void setPhone(String phone) {
        Objects.requireNonNull(phone, "Phone number cannot be null.");
        String numericPhone = phone.replaceAll("[^0-9]", ""); // Keep only digits

        if (numericPhone.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty after removing special characters.");
        }
        // Basic length validation (e.g., min 7 digits for a local number, up to 15 for international)
        if (numericPhone.length() < 7 || numericPhone.length() > 15) {
            throw new IllegalArgumentException("Phone number must be between 7 and 15 digits long.");
        }

        this.phone = numericPhone;
    }
    /**
     * Retrieves the calculated distance in miles between the participant's pickup and drop-off locations.
     *
     * @return The distance in miles as a {@link BigDecimal}.
     */
    public BigDecimal getDistanceMiles() {
        return distanceMiles;
    }
    /**
     * Sets the calculated distance in miles between the participant's pickup and drop-off locations.
     *
     * @param distanceMiles The distance in miles to set. Must not be {@code null} and must be non-negative.
     * @throws NullPointerException if {@code distanceMiles} is {@code null}.
     * @throws IllegalArgumentException if {@code distanceMiles} is negative.
     */
    public void setDistanceMiles(BigDecimal distanceMiles) {
        this.distanceMiles = Objects.requireNonNull(distanceMiles, "Distance in miles cannot be null.");
        if (this.distanceMiles.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Distance in miles cannot be negative.");
        }
    }
    /**
     * Retrieves the {@link LocalDate} when the participant officially joined or was added to the vanpool system.
     *
     * @return The {@link LocalDate} representing the join date.
     */
    public LocalDate getJoinDate() {
        return joinDate;
    }
    /**
     * Sets the {@link LocalDate} when the participant joined the vanpool program.
     *
     * @param joinDate The {@link LocalDate} to set as the join date. Must not be {@code null} and must not be in the future.
     * @throws NullPointerException if {@code joinDate} is {@code null}.
     * @throws IllegalArgumentException if {@code joinDate} is in the future.
     */
    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = Objects.requireNonNull(joinDate, "Join date cannot be null.");
        if (this.joinDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Join date cannot be in the future.");
        }
    }
    /**
     * Checks if the participant is currently marked as active in the system.
     *
     * @return {@code true} if the participant is active; {@code false} otherwise.
     */
    public boolean isActive() { // Changed getter name to isActive()
        return isActive;
    }
    /**
     * Sets the active status of the participant.
     *
     * @param isActive {@code true} to mark the participant as active; {@code false} to mark as inactive.
     */
    public void setIsActive(boolean isActive) { // Setter name remains setIsActive()
        this.isActive = isActive;
    }
    /**
     * Retrieves the {@link Program} enum associated with the participant.
     *
     * @return The {@link Program} enum constant for the participant's program.
     */
    public Program getProgram() {
        return program;
    }
    /**
     * Sets the {@link Program} for the participant.
     *
     * @param program The {@link Program} enum constant to set. Must not be {@code null}.
     * @throws NullPointerException if {@code program} is {@code null}.
     */
    public void setProgram(Program program) {
        this.program = Objects.requireNonNull(program, "Program cannot be null.");
    }
    /**
     * Retrieves the monthly benefit amount the participant is allotted.
     *
     * @return The {@link BigDecimal} monetary benefit amount (static monthly allowance).
     */
    public BigDecimal getBenefitAmount() {
        return benefitAmount;
    }
    /**
     * Sets the monthly benefit amount the participant is allotted.
     *
     * @param benefitAmount The {@link BigDecimal} monetary benefit amount to set. Must not be {@code null} and must be non-negative.
     * @throws NullPointerException if {@code benefitAmount} is {@code null}.
     * @throws IllegalArgumentException if {@code benefitAmount} is negative.
     */
    public void setBenefitAmount(BigDecimal benefitAmount) {
        this.benefitAmount = Objects.requireNonNull(benefitAmount, "Benefit amount cannot be null.");
        if (this.benefitAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Benefit amount cannot be negative.");
        }
    }
    /**
     * Retrieves the {@link Role} assigned to this participant, indicating their specific
     * responsibilities or administrative privileges within the vanpool (e.g., Coordinator, Treasurer, Driver).
     *
     * @return The {@link Role} enum constant for the participant's role.
     */
    public Role getRole() {
        return role;
    }
    /**
     * Sets the {@link Role} for the participant.
     *
     * @param role The {@link Role} enum constant to set. Must not be {@code null}.
     * @throws NullPointerException if {@code role} is {@code null}.
     */
    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "Participant role cannot be null.");
    }
    /**
     * Retrieves any additional notes or remarks associated with this participant.
     *
     * @return A string containing notes, or {@code null} if no notes are present.
     */
    public String getNotes() {
        return notes;
    }
    /**
     * Sets additional notes or administrative comments for this participant.
     * If the provided notes are not {@code null}, leading and trailing whitespace will be stripped.
     *
     * @param notes The string containing notes to set. Can be {@code null}.
     */
    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.strip() : null;
    }

    // ---------------------
    // Utilities
    // ---------------------

    /**
     * <p>
     * Returns a string representation of the {@code Participant} object.
     * This method is primarily used for debugging and logging, providing
     * a concise summary of the participant's key identifying attributes.
     * </p>
     * <p>
     * The format includes the participant ID (or "null" if not assigned),
     * first name, last name, email, and vanpool role.
     * </p>
     *
     * @return A string in the format:
     * "Participant{ID=..., Name='...', Email='...', VanpoolRole='...'}"
     */
    @Override
    public String toString() {
        return "Participant{" +
                "participantID=" + participantID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based primarily on the unique {@code participantID}.
     * </p>
     * <p>
     * This method adheres to the general contract of the {@link Object#equals(Object)} method.
     * It correctly handles cases where {@code participantID} might be {@code null} for unpersisted entities.
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
        // Use Objects.equals to handle potential null participantIDs gracefully
        return Objects.equals(participantID, that.participantID);
    }
    /**
     * <p>
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hash tables such as those provided by {@link java.util.HashMap}.
     * </p>
     * <p>
     * The hash code is generated based on the unique {@code participantID}. If {@code participantID}
     * is {@code null} (for unpersisted entities), its hash code will be 0, as per {@link Objects#hash(Object...)}.
     * This ensures that objects considered equal by {@link #equals(Object)} will have the same hash code.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(participantID);
    }
}