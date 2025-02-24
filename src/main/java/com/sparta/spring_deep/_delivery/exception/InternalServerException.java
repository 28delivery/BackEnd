package com.sparta.spring_deep._delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // HTTP 500
public class InternalServerException extends RuntimeException {

    public InternalServerException() {
        super("서버 내부 오류가 발생했습니다.");
    }

    public InternalServerException(String message) {
        super(message);
    }
}
