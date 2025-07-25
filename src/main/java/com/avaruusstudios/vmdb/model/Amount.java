package com.avaruusstudios.vmdb.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a composite financial amount, encapsulating monetary values that consist of
 * distinct components, such as a benefit due portion and a personal due portion.
 * This class provides a structured and **immutable** way to handle these dual-component amounts,
 * often seen in financial transactions within the Vanpool Management System.
 *
 * <p>
 * Crucially, all monetary values are handled using {@link BigDecimal} to ensure
 * arbitrary precision and avoid floating-point arithmetic errors, which are unacceptable
 * in financial calculations.
 * </p>
 *
 * <p>
 * Once an {@code Amount} object is created, its values cannot be changed. This ensures
 * data integrity and makes the object inherently thread-safe.
 * </p>
 *
 * <p>
 * It offers methods to access individual components and to conveniently calculate their total sum.
 * This model is designed to correspond directly to composite amount fields in database tables
 * like {@code InvoiceItems}.
 * </p>
 *
 * @author AvaruusStudios
 * @version 1.0
 * Created On: 2025-07-12
 * Updated On: 2025-07-12
 */
public final class Amount {
    /**
     * <p>
     * The portion of an amount designated as **due for benefit coverage** or **subsidy**.
     * </p>
     * <p>
     * This {@link BigDecimal} field represents a monetary value that is covered by a
     * benefit program or a subsidy, rather than being paid directly by an individual.
     * It corresponds to the {@code BenefitPayment REAL NOT NULL} column in the
     * {@code InvoiceItems} table. When storing in a database, {@link BigDecimal} values
     * should be carefully converted to a suitable database type (e.g., `REAL` or `NUMERIC`),
     * ensuring that precision is maintained.
     * </p>
     */
    private final BigDecimal benefitDue;
    /**
     * <p>
     * The portion of an amount designated as **due for personal payment**.
     * </p>
     * <p>
     * This {@link BigDecimal} field represents a monetary value that is the responsibility
     * of an individual (e.g., a participant) to pay directly. It corresponds to the
     * {@code PersonalPayment REAL NOT NULL} column in the {@code InvoiceItems} table.
     * Like {@link #benefitDue}, proper conversion is needed for database storage.
     * </p>
     */
    private final BigDecimal personalDue;

    /**
     * <p>
     * Default constructor for creating an empty {@code Amount} object.
     * </p>
     * <p>
     * This constructor initializes both the benefit and personal due components to {@code BigDecimal.ZERO},
     * effectively creating an amount with no value, which can then be used as a starting point.
     * </p>
     */
    public Amount() {
        this(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * <p>
     * Constructs an {@code Amount} object with specified benefit and personal due components.
     * </p>
     * <p>
     * This is the primary constructor used to create an immutable {@code Amount} instance.
     * All components are set at the time of instantiation and cannot be changed thereafter.
     * {@link BigDecimal} values are used for precise financial representation.
     * </p>
     *
     * @param benefitDue The {@link BigDecimal} monetary value designated as due for benefit coverage. Must not be {@code null} and must be non-negative.
     * @param personalDue The {@link BigDecimal} monetary value designated as due for personal payment. Must not be {@code null} and must be non-negative.
     * @throws NullPointerException if {@code benefitDue} or {@code personalDue} is {@code null}.
     * @throws IllegalArgumentException if {@code benefitDue} or {@code personalDue} is negative.
     */
    public Amount(BigDecimal benefitDue, BigDecimal personalDue) {
        // Ensure that BigDecimal objects are not null
        this.benefitDue = Objects.requireNonNull(benefitDue, "Benefit due amount cannot be null.");
        // Ensure that benefitDue is non-negative
        if (this.benefitDue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Benefit due amount cannot be negative.");
        }

        this.personalDue = Objects.requireNonNull(personalDue, "Personal due amount cannot be null.");
        // Ensure that personalDue is non-negative
        if (this.personalDue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Personal due amount cannot be negative.");
        }
    }

    // --- Getters ---

    /**
     * <p>
     * Retrieves the portion of this amount designated as due for benefit coverage.
     * </p>
     *
     * @return The {@link BigDecimal} value representing the benefit due component.
     */
    public BigDecimal getBenefitAmountDue() {
        return benefitDue;
    }

    /**
     * <p>
     * Retrieves the portion of this amount designated as due for personal payment.
     * </p>
     *
     * @return The {@link BigDecimal} value representing the personal due component.
     */
    public BigDecimal getPersonalAmountDue() {
        return personalDue;
    }

    // --- Utility Methods ---

    /**
     * <p>
     * Calculates the total sum of the benefit due and personal due components of this amount.
     * </p>
     * <p>
     * This method provides the combined monetary value of the composite amount using
     * {@link BigDecimal#add(BigDecimal)} for precise addition.
     * </p>
     *
     * @return The {@link BigDecimal} value representing the combined total of {@link #benefitDue} and {@link #personalDue}.
     */
    public BigDecimal getTotal() {
        return benefitDue.add(personalDue);
    }

    /**
     * <p>
     * Returns a string representation of the {@code Amount} object.
     * </p>
     * <p>
     * This method is primarily used for debugging and logging purposes, offering a
     * concise summary of the amount's components and its calculated total.
     * </p>
     *
     * @return A string in the format "Amount{benefit=..., personal=..., total=...}".
     */
    @Override
    public String toString() {
        return "Amount{" +
                "benefit=" + benefitDue +
                ", personal=" + personalDue +
                ", total=" + getTotal() +
                '}';
    }

    /**
     * <p>
     * Indicates whether some other object is "equal to" this one.
     * </p>
     * <p>
     * Equality for {@code Amount} objects is determined by precisely comparing both
     * the {@code benefitDue} and {@code personalDue} components using
     * {@link BigDecimal#equals(Object)}. This method considers two {@code BigDecimal}
     * objects equal if they have the same value and scale.
     * </p>
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount = (Amount) o;
        // Use BigDecimal's equals method for precise comparison
        return benefitDue.equals(amount.benefitDue) &&
                personalDue.equals(amount.personalDue);
    }

    /**
     * <p>
     * Returns a hash code value for the object.
     * </p>
     * <p>
     * The hash code is generated based on both the {@code benefitDue} and
     * {@code personalDue} components. This ensures that objects considered equal
     * by {@link #equals(Object)} will also have the same hash code, fulfilling
     * the contract required for proper functioning in hash-based collections
     * like {@link java.util.HashMap} and {@link java.util.HashSet}.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(benefitDue, personalDue);
    }
}