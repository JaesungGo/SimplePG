package me.jaesung.simplepg.service.webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.exception.PaymentException;
import me.jaesung.simplepg.domain.dto.payment.PaymentDTO;
import me.jaesung.simplepg.domain.dto.webhook.WebhookRequest;
import me.jaesung.simplepg.domain.dto.webhook.WebhookResponse;
import me.jaesung.simplepg.domain.vo.payment.PaymentStatus;
import me.jaesung.simplepg.mapper.PaymentLogMapper;
import me.jaesung.simplepg.mapper.PaymentMapper;
import me.jaesung.simplepg.service.webclient.WebClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public abstract class WebhookServiceImpl implements WebhookService {

    protected final PaymentMapper paymentMapper;
    protected final PaymentLogMapper paymentLogMapper;
    protected final WebClientService webClientService;

    @Override
    @Transactional
    public void webhookProcess(WebhookRequest webhookRequest, String paymentKey) {
        try {

            PaymentDTO paymentDTO = paymentMapper.findByPaymentKeyWithLock(paymentKey)
                    .orElseThrow(() -> new PaymentException.InvalidPaymentRequestException("결제 정보를 찾을 수 없습니다"));

            if (paymentDTO.getStatus() == PaymentStatus.READY) {
                throw new PaymentException.InvalidPaymentRequestException("이미 처리된 결제 내역 입니다");
            }

            validatePayment(paymentDTO, webhookRequest);

            processPaymentStatus(paymentDTO, webhookRequest);

            WebhookResponse webhookResponse = WebhookResponse.builder()
                    .clientId(paymentDTO.getClientId())
                    .paymentKey(paymentKey)
                    .amount(paymentDTO.getAmount().toString())
                    .orderNo(paymentDTO.getOrderNo())
                    .customerName(paymentDTO.getCustomerName())
                    .methodCode(paymentDTO.getMethodCode().toString())
                    .build();

            webClientService.sendResponse(webhookResponse);

        } catch (Exception e) {
            log.error("웹훅 처리 실패: paymentKey={}, 원인={}", paymentKey, e.getMessage(), e);
            throw new PaymentException.WebhookProcessingException("웹훅 수신 데이터 처리 실패");
        }
    }

    /**
     * 결제 정보와 웹훅 요청 검증
     */
    protected abstract void validatePayment(PaymentDTO paymentDTO, WebhookRequest webhookRequest);

    /**
     * 결제 상태 처리
     */
    protected abstract void processPaymentStatus(PaymentDTO paymentDTO, WebhookRequest webhookRequest);
}
