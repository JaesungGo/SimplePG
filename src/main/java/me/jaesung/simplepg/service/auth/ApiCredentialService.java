package me.jaesung.simplepg.service.auth;

import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.domain.dto.ApiCredentialResponse;
import me.jaesung.simplepg.domain.vo.ApiCredential;
import me.jaesung.simplepg.domain.vo.ApiStatus;
import me.jaesung.simplepg.mapper.ApiCredentialMapper;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class ApiCredentialService {
    private final ApiCredentialMapper apiCredentialMapper;

    public ApiCredentialService(ApiCredentialMapper apiCredentialMapper) {
        this.apiCredentialMapper = apiCredentialMapper;
    }

    public boolean isValidApiCredential(String clientId){
        ApiCredentialResponse keyByClientId = findByClientId(clientId);
        return keyByClientId != null && keyByClientId.getStatus().equals(ApiStatus.ACTIVE);
    }

    public ApiCredentialResponse findByClientId(String clientId){
        ApiCredential keyByClientId = apiCredentialMapper.findByClientId(clientId);
        return ApiCredentialResponse.of(keyByClientId);
    }

}
