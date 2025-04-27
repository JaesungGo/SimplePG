package me.jaesung.simplepg.domain.vo.payment;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentLog {
    private Long lodId;
    private String paymentId;
    private PaymentLogAction action;
    private PaymentStatus status;
    private String details;
    private LocalDateTime createdAt;
}
