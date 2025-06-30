package me.jaesung.simplepg.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.exception.ErrorCode;
import me.jaesung.simplepg.common.exception.PaymentException;
import me.jaesung.simplepg.domain.dto.payment.*;
import me.jaesung.simplepg.domain.vo.payment.MethodCode;
import me.jaesung.simplepg.domain.vo.payment.PaymentLogAction;
import me.jaesung.simplepg.domain.vo.payment.PaymentStatus;
import me.jaesung.simplepg.mapper.PaymentLogMapper;
import me.jaesung.simplepg.mapper.PaymentMapper;
import me.jaesung.simplepg.service.webclient.WebClientService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentLogMapper paymentLogMapper;
    private final WebClientService webClientService;
    private final PaymentKeyService paymentKeyService;

    @Transactional
    public PaymentResponse createPaymentAndLog(PaymentRequest request) {

        validateClientId(request);

        paymentMapper.lockByOrderNo(request.getOrderNo());

        validateOrderNo(request);

        BigDecimal amountBD = validateAmount(request);

        validateMethodCode(request);

        try {
            PaymentDTO paymentDTO = createPayment(request, amountBD);
            PaymentLogDTO paymentLogDTO = createPaymentLog(paymentDTO.getPaymentId());

            paymentMapper.insertPayment(paymentDTO);
            paymentLogMapper.insertPaymentLog(paymentLogDTO);

            log.debug("결제 요청 생성 성공: paymentId = {}", paymentDTO.getPaymentId());

            webClientService.sendRequest(paymentDTO);

            return PaymentResponse.of(paymentDTO);

        } catch (Exception e) {
            log.error("결제 요청 생성 중 오류 발생: {}", e.getMessage());
            throw new PaymentException.ProcessingException("결제 생성 중 오류가 발생했습니다");
        }

    }

    @Transactional
    public PaymentInfoDTO getPaymentStatus(String paymentKey) {
        PaymentDTO paymentDTO = paymentMapper.findByPaymentKeyWithLock(paymentKey)
                .orElseThrow(() -> new PaymentException.InvalidPaymentRequestException("결제 정보를 찾을 수 없습니다:" + paymentKey));

        log.info("결제 상태 체크: {}, 현재 상태: {}", paymentKey, paymentDTO.getStatus().toString());
        return PaymentInfoDTO.of(paymentDTO);
    }

    /**
     * 결제 취소 처리 메소드
     * 결제 상태가 APPROVED 또는 COMPLETED인 경우에만 취소 가능
     */
    @Transactional
    public PaymentInfoDTO cancelPayment(String paymentKey, String cancelReason) {

        PaymentDTO paymentDTO = paymentMapper.findByPaymentKeyWithLock(paymentKey)
                .orElseThrow(() -> new PaymentException.InvalidPaymentRequestException("결제 정보를 찾을 수 없습니다:" + paymentKey));

        validatePaymentStatusTransition(paymentDTO.getStatus(), PaymentStatus.CANCELED);

        try {
            webClientService.sendCancelRequest(paymentDTO, cancelReason);

            paymentDTO.setStatus(PaymentStatus.CANCELED);

            PaymentLogDTO paymentLogDTO = PaymentLogDTO.builder()
                    .paymentId(paymentDTO.getPaymentId())
                    .action(PaymentLogAction.CANCEL)
                    .status(PaymentStatus.CANCELED)
                    .details("결제 취소 처리되었습니다. " + (cancelReason != null ? "사유: " + cancelReason : ""))
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentMapper.updatePayment(paymentDTO);
            paymentLogMapper.insertPaymentLog(paymentLogDTO);

            log.info("결제 취소 성공: paymentKey={}, 취소사유={}", paymentKey, cancelReason);

            return PaymentInfoDTO.of(paymentDTO);
        } catch (Exception e) {
            log.error("결제 취소 중 오류 발생: {}", e.getMessage(), e);
            throw new PaymentException.ProcessingException("결제 취소 처리 중 오류가 발생했습니다");
        }
    }

    /**
     * 결제 완료 처리 메소드
     * APPROVED 상태의 결제만 COMPLETED로 변경 가능
     */
    @Transactional
    public PaymentInfoDTO completePayment(String paymentKey) {

        PaymentDTO paymentDTO = paymentMapper.findByPaymentKeyWithLock(paymentKey)
                .orElseThrow(() -> new PaymentException.InvalidPaymentRequestException("결제 정보를 찾을 수 없습니다:" + paymentKey));

        validatePaymentStatusTransition(paymentDTO.getStatus(), PaymentStatus.COMPLETED);

        try {
            paymentDTO.setStatus(PaymentStatus.COMPLETED);

            PaymentLogDTO paymentLogDTO = PaymentLogDTO.builder()
                    .paymentId(paymentDTO.getPaymentId())
                    .action(PaymentLogAction.COMPLETE)
                    .status(PaymentStatus.COMPLETED)
                    .details("결제가 완료 처리되었습니다.")
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentMapper.updatePayment(paymentDTO);
            paymentLogMapper.insertPaymentLog(paymentLogDTO);

            log.info("결제 완료 처리 성공: paymentKey={}", paymentKey);

            return PaymentInfoDTO.of(paymentDTO);
        } catch (Exception e) {
            log.error("결제 완료 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new PaymentException.ProcessingException("결제 완료 처리 중 오류가 발생했습니다");
        }
    }

    /**
     * 결제 상태 전이 유효성 검증 메소드
     * 허용된 상태 전이만 가능하도록 제한
     */
    private void validatePaymentStatusTransition(PaymentStatus currentStatus, PaymentStatus targetStatus) {
        boolean isValid = false;

        switch (currentStatus) {
            case READY:
                isValid = targetStatus == PaymentStatus.APPROVED ||
                        targetStatus == PaymentStatus.FAILED ||
                        targetStatus == PaymentStatus.CANCELED;
                break;
            case APPROVED:
                isValid = targetStatus == PaymentStatus.COMPLETED ||
                        targetStatus == PaymentStatus.CANCELED;
                break;
            case COMPLETED:
                isValid = targetStatus == PaymentStatus.CANCELED;
                break;
            case FAILED:
            case CANCELED:
                isValid = false;
                break;
        }

        if (!isValid) {
            throw new PaymentException.InvalidPaymentRequestException(
                    String.format("유효하지 않은 상태 전이입니다: %s → %s", currentStatus, targetStatus));
        }
    }

    /**
     * 결제 생성 메서드
     *
     * @param request
     * @param amountBD
     * @return
     */
    private PaymentDTO createPayment(PaymentRequest request, BigDecimal amountBD) {
        String paymentId = String.valueOf(UUID.randomUUID());
        String orderNo = request.getOrderNo();
        String paymentKey = paymentKeyService.createPaymentKey(orderNo);
        MethodCode methodCodeEnum = MethodCode.valueOf(request.getMethodCode());


        return PaymentDTO.builder()
                .paymentId(paymentId)
                .clientId(request.getClientId())
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


    private void validateClientId(PaymentRequest request) {
        String clientId = request.getClientId();

        if (clientId == null || clientId.trim().isEmpty()) {
            throw new PaymentException.InvalidPaymentRequestException("클라이언트 ID는 필수입니다");
        }
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
     * 금액 검증 (0보다 작은 경우 예외, 소수점 불가)
     * 검증한 값을 그대로 사용해서 불필요한 메모리 낭비 줄임
     */
    private BigDecimal validateAmount(PaymentRequest request) {
        try {
            BigDecimal amount = new BigDecimal(request.getAmount());

            // 1. 0원 이하 결제 차단
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new PaymentException.InvalidPaymentRequestException("금액이 0보다 같거나 작습니다");
            }

            // 2. 소수점 처리 - 정수만 허용
            if (amount.scale() > 0) {
                throw new PaymentException.InvalidPaymentRequestException(
                        "결제 금액은 정수만 허용됩니다");
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
