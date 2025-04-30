package me.jaesung.simplepg.mapper;

import me.jaesung.simplepg.domain.dto.payment.PaymentLogDTO;

import java.util.Optional;

public interface PaymentLogMapper {

    void insertPaymentLog(PaymentLogDTO paymentLogDTO);
    Optional<PaymentLogDTO> findByPaymentId(String paymentId);
}
