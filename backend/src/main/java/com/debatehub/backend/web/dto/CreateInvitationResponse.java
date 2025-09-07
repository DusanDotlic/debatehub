package com.debatehub.backend.web.dto;

import java.time.OffsetDateTime;

public class CreateInvitationResponse {
    private boolean success;
    private String code;
    private String message;            // <-- add message
    private OffsetDateTime expiresAt;  // <-- use OffsetDateTime to match controller
    private Integer maxUses;
    private Integer uses;

    public CreateInvitationResponse() {}

    // Match controller signature: (boolean, String, String, OffsetDateTime, int, int)
    public CreateInvitationResponse(boolean success,
                                    String code,
                                    String message,
                                    OffsetDateTime expiresAt,
                                    int maxUses,
                                    int uses) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.expiresAt = expiresAt;
        this.maxUses = maxUses;
        this.uses = uses;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Integer getMaxUses() { return maxUses; }
    public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }

    public Integer getUses() { return uses; }
    public void setUses(Integer uses) { this.uses = uses; }
}
