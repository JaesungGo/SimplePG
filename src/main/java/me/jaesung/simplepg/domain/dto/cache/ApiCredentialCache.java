package me.jaesung.simplepg.domain.dto.cache;

import lombok.Builder;
import lombok.Getter;
import me.jaesung.simplepg.domain.vo.api.ApiCredential;
import me.jaesung.simplepg.domain.vo.api.ApiStatus;

@Getter
@Builder
public class ApiCredentialCache {

    private String clientId;
    private String clientSecret;
    private ApiStatus apiStatus;

    public static ApiCredentialCache of(ApiCredential apiCredential) {
        return ApiCredentialCache.builder()
                .clientId(apiCredential.getClientId())
                .clientSecret(apiCredential.getClientSecret())
                .apiStatus(apiCredential.getStatus())
                .build();
    }
}
