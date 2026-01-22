package com.codeit.monew.global.exception;

import com.codeit.monew.domain.interest.exception.domain.InterestDomainException;
import com.codeit.monew.global.dto.ErrorResponse;
import com.codeit.monew.global.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
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


@Slf4j
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

        log.warn("[BUSINESS] code={} status={} message={}",
                errorcode.name(),
                errorcode.httpStatus.value(),
                safeMsg(e.getMessage())
        );

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

        log.warn("[BUSINESS] code={} status={} message={}",
                e.getErrorCode().name(),
                e.getErrorCode().httpStatus.value(),
                safeMsg(e.getMessage())
        );

        return ResponseEntity.status(e.getErrorCode().httpStatus)
                .body(new ErrorResponse<>(e));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse<?>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        ErrorCode errorCode = INVALID_ARGUMENT;

        log.warn("[VALIDATION] code={} status={} field={} value={} requiredType={}",
                errorCode.name(),
                errorCode.httpStatus.value(),
                e.getName(),
                e.getValue(),
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : null
        );

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
        var first = e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        log.warn("[VALIDATION] code={} status={} field={} rejectedValue={} message={}",
                errorCode.name(),
                errorCode.httpStatus.value(),
                first != null ? first.getField() : null,
                first != null ? first.getRejectedValue() : null,
                first != null ? safeMsg(first.getDefaultMessage()) : safeMsg(e.getMessage())
        );

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

        log.error("[UNHANDLED] code={} status={} message={}",
                errorCode.name(),
                errorCode.httpStatus.value(),
                safeMsg(e.getMessage()),
                e  // ← 여기서만 스택트레이스
        );
        Map<String, Object> details = new HashMap<>();
        details.put("message", e.getMessage());
        return ResponseEntity.status(errorCode.httpStatus)
                .body(new ErrorResponse<>(e, "서버 내부에 오류가 발생했습니다.", details, errorCode));
    }

    private String safeMsg(String msg) {
        if (msg == null) return null;
        // 너무 긴 메시지/개행 방지 (로그 한 줄 정책)
        msg = msg.replace("\n", " ").replace("\r", " ");
        return msg.length() > 300 ? msg.substring(0, 300) + "..." : msg;
    }

}
