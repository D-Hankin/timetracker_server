package org.timetracker_server.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import jakarta.validation.constraints.Email;

import java.util.Set;

public class User {
    
    @BsonId
    private ObjectId userId;
    private String username;
    private String name;
    private String password;
    private Email email;
    private Set<ObjectId> roleId;

    public User() {
    }

    public User(ObjectId userId, String username, String name, String password, Set<ObjectId> roleIds) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.password = password;
        this.roleId = roleIds;
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

    public Set<ObjectId> getRoleId() {
        return roleId;
    }

    public void setRoleId(Set<ObjectId> roleId) {
        this.roleId = roleId;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }
}
