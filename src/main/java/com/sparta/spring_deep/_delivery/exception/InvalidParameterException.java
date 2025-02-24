package com.sparta.spring_deep._delivery.exception;

/**
 * 메서드나 API 호출 시 잘못된 파라미터가 전달되었을 때 발생하는 예외입니다. 주로 HTTP 400 (Bad Request) 응답과 매핑하여 처리합니다.
 */
public class InvalidParameterException extends RuntimeException {

    public InvalidParameterException() {
        super("잘못된 파라미터가 전달되었습니다.");
    }

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidParameterException(Throwable cause) {
        super(cause);
    }
}