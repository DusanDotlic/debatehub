package com.debatehub.backend.web.dto;

import java.time.OffsetDateTime;

public class CreateInvitationResponse {
    private boolean success;
    private String code;
    private String message;
    private OffsetDateTime expiresAt;
    private int maxUses;
    private int uses;

    public CreateInvitationResponse() {}

    public CreateInvitationResponse(boolean success, String code, String message,
                                    OffsetDateTime expiresAt, int maxUses, int uses) {
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
    public int getMaxUses() { return maxUses; }
    public void setMaxUses(int maxUses) { this.maxUses = maxUses; }
    public int getUses() { return uses; }
    public void setUses(int uses) { this.uses = uses; }
}
