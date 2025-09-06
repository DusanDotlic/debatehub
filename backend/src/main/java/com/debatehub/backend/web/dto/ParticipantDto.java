package com.debatehub.backend.web.dto;

public class ParticipantDto {
    private String displayName;
    private String email;
    private String role;

    public ParticipantDto() {}
    public ParticipantDto(String displayName, String email, String role) {
        this.displayName = displayName;
        this.email = email;
        this.role = role;
    }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
