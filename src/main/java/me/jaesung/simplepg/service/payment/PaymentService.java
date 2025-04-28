package me.jaesung.simplepg.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.exception.PaymentException;
import me.jaesung.simplepg.domain.dto.payment.*;
import me.jaesung.simplepg.domain.vo.payment.MethodCode;
import me.jaesung.simplepg.domain.vo.payment.PaymentLogAction;
import me.jaesung.simplepg.domain.vo.payment.PaymentStatus;
import me.jaesung.simplepg.mapper.PaymentLogMapper;
import me.jaesung.simplepg.mapper.PaymentMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentLogMapper paymentLogMapper;

    @Transactional
    public PaymentResponse createPaymentAndLog(PaymentRequest request) {

        validateOrderNo(request);

        BigDecimal amountBD = validateAmount(request);

        validateMethodCode(request);

        try {
            PaymentDTO paymentDTO = createPayment(request, amountBD);
            PaymentLogDTO paymentLogDTO = createPaymentLog(paymentDTO.getPaymentId());

            paymentMapper.insertPayment(paymentDTO);
            paymentLogMapper.insertPaymentLog(paymentLogDTO);

            log.debug("결제 요청 생성 성공: paymentId = {}", paymentDTO.getPaymentId());

            return PaymentResponse.of(paymentDTO);

        } catch (Exception e) {
            log.error("결제 요청 생성 중 오류 발생: {}", e.getMessage());
            throw new PaymentException.ProcessingException("결제 생성 중 오류가 발생했습니다");
        }

    }

    @Transactional
    public PaymentInfoDTO getPaymentStatus(String paymentKey) {
        PaymentDTO paymentDTO = paymentMapper.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new PaymentException.InvalidPaymentRequestException("결제 정보를 찾을 수 없습니다:" + paymentKey));

        log.info("결제 상태 체크: {}, 현재 상태: {}", paymentKey, paymentDTO.getStatus().toString());
        return PaymentInfoDTO.of(paymentDTO);
    }

    private PaymentDTO createPayment(PaymentRequest request, BigDecimal amountBD) {
        String paymentId = String.valueOf(UUID.randomUUID());
        String orderNo = request.getOrderNo();
        String paymentKey = createPaymentKey(orderNo);
        MethodCode methodCodeEnum = MethodCode.valueOf(request.getMethodCode());


        return PaymentDTO.builder()
                .paymentId(paymentId)
                .paymentKey(paymentKey)
                .orderNo(orderNo)
                .amount(amountBD)
                .status(PaymentStatus.READY)
                .methodCode(methodCodeEnum)
                .productName(request.getProductName())
                .customerName(request.getCustomerName())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private PaymentLogDTO createPaymentLog(String paymentId) {
        return PaymentLogDTO.builder()
                .paymentId(paymentId)
                .status(PaymentStatus.READY)
                .action(PaymentLogAction.CREATE)
                .details("paymentID: " + paymentId + " 생성")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String createPaymentKey(String orderNo) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String randomSalt = UUID.randomUUID().toString();
        String data = orderNo + ":" + timeStamp + ":" + randomSalt;

        return DigestUtils.sha256Hex(data);
    }

    /**
     * 1. 주문번호가 포함되어 있는 지 확인
     * 2. 이미 주문번호가 있는 지 확인
     */
    private void validateOrderNo(PaymentRequest request) {
        String orderNo = request.getOrderNo();

        if (orderNo == null || orderNo.trim().isEmpty()) {
            throw new PaymentException.InvalidPaymentRequestException("주문번호는 필수입니다");
        }

        if (paymentMapper.existsByOrderNo(orderNo)) {
            throw new PaymentException.DuplicateKeyException("주문번호가 이미 존재합니다");
        }
    }

    /**
     * 금액 검증 (0보다 작은 경우 예외)
     * 검증한 값을 그대로 사용해서 불필요한 메모리 낭비 줄임
     */
    private BigDecimal validateAmount(PaymentRequest request) {
        try {
            BigDecimal amount = new BigDecimal(request.getAmount());
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new PaymentException.InvalidPaymentRequestException("금액이 0보다 작습니다");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new PaymentException.InvalidPaymentRequestException("유효한 금액 형식이 아닙니다");
        }
    }

    private void validateMethodCode(PaymentRequest request) {
        try {
            MethodCode.valueOf(request.getMethodCode());
        } catch (IllegalArgumentException e) {
            throw new PaymentException.InvalidPaymentRequestException("유효한 결제 방법이 아닙니다: " + request.getMethodCode());
        }
    }

}
