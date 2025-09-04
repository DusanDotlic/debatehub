package com.debatehub.backend.domain;

import com.debatehub.backend.domain.ids.UserPinId;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_pins")
public class UserPin {

    @EmbeddedId
    private UserPinId id = new UserPinId();

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("debateId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debate_id")
    private Debate debate;

    @Column(name = "pinned_at", nullable = false)
    private OffsetDateTime pinnedAt = OffsetDateTime.now();

    public UserPin() {}

    // Getters/setters
    public UserPinId getId() { return id; }
    public void setId(UserPinId id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Debate getDebate() { return debate; }
    public void setDebate(Debate debate) { this.debate = debate; }
    public OffsetDateTime getPinnedAt() { return pinnedAt; }
    public void setPinnedAt(OffsetDateTime pinnedAt) { this.pinnedAt = pinnedAt; }
}
