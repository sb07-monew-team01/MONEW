package com.codeit.monew.global.dto;

import com.codeit.monew.global.enums.ErrorCode;
import com.codeit.monew.global.exception.MonewException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 웹 오류 응답을 표준화하기 위한 클래스
 * @param <T> 에러 타입
 */
@Getter
public class ErrorResponse<T extends Throwable> {
    private final LocalDateTime timestamp;
    private final ErrorCode code;
    private final String message;
    private final Map<String, Object> details;
    private final String exceptionType;
    private final int status;

    public ErrorResponse(T exceptionType, String message, Map<String, Object> details, ErrorCode code) {
        this.timestamp = LocalDateTime.now();
        this.code = code;
        this.message = message;
        this.details = Map.copyOf(details);
        this.exceptionType = exceptionType.getClass().getSimpleName();
        this.status = code.httpStatus.value();
    }

    public ErrorResponse(MonewException ex){
        this.timestamp = ex.getTimestamp();
        this.code = ex.getErrorCode();
        this.message = ex.getErrorCode().description;
        this.details = Map.copyOf(ex.getDetails());
        this.exceptionType = ex.getClass().getSimpleName();
        this.status = ex.getErrorCode().httpStatus.value();
    }
}
