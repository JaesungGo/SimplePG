package me.jaesung.simplepg.domain.dto.payment;

import lombok.Builder;
import lombok.Data;
import me.jaesung.simplepg.domain.vo.payment.MethodCode;
import me.jaesung.simplepg.domain.vo.payment.Payment;
import me.jaesung.simplepg.domain.vo.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO <-> VO 변환용
 */
@Data @Builder
public class PaymentDTO {
    private String paymentId;
    private String clientId;
    private String paymentKey;
    private String orderNo;
    private BigDecimal amount;
    private PaymentStatus status;
    private MethodCode methodCode;
    private String productName;
    private String customerName;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
}
