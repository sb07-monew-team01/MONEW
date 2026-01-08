package com.codeit.monew.global.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class MonewException extends RuntimeException {
    private final Instant timestamp = Instant.now();
    private final ErrorCode errorCode;
    protected final Map<String, Object> details;

    public MonewException(Class<?> classType, ErrorCode errorCode, Map<String, Object> details) {
        this.errorCode = errorCode;
        this.details = new HashMap<>();
        this.details.put("class", classType.getSimpleName());
        this.details.putAll(details);
    }

    public MonewException(Class<?> classType, ErrorCode errorCode) {
        this(classType, errorCode, new HashMap<>());
    }
}
