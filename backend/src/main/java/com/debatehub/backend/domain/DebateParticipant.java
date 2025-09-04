package com.debatehub.backend.domain;

import com.debatehub.backend.domain.ids.DebateParticipantId;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "debate_participants")
public class DebateParticipant {

    @EmbeddedId
    private DebateParticipantId id = new DebateParticipantId();

    @MapsId("debateId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debate_id")
    private Debate debate;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String role; // host | debater | moderator

    @Column(name = "joined_at", nullable = false)
    private OffsetDateTime joinedAt = OffsetDateTime.now();

    public DebateParticipant() {}

    // Getters/setters
    public DebateParticipantId getId() { return id; }
    public void setId(DebateParticipantId id) { this.id = id; }
    public Debate getDebate() { return debate; }
    public void setDebate(Debate debate) { this.debate = debate; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public OffsetDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(OffsetDateTime joinedAt) { this.joinedAt = joinedAt; }
}
