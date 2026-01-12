package com.codeit.monew.domain.user.exception;

import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.global.enums.ErrorCode;
import com.codeit.monew.global.exception.MonewException;

import java.util.Map;

public class UserException extends MonewException {
    public UserException(ErrorCode errorCode, Map<String, Object> details) {
        super(User.class, errorCode, details);
    }
}
