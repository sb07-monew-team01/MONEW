package com.codeit.monew.domain.interest;

import com.codeit.monew.global.enums.ErrorCode;
import com.codeit.monew.global.exception.MonewException;

import java.util.Map;

public class InterestException extends MonewException {

    public InterestException(ErrorCode errorCode, Map<String, Object> details) {
        super(Interest.class, errorCode, details);
    }

    public InterestException(ErrorCode errorCode) {
        super(Interest.class, errorCode);
    }
}
