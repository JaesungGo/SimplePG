package me.jaesung.simplepg.domain.vo.payment;

public enum PaymentStatus {
    READY,   //결제 요청 생성
    APPROVED,  // 결제 요청 승인
    COMPLETED,  // 결제 최종 완료
    FAILED, // 결제 실패
    CANCELLED // 결제 취소
}
