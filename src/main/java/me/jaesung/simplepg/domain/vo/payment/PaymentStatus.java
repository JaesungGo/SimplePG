package me.jaesung.simplepg.domain.vo.payment;

public enum PaymentStatus {
    READY("아직 결제가 완료되지 않았습니다."),
    APPROVED("결제가 승인되었습니다."),
    COMPLETED("결제가 완료되었습니다."),
    CANCELED("결제가 취소되었습니다."),
    FAILED("결제가 실패했습니다.");

    private final String message;

    PaymentStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}