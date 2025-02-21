package com.sparta.spring_deep._delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)  // 또는 상황에 따라 FORBIDDEN (403)
public class OperationNotAllowedException extends RuntimeException {

    public OperationNotAllowedException() {
        super("현재 상태에서 수행할 수 없는 작업입니다.");
    }

    public OperationNotAllowedException(String message) {
        super(message);
    }
}
