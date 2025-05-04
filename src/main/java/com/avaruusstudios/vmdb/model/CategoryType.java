package com.avaruusstudios.vmdb.model;

/**
 * Enum representing the category type used in financial categorization.
 */
public enum CategoryType {
    INCOME("Income", "INC"),
    EXPENSE("Expense", "EXP"),
    CREDIT("Credit", "CRD");

    private final String name;
    private final String abbreviation;

    CategoryType(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    /**
     * Gets the full name of the category type.
     *
     * @return the full name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the abbreviation of the category type.
     *
     * @return the abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Returns the abbreviation when serialized or printed.
     *
     * @return the abbreviation
     */
    @Override
    public String toString() {
        return abbreviation;
    }
}
