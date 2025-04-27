package me.jaesung.simplepg.domain.vo.payment;

public enum PaymentLogAction {
    CREATE,       // 결제 요청 생성
    UPDATE,       // 결제 정보 수정 (예: 금액 변경, 메타데이터 업데이트)
    APPROVE,      // PG사 결제 승인 요청/응답
    COMPLETE,     // 결제 완료 처리
    CANCEL,       // 결제 취소 요청/처리
    FAIL,         // 결제 실패 처리
    TIMEOUT       // 타임아웃으로 인한 자동 취소
}
