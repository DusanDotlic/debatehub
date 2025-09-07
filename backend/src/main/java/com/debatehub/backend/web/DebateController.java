package com.debatehub.backend.web;

import com.debatehub.backend.domain.Debate;
import com.debatehub.backend.service.DebateService;
import com.debatehub.backend.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.debatehub.backend.web.dto.SimpleResponse;


import java.util.*;

@RestController
@RequestMapping("/api/debates")
public class DebateController {

    private final DebateService debates;

    public DebateController(DebateService debates) {
        this.debates = debates;
    }


    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateDebateRequest req, java.security.Principal principal) {
        try {
            if (principal == null) return ResponseEntity.status(401).body(new SimpleResponse(false, "Unauthorized"));
            Debate d = debates.createDebate(principal.getName(), req.getTitle(), req.getDescription(), req.isInviteOnly());
            return ResponseEntity.ok(Map.of("success", true, "slug", d.getSlug()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new SimpleResponse(false, "Failed to create debate"));
        }
    }


    @GetMapping("/mine/started")
    public ResponseEntity<?> mineStarted(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(new SimpleResponse(false, "Unauthorized"));
        }
        var list = debates.listHostedBy(principal.getName()).stream().map(this::toCard).toList();
        return ResponseEntity.ok(list);
    }



    @GetMapping("/mine/joined")
    public ResponseEntity<?> mineJoined(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(new SimpleResponse(false, "Unauthorized"));
        }
        var list = debates.listJoinedBy(principal.getName()).stream().map(this::toCard).toList();
        return ResponseEntity.ok(list);
    }


    @GetMapping("/pinned")
    public ResponseEntity<?> pinned(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(new SimpleResponse(false, "Unauthorized"));
        }
        var list = debates.listPinned(principal.getName()).stream().map(this::toCard).toList();
        return ResponseEntity.ok(list);
    }


    @PostMapping("/{slug}/pin")
    public ResponseEntity<SimpleResponse> pin(@PathVariable String slug, java.security.Principal principal) {
        try {
            if (principal == null) return ResponseEntity.status(401).body(new SimpleResponse(false, "Unauthorized"));
            boolean ok = debates.pin(principal.getName(), slug);
            return ResponseEntity.ok(new SimpleResponse(ok, ok ? "Pinned" : "Already pinned"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(new SimpleResponse(false, "Debate not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{slug}/pin")
    public ResponseEntity<SimpleResponse> unpin(@PathVariable String slug, java.security.Principal principal) {
        try {
            if (principal == null) return ResponseEntity.status(401).body(new SimpleResponse(false, "Unauthorized"));
            boolean ok = debates.unpin(principal.getName(), slug);
            return ResponseEntity.ok(new SimpleResponse(ok, ok ? "Unpinned" : "Not pinned"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(new SimpleResponse(false, "Debate not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{slug}")
    public ResponseEntity<?> updateDebate(
            @PathVariable String slug,
            @RequestBody UpdateDebateRequest req,
            java.security.Principal principal
    ) {
        String email = principal.getName();
        try {
            debates.updateDebate(
                    email,
                    slug,
                    req.getTitle(),
                    req.getDescription(),
                    req.getInviteOnly()
            );
            return ResponseEntity.ok(new SimpleResponse(true, "Debate updated"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, ex.getMessage()));
        }
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<?> deleteDebate(
            @PathVariable String slug,
            java.security.Principal principal
    ) {
        String email = principal.getName();
        try {
            debates.deleteDebate(email, slug);
            return ResponseEntity.ok(new SimpleResponse(true, "Debate deleted"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, ex.getMessage()));
        }
    }

    @PostMapping("/{slug}/join")
    public ResponseEntity<?> join(@PathVariable String slug, java.security.Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body(new SimpleResponse(false, "Unauthorized"));
        try {
            debates.joinDebate(principal.getName(), slug);
            return ResponseEntity.ok(new SimpleResponse(true, "Joined"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, ex.getMessage()));
        }
    }


    @GetMapping("/mine/hosted")
    public ResponseEntity<?> mineHosted(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(new SimpleResponse(false, "Unauthorized"));
        }
        var list = debates.listHostedBy(principal.getName()).stream().map(this::toCard).toList();
        return ResponseEntity.ok(list);
    }

    // GET /api/debates/{slug}  (public)
    @GetMapping("/{slug}")
    public ResponseEntity<?> getBySlug(@PathVariable String slug) {
        var opt = debates.getBySlug(slug);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(new SimpleResponse(false, "Not found"));
        var d = opt.get();
        DebateDetailsDto dto = new DebateDetailsDto();
        dto.setSlug(d.getSlug());
        dto.setTitle(d.getTitle());
        dto.setDescription(d.getDescription());
        dto.setStatus(d.getStatus());
        dto.setInviteOnly(d.isInviteOnly());
        dto.setHostDisplayName(d.getHostUser() != null ? d.getHostUser().getDisplayName() : null);
        dto.setHostEmail(d.getHostUser() != null ? d.getHostUser().getEmail() : null);
        dto.setCreatedAt(d.getCreatedAt());
        dto.setStartedAt(d.getStartedAt());
        dto.setEndedAt(d.getEndedAt());
        dto.setParticipants(debates.participantsForDebate(d));
        return ResponseEntity.ok(dto);
    }

    private DebateCardDto toCard(Debate d) {
        DebateCardDto dto = new DebateCardDto();
        dto.setSlug(d.getSlug());
        dto.setTitle(d.getTitle());
        dto.setStatus(d.getStatus());
        dto.setDescription(d.getDescription());
        dto.setInviteOnly(d.isInviteOnly());

        if (d.getHostUser() != null) {
            dto.setHostDisplayName(d.getHostUser().getDisplayName());
            dto.setHostEmail(d.getHostUser().getEmail());
        }
        dto.setCreatedAt(d.getCreatedAt());
        dto.setStartedAt(d.getStartedAt());
        dto.setParticipantCount((int) debates.participantCount(d));
        return dto;
    }




}
