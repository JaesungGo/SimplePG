package me.jaesung.simplepg.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Api 예외 처리 핸들러
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, WebRequest request) {

        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = errorCode.getHttpStatus();
        String path = getRequestURI(request);

        log.error("Api exception occurred: status={}, URI={}",
                status, path, ex);

        ErrorResponse errorResponse = ErrorResponse.of(status.value(), ex.getMessage(), path);
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Payment 관련 예외 처리 핸들러
     */
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException ex, WebRequest request){
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = errorCode.getHttpStatus();
        String path = getRequestURI(request);

        log.error("Payment exception occurred: status={}, URI={}",
                status, path, ex);

        ErrorResponse errorResponse = ErrorResponse.of(status.value(), ex.getMessage(), path);
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * 파라미터 검증 예외 처리 핸들러
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMAValidException(MethodArgumentNotValidException ex, WebRequest request) {

        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        HttpStatus status = errorCode.getHttpStatus();
        String path = getRequestURI(request);

        log.error("MethodArgumentNotValid exception occurred: status={}, URI={}",
                status, path, ex);

        ErrorResponse errorResponse = ErrorResponse.of(status.value(), "파라미터의 형식이 틀립니다.", path);
        return ResponseEntity.status(status).body(errorResponse);
    }


    /**
     * 전역 예외 처리 핸들러
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        HttpStatus status = errorCode.getHttpStatus();
        String path = getRequestURI(request);

        log.error("Unhandled exception occurred: code={}, status={}, URI={}",
                errorCode.name(), status, path, ex);

        ErrorResponse errorResponse = ErrorResponse.of(status.value(), "서버 측 예외 발생", path);

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Request에서 URI 추출
     */
    private String getRequestURI(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return "";
    }

}
