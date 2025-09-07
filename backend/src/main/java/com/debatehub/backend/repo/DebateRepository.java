package com.debatehub.backend.repo;

import com.debatehub.backend.domain.Debate;
import com.debatehub.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;

public interface DebateRepository extends JpaRepository<Debate, UUID> {

    @EntityGraph(attributePaths = {"hostUser"})
    Optional<Debate> findBySlug(String slug);

    @EntityGraph(attributePaths = {"hostUser"})
    List<Debate> findByHostUser(User hostUser);

    boolean existsBySlug(String slug);
}
