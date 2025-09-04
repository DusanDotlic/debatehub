package com.debatehub.backend.domain.ids;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class DebateParticipantId implements Serializable {
    private UUID debateId;
    private UUID userId;

    public DebateParticipantId() {}

    public DebateParticipantId(UUID debateId, UUID userId) {
        this.debateId = debateId;
        this.userId = userId;
    }

    public UUID getDebateId() { return debateId; }
    public void setDebateId(UUID debateId) { this.debateId = debateId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebateParticipantId that)) return false;
        return Objects.equals(debateId, that.debateId) && Objects.equals(userId, that.userId);
    }
    @Override public int hashCode() { return Objects.hash(debateId, userId); }
}
