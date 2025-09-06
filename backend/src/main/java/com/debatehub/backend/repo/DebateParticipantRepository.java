package com.debatehub.backend.repo;

import com.debatehub.backend.domain.DebateParticipant;
import com.debatehub.backend.domain.ids.DebateParticipantId;
import com.debatehub.backend.domain.Debate;
import com.debatehub.backend.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebateParticipantRepository extends JpaRepository<DebateParticipant, DebateParticipantId> {

    @EntityGraph(attributePaths = {"debate", "debate.hostUser"})
    List<DebateParticipant> findByUser(User user);

    @EntityGraph(attributePaths = {"debate", "debate.hostUser", "user"})
    List<DebateParticipant> findByDebate(Debate debate);

    long countByDebate(Debate debate);

    boolean existsByDebateAndUser(Debate debate, User user);
}
