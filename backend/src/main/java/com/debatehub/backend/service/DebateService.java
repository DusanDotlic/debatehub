package com.debatehub.backend.service;

import com.debatehub.backend.domain.*;
import com.debatehub.backend.domain.ids.DebateParticipantId;
import com.debatehub.backend.domain.Debate;
import com.debatehub.backend.domain.ids.UserPinId;
import com.debatehub.backend.repo.*;
import com.debatehub.backend.util.Slugger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DebateService {

    private final UserRepository userRepo;
    private final DebateRepository debateRepo;
    private final DebateParticipantRepository participantRepo;
    private final UserPinRepository pinRepo;

    public DebateService(UserRepository userRepo, DebateRepository debateRepo,
                         DebateParticipantRepository participantRepo, UserPinRepository pinRepo) {
        this.userRepo = userRepo;
        this.debateRepo = debateRepo;
        this.participantRepo = participantRepo;
        this.pinRepo = pinRepo;
    }

    @Transactional
    public Debate createDebate(String hostEmail, String title, String description, boolean inviteOnly) {
        var host = userRepo.findByEmailIgnoreCase(hostEmail)
                .orElseThrow(() -> new IllegalArgumentException("Host user not found: " + hostEmail));

        String base = Slugger.slugify(title);
        String slug = base;
        int guard = 0;
        while (debateRepo.existsBySlug(slug)) {
            slug = base + "-" + Slugger.randomSuffix(6);
            if (++guard > 20) throw new IllegalStateException("Could not allocate unique slug");
        }

        Debate d = new Debate();
        d.setTitle(title);
        d.setSlug(slug);
        d.setHostUser(host);
        d.setDescription(description);
        d.setInviteOnly(inviteOnly);
        d.setStatus("scheduled");
        d.setCreatedAt(OffsetDateTime.now());
        d.setUpdatedAt(OffsetDateTime.now());
        debateRepo.save(d);

        DebateParticipant p = new DebateParticipant();
        p.setDebate(d);
        p.setUser(host);
        p.setRole("host");
        p.setJoinedAt(OffsetDateTime.now());
        p.setId(new DebateParticipantId(d.getId(), host.getId()));
        participantRepo.save(p);

        return d;
    }

    public Optional<Debate> getBySlug(String slug) {
        return debateRepo.findBySlug(slug);
    }

    public List<Debate> listHostedBy(String email) {
        var u = userRepo.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return debateRepo.findByHostUser(u);
    }

    public List<Debate> listJoinedBy(String email) {
        var u = userRepo.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return participantRepo.findByUser(u).stream()
                .map(DebateParticipant::getDebate)
                .filter(d -> d.getHostUser() != null && !d.getHostUser().getId().equals(u.getId())) // exclude those you host
                .collect(Collectors.toCollection(LinkedHashSet::new)) // distinct, keep order
                .stream().toList();
    }

    public List<Debate> listPinned(String email) {
        var u = userRepo.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return pinRepo.findByUser(u).stream().map(UserPin::getDebate).toList();
    }

    @Transactional
    public boolean pin(String email, String slug) {
        var u = userRepo.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        var d = debateRepo.findBySlug(slug).orElseThrow(() -> new NoSuchElementException("Debate not found"));
        var id = new UserPinId(u.getId(), d.getId());
        // Can't be changed: if already pinned, return false to let controller say "Already pinned"
        if (pinRepo.existsById(id)) return false;
        UserPin pin = new UserPin();
        pin.setId(id);
        pin.setUser(u);
        pin.setDebate(d);
        pin.setPinnedAt(OffsetDateTime.now());
        pinRepo.save(pin);
        return true;
    }

    @Transactional
    public boolean unpin(String email, String slug) {
        var u = userRepo.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        var d = debateRepo.findBySlug(slug).orElseThrow(() -> new NoSuchElementException("Debate not found"));
        var id = new UserPinId(u.getId(), d.getId());
        if (pinRepo.existsById(id)) {
            pinRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public long participantCount(Debate debate) {
        return participantRepo.countByDebate(debate);
    }

    public java.util.List<com.debatehub.backend.web.dto.ParticipantDto> participantsForDebate(Debate debate) {
        return participantRepo.findByDebate(debate).stream()
                .map(dp -> new com.debatehub.backend.web.dto.ParticipantDto(
                        dp.getUser() != null ? dp.getUser().getDisplayName() : null,
                        dp.getUser() != null ? dp.getUser().getEmail() : null,
                        dp.getRole()
                ))
                .toList();
    }

    @org.springframework.transaction.annotation.Transactional
    public Debate updateDebate(
            String currentUserEmail,
            String slug,
            String newTitle,
            String newDescription,
            Boolean newInviteOnly
    ) {
        var d = debateRepo.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Debate not found"));

        if (d.getHostUser() == null || d.getHostUser().getEmail() == null
                || !d.getHostUser().getEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new IllegalArgumentException("Only the host can update this debate");
        }

        boolean changed = false;

        if (newTitle != null && !newTitle.isBlank() && !newTitle.equals(d.getTitle())) {
            d.setTitle(newTitle.trim());
            changed = true;
        }
        if (newDescription != null && !java.util.Objects.equals(newDescription, d.getDescription())) {
            d.setDescription(newDescription);
            changed = true;
        }
        if (newInviteOnly != null && d.isInviteOnly() != newInviteOnly.booleanValue()) {
            d.setInviteOnly(newInviteOnly);
            changed = true;
        }

        if (changed) {
            d.setUpdatedAt(java.time.OffsetDateTime.now());
            debateRepo.save(d);
        }
        return d;
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteDebate(
            String currentUserEmail,
            String slug
    ) {
        var d = debateRepo.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Debate not found"));

        if (d.getHostUser() == null || d.getHostUser().getEmail() == null
                || !d.getHostUser().getEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new IllegalArgumentException("Only the host can delete this debate");
        }

        debateRepo.delete(d);
    }

    @Transactional
    public void joinDebate(String currentUserEmail, String slug) {
        var u = userRepo.findByEmailIgnoreCase(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var d = debateRepo.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Debate not found"));

        // Prevent host “joining” again
        if (d.getHostUser() != null && d.getHostUser().getId().equals(u.getId())) return;

        var id = new DebateParticipantId(d.getId(), u.getId());
        if (participantRepo.existsById(id)) return;

        var p = new DebateParticipant();
        p.setId(id);
        p.setDebate(d);
        p.setUser(u);
        p.setRole("participant"); // <- use an allowed role
        p.setJoinedAt(OffsetDateTime.now());
        participantRepo.save(p);
    }



}
