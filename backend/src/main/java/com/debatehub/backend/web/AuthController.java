package com.debatehub.backend.web;

import com.debatehub.backend.domain.User;
import com.debatehub.backend.repo.UserRepository;
import com.debatehub.backend.security.JwtUtil;
import com.debatehub.backend.web.dto.SimpleResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.debatehub.backend.service.UserService;
import com.debatehub.backend.web.dto.ChangePasswordRequest;
import com.debatehub.backend.web.dto.DeleteAccountRequest;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;
    private final AuthenticationManager authMgr;
    private final UserService userService;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtUtil jwt, AuthenticationManager authMgr,UserService userService) {
        this.users = users; this.encoder = encoder; this.jwt = jwt; this.authMgr = authMgr;
        this.userService = userService;
    }

    public record RegisterReq(@Email @NotBlank String email,
                              @NotBlank String displayName,
                              @NotBlank String password) {}
    public record LoginReq(@Email @NotBlank String email,
                           @NotBlank String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req) {
        if (users.findByEmailIgnoreCase(req.email()).isPresent())
            return ResponseEntity.badRequest().body(new SimpleResponse(false, "Email already registered"));

        User u = new User();
        u.setEmail(req.email());
        u.setDisplayName(req.displayName());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setActive(true);
        users.save(u);

        String token = jwt.generate(u.getEmail(), Map.of("dn", u.getDisplayName()));
        return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token,
                "user", Map.of("email", u.getEmail(), "displayName", u.getDisplayName())
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        authMgr.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        var u = users.findByEmailIgnoreCase(req.email()).orElseThrow();
        String token = jwt.generate(u.getEmail(), Map.of("dn", u.getDisplayName()));
        return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token,
                "user", Map.of("email", u.getEmail(), "displayName", u.getDisplayName())
        ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            userService.changePassword(email, req.getOldPassword(), req.getNewPassword(), users, encoder);
            return ResponseEntity.ok(new SimpleResponse(true, "Password changed successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, ex.getMessage()));
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountRequest req) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            userService.deleteAccount(email, req.getPassword(), users, encoder);
            return ResponseEntity.ok(new SimpleResponse(true, "Account deleted"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, ex.getMessage()));
        }
    }

}
