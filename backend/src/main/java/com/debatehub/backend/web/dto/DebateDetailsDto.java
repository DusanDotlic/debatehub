package com.debatehub.backend.web.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class DebateDetailsDto {
    private String slug;
    private String title;
    private String description;
    private String status;
    private boolean inviteOnly;
    private String hostDisplayName;
    private String hostEmail;
    private OffsetDateTime createdAt;
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;
    private List<ParticipantDto> participants;

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isInviteOnly() { return inviteOnly; }
    public void setInviteOnly(boolean inviteOnly) { this.inviteOnly = inviteOnly; }
    public String getHostDisplayName() { return hostDisplayName; }
    public void setHostDisplayName(String hostDisplayName) { this.hostDisplayName = hostDisplayName; }
    public String getHostEmail() { return hostEmail; }
    public void setHostEmail(String hostEmail) { this.hostEmail = hostEmail; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }
    public OffsetDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(OffsetDateTime endedAt) { this.endedAt = endedAt; }
    public List<ParticipantDto> getParticipants() { return participants; }
    public void setParticipants(List<ParticipantDto> participants) { this.participants = participants; }
}
