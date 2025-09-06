package com.debatehub.backend.web;

import com.debatehub.backend.domain.Debate;
import com.debatehub.backend.domain.DebateParticipant;
import com.debatehub.backend.service.DebateService;
import com.debatehub.backend.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/debates")
public class DebateController {

    private final DebateService debates;

    public DebateController(DebateService debates) {
        this.debates = debates;
    }

    // POST /api/debates
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateDebateRequest req) {
        try {
            Debate d = debates.createDebate(req.getHostEmail(), req.getTitle(), req.getDescription(), req.isInviteOnly());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "slug", d.getSlug()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new SimpleResponse(false, "Failed to create debate"));
        }
    }

    // GET /api/debates/mine/started?email=
    @GetMapping("/mine/started")
    public List<DebateCardDto> mineStarted(@RequestParam String email) {
        return debates.listStartedBy(email).stream().map(this::toCard).toList();
    }

    // GET /api/debates/mine/joined?email=
    @GetMapping("/mine/joined")
    public List<DebateCardDto> mineJoined(@RequestParam String email) {
        return debates.listJoinedBy(email).stream().map(this::toCard).toList();
    }

    // GET /api/debates/pinned?email=
    @GetMapping("/pinned")
    public List<DebateCardDto> pinned(@RequestParam String email) {
        return debates.listPinned(email).stream().map(this::toCard).toList();
    }

    // POST /api/debates/{slug}/pin?email=
    @PostMapping("/{slug}/pin")
    public ResponseEntity<SimpleResponse> pin(@PathVariable String slug, @RequestParam String email) {
        try {
            boolean ok = debates.pin(email, slug);
            return ResponseEntity.ok(new SimpleResponse(ok, ok ? "Pinned" : "Already pinned"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(new SimpleResponse(false, "Debate not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, e.getMessage()));
        }
    }

    // DELETE /api/debates/{slug}/pin?email=
    @DeleteMapping("/{slug}/pin")
    public ResponseEntity<SimpleResponse> unpin(@PathVariable String slug, @RequestParam String email) {
        try {
            boolean ok = debates.unpin(email, slug);
            return ResponseEntity.ok(new SimpleResponse(ok, ok ? "Unpinned" : "Not pinned"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(new SimpleResponse(false, "Debate not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, e.getMessage()));
        }
    }

    // GET /api/debates/{slug}
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

        // participants (now real)
        dto.setParticipants(debates.participantsForDebate(d));

        return ResponseEntity.ok(dto);
    }

    private DebateCardDto toCard(Debate d) {
        DebateCardDto dto = new DebateCardDto();
        dto.setSlug(d.getSlug());
        dto.setTitle(d.getTitle());
        dto.setStatus(d.getStatus());
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
