package me.jaesung.simplepg.controller.webhook;

import me.jaesung.simplepg.domain.dto.webhook.WebhookResponse;
import me.jaesung.simplepg.service.webhook.WebhookService;
import me.jaesung.simplepg.service.webhook.WebhookServiceFactory;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/protected/webhook")
public class WebhookController {

    private final WebhookServiceFactory webhookServiceFactory;

    public WebhookController(WebhookServiceFactory webhookServiceFactory) {
        this.webhookServiceFactory = webhookServiceFactory;
    }

    @PostMapping("/{paymentKey}/{webhookStatus}")
    public void webhookProcess(
            @PathVariable String paymentKey,
            @PathVariable String webhookStatus,
            @RequestBody WebhookResponse webhookResponse) {

        WebhookService webhookService = webhookServiceFactory.getWebhookService(webhookStatus);

        webhookService.webhookProcess(webhookResponse, paymentKey);

    }

}
