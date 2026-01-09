package com.codeit.monew.domain.user.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class UserLoginFailedException extends UserException {
    public UserLoginFailedException(String email) {
        super(ErrorCode.USER_LOGIN_FAILED, Map.of("email", email));
    }
}
