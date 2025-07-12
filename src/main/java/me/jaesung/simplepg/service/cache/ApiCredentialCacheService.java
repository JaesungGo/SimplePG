package me.jaesung.simplepg.service.cache;

import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.exception.ApiException;
import me.jaesung.simplepg.domain.dto.cache.ApiCredentialCache;
import me.jaesung.simplepg.domain.vo.api.ApiCredential;
import me.jaesung.simplepg.mapper.ApiCredentialMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ApiCredentialCacheService {

    private final ApiCredentialMapper apiCredentialMapper;

    public ApiCredentialCacheService(ApiCredentialMapper apiCredentialMapper) {
        this.apiCredentialMapper = apiCredentialMapper;
    }

    @Cacheable(value = "apiCredentials", key = "#clientId")
    public ApiCredentialCache findByClientIdWithCache(String clientId) {
        ApiCredential apiCredential = apiCredentialMapper.findByClientId(clientId).orElseThrow(
                () -> new ApiException.NotFoundException("api_credentials is not found")
        );
        return ApiCredentialCache.of(apiCredential);
    }

    @CacheEvict(value = "apiCredentials", key = "#clientId")
    public void removeApiCredentialsCache(String clientId) {
        log.info("apiCredentials 캐시 제거 : {} ", clientId);
    }

    @CacheEvict(value = "apiCredentials", allEntries = true)
    public void removeAllCache() {
        log.info("모든 apiCredentials 캐시 제거");
    }

    @CachePut(value = "apiCredentials", key = "#clientId")
    public ApiCredentialCache updateCache(String clientId) {
        log.info("즉시 캐시 생성 : {} ", clientId);

        ApiCredential apiCredential = apiCredentialMapper.findByClientId(clientId)
                .orElseThrow(() -> new ApiException.NotFoundException("api_credentials is not found"));

        return ApiCredentialCache.of(apiCredential);
    }


}
