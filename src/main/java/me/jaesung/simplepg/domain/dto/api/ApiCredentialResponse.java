package me.jaesung.simplepg.domain.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.jaesung.simplepg.domain.vo.api.ApiCredential;
import me.jaesung.simplepg.domain.vo.api.ApiStatus;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ApiCredentialResponse {

    private String clientId;
    private String clientSecret;
    private String clientName;
    private ApiStatus status;
    private LocalDateTime createdAt;


    public static ApiCredentialResponse of(ApiCredential credential) {
        return ApiCredentialResponse.builder()
                .clientId(credential.getClientId())
                .clientSecret(credential.getClientSecret())
                .clientName(credential.getClientName())
                .status(credential.getStatus())
                .createdAt(credential.getCreatedAt())
                .build();
    }

}
