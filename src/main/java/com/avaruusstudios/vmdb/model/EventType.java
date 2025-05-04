package com.avaruusstudios.vmdb.model;

/**
 * Enum representing types of system events in the EventLog.
 */
public enum EventType {
    INSERT("Insert", "INS"),
    UPDATE("Update", "UPD"),
    DELETE("Delete", "DEL"),
    ERROR("Error", "ERR");

    private final String name;
    private final String abbreviation;

    EventType(String name, String abbreviation) {
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
