package com.debatehub.backend.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AcceptInvitationRequest {

    @NotBlank
    private String code;

    @Email @NotBlank
    private String userEmail;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
