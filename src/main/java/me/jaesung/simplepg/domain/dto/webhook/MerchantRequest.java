package me.jaesung.simplepg.domain.dto.webhook;

import lombok.Builder;
import lombok.Data;

/**
 * PG -> 가맹점
 */
@Data
@Builder
public class MerchantRequest {

    private String clientId;
    private String paymentKey;
    private String amount;
    private String orderNo;
    private String customerName;
    private String methodCode;

}
