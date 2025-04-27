package me.jaesung.simplepg.domain.dto.api;

import lombok.Builder;
import lombok.Data;
import me.jaesung.simplepg.domain.vo.api.ApiCredential;
import me.jaesung.simplepg.domain.vo.api.ApiStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiCredentialResponse {

    private String clientId;
    private String clientSecret;
    private String name;
    private ApiStatus status;
    private LocalDateTime createdAt;


    public static ApiCredentialResponse of(ApiCredential credential) {
        return ApiCredentialResponse.builder()
                .clientId(credential.getClientId())
                .clientSecret(credential.getClientSecret())
                .name(credential.getName())
                .status(credential.getStatus())
                .createdAt(credential.getCreatedAt())
                .build();
    }

}
