package me.jaesung.simplepg.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterAccess(Duration.ofMinutes(10))
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .removalListener(((key, value, cause) -> {
                            log.info("Caffeine 캐시 제거 key: {}, value: {}, cause: {}", key, value, cause);
                        }))
        );
        return cacheManager;
    }
}
