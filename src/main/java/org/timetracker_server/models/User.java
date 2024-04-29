package org.timetracker_server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "users")
public class User {
    
    @Id
    @Column(unique = true)
    private String userId;
    private String username;
    private String name;
    private String password;

    public User() {
    }

    public User(String userId, String username, String name, String password) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    
    
}
