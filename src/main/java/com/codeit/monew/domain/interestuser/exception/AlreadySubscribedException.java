package com.codeit.monew.domain.interestuser.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class AlreadySubscribedException extends InterestUserException{

    public AlreadySubscribedException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public AlreadySubscribedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
