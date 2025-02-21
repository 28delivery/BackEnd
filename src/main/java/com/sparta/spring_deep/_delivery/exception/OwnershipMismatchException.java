package com.sparta.spring_deep._delivery.exception;

/**
 * 리소스의 소유자와 다른 ID로 접근하려 할 때 발생하는 예외입니다. 주로 HTTP 403 (Forbidden) 응답과 매핑하여 처리합니다.
 */
public class OwnershipMismatchException extends RuntimeException {

    public OwnershipMismatchException() {
        super("소유자와 일치하지 않는 사용자입니다.");
    }

    public OwnershipMismatchException(String message) {
        super(message);
    }

    public OwnershipMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public OwnershipMismatchException(Throwable cause) {
        super(cause);
    }
}