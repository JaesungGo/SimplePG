package me.jaesung.simplepg.common.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.event.PaymentProcessedEvent;
import me.jaesung.simplepg.domain.dto.webhook.MerchantRequest;
import me.jaesung.simplepg.service.webclient.WebClientService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final WebClientService webClientService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Retryable(value = {Exception.class}, backoff = @Backoff(delay = 3000))
    public void handlePaymentEvent(PaymentProcessedEvent event) {
        try {
            log.info("Payment 정상 커밋 완료 후 비동기 요청 실행: {}", event.getMerchantRequest().getPaymentKey());
            webClientService.sendResponse(event.getMerchantRequest());
        } catch (Exception e) {
            log.error("Payment -> Client 결제 정보 전송 실패 및 재시도 예정: {}", event.getMerchantRequest().getPaymentKey(), e);
        }
    }

    @Recover
    public void retryFailed(Exception e, PaymentProcessedEvent event){
        log.error("Payment -> Client 결제 정보 최종 실패: {}", event.getMerchantRequest().getPaymentKey(), e);

    }
}
