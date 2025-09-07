package com.debatehub.backend.web.dto;

public class StartedDebateDto {
    private String slug;
    private String title;
    private String description;
    private Boolean inviteOnly;

    public StartedDebateDto(String slug, String title, String description, Boolean inviteOnly) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.inviteOnly = inviteOnly;
    }

    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Boolean getInviteOnly() { return inviteOnly; }
}
