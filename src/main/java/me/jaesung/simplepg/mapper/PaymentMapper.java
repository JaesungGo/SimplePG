package me.jaesung.simplepg.mapper;

import me.jaesung.simplepg.domain.dto.payment.PaymentDTO;
import me.jaesung.simplepg.domain.vo.payment.Payment;

public interface PaymentMapper {
    void insertPayment(PaymentDTO paymentDTO);

    boolean existsByOrderNo(String orderNo);
}
