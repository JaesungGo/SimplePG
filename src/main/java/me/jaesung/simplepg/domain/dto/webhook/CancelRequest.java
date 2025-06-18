package me.jaesung.simplepg.domain.dto.webhook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PG -> 외부 결제
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelRequest {

    private String paymentKey;
    private String amount;
    private String orderNo;
    private String customerName;
    private String methodCode;
    private String returnUrl;
    private String transactionId;
    private String cancelReason;

}
