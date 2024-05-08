package org.timetracker_server.models;

public class TokenResponse {
    private String token;
    private String expiresIn;
    private String role;

    public TokenResponse() {
    }

    public TokenResponse(String token, String expiresIn, String role) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

