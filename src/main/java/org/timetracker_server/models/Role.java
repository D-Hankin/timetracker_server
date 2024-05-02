package org.timetracker_server.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.security.Permission;
import java.util.Set;

public class Role {
    
    @BsonId
    private ObjectId roleId;
    private String name;
    private Set<Permission> permissions;

    public Role() {
    }

    public Role(ObjectId roleId, String name, Set<Permission> permissions) {
        this.roleId = roleId;
        this.name = name;
        this.permissions = permissions;
    }

    public ObjectId getRoleId() {
        return roleId;
    }

    public void setRoleId(ObjectId roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
