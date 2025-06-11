package com.avaruusstudios.vmdb.model;

import java.util.Objects;

/**
 * <p>
 * Represents a composite financial amount, typically used to encapsulate components
 * like benefit payments and personal payments. This class provides a structured way
 * to handle monetary values that consist of distinct parts.
 * </p>
 *
 * <p>
 * It provides methods to access individual components and calculate a total.
 * </p>
 */
public class Amount {
    /**
     * The portion of an amount designated for benefit payment or subsidy.
     * Corresponds to {@code BenefitPayment REAL NOT NULL} in the InvoiceItems table.
     */
    private double benefitPayment;
    /**
     * The portion of an amount designated for personal payment.
     * Corresponds to {@code PersonalPayment REAL NOT NULL} in the InvoiceItems table.
     */
    private double personalPayment;

    /**
     * Default constructor for creating an empty {@code Amount} object.
     * Initializes both benefit and personal payments to 0.0.
     */
    public Amount() {
        this(0.0, 0.0);
    }
    /**
     * Constructs an {@code Amount} object with specified benefit and personal payment components.
     *
     * @param benefitPayment The amount designated for benefit payment.
     * @param personalPayment The amount designated for personal payment.
     */
    public Amount(double benefitPayment, double personalPayment) {
        this.benefitPayment = benefitPayment;
        this.personalPayment = personalPayment;
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Retrieves the benefit payment portion of this amount.
     *
     * @return The benefit payment amount.
     */
    public double getBenefitPayment() {
        return benefitPayment;
    }
    /**
     * Sets the benefit payment portion of this amount.
     *
     * @param benefitPayment The benefit payment amount to set.
     */
    public void setBenefitPayment(double benefitPayment) {
        this.benefitPayment = benefitPayment;
    }
    /**
     * Retrieves the personal payment portion of this amount.
     *
     * @return The personal payment amount.
     */
    public double getPersonalPayment() {
        return personalPayment;
    }
    /**
     * Sets the personal payment portion of this amount.
     *
     * @param personalPayment The personal payment amount to set.
     */
    public void setPersonalPayment(double personalPayment) {
        this.personalPayment = personalPayment;
    }

    // ---------------------
    // Utility Methods
    // ---------------------

    /**
     * Calculates the total sum of the benefit and personal payment components.
     *
     * @return The combined total of benefitPayment and personalPayment.
     */
    public double getTotal() {
        return benefitPayment + personalPayment;
    }
    /**
     * Returns a string representation of the {@code Amount} object.
     * Primarily used for debugging and logging.
     *
     * @return A string in the format "Amount{benefit=..., personal=..., total=...}".
     */
    @Override
    public String toString() {
        return "Amount{" +
                "benefit=" + benefitPayment +
                ", personal=" + personalPayment +
                ", total=" + getTotal() +
                '}';
    }
    /**
     * Indicates whether some other object is "equal to" this one.
     * Equality is based on both {@code benefitPayment} and {@code personalPayment}.
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount = (Amount) o;
        return Double.compare(amount.benefitPayment, benefitPayment) == 0 &&
                Double.compare(amount.personalPayment, personalPayment) == 0;
    }
    /**
     * Returns a hash code value for the object.
     * The hash code is generated based on {@code benefitPayment} and {@code personalPayment}.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(benefitPayment, personalPayment);
    }
}