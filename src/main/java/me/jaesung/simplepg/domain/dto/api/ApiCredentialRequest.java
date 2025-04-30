package me.jaesung.simplepg.domain.dto.api;

import lombok.Data;

@Data
public class ApiCredentialRequest {
    private String clientId;
    private String clientSecret;
    private String active;
    private String clientName;
    private String returnUrl;
}
