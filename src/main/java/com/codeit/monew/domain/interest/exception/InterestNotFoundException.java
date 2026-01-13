package com.codeit.monew.domain.interest.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class InterestNotFoundException extends InterestException{
    public InterestNotFoundException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public InterestNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
