package com.debatehub.backend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class CreateInvitationRequest {

    @NotBlank
    private String debateSlug;

    // debater | moderator
    @NotBlank
    @Pattern(regexp = "^(debater|moderator)$")
    private String roleGranted;

    @Positive
    private Integer maxUses = 1;

    // days from now; optional (default 7)
    @Positive
    private Integer ttlDays = 7;

    public String getDebateSlug() { return debateSlug; }
    public void setDebateSlug(String debateSlug) { this.debateSlug = debateSlug; }

    public String getRoleGranted() { return roleGranted; }
    public void setRoleGranted(String roleGranted) { this.roleGranted = roleGranted; }

    public Integer getMaxUses() { return maxUses; }
    public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }

    public Integer getTtlDays() { return ttlDays; }
    public void setTtlDays(Integer ttlDays) { this.ttlDays = ttlDays; }
}
