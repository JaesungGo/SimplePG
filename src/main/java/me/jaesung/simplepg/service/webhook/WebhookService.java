package me.jaesung.simplepg.service.webhook;

import me.jaesung.simplepg.domain.dto.webhook.WebhookResponse;

public interface WebhookService {
    void webhookProcess(WebhookResponse webhookRequest, String paymentKey);
}
