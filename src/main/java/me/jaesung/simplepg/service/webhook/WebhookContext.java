package me.jaesung.simplepg.service.webhook;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WebhookContext {

    private final Map<String, WebhookService> webhookStrategies;

    public WebhookContext(List<WebhookService> webhookServiceList) {
        this.webhookStrategies = webhookServiceList.stream()
                .collect(Collectors.toMap(
                        WebhookService::getStrategyName,
                        Function.identity()
                        )
                );
    }

    /**
     * 웹훅 상태에 따라 적절한 서비스를 반환
     */
    public WebhookService getWebhookService(String webhookStatus) {
        WebhookService strategy = webhookStrategies.get(webhookStatus);
        if( strategy == null ){
            throw new IllegalArgumentException("현재 웹훅 상태를 지원하지 않습니다" + webhookStatus);
        }
        return strategy;
    }
}