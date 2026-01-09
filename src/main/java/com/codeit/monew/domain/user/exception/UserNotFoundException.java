package com.codeit.monew.domain.user.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class UserNotFoundException extends UserException {
        public UserNotFoundException(String email) {
            super(ErrorCode.ALREADY_EXISTS, Map.of("email", email));
        }
}
