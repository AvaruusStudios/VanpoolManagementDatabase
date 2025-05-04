package com.avaruusstudios.vmdb.model;

/**
 * Represents a category for financial classification.
 * Can be of type Income, Expense, or Credit. May optionally be tied to a specific participant.
 */
public class Category {
    private int categoryID;
    private String categoryName;
    private String categoryType; // Could later enum this to restrict to Income, Expense, Credit
    private Integer participantID; // Nullable FK
    private String description;

    public Category() {}

    public Category(int categoryID, String categoryName, String categoryType, Integer participantID, String description) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.participantID = participantID;
        this.description = description;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public Integer getParticipantID() {
        return participantID;
    }

    public void setParticipantID(Integer participantID) {
        this.participantID = participantID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
