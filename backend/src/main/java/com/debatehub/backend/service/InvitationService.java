package com.debatehub.backend.service;

import com.debatehub.backend.domain.*;
import com.debatehub.backend.domain.ids.DebateParticipantId;
import com.debatehub.backend.repo.*;
import com.debatehub.backend.util.Slugger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;

@Service
public class InvitationService {

    private final UserRepository userRepo;
    private final DebateRepository debateRepo;
    private final InvitationRepository invitationRepo;
    private final DebateParticipantRepository participantRepo;

    public InvitationService(UserRepository userRepo, DebateRepository debateRepo,
                             InvitationRepository invitationRepo, DebateParticipantRepository participantRepo) {
        this.userRepo = userRepo;
        this.debateRepo = debateRepo;
        this.invitationRepo = invitationRepo;
        this.participantRepo = participantRepo;
    }

    @Transactional
    public Invitation createInvitation(String hostEmail, String debateSlug, String roleGranted, int maxUses, int ttlDays) {
        var host = userRepo.findByEmailIgnoreCase(hostEmail)
                .orElseThrow(() -> new IllegalArgumentException("Host user not found"));
        var debate = debateRepo.findBySlug(debateSlug)
                .orElseThrow(() -> new IllegalArgumentException("Debate not found"));

        if (debate.getHostUser() == null || !debate.getHostUser().getId().equals(host.getId())) {
            throw new IllegalArgumentException("Only host can create invitations");
        }

        // unique code
        String codeBase = Slugger.randomSuffix(8);
        String code = codeBase;
        int guard = 0;
        while (invitationRepo.findByCode(code).isPresent()) {
            code = Slugger.randomSuffix(10);
            if (++guard > 20) throw new IllegalStateException("Could not allocate unique code");
        }

        Invitation inv = new Invitation();
        inv.setDebate(debate);
        inv.setRoleGranted(roleGranted);
        inv.setCode(code);
        inv.setMaxUses(Math.max(1, maxUses));
        inv.setUses(0);
        inv.setCreatedBy(host);
        inv.setCreatedAt(OffsetDateTime.now());
        inv.setExpiresAt(OffsetDateTime.now().plusDays(Math.max(1, ttlDays)));
        return invitationRepo.save(inv);
    }

    @Transactional
    public boolean acceptInvitation(String code, String userEmail) {
        var inv = invitationRepo.findByCode(code).orElseThrow(() -> new NoSuchElementException("Invalid code"));
        if (inv.getExpiresAt() != null && inv.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new IllegalStateException("Invitation expired");
        }
        if (inv.getUses() >= inv.getMaxUses()) {
            throw new IllegalStateException("Invitation already used");
        }

        var user = userRepo.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var debate = inv.getDebate();
        if (participantRepo.existsByDebateAndUser(debate, user)) {
            return true; // Cant be changed
        }

        DebateParticipant p = new DebateParticipant();
        p.setDebate(debate);
        p.setUser(user);
        p.setRole(inv.getRoleGranted());
        p.setJoinedAt(OffsetDateTime.now());
        p.setId(new com.debatehub.backend.domain.ids.DebateParticipantId(debate.getId(), user.getId()));
        participantRepo.save(p);

        inv.setUses(inv.getUses() + 1);
        inv.setAcceptedBy(user);
        inv.setAcceptedAt(OffsetDateTime.now());
        invitationRepo.save(inv);

        return true;
    }
}
