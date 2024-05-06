package org.timetracker_server.models;

import java.time.LocalDateTime;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class Entry {
    
    @BsonId
    private ObjectId entryId;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private String username;

    public Entry() {}

    public Entry(ObjectId entryId, String name, LocalDateTime startTime, LocalDateTime stopTime, String username) {
        this.entryId = entryId;
        this.name = name;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.username = username;
    }

    public ObjectId getEntryId() {
        return entryId;
    }

    public void setEntryId(ObjectId entryId) {
        this.entryId = entryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(LocalDateTime stopTime) {
        this.stopTime = stopTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    
}
