package me.jaesung.simplepg.mapper;

import me.jaesung.simplepg.domain.dto.payment.PaymentDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

public interface PaymentMapper {
    void insertPayment(PaymentDTO paymentDTO);

    boolean existsByOrderNo(String orderNo);

    boolean existsByClientIdAndOrderNo(@Param("clientId") String clientId, @Param("orderNo") String orderNo);

    Boolean lockByClientIdAndOrderNo(@Param("clientId") String clientId, @Param("orderNo") String orderNo);

    Optional<PaymentDTO> findByPaymentKeyWithLock(String paymentKey);

    void updatePayment(PaymentDTO paymentDTO);

}
