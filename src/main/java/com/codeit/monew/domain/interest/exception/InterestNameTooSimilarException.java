package com.codeit.monew.domain.interest.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class InterestNameTooSimilarException extends InterestException {

    public InterestNameTooSimilarException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public InterestNameTooSimilarException(ErrorCode errorCode) {
        super(errorCode);
    }
}
