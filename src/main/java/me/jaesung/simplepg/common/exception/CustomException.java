package me.jaesung.simplepg.common.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getHttpStatus();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }


    /**
     * 잘못된 요청 파라미터 (400 Bad Request)
     */
    public static class InvalidRequestException extends CustomException {
        public InvalidRequestException(String message) {
            super(ErrorCode.INVALID_REQUEST, message);
        }

    }

    /**
     * 인증 실패 (401 Unauthorized)
     */
    public static class UnauthorizedException extends CustomException {
        public UnauthorizedException(String message) {
            super(ErrorCode.UNAUTHORIZED, message);
        }

    }

    /**
     * 리소스를 찾을 수 없음 (404 Not Found)
     */
    public static class NotFoundException extends CustomException {
        public NotFoundException(String message) {
            super(ErrorCode.NOT_FOUND, message);
        }

    }

    /**
     * API 인증 권한 없음 (401 Unauthorized)
     */
    public static class ApiAuthenticationException extends CustomException {
        public ApiAuthenticationException(String message) {
            super(ErrorCode.UNAUTHORIZED, message);
        }
    }

    /**
     * Api 헤더 없음 (400 Bad Request)
     */
    public static class MissingApiHeaderException extends CustomException {
        public MissingApiHeaderException(String message) {
            super(ErrorCode.INVALID_REQUEST, message);
        }
    }

    /**
     * 요청 기간 만료 (408 Request Timeout)
     */
    public static class ExpiredRequestException extends CustomException {
        public ExpiredRequestException(String message) {
            super(ErrorCode.REQUEST_TIMEOUT, message);
        }
    }


}
