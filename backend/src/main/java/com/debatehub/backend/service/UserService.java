package com.debatehub.backend.service;

import com.debatehub.backend.domain.User;
import com.debatehub.backend.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public UserService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Transactional
    public User register(String email, String displayName, String rawPassword) {
        if (users.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        User u = new User();
        u.setEmail(email.trim());
        u.setDisplayName(displayName.trim());
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setActive(true);
        u.setCreatedAt(OffsetDateTime.now());
        u.setUpdatedAt(OffsetDateTime.now());
        return users.save(u);
    }

    public User authenticate(String email, String rawPassword) {
        return users.findByEmailIgnoreCase(email)
                .filter(u -> encoder.matches(rawPassword, u.getPasswordHash()))
                .orElse(null);
    }

    public void changePassword(String email, String oldPassword, String newPassword,
                               UserRepository userRepo, PasswordEncoder encoder) {
        User u = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!encoder.matches(oldPassword, u.getPasswordHash())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        u.setPasswordHash(encoder.encode(newPassword));
        userRepo.save(u);
    }

    public void deleteAccount(String email, String password,
                              UserRepository userRepo, PasswordEncoder encoder) {
        User u = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!encoder.matches(password, u.getPasswordHash())) {
            throw new IllegalArgumentException("Password is incorrect");
        }
        userRepo.delete(u);
    }

}
