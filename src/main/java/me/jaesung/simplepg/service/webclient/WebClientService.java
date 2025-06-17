package me.jaesung.simplepg.service.webclient;

import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.exception.ApiException;
import me.jaesung.simplepg.common.exception.PaymentException;
import me.jaesung.simplepg.common.util.HmacUtil;
import me.jaesung.simplepg.domain.dto.api.ApiCredentialResponse;
import me.jaesung.simplepg.domain.dto.payment.PaymentDTO;
import me.jaesung.simplepg.domain.dto.webhook.WebhookRequest;
import me.jaesung.simplepg.domain.dto.webhook.MerchantRequest;
import me.jaesung.simplepg.domain.vo.api.ApiCredential;
import me.jaesung.simplepg.mapper.ApiCredentialMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
public class WebClientService {

    private final String requestApiUrl;
    private final String returnUrl;
    private final String cancelUrl;
    private final WebClient webClient;
    private final ApiCredentialMapper apiCredentialMapper;

    public WebClientService(@Value("${api.request.url}") String requestApiUrl,
                            @Value("${api.return.url}") String returnUrl,
                            @Value("${api.request.url}/cancel}") String cancelUrl,
                            WebClient webClient, ApiCredentialMapper apiCredentialMapper) {
        this.requestApiUrl = requestApiUrl;
        this.returnUrl = returnUrl;
        this.cancelUrl = cancelUrl;
        this.webClient = webClient;
        this.apiCredentialMapper = apiCredentialMapper;
    }

    /**
     * 외부 결제 시스템으로 결제 요청
     * 비동기 + 웹훅 (Mono)
     *
     * @param paymentDTO
     */
    public void sendRequest(PaymentDTO paymentDTO) {

        WebhookRequest webhookRequest = WebhookRequest.builder()
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
                .bodyValue(webhookRequest)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        null,
                        error -> {
                            throw new PaymentException.ExternalPaymentException("외부 결제 서버로 요청 실패");
                        }
                );
    }

    /**
     * 외부 결제 시스템으로 결제 취소 요청 전송
     *
     * @param paymentDTO   취소할 결제 정보
     * @param cancelReason 취소 사유(옵션)
     * @throws PaymentException.ExternalPaymentException 외부 API 호출 실패 시
     */
    public void sendCancelRequest(PaymentDTO paymentDTO, String cancelReason) {
        try {
            log.info("결제 취소 요청 전송: paymentKey={}, transactionId={}",
                    paymentDTO.getPaymentKey(), paymentDTO.getTransactionId());

            WebhookRequest cancelRequest = WebhookRequest.builder()
                    .paymentKey(paymentDTO.getPaymentKey())
                    .transactionId(paymentDTO.getTransactionId())
                    .amount(paymentDTO.getAmount().toString())
                    .orderNo(paymentDTO.getOrderNo())
                    .cancelReason(cancelReason != null ? cancelReason : "고객 요청에 의한 취소")
                    .build();

            // 동기 방식으로 요청 처리 - 취소는 즉시 응답이 필요함
            String response = webClient.post()
                    .uri(cancelUrl)
                    .bodyValue(cancelRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("결제 취소 응답: {}", response);
        } catch (Exception e) {
            log.error("결제 취소 요청 실패: {}", e.getMessage(), e);
            throw new PaymentException.ExternalPaymentException("외부 결제 시스템으로 취소 요청 실패: " + e.getMessage());
        }
    }


    /**
     * 가맹점 서버에 웹훅 응답 전송
     *
     * @param merchantRequest
     */
    public void sendResponse(MerchantRequest merchantRequest) {
        try {
            String data = plusBody(merchantRequest);
            String clientId = merchantRequest.getClientId();

            ApiCredential apiCredential = apiCredentialMapper.findByClientId(clientId)
                    .orElseThrow(() -> new ApiException.NotFoundException("해당 clientId가 존재하지 않습니다"));
            String clientSecret = apiCredential.getClientSecret();

            ApiCredentialResponse apiCredentialResponse = ApiCredentialResponse.of(apiCredential);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-CLIENT-ID", clientId);
            httpHeaders.add("X-TIMESTAMP", LocalDateTime.now().toString());
            httpHeaders.add("X-SIGNATURE", HmacUtil.generateSignature(data, clientSecret));

            webClient.post()
                    .uri(apiCredential.getReturnUrl())
                    .headers(h -> h.addAll(httpHeaders))
                    .bodyValue(apiCredentialResponse)
                    .retrieve()
                    .bodyToMono(ApiCredentialResponse.class)
                    .doOnSuccess(response -> {
                        log.info("가맹점 서버 응답 성공: {}", response);
                    })
                    .doOnError(error -> {
                        log.error("가맹점 서버 요청 실패: {}", error.getMessage(), error);
                    })
                    .onErrorResume(error -> Mono.empty())
                    .subscribe();

        } catch (Exception e) {
            log.error("가맹점 서버로의 요청 준비 실패: {}", e.getMessage(), e);
        }
    }

    private String plusBody(MerchantRequest merchantRequest) {
        try {
            if (merchantRequest == null) {
                throw new NullPointerException("webhookResponse가 null입니다");
            }

            StringBuffer sb = new StringBuffer();
            sb.append(merchantRequest.getClientId());
            sb.append(merchantRequest.getPaymentKey());
            sb.append(merchantRequest.getAmount());
            sb.append(merchantRequest.getOrderNo());
            sb.append(merchantRequest.getCustomerName());
            sb.append(merchantRequest.getMethodCode());

            return sb.toString();

        } catch (NullPointerException e) {
            log.error("webhook 수신 후 바디 생성 중 예외 발생 내용:{}", e.getMessage());
            throw new PaymentException.ProcessingException("webhook 수신 후 바디 생성 중 예외 발생");
        }
    }
}
