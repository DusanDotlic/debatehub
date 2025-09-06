package com.debatehub.backend.service;

import com.debatehub.backend.domain.*;
import com.debatehub.backend.domain.ids.DebateParticipantId;
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

    public List<Debate> listStartedBy(String email) {
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
        if (pinRepo.existsById(id)) return true; // idempotent
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

}
