package com.debatehub.backend.repo;

import com.debatehub.backend.domain.Invitation;
import com.debatehub.backend.domain.Debate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    Optional<Invitation> findByCode(String code);
    long countByDebate(Debate debate);
}
