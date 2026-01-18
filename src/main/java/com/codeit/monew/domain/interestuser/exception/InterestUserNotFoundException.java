package com.codeit.monew.domain.interestuser.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class InterestUserNotFoundException extends InterestUserException{
    public InterestUserNotFoundException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public InterestUserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
