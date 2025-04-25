package me.jaesung.simplepg.domain.vo;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Payment {
    private String paymentId;
    private String paymentKey;
    private String orderNo;
    private Long amount;
    private PaymentStatus status;
    private String productName;
    private String customerName;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

}
