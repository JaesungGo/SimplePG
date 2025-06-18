package me.jaesung.simplepg.domain.dto.webhook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@AllArgsConstructor @NoArgsConstructor
public class WebhookResponse {
    private String transactionId;
    private String paymentStatus;
    private String createdAt;
}
