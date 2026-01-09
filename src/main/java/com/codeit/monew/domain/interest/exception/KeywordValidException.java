package com.codeit.monew.domain.interest.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class KeywordValidException extends InterestException {
    public KeywordValidException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public KeywordValidException(ErrorCode errorCode) {
        super(errorCode);
    }
}