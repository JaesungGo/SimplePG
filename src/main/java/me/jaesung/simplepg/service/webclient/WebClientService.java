package me.jaesung.simplepg.service.webclient;

import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.exception.ApiException;
import me.jaesung.simplepg.common.exception.PaymentException;
import me.jaesung.simplepg.common.util.HmacUtil;
import me.jaesung.simplepg.domain.dto.api.ApiCredentialResponse;
import me.jaesung.simplepg.domain.dto.payment.PaymentDTO;
import me.jaesung.simplepg.domain.dto.webhook.ExternalApiDTO;
import me.jaesung.simplepg.domain.dto.webhook.WebhookResponse;
import me.jaesung.simplepg.domain.vo.api.ApiCredential;
import me.jaesung.simplepg.mapper.ApiCredentialMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
@Slf4j
public class WebClientService {

    private final String requestApiUrl;
    private final String returnUrl;
    private final WebClient webClient;
    private final ApiCredentialMapper apiCredentialMapper;

    public WebClientService(@Value("${api.request.url}") String requestApiUrl,
                            @Value("${api.return.url}") String returnUrl,
                            WebClient webClient, ApiCredentialMapper apiCredentialMapper) {
        this.requestApiUrl = requestApiUrl;
        this.returnUrl = returnUrl;
        this.webClient = webClient;
        this.apiCredentialMapper = apiCredentialMapper;
    }

    public void sendRequest(PaymentDTO paymentDTO) {

        ExternalApiDTO externalApiDTO = ExternalApiDTO.builder()
                .paymentKey(paymentDTO.getPaymentKey())
                .amount(paymentDTO.getAmount().toString())
                .orderNo(paymentDTO.getOrderNo())
                .customerName(paymentDTO.getCustomerName())
                .methodCode(paymentDTO.getMethodCode().toString())
                .successUrl(returnUrl + "/" + paymentDTO.getPaymentKey() + "/success")
                .failureUrl(returnUrl + "/" + paymentDTO.getPaymentKey() + "/failure")
                .build();

        webClient.post()
                .uri(requestApiUrl)
                .bodyValue(externalApiDTO)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        null,
                        error -> {
                            throw new PaymentException.ExternalPaymentException("외부 결제 서버로 요청 실패");
                        }
                );
    }

    public void sendResponse(WebhookResponse webhookResponse) throws Exception {

        String data = plusBody(webhookResponse);
        String clientId = webhookResponse.getClientId();

        ApiCredential apiCredential = apiCredentialMapper.findByClientId(clientId)
                .orElseThrow(() -> new ApiException.NotFoundException("해당 clientId가 존재하지 않습니다"));
        String clientSecret = apiCredential.getClientSecret();

        ApiCredentialResponse apiCredentialResponse = ApiCredentialResponse.of(apiCredential);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-CLIENT-ID", clientId);
        httpHeaders.add("X-TIMESTAMP", LocalDateTime.now().toString());
        httpHeaders.add("X-SIGNATURE", HmacUtil.generateSignature(data,clientSecret));

        webClient.post()
                .uri(apiCredential.getReturnUrl())
                .headers(h -> h.addAll(httpHeaders))
                .bodyValue(apiCredentialResponse)
                .retrieve()
                .bodyToMono(ApiCredentialResponse.class)
                .block();

    }

    private String plusBody(WebhookResponse webhookResponse) {
        try {
            if (webhookResponse == null) {
                throw new NullPointerException("webhookResponse가 null입니다");
            }

            StringBuffer sb = new StringBuffer();
            sb.append(webhookResponse.getClientId());
            sb.append(webhookResponse.getPaymentKey());
            sb.append(webhookResponse.getAmount());
            sb.append(webhookResponse.getOrderNo());
            sb.append(webhookResponse.getCustomerName());
            sb.append(webhookResponse.getMethodCode());

            return sb.toString();

        } catch (NullPointerException e) {
            log.error("webhook 수신 후 바디 생성 중 예외 발생 내용:{}", e.getMessage());
            throw new PaymentException.ProcessingException("webhook 수신 후 바디 생성 중 예외 발생");
        }
    }
}
