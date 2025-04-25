package me.jaesung.simplepg.domain.vo;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiCredential {
    private String clientId;
    private String clientSecret;
    private String name;
    private ApiStatus status;
    private LocalDateTime createdAt;
}
