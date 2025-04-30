package me.jaesung.simplepg.service.webhook;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebhookServiceFactory {

    private final WebhookService webhookSuccessService;
    private final WebhookService webhookFailedService;

    /**
     * 웹훅 상태에 따라 적절한 서비스를 반환
     */
    public WebhookService getWebhookService(String webhookStatus) {
        if ("failure".equals(webhookStatus)) {
            return webhookFailedService;
        } else {
            return webhookSuccessService;
        }
    }
}