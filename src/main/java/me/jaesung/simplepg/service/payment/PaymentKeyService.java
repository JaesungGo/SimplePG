package me.jaesung.simplepg.service.payment;

import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.exception.ErrorCode;
import me.jaesung.simplepg.common.exception.PaymentException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class PaymentKeyService {

    /**
     * paymentKey 생성 (timeStamp + randomSalt + data)
     * SHA256 기반
     *
     * @param orderNo
     * @return paymentKey
     */
    public String createPaymentKey(String orderNo) {
        int countRetries = 3;
        for (int i = 0; i < countRetries; i++) {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String randomSalt = UUID.randomUUID().toString();
            String data = orderNo + ":" + timeStamp + ":" + randomSalt;

            String paymentKey = DigestUtils.sha256Hex(data);
            if (isValidPaymentKey(paymentKey)) {
                return paymentKey;
            }
        }
        log.error("Failed to generate PaymentKey:{}", countRetries);
        throw new PaymentException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to generate PaymentKey");
    }

    /**
     * 보안 강화를 위한 payment_key 검증 로직
     */
    private boolean isValidPaymentKey(String paymentKey) {
        // 길이 검증 (SHA-256)
        if (paymentKey.length() != 64) return false;

        // 16진수 문자열 검증
        return paymentKey.matches("^[a-fA-F0-9]{64}$");
    }

}
