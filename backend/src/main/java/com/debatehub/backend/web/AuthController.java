package com.debatehub.backend.web;

import com.debatehub.backend.domain.User;
import com.debatehub.backend.service.UserService;
import com.debatehub.backend.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService users;

    public AuthController(UserService users) {
        this.users = users;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            User u = users.register(req.getEmail(), req.getDisplayName(), req.getPassword());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Registered",
                    "user", Map.of(
                            "id", u.getId(),
                            "email", u.getEmail(),
                            "displayName", u.getDisplayName()
                    )
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        User u = users.authenticate(req.getEmail(), req.getPassword());
        if (u == null) {
            return ResponseEntity.status(401).body(new AuthResponse(false, "Invalid credentials"));
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login ok",
                "user", Map.of(
                        "id", u.getId(),
                        "email", u.getEmail(),
                        "displayName", u.getDisplayName()
                )
        ));
    }

    @PostMapping("/auth/change-password")
    public ResponseEntity<AuthResponse> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        boolean ok = users.changePassword(req.getEmail(), req.getOldPassword(), req.getNewPassword());
        if (!ok) return ResponseEntity.status(400).body(new AuthResponse(false, "Invalid email or password"));
        return ResponseEntity.ok(new AuthResponse(true, "Password changed"));
    }

    @DeleteMapping("/account")
    public ResponseEntity<AuthResponse> deleteAccount(@Valid @RequestBody DeleteAccountRequest req) {
        boolean ok = users.deleteAccount(req.getEmail(), req.getPassword());
        if (!ok) return ResponseEntity.status(400).body(new AuthResponse(false, "Invalid email or password"));
        return ResponseEntity.ok(new AuthResponse(true, "Account deleted"));
    }
}
