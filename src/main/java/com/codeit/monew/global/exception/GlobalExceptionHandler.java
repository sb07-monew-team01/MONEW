package com.codeit.monew.global.exception;

import com.codeit.monew.domain.interest.exception.domain.InterestDomainException;
import com.codeit.monew.global.dto.ErrorResponse;
import com.codeit.monew.global.enums.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.codeit.monew.global.enums.ErrorCode.INVALID_ARGUMENT;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InterestDomainException.class)
    public ResponseEntity<ErrorResponse<?>> handleInterestDomainException(InterestDomainException e) {
        ErrorCode errorcode = switch (e.getInterestErrorCode()){
            case KEYWORD_DUPLICATE -> ErrorCode.INTEREST_KEYWORD_DUPLICATE;
            case EMPTY_KEYWORD -> ErrorCode.INTEREST_EMPTY_KEYWORD;
            case NULL_KEYWORD -> ErrorCode.INTEREST_NULL_KEYWORD;
            case TOO_MANY_KEYWORD -> ErrorCode.TOO_MANY_KEYWORD;
        };
        return ResponseEntity.status(errorcode.httpStatus).body(
            new ErrorResponse<>(
                e,
                e.getInterestErrorCode().getDescription(),
                null,
                errorcode
            )
        );
    }

    @ExceptionHandler(MonewException.class)
    public ResponseEntity<ErrorResponse<?>> handleMonewException(MonewException e) {
        return ResponseEntity.status(e.getErrorCode().httpStatus)
                .body(new ErrorResponse<>(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        ErrorCode errorCode = INVALID_ARGUMENT;
        Map<String, Object> details = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            details.put("message", error.getDefaultMessage() == null? errorCode.description : error.getDefaultMessage());
            details.put("field", error.getObjectName());
        });
        return ResponseEntity.status(errorCode.httpStatus)
                .body(new ErrorResponse<>(e, "올바른 형식이 아닙니다.", details, errorCode));
    }
}
