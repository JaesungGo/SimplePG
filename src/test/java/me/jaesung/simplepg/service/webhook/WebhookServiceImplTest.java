//package me.jaesung.simplepg.service.webhook;
//
//import me.jaesung.simplepg.common.exception.PaymentException;
//import me.jaesung.simplepg.domain.dto.payment.PaymentDTO;
//import me.jaesung.simplepg.domain.dto.payment.PaymentLogDTO;
//import me.jaesung.simplepg.domain.dto.webhook.WebhookResponse;
//import me.jaesung.simplepg.domain.vo.payment.*;
//import me.jaesung.simplepg.mapper.PaymentLogMapper;
//import me.jaesung.simplepg.mapper.PaymentMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.ApplicationEventPublisher;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class WebhookServiceImplTest {
//
//    @Mock
//    private PaymentMapper paymentMapper;
//
//    @Mock
//    private PaymentLogMapper paymentLogMapper;
//
//    @Mock
//    private ApplicationEventPublisher applicationEventPublisher;
//
//    @InjectMocks
//    private TestWebhookServiceImpl testWebhookService;
//
//    @Test
//    @DisplayName("이벤트 발행 테스트")
//    void webhookProcess() {
//        //given
//        String paymentKey = "test-payment-key";
//        PaymentDTO mockPaymentDTO = createMockPaymentDTO(paymentKey);
//        WebhookResponse mockWebhookResponse = createMockWebhookResponse();
//
//        when(paymentMapper.findByPaymentKeyWithLock(paymentKey))
//                .thenReturn(Optional.ofNullable(mockPaymentDTO));
//
//        //when
//        testWebhookService.webhookProcess(mockWebhookResponse, paymentKey);
//
//        ///then
//
//
//    }
//
//
//    private static class TestWebhookServiceImpl extends WebhookServiceImpl {
//
//        public TestWebhookServiceImpl(PaymentMapper paymentMapper, PaymentLogMapper paymentLogMapper, ApplicationEventPublisher eventPublisher) {
//            super(paymentMapper, paymentLogMapper, eventPublisher);
//        }
//
//        @Override
//        protected void validatePayment(PaymentDTO paymentDTO, WebhookResponse webhookResponse) {
//            if (!webhookResponse.getPaymentStatus().equals(PaymentStatus.APPROVED.name())) {
//                throw new PaymentException.WebhookProcessingException("외부 Status 정보가 서버의 정보와 다릅니다");
//            }
//        }
//
//        @Override
//        protected void processPaymentStatus(PaymentDTO paymentDTO, WebhookResponse webhookResponse) {
//            paymentDTO.setStatus(PaymentStatus.APPROVED);
//            paymentDTO.setApprovedAt(LocalDateTime.parse(webhookResponse.getCreatedAt()));
//            paymentDTO.setTransactionId(webhookResponse.getTransactionId());
//
//            PaymentLogDTO paymentLogDTO = PaymentLogDTO.builder()
//                    .paymentId(paymentDTO.getPaymentId())
//                    .action(PaymentLogAction.APPROVE)
//                    .status(PaymentStatus.APPROVED)
//                    .details("승인된 결제 내역입니다(승인 일시:" + LocalDateTime.now() + ")")
//                    .build();
//
//            paymentMapper.updatePayment(paymentDTO);
//            paymentLogMapper.insertPaymentLog(paymentLogDTO);
//        }
//    }
//
//    private PaymentDTO createMockPaymentDTO(String paymentKey) {
//        return PaymentDTO.builder()
//                .paymentKey(paymentKey)
//                .clientId("test-client")
//                .orderNo("test-order")
//                .transactionId("test-TID")
//                .amount(BigDecimal.valueOf(10000))
//                .customerName("testUser")
//                .status(PaymentStatus.READY)
//                .productName("test-product")
//                .methodCode(MethodCode.BANK)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//    }
//
//    private WebhookResponse createMockWebhookResponse() {
//        return WebhookResponse.builder()
//                .paymentStatus(String.valueOf(PaymentStatus.APPROVED))
//                .transactionId("test-TID")
//                .createdAt(LocalDateTime.now().toString())
//                .build();
//    }
//}