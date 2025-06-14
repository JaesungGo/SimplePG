package me.jaesung.simplepg.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.jaesung.simplepg.domain.dto.webhook.MerchantRequest;

@Getter
@AllArgsConstructor
public class PaymentProcessedEvent {

    private final MerchantRequest merchantRequest;

}
