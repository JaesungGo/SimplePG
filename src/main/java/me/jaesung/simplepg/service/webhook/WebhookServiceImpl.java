package me.jaesung.simplepg.service.webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.event.PaymentProcessedEvent;
import me.jaesung.simplepg.common.exception.PaymentException;
import me.jaesung.simplepg.domain.dto.payment.PaymentDTO;
import me.jaesung.simplepg.domain.dto.webhook.WebhookResponse;
import me.jaesung.simplepg.domain.dto.webhook.MerchantRequest;
import me.jaesung.simplepg.domain.vo.payment.PaymentStatus;
import me.jaesung.simplepg.mapper.PaymentLogMapper;
import me.jaesung.simplepg.mapper.PaymentMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public abstract class WebhookServiceImpl implements WebhookService {

    protected final PaymentMapper paymentMapper;
    protected final PaymentLogMapper paymentLogMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 1.상점 서버로 보낼 결제 및 로그 상태 처리 및 저장
     * 2.가맹점 서버로 보낼 DTO 와 함께 이벤트 발생 (비동기 요청)
     *
     * @param webhookResponse (결제 서버로 부터 웹훅으로 받은 데이터)
     * @param paymentKey      (결제 고유 키 -> URL 파라미터로 받음)
     */
    @Transactional
    @Override
    public void webhookProcess(WebhookResponse webhookResponse, String paymentKey) {
        MerchantRequest merchantRequest = paymentTransaction(webhookResponse, paymentKey);
        eventPublisher.publishEvent(new PaymentProcessedEvent(merchantRequest));
    }

    /**
     * 상점 서버로 보낼 결제 및 로그 상태 처리 및 저장
     * @param webhookResponse (결제 서버로 부터 웹훅으로 받은 데이터)
     * @param paymentKey      (결제 고유 키 -> URL 파라미터로 받음)
     * @return MerchantRequest (상점 서버로 보낼 데이터 )
     */
    private MerchantRequest paymentTransaction(WebhookResponse webhookResponse, String paymentKey) {
        PaymentDTO paymentDTO = paymentMapper.findByPaymentKeyWithLock(paymentKey)
                .orElseThrow(() -> new PaymentException.InvalidPaymentRequestException("결제 정보를 찾을 수 없습니다"));

        if (paymentDTO.getStatus() != PaymentStatus.READY) {
            throw new PaymentException.InvalidPaymentRequestException("이미 처리된 결제 내역 입니다");
        }

        processPaymentStatus(paymentDTO, webhookResponse);
        validatePayment(paymentDTO, webhookResponse);

        return MerchantRequest.builder()
                .clientId(paymentDTO.getClientId())
                .paymentKey(paymentKey)
                .amount(paymentDTO.getAmount().toString())
                .orderNo(paymentDTO.getOrderNo())
                .customerName(paymentDTO.getCustomerName())
                .methodCode(paymentDTO.getMethodCode().toString())
                .build();
    }

    /**
     * 결제 정보와 웹훅 요청 검증 (Success/Failed에 따라 분기)
     */
    protected abstract void validatePayment(PaymentDTO paymentDTO, WebhookResponse webhookResponse);

    /**
     * 결제 상태 처리 (Success/Failed에 따라 분기)
     */
    protected abstract void processPaymentStatus(PaymentDTO paymentDTO, WebhookResponse webhookResponse);
}