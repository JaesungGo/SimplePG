package me.jaesung.simplepg.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.exception.ApiException;
import me.jaesung.simplepg.mapper.ApiCredentialMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
public class CacheWarmUpService {

    private final ApiCredentialMapper apiCredentialMapper;
    private final ApiCredentialCacheService apiCredentialCacheService;

    @PostConstruct
    public void warmupCacheOnApplicationStart(){
        warmupCredentialsCache();
    }


    public void warmupCredentialsCache(){
        List<String> allClientId = apiCredentialMapper.findAllClientId();

        for (String clientId : allClientId) {
            try{
                apiCredentialCacheService.findByClientIdWithCache(clientId);
            } catch (Exception e){
                log.error("캐시 웜업 실패 : {}", e.getMessage());
            }
        }
        log.info("캐시 웜업 완료 : {} 개 ", allClientId.size());
    }

    public void refreshAllCache(){
        apiCredentialCacheService.removeAllCache();
        warmupCredentialsCache();
    }
}
