package me.jaesung.simplepg.domain.vo.api;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiCredential {
    private String clientId;
    private String clientSecret;
    private String clientName;
    private ApiStatus status;
    private String returnUrl;
    private LocalDateTime createdAt;
}
