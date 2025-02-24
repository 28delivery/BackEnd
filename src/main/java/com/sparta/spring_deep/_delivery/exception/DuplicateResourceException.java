package com.sparta.spring_deep._delivery.exception;

import org.springframework.dao.DataAccessException;

/**
 * 이미 존재하는 데이터를 생성하려 할 때 발생하는 예외입니다. 주로 HTTP 409 (Conflict) 응답과 매핑하여 처리합니다.
 */
public class DuplicateResourceException extends DataAccessException {

    public DuplicateResourceException() {
        super("이미 존재하는 리소스입니다.");
    }

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

}
