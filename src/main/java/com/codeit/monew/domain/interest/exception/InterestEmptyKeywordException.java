package com.codeit.monew.domain.interest.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class InterestEmptyKeywordException extends InterestException {

    public InterestEmptyKeywordException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public InterestEmptyKeywordException(ErrorCode errorCode) {
        super(errorCode);
    }
}