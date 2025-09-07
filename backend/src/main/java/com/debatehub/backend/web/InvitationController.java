package com.debatehub.backend.web;

import com.debatehub.backend.domain.Invitation;
import com.debatehub.backend.service.InvitationService;
import com.debatehub.backend.web.dto.AcceptInvitationRequest;
import com.debatehub.backend.web.dto.CreateInvitationRequest;
import com.debatehub.backend.web.dto.CreateInvitationResponse;
import com.debatehub.backend.web.dto.SimpleResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService invitations;

    public InvitationController(InvitationService invitations) {
        this.invitations = invitations;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateInvitationRequest req) {

        try {
            var hostEmail = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
            Invitation inv = invitations.createInvitation(hostEmail, req.getDebateSlug(), req.getRoleGranted(), req.getMaxUses(), req.getTtlDays());
            return ResponseEntity.ok(new CreateInvitationResponse(
                    true, inv.getCode(), "Created", inv.getExpiresAt(), inv.getMaxUses(), inv.getUses()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new SimpleResponse(false, "Failed to create invitation"));
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<SimpleResponse> accept(@Valid @RequestBody AcceptInvitationRequest req) {
        try {
            var userEmail = org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            boolean ok = invitations.acceptInvitation(req.getCode(), userEmail);
            return ResponseEntity.ok(new SimpleResponse(ok, ok ? "Joined" : "Already joined"));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(404).body(new SimpleResponse(false, "Invalid code"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(new SimpleResponse(false, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new SimpleResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new SimpleResponse(false, "Failed to accept invitation"));
        }
    }
}
