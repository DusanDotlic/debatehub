package com.debatehub.backend.repo;

import com.debatehub.backend.domain.DebateParticipant;
import com.debatehub.backend.domain.ids.DebateParticipantId;
import com.debatehub.backend.domain.Debate;
import com.debatehub.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebateParticipantRepository extends JpaRepository<DebateParticipant, DebateParticipantId> {
    List<DebateParticipant> findByUser(User user);
    List<DebateParticipant> findByDebate(Debate debate);
}
