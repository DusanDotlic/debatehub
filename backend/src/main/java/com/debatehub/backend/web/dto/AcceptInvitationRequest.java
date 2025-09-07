package com.debatehub.backend.web.dto;

import jakarta.validation.constraints.NotBlank;

public class AcceptInvitationRequest {
    @NotBlank
    private String code;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
