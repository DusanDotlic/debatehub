package com.debatehub.backend.repo;

import com.debatehub.backend.domain.Debate;
import com.debatehub.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DebateRepository extends JpaRepository<Debate, UUID> {
    Optional<Debate> findBySlug(String slug);
    List<Debate> findByHostUser(User hostUser);
}
