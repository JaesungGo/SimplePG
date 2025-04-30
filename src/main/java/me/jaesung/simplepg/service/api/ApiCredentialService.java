package me.jaesung.simplepg.service.api;

import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.exception.ApiException;
import me.jaesung.simplepg.domain.dto.api.ApiCredentialRequest;
import me.jaesung.simplepg.domain.dto.api.ApiCredentialResponse;
import me.jaesung.simplepg.domain.vo.api.ApiCredential;
import me.jaesung.simplepg.domain.vo.api.ApiStatus;
import me.jaesung.simplepg.mapper.ApiCredentialMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApiCredentialService {
    private final ApiCredentialMapper apiCredentialMapper;

    public ApiCredentialService(ApiCredentialMapper apiCredentialMapper) {
        this.apiCredentialMapper = apiCredentialMapper;
    }

    public boolean isValidApiCredential(String clientId) {
        ApiCredentialResponse keyByClientId = findByClientId(clientId);
        return keyByClientId != null && keyByClientId.getStatus().equals(ApiStatus.ACTIVE);
    }

    public ApiCredentialResponse findByClientId(String clientId) {
        ApiCredential keyByClientId = apiCredentialMapper.findByClientId(clientId)
                .orElseThrow(() -> new ApiException.NotFoundException("clientID is not Found"));
        return ApiCredentialResponse.of(keyByClientId);
    }

}
