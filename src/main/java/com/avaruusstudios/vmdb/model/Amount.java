package com.avaruusstudios.vmdb.model;

/**
 * Represents a split of charges between benefit and personal components.
 */
public class Amount {
    private double benefit;
    private double personal;

    public Amount() {}

    public Amount(double benefit, double personal) {
        this.benefit = benefit;
        this.personal = personal;
    }

    public double getBenefit() {
        return benefit;
    }

    public void setBenefit(double benefit) {
        this.benefit = benefit;
    }

    public double getPersonal() {
        return personal;
    }

    public void setPersonal(double personal) {
        this.personal = personal;
    }

    /**
     * Returns the total of benefit + personal.
     */
    public double getTotal() {
        return benefit + personal;
    }

    /**
     * Returns a new Amount where pastDue is added to personal .
     *
     * @param pastDue amount from a previous cycle
     * @return new Amount with pastDue added to personal
     */
    public Amount withPastDue(double pastDue) {
        return new Amount(benefit, personal + pastDue);
    }

    @Override
    public String toString() {
        return String.format("Amount[Benefit=%.2f, Personal=%.2f, Total=%.2f]",
                benefit, personal, getTotal());
    }
}
