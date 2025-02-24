package com.sparta.spring_deep._delivery.exception;

import org.springframework.dao.DataAccessException;

/**
 * 존재하지 않는 ID에 접근할 때 발생하는 예외입니다. 주로 HTTP 404 (Not Found) 응답과 매핑하여 처리합니다.
 */
public class ResourceNotFoundException extends DataAccessException {

    public ResourceNotFoundException() {
        super("요청한 리소스를 찾을 수 없습니다.");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}