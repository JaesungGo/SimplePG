package me.jaesung.simplepg.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_REQUEST("Invalid request parameters", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("Authentication failed", HttpStatus.UNAUTHORIZED),
    NOT_FOUND("Resource not found", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("An unexpected error", HttpStatus.INTERNAL_SERVER_ERROR),
    REQUEST_TIMEOUT("Request Timeout", HttpStatus.REQUEST_TIMEOUT),
    KEY_CONFLICT("Key Conflict", HttpStatus.CONFLICT),
    BAD_GATEWAY("Bad GateWay", HttpStatus.BAD_GATEWAY);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
