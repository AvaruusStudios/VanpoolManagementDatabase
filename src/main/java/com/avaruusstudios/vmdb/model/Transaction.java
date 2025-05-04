package com.avaruusstudios.vmdb.model;

import java.time.LocalDate;

/**
 * Represents a financial transaction within the Vanpool system.
 * Tied to a vehicle, category, and optionally an invoice.
 */
public class Transaction {
    private int transactionID;
    private LocalDate transactionDate;
    private int vehicleID;
    private int categoryID;
    private double amount;
    private String paymentMethod;
    private Integer invoiceID;
    private String notes;

    public Transaction() {}

    public Transaction(int transactionID, LocalDate transactionDate, int vehicleID, int categoryID,
                       double amount, String paymentMethod, Integer invoiceID, String notes) {
        this.transactionID = transactionID;
        this.transactionDate = transactionDate;
        this.vehicleID = vehicleID;
        this.categoryID = categoryID;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.invoiceID = invoiceID;
        this.notes = notes;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(Integer invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
