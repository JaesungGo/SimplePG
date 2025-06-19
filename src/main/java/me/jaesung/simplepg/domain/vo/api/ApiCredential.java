package me.jaesung.simplepg.domain.vo.api;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 상점 <-> SimplePG 통신에서 필요한 상점의 식별자(clientId), MAC 인증에 필요한 secret_key(clientSecret)
 */
@Getter
public class ApiCredential {
    private String clientId;
    private String clientSecret;
    private String clientName;
    private ApiStatus status;
    private String returnUrl;
    private LocalDateTime createdAt;
}
