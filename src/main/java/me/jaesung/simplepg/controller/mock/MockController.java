package me.jaesung.simplepg.controller.mock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.domain.dto.api.ApiCredentialResponse;
import me.jaesung.simplepg.domain.dto.webhook.ExternalApiDTO;
import me.jaesung.simplepg.domain.dto.webhook.WebhookRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mock")
public class MockController {

    private final WebClient webClient;

    @PostMapping("/request")
    public ResponseEntity<String> handleMockData(@RequestBody ExternalApiDTO externalApiDTO) {

        String transactionId = UUID.randomUUID().toString();
        String paymentKey = externalApiDTO.getPaymentKey();

        WebhookRequest webhookRequest = WebhookRequest.builder()
                .transactionId(transactionId)
                .paymentStatus("APPROVED")
                .approvedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .build();

        new Thread(() -> {
            try {
                Thread.sleep(1000);

                webClient.post()
                        .uri("http://localhost:8080/api/protected/webhook/" + paymentKey + "/success")
                        .bodyValue(webhookRequest)
                        .retrieve()
                        .bodyToMono(String.class)
                        .subscribe(
                                response -> log.info("웹훅 전송 성공: {}", response),
                                error -> log.error("웹훅 전송 실패: {}", error.getMessage())
                        );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("웹훅 전송 중 인터럽트 발생", e);
            }
        }).start();

        log.info("Mock 서버가 결제 요청 접수 완료. 트랜잭션 ID: {}", transactionId);
        return ResponseEntity.ok("Payment request received successfully. Transaction ID: " + transactionId);
    }

    @PostMapping("/callback1")
    public ResponseEntity<ApiCredentialResponse> handleWebhookCallback(
            @RequestHeader("X-CLIENT-ID") String clientId,
            @RequestHeader("X-TIMESTAMP") String timestamp,
            @RequestHeader("X-SIGNATURE") String signature,
            @RequestBody ApiCredentialResponse request) {

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request);
    }
}
