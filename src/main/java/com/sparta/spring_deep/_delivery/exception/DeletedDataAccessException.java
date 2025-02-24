package com.sparta.spring_deep._delivery.exception;

import org.springframework.dao.DataAccessException;

/**
 * 삭제된 데이터에 접근할 때 발생하는 예외입니다. 보안상 상세 정보를 노출하지 않고, "삭제된 리소스" 혹은 "잘못된 요청"으로 처리할 수 있습니다.
 */
public class DeletedDataAccessException extends DataAccessException {

    public DeletedDataAccessException() {
        super("삭제된 리소스에 접근했습니다.");
    }

    public DeletedDataAccessException(String msg) {
        super(msg);
    }

    public DeletedDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}