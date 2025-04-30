package me.jaesung.simplepg.domain.dto.payment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class PaymentRequest {

    @NotBlank(message = "가맹점 ID는 필수입니다")
    private String clientId;

    @NotBlank(message = "주문 번호는 필수입니다")
    private String orderNo;

    @NotBlank(message = "금액은 필수입니다")
    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "금액은 숫자이며 소수점 2자리까지를 허용합니다")
    private String amount;

    @NotBlank(message = "결제 수단은 필수입니다")
    @Pattern(regexp = "CARD|BANK|MOBILE", message = "유효하지 않은 결제수단입니다")
    private String methodCode;

    private String productName;
    private String customerName;

}
