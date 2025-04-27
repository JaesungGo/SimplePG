package me.jaesung.simplepg.domain.dto.payment;

import lombok.Builder;
import lombok.Data;
import me.jaesung.simplepg.domain.vo.payment.PaymentLogAction;
import me.jaesung.simplepg.domain.vo.payment.PaymentStatus;

import java.time.LocalDateTime;

@Data @Builder
public class PaymentLogDTO {
    private Long lodId;
    private String paymentId;
    private PaymentLogAction action;
    private PaymentStatus status;
    private String details;
    private LocalDateTime createdAt;
}
