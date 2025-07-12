package me.jaesung.simplepg.common.event.handler;

import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.event.CredentialUpdateEvent;
import me.jaesung.simplepg.service.cache.ApiCredentialCacheService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component @Slf4j
public class CacheEventHandler {

    private final ApiCredentialCacheService apiCredentialCacheService;

    public CacheEventHandler(ApiCredentialCacheService apiCredentialCacheService) {
        this.apiCredentialCacheService = apiCredentialCacheService;
    }

    @EventListener
    public void handleCacheUpdate(CredentialUpdateEvent event){
        apiCredentialCacheService.updateCache(event.getClientId());
    }
}
