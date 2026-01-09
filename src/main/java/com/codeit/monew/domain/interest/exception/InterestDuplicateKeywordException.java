package com.codeit.monew.domain.interest.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class InterestDuplicateKeywordException extends InterestException {

    public InterestDuplicateKeywordException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public InterestDuplicateKeywordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
