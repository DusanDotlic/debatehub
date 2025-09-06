package com.debatehub.backend.web.dto;

import java.time.OffsetDateTime;

public class DebateCardDto {
    private String slug;
    private String title;
    private String status;
    private String hostDisplayName;
    private String hostEmail;
    private int participantCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime startedAt;

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHostDisplayName() { return hostDisplayName; }
    public void setHostDisplayName(String hostDisplayName) { this.hostDisplayName = hostDisplayName; }
    public String getHostEmail() { return hostEmail; }
    public void setHostEmail(String hostEmail) { this.hostEmail = hostEmail; }
    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }
}

