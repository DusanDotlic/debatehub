package com.debatehub.backend.domain.ids;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UserPinId implements Serializable {
    private UUID userId;
    private UUID debateId;

    public UserPinId() {}

    public UserPinId(UUID userId, UUID debateId) {
        this.userId = userId;
        this.debateId = debateId;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getDebateId() { return debateId; }
    public void setDebateId(UUID debateId) { this.debateId = debateId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPinId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(debateId, that.debateId);
    }
    @Override public int hashCode() { return Objects.hash(userId, debateId); }
}
