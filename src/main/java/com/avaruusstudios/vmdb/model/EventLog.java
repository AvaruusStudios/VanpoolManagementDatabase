package com.avaruusstudios.vmdb.model;

import java.time.LocalDateTime;

/**
 * Represents a log entry capturing system-level events (insert, update, delete, or errors).
 * Useful for audit and debugging.
 */
public class EventLog {
    private int eventID;
    private LocalDateTime eventDate;
    private String eventType;
    private String tableName;
    private Integer recordID;
    private Integer errorCode;
    private String description;
    private String userName;

    public EventLog() {}

    public EventLog(int eventID, LocalDateTime eventDate, String eventType, String tableName,
                    Integer recordID, Integer errorCode, String description, String userName) {
        this.eventID = eventID;
        this.eventDate = eventDate;
        this.eventType = eventType;
        this.tableName = tableName;
        this.recordID = recordID;
        this.errorCode = errorCode;
        this.description = description;
        this.userName = userName;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getRecordID() {
        return recordID;
    }

    public void setRecordID(Integer recordID) {
        this.recordID = recordID;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
