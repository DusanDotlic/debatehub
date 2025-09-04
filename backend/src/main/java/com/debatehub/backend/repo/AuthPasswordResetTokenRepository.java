package com.debatehub.backend.repo;

import com.debatehub.backend.domain.AuthPasswordResetToken;
import com.debatehub.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthPasswordResetTokenRepository extends JpaRepository<AuthPasswordResetToken, UUID> {
    Optional<AuthPasswordResetToken> findByToken(String token);
    long deleteByUser(User user);
}
