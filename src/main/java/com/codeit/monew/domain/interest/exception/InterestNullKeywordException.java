package com.codeit.monew.domain.interest.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class InterestNullKeywordException extends InterestException {

    public InterestNullKeywordException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public InterestNullKeywordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
