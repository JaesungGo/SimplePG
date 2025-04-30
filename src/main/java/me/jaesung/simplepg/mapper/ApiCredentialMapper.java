package me.jaesung.simplepg.mapper;

import me.jaesung.simplepg.domain.dto.api.ApiCredentialRequest;
import me.jaesung.simplepg.domain.vo.api.ApiCredential;

import java.util.Optional;

public interface ApiCredentialMapper {
    Optional<ApiCredential> findByClientId(String clientId);

    void insertApiCredential(ApiCredentialRequest request);
}
