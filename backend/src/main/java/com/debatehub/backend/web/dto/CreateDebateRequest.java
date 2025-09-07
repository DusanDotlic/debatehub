package com.debatehub.backend.web.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateDebateRequest {
    @NotBlank
    private String title;
    private String description;
    private boolean inviteOnly = true;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isInviteOnly() { return inviteOnly; }
    public void setInviteOnly(boolean inviteOnly) { this.inviteOnly = inviteOnly; }
}
