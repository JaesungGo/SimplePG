package me.jaesung.simplepg.common.exception;

import org.springframework.http.HttpStatus;

public class PaymentException extends RuntimeException{

    private final ErrorCode errorCode;

    public PaymentException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public PaymentException(ErrorCode errorCode, String customMessage){
        super(customMessage);
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getHttpStatus();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }


    public static class InvalidPaymentRequestException extends PaymentException{
        public InvalidPaymentRequestException(String message) {super(ErrorCode.INVALID_REQUEST, message);}
    }

    public static class DuplicateKeyException extends PaymentException{
        public DuplicateKeyException(String message) {super(ErrorCode.KEY_CONFLICT, message);}
    }

    public static class ProcessingException extends PaymentException{
        public ProcessingException(String message) {super(ErrorCode.INTERNAL_SERVER_ERROR, message);}
    }

    public static class ExternalPaymentException extends PaymentException{
        public ExternalPaymentException(String message) {super(ErrorCode.BAD_GATEWAY, message);}
    }

    public static class WebhookProcessingException extends PaymentException{
        public WebhookProcessingException(String message) {super(ErrorCode.INTERNAL_SERVER_ERROR, message);}
    }
    
    public static class WebhookSendingException extends PaymentException{
        public WebhookSendingException(String message) {super(ErrorCode.BAD_GATEWAY, message);}
    }
}
