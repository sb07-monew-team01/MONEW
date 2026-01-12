package com.codeit.monew.domain.user;

import com.codeit.monew.domain.user.exception.UserException;
import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException(User user) {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, Map.of("email", user.getEmail()));
    }
}
