package me.jaesung.simplepg.domain.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class PaymentInfoDTO {
    String paymentKey;
    String clientId;
    String orderNo;
    String status;
    String amount;
    String methodCode;
    String customerName;
    String createdAt;
    String approvedAt;

    public static PaymentInfoDTO of(PaymentDTO paymentDTO) {
        return PaymentInfoDTO.builder()
                .paymentKey(paymentDTO.getPaymentKey())
                .clientId(paymentDTO.getClientId())
                .orderNo(paymentDTO.getOrderNo())
                .status(paymentDTO.getStatus().toString())
                .amount(paymentDTO.getAmount().toString())
                .methodCode(paymentDTO.getMethodCode().toString())
                .customerName(paymentDTO.getCustomerName())
                .createdAt(paymentDTO.getCreatedAt().toString())
                .approvedAt(paymentDTO.getApprovedAt() != null ? paymentDTO.getApprovedAt().toString() : null)
                .build();
    }
}
