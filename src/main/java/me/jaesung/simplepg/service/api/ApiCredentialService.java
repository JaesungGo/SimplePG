package me.jaesung.simplepg.service.api;

import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.event.CredentialUpdateEvent;
import me.jaesung.simplepg.common.exception.ApiException;
import me.jaesung.simplepg.domain.dto.api.ApiCredentialRequest;
import me.jaesung.simplepg.domain.dto.api.ApiCredentialResponse;
import me.jaesung.simplepg.domain.vo.api.ApiCredential;
import me.jaesung.simplepg.mapper.ApiCredentialMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApiCredentialService {
    private final ApiCredentialMapper apiCredentialMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ApiCredentialService(ApiCredentialMapper apiCredentialMapper, ApplicationEventPublisher applicationEventPublisher) {
        this.apiCredentialMapper = apiCredentialMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Update ReturnUrl
     *
     * @param request (ApiCredentialRequest) 1.clientId 2.clientName 3.returnUrl
     */
    public void updateReturnUrl(ApiCredentialRequest request) {
        ApiCredential apiCredential = findByClientId(request.getClientId());

        validateReturnUrl(request, apiCredential);

        ApiCredentialResponse apiCredentialDTO = ApiCredentialResponse.of(apiCredential);
        apiCredentialDTO.setReturnUrl(request.getReturnUrl());

        apiCredentialMapper.updateReturnUrl(apiCredentialDTO);

        updateCredential(request.getClientId());

    }

    private void updateCredential(String clientId){
        applicationEventPublisher.publishEvent(new CredentialUpdateEvent(clientId));
    }


    private void validateReturnUrl(ApiCredentialRequest request, ApiCredential apiCredential) {
        if (apiCredential.getReturnUrl().equals(request.getReturnUrl())) {
            throw new ApiException.InvalidRequestException("duplicate returnUrl!! check your request");
        }
    }

    private ApiCredential findByClientId(String clientId) {
        return apiCredentialMapper.findByClientId(clientId)
                .orElseThrow(() -> new ApiException.NotFoundException("clientKey not Found"));

    }

}
