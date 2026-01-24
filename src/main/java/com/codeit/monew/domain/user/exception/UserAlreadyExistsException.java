package com.codeit.monew.domain.user.exception;

import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException(User user) {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, Map.of("email", user.getEmail()));
    }

    public UserAlreadyExistsException(String email) {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, Map.of("email", email));
    }
}
