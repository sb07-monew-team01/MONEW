package com.codeit.monew.domain.interestuser.exception;

import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.global.enums.ErrorCode;
import com.codeit.monew.global.exception.MonewException;

import java.util.Map;

public class InterestUserException extends MonewException {
    public InterestUserException(ErrorCode errorCode, Map<String, Object> details) {
        super(InterestUser.class, errorCode, details);
    }

    public InterestUserException(ErrorCode errorCode) {
        super(InterestUser.class, errorCode);
    }
}
