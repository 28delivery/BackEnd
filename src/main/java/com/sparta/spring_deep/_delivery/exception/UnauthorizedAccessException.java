package com.sparta.spring_deep._delivery.exception;

/**
 * 사용자가 필요한 권한을 보유하지 않은 경우 발생하는 예외입니다. HTTP 401 (Unauthorized) 또는 403 (Forbidden)으로 매핑할 수 있습니다.
 */
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException() {
        super("권한이 없습니다.");
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedAccessException(Throwable cause) {
        super(cause);
    }
}