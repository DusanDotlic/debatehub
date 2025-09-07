package com.debatehub.backend.web.dto;

public class UpdateDebateRequest {
    private String title;
    private String description;
    private Boolean inviteOnly; // nullable: update only when provided

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title != null ? title.trim() : null; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description != null ? description.trim() : null; }

    public Boolean getInviteOnly() { return inviteOnly; }
    public void setInviteOnly(Boolean inviteOnly) { this.inviteOnly = inviteOnly; }
}
