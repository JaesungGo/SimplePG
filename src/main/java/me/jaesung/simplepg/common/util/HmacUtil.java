package me.jaesung.simplepg.common.util;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Component
public class HmacUtil {

    private static final String ALGORITHM = "HmacSHA256";

    public static String generateSignature(String data, String secretKey) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(secretKeySpec);
        byte[] hmacData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacData);
    }

    public static boolean isTimestampValid(String timestamp, int toleranceMinutes) {
        try {
            long timeStampMillis = Long.parseLong(timestamp);
            long currentTimeMillis = System.currentTimeMillis();

            long minDiffs = Math.abs(timeStampMillis - currentTimeMillis) / (60 * 1000);
            return minDiffs <= toleranceMinutes;

        } catch (NumberFormatException e) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            ZonedDateTime requestTime = ZonedDateTime.parse(timestamp, formatter);
            ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);

            long minutesDiff = Math.abs(ChronoUnit.MINUTES.between(requestTime, currentTime));
            return minutesDiff <= toleranceMinutes;
        }
    }
}