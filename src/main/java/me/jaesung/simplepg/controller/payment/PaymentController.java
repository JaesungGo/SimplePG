package me.jaesung.simplepg.controller.payment;

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
        PaymentResponse paymentResponse = paymentService.createPaymentAndLog(paymentRequest);
        return ResponseEntity.ok(paymentResponse);
    }

    @GetMapping("/{paymentKey}")
    public ResponseEntity<PaymentInfoDTO> getPaymentStatus(@PathVariable String paymentKey) {
        PaymentInfoDTO paymentStatus = paymentService.getPaymentStatus(paymentKey);
        return ResponseEntity.ok(paymentStatus);
    }

    @PostMapping("/{paymentKey}/cancel")
    public ResponseEntity<PaymentInfoDTO> cancelPayment(
            @PathVariable String paymentKey,
            @RequestParam(required = false) String cancelReason) {
        PaymentInfoDTO canceledPayment = paymentService.cancelPayment(paymentKey, cancelReason);
        return ResponseEntity.ok(canceledPayment);
    }

    @PostMapping("/{paymentKey}/complete")
    public ResponseEntity<PaymentInfoDTO> completePayment(@PathVariable String paymentKey) {
        PaymentInfoDTO completedPayment = paymentService.completePayment(paymentKey);
        return ResponseEntity.ok(completedPayment);
    }

}
