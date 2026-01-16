package com.codeit.monew.global.exception;

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
