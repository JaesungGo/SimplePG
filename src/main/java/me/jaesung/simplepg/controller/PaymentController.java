package me.jaesung.simplepg.controller;

import lombok.RequiredArgsConstructor;
import me.jaesung.simplepg.domain.dto.payment.PaymentInfoDTO;
import me.jaesung.simplepg.domain.dto.payment.PaymentRequest;
import me.jaesung.simplepg.domain.dto.payment.PaymentResponse;
import me.jaesung.simplepg.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/protected/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody @Valid PaymentRequest paymentRequest) {
        PaymentResponse paymentAndLog = paymentService.createPaymentAndLog(paymentRequest);
        return ResponseEntity.ok(paymentAndLog);
    }

    @GetMapping("/{paymentKey}")
    public ResponseEntity<PaymentInfoDTO> getPaymentStatus(@PathVariable String paymentKey){
        PaymentInfoDTO paymentStatus = paymentService.getPaymentStatus(paymentKey);
        return ResponseEntity.ok(paymentStatus);
    }

}
