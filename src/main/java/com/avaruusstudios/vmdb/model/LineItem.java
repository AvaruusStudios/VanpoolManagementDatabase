package com.avaruusstudios.vmdb.model;

public class LineItem {
    private int id;
    private int invoiceID;
    private int participantID;
    private Amount amount;
    private double pastDue;
    private int isPaid;
    private String notes;

    /**
     * Default constructor for a blank participant object.
     * Typically used when populating data via deserialization or UI binding.
     */
    public LineItem() {}
    /**
     * Instantiates a new Line item.
     *
     * @param id            the id
     * @param invoiceID     the invoice id
     * @param participantID the participant id
     * @param amount        the amount
     * @param pastDue       the pastdue
     * @param isPaid        the is paid
     * @param notes         the notes
     */
    public LineItem(int id, int invoiceID, int participantID, Amount amount, double pastDue, int isPaid, String notes) {
        this.id = id;
        this.invoiceID = invoiceID;
        this.participantID = participantID;
        this.amount = amount;
        this.pastDue = pastDue;
        this.isPaid = isPaid;
        this.notes = notes;
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * Gets invoice id.
     *
     * @return the invoice id
     */
    public int getInvoiceID() {
        return invoiceID;
    }
    /**
     * Sets invoice id.
     *
     * @param invoiceID the invoice id
     */
    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }
    /**
     * Gets participant id.
     *
     * @return the participant id
     */
    public int getParticipantID() {
        return participantID;
    }
    /**
     * Sets participant id.
     *
     * @param participantID the participant id
     */
    public void setParticipantID(int participantID) {
        this.participantID = participantID;
    }
    /**
     * Gets amount.
     *
     * @return the amount
     */
    public Amount getAmount() {
        return amount;
    }
    /**
     * Sets amount.
     *
     * @param amount the amount
     */
    public void setAmount(Amount amount) {
        this.amount = amount;
    }
    /**
     * Gets pastdue.
     *
     * @return the pastdue
     */
    public double getPastDue() {
        return pastDue;
    }
    /**
     * Sets pastdue.
     *
     * @param pastDue the pastdue
     */
    public void setPastDue(double pastDue) {
        this.pastDue = pastDue;
    }
    /**
     * Gets is paid.
     *
     * @return the is paid
     */
    public int getIsPaid() {
        return isPaid;
    }
    /**
     * Sets is paid.
     *
     * @param isPaid the is paid
     */
    public void setIsPaid(int isPaid) {
        this.isPaid = isPaid;
    }
    /**
     * Gets notes.
     *
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }
    /**
     * Sets notes.
     *
     * @param notes the notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    // ---------------------
    // Computed Properties
    // ---------------------

    /**
     * Gets total due amount (current + past due).
     *
     * @return total amount due
     */
    public double getTotalDue() {
        return (amount != null ? amount.getTotal() : 0) + pastDue;
    }

    /**
     * Dynamically determines if this line item is fully paid.
     *
     * @return true if paidAmount >= totalDue
     */
    public boolean isPaid() {
        return Double.compare(pastDue, getTotalDue()) >= 0;
    }
}
