package me.jaesung.simplepg.service.webhook;

import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.exception.PaymentException;
import me.jaesung.simplepg.domain.dto.payment.PaymentDTO;
import me.jaesung.simplepg.domain.dto.payment.PaymentLogDTO;
import me.jaesung.simplepg.domain.dto.webhook.WebhookResponse;
import me.jaesung.simplepg.domain.vo.payment.PaymentLogAction;
import me.jaesung.simplepg.domain.vo.payment.PaymentStatus;
import me.jaesung.simplepg.mapper.PaymentLogMapper;
import me.jaesung.simplepg.mapper.PaymentMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j @Transactional
public class WebhookFailedService extends WebhookServiceImpl {

    public WebhookFailedService(PaymentMapper paymentMapper, PaymentLogMapper paymentLogMapper, ApplicationEventPublisher eventPublisher) {
        super(paymentMapper, paymentLogMapper, eventPublisher);
    }

    @Override
    protected void validatePayment(PaymentDTO paymentDTO, WebhookResponse webhookRequest) {
        if (!webhookRequest.getPaymentStatus().equals(PaymentStatus.FAILED.name())) {
            throw new PaymentException.WebhookProcessingException("외부 Status 정보가 서버의 정보와 다릅니다");
        }
    }

    @Override
    protected void processPaymentStatus(PaymentDTO paymentDTO, WebhookResponse webhookRequest) {
        paymentDTO.setStatus(PaymentStatus.FAILED);
        paymentDTO.setTransactionId(webhookRequest.getTransactionId());

        PaymentLogDTO paymentLogDTO = PaymentLogDTO.builder()
                .paymentId(paymentDTO.getPaymentId())
                .action(PaymentLogAction.FAIL)
                .status(PaymentStatus.FAILED)
                .details("실패한 결제 내역입니다(실패 일시:" + LocalDateTime.now())
                .build();

        paymentMapper.updatePayment(paymentDTO);
        paymentLogMapper.insertPaymentLog(paymentLogDTO);
    }

    @Override
    public String getStrategyName() {
        return "failed";
    }
}