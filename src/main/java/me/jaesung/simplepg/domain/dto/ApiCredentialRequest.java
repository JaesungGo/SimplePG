package me.jaesung.simplepg.domain.dto;

import lombok.Data;

@Data
public class ApiCredentialRequest {
    private String cliendId;
    private String clientSecret;
    private String active;
}
