package me.jaesung.simplepg.controller.webhook;

import me.jaesung.simplepg.domain.dto.webhook.WebhookResponse;
import me.jaesung.simplepg.service.webhook.WebhookService;
import me.jaesung.simplepg.service.webhook.WebhookContext;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/protected/webhook")
public class WebhookController {

    private final WebhookContext webhookContext;

    public WebhookController(WebhookContext webhookContext) {
        this.webhookContext = webhookContext;
    }

    @PostMapping("/{paymentKey}/{webhookStatus}")
    public void webhookProcess(
            @PathVariable String paymentKey,
            @PathVariable String webhookStatus,
            @RequestBody WebhookResponse webhookResponse) {

        WebhookService webhookService = webhookContext.getWebhookService(webhookStatus);

        webhookService.webhookProcess(webhookResponse, paymentKey);

    }

}
