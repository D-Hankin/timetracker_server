package org.timetracker_server.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class User {
    
    @BsonId
    private ObjectId userId;
    private String username;
    private String name;
    private String password;
    private String email;
    private ObjectId roleId;

    public User() {
    }

    public User(ObjectId userId, String username, String name, String password, String email, ObjectId roleId) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.password = password;
        this.roleId = roleId;
        this.email = email;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
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

    public ObjectId getRoleId() {
        return roleId;
    }

    public void setRoleId(ObjectId roleId) {
        this.roleId = roleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
