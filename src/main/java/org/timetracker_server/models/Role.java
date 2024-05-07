package org.timetracker_server.models;

import org.bson.codecs.pojo.annotations.BsonId;
import java.security.Permission;
import java.util.Set;

public class Role {
    
    @BsonId
    private String roleId;
    private String name;
    private Set<Permission> permissions;

    public Role(String roleId, String name, Set<Permission> permissions) {
        this.roleId = roleId;
        this.name = name;
        this.permissions = permissions;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
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
