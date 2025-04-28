package me.jaesung.simplepg.domain.vo.payment;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Payment {
    private String paymentId;
    private String paymentKey;
    private String orderNo;
    private BigDecimal amount;
    private PaymentStatus status;
    private String methodCode;
    private String productName;
    private String customerName;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

}
