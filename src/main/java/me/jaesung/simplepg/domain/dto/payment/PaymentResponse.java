package me.jaesung.simplepg.domain.dto.payment;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PaymentResponse {

    private String paymentId;
    private String status;
    private String createdAt;

    public static PaymentResponse of(PaymentDTO paymentDTO) {
        return PaymentResponse.builder()
                .paymentId(paymentDTO.getPaymentId())
                .status(paymentDTO.getStatus().toString())
                .createdAt(paymentDTO.getCreatedAt().toString())
                .build();
    }
}
