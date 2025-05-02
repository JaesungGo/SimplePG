package me.jaesung.simplepg.mapper;

import me.jaesung.simplepg.domain.dto.payment.PaymentDTO;

import java.util.Optional;

public interface PaymentMapper {
    void insertPayment(PaymentDTO paymentDTO);

    boolean existsByOrderNo(String orderNo);

    boolean lockByOrderNo(String orderNo);

    Optional<PaymentDTO> findByPaymentKeyWithLock(String paymentKey);

    void updatePayment(PaymentDTO paymentDTO);

}
