package com.codeit.monew.global.exception;

import com.codeit.monew.domain.interest.exception.domain.InterestDomainException;
import com.codeit.monew.global.dto.ErrorResponse;
import com.codeit.monew.global.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

import static com.codeit.monew.global.enums.ErrorCode.INTERNAL_SERVER_ERROR;
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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse<?>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        ErrorCode errorCode = INVALID_ARGUMENT;
        Map<String, Object> details = new HashMap<>();
        details.put("field", e.getName());
        details.put("message", e.getMessage());
        details.put("value", e.getValue());
        details.put("requiredType", e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : null);
        return ResponseEntity.status(HttpStatusCode.valueOf(errorCode.httpStatus.value()))
                .body(new ErrorResponse<>(e, "올바른 형식이 아닙니다.", details, errorCode));
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<?>> handelException(Exception e){
        ErrorCode errorCode = INTERNAL_SERVER_ERROR;
        Map<String, Object> details = new HashMap<>();
        details.put("message", e.getMessage());
        return ResponseEntity.status(errorCode.httpStatus)
                .body(new ErrorResponse<>(e, "서버 내부에 오류가 발생했습니다.", details, errorCode));
    }
}
