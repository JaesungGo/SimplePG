package me.jaesung.simplepg.mapper;

import me.jaesung.simplepg.domain.dto.payment.PaymentLogDTO;

public interface PaymentLogMapper {

    void insertPaymentLog(PaymentLogDTO paymentLogDTO);
}
