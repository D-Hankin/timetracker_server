package org.timetracker_server.models;

import java.time.LocalDateTime;

import org.bson.codecs.pojo.annotations.BsonId;

public class Entry {
    
    @BsonId
    private String entryId;
    private String name;
    private String username;
    private int minutes;

    public Entry() {}

    public Entry(String entryId, String name, LocalDateTime startTime, LocalDateTime stopTime, String username, int minutes) {
        this.entryId = entryId;
        this.name = name;
        this.username = username;
        this.minutes = minutes;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }


    
}
