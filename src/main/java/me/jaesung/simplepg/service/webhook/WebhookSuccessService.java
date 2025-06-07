package me.jaesung.simplepg.service.webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.exception.PaymentException;
import me.jaesung.simplepg.common.util.DateTimeUtil;
import me.jaesung.simplepg.domain.dto.payment.PaymentDTO;
import me.jaesung.simplepg.domain.dto.payment.PaymentLogDTO;
import me.jaesung.simplepg.domain.dto.webhook.WebhookRequest;
import me.jaesung.simplepg.domain.dto.webhook.WebhookResponse;
import me.jaesung.simplepg.domain.vo.payment.PaymentLogAction;
import me.jaesung.simplepg.domain.vo.payment.PaymentStatus;
import me.jaesung.simplepg.mapper.PaymentLogMapper;
import me.jaesung.simplepg.mapper.PaymentMapper;
import me.jaesung.simplepg.service.webclient.WebClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Slf4j
public class WebhookSuccessService extends WebhookServiceImpl {

    public WebhookSuccessService(PaymentMapper paymentMapper, PaymentLogMapper paymentLogMapper, WebClientService webClientService) {
        super(paymentMapper, paymentLogMapper, webClientService);
    }

    @Override
    protected void validatePayment(PaymentDTO paymentDTO, WebhookRequest webhookRequest) {
        if (!webhookRequest.getPaymentStatus().equals(PaymentStatus.APPROVED.name())) {
            throw new PaymentException.WebhookProcessingException("외부 Status 정보가 서버의 정보와 다릅니다");
        }
    }

    @Override
    protected void processPaymentStatus(PaymentDTO paymentDTO, WebhookRequest webhookRequest) {
        paymentDTO.setStatus(PaymentStatus.APPROVED);
        paymentDTO.setApprovedAt(LocalDateTime.parse(webhookRequest.getApprovedAt()));
        paymentDTO.setTransactionId(webhookRequest.getTransactionId());

        PaymentLogDTO paymentLogDTO = PaymentLogDTO.builder()
                .paymentId(paymentDTO.getPaymentId())
                .action(PaymentLogAction.APPROVE)
                .status(PaymentStatus.APPROVED)
                .details("승인된 결제 내역입니다(승인 일시:" + LocalDateTime.now()+ ")")
                .build();

        paymentMapper.updatePayment(paymentDTO);
        paymentLogMapper.insertPaymentLog(paymentLogDTO);
    }
}